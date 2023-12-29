package com.soul.game.rpc.client.rpc;

import com.soul.game.netty.rpc.api.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 3:28 PM
 */
@Slf4j
@Component
public class RpcBeanPostProcessor implements InstantiationAwareBeanPostProcessor, EnvironmentAware {
    private static ConcurrentHashMap<String, Object> cacheProxyMap = new ConcurrentHashMap<>();

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment){ this.environment = environment; }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for(Field field: clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(RpcReference.class)){
                Object instance;
                String beanClassName = field.getType().getName();
                try{
                    if(cacheProxyMap.containsKey(beanClassName)){
                        instance = cacheProxyMap.get(beanClassName);
                    }else{
                        //根据不同服务名称传递不同的rpc调用地址
                        RpcReference annotation = field.getAnnotation(RpcReference.class);
                        instance = Proxy.newProxyInstance(
                                field.getType().getClassLoader(),
                                new Class[]{field.getType()},
                                new ProxyHandler(this.environment.getProperty(annotation.name() + ".rpcHost"),
                                        Integer.valueOf(this.environment.getProperty(annotation.name() + ".rpcPort")),
                                        bean, beanClassName)
                        );
                    }
                    log.info("create proxy bean: {}", beanClassName);
                    //反射注入
                    field.setAccessible(true);
                    field.set(bean, instance);
                    cacheProxyMap.put(field.getType().getName(), instance);
                } catch (IllegalAccessException e) {
                    log.error("create bean error beanClassName = {}", beanClassName);
                }
            }
        }
        return bean;
    }

}

package com.soul.game.netty.rpc.server.rpc;

import com.soul.game.netty.rpc.api.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 3:59 PM
 */
@Slf4j
@Component
public class RpcBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
    public static Map<String, Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if(clazz.isAnnotationPresent(RpcService.class)){
            beanMap.put(clazz.getInterfaces()[0].getName(), bean);
            log.info("register rpc service : {}", clazz.getInterfaces()[0].getName());
        }
        return bean;
    }
}

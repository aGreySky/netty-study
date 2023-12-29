package com.soul.game.rpc.client.rpc;

import com.soul.game.netty.rpc.api.protocol.constants.RpcRequest;
import com.soul.game.netty.rpc.api.protocol.core.NettyClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 2:57 PM
 */

@Slf4j
@AllArgsConstructor
public class ProxyHandler implements InvocationHandler, Serializable {
    private String rpcHost;
    private int rpcPort;
    private Object target;
    private String service;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //组装协议
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setReqId(UUID.randomUUID().toString());
        rpcRequest.setService(this.service);
        rpcRequest.setMethod(method.getName());
        rpcRequest.setArgs(args);
        rpcRequest.setParamType(method.getParameterTypes());
        log.info("invoke rpcRequest = {}",rpcRequest);
        //发起调用
        NettyClient nettyClient = new NettyClient();
        nettyClient.start(rpcHost, rpcPort, new MyRpcClientHandler());


        return nettyClient.sendRequest(rpcRequest);
    }


}

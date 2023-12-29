package com.soul.game.netty.rpc.server.rpc;

import com.alibaba.fastjson.JSON;
import com.soul.game.netty.rpc.api.protocol.constants.RpcRequest;
import com.soul.game.netty.rpc.api.protocol.constants.RpcResponse;
import com.soul.game.netty.rpc.api.protocol.handler.RpcHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 3:55 PM
 */
@Slf4j
public class MyRpcHandler extends RpcHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("request msg = {}", msg);
        RpcRequest request = JSON.parseObject(msg, RpcRequest.class);
        if(request == null || request.getReqId() == null) return;

        String service = request.getService();
        Object bean = RpcBeanPostProcessor.beanMap.get(service);
        //获取方法
        Method method = bean.getClass().getMethod(request.getMethod(), request.getParamType());
        Object result = method.invoke(bean, request.getArgs());

        //响应协议
        RpcResponse response = new RpcResponse();
        response.setReqId(request.getReqId());
        response.setContent(result);
        ctx.writeAndFlush(JSON.toJSONString(response));
    }
}

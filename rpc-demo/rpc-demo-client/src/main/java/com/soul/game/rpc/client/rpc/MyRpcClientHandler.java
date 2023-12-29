package com.soul.game.rpc.client.rpc;

import com.alibaba.fastjson.JSON;
import com.soul.game.netty.rpc.api.protocol.constants.RpcResponse;
import com.soul.game.netty.rpc.api.protocol.core.NettyConstants;
import com.soul.game.netty.rpc.api.protocol.handler.RpcHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 2:58 PM
 */
@Slf4j
public class MyRpcClientHandler extends RpcHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("rpc Response msg : {}", msg);
        RpcResponse response = JSON.parseObject(msg, RpcResponse.class);
        if(response == null || !NettyConstants.rpcFutureMap.containsKey(response.getReqId())) return;
        //给指定的reqId回调
        NettyConstants.rpcFutureMap.get(response.getReqId()).getPromise().setSuccess(response);
        NettyConstants.rpcFutureMap.remove(response.getReqId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("connect error: {}", ctx.channel().remoteAddress(), cause);
        NettyConstants.clientMap.remove(ctx.channel().remoteAddress().toString());
    }
}

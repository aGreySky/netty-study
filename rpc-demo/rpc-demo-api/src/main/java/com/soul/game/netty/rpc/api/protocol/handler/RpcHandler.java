package com.soul.game.netty.rpc.api.protocol.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 1:41 PM
 */
@ChannelHandler.Sharable
public abstract class RpcHandler extends SimpleChannelInboundHandler<String> {
}

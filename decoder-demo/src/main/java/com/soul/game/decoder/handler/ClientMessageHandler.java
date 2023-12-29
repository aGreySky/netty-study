package com.soul.game.decoder.handler;

import com.soul.game.decoder.protocol.MyProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {
    public ClientMessageHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyProtocol myProtocol = (MyProtocol) msg;
        log.info("接收到服务端数据：{}", myProtocol);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
        log.error("exceptionCaught：e", cause);
        ctx.close();//抛出异常，断开与客户端的连接
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        log.info("channelActive------TCP客户端新建连接------clientIp:{}", clientIp);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ctx.close(); //断开连接时，必须关闭，否则造成资源浪费
        log.info("channelInactive------TCP客户端断开连接----------clientIp:{}", clientIp);
    }

}

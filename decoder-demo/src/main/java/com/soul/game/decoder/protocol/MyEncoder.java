package com.soul.game.decoder.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyEncoder extends MessageToByteEncoder<MyProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyProtocol myProtocol, ByteBuf out) throws Exception {
        out.writeBytes(myProtocol.getHead().getBytes());
        out.writeShort(myProtocol.getContentLen());
        out.writeBytes(myProtocol.getContent());
    }
}

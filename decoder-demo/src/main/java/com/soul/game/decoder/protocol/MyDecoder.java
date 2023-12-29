package com.soul.game.decoder.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {
    /**
     * 协议标准
     * header_data SOUL 4个字节
     * 数据长度 2个字节
     */
    public final int BASE_LEN = 10;

    /**
     *
     * @param channelHandlerContext
     * @param byteBuf
     * @param out
     * @throws Exception
     */

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
//        byte[] data1 = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(data1);
        if(byteBuf.readableBytes() >= BASE_LEN){
            if(byteBuf.readableBytes() > 2048){
                byteBuf.skipBytes(byteBuf.readableBytes());
            }
        }

        int beginReader;

        while(true){
            if(byteBuf.readableBytes() < BASE_LEN){
                return;
            }
            beginReader = byteBuf.readerIndex();
            byteBuf.markReaderIndex();

            int head1 = byteBuf.readUnsignedShort();
            int head2 = byteBuf.readUnsignedShort();

            if(head1 == 21327 && head2 == 21836){
                break;
            }
            byteBuf.resetReaderIndex();
            byteBuf.readByte();
        }

        int len = byteBuf.readUnsignedShort();
        if(byteBuf.readableBytes() < len){
            byteBuf.readerIndex(beginReader);
            return;
        }

        byte[] data = new byte[len];
        byteBuf.readBytes(data);

        MyProtocol protocol = new MyProtocol(data.length, data);
        out.add(protocol);

    }

    public static void main(String[] args) {
        String s = "SOUL";
        byte[] bytes = s.getBytes();
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(bytes);
        System.out.println(buf.readUnsignedShort());;
        System.out.println(buf.readUnsignedShort());;
    }
}

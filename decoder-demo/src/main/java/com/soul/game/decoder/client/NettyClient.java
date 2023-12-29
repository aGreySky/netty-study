package com.soul.game.decoder.client;

import com.soul.game.decoder.handler.ClientMessageHandler;
import com.soul.game.decoder.protocol.MyDecoder;
import com.soul.game.decoder.protocol.MyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {
    public void connect(String host, int port){
        /**
         * 客户端的nio线程组
         */
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);

            bootstrap = bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);

            bootstrap = bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    channelPipeline.addLast(new MyDecoder());
                    channelPipeline.addLast(new MyEncoder());
                    channelPipeline.addLast(new ClientMessageHandler());
                }
            });
            ChannelFuture f = bootstrap.connect(host, port).sync();
            log.info("tcp客户端链接成功，地址是：{}:{}", host, port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("启动netty 客户端失败：", e);
        }finally {
            group.shutdownGracefully();
        }
    }
}

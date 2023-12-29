package com.soul.game.netty.rpc.api.protocol.core;
import com.alibaba.fastjson.JSON;
import com.soul.game.netty.rpc.api.protocol.constants.RpcRequest;
import com.soul.game.netty.rpc.api.protocol.constants.RpcResponse;
import com.soul.game.netty.rpc.api.protocol.handler.RpcHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 1:40 PM
 */
@Slf4j
@Data
public class NettyClient {
    private Channel channel;

    public void start(String host, int port, RpcHandler rpcHandler){
        String mapKey = "/" + host + ":" + port;
        if(NettyConstants.clientMap.containsKey(mapKey)){
            this.channel = NettyConstants.clientMap.get(mapKey);
            return;
        }
        NioEventLoopGroup b1 = new NioEventLoopGroup();
        Bootstrap bs = new Bootstrap()
                .group(b1)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(rpcHandler);
                    }
                });
        try{
            ChannelFuture future = bs.connect(host, port).sync();
            future.addListener(listen -> {
                if(listen.isSuccess()){
                    log.info("connect rpc success {}: {}", host, port);
                }
            });
            channel = future.channel();
            NettyConstants.clientMap.put(mapKey, channel);
        } catch (Exception e) {
            b1.shutdownGracefully();
            log.error("connect rpc service error,{}:{}", host, port);
        }
    }
    public Object sendRequest(RpcRequest rpcRequest) throws Exception {
        log.info("sendRequest rpcRequest = {}",rpcRequest);
        //自定义一个返回结果的回调 保存到map中
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(
                new DefaultPromise<RpcResponse>(new DefaultEventLoop())
        );
        NettyConstants.rpcFutureMap.put(rpcRequest.getReqId(), rpcFuture);

        //消息发送
        channel.writeAndFlush(JSON.toJSONString(rpcRequest));

        //阻塞等待回调
        return rpcFuture.getPromise().get().getContent();
    }
}

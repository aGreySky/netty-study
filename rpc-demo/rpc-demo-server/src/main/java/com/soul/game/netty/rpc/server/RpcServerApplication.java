package com.soul.game.netty.rpc.server;

import com.soul.game.netty.rpc.api.protocol.core.NettyServer;
import com.soul.game.netty.rpc.server.rpc.MyRpcHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class RpcServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
    }

    @Value("${server.rpcPort}")
    int port;

    @Override
    public void run(String... args) throws Exception {
        NettyServer.start(port, new MyRpcHandler());
    }
}

package com.soul.game.netty.rpc.server.service;

import com.soul.game.netty.rpc.api.annotation.RpcService;
import com.soul.game.netty.rpc.api.service.CalService;
import com.soul.game.netty.rpc.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 3:56 PM
 */
@RpcService
@Component
public class CalServiceImpl implements CalService {


    @Override
    public String hello(String name) {
        return "hello my name is " + name;
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}

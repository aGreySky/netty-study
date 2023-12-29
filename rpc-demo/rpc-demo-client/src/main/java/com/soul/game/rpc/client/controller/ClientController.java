package com.soul.game.rpc.client.controller;


import com.soul.game.netty.rpc.api.annotation.RpcReference;
import com.soul.game.netty.rpc.api.service.CalService;
import com.soul.game.netty.rpc.api.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 2:53 PM
 */
@RestController
public class ClientController {
    @RpcReference
    CalService calService;

    @RpcReference
    OrderService orderService;

    @GetMapping("/hello")
    public String hello(@RequestParam String orderId){
        return orderService.getOrder(orderId);
    }

    @GetMapping("/add")
    public int add(@RequestParam Integer a, @RequestParam Integer b){ return calService.add(a, b); }
}

package com.soul.game.netty.rpc.api.protocol.constants;

import lombok.Data;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 1:46 PM
 */
@Data
public class RpcRequest {
    private String reqId;

    private String service;

    private String method;

    private Object[] args;

    private Class[] paramType;
}

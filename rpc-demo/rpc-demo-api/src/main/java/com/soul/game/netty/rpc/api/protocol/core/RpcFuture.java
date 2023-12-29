package com.soul.game.netty.rpc.api.protocol.core;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @Author zhouzhiqiang
 * @Date 2023/7/6 1:40 PM
 */

@Data
public class RpcFuture<T> {
    private Promise<T> promise;

    public RpcFuture(Promise<T> promise){
        this.promise = promise;
    }
}

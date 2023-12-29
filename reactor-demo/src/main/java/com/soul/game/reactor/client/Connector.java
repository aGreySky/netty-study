package com.soul.game.reactor.client;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Connector implements Runnable{
    private final Selector selector;

    private final SocketChannel socketChannel;

    Connector(SocketChannel socketChannel, Selector selector){
        this.selector = selector;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run(){
        try{
            if(socketChannel.finishConnect()){
                System.out.println(String.format("connected to %s", socketChannel.getRemoteAddress()));
                //交给handler处理
                new Handler(socketChannel, selector);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

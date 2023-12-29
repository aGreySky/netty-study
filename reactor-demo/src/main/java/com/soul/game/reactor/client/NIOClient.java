package com.soul.game.reactor.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient implements Runnable {

    private Selector selector;
    private SocketChannel socketChannel;

    public NIOClient(String ip, int port) {
        try{
            selector = Selector.open(); //打开一个Selector
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(ip, port));

            //注册
            SelectionKey sk = socketChannel.register(selector, SelectionKey.OP_CONNECT);
            //连接就绪处理
            sk.attach(new Connector(socketChannel, selector));
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try{
            while(!Thread.interrupted()){
                //就绪之前阻塞
                selector.select();
                //拿到就绪时间
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selected.iterator();
                while(iterator.hasNext()){
                    dispatch((SelectionKey) iterator.next());
                }
                selected.clear();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey k){
        Runnable r = (Runnable) (k.attachment());

        //调用之前注册的回调对象
        if(r != null){
            r.run();
        }
    }
}

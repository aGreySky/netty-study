package com.soul.game.reactor.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable{

    private final ServerSocketChannel serverSocketChannel;
    private final int coreNum = Runtime.getRuntime().availableProcessors();
    private final Selector[] selectors = new Selector[coreNum]; //创建selector给subReactor使用

    private int next = 0; //轮询使用subReactor的下标索引

    private Thread[] threads = new Thread[coreNum]; //sub reactor的处理线程

    private SubReactor[] reactors = new SubReactor[coreNum];
    public Acceptor(ServerSocketChannel serverSocketChannel) throws IOException {
        this.serverSocketChannel = serverSocketChannel;
        for (int i = 0; i < coreNum; i++) {
            selectors[i] = Selector.open();
            reactors[i] = new SubReactor(selectors[i], i); //初始化sub reactor
            threads[i] = new Thread(reactors[i]); // 初始化运行sub reactor的线程
            threads[i].start();
        }
    }

    @Override
    public void run() {
        SocketChannel socketChannel;
        try {
            socketChannel = serverSocketChannel.accept();
            if (socketChannel != null){
                System.out.println(String.format("accept %s", socketChannel.getRemoteAddress()));
                socketChannel.configureBlocking(false);

                //注意一个selector在select时是无法注册新事件的，因此这里要先暂停下select方法触发的程序段
                //下面的wakeup和这里的setRestart都是做这个事情的，具体参考sub reactor里的run方法
                reactors[next].registering(true);
                selectors[next].wakeup(); //使一个阻塞住的selector操作立即返回
                SelectionKey selectionKey = socketChannel.register(selectors[next], SelectionKey.OP_READ);//注册事件
                selectors[next].wakeup(); //使一个阻塞住的selector操作立即返回
                //注册完成后，取消reactor的注册状态
                reactors[next].registering(false);

                //绑定handler
                selectionKey.attach(new AsyncHandler(socketChannel, selectors[next], next));
                if(++next == selectors.length){
                    next = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

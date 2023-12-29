package com.soul.game.reactor.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler implements Runnable {
    private final SelectionKey selectionKey;

    private final SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(2048);
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

    private final static int READ = 0;
    private final static int SEND = 1;

    private int status = SEND;  //默认是发送数据

    private AtomicInteger counter = new AtomicInteger();

    Handler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    @Override
    public void run(){
        try{
            switch (status){
                case SEND:
                    send();
                    break;
                case READ:
                    read();
                    break;
                default:
            }
        }catch (IOException e){
            //服务端断线时，断开连接
            System.out.println("异常退出：" + e.getMessage());
            selectionKey.cancel();
            try{
                socketChannel.close();
            }catch (IOException e2){
                System.out.println("关闭异常：" + e2.getMessage());
                e2.printStackTrace();
            }
        }

    }
    void send() throws IOException{
        if(selectionKey.isValid()){
            sendBuffer.clear();
            int count = counter.incrementAndGet();
            if(count <= 10){
                sendBuffer.put(String.format("msg is %d", count).getBytes());
                sendBuffer.flip(); //切换到读模式，让通道读到buffer里的数据
                socketChannel.write(sendBuffer);

                //再次切换到读，用以接收服务端的响应
                status = READ;
                selectionKey.interestOps(SelectionKey.OP_READ);
            }else{
                selectionKey.cancel();
                socketChannel.close();
            }
        }
    }

    void read() throws IOException {
        if(selectionKey.isValid()){
            readBuffer.clear();
            socketChannel.read(readBuffer);
            System.out.println(String.format("server -> client: %s",
                    new String(readBuffer.array(), 0, readBuffer.position())));

            //收到服务端响应，往服务端写数据
            status = SEND;
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }




}

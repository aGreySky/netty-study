package com.soul.game.reactor.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHandler implements Runnable{

    private final Selector selector;

    private final SelectionKey selectionKey;
    private final SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);

    private final static int READ = 0; //读取就绪
    private final static int SEND = 1; //响应就绪
    private final static int PROCESSING = 2; //处理中

    private int status = READ; //所有连接完成后都是从第一个读取动作开始的

    private int num;

    private static final ExecutorService workers = Executors.newFixedThreadPool(5);
    public AsyncHandler(SocketChannel socketChannel, Selector selector, int num) throws IOException {
        this.num = num; //为了区分handler被那个执行
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0); //将该客户端注册到selector
        selectionKey.attach(this); //附加处理对象
        selectionKey.interestOps(SelectionKey.OP_READ);
        this.selector = selector;
        this.selector.wakeup();
    }
    @Override
    public void run() {
        switch (status){
            case READ:
                read();
                break;
            case SEND:
                send();
                break;
            default:
        }
    }

    private void read() {
        if(selectionKey.isValid()){
            try{
                readBuffer.clear();

                //reader方法结束，
                int count = socketChannel.read(readBuffer);
                if(count > 0){
                    status = PROCESSING;
                    workers.execute(this::readWorker); //异步处理
                }else{
                    selectionKey.cancel();
                    socketChannel.close();
                    System.out.println(String.format("No. %d sub reactor read closed...", num));
                }
            }catch (IOException e){
                System.out.println("e：" + e.getMessage());
                selectionKey.cancel();
                try{
                    socketChannel.close();
                }catch (IOException e1){
                    System.out.println("e:" + e1.getMessage());
                }
            }
        }
    }

    public void send(){
        if(selectionKey.isValid()){
            status = PROCESSING;
            workers.execute(this::sendWorker);
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    private void readWorker(){
        try{
            Thread.sleep(5000L);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        try{
            System.out.println(String.format("NO. %d %s -> server: %s",
                    num, socketChannel.getRemoteAddress(),
                    new String(readBuffer.array(), 0, readBuffer.position())));
        }catch (IOException e){
            System.out.println("e:" + e.getMessage());
        }
        status = SEND;
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        this.selector.wakeup();
    }

    private void sendWorker(){
        try{
            sendBuffer.clear();
            sendBuffer.put(String.format("NO. %d sub reactor received %s from %s", num,
                    new String(readBuffer.array(), 0, readBuffer.position()),
                    socketChannel.getRemoteAddress()).getBytes());
            sendBuffer.flip();

            int count = socketChannel.write(sendBuffer);

            if(count < 0){
                selectionKey.cancel();
                socketChannel.close();
                System.out.println(String.format("%d sub reactor send closed", num));
            }

            status = READ;
        }catch (IOException e){
            System.out.println("e:" + e.getMessage());
            selectionKey.cancel();
            try{
                socketChannel.close();
            }catch (IOException e1){
                System.out.println("e:" + e1.getMessage());
            }
        }
    }
}

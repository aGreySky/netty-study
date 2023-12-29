package com.soul.game.reactor.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class SubReactor implements Runnable {
    private final Selector selector;

    private boolean register = false; //注册开关表示

    private int num; //序号，也就是acceptor初始化subReactor时的下标

    SubReactor(Selector selector, int num){
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run(){
        while (!Thread.interrupted()){
            System.out.println(String.format("Now %d sub reactor waiting for register...", num));
            while(!Thread.interrupted() && !register){
                try{
                    if(selector.select() == 0){
                        continue;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while(it.hasNext()){
                    dispatch(it.next());
                    it.remove();
                }
            }
        }
    }

    private void dispatch(SelectionKey key){
        Runnable r = (Runnable) key.attachment();
        if(r != null){
            r.run();
        }
    }

    public void registering(boolean register){
        this.register = register;
    }

}

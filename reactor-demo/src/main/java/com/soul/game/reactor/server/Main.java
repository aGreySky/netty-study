package com.soul.game.reactor.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Thread(new Reactor(2333)).start();
    }
}
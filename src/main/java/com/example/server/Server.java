package com.example.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService threadPool;

    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10); // Пул из 10 потоков
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("Server started on port " + port);

            while (true) {
                SocketChannel clientSocket = serverSocketChannel.accept();
                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

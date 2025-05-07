package com.intercom.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running;
    private MessageListener messageListener;

    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            
            // Start accepting connections in a separate thread
            threadPool.execute(() -> {
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        if (running) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        threadPool.execute(() -> {
            try (ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())) {
                while (running) {
                    Object message = input.readObject();
                    if (messageListener != null) {
                        messageListener.onMessageReceived(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        running = false;
        threadPool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface MessageListener {
        void onMessageReceived(Object message);
    }
} 
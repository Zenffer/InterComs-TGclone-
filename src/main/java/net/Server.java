/**
 * Server class implements a multi-threaded TCP server for handling client connections.
 * Uses a thread pool to manage client connections and message handling.
 * Supports asynchronous message processing through a listener interface.
 */
package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    /** Port number the server listens on */
    private final int port;
    
    /** Thread pool for managing client connections and message handling */
    private final ExecutorService threadPool;
    
    /** Server socket for accepting client connections */
    private ServerSocket serverSocket;
    
    /** Flag indicating if the server is currently running */
    private boolean running;
    
    /** Listener for handling received messages */
    private MessageListener messageListener;

    /**
     * Constructs a new Server instance.
     * 
     * @param port The port number to listen on
     */
    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newCachedThreadPool();
    }

    /**
     * Sets the message listener for handling received messages.
     * 
     * @param listener The listener to be notified of received messages
     */
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    /**
     * Starts the server and begins accepting client connections.
     * Creates a new thread for accepting connections and handles clients asynchronously.
     */
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

    /**
     * Handles a client connection in a separate thread.
     * Continuously reads messages from the client and notifies the message listener.
     * 
     * @param clientSocket The socket connected to the client
     */
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

    /**
     * Stops the server and cleans up resources.
     * Closes the server socket and shuts down the thread pool.
     */
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

    /**
     * Interface for receiving messages from clients.
     * Implementations of this interface will be notified when messages are received.
     */
    public interface MessageListener {
        /**
         * Called when a message is received from a client.
         * 
         * @param message The received message object
         */
        void onMessageReceived(Object message);
    }
} 
/**
 * Client class provides functionality for sending messages and files to a server.
 * Supports both object serialization for messages and binary transfer for files.
 * Includes message classes for structured communication.
 */
package net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Client {
    /** Host address of the server to connect to */
    private final String host;
    
    /** Port number of the server to connect to */
    private final int port;

    /**
     * Constructs a new Client instance.
     * 
     * @param host The host address of the server
     * @param port The port number of the server
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Sends a serializable object message to the server.
     * Creates a new connection for each message.
     * 
     * @param message The message object to send
     * @throws IOException if there's an error connecting to the server or sending the message
     */
    public void sendMessage(Object message) throws IOException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
            output.writeObject(message);
            output.flush();
        }
    }

    /**
     * Sends a file to the server.
     * First sends the file size, then streams the file data.
     * Uses a buffer for efficient transfer.
     * 
     * @param file The file to send
     * @throws IOException if there's an error connecting to the server or sending the file
     */
    public void sendFile(File file) throws IOException {
        try (Socket socket = new Socket(host, port);
             OutputStream output = socket.getOutputStream();
             FileInputStream fileInput = new FileInputStream(file)) {
            
            // Send file size first
            DataOutputStream dataOutput = new DataOutputStream(output);
            dataOutput.writeLong(file.length());
            
            // Send file data
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
        }
    }

    /**
     * Represents a text message in the chat system.
     * Contains sender information, message content, and timestamp.
     */
    public static class Message implements Serializable {
        /** Serialization version ID */
        private static final long serialVersionUID = 1L;
        
        /** Username of the message sender */
        private final String sender;
        
        /** Content of the message */
        private final String content;
        
        /** Timestamp when the message was created */
        private final long timestamp;

        /**
         * Constructs a new Message.
         * 
         * @param sender The username of the sender
         * @param content The message content
         */
        public Message(String sender, String content) {
            this.sender = sender;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        /** @return The username of the message sender */
        public String getSender() { return sender; }
        
        /** @return The content of the message */
        public String getContent() { return content; }
        
        /** @return The timestamp when the message was created */
        public long getTimestamp() { return timestamp; }
    }

    /**
     * Represents a file transfer message in the chat system.
     * Contains sender information, file details, and timestamp.
     */
    public static class FileMessage implements Serializable {
        /** Serialization version ID */
        private static final long serialVersionUID = 1L;
        
        /** Username of the file sender */
        private final String sender;
        
        /** Name of the file being sent */
        private final String fileName;
        
        /** Size of the file in bytes */
        private final long fileSize;
        
        /** Timestamp when the file message was created */
        private final long timestamp;

        /**
         * Constructs a new FileMessage.
         * 
         * @param sender The username of the sender
         * @param fileName The name of the file
         * @param fileSize The size of the file in bytes
         */
        public FileMessage(String sender, String fileName, long fileSize) {
            this.sender = sender;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.timestamp = System.currentTimeMillis();
        }

        /** @return The username of the file sender */
        public String getSender() { return sender; }
        
        /** @return The name of the file */
        public String getFileName() { return fileName; }
        
        /** @return The size of the file in bytes */
        public long getFileSize() { return fileSize; }
        
        /** @return The timestamp when the file message was created */
        public long getTimestamp() { return timestamp; }
    }
}
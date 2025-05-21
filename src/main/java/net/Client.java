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
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendMessage(Object message) throws IOException {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
            output.writeObject(message);
            output.flush();
        }
    }

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

    public static class Message implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String sender;
        private final String content;
        private final long timestamp;

        public Message(String sender, String content) {
            this.sender = sender;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public String getSender() { return sender; }
        public String getContent() { return content; }
        public long getTimestamp() { return timestamp; }
    }

    public static class FileMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String sender;
        private final String fileName;
        private final long fileSize;
        private final long timestamp;

        public FileMessage(String sender, String fileName, long fileSize) {
            this.sender = sender;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.timestamp = System.currentTimeMillis();
        }

        public String getSender() { return sender; }
        public String getFileName() { return fileName; }
        public long getFileSize() { return fileSize; }
        public long getTimestamp() { return timestamp; }
    }
}
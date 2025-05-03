package network;

import java.net.*;
import java.io.*;

public class ClientThread extends Thread {
    String ip, message;

    public ClientThread(String ip, String message) {
        this.ip = ip;
        this.message = message;
    }

    public void run() {
        try (Socket socket = new Socket(ip, 5000)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.out.println("Failed to send message");
        }
    }
}

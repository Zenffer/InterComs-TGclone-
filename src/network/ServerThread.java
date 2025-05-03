package network;

import javax.swing.*;
import java.net.*;
import java.io.*;

public class ServerThread extends Thread {
    JTextArea chatArea;

    public ServerThread(JTextArea area) {
        this.chatArea = area;
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(5000)) {
            while (true) {
                Socket socket = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = in.readLine();
                chatArea.append("Friend: " + msg + "\n");
                socket.close();
            }
        } catch (IOException e) {
            chatArea.append("Server error\n");
        }
    }
}

package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import network.*;
import util.FileTransfer;

public class ChatWindow extends JFrame {
    JTextArea chatArea = new JTextArea();
    JTextField messageField = new JTextField();
    JButton sendBtn = new JButton("Send");
    JButton fileBtn = new JButton("Send File");

    public ChatWindow(String username, String ip) {
        setTitle("Chat - " + username);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.EAST);
        panel.add(fileBtn, BorderLayout.WEST);
        add(panel, BorderLayout.SOUTH);

        new ServerThread(chatArea).start();

        sendBtn.addActionListener(e -> {
            String msg = messageField.getText();
            if (!msg.isEmpty()) {
                new ClientThread(ip, msg).start();
                chatArea.append("Me: " + msg + "\n");
                messageField.setText("");
            }
        });

        fileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file.length() <= 15 * 1024 * 1024) {
                    FileTransfer.sendFile(ip, file);
                    chatArea.append("File sent: " + file.getName() + "\n");
                } else {
                    JOptionPane.showMessageDialog(this, "File too large.");
                }
            }
        });

        setVisible(true);
    }
}

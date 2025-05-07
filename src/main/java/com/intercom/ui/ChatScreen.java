package com.intercom.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import com.intercom.messaging.ActiveMQHandler;
import com.intercom.storage.UserManager;
import com.intercom.storage.UserManager.User;

public class ChatScreen extends JFrame {
    private final User currentUser;
    private final JTextArea chatArea;
    private final JList<String> userList;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton fileButton;
    private final JButton logoutButton;
    private final ActiveMQHandler activeMQHandler;
    private static final int PORT = 61616;

    public ChatScreen(User user) {
        this.currentUser = user;
        
        // Initialize UI components
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        userList = new JList<>();
        messageField = new JTextField();
        sendButton = new JButton("Send");
        fileButton = new JButton("Send File");
        logoutButton = new JButton("Logout");
        
        // Initialize ActiveMQ
        activeMQHandler = new ActiveMQHandler();
        try {
            activeMQHandler.connect();
            setupActiveMQ();
        } catch (JMSException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to ActiveMQ: " + e.getMessage());
            e.printStackTrace();
        }
        
        initializeUI();
        setupEventHandlers();
    }

    private void setupActiveMQ() throws JMSException {
        // Create a queue for private messages
        String privateQueue = "queue." + currentUser.getUsername();
        activeMQHandler.createQueue(privateQueue);
        
        // Create a topic for broadcast messages
        String broadcastTopic = "topic.broadcast";
        activeMQHandler.createTopic(broadcastTopic);
        
        // Set up message listener
        activeMQHandler.setMessageListener(privateQueue, new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        SwingUtilities.invokeLater(() -> {
                            chatArea.append("Received: " + text + "\n");
                        });
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeUI() {
        setTitle("InterCom - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for logout button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(logoutButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Chat area with scroll pane
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(600, 400));
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // User list with scroll pane
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(userScrollPane, BorderLayout.EAST);

        // Bottom panel for message input
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        bottomPanel.add(messageField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(fileButton);
        buttonPanel.add(sendButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateUserList();
    }

    private void setupEventHandlers() {
        // Send message on Enter key
        messageField.addActionListener(e -> sendMessage());
        
        // Send button click
        sendButton.addActionListener(e -> sendMessage());
        
        // File button click
        fileButton.addActionListener(e -> handleFileSend());
        
        // Logout button click
        logoutButton.addActionListener(e -> handleLogout());
        
        // Window close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                try {
                    // Send private message
                    String queueName = "queue." + selectedUser;
                    String fullMessage = currentUser.getUsername() + ": " + message;
                    activeMQHandler.sendMessage(queueName, fullMessage);
                    
                    // Update chat area
                    chatArea.append("You to " + selectedUser + ": " + message + "\n");
                    messageField.setText("");
                } catch (JMSException e) {
                    JOptionPane.showMessageDialog(this, "Error sending message: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to send message to");
            }
        }
    }

    private void handleFileSend() {
        String selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to send file to");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                // Send file notification
                String queueName = "queue." + selectedUser;
                String message = "FILE:" + fileChooser.getSelectedFile().getName();
                activeMQHandler.sendMessage(queueName, message);
                
                // Update chat area
                chatArea.append("Sending file to " + selectedUser + ": " + 
                              fileChooser.getSelectedFile().getName() + "\n");
            } catch (JMSException e) {
                JOptionPane.showMessageDialog(this, "Error sending file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleLogout() {
        cleanup();
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }

    private void updateUserList() {
        List<User> users = UserManager.getInstance().getAllUsers();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        for (User user : users) {
            if (!user.getUsername().equals(currentUser.getUsername())) {
                listModel.addElement(user.getUsername());
            }
        }
        
        userList.setModel(listModel);
    }

    private void cleanup() {
        try {
            if (activeMQHandler != null) {
                activeMQHandler.disconnect();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
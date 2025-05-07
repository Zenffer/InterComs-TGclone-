package com.intercom.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intercom.storage.UserManager;
import com.intercom.ui.ChatScreen;

public class LoginScreen extends JFrame {
    private final UserManager userManager;
    private JTextField usernameField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JComboBox<String> userComboBox;

    public LoginScreen() {
        userManager = UserManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("InterCom Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Welcome to InterCom");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Username field
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(10));

        // Existing users dropdown
        userComboBox = new JComboBox<>();
        userComboBox.setMaximumSize(new Dimension(300, 30));
        userComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateUserList();
        mainPanel.add(userComboBox);
        mainPanel.add(Box.createVerticalStrut(10));

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create Account");

        loginButton.addActionListener(e -> handleLogin());
        createAccountButton.addActionListener(e -> handleCreateAccount());

        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private void updateUserList() {
        userComboBox.removeAllItems();
        userComboBox.addItem("Select existing user...");
        for (UserManager.User user : userManager.getAllUsers()) {
            userComboBox.addItem(user.getUsername());
        }
    }

    private void handleLogin() {
        String selectedUser = (String) userComboBox.getSelectedItem();
        if (selectedUser == null || selectedUser.equals("Select existing user...")) {
            JOptionPane.showMessageDialog(this, "Please select a user to login");
            return;
        }

        UserManager.User user = userManager.findUserByUsername(selectedUser);
        if (user != null) {
            openChatScreen(user);
        }
    }

    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username");
            return;
        }

        if (userManager.findUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already exists");
            return;
        }

        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            userManager.addUser(username, ip);
            updateUserList();
            usernameField.setText("");
            
            UserManager.User newUser = userManager.findUserByUsername(username);
            openChatScreen(newUser);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating account: " + e.getMessage());
        }
    }

    private void openChatScreen(UserManager.User user) {
        ChatScreen chatScreen = new ChatScreen(user);
        chatScreen.setVisible(true);
        this.dispose();
    }
} 
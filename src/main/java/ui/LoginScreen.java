package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.net.InetAddress;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import storage.UserManager;

public class LoginScreen extends JFrame {
    private final UserManager userManager;
    private JTextField usernameField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JComboBox<String> userComboBox;
    
    // Color constants
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80); // #2c3e50
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241); // #ecf0f1
    private static final Color ACCENT_COLOR = new Color(52, 152, 219); // #3498db

    public LoginScreen() {
        userManager = UserManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("InterCom Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set the background color for the frame
        getContentPane().setBackground(PRIMARY_COLOR);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(PRIMARY_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Welcome to InterCom");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(SECONDARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Username field
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBackground(SECONDARY_COLOR);
        usernameField.setForeground(PRIMARY_COLOR);
        usernameField.setCaretColor(PRIMARY_COLOR);
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Existing users dropdown
        userComboBox = new JComboBox<>();
        userComboBox.setMaximumSize(new Dimension(300, 35));
        userComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        userComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userComboBox.setBackground(SECONDARY_COLOR);
        userComboBox.setForeground(PRIMARY_COLOR);
        updateUserList();
        mainPanel.add(userComboBox);
        mainPanel.add(Box.createVerticalStrut(25));

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(PRIMARY_COLOR);

        loginButton = createStyledButton("Login");
        createAccountButton = createStyledButton("Create Account");

        loginButton.addActionListener(e -> handleLogin());
        createAccountButton.addActionListener(e -> handleCreateAccount());

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Make both buttons the same size
        Dimension buttonSize = new Dimension(180, 35);
        loginButton.setMaximumSize(buttonSize);
        loginButton.setPreferredSize(buttonSize);
        createAccountButton.setMaximumSize(buttonSize);
        createAccountButton.setPreferredSize(buttonSize);

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createAccountButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(SECONDARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        
        return button;
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
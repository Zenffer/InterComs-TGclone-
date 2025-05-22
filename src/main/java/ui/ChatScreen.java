/**
 * ChatScreen class represents the main chat interface of the InterCom application.
 * It provides real-time messaging functionality using ActiveMQ for message handling.
 * The UI includes a chat area, user list, and message input components.
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import messaging.ActiveMQHandler;
import storage.UserManager;
import storage.UserManager.User;

public class ChatScreen extends JFrame {
    /** Current user of the chat session */
    private final User currentUser;
    
    /** UI Components */
    private final JTextArea chatArea;
    private final JList<String> userList;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton logoutButton;
    
    /** Message broker handler for real-time communication */
    private final ActiveMQHandler activeMQHandler;
    
    /** ActiveMQ broker port */
    private static final int PORT = 61616;

    /** UI Color scheme constants */
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80); // #2c3e50 - Dark blue-gray
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241); // #ecf0f1 - Light gray
    private static final Color ACCENT_COLOR = new Color(52, 152, 219); // #3498db - Bright blue

    /**
     * Constructs a new ChatScreen for the specified user.
     * Initializes UI components and sets up ActiveMQ connection.
     * 
     * @param user The user to create the chat screen for
     */
    public ChatScreen(User user) {
        this.currentUser = user;
        
        // Initialize UI components
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Helvetica", Font.PLAIN, 14));
        chatArea.setBackground(SECONDARY_COLOR);
        chatArea.setForeground(PRIMARY_COLOR);
        
        userList = new JList<>();
        userList.setFont(new Font("Helvetica", Font.PLAIN, 14));
        userList.setBackground(SECONDARY_COLOR);
        userList.setForeground(PRIMARY_COLOR);
        userList.setSelectionBackground(ACCENT_COLOR);
        userList.setSelectionForeground(SECONDARY_COLOR);
        
        messageField = new JTextField();
        messageField.setFont(new Font("Helvetica", Font.PLAIN, 14));
        messageField.setBackground(SECONDARY_COLOR);
        messageField.setForeground(PRIMARY_COLOR);
        messageField.setCaretColor(PRIMARY_COLOR);
        
        sendButton = createStyledButton("Send");
        logoutButton = createStyledButton("Logout");
        
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

    /**
     * Creates a styled button with hover effects and consistent appearance.
     * 
     * @param text The text to display on the button
     * @return A styled JButton instance
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.BOLD, 14));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(SECONDARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        
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

    /**
     * Sets up ActiveMQ connection and message handling.
     * Creates private queue for the user and broadcast topic.
     * Configures message listener for incoming messages.
     * 
     * @throws JMSException if there's an error in the JMS setup
     */
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

    /**
     * Initializes and sets up the main UI components of the chat screen.
     * Creates a layout with user list, chat area, and message input.
     */
    private void initializeUI() {
        setTitle("InterCom - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Set the background color for the frame
        getContentPane().setBackground(PRIMARY_COLOR);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(PRIMARY_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top panel for Contacts label and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        
        JLabel userListLabel = new JLabel("Contacts");
        userListLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
        userListLabel.setForeground(SECONDARY_COLOR);
        userListLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        topPanel.add(userListLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // User list panel (now on the left)
        JPanel userListPanel = new JPanel(new BorderLayout());
        userListPanel.setBackground(PRIMARY_COLOR);
        
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(200, 0));
        userScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        userListPanel.add(userScrollPane, BorderLayout.CENTER);
        mainPanel.add(userListPanel, BorderLayout.WEST);

        // Chat area with scroll pane
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Bottom panel for message input
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        bottomPanel.setBackground(PRIMARY_COLOR);
        bottomPanel.add(messageField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);
        buttonPanel.add(sendButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateUserList();
    }

    /**
     * Sets up event handlers for user interactions.
     * Configures actions for message sending, logout, and window closing.
     */
    private void setupEventHandlers() {
        // Send message on Enter key
        messageField.addActionListener(e -> sendMessage());
        
        // Send button click
        sendButton.addActionListener(e -> sendMessage());
        
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

    /**
     * Handles sending messages to selected users.
     * Sends private messages through ActiveMQ and updates the chat display.
     */
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

    /**
     * Handles user logout.
     * Cleans up resources and returns to the login screen.
     */
    private void handleLogout() {
        cleanup();
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }

    /**
     * Updates the list of available users in the chat interface.
     * Called when the user list changes.
     */
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

    /**
     * Cleans up resources when closing the chat screen.
     * Closes ActiveMQ connection and performs necessary cleanup.
     */
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
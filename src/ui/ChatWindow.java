
package ui;

import util.ConversationLogger;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ChatWindow extends JFrame {
    private JTextArea chatArea = new JTextArea();
    private JTextField messageField = new JTextField();
    private JButton sendBtn = new JButton("Send");
    private JButton logoutBtn = new JButton("Logout");
    private JList<String> contactsList;
    private DefaultListModel<String> contactsModel = new DefaultListModel<>();
    private String username;
    private String currentContact;
    private Timer refreshTimer;

    public ChatWindow(String username, List<String> contacts) {
        this.username = username;
        setTitle("Chat - " + username);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Contacts list
        for (String contact : contacts) contactsModel.addElement(contact);
        contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (!contactsModel.isEmpty()) {
            contactsList.setSelectedIndex(0);
            currentContact = contactsList.getSelectedValue();
        }
        JScrollPane contactsScroll = new JScrollPane(contactsList);
        contactsScroll.setPreferredSize(new Dimension(150, 0));
        add(contactsScroll, BorderLayout.WEST);

        // Chat area
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Bottom panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);

        // Top panel for logout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(logoutBtn);
        add(topPanel, BorderLayout.NORTH);

        // Load initial conversation
        loadConversation();

        // Listen for contact changes
        contactsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    currentContact = contactsList.getSelectedValue();
                    loadConversation();
                }
            }
        });

        sendBtn.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        logoutBtn.addActionListener(e -> logout());

        // Timer to refresh conversation every 3 seconds (3000 ms)
        refreshTimer = new Timer(3000, e -> loadConversation());
        refreshTimer.start();

        // Stop timer on window close to avoid memory leaks
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (refreshTimer != null) {
                    refreshTimer.stop();
                }
            }
        });

        setVisible(true);
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty() && currentContact != null) {
            String timestamp = ConversationLogger.getCurrentTimestamp();
            chatArea.append("Me: " + msg + " [" + timestamp + "]\n");
            ConversationLogger.appendMessage(username, currentContact, username, msg, timestamp);
            messageField.setText("");
        }
    }

    private void loadConversation() {
        // Save caret position if at bottom, else don't scroll
        int oldCaret = chatArea.getCaretPosition();
        int docLen = chatArea.getDocument().getLength();
        boolean atBottom = (oldCaret == docLen);

        chatArea.setText("");
        if (currentContact != null) {
            List<String> messages = ConversationLogger.readConversation(username, currentContact);
            for (String line : messages) chatArea.append(line + "\n");
        }

        // Restore caret position
        if (atBottom) {
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } else {
            chatArea.setCaretPosition(oldCaret);
        }
    }

    private void logout() {
        // Dispose this window and show the login window
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}

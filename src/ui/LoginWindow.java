
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import storage.UserManager;
import storage.UserManager.User;

public class LoginWindow extends JFrame {
    private JComboBox<String> userCombo;
    private JTextField usernameField;
    private JTextField ipField;
    private UserManager userManager;
    private List<User> users;

    public LoginWindow() {
        setTitle("Login");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        userManager = new UserManager();

        // Load users from XML
        users = userManager.loadUsers();

        // UI Components
        userCombo = new JComboBox<>();
        refreshUserList();

        usernameField = new JTextField();
        ipField = new JTextField();

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create Account");

        // Layout
        setLayout(new GridLayout(7, 1, 5, 5));
        add(new JLabel("Select Existing User:"));
        add(userCombo);
        add(new JLabel("Or Create New Account:"));
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("IP Address:"));
        add(ipField);

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        btnPanel.add(createBtn);
        add(btnPanel);

        // Actions
        loginBtn.addActionListener((ActionEvent e) -> {
            int idx = userCombo.getSelectedIndex();
            if (users != null && idx >= 0 && idx < users.size()) {
                User selected = users.get(idx);
                openChat(selected.username, selected.ip);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user or create a new account.");
            }
        });

        createBtn.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String ip = ipField.getText().trim();
            if (username.isEmpty() || ip.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter both username and IP.");
                return;
            }
            if (!ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
                JOptionPane.showMessageDialog(this, "Enter a valid IP address.");
                return;
            }
            boolean exists = users != null && users.stream().anyMatch(u -> u.username.equals(username));
            if (exists) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }
            userManager.addUser(username, ip);
            JOptionPane.showMessageDialog(this, "Account created! Please select it from the list.");
            refreshUserList();
            userCombo.setSelectedItem(username + " (" + ip + ")");
            usernameField.setText("");
            ipField.setText("");
        });

        setVisible(true);
    }

    private void refreshUserList() {
        users = userManager.loadUsers();
        userCombo.removeAllItems();
        if (users != null) {
            for (User u : users) {
                userCombo.addItem(u.username + " (" + u.ip + ")");
            }
        }
        userCombo.setSelectedIndex(-1);
    }

    private void openChat(String username, String ip) {
        try {
            new ChatWindow(username, ip);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to open chat window: " + e.getMessage());
        }
    }
}

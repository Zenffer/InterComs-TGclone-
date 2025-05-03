
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
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        userManager = new UserManager();

        // Load users from XML
        users = userManager.loadUsers();

        // UI Components
        userCombo = new JComboBox<>();
        refreshUserList();

        usernameField = new JTextField(15);
        ipField = new JTextField(15);

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create Account");

        // Layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Existing user label
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel existingLabel = new JLabel("Select Existing User:");
        existingLabel.setFont(existingLabel.getFont().deriveFont(Font.BOLD));
        mainPanel.add(existingLabel, gbc);

        // User combo box
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        mainPanel.add(userCombo, gbc);

        // Separator
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(1, 10));
        mainPanel.add(sep, gbc);

        // New account label
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel newAccLabel = new JLabel("Or Create New Account:");
        newAccLabel.setFont(newAccLabel.getFont().deriveFont(Font.BOLD));
        mainPanel.add(newAccLabel, gbc);

        // Username label and field
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // IP label and field
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("IP Address:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(ipField, gbc);

        // Button panel
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.add(loginBtn);
        btnPanel.add(createBtn);
        mainPanel.add(btnPanel, gbc);

        setContentPane(mainPanel);

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

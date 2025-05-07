package com.intercom;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.intercom.ui.LoginScreen;

public class App {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Start the application on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
} 
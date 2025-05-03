package main;

import ui.LoginWindow;

public class Main {
    public static void main(String[] args) {
        // Ensure GUI is created on the Event Dispatch Thread hi
        javax.swing.SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}
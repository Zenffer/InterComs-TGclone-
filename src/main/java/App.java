/**
 * Main application class that serves as the entry point for the InterComs application.
 * This class initializes the UI and sets up the system look and feel.
 */
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.LoginScreen;

public class App {
    /**
     * Main entry point of the application.
     * Sets up the system look and feel and launches the login screen.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel to match the operating system's native appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Launch the login screen on the Event Dispatch Thread (EDT)
            // This ensures thread safety for Swing components
            SwingUtilities.invokeLater(() -> {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            });
        } catch (Exception e) {
            // Log any initialization errors and exit the application
            e.printStackTrace();
            System.exit(1);
        }
    }
} 
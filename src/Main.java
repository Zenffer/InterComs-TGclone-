// File: src/Main.java
import java.util.Arrays;
import ui.ChatWindow;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ChatWindow("alice", Arrays.asList("bob", "carol", "dave"));
        });
    }
}
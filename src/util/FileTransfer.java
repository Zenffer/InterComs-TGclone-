package util;

import java.io.*;
import java.net.*;

public class FileTransfer {
    public static void sendFile(String ip, File file) {
        new Thread(() -> {
            try (Socket socket = new Socket(ip, 6000);
                 FileInputStream fis = new FileInputStream(file);
                 BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {

                byte[] buffer = new byte[4096];
                int count;
                while ((count = fis.read(buffer)) > 0) {
                    bos.write(buffer, 0, count);
                }
                bos.flush();
            } catch (IOException e) {
                System.out.println("File send failed");
            }
        }).start();
    }
}

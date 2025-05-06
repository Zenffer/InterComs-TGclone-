// File: src/util/ConversationLogger.java
package util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class ConversationLogger {
    private static final String DATA_DIR = "data";

    public static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static String getFileName(String user1, String user2) {
        List<String> users = Arrays.asList(user1, user2);
        Collections.sort(users);
        return DATA_DIR + "/" + users.get(0) + "_" + users.get(1) + ".xml";
    }

    public static void appendMessage(String user1, String user2, String sender, String message, String timestamp) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();
            String fileName = getFileName(user1, user2);
            File file = new File(fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            Element rootElement;

            if (file.exists()) {
                doc = dBuilder.parse(file);
                rootElement = doc.getDocumentElement();
            } else {
                doc = dBuilder.newDocument();
                rootElement = doc.createElement("conversation");
                doc.appendChild(rootElement);
            }

            Element msgElem = doc.createElement("message");
            msgElem.setAttribute("sender", sender);
            msgElem.setAttribute("timestamp", timestamp);
            msgElem.setTextContent(message);
            rootElement.appendChild(msgElem);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> readConversation(String user1, String user2) {
        List<String> messages = new ArrayList<>();
        String fileName = getFileName(user1, user2);
        File file = new File(fileName);
        if (!file.exists()) return messages;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList msgNodes = doc.getElementsByTagName("message");
            for (int i = 0; i < msgNodes.getLength(); i++) {
                Element msgElem = (Element) msgNodes.item(i);
                String sender = msgElem.getAttribute("sender");
                String timestamp = msgElem.getAttribute("timestamp");
                String text = msgElem.getTextContent();
                messages.add(sender + ": " + text + " [" + timestamp + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}
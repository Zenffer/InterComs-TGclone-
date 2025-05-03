package storage;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String XML_PATH = "data/users.xml";

    public static class User {
        public String username;
        public String ip;

        public User(String username, String ip) {
            this.username = username;
            this.ip = ip;
        }
    }

    // Load all users from XML
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try {
            File xmlFile = new File(XML_PATH);
            if (!xmlFile.exists()) return users;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList userNodes = doc.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Node node = userNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String username = elem.getElementsByTagName("username").item(0).getTextContent();
                    String ip = elem.getElementsByTagName("ip").item(0).getTextContent();
                    users.add(new User(username, ip));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Save all users to XML
    public void saveUsers(List<User> users) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element root = doc.createElement("users");
            doc.appendChild(root);

            for (User user : users) {
                Element userElem = doc.createElement("user");

                Element usernameElem = doc.createElement("username");
                usernameElem.appendChild(doc.createTextNode(user.username));
                userElem.appendChild(usernameElem);

                Element ipElem = doc.createElement("ip");
                ipElem.appendChild(doc.createTextNode(user.ip));
                userElem.appendChild(ipElem);

                root.appendChild(userElem);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(XML_PATH));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add a new user and save
    public void addUser(String username, String ip) {
        List<User> users = loadUsers();
        users.add(new User(username, ip));
        saveUsers(users);
    }

    // Remove a user by username and save
    public void removeUser(String username) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.username.equals(username));
        saveUsers(users);
    }

    // Find a user by username
    public User findUser(String username) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.username.equals(username)) return u;
        }
        return null;
    }
}
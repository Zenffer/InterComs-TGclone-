package com.intercom.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UserManager {
    private static final String USERS_XML_PATH = "src/main/resources/data/users.xml";
    private Document document;
    private static UserManager instance;

    private UserManager() {
        try {
            loadDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private void loadDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(USERS_XML_PATH);
        
        if (file.exists()) {
            document = builder.parse(file);
        } else {
            document = builder.newDocument();
            Element root = document.createElement("users");
            document.appendChild(root);
            saveDocument();
        }
    }

    private void saveDocument() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(USERS_XML_PATH));
        transformer.transform(source, result);
    }

    public void addUser(String username, String ip) throws Exception {
        Element user = document.createElement("user");
        
        Element idElement = document.createElement("id");
        idElement.appendChild(document.createTextNode(generateUserId()));
        
        Element usernameElement = document.createElement("username");
        usernameElement.appendChild(document.createTextNode(username));
        
        Element ipElement = document.createElement("ip");
        ipElement.appendChild(document.createTextNode(ip));
        
        user.appendChild(idElement);
        user.appendChild(usernameElement);
        user.appendChild(ipElement);
        
        document.getDocumentElement().appendChild(user);
        saveDocument();
    }

    private String generateUserId() {
        NodeList users = document.getElementsByTagName("user");
        return String.format("%03d", users.getLength() + 1);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        NodeList userNodes = document.getElementsByTagName("user");
        
        for (int i = 0; i < userNodes.getLength(); i++) {
            Element userElement = (Element) userNodes.item(i);
            String id = userElement.getElementsByTagName("id").item(0).getTextContent();
            String username = userElement.getElementsByTagName("username").item(0).getTextContent();
            String ip = userElement.getElementsByTagName("ip").item(0).getTextContent();
            
            users.add(new User(id, username, ip));
        }
        
        return users;
    }

    public User findUserByUsername(String username) {
        NodeList users = document.getElementsByTagName("user");
        
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            String currentUsername = user.getElementsByTagName("username").item(0).getTextContent();
            
            if (currentUsername.equals(username)) {
                String id = user.getElementsByTagName("id").item(0).getTextContent();
                String ip = user.getElementsByTagName("ip").item(0).getTextContent();
                return new User(id, username, ip);
            }
        }
        
        return null;
    }

    public static class User {
        private final String id;
        private final String username;
        private final String ip;

        public User(String id, String username, String ip) {
            this.id = id;
            this.username = username;
            this.ip = ip;
        }

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getIp() { return ip; }
    }
}
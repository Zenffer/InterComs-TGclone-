/**
 * UserManager class manages user data persistence using XML storage.
 * Implements the Singleton pattern to ensure a single instance manages user data.
 * Handles user creation, retrieval, and storage operations.
 */
package storage;

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
    /** Path to the XML file storing user data */
    private static final String USERS_XML_PATH = "src/main/resources/data/users.xml";
    
    /** XML document containing user data */
    private Document document;
    
    /** Singleton instance of UserManager */
    private static UserManager instance;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the XML document on creation.
     */
    private UserManager() {
        try {
            loadDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the singleton instance of UserManager.
     * Creates a new instance if one doesn't exist.
     * 
     * @return The singleton UserManager instance
     */
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Loads the XML document from file or creates a new one if it doesn't exist.
     * 
     * @throws Exception if there's an error loading or creating the document
     */
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

    /**
     * Saves the current XML document to file with proper formatting.
     * 
     * @throws Exception if there's an error saving the document
     */
    private void saveDocument() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(USERS_XML_PATH));
        transformer.transform(source, result);
    }

    /**
     * Adds a new user to the XML storage.
     * Creates a new user element with ID, username, and IP address.
     * 
     * @param username The username of the new user
     * @param ip The IP address of the new user
     * @throws Exception if there's an error adding the user or saving the document
     */
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

    /**
     * Generates a unique user ID based on the current number of users.
     * IDs are formatted as three-digit numbers (e.g., "001", "002").
     * 
     * @return A new unique user ID
     */
    private String generateUserId() {
        NodeList users = document.getElementsByTagName("user");
        return String.format("%03d", users.getLength() + 1);
    }

    /**
     * Retrieves all users from the XML storage.
     * 
     * @return A list of all User objects
     */
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

    /**
     * Finds a user by their username.
     * 
     * @param username The username to search for
     * @return The User object if found, null otherwise
     */
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

    /**
     * Inner class representing a user in the system.
     * Contains user ID, username, and IP address.
     */
    public static class User {
        /** Unique identifier for the user */
        private final String id;
        
        /** Username of the user */
        private final String username;
        
        /** IP address of the user */
        private final String ip;

        /**
         * Constructs a new User object.
         * 
         * @param id The user's unique identifier
         * @param username The user's username
         * @param ip The user's IP address
         */
        public User(String id, String username, String ip) {
            this.id = id;
            this.username = username;
            this.ip = ip;
        }

        /** @return The user's unique identifier */
        public String getId() { return id; }
        
        /** @return The user's username */
        public String getUsername() { return username; }
        
        /** @return The user's IP address */
        public String getIp() { return ip; }
    }
}
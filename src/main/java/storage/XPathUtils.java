/**
 * XPathUtils class provides utility methods for XPath operations on XML documents.
 * Offers simplified access to common XPath queries and operations.
 * Uses a singleton XPath instance for better performance.
 */
package storage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XPathUtils {
    /** Singleton XPathFactory instance for creating XPath objects */
    private static final XPathFactory xPathFactory = XPathFactory.newInstance();
    
    /** Singleton XPath instance for performing queries */
    private static final XPath xPath = xPathFactory.newXPath();

    /**
     * Queries an XML document for a set of nodes matching the XPath expression.
     * 
     * @param document The XML document to query
     * @param expression The XPath expression to evaluate
     * @return A NodeList containing all matching nodes
     * @throws XPathExpressionException if the XPath expression is invalid
     */
    public static NodeList queryNodes(Document document, String expression) throws XPathExpressionException {
        return (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
    }

    /**
     * Queries an XML document for a single string value.
     * Returns the text content of the first matching node.
     * 
     * @param document The XML document to query
     * @param expression The XPath expression to evaluate
     * @return The string value of the first matching node
     * @throws XPathExpressionException if the XPath expression is invalid
     */
    public static String queryString(Document document, String expression) throws XPathExpressionException {
        return (String) xPath.evaluate(expression, document, XPathConstants.STRING);
    }

    /**
     * Queries an XML document for a list of string values.
     * Returns the text content of all matching nodes as a list.
     * 
     * @param document The XML document to query
     * @param expression The XPath expression to evaluate
     * @return A list of string values from all matching nodes
     * @throws XPathExpressionException if the XPath expression is invalid
     */
    public static List<String> queryStringList(Document document, String expression) throws XPathExpressionException {
        NodeList nodes = queryNodes(document, expression);
        List<String> results = new ArrayList<>();
        
        for (int i = 0; i < nodes.getLength(); i++) {
            results.add(nodes.item(i).getTextContent());
        }
        
        return results;
    }

    /**
     * Checks if any nodes exist matching the XPath expression.
     * 
     * @param document The XML document to query
     * @param expression The XPath expression to evaluate
     * @return true if at least one matching node exists, false otherwise
     * @throws XPathExpressionException if the XPath expression is invalid
     */
    public static boolean nodeExists(Document document, String expression) throws XPathExpressionException {
        NodeList nodes = queryNodes(document, expression);
        return nodes.getLength() > 0;
    }

    /**
     * Inner class containing common XPath expressions used throughout the application.
     * Expressions use format specifiers (%s) for dynamic values.
     */
    public static class Expressions {
        /** XPath expression to select all user nodes */
        public static final String ALL_USERS = "//user";
        
        /** XPath expression to select a user by username (use String.format with username) */
        public static final String USER_BY_USERNAME = "//user[username='%s']";
        
        /** XPath expression to select a user by ID (use String.format with ID) */
        public static final String USER_BY_ID = "//user[id='%s']";
        
        /** XPath expression to get a user's IP address (use String.format with username) */
        public static final String USER_IP = "//user[username='%s']/ip";
        
        /** XPath expression to get a user's ID (use String.format with username) */
        public static final String USER_ID = "//user[username='%s']/id";
    }
}

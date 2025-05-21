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
    private static final XPathFactory xPathFactory = XPathFactory.newInstance();
    private static final XPath xPath = xPathFactory.newXPath();

    public static NodeList queryNodes(Document document, String expression) throws XPathExpressionException {
        return (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
    }

    public static String queryString(Document document, String expression) throws XPathExpressionException {
        return (String) xPath.evaluate(expression, document, XPathConstants.STRING);
    }

    public static List<String> queryStringList(Document document, String expression) throws XPathExpressionException {
        NodeList nodes = queryNodes(document, expression);
        List<String> results = new ArrayList<>();
        
        for (int i = 0; i < nodes.getLength(); i++) {
            results.add(nodes.item(i).getTextContent());
        }
        
        return results;
    }

    public static boolean nodeExists(Document document, String expression) throws XPathExpressionException {
        NodeList nodes = queryNodes(document, expression);
        return nodes.getLength() > 0;
    }

    // Common XPath expressions
    public static class Expressions {
        public static final String ALL_USERS = "//user";
        public static final String USER_BY_USERNAME = "//user[username='%s']";
        public static final String USER_BY_ID = "//user[id='%s']";
        public static final String USER_IP = "//user[username='%s']/ip";
        public static final String USER_ID = "//user[username='%s']/id";
    }
}

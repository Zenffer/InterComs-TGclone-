package messaging;

import org.apache.activemq.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class ActiveMQHandler {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private Connection connection;
    private Session session;

    public void connect() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void disconnect() throws JMSException {
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    public void createQueue(String queueName) throws JMSException {
        Destination queue = session.createQueue(queueName);
    }

    public void createTopic(String topicName) throws JMSException {
        Destination topic = session.createTopic(topicName);
    }

    public void setMessageListener(String destinationName, MessageListener listener) throws JMSException {
        Destination destination = session.createQueue(destinationName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(listener);
    }

    public void sendMessage(String destinationName, String message) throws JMSException {
        Destination destination = session.createQueue(destinationName);
        MessageProducer producer = session.createProducer(destination);
        TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);
    }
} 
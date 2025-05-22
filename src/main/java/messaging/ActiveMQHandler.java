/**
 * ActiveMQHandler class manages JMS (Java Message Service) connections and operations using ActiveMQ.
 * Provides functionality for creating queues and topics, sending messages, and setting up message listeners.
 * Handles connection lifecycle and session management for message-oriented middleware.
 */
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
    /** URL of the ActiveMQ broker (default: localhost on standard port) */
    private static final String BROKER_URL = "tcp://localhost:61616";
    
    /** JMS connection to the message broker */
    private Connection connection;
    
    /** JMS session for creating producers and consumers */
    private Session session;

    /**
     * Establishes a connection to the ActiveMQ broker and creates a session.
     * The session is created with non-transacted mode and auto-acknowledge.
     * 
     * @throws JMSException if there's an error connecting to the broker or creating the session
     */
    public void connect() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * Closes the JMS session and connection to the broker.
     * Should be called when the handler is no longer needed.
     * 
     * @throws JMSException if there's an error closing the session or connection
     */
    public void disconnect() throws JMSException {
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Creates a new queue in the message broker.
     * Queues are used for point-to-point messaging.
     * 
     * @param queueName The name of the queue to create
     * @throws JMSException if there's an error creating the queue
     */
    public void createQueue(String queueName) throws JMSException {
        Destination queue = session.createQueue(queueName);
    }

    /**
     * Creates a new topic in the message broker.
     * Topics are used for publish-subscribe messaging.
     * 
     * @param topicName The name of the topic to create
     * @throws JMSException if there's an error creating the topic
     */
    public void createTopic(String topicName) throws JMSException {
        Destination topic = session.createTopic(topicName);
    }

    /**
     * Sets up a message listener for a specific destination (queue or topic).
     * The listener will be notified when messages arrive at the destination.
     * 
     * @param destinationName The name of the queue or topic to listen to
     * @param listener The message listener to handle incoming messages
     * @throws JMSException if there's an error setting up the consumer or listener
     */
    public void setMessageListener(String destinationName, MessageListener listener) throws JMSException {
        Destination destination = session.createQueue(destinationName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(listener);
    }

    /**
     * Sends a text message to a specific destination (queue or topic).
     * Creates a producer for the destination and sends the message.
     * 
     * @param destinationName The name of the queue or topic to send to
     * @param message The text message to send
     * @throws JMSException if there's an error creating the producer or sending the message
     */
    public void sendMessage(String destinationName, String message) throws JMSException {
        Destination destination = session.createQueue(destinationName);
        MessageProducer producer = session.createProducer(destination);
        TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);
    }
} 
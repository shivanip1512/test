package com.cannontech.cloud.publisher;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {
    @PostConstruct
    public static void startPublish() {
        Connection connection = null;
        try {
            // Producer
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "tcp://localhost:61616");
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("cloudTopic");
            connection.start();

            // Publish
            for (int i = 0; i < 10; i++) {
                String payload = null;
                payload = "Test Message From Yukon Publisher " + i;
                Message msg = session.createTextMessage(payload);
                MessageProducer producer = session.createProducer(topic);
                System.out.println("Sending text '" + payload + "'");
                producer.send(msg);

                // Send message every 20 sec
                Thread.sleep(20000);
            }
            session.close();
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

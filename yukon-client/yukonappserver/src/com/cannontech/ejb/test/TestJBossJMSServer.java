package com.cannontech.ejb.test;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.jms.MessageListener;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.jms.Topic;
import javax.jms.TextMessage;
import javax.jms.Session;
import javax.jms.JMSException;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestJBossJMSServer {


  /**
   * Topic connection, hold on to this so you may close it.
   */
  TopicConnection topicConnection;

  /**
   * Topic session, hold on to this so you may close it.
   * Also used to create messages.
   */
  TopicSession topicSession;

  /**
   * Use this to publish messages.
   */
  TopicPublisher topicPublisher;

  /**
   * Destination where to publish.
   */
  Topic topic;


  /**
   * Sets up all the JMS fixtures.
   *
   * Use close() when finished with object.
   *
   * @param factoryJNDI name of the topic connection factory to look up.
   * @param topicJNDI name of the topic destination to look up
   */
  public TestJBossJMSServer(String factoryJNDI, String topicJNDI)
    throws JMSException, NamingException {

    // Get the initial context
    Context context = new InitialContext();

    // Get the connection factory
    TopicConnectionFactory topicFactory =
      (TopicConnectionFactory)context.lookup(factoryJNDI);

    // Create the connection
    topicConnection = topicFactory.createTopicConnection();

    // Create the session
    topicSession = topicConnection.createTopicSession(
      // No transaction
      false,
      // Auto ack
      Session.AUTO_ACKNOWLEDGE);

    // Look up the destination
    topic = (Topic)context.lookup(topicJNDI);

    // Create a publisher
    topicPublisher = topicSession.createPublisher(topic);

  }


	public static void main(String[] args) {
		
		
    try {

      // Create the HelloPublisher, giving it the name of the
      // TopicConnection Factory and the Topic destination to
      // use in lookup.
      TestJBossJMSServer publisher = new TestJBossJMSServer(
        // Name of ConnectionFactory
        "ConnectionFactory",
        // Name of destination to publish to
        "topic/testTopic");

      // Publish 10 messages
      for (int i = 1; i < 11; i++) {

        String msg = "Hello World no. " + i;
        System.out.println("Publishing message: " + msg);
        publisher.publish(msg);

      }

      // Close down your publisher
      publisher.close();

    } catch(Exception ex) {

      System.err.println(
        "An exception occurred while testing TestJBossJMSServer: " + ex);
      ex.printStackTrace();

    }
		
	}





  /**
   * Publish the given String as a JMS message to the testTopic topic.
   */
  public void publish(String msg) throws JMSException {

    // Create a message
    TextMessage message = topicSession.createTextMessage();
    message.setText(msg);

    // Publish the message
    topicPublisher.publish(topic, message);

  }

  /**
   * Close session and connection.
   * When done, no publishing is possible any more.
   */
  public void close() throws JMSException {

    topicSession.close();
    topicConnection.close();

  }


}

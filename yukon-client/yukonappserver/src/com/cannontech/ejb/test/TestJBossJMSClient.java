package com.cannontech.ejb.test;

import javax.jms.MessageListener;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.Topic;
import javax.jms.Message;
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
public class TestJBossJMSClient implements MessageListener {


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
   * Subscriber
   */
  TopicSubscriber topicSubscriber;

  /**
   * Destination to subscribe to
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
  public TestJBossJMSClient(String factoryJNDI, String topicJNDI)
    throws JMSException, NamingException
  {

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

    // Create a subscriber
    topicSubscriber = topicSession.createSubscriber(topic);

    // Set the message listener, which is this class since we implement
    // the MessageListener interface
    topicSubscriber.setMessageListener(this);

    System.out.println(
      "TestJBossJMSClient subscribed to topic: " + topicJNDI);

    // OBS! For the message listener to receive any messages the
    // connection has to be started
    topicConnection.start();
  }


  /**
   * Implementation of the MessageListener interface,
   * messages will be received through this method.
   */
  public void onMessage(Message m) {

    // Unpack the message, be careful when casting to the correct
    // message type. onMessage should not throw any application
    // exceptions.
    try {

      String msg = ((TextMessage)m).getText();
      System.out.println("TestJBossJMSClient got message: " + msg);

    } catch(JMSException ex) {

      System.err.println("Could not get text message: " + ex);
      ex.printStackTrace();

    }

  }


  /**
   * Close session and connection.
   */
  public void close() throws JMSException {

    topicSession.close();
    topicConnection.close();

  }


	public static void main(String[] args) {

    try {

      // Create the HelloSubscriber, giving it the name of the
      // TopicConnection Factory and the Topic destination to use in
      // lookup.
      TestJBossJMSClient subscriber = new TestJBossJMSClient(
        // Name of ConnectionFactory
        "TopicConnectionFactory",
        // Name of destination to publish to
        "topic/testTopic");

    } catch(Exception ex) {

      System.err.println(
        "An exception occurred while testing HelloSubscriber: " + ex);
      ex.printStackTrace();

    }
	}




}

package com.cannontech.sensus;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.log4j.Logger;


public class SensusServer {
    private static Logger log = Logger.getLogger(SensusServer.class);
    private Object errorWaiter = new Object();
    private MessageListener messageListener;
    
    private Destination destination;
    private ConnectionFactory factory;
    private String messageSelector = null;
    
    public void startServer() {
        try {
            while (true) {
                try {
                    final Connection con = factory.createConnection();
                    con.setExceptionListener(new ExceptionListener() {
                        public void onException(JMSException e) {
                            log.warn("Async JMS Exception, kicking main loop", e);
                            synchronized (errorWaiter) {
                                errorWaiter.notifyAll();
                            }
                        }
                    });
                    Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    MessageConsumer consumer = session.createConsumer(destination, messageSelector);
                    consumer.setMessageListener(messageListener);
                    con.start();
                    log.info("JMS connection started");
                    synchronized (errorWaiter) {
                        errorWaiter.wait();
                        log.info("Main loop has awoken!");
                        con.close();
                    }
                } catch (JMSException e) {
                    log.error("Caught exception durring startup.", e);
                }
                log.info("Sleeping for 15 seconds");
                Thread.sleep(15000);
            }
        } catch (InterruptedException e) {
            log.error("Got interrupted, closing", e);
        }
    }


    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public ConnectionFactory getFactory() {
        return factory;
    }

    public void setFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
    
    public void setMessageSelector(String messageSelector) {
        this.messageSelector = messageSelector;
    }
}

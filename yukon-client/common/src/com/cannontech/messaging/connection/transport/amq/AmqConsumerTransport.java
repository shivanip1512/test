package com.cannontech.messaging.connection.transport.amq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.command.ActiveMQDestination;

import com.cannontech.messaging.connection.transport.TransportException;

public class AmqConsumerTransport extends AmqDestinationTransport {

    private ActiveMQMessageConsumer consumer;
    private String selector;

    public AmqConsumerTransport(ActiveMQConnection connection, ActiveMQDestination destination, MessageListener listener) {
        super(connection, destination);
        setListener(listener);
    }

    @Override
    public void start() {
        super.start();
        if (consumer == null) {
            try {
                consumer =
                    (ActiveMQMessageConsumer) getSession().createConsumer(getDestination(), getSelector(),
                                                                          getListener());
            }
            catch (JMSException e) {
                throw new TransportException("Unable to start Comsumer Transport", e);
            }
        }
    }

    @Override
    public void close() {
        if (consumer != null) {
            try {
                consumer.close();
            }
            catch (JMSException e) {
                // TODO log it but don't throw as we don't want to interrupt the closing/disconnecting operation
                // throw new TransportException("Unable to close Consumer Transport", e);
            }
            finally {
                consumer = null;
            }
        }
        super.close();
    }

    @Override
    public Message recieveMessage() {
        return receiveMessage(Long.MAX_VALUE);
    }

    @Override
    public Message receiveMessage(long timeout) {
        try {
            return consumer.receive(timeout);
        }
        catch (JMSException e) {
            throw new TransportException("Error while receiving a message on Consumer Transport", e);
        }
    }

    @Override
    public void setListener(MessageListener listener) {
        super.setListener(listener);

        if (consumer != null) {
            try {
                consumer.setMessageListener(listener);
            }
            catch (JMSException e) {
                throw new TransportException("Unable to set the message listnener on the Consumer Transport", e);
            }
        }
    }

    public ActiveMQMessageConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(ActiveMQMessageConsumer consumer) {
        checkStarted("Can not change consumer after transport has been started");
        this.consumer = consumer;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selectorString) {
        this.selector = selectorString;
    }
}

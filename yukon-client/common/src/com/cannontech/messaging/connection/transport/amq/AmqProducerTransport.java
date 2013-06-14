package com.cannontech.messaging.connection.transport.amq;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.command.ActiveMQDestination;

import com.cannontech.messaging.connection.transport.TransportException;

public class AmqProducerTransport extends AmqDestinationTransport {

    private ActiveMQMessageProducer producer;

    public AmqProducerTransport(ActiveMQConnection connection, ActiveMQDestination destination) {
        super(connection, destination);
    }

    @Override
    public void start() {
        super.start();
        if (producer == null) {
            try {
                producer = (ActiveMQMessageProducer) getSession().createProducer(getDestination());
            }
            catch (JMSException e) {
                throw new TransportException("Unable to start Producer Transport", e);
            }
        }
    }

    @Override
    public void close() {
        if (producer != null) {
            try {
                producer.close();
            }
            catch (JMSException e) {
                // Log it but don't throw as we don't want to interrupt the closing/disconnecting operation
                // throw new TransportException("Unable to close Producer Transport", e);
                logger.error("Unable to close an AMQ producer", e);
                
            }
            finally {
                producer = null;
            }
        }

        super.close();
    }

    @Override
    public void sendMessage(Message message) {
        try {
            producer.send(message);
        }
        catch (JMSException e) {
            throw new TransportException("Error while sending a message on Producer Transport", e);
        }
    }

    public ActiveMQMessageProducer getProducer() {
        return producer;
    }

    protected void setProducer(ActiveMQMessageProducer producer) {
        checkStarted("Can not change producer after transport has been started");
        this.producer = producer;
    }
}

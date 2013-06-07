package com.cannontech.messaging.connection.transport.amq;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;

public class TwoWayTransport extends AmqTransport {

    private ActiveMQDestination outQueue;
    private ActiveMQDestination inQueue;

    private AmqProducerTransport outTransport;
    private AmqConsumerTransport inTransport;

    public TwoWayTransport(ActiveMQConnection connection, ActiveMQDestination outQueue, ActiveMQDestination inQueue,
                           MessageListener listener) {
        super(connection);
        setListener(listener);
        this.outQueue = outQueue;
        this.inQueue = inQueue;
    }

    public TwoWayTransport(AmqProducerTransport outTransport, AmqConsumerTransport inTransport) {
        super(outTransport.getConnection());

        setOutTransport(outTransport);
        setSession(this.outTransport.getSession());

        setInTransport(inTransport);
        this.setListener(this.inTransport.getListener());
    }

    @Override
    public void start() {
        super.start(); // Creates a session
        ActiveMQConnection connection = getConnection();

        if (outTransport == null) {
            outTransport = new AmqProducerTransport(connection, outQueue);
            // Use this transport session for the output transport because this is the session that
            // will be used to
            // create outgoing messages
            outTransport.setSession(getSession());
            getOutTransport().start();
        }

        if (inTransport == null) {
            inTransport = new AmqConsumerTransport(connection, inQueue, getListener());
            inTransport.setManagedDestination(true);
            getInTransport().start();
        }
    }

    @Override
    public void close() {
        if (outTransport != null) {
            outTransport.close();
            outTransport = null;
        }

        if (inTransport != null) {
            inTransport.close();
            inTransport = null;
        }

        super.close();
    }

    public void emptyConsumerMessage() {
        MessageListener listener = getListener();
        setListener(null);
        if (listener != null) {
            Message msg;
            while ((msg = receiveMessage(100)) != null) {
                listener.onMessage(msg);
            }
        }
    }

    public ActiveMQDestination getOutQueue() {
        return outQueue;
    }

    public void setOutQueue(ActiveMQDestination outQueue) {
        this.outQueue = outQueue;
    }

    public ActiveMQDestination getInQueue() {
        return inQueue;
    }

    public void setInQueue(ActiveMQDestination inQueue) {
        this.inQueue = inQueue;
    }

    @Override
    public void sendMessage(Message message) {
        getOutTransport().sendMessage(message);
    }

    @Override
    public Message recieveMessage() {
        return getInTransport().recieveMessage();
    }

    @Override
    public Message receiveMessage(long timeout) {
        return getInTransport().receiveMessage(timeout);
    }

    public AmqProducerTransport getOutTransport() {
        return outTransport;
    }

    public void setOutTransport(AmqProducerTransport outTransport) {
        this.outTransport = outTransport;
        if (this.outTransport != null) {
            setOutQueue(this.outTransport.getDestination());
        }
    }

    public AmqConsumerTransport getInTransport() {
        return inTransport;
    }

    public void setInTransport(AmqConsumerTransport inTransport) {
        this.inTransport = inTransport;
        if (this.inTransport != null) {
            this.setInQueue(this.inTransport.getDestination());
        }
    }

    @Override
    public void setListener(MessageListener listener) {
        super.setListener(listener);

        if (inTransport != null) {
            inTransport.setListener(listener);
        }
    }
}

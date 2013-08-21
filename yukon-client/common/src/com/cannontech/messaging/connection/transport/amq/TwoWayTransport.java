package com.cannontech.messaging.connection.transport.amq;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;

import com.cannontech.messaging.connection.transport.TransportException;

public class TwoWayTransport extends AmqTransport {

    private static final long MAX_READ_TIME_AFTER_CLOSE = 1000; // 1s
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
            outTransport.start();
        }

        if (inTransport == null) {
            inTransport = new AmqConsumerTransport(connection, inQueue, getListener());
            inTransport.setManagedDestination(true);
            inTransport.start();
        }
    }

    @Override
    public void close() {
        if (outTransport != null) {
            outTransport.close();
            outTransport = null;
        }

        if (inTransport != null) {
            emptyConsumerMessageWithSecondConsumer();
            // emptyConsumerMessage();
            inTransport.close();
            inTransport = null;
        }

        super.close();
    }

    /**
     * This method is willingly unused. See emptyConsumerMessageWithSecondConsumer for details.
     */
    @SuppressWarnings("unused")
    private void emptyConsumerMessage() {
        MessageListener listener = getListener();
        setListener(null);

        if (listener != null) {
            long currentTime = System.currentTimeMillis();
            long endTime = currentTime + MAX_READ_TIME_AFTER_CLOSE;

            while (currentTime < endTime) {
                Message msg = receiveMessage(endTime - currentTime);

                if (msg == null) {
                    break;
                }

                listener.onMessage(msg);
                currentTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * We use this method instead of emptyConsumerMessage because of a potential bug in ActiveMQ regarding the
     * producerCount never returning to zero. This method will simply create another consumer to empty the receiving
     * queue. It will react exactly the same way from the point of view of this client connection. On the other end of
     * the connection however, consumer count will reach 2 thus forcing the disconnection process (witch is ultimately what
     * we want).
     */
    private void emptyConsumerMessageWithSecondConsumer() {
        MessageListener listener = getListener();
        setListener(null);

        try {
            AmqConsumerTransport tempInTransport = new AmqConsumerTransport(getConnection(), inQueue, listener);
            tempInTransport.start();

            if (listener != null) {
                long currentTime = System.currentTimeMillis();
                long endTime = currentTime + MAX_READ_TIME_AFTER_CLOSE;

                while (currentTime < endTime) {
                    Message msg = tempInTransport.receiveMessage(endTime - currentTime);

                    if (msg == null) {
                        break;
                    }

                    listener.onMessage(msg);
                    currentTime = System.currentTimeMillis();
                }
            }

            tempInTransport.close();
        }
        catch (TransportException e) {}

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
    public Message receiveMessage() {
        return getInTransport().receiveMessage();
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

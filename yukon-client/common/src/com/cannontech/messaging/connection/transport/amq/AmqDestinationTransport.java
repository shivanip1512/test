package com.cannontech.messaging.connection.transport.amq;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQDestination;

import com.cannontech.messaging.connection.transport.TransportException;

public class AmqDestinationTransport extends AmqTransport {

    private ActiveMQDestination destination;
    private boolean managedDestination = false; // Do not create or destroy the destination explicitly

    public AmqDestinationTransport(ActiveMQConnection connection, ActiveMQDestination destination) {
        super(connection);
        setDestination(destination);
    }

    @Override
    public void start() {
        super.start();
        if (isManagedDestination()) {
            createDestination();
        }
    }

    @Override
    public void close() {
        if (isManagedDestination() && destination.isTemporary()) {
            try {
                if (destination instanceof TemporaryQueue) {
                    ((TemporaryQueue) destination).delete();
                }
                else if (destination instanceof TemporaryTopic) {
                    ((TemporaryTopic) destination).delete();
                }
            }
            catch (JMSException e) {
                // Log it but don't throw
                // throw new TransportException("Unable to delete the temporary queue \"" +
                // destination.getPhysicalName() + "\"", e);
                logger.debug("Unable de delete the temporary queue \"" + destination.getPhysicalName() +
                             "\" on a AMQ DestinationTransport ", e);
            }
        }

        super.close();
    }

    protected void createDestination() {
        ActiveMQSession session = getSession();

        try {
            if (destination == null) {
                destination = (ActiveMQDestination) session.createTemporaryQueue();
            }
            else if (destination instanceof Queue) {
                session.createQueue(destination.getPhysicalName());
            }
            else if (destination instanceof Topic) {
                session.createTopic(destination.getPhysicalName());
            }
            else if (destination instanceof TemporaryQueue) {
                destination = (ActiveMQDestination) session.createTemporaryQueue();
            }
            else if (destination instanceof TemporaryTopic) {
                destination = (ActiveMQDestination) session.createTemporaryTopic();
            }
        }
        catch (JMSException e) {
            throw new TransportException("Unable to create destination \"" + destination == null ? ""
                : destination.getPhysicalName() + "\"", e);
        }
    }

    public ActiveMQDestination getDestination() {
        return destination;
    }

    protected void setDestination(ActiveMQDestination destination) {
        checkStarted("Can not change destination after transport has been started");

        this.destination = destination;
    }

    public boolean isManagedDestination() {
        return managedDestination;
    }

    public void setManagedDestination(boolean managedDestination) {
        this.managedDestination = managedDestination;
    }
}

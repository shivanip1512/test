package com.cannontech.messaging.connection.amq;

import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;

import com.cannontech.messaging.connection.MessagingConnectionException;
import com.cannontech.messaging.connection.transport.amq.TwoWayTransport;

public class AmqServerConnection extends AmqConnectionBase<TwoWayTransport> {

    private ActiveMQDestination destination;

    AmqServerConnection(ActiveMQConnection connection, ActiveMQDestination destination) {
        super();
        setConnection(connection);
        setDestination(destination);
    }

    @Override
    protected ActiveMQConnection createConnection() {
        throw new MessagingConnectionException(
                                      "A <ServerConnection> object should never attempt to create an ActiveMQ connection to a broker by itself");
    }

    @Override
    protected TwoWayTransport createTransport() {
        return HandShakeConnector.createServerConnectionTransport(this);
    }

    @Override
    protected AmqConnectionMonitor createConnectionMonitor(TwoWayTransport transport, MessageListener listener) {
        AmqConnectionMonitor monitor =
            new AmqConnectionMonitor(transport.getInTransport(), transport.getOutTransport(), listener);
        return monitor;
    }

    protected ActiveMQDestination getDestination() {
        return destination;
    }

    protected void setDestination(ActiveMQDestination destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return super.toString() + " (server side)";
    }
}

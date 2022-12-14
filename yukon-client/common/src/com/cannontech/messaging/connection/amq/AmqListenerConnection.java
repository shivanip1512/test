package com.cannontech.messaging.connection.amq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.event.Event;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.InboundConnectionEvent;
import com.cannontech.messaging.connection.event.InboundConnectionEventHandler;
import com.cannontech.messaging.connection.transport.amq.AmqConsumerTransport;
import com.cannontech.services.jms.InternalMessagingConnectionFactory;

public class AmqListenerConnection extends AmqConnectionBase<AmqConsumerTransport> implements ListenerConnection,
    ConnectionEventHandler {

    private static final long REQUEST_EXPIRE_TIME_SECONDS = 30; // filter duplicate request younger then this
    
    protected final InboundConnectionEvent inboundConnectionEvent;
    private final List<AmqServerConnection> serverConnectionList;
    private final Map<Destination, Instant> requestTimeMap;

    public AmqListenerConnection(String name, String queueName, InternalMessagingConnectionFactory internalMessagingConnectionFactory) {
        super(name, queueName, internalMessagingConnectionFactory);
        setManagedConnection(true);
        inboundConnectionEvent = new InboundConnectionEvent();
        serverConnectionList = new LinkedList<AmqServerConnection>();
        requestTimeMap = new HashMap<Destination, Instant>();
    }

    @Override
    protected AmqConsumerTransport createTransport() {
        return new AmqConsumerTransport(getConnection(), new ActiveMQQueue(getQueueName()), this);
    }

    @Override
    protected AmqConnectionMonitor createConnectionMonitor(AmqConsumerTransport transport, MessageListener listener) {
        return null;
    }

    /**
     * Message received handler method. Incoming messages represent connection requests sent by clients that want to
     * connect the this Server. This method will create a serverConnection binding the requesting client and will fire
     * the inboundConnectionEvent. The created serverConnection is automatically started.
     */
    @Override
    public void onMessage(Message message) {
        try {

            if (message == null || !HandShakeConnector.HAND_SHAKE_REQ_MSG_TYPE.equals(message.getJMSType()) || message.getJMSReplyTo() == null) {
                return;
            }

            final Instant now = new Instant();
            final Instant expired = now.minus( Duration.standardSeconds( REQUEST_EXPIRE_TIME_SECONDS ));

            // clean up expired
            Iterator<Map.Entry<Destination, Instant>> iterator = requestTimeMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Destination, Instant> entry = iterator.next();
                if (!entry.getValue().isAfter(expired)) {
                    iterator.remove();
                }
            }

            // filter duplicate
            if (requestTimeMap.containsKey(message.getJMSReplyTo())) {
                return;
            }

            requestTimeMap.put(message.getJMSReplyTo(), now);

            // Incoming connection request received

            AmqServerConnection serverConnection = null;
            synchronized (serverConnectionList) {
                if (isDisconnectRequested()) {
                    // We can NOT accept incoming connection request as this listener is being closed;
                    return;
                }

                // Create a corresponding serverConnection
                serverConnection =
                    new AmqServerConnection(getConnection(), (ActiveMQDestination) message.getJMSReplyTo());
                serverConnection.getConnectionEvent().registerHandler(this);
                serverConnection.setMessageFactory(getMessageFactory());

                // Add it the the list of managed connections
                serverConnectionList.add(serverConnection);
            }

            inboundConnectionEvent.fire(this, serverConnection);

            serverConnection.start();
        }
        catch (JMSException e) {
            safeDisconnect(e);
        }
    }

    /**
     * disconnects this ListenerConnection as well as all ClientConnection it has created
     */
    @Override
    protected void disconnect() {
        synchronized (serverConnectionList) {
            for (AmqServerConnection client : serverConnectionList) {
                try {
                    client.getConnectionEvent().unregisterHandler(this);
                    client.close();
                }
                catch (Exception e) {
                    logger.debug("Unable to close a server side connection", e);
                }
            }
            serverConnectionList.clear();
        }
        super.disconnect();
    }

    @Override
    public Event<InboundConnectionEventHandler> getInboundConnectionEvent() {
        return inboundConnectionEvent.getEventInterface();
    }

    /**
     * this ConnectionEventHandler method removes from the list a ClientConnection object created by this
     * ListenerConnection when it becomes unusable
     */
    @Override
    public void onConnectionEvent(Connection source, ConnectionState state) {
        if (state == ConnectionState.Closed || state == ConnectionState.Error) {
            synchronized (serverConnectionList) {
                source.getConnectionEvent().unregisterHandler(this);
                serverConnectionList.remove(source);
            }
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " (listener)";
    }
}

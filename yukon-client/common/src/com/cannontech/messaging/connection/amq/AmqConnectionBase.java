package com.cannontech.messaging.connection.amq;

import javax.jms.BytesMessage;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang.SerializationException;

import com.cannontech.messaging.connection.transport.TransportException;
import com.cannontech.messaging.connection.transport.amq.AmqConsumerTransport;
import com.cannontech.messaging.connection.transport.amq.AmqTransport;
import com.cannontech.messaging.connection.ConnectionBase;
import com.cannontech.messaging.connection.ConnectionException;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.serialization.SerializationResult;

public abstract class AmqConnectionBase<T extends AmqTransport> extends ConnectionBase<T> implements MessageListener,
    ExceptionListener {
    private final MessageListener advisoryListener;
    private AmqConsumerTransport monitorTransport;

    private String queueName;
    private ActiveMQConnection connection;
    private boolean managedConnection = false;

    private AmqConnectionFactoryService connectionService;
    private MessageFactory messageFactory;

    private static int conID = 0;

    protected AmqConnectionBase(String name) {
        super(name);

        advisoryListener = new MessageListener() {
            @Override
            public void onMessage(javax.jms.Message msg) {
                onAdvisoryMessage(msg);
            }
        };
    }

    protected AmqConnectionBase() {
        this("AMQConn_" + conID++);
    }

    protected AmqConnectionBase(String name, String queueName) {
        this(name);
        setQueueName(queueName);
    }

    protected abstract T createTransport() throws TransportException;

    protected abstract AmqConsumerTransport createMonitorTransport(T transport, MessageListener listener);

    protected ActiveMQConnection createConnection() throws ConnectionException {
        try {
            return getConnectionService().createConnection();
        }
        catch (JMSException e) {
            throw new ConnectionException("Unable de creation a connection to the ActiveMQ broker", e);
        }
    }

    @Override
    protected void connect() {
        try {
            // Create the connection to the AMQ broker (if needed)
            if (isManagedConnection()) {
                if (connection == null) {
                    connection = createConnection();
                    connection.setExceptionListener(this);
                    connection.start();
                }
            }

            // Create and start the transport layer (if needed)
            T transport = getTransport();
            if (transport == null) {
                transport = createTransport();
                setTransport(transport);
                transport.setListener(this);
                transport.start();
            }
            else {
                transport.setListener(this);
            }

            // Create and start the monitor transport (if needed)
            monitorTransport = createMonitorTransport(transport, advisoryListener);
            if (monitorTransport != null) {
                monitorTransport.start();
            }
        }
        catch (JMSException e) {
            throw new ConnectionException("Error while starting an ActiveMQ connection", e);
        }

        setState(ConnectionState.Connected);
    }

    @Override
    protected void disconnect() {
        super.disconnect();
        try {
            if (monitorTransport != null) {
                monitorTransport.close();
            }

            if (isManagedConnection() && connection != null) {
                connection.close();
            }
        }
        catch (JMSException e) {
            // log it but don't throw as we don't want to interrupt the closing/disconnecting operation as we don't want
            // to interrupt the closing/disconnecting operation
            // throw new ConnectionException("Error while disconnecting from the ActiveMQ broker", e);
            logger.error("Error while disconnecting from the ActiveMQ broker", e);
        }
        finally {
            connection = null;
            monitorTransport = null;
        }
    }

    @Override
    public void sendMessage(BaseMessage message) throws ConnectionException {
        T transport = getTransport();

        if (transport == null) {
            throw new ConnectionException("Impossible to send messge because transport is not initialized");
        }

        BytesMessage msg;
        try {
            SerializationResult result = getMessageFactory().encodeMessage(message);

            if (!result.isValid()) {
                // We treat serialization error as exception. And we break the current connection.
                throw new SerializationException("Serialization error, message type= " + result.getMessageType() +
                                                 ", message class=" + result.getMessageClass(), result.getException());
            }

            msg = transport.getSession().createBytesMessage();
            msg.writeBytes(result.getMessagePayload());
            msg.setJMSType(result.getMessageType());
            transport.sendMessage(msg);
        }
        catch (Exception e) {
            throw new ConnectionException("Error while sending message", e);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            byte[] data;
            BytesMessage byteMsg = (BytesMessage) message;
            data = new byte[(int) byteMsg.getBodyLength()];
            byteMsg.readBytes(data);

            SerializationResult result = getMessageFactory().decodeMessage(byteMsg.getJMSType(), data);
         
            if (!result.isValid()) {
                Exception e = result.getException();
                if (e == null) {
                    e = new SerializationException("Unable to deserialize message of type '" + result.getMessageType() +"'");
                }
                safeDisconnect(e);
                return;
            }

            messageEvent.fire(this, (BaseMessage) result.getMessageObject());
        }
        catch (Exception e) {
            safeDisconnect(e);
        }
    }

    protected ActiveMQTopic getAdvisoryTopicName(ActiveMQDestination destination) {
        return AdvisorySupport.getConsumerAdvisoryTopic(destination);
    }

    /**
     * Handles Advisory notifications. It is called when the peer on the other side of this connection has been
     * disconnected
     * @param message Advisory message notifying us of a consumer being disconnect
     */
    public void onAdvisoryMessage(javax.jms.Message message) {
        if (message instanceof ActiveMQMessage) {
            try {
                ActiveMQMessage aMsg = (ActiveMQMessage) message;

                if (!new Integer(1).equals(aMsg.getProperty("consumerCount"))) {
                    monitorTransport.setListener(null); // we don't want to be notified anymore
                    safeDisconnect();
                    return;
                }
            }
            catch (Exception e) {
                safeDisconnect();
            }
        }
    }

    @Override
    public void onException(JMSException e) {
        safeDisconnect(e);
    }

    public ActiveMQConnection getConnection() {
        return connection;
    }

    protected void setConnection(ActiveMQConnection connection) {
        this.connection = connection;
    }

    public AmqConnectionFactoryService getConnectionService() {
        if (connectionService == null) {
            connectionService = AmqConnectionFactoryService.getDefaultService();
        }
        return connectionService;
    }

    public void setConnectionService(AmqConnectionFactoryService connectionSvc) {
        this.connectionService = connectionSvc;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    private boolean isManagedConnection() {
        return managedConnection;
    }

    protected void setManagedConnection(boolean managedConnection) {
        this.managedConnection = managedConnection;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public String toString() {
        return "AMQ p2p connection";
    }
}

package com.cannontech.messaging.connection.amq;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.BytesMessage;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;

import com.cannontech.message.util.Message;
import com.cannontech.messaging.connection.ConnectionBase;
import com.cannontech.messaging.connection.MessagingConnectionException;
import com.cannontech.messaging.connection.transport.TransportException;
import com.cannontech.messaging.connection.transport.amq.AmqTransport;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.serialization.SerializationException;
import com.cannontech.messaging.serialization.SerializationResult;

public abstract class AmqConnectionBase<T extends AmqTransport> extends ConnectionBase<T> implements MessageListener,
    ExceptionListener {
    private final MessageListener advisoryListener;
    private AmqConnectionMonitor connectionMonitor;

    private String queueName;
    private ActiveMQConnection connection;
    private boolean managedConnection = false;

    private AmqConnectionFactoryService connectionService;
    private MessageFactory messageFactory;

    private static AtomicInteger conID = new AtomicInteger(0);

    private static final long RECONNECT_DELAY_INITIAL_MILLIS = 1000;
    private static final long RECONNECT_DELAY_MAX_MILLIS     = 30000;
    private static final int  RECONNECT_ATTEMPT_MAX          = 120;
    private static final int  RECONNECT_LOGGING_FREQ         = 10; // number of attempt
    
    private long   reconnectDelayMillis = RECONNECT_DELAY_INITIAL_MILLIS;
    private String reconnectLastError   = "";
    
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
        this("AMQConn_" + conID.getAndIncrement());
    }

    protected AmqConnectionBase(String name, String queueName) {
        this(name);
        setQueueName(queueName);
    }

    protected abstract T createTransport() throws TransportException;

    protected abstract AmqConnectionMonitor createConnectionMonitor(T transport, MessageListener listener);

    protected ActiveMQConnection createConnection() throws MessagingConnectionException, InterruptedException {
        try {
            return getConnectionService().createConnection();
        }
        catch (JMSException e) {
            throw new MessagingConnectionException("Unable to create a connection to the ActiveMQ broker", e);
        }
    }

    @Override
    protected void connect() {
        try {
            // Create the connection to the AMQ broker (if needed)
            if (isManagedConnection()) {

                if (connection == null) {
                    final int maxAttempts = (isConnectionFailed()) ? RECONNECT_ATTEMPT_MAX : 1;
                    
                    int     attempt = 0;
                    boolean started = false;

                    while (! started) {
                        attempt++;
                        try {
                            connection = getConnectionService().createConnection();
                            connection.setExceptionListener(this);
                            connection.start();
                            started = true;
                            logger.info("Connection to "+getName()+ " via "+connection.toString());
                        }
                        catch (JMSException e) {
                            connection = null;
                            
                            if (attempt % RECONNECT_LOGGING_FREQ == 1 || ! reconnectLastError.equals(e.getMessage())) {
                                logger.error(e.getMessage());
                                reconnectLastError = e.getMessage();
                            }
                            
                            if (attempt == maxAttempts) {
                                if (! isConnectionFailed()) {
                                    disableWorkerThreadLogError(); // disable logging for the first broker connection error
                                }
                                throw e;
                            }
                        }

                        if (started) {
                            reconnectDelayMillis = RECONNECT_DELAY_INITIAL_MILLIS;
                            reconnectLastError   = "";
                        }
                        else {
                            Thread.sleep(reconnectDelayMillis);
                            reconnectDelayMillis = Math.min(2*reconnectDelayMillis, RECONNECT_DELAY_MAX_MILLIS); // using exponential backoff
                        }
                    }
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
        }
        catch (JMSException e) {            
            throw new MessagingConnectionException("Error while starting an ActiveMQ connection", e);
        }
        catch (InterruptedException e) {
            Thread.interrupted();
            throw new MessagingConnectionException("Interrupted while waiting to retry connecting", e);
        }

        setState(ConnectionState.Connected);
    }

    @Override
    protected void disconnect() {
        try {
            if (connectionMonitor != null) {
                connectionMonitor.close();
            }

            super.disconnect();

            if (isManagedConnection() && connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (JMSException e) {
            // log it but don't throw as we don't want to interrupt the closing/disconnecting operation as we don't want
            // to interrupt the closing/disconnecting operation
            // throw new ConnectionException("Error while disconnecting from the ActiveMQ broker", e);
            logger.error("Error while disconnecting from the ActiveMQ broker", e);
        }
        finally {
            connection = null;
            connectionMonitor = null;
        }
    }

    @Override
    public void sendMessageToTransport(Message message) throws MessagingConnectionException {
        T transport = getTransport();

        if (transport == null) {
            throw new MessagingConnectionException("Cannot send message, transport not initialized");
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
            throw new MessagingConnectionException("Error while sending message", e);
        }
    }

    @Override
    public void onMessage(javax.jms.Message message) {
        try {
            BytesMessage byteMsg = (BytesMessage) message;
            byte[] data = new byte[(int) byteMsg.getBodyLength()];
            byteMsg.readBytes(data);

            SerializationResult result = getMessageFactory().decodeMessage(byteMsg.getJMSType(), data);

            if (!result.isValid()) {
                Exception exception = result.getException();
                if (exception == null) {
                    exception = new SerializationException("Unable to deserialize message of type '"
                        + result.getMessageType() + "'");
                }
                safeDisconnect(exception);
                return;
            }

            messageEvent.fire(this, (Message) result.getMessageObject());
        }
        catch (Exception e) {
            safeDisconnect(e);
        }
    }

    /**
     * Handles Advisory notifications. It is called when the peer on the other side of this connection has been
     * disconnected
     * @param message Advisory message notifying us of a consumer being disconnect
     */
    public void onAdvisoryMessage(javax.jms.Message message) {
        if (connectionMonitor != null) {
            connectionMonitor.setListener(null); // We don't want to be notified anymore            
        }
        safeDisconnect();
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

    /**
     * @return The Connection URI (the broker URI since we are physically connected to the broker)
     * @throws Exception ConnectionException if an error occur
     */
    @Override
    public URI getConnectionUri() throws MessagingConnectionException {
        try {
            if (isManagedConnection()) {
                return URI.create(getConnectionService().getBrokerUrl());
            }
            return URI.create(connection.getBrokerInfo().getBrokerURL());
        }
        catch (Exception e) {
            throw new MessagingConnectionException("Error while retrieving broker URI");
        }
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
        return "'" + getName() + "' AMQ p2p connection";
    }
    
    public void setupConnectionMonitor(T transport) {
        // Create and start the monitor transport (if needed)
        connectionMonitor = createConnectionMonitor(transport, advisoryListener);
        if (connectionMonitor != null) {
            connectionMonitor.start();
        }
    }
}

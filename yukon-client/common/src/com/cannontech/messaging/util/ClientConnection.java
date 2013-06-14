package com.cannontech.messaging.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import sun.audio.ContinuousAudioDataStream;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.util.CommonCollectableMappings;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.Connection.ConnectionState;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.MessageEventHandler;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.CommandMessage;
import com.cannontech.messaging.message.ConnStateChangeMessage;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.message.porter.ReturnMessage;
import com.cannontech.yukon.IServerConnection;
import com.google.common.collect.Lists;
import com.roguewave.vsj.DefineCollectable;

@ManagedResource
public abstract class ClientConnection extends java.util.Observable implements IServerConnection {

    private Connection connection;
    private ConnectionFactory connectionFactory;

    // Keep track of all of this connections MessageListeners
    private List<MessageListener> messageListeners = new CopyOnWriteArrayList<MessageListener>();
    private final String connectionName;
    private AtomicLong totalSentMessages = new AtomicLong();
    private AtomicLong totalReceivedMessages = new AtomicLong();
    private AtomicLong totalFiredEvents = new AtomicLong();
    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());

    // This message will be sent automatically on connecting
    private BaseMessage registrationMsg = null;

    private Queue<BaseMessage> inQueue = new LinkedList<BaseMessage>();
    private PriorityBlockingQueue<BaseMessage> outQueue =
        new PriorityBlockingQueue<BaseMessage>(100, new MessagePriorityComparable());

    private String host = "127.0.0.1";
    private int port;

    private boolean isValid = false;
    private boolean autoReconnect = false;
    private boolean queueMessages = false; // if true, be sure you are reading from the inQueue!!
    private boolean disconnected = false;

    protected ClientConnection(String connectionName) {
        super();
        this.connectionName = connectionName;
        autoReconnect = true;
        connectionFactory = ConnectionFactoryService.getInstance().findConnectionFactory(connectionName);
    }

    /**
     * Create a connection with a given server side connection. Used for Java servers.
     */
    protected ClientConnection(Connection connection) {
        this("Auto from " + connection);

        this.connection = connection;
        this.connection.setName(connectionName);
        this.connection.getMessageEvent().registerHandler(eventHandler);
        this.connection.getConnectionEvent().registerHandler(eventHandler);
    }

    private final void cleanUp() {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public final void connect() throws java.io.IOException {
        // wait a long time to timeout
        // for the freaks out there: 106,751,991,167 days (292,471,208 years)
        connect(Long.MAX_VALUE);
    }

    private final void connect(long millis) {
        try {
            synchronized (this) {
                connectWithoutWait();
                wait(millis);
            }
        }
        catch (InterruptedException e) {
            // CTILogger.error( e.getMessage(), e );
            logger.info("Interruped while waiting for connection on " + this, e);
        }
    }

    public final void connectWithoutWait() {
        if (connection == null) {
            connection = connectionFactory.createConnection(this.host, this.port);
            connection.setName(connectionName);
            connection.getMessageEvent().registerHandler(eventHandler);
            connection.getConnectionEvent().registerHandler(eventHandler);
        }
        connection.setAutoReconnect(getAutoReconnect());
        connection.start();
    }

    public void waitForValidConnection() throws InterruptedException {
        while (!isValid()) {
            logger.info("No connection available for " + connectionName + ". Waiting 10 seconds then trying again...");
            // sleep for 10 seconds, then try again
            Thread.sleep(10000);
        }
    }

    /**
     * read blocks until an object is available in the in queue and then returns a reference to it.
     * @return java.lang.Object
     */
    public final BaseMessage read() {
        return read(Long.MAX_VALUE);
    }

    /**
     * read blocks until an object is available in the in queue or at least millis milliseconds have elapsed then
     * returns a reference to it.
     * @return java.lang.Object
     */
    public final BaseMessage read(long millis) {
        BaseMessage in = null;

        try {
            synchronized (inQueue) {
                in = readInQueue();

                if (in == null && millis > 0) {
                    inQueue.wait(millis);
                    in = readInQueue();
                }
            }
        }
        catch (InterruptedException e) {}

        return in;
    }

    /**
     * Don't call this unless you are synchronized on inQueue Creation date: (2/27/2002 6:22:43 PM)
     * @return java.lang.Object
     */
    private final BaseMessage readInQueue() {
        return inQueue.poll();
    }

    /**
     * Send a MessageEvent to all of this connections MessageListeners
     * @param msg
     */
    protected void fireMessageEvent(BaseMessage msg) {
        totalReceivedMessages.incrementAndGet();
        if (msg instanceof CommandMessage && ((CommandMessage) msg).getOperation() == CommandMessage.ARE_YOU_THERE) {
            // Only instances of com.cannontech.message.util.Command should
            // get here and it should have a ARE_YOU_THERE operation
            // echo it back so servers don't time out on us
            write(msg);
        }

        MessageEvent e = new MessageEvent(this, msg);
        if (logger.isDebugEnabled()) {
            logger.debug("sending MessageEvent to " + messageListeners.size() + " listeners: " + e);
        }
        // At one time, the listeners were processed in reverse so that the messageReceived
        // implementation could remove itself as a listener without causing a concurrent
        // modification problem. That is no longer a problem, but just in case there is some code
        // that relies on the reverse ordering, we'll keep it.
        for (MessageListener ml : Lists.reverse(messageListeners)) {
            if (logger.isDebugEnabled()) {
                logger.debug("sending MessageEvent to " + ml);
            }
            ml.messageReceived(e);
            totalFiredEvents.incrementAndGet();
        }
    }

    /**
     * Tries to send all queued messages through the {@linkplain #connection} object. We must not simply forward
     * messages to this object because the connection may queue them itself and that will break message priority
     * queuing. To prevent this form happening, this method ensures the connection is established before trying to send
     * messages currently in the {@link #outQueue}
     */
    private synchronized void trySendMessages() {
        if (connection != null && connection.getState() == ConnectionState.Connected) {
            // This if is probably useless but it is here for the sake of consistency with the legacy code
            if (isValid) {
                while (!outQueue.isEmpty()) {
                    connection.send(outQueue.poll());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ClientConnection [name= " + getName() + ", " + connection + "]";
    }

    /**************************************************************************
     * Properties
     **************************************************************************/
    public final void setHost(String host) {
        this.host = host;
    }

    public final void setPort(int port) {
        this.port = port;
    }

    protected final String getName() {
        return connectionName;
    }

    public final BaseMessage getRegistrationMsg() {
        return registrationMsg;
    }

    public final void setRegistrationMsg(BaseMessage newValue) {
        this.registrationMsg = newValue;
    }

    @ManagedAttribute
    public final int getListenerCount() {
        return messageListeners.size();
    }

    @ManagedAttribute
    public final long getTotalFiredEvents() {
        return totalFiredEvents.get();
    }

    @ManagedAttribute
    public final long getTotalReceivedMessages() {
        return totalReceivedMessages.get();
    }

    @ManagedAttribute
    public final long getTotalSentMessages() {
        return totalSentMessages.get();
    }

    /**************************************************************************
     * BasicServerConnection interface implementation *
     **************************************************************************/
    /**
     * Writes an object to the output queue. If the connection is invalid, an exception will be thrown (if you want the
     * old behavior of queuing the message, use the queue(Object) method).
     * @param o The message to write
     * @throws ConnectionException if the connection is not valid
     */
    @Override
    public final void write(BaseMessage o) {
        LogHelper.debug(logger, "writing msg: %s", o);
        BaseMessage msg = o;
        if (!isValid()) {
            throw new ConnectionException("Unable to write message (" + msg + "), " + this + " is invalid.");
        }
        queue(msg);
    }

    /**
     * Writes an object to the output queue.
     * @param o java.lang.Object
     */
    @Override
    public final void queue(BaseMessage o) {
        BaseMessage msg = o;
        outQueue.put(msg);
        totalSentMessages.incrementAndGet();
        if (logger.isDebugEnabled()) {
            int size = outQueue.size();
            if (size > 2) {
                logger.debug("Current queue size: " + size);
            }
        }

        trySendMessages();
    }

    /**
     * Add a message listener to this connection
     * @param l
     */
    @Override
    public final void addMessageListener(MessageListener l) {
        messageListeners.add(l);
    }

    /**
     * Remove a message listener from this connection
     * @param l
     */
    @Override
    public final void removeMessageListener(MessageListener l) {
        messageListeners.remove(l);
    }

    @Override
    @ManagedAttribute
    public final boolean isValid() {
        return isValid;
    }

    /**************************************************************************
     * IServerConnection interface implementation *
     **************************************************************************/
    @Override
    @ManagedAttribute
    public final boolean getAutoReconnect() {
        return this.autoReconnect;
    }

    @Override
    @ManagedAttribute
    public final String getHost() {
        return this.host;
    }

    @Override
    @ManagedAttribute
    public final int getNumOutMessages() {
        return outQueue.size();
    }

    @Override
    @ManagedAttribute
    public final int getPort() {
        return this.port;
    }

    @Override
    public final int getTimeToReconnect() {
        return RandomUtils.nextInt(50) + 10;
    }

    /**
     * Use this method to determine if this connection has been told to connect() or connectWithoutWait(). If, for
     * example, the user wanted 1 instance of this ClientConnection() active and did not want another monitorThread to
     * be created if either connection methods were called. -- RWN
     */
    @Override
    @ManagedAttribute
    public final boolean isMonitorThreadAlive() {
        if (connection != null) {
            switch (connection.getState()) {
                case New:
                case Closed:
                case Error:
                    return false;

                case Connected:
                case Connecting:
                case Disconnected:
                    return true;
            }
        }

        return false;
    }

    @Override
    public final void setAutoReconnect(boolean val) {
        this.autoReconnect = val;
        if (connection != null) {
            connection.setAutoReconnect(val);
        }
    }

    @Override
    @ManagedAttribute
    public final boolean isQueueMessages() {
        return queueMessages;
    }

    /**
     * Set this to false if you don't want the connection to queue up received messages.
     * @param b
     */
    @Override
    public final void setQueueMessages(boolean b) {
        queueMessages = b;
    }

    @Override
    public void disconnect() {
        if (disconnected) {
            logger.warn("already disconnected " + getName());
        }
        deleteObservers();
        setAutoReconnect(false);
        cleanUp();

        isValid = false;
        disconnected = true;
    }

    /**************************************************************************
     * ConnectionEventHandler inner class
     **************************************************************************/
    private final ClientConnectionEventHandler eventHandler = new ClientConnectionEventHandler();

    private class ClientConnectionEventHandler implements ConnectionEventHandler, MessageEventHandler {

        @Override
        public void onConnectionEvent(Connection source, ConnectionState state) {
            ClientConnection conn = ClientConnection.this;
            
            logger.info("Connection state changed to <" + state + "> for " + conn);
                        
            switch (state) {
                case Connected:
                    
                    conn.isValid = true;

                    // Check to see if there is a registration message to be sent
                    BaseMessage reg = conn.getRegistrationMsg();
                    if (reg != null) {
                        source.send(reg);
                    }

                    conn.setChanged();
                    conn.notifyObservers(conn);
                    // use this for alerting clients to our connection state change
                    conn.fireMessageEvent(new ConnStateChangeMessage(true));

                    synchronized (conn) {
                        conn.notifyAll();

                        trySendMessages();
                    }
                    break;

                case Error:
                case Disconnected:
                    logger.debug("Setting " + conn + " invalid");

                    conn.isValid = false;

                    setChanged();
                    notifyObservers(conn);
                    // use this for alerting clients to our connection state change
                    fireMessageEvent(new ConnStateChangeMessage(false));
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onMessageEvent(Connection connection, BaseMessage msg) {
            ClientConnection conn = ClientConnection.this;

            if (msg.getClass() == MultiMessage.class) {
                MultiMessage<?> mpc = (MultiMessage<?>) msg;

                for (BaseMessage subMsg : mpc.getVector()) {
                    onMessageEvent(connection, subMsg);
                }
                return;
            }

            if (conn.isQueueMessages()) {
                // Add this message to the in queue so it can be 'read'
                synchronized (conn.inQueue) {
                    conn.inQueue.offer(msg);
                    conn.inQueue.notifyAll();
                }

                if (conn.inQueue.size() > 0 && (conn.inQueue.size() % 10000) == 0) {
                    CTILogger.warn("Message inQueue is growing! Queue Count = " + conn.inQueue.size());
                }
            }

            // Tell out listeners about this message
            try {
                // protect against missbehaved listeners
                conn.fireMessageEvent(msg);
            }
            catch (Throwable t) {
                CTILogger.error(getClass(), t);
            }
        }
    }
}

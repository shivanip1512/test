package com.cannontech.message.util;

import java.net.URI;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.Connection.ConnectionState;
import com.cannontech.messaging.connection.MessagingConnectionException;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.MessageEventHandler;
import com.cannontech.messaging.util.ConnectionFactory;
import com.cannontech.yukon.IServerConnection;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@ManagedResource
public abstract class ClientConnection extends Observable implements IServerConnection {

    private Connection connection;
    private ConnectionFactory connectionFactory;

    // Keep track of all of this connections MessageListeners
    private List<MessageListener> messageListeners = new CopyOnWriteArrayList<>();
    private final int EXCEPTIONS_BEFORE_LOG_SNOOZE = 20;
    private final int LOG_SNOOZE_MINUTES = 5;
    private LoadingCache<MessageListener, Integer> listenerExceptions = CacheBuilder.newBuilder().expireAfterWrite(
        LOG_SNOOZE_MINUTES, TimeUnit.MINUTES).build(new CacheLoader<MessageListener, Integer>() {
        @Override
        public Integer load(MessageListener key) throws Exception {
            return 0;
        }
    });
    private final String connectionName;
    private AtomicLong totalSentMessages = new AtomicLong();
    private AtomicLong totalReceivedMessages = new AtomicLong();
    private AtomicLong totalFiredEvents = new AtomicLong();

    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());

    // This message will be sent automatically on connecting
    private Message registrationMsg = null;

    private PriorityBlockingQueue<Message> outQueue = new PriorityBlockingQueue<>(100, new MessagePriorityComparable());

    private boolean isValid = false;
    private boolean autoReconnect = false;
    private boolean disconnected = false;

    private static final int maxQueueSize = 64 * 1024;
    private static final int latchOffOnRemainingCapacity = 1024;
    private static final int latchOnRemainingCapacity = maxQueueSize * 50 / 100;

    private volatile Long maxMessagesQueued = new Long(0);

    private boolean isBehindLatch = false;
    private ArrayBlockingQueue<Message> inQueue = new ArrayBlockingQueue<Message>(maxQueueSize);

    private class HandleMessage extends Thread
    {
        boolean run=true;
        
        public void setRun(boolean run) {
            this.run = run;
        }

        @Override
        public void run() {
            while (run) {
                Message msg;

                try {
                    msg = inQueue.take();
                    if (inQueue.remainingCapacity() > latchOnRemainingCapacity) {
                        isBehindLatch = false;
                    }
                    
                } catch (InterruptedException e) {
                    logger.warn("received shutdown signal, queue size: " + inQueue.size());
                    break;
                }

                if (inQueue.size() > inQueueSizeWarning) {
                    logger.warn(this.toString() + " - inQueue has more than " + inQueueSizeWarning
                                + " elements (" + inQueue.size() + ")");
                    inQueueSizeWarning = inQueueSizeWarning * 2;
                }

                long inQueueSize = inQueue.size();
                synchronized(maxMessagesQueued) {
                    if (inQueueSize > maxMessagesQueued) {
                        maxMessagesQueued = inQueueSize;
                    }
                }
                
                fireMessageEvent(msg);
            }
        }
    };
    
    private HandleMessage worker=new HandleMessage();
    
    /**
     * Out Queue size warning limit threshold. We warn when we hit 100 messages in queue, then
     * increment the warning threshold *2 and warn again when that is hit.
     */
    private int inQueueSizeWarning =
        Integer
            .getInteger("com.cannontech.message.util.ClientConnection.inQueueSizeWarning",
                        100);

    protected ClientConnection(String connectionName) {
        super();

        this.connectionName = connectionName;
        autoReconnect = true;
        
        worker.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Error in thread " + t, e);
            }
        });
        worker.setName(connectionName + "ClientConnection_worker_thread");
        worker.setDaemon(true);
        worker.start();

    }

    /**
     * Create a connection with a given server side connection. Used for Java servers.
     */
    protected ClientConnection(Connection connection) {
        this("Auto from " + connection);

        autoReconnect = false;

        connection.setName(connectionName);
        connection.getMessageEvent().registerHandler(eventHandler);
        connection.getConnectionEvent().registerHandler(eventHandler);
        
        this.connection = connection;
    }

    private final void cleanUp() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        
        worker.setRun(false);
    }

    public final void connect() {
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
        } catch (InterruptedException e) {
            // CTILogger.error( e.getMessage(), e );
            logger.info("Interruped while waiting for connection on " + this, e);
        }
    }

    public final void connectWithoutWait() {
        if (connection == null) {
            connection = getConnectionFactory().createConnection();
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
     *
     * @return java.lang.Object
     */
    public final Message read() {
        return read(Long.MAX_VALUE);
    }

    /**
     * read blocks until an object is available in the in queue or at least millis milliseconds have elapsed
     * then
     * returns a reference to it.
     *
     * @return java.lang.Object
     */
    public final Message read(long millis) {
        Message in = null;

        try {
            synchronized (inQueue) {
                in = readInQueue();

                if (in == null && millis > 0) {
                    inQueue.wait(millis);
                    in = readInQueue();
                }
            }
        } catch (InterruptedException e) {
            logger.debug("Read was iterrupted.", e);
        }

        return in;
    }

    /**
     * Don't call this unless you are synchronized on inQueue Creation date: (2/27/2002 6:22:43 PM)
     *
     * @return java.lang.Object
     */
    private final Message readInQueue() {
        return inQueue.poll();
    }

    /**
     * Send a MessageEvent to all of this connections MessageListeners
     *
     * @param msg
     */
    protected void fireMessageEvent(Message msg) {
        totalReceivedMessages.incrementAndGet();
        if (msg instanceof Command && ((Command) msg).getOperation() == Command.ARE_YOU_THERE) {
            // Only instances of com.cannontech.message.util.Command should
            // get here and it should have a ARE_YOU_THERE operation
            // echo it back so servers don't time out on us
            try {
                write(msg);
            } catch (ConnectionException e) {
                logger.warn("could not respond to ARE_YOU_THERE", e);
            }
        } else {
            MessageEvent messageEvent = new MessageEvent(this, msg);
            if (logger.isDebugEnabled()) {
                logger.debug("sending MessageEvent to " + messageListeners.size() + " listeners: " + messageEvent);
            }
            for (MessageListener messageListener : messageListeners) {
                if (logger.isDebugEnabled()) {
                    logger.debug("sending MessageEvent to " + messageListener);
                }
                try {
                    messageListener.messageReceived(messageEvent);
                } catch (Throwable t) {
                    String errorString = "Error while firing a message event to " + messageListener + ". ";
                    if (msg != null) {
                        errorString += "Msg type '" + msg.getClass() + "' content :\n" + msg;
                    } else {
                        errorString += "Msg is null";
                    }

                    Integer errorCount = listenerExceptions.getUnchecked(messageListener);
                    if (errorCount < EXCEPTIONS_BEFORE_LOG_SNOOZE) {
                        errorCount++;
                        if (errorCount == EXCEPTIONS_BEFORE_LOG_SNOOZE) {
                            logger.error("Temporarily muting stack traces for " + messageListener + " because of "
                                + EXCEPTIONS_BEFORE_LOG_SNOOZE + " exceptions within " + LOG_SNOOZE_MINUTES
                                + " minutes of each other");
                        }
                        listenerExceptions.put(messageListener, errorCount);
                        logger.error(errorString, t);
                    } else {
                        logger.error(errorString + "\n" + t);
                    }
                }
                totalFiredEvents.incrementAndGet();
            }
        }
    }

    /**
     * Tries to send all queued messages through the {@linkplain #connection} object. We must not simply
     * forward
     * messages to this object because the connection may queue them itself and that will break message
     * priority
     * queuing. To prevent this form happening, this method ensures the connection is established before
     * trying to send
     * messages currently in the {@link #outQueue}
     */
    private synchronized void trySendMessages() {
        if (connection != null && connection.getState() == ConnectionState.Connected) {
            // This if is probably useless but it is here for the sake of consistency with the legacy code
            if (isValid) {
                try {
                    while (!outQueue.isEmpty()) {
                        connection.send(outQueue.poll());
                    }
                } catch (MessagingConnectionException e) {
                    logger.warn("could not send message", e);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ClientConnection [name = " + getName() + ", " + connection + "]";
    }

    /**************************************************************************
     * Properties
     **************************************************************************/
    protected final String getName() {
        return connectionName;
    }

    public final Message getRegistrationMsg() {
        return registrationMsg;
    }

    public final void setRegistrationMsg(Message newValue) {
        registrationMsg = newValue;
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

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**************************************************************************
     * BasicServerConnection interface implementation *
     **************************************************************************/
    /**
     * Writes an object to the output queue. If the connection is invalid, an exception will be thrown (if you
     * want the
     * old behavior of queuing the message, use the queue(Object) method).
     *
     * @param o The message to write
     * @throws ConnectionException if the connection is not valid
     */
    @Override
    public final void write(Message o) {
        if (logger.isDebugEnabled()) {
            logger.debug("writing msg: " + o);
        }
        Message msg = o;
        if (!isValid()) {
            throw new ConnectionException("Unable to write message (" + msg + "), " + this + " is invalid.");
        }
        queue(msg);
    }

    /**
     * Writes an object to the output queue.
     *
     */
    @Override
    public final void queue(Message o) {
        Message msg = o;
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
     *
     * @param l
     */
    @Override
    public final void addMessageListener(MessageListener l) {
        messageListeners.add(l);
    }

    /**
     * Remove a message listener from this connection
     *
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
        return autoReconnect;
    }

    @Override
    @ManagedAttribute
    public final int getNumOutMessages() {
        return outQueue.size();
    }

    /**
     * Use this method to determine if this connection has been told to connect() or connectWithoutWait(). If,
     * for
     * example, the user wanted 1 instance of this ClientConnection() active and did not want another
     * monitorThread to
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
        autoReconnect = val;
        if (connection != null) {
            connection.setAutoReconnect(val);
        }
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
                Message reg = conn.getRegistrationMsg();
                if (reg != null) {
                    source.send(reg);
                }

                conn.setChanged();
                conn.notifyObservers(conn);
                // use this for alerting clients to our connection state change
                conn.fireMessageEvent(new ConnStateChange(true));

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
                fireMessageEvent(new ConnStateChange(false));
                break;

            default:
                break;
            }
        }

        @Override
        public void onMessageEvent(Connection connection, Message msg) {
            ClientConnection conn = ClientConnection.this;

            if (msg instanceof Multi) {
                Multi<?> mpc = (Multi<?>) msg;

                for (Message subMsg : mpc.getVector()) {
                    onMessageEvent(connection, subMsg);
                }
                return;
            }

//            CTILogger.info(conn.getName()+": Inbound message: "+msg);
            // Add this message to the in queue so it can be 'read'
            synchronized (conn.inQueue) {
                conn.inQueue.offer(msg);
                conn.inQueue.notifyAll();
                if (inQueue.remainingCapacity() < latchOffOnRemainingCapacity) {
                    isBehindLatch = true;
                }
            }
        }
    }

    public URI getConnectionUri() {
        if (connection == null) {
            throw new ConnectionException("Unexpected connection object is null");
        }
        return connection.getConnectionUri();
    }

    public boolean isConnectionFailed() {
        if (connection == null) {
            throw new ConnectionException("Unexpected connection object is null");
        }
        return connection.isConnectionFailed();
    }

    @ManagedAttribute
    public boolean isBehind() {
        return isBehindLatch;
    }

    @ManagedAttribute
    public int getQueueSize() {
        return inQueue.size();
    }
}

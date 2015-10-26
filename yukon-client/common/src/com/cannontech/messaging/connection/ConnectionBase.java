package com.cannontech.messaging.connection;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.event.Event;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.connection.event.ConnectionEvent;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.MessageEvent;
import com.cannontech.messaging.connection.event.MessageEventHandler;
import com.cannontech.messaging.connection.transport.Transport;

/**
 * Base implementation of a Connection. A connection can be started once only. Once it is stopped, it can not be
 * restarted. Outgoing messages are queued in a FIFO-like queue only if specific condition are met. See {@link
 * #canQueueMessage() canQueueMessage} for more information. To receive incoming messages, one must register to the
 * {@link #getMessageEvent() MessageEvent} this Connection exposes. Incoming messages are not queued and the
 * corresponding event is fired as soon as they are received and in the order they are received.
 * @param <T> Type of transport this connection uses. This must be specified by sub classes.
 */
public abstract class ConnectionBase<T extends Transport> implements Connection {

    private static final long DEFAULT_RECONNECTION_DELAY = 10000; // 10 sec

    protected final ConnectionEvent connectionEvent;
    protected final MessageEvent messageEvent;
    private ConnectionState state;

    private String name;
    private Thread worker;
    private T transport;

    /** Queue of messages to be sent over the transport */
    private Queue<Message> outQueue;

    /**
     * Out Queue size warning limit threshold. We warn when we hit 100 messages in queue, then
     * increment the warning threshold *2 and warn again when that is hit.
     */
    private int outQueueSizeWarning =
        Integer
            .getInteger("com.cannontech.messaging.connection.ConnectionBase.outQueueSizeWarning",
                        100);

    /** Count of messages sent */
    private AtomicLong outQueueSentMessages= new AtomicLong(0);    

    private boolean disconnectRequested = false;
    private boolean closeRequested = false;
    private boolean autoReconnect = false;
    private long reconnectionDelay;

    private boolean isFailed = false;
    private boolean disableWorkerThreadLogError = false;

    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());


    protected ConnectionBase(String name) {
        connectionEvent = new ConnectionEvent();
        messageEvent = new MessageEvent();
        outQueue = new LinkedList<Message>();

        setName(name);
        setState(ConnectionState.New);
        setReconnectionDelay(DEFAULT_RECONNECTION_DELAY);
    }

    /**
     * Starts the connection. This call is asynchronous (not blocking). When returning from this method, no assumptions
     * can be made about the connection state (if it is connected or not). To know when a connection has actually been
     * established, one must register to the {@link #connectionEvent connection Event} and decode "listen" for the
     * {@link {@link #Connection state} it is interested in.
     */
    @Override
    public final synchronized void start() {
        if (worker == null) {
            worker = new Thread(name + "_worker_thread") {
                @Override
                public void run() {
                    try {
                        execute();
                    }
                    catch (Throwable e) {
                        setState(ConnectionState.Error);
                        safeDisconnect(e);
                    }
                }
            };
            worker.setDaemon(true);
            worker.start();
        }
    }

    /**
     * Main connection loop. This method is called by the worker thread instantiated when the connection is stated. It
     * try to establish a connection and send queued outgoing messages. When a disconnection occurs, whether planned or
     * not, it either restart the connection sequence or terminates the loop depending on the value of the
     * {@link #autoReconnect autoReconnect} property.
     */
    protected void execute() {
        while (canConnect()) {
            try {
                waitForReconnectionDelay();
 
                setState(ConnectionState.Connecting);

                this.connect();

                if (state != ConnectionState.Connected) {
                    throw new MessagingConnectionException("Connection is not established correctly");
                }

                isFailed = false;
                
                while (canSendMessage()) {
                    Message msg;
                    synchronized (outQueue) {
                        if (outQueue.isEmpty()) {
                            outQueue.notifyAll(); // In case some one is waiting for the queue to be empty
                            outQueue.wait();      // wait for someone to write a message
                            continue;
                        }

                        msg = outQueue.peek();
                    }

                    if (msg != null) {
                        sendMessageToTransport(msg);
                    }

                    synchronized (outQueue) {
                        outQueue.poll();
                    }
                }
            }
            catch (InterruptedException e) {
                // we shall now disconnect and restart the connection sequence if it is an auto
                // reconnect connection
                // done in the finally block
                
                //clear the flag
                Thread.interrupted();
            }
            catch (Exception e) {
                isFailed = true;
                
                if (disconnectRequested || disableWorkerThreadLogError) {
                    logger.debug("Error in connection worker thread", e);
                }
                else {
                    logger.error("Error in connection worker thread", e);
                }
            }
            finally {
                safeDisconnect();
                setDisconnectRequested(false);              // clear the flag
                setState(ConnectionState.Disconnected);
            }
            disableWorkerThreadLogError = false;
        }
        setState(ConnectionState.Closed);
    }

    protected void waitForReconnectionDelay() throws InterruptedException {
        if (state == ConnectionState.New) {
            return;
        }

        Thread.sleep(getReconnectionDelay());
    }

    /**
     * Internal procedure to actually establish the connection. Implementation Must be synchronous, that is, connection
     * must be established or must have failed to establish when this method returns.
     * @throws ConnectionException
     */
    protected abstract void connect() throws MessagingConnectionException;

    @Override
    public final void close() {
        setCloseRequested(true);

        // Give the outQueue the time to empty itself
        synchronized (outQueue) {
            if (!outQueue.isEmpty()) {
                try {
                    outQueue.wait(2000);
                }
                catch (InterruptedException e) {}
            }
        }

        // At this point, empty or not the connection is closed and its associated resources freed
        safeDisconnect();
    }

    /**
     * Disconnect the current connection and free associated resources. subclasses MUST override it and call this super
     * implementation if they need to cleanup additional resources at the subclass level.
     */
    protected void disconnect() throws MessagingConnectionException {
        setDisconnectRequested(true);
        if (transport != null) {
            transport.close();
            transport = null;
        }
    }

    /**
     * Safe version of disconnect method as it should never throw exceptions. Useful in catch block to prevent verbose
     * try/catch block nesting. Also guarantees that only the worker thread will run the disconnect method
     */
    protected final void safeDisconnect() {
        try {
            // Prevent interrupting the thread twice
            boolean doDisconnect = false;
            synchronized (this) {
                if (!isDisconnectRequested()) {
                    setDisconnectRequested(true);
                    doDisconnect = true;
                }
            }

            if (doDisconnect) {
                disconnect();
                // Do not interrupt self!
                if (worker != null && Thread.currentThread() != worker) {
                    worker.interrupt();
                }
            }
        }
        catch (Throwable e) {
            logger.error("Error while disconnecting the connection", e);
        }
    }

    /**
     * Safe version of disconnect method as it should never throw exceptions. Useful in catch block to prevent verbose
     * try/catch block nesting. The cause of the disconnection request is specified by the optional Exception argument
     */
    protected final void safeDisconnect(Throwable e) {
        if (e != null) {
            logger.error("Disconnecting connection because of : " + e.getMessage(), e);
        }
        safeDisconnect();
    }

    @Override
    public void send(Message message) throws MessagingConnectionException {
        if (!canQueueMessage()) {
            throw new MessagingConnectionException("Can not send message as connection is invalid and will not reconnect");
        }

        synchronized (outQueue) {
            outQueue.offer(message);
            outQueue.notifyAll();
            
            if (outQueue.size() > outQueueSizeWarning)
            {
                Log.warn(this.toString() + " - outQueue has more than " + outQueueSizeWarning
                         + " elements (" + outQueue.size() + ")");
                outQueueSizeWarning = outQueueSizeWarning * 2;
            }
            
            outQueueSentMessages.getAndIncrement();
            if (outQueueSentMessages.get() % 1000 == 0)
            {
                Log.info(this.toString() + " - has sent " + outQueueSentMessages + "messages");
            }
        }
    }

    /**
     * Synchronously sends a message to the underling transport. Note that the transport itself can actually try to send
     * the message asynchronously depending on the actual technology used.
     * @param message The message to send
     */
    protected abstract void sendMessageToTransport(Message message);

    /**
     * Indicates if a Connection instance can try to establish a connection now based on its current state and flags
     * @return true if it is allowed to connect, false otherwise
     */
    public boolean canConnect() {
        // this connection was already closed or is invalid
        if (isCloseRequested() || state == ConnectionState.Closed || state == ConnectionState.Error) {
            return false;
        }

        if (state == ConnectionState.New) {
            // this connection has not started yet
            return true;
        }
        else {
            // this connection has already been started so it depends on the autoReconnect flag
            return isAutoReconnect();
        }
    }

    /**
     * Indicates if a message can be queued on this connection to be sent latter.
     * @return true if a message has a "reasonable" chance to be sent by this connection
     */
    public boolean canQueueMessage() {
        // this connection has been explicitly closed or is invalid
        if (isCloseRequested() || state == ConnectionState.Closed || state == ConnectionState.Error) {
            return false;
        }

        // it is an auto-reconnecting connection so we can (optimistically) assume it will
        // eventually (re)establish a connection.
        if (isAutoReconnect()) {
            return true;
        }

        // Disconnection already requested, thus we can not take more messages (as this connection will NOT
        // auto-reconnect)
        if (isDisconnectRequested()) {
            return false;
        }

        // After eliminating the exceptional cases, it is just a matter of "are we connected or not"
        return state == ConnectionState.Connected;
    }

    /**
     * Indicates if a message can be send on this connection now (synchronously) base on this connection current state
     * and flags
     */
    public boolean canSendMessage() {
        if (state != ConnectionState.Connected) {
            return false;
        }

        // When closing, the connection can still send its last outgoing messages left in the
        // outQueue
        if (isCloseRequested()) {
            return true;
        }

        // At this point if a disconnection is requested, we can no longer send messages
        return !isDisconnectRequested();
    }

    @Override
    public Event<ConnectionEventHandler> getConnectionEvent() {
        return connectionEvent.getEventInterface();
    }

    @Override
    public Event<MessageEventHandler> getMessageEvent() {
        return messageEvent.getEventInterface();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    @Override
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    @Override
    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    protected boolean isCloseRequested() {
        return closeRequested;
    }

    protected void setCloseRequested(boolean closeRequested) {
        this.closeRequested = closeRequested;
    }

    protected boolean isDisconnectRequested() {
        return disconnectRequested;
    }

    protected synchronized void setDisconnectRequested(boolean disconnectRequested) {
        this.disconnectRequested = disconnectRequested;
    }

    public T getTransport() {
        return transport;
    }

    public void setTransport(T transport) {
        this.transport = transport;
    }

    @Override
    public ConnectionState getState() {
        return state;
    }

    /**
     * Set this connection state and fire the connectionEvent with the new state value if it has changed. The Error
     * state is a sticky state and can not be changed once set.
     * @param newState New state to set
     */
    protected void setState(ConnectionState newState) {
        // Error is a sticky state, once set, it can not be changed
        if (state == ConnectionState.Error) {
            return;
        }

        // Set the new state and fire the connectionStateEvent only if the new state is different from the previous one
        if (state != newState) {
            state = newState;
            connectionEvent.fire(this, state);
        }
    }

    public long getReconnectionDelay() {
        return reconnectionDelay;
    }

    public void setReconnectionDelay(long reconnectionDelay) {
        this.reconnectionDelay = Math.max(reconnectionDelay, 0);
    }

    @Override
    public String toString() {
        return "messaging connection " + getName();
    }
    
    @Override
    public boolean isConnectionFailed() {
        return isFailed;
    }
    
    public void disableWorkerThreadLogError() {
        disableWorkerThreadLogError = true;
    }
    
    /**
     * This method is use internally during connecting state to log a warning and fire disconnect events.
     */
    protected void warnConnectingFailure(String message) {
        isFailed = true;

        logger.warn(message);
    
        setState(ConnectionState.Disconnected);
        setState(ConnectionState.Connecting);
    }
}

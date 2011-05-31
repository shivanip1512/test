package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.yukon.IServerConnection;
import com.google.common.collect.Iterables;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.PortableInputStream;
import com.roguewave.vsj.PortableOutputStream;

@ManagedResource
public class ClientConnection extends java.util.Observable implements Runnable, IServerConnection {
    private InputStream inStrm = null;
    private OutputStream outStrm = null;

    private Socket sock = null;

    private String host = "127.0.0.1";
    private int port;

    private InThread inThread;
    private OutThread outThread;
    private Thread monitorThread;

    private AtomicLong totalSentMessages = new AtomicLong();
    private AtomicLong totalReceivedMessages = new AtomicLong();
    private AtomicLong totalFiredEvents = new AtomicLong();

    protected ArrayList<Object> inQueue = new ArrayList<Object>(100);
    protected PriorityBlockingQueue<Message> outQueue =
        new PriorityBlockingQueue<Message>(100, new MessagePriorityComparable());

    private boolean isValid = false;
    private boolean autoReconnect = false;
    private boolean queueMessages = false; // if true, be sure you are reading from the inQueue!!
    private boolean serverSocket = false;
    private boolean disconnected = false;

    // create a logger for instances of this class and its subclasses
    private Logger logger = YukonLogManager.getLogger(this.getClass());

    // This message will be sent automatically on connecting
    private Message registrationMsg = null;

    // Keeps track of the last index in inQueue that was read
    // so that every read doesn't cause one element to be removed
    // this is done since removing elements from an arraylist is expensive
    private int lastReadIndex = 0;

    // Keep track of all of this connections MessageListeners
    private List<MessageListener> messageListeners = new CopyOnWriteArrayList<MessageListener>();
    private final String connectionName;

    /**
     * ClientConnection constructor comment.
     */
    public ClientConnection(String connectionName) {
        super();
        this.connectionName = connectionName;
        autoReconnect = true;
    }

    /**
     * Create a connection with a given client connection. Used for Java servers.
     */
    public ClientConnection(Socket newSocket) {
        this("Auto from " + newSocket);

        this.sock = newSocket;
        serverSocket = true;
    }

    /**
     * Insert the method's description here.
     * Creation date: (8/9/00 12:13:06 PM)
     */
    private void cleanUp() {
        try {
            if (sock != null) {
                sock.close();
            }

            if (outStrm != null) {
                outStrm.close();
            }

            outStrm = null;
            sock = null;
        } catch (java.io.IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * This method was created in VisualAge.
     */
    public void connect() throws java.io.IOException {
        // wait a long time to timeout
        // for the freaks out there: 106,751,991,167 days (292,471,208 years)
        connect(Long.MAX_VALUE);
    }

    /**
     * This method was created in VisualAge.
     */
    public void connect(long millis) {
        connectWithoutWait();

        int sleep = 100, cnt = 0;
        try {
            while (!isValid() && ((cnt++) * sleep) <= millis) {
                Thread.sleep(sleep);
            }
        } catch (InterruptedException e) {
            // CTILogger.error( e.getMessage(), e );
            logger.info("Interruped while waiting for connection on " + this, e);
        }
    }

    /**
     * This method was created in VisualAge.
     */
    public void connectWithoutWait() {
        monitorThread = new Thread(this, getName() + "Monitor");
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    public void disconnect() {
        if (disconnected) {
            logger.warn("already disconnected " + getName());
        }
        this.deleteObservers();
        this.setAutoReconnect(false);

        if (monitorThread != null) {
            monitorThread.interrupt();
        }

        if (inThread != null) {
            inThread.interrupt();
        }

        // we must wait for the outQueue to be totally consumed
        // before destroying the outThread --RWN 1-9-2000
        int i = 0; // allow 50 iterations @ 50 milliseconds each (2.5 seconds)
        while (outQueue.size() > 0 && i < 50) {
            try {
                Thread.sleep(50);
                i++;
            } catch (InterruptedException e) {} // dont do anything
        }

        if (outThread != null) {
            outThread.interrupt();
        }

        cleanUp();

        isValid = false;
        disconnected = true;
    }

    protected String getName() {
        return connectionName;
    }

    /**
     * This method was created in VisualAge.
     * @return boolean
     */
    @Override
    @ManagedAttribute
    public boolean getAutoReconnect() {
        return this.autoReconnect;
    }

    /**
     * This method was created in VisualAge.
     */
    @Override
    @ManagedAttribute
    public String getHost() {
        return this.host;
    }

    /**
     * Creation date: (11/28/2001 11:10:31 AM)
     * @return int
     */
    @Override
    @ManagedAttribute
    public int getNumOutMessages() {
        return outQueue.size();
    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    @Override
    @ManagedAttribute
    public int getPort() {
        return this.port;
    }

    /**
     * This method was created in VisualAge.
     * @return com.cannontech.message.util.Message
     */
    public Message getRegistrationMsg() {
        return registrationMsg;
    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    @Override
    public int getTimeToReconnect() {
        return RandomUtils.nextInt(50) + 10;
    }

    /**
     * Insert the method's description here.
     * Creation date: (4/17/2001 1:40:04 PM)
     * @return boolean
     */

    @Override
    /*
     * Use this method to determine if this connection has been told
     * to connect() or connectWithoutWait(). If, for example, the user
     * wanted 1 instance of this ClientConnection() active and did not want
     * another monitorThread to be created if either connection methods were
     * called. -- RWN
     */
    @ManagedAttribute
    public boolean isMonitorThreadAlive() {
        if (monitorThread != null) {
            return monitorThread.isAlive();
        } else {
            return false;
        }
    }

    /**
     * This method was created in VisualAge.
     * @return boolean
     */
    @Override
    @ManagedAttribute
    public boolean isValid() {
        return isValid;
    }

    /**
     * read blocks until an object is available in the in queue and
     * then returns a reference to it.
     * @return java.lang.Object
     */
    public Object read() {
        return read(Long.MAX_VALUE);
    }

    /**
     * read blocks until an object is available in the in queue or at least millis milliseconds have
     * elapsed
     * then returns a reference to it.
     * @return java.lang.Object
     */
    public Object read(long millis) {
        Object in = null;

        try {
            synchronized (inQueue) {
                in = readInQueue();

                if (in == null && millis > 0) {
                    inQueue.wait(millis);
                    in = readInQueue();
                }
            }
        } catch (InterruptedException e) {}

        return in;
    }

    /**
     * Don't call this unluss you are synchronized on inQueue
     * Creation date: (2/27/2002 6:22:43 PM)
     * @return java.lang.Object
     */
    private Object readInQueue() {
        int qSize = inQueue.size();

        if (lastReadIndex < qSize) {

            Object in = inQueue.get(lastReadIndex++);

            if (lastReadIndex == qSize) {
                inQueue.clear();
                lastReadIndex = 0;
            }

            return in;
        }

        return null;
    }

    /**
     * This method was created in VisualAge.
     */
    public void reconnect() throws java.io.IOException {
        disconnect();
        connect();
    }

    /**
     * This method was created in VisualAge.
     */
    protected void registerMappings(CollectableStreamer polystreamer) {
        // Register mappings
        com.roguewave.vsj.streamer.CollectableMappings.registerAllCollectables(polystreamer);

        com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();

        for (int i = 0; i < mappings.length; i++) {
            polystreamer.register(mappings[i]);
        }
    }

    protected boolean isServerSocket() {
        return serverSocket;
    }

    /*
     * This is for the monitor thread.
     */
    @Override
    public void run() {
        // Use a count of retries after failing to connect
        // to avoid logging too much when a server is down.
        int retryCount = 0;

        while (true) {
            try {
                // we were not given a Socket, so create a new one everytime
                if (!isServerSocket()) {
                    String logStr = "Attempting to open SOCKET for " + this;
                    if (retryCount == 0) {
                        logger.info(logStr);
                    } else {
                        logger.debug(logStr);
                    }
                    this.sock = new Socket(this.host, this.port);
                }

                inStrm = new BufferedInputStream(this.sock.getInputStream());
                outStrm = new BufferedOutputStream(this.sock.getOutputStream());

                PortableInputStream pinStrm = new PortableInputStream(inStrm);
                PortableOutputStream poutStrm = new PortableOutputStream(outStrm);

                CollectableStreamer polystreamer = new CollectableStreamer();
                registerMappings(polystreamer);

                logger.debug("Starting connection in/out threads. ");

                inThread = new InThread(this, pinStrm, polystreamer, inQueue);
                outThread = new OutThread(this, poutStrm, polystreamer, outQueue);

                inThread.start();
                outThread.start();

                isValid = true;
                retryCount = 0;

                setChanged();
                notifyObservers(this);
                // use this for alerting clients to our connection state change
                fireMessageEvent(new ConnStateChange(true));

                logger.info("SOCKET open for " + this);

                do {
                    Thread.sleep(400);
                } while (inThread.isAlive() && outThread.isAlive());

                logger.debug("Interruping connection in/out threads");
                inThread.interrupt();
                outThread.interrupt();

                logger.info("CLOSING SOCKET for " + this);
                sock.close();

            } catch (InterruptedException e) {
                // The monitorThread must have been interrupted probably from disconnect()
                logger.info("InterruptedException in monitorThread", e);
                return;
            } catch (java.io.IOException io) {
                if (retryCount == 0) {
                    logger.info(io.getMessage());
                } else {
                    logger.debug(io.getMessage());
                }
            }

            logger.debug("Setting " + this + " invalid");

            isValid = false;

            setChanged();
            notifyObservers(this);
            // use this for alerting clients to our connection state change
            fireMessageEvent(new ConnStateChange(false));

            if (!getAutoReconnect()) {
                return;
            } else {
                int timeToReconnect = getTimeToReconnect();
                logger.debug("Connection to  " + host + " " + port + " is set to autoreconnect in "
                             + timeToReconnect + " seconds");

                try {
                    Thread.sleep(timeToReconnect * 1000);
                } catch (InterruptedException e) {
                    // The monitorThread must have been interrupted probably from disconnect()
                    logger.info("  InterruptedException in monitorThread : " + e.getMessage());
                    return;
                }
            }

            if (retryCount == 0) {
                logger.info("Attempting to reconnect " + this);
            } else {
                logger.debug("Attempting to reconnect " + this);
            }
            retryCount++;
        }

    }

    /**
     * This method was created in VisualAge.
     * @param val booean
     */
    @Override
    public void setAutoReconnect(boolean val) {
        this.autoReconnect = val;
    }

    /**
     * This method was created in VisualAge.
     * @param host java.lang.String
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * This method was created in VisualAge.
     * @param port int
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue com.cannontech.message.util.Message
     */
    public void setRegistrationMsg(Message newValue) {
        this.registrationMsg = newValue;
    }

    /**
     * Writes an object to the output queue. If the connection is invalid,
     * an exception will be thrown (if you want the old behavior of queing the
     * message, use the queue(Object) method).
     * @param o The message to write
     * @throws ConnectionException if the connection is not valid
     */
    @Override
    public void write(Message o) {
        Message msg = o;
        if (!isValid()) {
            throw new ConnectionException("Unable to write message (" + msg +
                                          "), " + this + " is invalid.");
        }
        queue(msg);
    }

    /**
     * Writes an object to the output queue.
     * @param o java.lang.Object
     */
    @Override
    public void queue(Message o) {
        Message msg = o;
        outQueue.put(msg);
        totalSentMessages.incrementAndGet();
        if (logger.isDebugEnabled()) {
            int size = outQueue.size();
            if (size > 2) {
                logger.debug("Current queue size: " + size);
            }
        }
    }

    /**
     * Add a message listener to this connection
     * @param l
     */
    @Override
    public void addMessageListener(MessageListener l) {
        messageListeners.add(l);
    }

    /**
     * Remove a message listener from this connection
     * @param l
     */
    @Override
    public void removeMessageListener(MessageListener l) {
        messageListeners.remove(l);
    }

    /**
     * Send a MessageEvent to all of this connections MessageListeners
     * @param msg
     */
    protected void fireMessageEvent(Message msg) {
        totalReceivedMessages.incrementAndGet();
        if (msg instanceof Command && ((Command) msg).getOperation() == Command.ARE_YOU_THERE) {
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
        for (MessageListener ml : Iterables.reverse(messageListeners)) {
            if (logger.isDebugEnabled()) {
                logger.debug("sending MessageEvent to " + ml);
            }
            ml.messageReceived(e);
            totalFiredEvents.incrementAndGet();
        }
    }

    @ManagedAttribute
    public int getListenerCount() {
        return messageListeners.size();
    }

    @Override
    @ManagedAttribute
    public boolean isQueueMessages() {
        return queueMessages;
    }

    @ManagedAttribute
    public long getTotalFiredEvents() {
        return totalFiredEvents.get();
    }

    @ManagedAttribute
    public long getTotalReceivedMessages() {
        return totalReceivedMessages.get();
    }

    @ManagedAttribute
    public long getTotalSentMessages() {
        return totalSentMessages.get();
    }

    /**
     * Set this to false if you don't want the connection to queue up received messages.
     * @param b
     */
    @Override
    public void setQueueMessages(boolean b) {
        queueMessages = b;
    }

    @Override
    public String toString() {
        return "ClientConnection[name=" + getName() + ", host=" + getHost() + ", port=" + getPort()
               + "]";
    }
}

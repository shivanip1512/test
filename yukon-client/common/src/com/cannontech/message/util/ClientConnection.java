package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.yukon.IConnectionBase;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.PortableInputStream;
import com.roguewave.vsj.PortableOutputStream;

public class ClientConnection extends java.util.Observable implements Runnable, IConnectionBase 
{
	private InputStream inStrm = null;
	private OutputStream outStrm = null;

	private Socket sock = null;
	
	private String host;
	private int port;
 
	private InThread inThread;
	private OutThread outThread;
	private Thread monitorThread;


	protected ArrayList inQueue = new ArrayList(100);
	protected ArrayList outQueue = new ArrayList(100);
	
	private boolean isValid = false;
	private boolean autoReconnect = false;
	private boolean queueMessages = true;
	private boolean serverSocket = false;


	//seconds until an another attempt is made to reconnect
	//if autoReconenct is true
	private int timeToReconnect = 5;
	
	//This message will be sent automatically on connecting
	private Message registrationMsg = null;

	// Keeps track of the last index in inQueue that was read
	// so that every read doesn't cause one element to be removed
	// this is done since removing elements from an arraylist is expensive
	private int lastReadIndex = 0;
	
	// Keep track of all of this connections MessageListeners 
	private ArrayList messageListeners = new ArrayList(5);

/**
 * ClientConnection constructor comment.
 */
public ClientConnection() {
	super();

	this.host = null;	
}
/**
 * ClientConnection constructor comment.
 */
public ClientConnection(String host, int port) {
	super();

	this.host = host;
	this.port = port;	
}

/**
 * Create a connection with a given client connection. Used for Java servers.
 */
public ClientConnection( Socket newSocket ) 
{
	this();

	this.sock = newSocket;
	serverSocket = true;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/00 12:13:06 PM)
 */
private void cleanUp()
{		
	try
	{
		if( sock != null )
			sock.close();

		if( outStrm != null )		
			outStrm.close();

/*		if( inStrm != null )
			inStrm.close();

		inStrm = null;
*/
		outStrm = null;
		sock = null;
	}
	catch( java.io.IOException ex )
	{
		CTILogger.error( ex.getMessage(), ex );
	}
}
/**
 * This method was created in VisualAge.
 */
public void connect() throws java.io.IOException 
{
	connectWithoutWait();

	
 	try
	{
		while( !isValid() )
			Thread.sleep( 100 );
	}
	catch( InterruptedException e )
	{
		//CTILogger.error( e.getMessage(), e );
		CTILogger.info("InterruptedException in " + this.getClass().getName() + ".connect() : " + e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 */
public void connectWithoutWait() throws java.io.IOException 
{	
	monitorThread = new Thread(this, "ConnectionMonitor");
	monitorThread.setDaemon(true);
	monitorThread.start();
}
/**
 * This method was created in VisualAge.
 */
public void disconnect() throws java.io.IOException 
{
	this.deleteObservers();
	this.setAutoReconnect(false);
	
	if( monitorThread != null )	
		monitorThread.interrupt();

	if( inThread != null )
		inThread.interrupt();

	// we must wait for the outQueue to be totally consumed
	// before destroying the outThread		--RWN  1-9-2000
	int i = 0;  // allow 50 iterations @ 50 milliseconds each (2.5 seconds)
	while( outQueue.size() > 0 && i < 50 )
	{
		try
		{
			Thread.sleep(50);
			i++;
		}
		catch(InterruptedException e )
		{}  // dont do anything
	}
	
	if( outThread != null )
		outThread.interrupt();

	cleanUp();

	isValid = false;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean getAutoReconnect() {
	return this.autoReconnect;
}
/**
 * This method was created in VisualAge.
 */
public String getHost() {
	return this.host;
	}
/**
 * Creation date: (11/28/2001 11:10:31 AM)
 * @return int
 */
public int getNumOutMessages() {
	synchronized (outQueue) {
		return outQueue.size();		
	}
}
/**
 * This method was created in VisualAge.
 * @return int
 */
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
public int getTimeToReconnect() {
	return this.timeToReconnect;
}

/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 1:40:04 PM)
 * @return boolean
 */
 
/* Use this method to determine if this connection has been told
 *   to connect() or connectWithoutWait(). If, for example, the user
 *   wanted 1 instance of this ClientConnection() active and did not want
 *   another monitorThread to be created if either connection methods were
 *   called.   -- RWN */
public boolean isMonitorThreadAlive() 
{
	if( monitorThread != null )
		return monitorThread.isAlive();
	else
		return false;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isValid() {
	return isValid;
}
/**
 * read blocks until an object is available in the in queue and
 * then returns a reference to it.
 * @return java.lang.Object
 */
public Object read() {
	return read( Long.MAX_VALUE );
}
/**
 * read blocks until an object is available in the in queue or at least millis milliseconds have elapsed
 * then returns a reference to it.  
 * @return java.lang.Object
 */
public Object read(long millis)
{
	Object in = null;
	
	try {
		synchronized(inQueue) {
			in = readInQueue();
			
			if( in == null && millis > 0) {
				inQueue.wait(millis);
				in = readInQueue();			
			}
		}
	}
	catch(InterruptedException e ) {
	}

	return in;
}
/**
 * Don't call this unluss you are synchronized on inQueue 
 * Creation date: (2/27/2002 6:22:43 PM)
 * @return java.lang.Object
 */
private Object readInQueue() {
	int qSize = inQueue.size();
	
	if ( lastReadIndex < qSize ) {
		 
		Object in = inQueue.get(lastReadIndex++);

		if( lastReadIndex == qSize ) {
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
	//Register mappings
	com.roguewave.vsj.streamer.CollectableMappings.registerAllCollectables( polystreamer );

	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();

	for( int i = 0; i < mappings.length; i++ )
		polystreamer.register( mappings[i] );
}

protected boolean isServerSocket()
{
	return serverSocket;
}

/**
 * This method was created in VisualAge.
 */
public void run() 
{		
	while( true )
	{		
		try
		{
			// we were not given a Socket, so create a new one everytime
			if( !isServerSocket() )
			{
				CTILogger.info("Attempting to open SOCKET to: " + this.host + " " + this.port);
				this.sock = new Socket( this.host, this.port );
			}


			inStrm = new java.io.BufferedInputStream(this.sock.getInputStream());
			outStrm = new java.io.BufferedOutputStream(this.sock.getOutputStream());
			
			PortableInputStream pinStrm = new PortableInputStream( inStrm );
			PortableOutputStream poutStrm = new PortableOutputStream( outStrm );


			CollectableStreamer polystreamer = new CollectableStreamer();
			registerMappings(polystreamer);
			
			inThread  = new InThread( this, pinStrm, polystreamer, inQueue );
			outThread = new OutThread( this, poutStrm, polystreamer, outQueue);

			inThread.start();
			outThread.start();

			isValid = true;

			setChanged();
			notifyObservers(this);
			
			CTILogger.info("SOCKET open for " + this.getClass().getName() );

			do
			{
				Thread.sleep(400);		
			} while( inThread.isAlive() && outThread.isAlive() );

			inThread.interrupt();
			outThread.interrupt();

			CTILogger.info("CLOSING SOCKET for " + this.getClass().getName() );
			sock.close();
			
		}
		catch( InterruptedException e )
		{
			// The monitorThread must have been interrupted probably from disconnect()
			CTILogger.info("  InterruptedException in monitorThread : " + e.getMessage() );
			return;
		}
		catch( java.io.IOException io )
		{
			CTILogger.info( io.getMessage() );
		}
		
		isValid = false;

		setChanged();
		notifyObservers(this);


		if( !getAutoReconnect() )
			return;
		else
		{
			try
			{
				Thread.sleep( getTimeToReconnect() * 1000 );
			}
			catch(InterruptedException e )
			{
				// The monitorThread must have been interrupted probably from disconnect()
				CTILogger.info("  InterruptedException in monitorThread : " +  e.getMessage() );
				return;
			}
		}

		CTILogger.info("Attempting to reconnect to " + getHost() + ":" + getPort() );
	} 
	
}
/**
 * This method was created in VisualAge.
 * @param val booean
 */
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
 * This method was created in VisualAge.
 * @param secs int
 */
public void setTimeToReconnect(int secs) {
	this.timeToReconnect = secs;
}
/**
 * write writes an object to the output queue.
 * @param o java.lang.Object
 */
public void write(Object o) {
	synchronized(outQueue) {
		outQueue.add(o);
		outQueue.notifyAll();
	}
}

/**
 * Add a message listener to this connection
 * @param l
 */
public void addMessageListener(MessageListener l) {
	messageListeners.add(l);
}

/**
 *  Remove a message listener from this connection
 * @param l
 */
public void removeMessageListener(MessageListener l) {
	messageListeners.remove(l);
}

/**
 * Send a MessageEvent to all of this connections MessageListeners
 * @param msg
 */
protected void fireMessageEvent(Message msg) {
	MessageEvent e = new MessageEvent(this, msg);	
	for(int i = messageListeners.size()-1; i >= 0; i--) {
		MessageListener ml = (MessageListener) messageListeners.get(i);
		ml.messageReceived(e);
	}
}

	/**
	 * @return
	 */
	public boolean isQueueMessages() {
		return queueMessages;
	}

	/**
	 * Set this to false if you don't want the connection to queue up received messages.
	 * @param b
	 */
	public void setQueueMessages(boolean b) {
		queueMessages = b;
	}

}

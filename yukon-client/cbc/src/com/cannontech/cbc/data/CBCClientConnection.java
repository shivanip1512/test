package com.cannontech.cbc.data;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * CBC.  Specifically it registers CBC specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import com.cannontech.cbc.messages.CBCCommand;
import com.cannontech.cbc.messages.CBCMessage;
import com.cannontech.cbc.messages.CBCStates;
import com.cannontech.cbc.messages.CBCSubAreaNames;
import com.cannontech.cbc.messages.CBCSubstationBuses;
import com.cannontech.cbc.messages.DefineCollectableCBCCommand;
import com.cannontech.cbc.messages.DefineCollectableCBCMessage;
import com.cannontech.cbc.messages.DefineCollectableCBCStateGroupMessage;
import com.cannontech.cbc.messages.DefineCollectableCBCSubAreaName;
import com.cannontech.cbc.messages.DefineCollectableCBCSubstationBuses;
import com.cannontech.cbc.messages.DefineCollectableCBCTempMoveCapBank;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.yukon.SystemRole;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;

public class CBCClientConnection extends java.util.Observable implements java.util.Observer, Runnable
{
	private ClientConnection connection = null;
	private String host = "127.0.0.1";
	private int port = 1910;
	
	private java.util.Vector messageEventListeners = new java.util.Vector();
	private java.util.Hashtable areaNames = null;
	
	//	private ResponseCheckerThread checkerThread = null;
	private Thread inThread = null;

	// this MUST conatin all the DefineCollectibles of the CBC client
	private static DefineCollectable[] mappings =
	{
		//Data Structures
		new DefineCollectableCapBankDevice(),
		new DefineCollectableSubBus(),
		new DefineCollectableFeeder(),
		new DefineCollectableState(),
		
		//Collectable Mappings
		com.roguewave.vsj.streamer.CollectableMappings.OrderedVector,
		com.roguewave.vsj.streamer.CollectableMappings.CollectableString,
		
		//Messages
		new com.cannontech.message.dispatch.message.DefineCollectableMulti(),
		new DefineCollectableCBCCommand(),
		new DefineCollectableCBCSubAreaName(),
		new DefineCollectableCBCSubstationBuses(),
		new DefineCollectableCBCMessage(), // not used except as a superclass
		new com.cannontech.message.dispatch.message.DefineCollectablePointData(),
		new com.cannontech.message.dispatch.message.DefineCollectableCommand(),
		new DefineCollectableCBCStateGroupMessage(),
		new DefineCollectableCBCTempMoveCapBank()
	};
	
/**
 * ClientConnection constructor comment.The caller must start the listening.
 * @param host java.lang.String
 * @param port int
 */
public CBCClientConnection() 
{
	super();// "127.0.0.1", 1910 );
	initialize();
	getExternalResources();
}

/**
 * ClientConnection constructor comment.The caller must start the listening.
 * @param host java.lang.String
 * @param port int
 */
public CBCClientConnection( Message registrationMsg_ ) 
{
	super();
	
	getExternalResources();
	initialize();
	getConnection().setRegistrationMsg( registrationMsg_ );
}

/**
 * ClientConnection constructor comment.The caller must start the listening.
 * @param host java.lang.String
 * @param port int
 */
public CBCClientConnection(String hostStr, int portInt)
{
	super();
	initialize();
	
	host = hostStr;
	port = portInt;	
}


/**
 * This method was created in VisualAge.
 * @param listener com.cannontech.common.util.MessageEventListener
 */
public void addMessageEventListener(MessageEventListener listener) {

	synchronized( messageEventListeners )
	{
		for( int i = messageEventListeners.size() - 1; i >= 0; i-- )
			if( messageEventListeners.elementAt(i) == listener )
				return;

		messageEventListeners.addElement(listener);
	}
}
public void addMessageListener(MessageListener listener) {

	connection.addMessageListener(listener); 
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 3:20:03 PM)
 * Version: <version>
 */
public void disconnect() throws java.io.IOException
{
	getConnection().disconnect();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param strat com.cannontech.cbc.Strategy
 */
public void executeCommand(int deviceID, int cmdOperation) throws java.io.IOException
{
	CBCCommand cmd = new CBCCommand();
	cmd.setDeviceID( deviceID );
	cmd.setCommand( cmdOperation );

	sendCommand( cmd );
	
	return;
}
/**
 * This method was created in VisualAge.
 * @param event MessageEvent
 */
public void fireMessageEvent(com.cannontech.common.util.MessageEvent event) 
{	
	synchronized( messageEventListeners )
	{
		for( int i = messageEventListeners.size() - 1; i >= 0; i-- )
			((com.cannontech.common.util.MessageEventListener) messageEventListeners.elementAt(i)).messageEvent(event);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 11:58:39 AM)
 * @return java.util.HashMap
 */
public java.util.Hashtable getAreaNames() 
{
/*	if( areaNames != null && getSubBuses() != null )
	{
		synchronized( getSubBuses() )
		{
			java.util.Enumeration keys = getAreaNames().keys();

			while( keys.hasMoreElements() )
			{
				SubBus[] subs = (SubBus[])getAreaNames().get( keys.nextElement() );
				
				for( int i = 0; i < getSubBuses().length; i++ )
				{
				SubBus[] subs = (SubBus[])getAreaNames().get( msg.getSubBusAt(i).getCcArea() );
				if( subs != null )
					for( int k = 0; k < subs.length; k++ )
						if( subs[k].getCcId().intValue() == msg.getSubBusAt(i).getCcId().intValue() )
						{
							subs[k] = msg.getSubBusAt(k);
							//getAreaNames().put( msg.getSubBusAt(i).getCcArea() );
							break;
						}
			}
		}
	}
*/

	return areaNames;
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/00 10:35:17 AM)
 * @return com.cannontech.message.util.ClientConnection
 */
protected com.cannontech.message.util.ClientConnection getConnection() 
{
	if( connection == null )
	{
		getExternalResources();
		
		com.cannontech.clientutils.CTILogger.info("CBCClientConnection Trying to connect to:  " + host + " " + port );
		connection = new com.cannontech.message.dispatch.ClientConnection()
		{
			protected void registerMappings(CollectableStreamer streamer)
			{
				for( int i = 0; i < mappings.length; i++ )
					streamer.register( mappings[i] );
			}
		};			


		connection.setHost( getHost() );
		connection.setPort( getPort() );
		
		connection.setAutoReconnect( true );
		connection.setTimeToReconnect( 10 );
	}

	return connection;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/00 2:26:52 PM)
 */
private void getExternalResources() 
{
	setHost( ClientSession.getInstance().getRolePropertyValue(
		SystemRole.CAP_CONTROL_MACHINE, "127.0.0.1" ) );

	setPort( new Integer( ClientSession.getInstance().getRolePropertyValue(
		SystemRole.CAP_CONTROL_PORT, "1910") ).intValue() );

}
/**
 * Insert the method's description here.
 * Creation date: (8/22/00 1:27:39 PM)
 * @return java.lang.String
 */
public java.lang.String getHost() {
	return host;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/00 1:27:39 PM)
 * @return int
 */
public int getPort() {
	return port;
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/00 3:17:33 PM)
 * @param msg com.cannontech.cbc.messages.CBCError
 */
private void handleCBCStatesMessage(CBCStates msg) 
{
	com.cannontech.database.db.state.State[] states = new com.cannontech.database.db.state.State[msg.getNumberOfStates()];

   com.cannontech.clientutils.CTILogger.info(new com.cannontech.clientutils.commonutils.ModifiedDate(new java.util.Date().getTime()).toString()
   		+ " : Got a CapBank State Message with " + msg.getNumberOfStates()
   		+ " states" );
	
	synchronized ( states ) 
	{		
		for( int i = 0; i < msg.getNumberOfStates(); i++ )
		{
			msg.getState(i).setRawState( new Integer(i) ); // set the rawstate value
			states[i] = msg.getState(i);
		}		
	}

	com.cannontech.common.util.MessageEvent msgEvent = new com.cannontech.common.util.MessageEvent( this, "New state list received with " + msg.getNumberOfStates() + " state(s)." );
	msgEvent.setMessageType( MessageEvent.INFORMATION_MESSAGE );
	fireMessageEvent(msgEvent);

	setChanged();
	notifyObservers( states );
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/00 3:17:33 PM)
 */
private void handleCBCSubAreaNames( CBCSubAreaNames msg )
{
   com.cannontech.clientutils.CTILogger.info(new com.cannontech.clientutils.commonutils.ModifiedDate(new java.util.Date().getTime()).toString()
   		+ " : Got an Area Message with " + msg.getNumberOfAreas()
   		+ " areas" );
	
	setChanged();
	notifyObservers( msg ); // tell our listeners we have new data
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/00 3:17:33 PM)
 * @param msg com.cannontech.cbc.messages.CBCError
 */
private void handleCBCSubstationBuses(CBCSubstationBuses msg) 
{
	//if( getSubBuses() == null || getSubBuses().length == 0 )
		//setSubBuses( new SubBus[msg.getNumberOfBuses()] );

	for( int i = 0; i < msg.getNumberOfBuses(); i++ )
	{
      com.cannontech.clientutils.CTILogger.info(new com.cannontech.clientutils.commonutils.ModifiedDate(new java.util.Date().getTime()).toString()
      		+ " : Received SubBus - " + msg.getSubBusAt(i).getCcName() 
      		+ "/" + msg.getSubBusAt(i).getCcArea() );
	}

	if( msg.getNumberOfBuses() > 0 )
	{
		setChanged();
		notifyObservers( msg ); // tell our listeners we have new data
	}

}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 3:20:03 PM)
 * Version: <version>
 * @param exc java.lang.Exception
 */
private void handleException(Throwable e)
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("---------------- EXCEPTION CBCClientConnection() ----------------");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/00 5:11:57 PM)
 */
private void initialize() 
{	
	getConnection().addObserver( this );	
}

/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 11:23:42 AM)
 * @return boolean
 */
public boolean isConnValid() 
{
	return getConnection().isValid();
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) 
{

	SubBus[] subs = null;

	//Client connection test
	//Simply sends pings and receives them
	try
	{
		//CBCClientConnection conn = new CBCClientConnection("127.0.0.1", 1910 );			
		
		//conn.getConnection().write( new CBCCommand( CBCCommand.REQUEST_ALL_SUBS, 0 ) );
		
		//Object ret = conn.getConnection().read( 5000 );
		//com.cannontech.clientutils.CTILogger.info(ret.getClass());
		
		//while( subs == null )
		{
			//subs = conn.getSubBuses();
			//Thread.currentThread().sleep(1000);
		}

		com.cannontech.debug.gui.ObjectInfoDialog d = new com.cannontech.debug.gui.ObjectInfoDialog();
		d.setModal(true);
		d.showDialog( new com.cannontech.tdc.TDCMainFrame() );
	
//for( int i = 0; i < subs.length; i++ )
	//com.cannontech.clientutils.CTILogger.info("   " + subs[i].getRecentlyControlledFlag().toString());

		Thread.sleep( 1000 );
com.cannontech.clientutils.CTILogger.info("*** HI***");
		Thread.sleep( 10000 );
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	com.cannontech.clientutils.CTILogger.info("exiting main" );
}
/**
 * run method comment.
 */
// This method continously reads from the inputQueue
public void run() 
{
	try
	{	// sit here and try to connect if we have to
		if( !connection.isValid() )
			connection.connect();
	}
	catch( java.io.IOException ex )
	{
		handleException( ex );
	}

	try
	{
      Object in = null;
      
		while( true )
		{
			in = null;

			if( getConnection().isValid() && ((in = getConnection().read(0L)) != null) )
			{
				if( in instanceof CBCMessage )
				{
					if( in instanceof CBCSubstationBuses )
					{
						handleCBCSubstationBuses( (CBCSubstationBuses)in );
					}				
					else if( in instanceof CBCStates )
					{
						handleCBCStatesMessage( (CBCStates)in );
					}				
					else if( in instanceof CBCSubAreaNames )
					{
						handleCBCSubAreaNames( (CBCSubAreaNames)in );
					}				
					else
						com.cannontech.clientutils.CTILogger.info("Unrecoginized CBCMessage in CBClientConnection received.");
				}
				else
					com.cannontech.clientutils.CTILogger.info("Unrecoginized Object in CBClientConnection received.");
			}

			if( !getConnection().isValid() )
			{
				stopInThread();
				return;
			}		
			
			try
			{
				Thread.sleep(200);
			}
			catch( InterruptedException ex )
			{
				stopInThread();
				return;			
			}	

		}
	}
	catch( Exception exx )
	{
		com.cannontech.clientutils.CTILogger.info("*** " + this.getClass().getName() + " Thread DEATH!!!!");
		handleException(exx);
	}
	
	
}
/**
 * 
 * @return nothing
 */
public void sendCommand( CBCCommand cmd )
{
	//Don't bother sending this out if were not in a good state
	if( !getConnection().isValid() )
		return;
		
	synchronized( this )
	{
		getConnection().write( cmd );
	}

	
	com.cannontech.common.util.MessageEvent msgEvent = new com.cannontech.common.util.MessageEvent( this, CBCCommand.getCommandString(cmd.getCommand()) + " was executed." );
	msgEvent.setMessageType( MessageEvent.INFORMATION_MESSAGE );
	fireMessageEvent(msgEvent);
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/00 10:35:17 AM)
 * @param newConnection com.cannontech.message.util.ClientConnection
 */
private void setConnection(com.cannontech.message.util.ClientConnection newConnection) {
	connection = newConnection;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/00 1:27:39 PM)
 * @param newHost java.lang.String
 */
private void setHost(java.lang.String newHost) {
	host = newHost;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/00 1:27:39 PM)
 * @param newPort int
 */
private void setPort(int newPort) {
	port = newPort;
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 5:33:36 PM)
 */
public synchronized void startInThread()
{
	if( inThread == null )
	{
		inThread = new Thread(this, "CBCClientConnection");
		inThread.setDaemon(true);
		inThread.start();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/00 5:33:36 PM)
 */
public void stopInThread()
{
	if( inThread != null )
	{
		inThread.interrupt();
		inThread = null;
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:36:12 AM)
 * @param o java.util.Observable
 * @param val java.lang.Object
 */
public void update(java.util.Observable o, Object val)
{
	//Should be an instance of com.cannontech.message.util.ClientConnection
	//notifying us of a change in the connections state
	if( o instanceof ClientConnection )
	{
		ClientConnection conn = (ClientConnection) o;
		
		if( conn.isValid() )
			startInThread();
		else
			stopInThread();

		setChanged();
		notifyObservers( this ); // tell our listeners we have been updated
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 6:31:54 PM)
 * @param subBus com.cannontech.cbc.data.SubBus
 */
/* This method will update the subbus inside our AreaNames Hashmap. */
/*  This needs to be done to keep the data consistent */
private void updateAreaSubBus(SubBus subBus) 
{
	if( getAreaNames() != null )
	{
		synchronized( getAreaNames() )
		{
			SubBus[] subs = (SubBus[])getAreaNames().get( subBus.getCcArea() );
			if( subs != null )
			{
				for( int k = 0; k < subs.length; k++ )
					if( subs[k].getCcId().intValue() == subBus.getCcId().intValue() )
					{
						subs[k] = subBus;
						break;
					}
			}

		}
	}
	
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 11:17:28 AM)
 */
public void write( com.cannontech.message.util.Message cmd )
{
	getConnection().write( cmd );
}

}

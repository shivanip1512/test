package com.cannontech.cbc.data;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * CBC.  Specifically it registers CBC specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import com.cannontech.cbc.messages.CBCCommand;
import com.cannontech.cbc.messages.DefineCollectableCBCCommand;
import com.cannontech.cbc.messages.DefineCollectableCBCMessage;
import com.cannontech.cbc.messages.DefineCollectableCBCStateGroupMessage;
import com.cannontech.cbc.messages.DefineCollectableCBCSubAreaName;
import com.cannontech.cbc.messages.DefineCollectableCBCSubstationBuses;
import com.cannontech.cbc.messages.DefineCollectableCBCTempMoveCapBank;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.Message;
import com.cannontech.roles.yukon.SystemRole;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;

public class CBCClientConnection extends ClientConnection
{
	private String host = "127.0.0.1";
	private int port = 1910;
	
	private java.util.Vector messageEventListeners = new java.util.Vector();
	private java.util.Hashtable areaNames = null;

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
		setRegistrationMsg( registrationMsg_ );
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
	public void fireMsgEventGUI(com.cannontech.common.util.MessageEvent event) 
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
	 *
	public java.util.Hashtable getAreaNames() 
	{
		if ( !isValid() && areaNames != null )
			areaNames.clear();
	
		return areaNames;
	}
	*/
	
	protected void registerMappings( CollectableStreamer streamer )
	{
		super.registerMappings(streamer);
		
		for( int i = 0; i < mappings.length; i++ )
			streamer.register( mappings[i] );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/21/00 2:26:52 PM)
	 */
	private void getExternalResources() 
	{
		setHost( RoleFuncs.getGlobalPropertyValue( SystemRole.CAP_CONTROL_MACHINE ) );
	
		setPort( Integer.parseInt(
			RoleFuncs.getGlobalPropertyValue( SystemRole.CAP_CONTROL_PORT ) ) );
	
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
		setAutoReconnect( true );
	}
	
	/**
	 * 
	 * @return nothing
	 */
	public void sendCommand( CBCCommand cmd )
	{
		//Don't bother sending this out if were not in a good state
		if( !isValid() )
			return;
			
		synchronized( this )
		{
			write( cmd );
		}
	
		
		com.cannontech.common.util.MessageEvent msgEvent = new com.cannontech.common.util.MessageEvent( this, CBCCommand.getCommandString(cmd.getCommand()) + " was executed." );
		msgEvent.setMessageType( MessageEvent.INFORMATION_MESSAGE );
		fireMsgEventGUI(msgEvent);
	}

}

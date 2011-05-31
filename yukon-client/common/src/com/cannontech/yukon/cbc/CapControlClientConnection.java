package com.cannontech.yukon.cbc;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * CBC.  Specifically it registers CBC specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.message.capcontrol.DefineCollectableCapControlServerResponse;
import com.cannontech.message.util.ClientConnection;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;

public class CapControlClientConnection extends ClientConnection
{
	private java.util.Vector messageEvntListGUI = new java.util.Vector();
	private java.util.Hashtable areaNames = null;

	// this MUST conatin all the DefineCollectibles of the CBC client
	private static DefineCollectable[] mappings =
	{
		//Data Structures
		new DefineCollectableCapBankDevice(),
		new DefineCollectableSubBus(),
		new DefineCollectableFeeder(),
		new DefineCollectableState(),
		new DefineCollectableCBCArea(),
        new DefineCollectableCCSpecialSubAreas(),
        new DefineCollectableSpecialCBCArea(),
        new DefineCollectableVoltageRegulator(),
		
        //Collectable Mappings
		com.roguewave.vsj.streamer.CollectableMappings.OrderedVector,
		com.roguewave.vsj.streamer.CollectableMappings.CollectableString,
		
		//Messages
		new DefineCollectableCBCStateGroupMessage(),
		new com.cannontech.message.dispatch.message.DefineCollectableMulti(),
		new DefineCollectableCBCCommand(),
		new DefineCollectableCCSubAreas(),
		new DefineCollectableCBCSubstationBuses(),
		new DefineCollectableCBCMessage(), // not used except as a superclass
		new com.cannontech.message.dispatch.message.DefineCollectablePointData(),
		new DefineCollectableCBCTempMoveCapBank(),
		new DefineCollectableVerifySub(),
		new DefineCollectableSubStation(),
		new DefineCollectableCBCSubStations(),
		new DefineCollectableCapControlServerResponse(),
		new DefineCollectableVoltageRegulatorMessage(),
		new DefineCollectableDynamicCommand()
	};
	
	/**
	 * ClientConnection constructor comment.The caller must start the listening.
	 * @param host java.lang.String
	 * @param port int
	 */
	public CapControlClientConnection() 
	{
		super("CBC");
        setRegistrationMsg(new CapControlCommand ( CapControlCommand.REQUEST_ALL_AREAS, 0) );
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param listener com.cannontech.common.util.MessageEventListener
	 */
	public void addMessageEventListener(MessageEventListener listener) {
	
		synchronized( messageEvntListGUI )
		{
			for( int i = messageEvntListGUI.size() - 1; i >= 0; i-- )
				if( messageEvntListGUI.elementAt(i) == listener )
					return;
	
			messageEvntListGUI.addElement(listener);
		}
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param strat com.cannontech.cbc.Strategy
	 */
	public void executeCommand(int deviceID, int cmdOperation) throws java.io.IOException
	{
		CapControlCommand cmd = new CapControlCommand();
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
		synchronized( messageEvntListGUI )
		{
			for( int i = messageEvntListGUI.size() - 1; i >= 0; i-- )
				((com.cannontech.common.util.MessageEventListener) messageEvntListGUI.elementAt(i)).messageEvent(event);
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
    
    
	public void sendCommand(CapControlCommand cmd)
	{
		if( !isValid()) {
			return;
		}

		synchronized(this)
		{
			write( cmd );
		}

		MessageEvent msgEvent = new MessageEvent( this, CapControlCommand.getCommandString(cmd.getCommand()) + " was executed." );
		msgEvent.setMessageType( MessageEvent.INFORMATION_MESSAGE );
		fireMsgEventGUI(msgEvent);
	}
	
    public void sendCommand(DynamicCommand command)
    {
        if(!isValid()) {
            return;            
        }
            
        synchronized( this )
        {
            write(command);
        }
        
        MessageEvent msgEvent = new MessageEvent( this, command.getCommandType().toString() + " command was executed." );
        msgEvent.setMessageType( MessageEvent.INFORMATION_MESSAGE );
        fireMsgEventGUI(msgEvent);
    }

}

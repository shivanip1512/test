package com.cannontech.loadcontrol;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * LoadControl.  Specifically it registers LoadControl specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.events.LCChangeEvent;
import com.cannontech.loadcontrol.messages.LMControlAreaMsg;
import com.roguewave.vsj.CollectableStreamer;

public class LoadControlClientConnection extends com.cannontech.message.util.ClientConnection
{
	private static LoadControlClientConnection staticLoadControlClientConnection = null;
	
	private java.util.Vector messageEventListeners = new java.util.Vector();

	private java.util.Vector controlAreas = null;
/**
 * ClientConnection constructor comment.
 * @param host java.lang.String
 * @param port int
 */
protected LoadControlClientConnection() {
	super();
	initialize();
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
 * Insert the method's description here.
 * Creation date: (7/27/2001 3:10:51 PM)
 * @param obs java.util.Observer
 */
public void addObserver(java.util.Observer obs) 
{
	super.addObserver(obs);

	//if this is not the first registered client, we must manually ask the server
	// for all the ControlAreas
	if( countObservers() >= 2 )
	{
		com.cannontech.loadcontrol.messages.LMCommand cmd = new com.cannontech.loadcontrol.messages.LMCommand();
		cmd.setCommand( com.cannontech.loadcontrol.messages.LMCommand.RETRIEVE_ALL_CONTROL_AREAS );
		
		//tell the server we need all the ControlAreas sent to the new registered object
		LoadControlClientConnection.getInstance().write( cmd );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 11:21:10 AM)
 */
public void clearControlAreas() 
{
	getControlAreas().removeAllElements();

	// tell our listeners we need to remove everything
	setChanged();
	notifyObservers( new com.cannontech.loadcontrol.events.LCChangeEvent( 
									this,
									com.cannontech.loadcontrol.events.LCChangeEvent.DELETE_ALL,
									"deleteAll") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/2001 3:43:51 PM)
 */
public void disconnect() throws java.io.IOException
{
	super.disconnect();

	if( messageEventListeners != null )
		messageEventListeners.removeAllElements();

	deleteObservers();

	staticLoadControlClientConnection = null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 10:03:31 AM)
 */
public void doHandleMessage(Object obj)
{	
	if( obj instanceof LMControlArea )
	{
		handleLMControlArea( (LMControlArea)obj );
	}
	else if( obj instanceof LMControlAreaMsg )
	{
		for( int i = 0; i < ((LMControlAreaMsg)obj).getNumberOfLMControlAreas(); i++ )
			doHandleMessage( ((LMControlAreaMsg)obj).getLMControlArea(i) );
	}
	else if( obj instanceof com.cannontech.message.dispatch.message.Multi )
	{
		com.cannontech.message.dispatch.message.Multi multi =
			(com.cannontech.message.dispatch.message.Multi) obj;
			
		for( int i = 0; i < multi.getVector().size(); i ++ )
		{
			Object msg = multi.getVector().elementAt(i);
			doHandleMessage( msg );
		}		
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info("Received an unknown message of type:  " + obj.getClass() );
	}
		//throw new RuntimeException("Recieved a message of type " + obj.getClass() );
	
	return;
}
/**
 * This method was created in VisualAge.
 * @param event MessageEvent
 */
public void fireMessageEvent(com.cannontech.common.util.MessageEvent event) {
	
	synchronized( messageEventListeners )
	{
		for( int i = messageEventListeners.size() - 1; i >= 0; i-- )
			((com.cannontech.common.util.MessageEventListener) messageEventListeners.elementAt(i)).messageEvent(event);
	}
}
/**
 * This method was created in VisualAge.
 * @return LMControlArea[]
 */
public LMControlArea[] getAllLMControlAreas()
{	
	try
	{
		synchronized( getControlAreas() )
		{
			LMControlArea[] areas = new LMControlArea[ getControlAreas().size() ];
			getControlAreas().toArray( areas );
			return areas;
		}
	}
	catch( ArrayStoreException e )
	{
		throw e;
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 3:05:47 PM)
 * @return int
 */
public int getControlAreaCount() 
{
	return getControlAreas().size();
}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 6:17:59 PM)
 * @return java.util.Vector
 */
private java.util.Vector getControlAreas() 
{
	if( controlAreas == null )
		controlAreas = new java.util.Vector(10);
		
	return controlAreas;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2001 3:00:38 PM)
 * @return java.util.ArrayList
 */
private java.util.ArrayList getInQueue() {
	return inQueue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 2:53:44 PM)
 */
public synchronized static LoadControlClientConnection getInstance() 
{
	if( staticLoadControlClientConnection == null )
		staticLoadControlClientConnection = new LoadControlClientConnection();
		
	return staticLoadControlClientConnection;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 9:11:31 AM)
 * @return com.cannontech.loadcontrol.LoadControlProgram[]
 * @param lmControlAreaID int
 */
public LMProgramBase[] getPrograms(int lmControlAreaID) 
{
	synchronized( getControlAreas() )
	{
		for( int i = 0; i < getControlAreaCount(); i++ )
		{
			if( lmControlAreaID == ((LMControlArea)getControlAreas().get(i)).getYukonID().intValue() )
				return (LMProgramBase[])( ((LMControlArea)getControlAreas().get(i)).getLmProgramVector().toArray() );
		}

		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 6:03:46 PM)
 * @param sched LMControlArea
 */
private synchronized void handleLMControlArea(LMControlArea controlArea) 
{
	com.cannontech.clientutils.CTILogger.info( new com.cannontech.clientutils.commonutils.ModifiedDate(new java.util.Date().getTime()).toString() 
		+ " ---> Received a control area named " + controlArea.getYukonName() );

	synchronized ( getControlAreas() )
	{
		boolean found = false;
		for( int j = 0; j < getControlAreas().size(); j++ )
		{
			if( ((LMControlArea)getControlAreas().get(j)).equals(controlArea) )
			{
				getControlAreas().set( j, controlArea );
				found = true;
				
				// tell all listeners that we received an updated LMControlArea
				setChanged();
				notifyObservers( new LCChangeEvent(
										this, 
										LCChangeEvent.UPDATE,
										controlArea) );				
				return;
			}
		}

		if( !found )
		{
			getControlAreas().add( controlArea );
			
			// tell all listeners that we received a new LMControlArea
			setChanged();
			notifyObservers( new LCChangeEvent( 
									this, 
									LCChangeEvent.INSERT, 
									controlArea) );
		}
		
	}

}
/**
 * handleMessage should be defined by subclasses should they want
 * a chance to handle a particular message when it comes in.
 * Before a message is put in the inVector handleMessage is
 * called.  If handleMEssage returns true then the message
 * is considered handled otherwise the message will be put
 * in the inVector to await processing.
 * Do not actually handle the message here, handle it in doHandleMessage
 * @param message CtiMessage
 */
public boolean handleMessage(Object message) 
{
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 5:56:32 PM)
 */
private void initialize() 
{
}
/**
 * Insert the method's description here.
 * Creation date: (7/30/2001 4:05:08 PM)
 * @return boolean
 */
public boolean needInitConn() 
{
	//return true if there hasn't been any Observers set to watch this connection
	return (countObservers() <= 0);
}
/**
 * This method was created in VisualAge.
 * @param streamer CollectableStreamer
 */
public void registerMappings(CollectableStreamer streamer ) {
	super.registerMappings( streamer );

	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();

	for( int i = 0; i < mappings.length; i++ )
		streamer.register( mappings[i] );
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2001 2:49:33 PM)
 * @param client java.lang.Object
 */
public synchronized void removeClient(Object client) 
{

	if( messageEventListeners != null )
		messageEventListeners.remove( client );

	if( client instanceof java.util.Observer )
		deleteObserver( (java.util.Observer)client );

	try
	{
		if( countObservers() == 0 )
		{
			super.disconnect();
			staticLoadControlClientConnection = null;
		}
	}
	catch( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}
}

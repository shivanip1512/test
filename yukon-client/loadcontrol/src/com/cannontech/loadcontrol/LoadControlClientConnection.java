package com.cannontech.loadcontrol;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * LoadControl.  Specifically it registers LoadControl specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.events.LCChangeEvent;
import com.cannontech.loadcontrol.messages.LMControlAreaMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.roguewave.vsj.CollectableStreamer;

public class LoadControlClientConnection extends com.cannontech.message.util.ClientConnection implements MessageListener
{
	private static LoadControlClientConnection staticLoadControlClientConnection = null;
	
	private java.util.Vector controlAreas = null;
/**
 * ClientConnection constructor commerent.
 * @param host java.lang.String
 * @param port int
 */
protected LoadControlClientConnection() {
	super();
	initialize();
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

	deleteObservers();

	staticLoadControlClientConnection = null;
}

private void handleDeletedAreas( LMControlAreaMsg msg )
{
	Vector deleteAreas = new Vector( getControlAreas().size() );
	
	for( int i = 0; i < getControlAreas().size(); i++ )
	{
		boolean fnd = false;
		LMControlArea existingArea = (LMControlArea)getControlAreas().get(i);

		for( int j = 0; j  < msg.getNumberOfLMControlAreas(); j++ )
		{
			if( msg.getLMControlArea(j).equals(existingArea) )
			{
				fnd = true;
				break;
			}
		}
				
		if( !fnd )
			deleteAreas.add( existingArea );
	}

	for( int i = (deleteAreas.size()-1); i >=0; i-- )
	{
		getControlAreas().remove( deleteAreas.get(i) );
		setChanged();
		notifyObservers( new LCChangeEvent(
								this, 
								LCChangeEvent.DELETE,
								deleteAreas.get(i)) );	
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
	CTILogger.debug( new ModifiedDate(new java.util.Date().getTime()).toString() 
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
 * Insert the method's description here.
 * Creation date: (2/21/2001 5:56:32 PM)
 */
private void initialize() 
{
	addMessageListener( this );
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
 * Insert the method's description here.
 * Creation date: (7/27/2001 2:49:33 PM)
 * @param client java.lang.Object
 */
public synchronized void removeClient(Object client) 
{

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
		CTILogger.error( e.getMessage(), e );
	}

}

public synchronized void messageReceived( MessageEvent e )
{
	Object obj = e.getMessage();
	if( obj instanceof LMControlArea )
	{
		handleLMControlArea( (LMControlArea)obj );
	}
	else if( obj instanceof LMControlAreaMsg )
	{
		LMControlAreaMsg msg = (LMControlAreaMsg)obj;
		
		if( msg.isDeletedCntrlArea() ) //we may be deleting an area
		{
			//this should not happen much
			handleDeletedAreas( msg );
		}
		

		for( int i = 0; i < msg.getNumberOfLMControlAreas(); i++ )
			handleLMControlArea( msg.getLMControlArea(i) );
	}
	else if( obj instanceof ServerResponseMsg ) {
//		CTILogger.debug("Received a ServerResponseMsg, ignoring it since I didn't send a request");
	}

}

public void registerMappings(CollectableStreamer streamer ) {
	super.registerMappings( streamer );

	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();

	for( int i = 0; i < mappings.length; i++ )
		streamer.register( mappings[i] );
}


}

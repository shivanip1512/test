package com.cannontech.macs;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * MACS.  Specifically it registers MACS specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.macs.events.MACSCategoryChange;
import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.message.macs.message.Schedule;
import com.roguewave.vsj.CollectableStreamer;

public class MACSClientConnection extends com.cannontech.message.util.ClientConnection
{
	private java.util.Vector messageEventListeners = new java.util.Vector();

	private java.util.Vector schedules = null;

	// This hastTable should contain String keys and ArrayList values
	private java.util.Hashtable categoryNames = null;
/**
 * ClientConnection constructor comment.
 * @param host java.lang.String
 * @param port int
 */
public MACSClientConnection() {
	super();
	initialize();
}
/**
 * ClientConnection constructor comment.
 * @param host java.lang.String
 * @param port int
 */
public MACSClientConnection(String host, int port) {
	super(host, port);

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 10:08:10 AM)
 * @param catName java.lang.String
 */
private void addCategory(Schedule sched) 
{
	if( sched == null )
		return;

	Object o = getCategoryNames().get( sched.getCategoryName() );

	if( o != null )
	{
		// we found this category name to exist, lets just append the new Schedule to the
		// arrayList associated with the categoryName if it does not exist, else
		// we set the preexisting schedule to the new schedule
		try
		{
			java.util.ArrayList list = (java.util.ArrayList)o;
			boolean add = false;
			int i = 0;
			for( i = 0; i < list.size(); i++ )
			{
				if( ((Schedule)list.get(i)).equals(sched) )
				{
					add = false;
					break; //found the schedule, lets get out of here
				}
				else
				{
					add = true;
					continue; //havent found the schedule, lets keep searching
				}				
			}

			if( add ) // the schedule was not found
				list.add( sched );
			else // the schedule is already present, update it
				list.set( i, sched );
				
		}
		catch( ClassCastException e )
		{
			throw new ClassCastException("CategoryName hashTable contained values that are not of type ArrayList");
		}

	}
	else
	{
		java.util.ArrayList list = new java.util.ArrayList(5);
		list.add( sched );

		// store the list
		getCategoryNames().put( sched.getCategoryName(), list );

		 // tell our listeners we have added a new category
		setChanged();
		notifyObservers( new MACSCategoryChange( 
									this, 
									MACSCategoryChange.INSERT, 
									sched.getCategoryName()) );
	}

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
 * Creation date: (2/23/2001 11:21:10 AM)
 */
public void clearSchedules() 
{
	getSchedules().removeAllElements();

	// tell our listeners we have added a new category
	setChanged();
	notifyObservers( new MACSCategoryChange( this,
									MACSCategoryChange.DELETE_ALL,
									"deleteAll") );

	getCategoryNames().clear();
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 10:03:31 AM)
 */
public void doHandleMessage(Object obj)
{
	if( obj instanceof Schedule )
	{
		handleSchedule( (Schedule)obj );
	}
	else if( obj instanceof com.cannontech.message.dispatch.message.Multi )
	{
		handleMulti( (com.cannontech.message.dispatch.message.Multi)obj );
	}
	else if( obj instanceof com.cannontech.message.macs.message.DeleteSchedule )
	{
		handleDeleteSchedule( (com.cannontech.message.macs.message.DeleteSchedule)obj );
	}
	else if( obj instanceof com.cannontech.message.macs.message.Info )
	{
		handleInfo( (com.cannontech.message.macs.message.Info)obj );
	}
	else if( obj instanceof com.cannontech.message.macs.message.ScriptFile )
	{
		handleScriptFile( (com.cannontech.message.macs.message.ScriptFile)obj );
	}
	else
		throw new RuntimeException("Recieved a message of type " + obj.getClass() );
	
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
 * @return com.cannontech.macs.Schedule[]
 */
public Schedule[] getCategories( String category )
{
	synchronized( getSchedules() )
	{
		synchronized( getCategoryNames() )
		{
			java.util.ArrayList list = (java.util.ArrayList)getCategoryNames().get(category);
			Schedule[] sched = new Schedule[list.size()];
			list.toArray( sched );

			return sched;
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 10:03:31 AM)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getCategoryNames() 
{
	if( categoryNames == null )
		categoryNames = new java.util.Hashtable( 40, (float)0.6 );

	return categoryNames;
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
 * Creation date: (2/21/2001 6:17:59 PM)
 * @return java.util.Vector
 */
private java.util.Vector getSchedules() 
{
	if( schedules == null )
		schedules = new java.util.Vector(10);
		
	return schedules;
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 10:37:45 AM)
 * @param multi com.cannontech.message.dispatch.message.Multi
 */
private synchronized void handleDeleteSchedule(com.cannontech.message.macs.message.DeleteSchedule deleted) 
{
	synchronized ( getSchedules() ) 
	{
		for( int j = 0; j < getSchedules().size(); j++ )
		{
			if( ((Schedule)getSchedules().get(j)).getId() == deleted.getScheduleId() )
			{
				removeCategory( (Schedule)getSchedules().get(j) );
				getSchedules().remove( j );
				break;
			}
		}

	}
	
	// tell all listeners that we need to delete a schedule
	setChanged();
	notifyObservers( deleted );
}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 6:03:46 PM)
 * @param sched com.cannontech.macs.Info
 */
private synchronized void handleInfo(com.cannontech.message.macs.message.Info info) 
{
	com.cannontech.clientutils.CTILogger.info("Received an Info msg : " + info.getInfo()  );
	
	// tell all listeners that we received an Info message
	setChanged();
	notifyObservers( info );
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
 * Creation date: (2/23/2001 10:37:45 AM)
 * @param multi com.cannontech.message.dispatch.message.Multi
 */
private void handleMulti(com.cannontech.message.dispatch.message.Multi multi) 
{

	for( int i = 0; i < multi.getVector().size(); i ++ )
	{
		Object obj = multi.getVector().elementAt(i);

		doHandleMessage( obj );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 6:03:46 PM)
 * @param sched com.cannontech.macs.Schedule
 */
private synchronized void handleSchedule(Schedule sched) 
{
	com.cannontech.clientutils.CTILogger.info("Received a schedule named " + sched.getScheduleName() + "/" + sched.getCategoryName() );

	synchronized ( getSchedules() ) 
	{
		boolean found = false;
		for( int j = 0; j < getSchedules().size(); j++ )
		{
			if( ((Schedule)getSchedules().get(j)).equals(sched) )
			{
				// remove the previous category entry for the old version of the schedule
				// if the category name has changed
				if( !((Schedule)getSchedules().get(j)).getCategoryName().equals(sched.getCategoryName()) )
					removeCategory( (Schedule)getSchedules().get(j) );
				
				// the schedule already exists, so just assign it to the new value				
				getSchedules().set( j, sched );
				found = true;
				break;
			}
		}

		addCategory( sched );
			
		if( !found )
			getSchedules().add( sched );
	}

	// tell all listeners that we received a schedule
	setChanged();
	notifyObservers( sched );

}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 6:03:46 PM)
 * @param sched com.cannontech.macs.Info
 */
private synchronized void handleScriptFile(com.cannontech.message.macs.message.ScriptFile file) 
{
	com.cannontech.clientutils.CTILogger.info("Received a ScriptFile " + file.getFileName()  );

	synchronized ( getSchedules() ) 
	{
		boolean found = false;
		for( int j = 0; j < getSchedules().size(); j++ )
		{
			if( ((Schedule)getSchedules().get(j)).getScriptFileName().equalsIgnoreCase(file.getFileName()) )
			{
				// if this schedule uses the newly received script file name, update its values
				((Schedule)getSchedules().get(j)).getNonPersistantData().setScript( file );
			}
		}

	}

	
	// tell all listeners that we received a Info message
	setChanged();
	notifyObservers( file );
}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 5:56:32 PM)
 */
private void initialize() 
{
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
 * Creation date: (2/23/2001 10:08:10 AM)
 * @param catName java.lang.String
 */
private void removeCategory(Schedule sched)
{
	if( sched == null )
		return;
		
	Object o = getCategoryNames().get( sched.getCategoryName() );

	if( o != null )
	{
		// we found this category name to exist, lets just remove the
		// schedule in the arrayList associated with the categoryName
		try
		{
			java.util.ArrayList list = (java.util.ArrayList)o;
			list.remove( sched );

			// if there are no more schedules that have this category name, lets delete this category
			if( list.size() <= 0 )
			{
				getCategoryNames().remove( sched.getCategoryName() );

				// tell our listeners we have added a new category
				setChanged();
				notifyObservers( new MACSCategoryChange( this,
												MACSCategoryChange.DELETE, 
												sched.getCategoryName()) );
			}
			
		}
		catch( ClassCastException e )
		{
			throw new ClassCastException("CategoryName hashTable contained values that are not of type ArrayList");
		}

	}
	
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.macs.Schedule[]
 */
public Schedule[] retrieveSchedules()
{	
	try
	{
		synchronized( getSchedules() )
		{
			Schedule[] scheds = new Schedule[ getSchedules().size() ];
			getSchedules().toArray( scheds );
			return scheds;
		}
	}
	catch( ArrayStoreException e )
	{
		throw e;
	}
	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param sched com.cannontech.macs.Schedule
 * @exception java.io.IOException The exception description.
 */
public void sendCreateSchedule(Schedule sched) throws java.io.IOException 
{

	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.AddSchedule newSchedule = new com.cannontech.message.macs.message.AddSchedule();
	newSchedule.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	newSchedule.setSchedule( sched );
	newSchedule.setScript( sched.getNonPersistantData().getScript().getFileContents() );
	
	write( newSchedule );
	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param sched com.cannontech.macs.Schedule
 */
public void sendDeleteSchedule(int scheduleID) throws java.io.IOException
{
	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.DeleteSchedule sched = new com.cannontech.message.macs.message.DeleteSchedule();
	sched.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	sched.setScheduleId( scheduleID );
	
	write( sched );
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param sched com.cannontech.macs.Schedule
 */
public void sendEnableDisableSchedule(Schedule sched) throws java.io.IOException
{
	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.OverrideRequest request = new com.cannontech.message.macs.message.OverrideRequest();
	request.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	request.setSchedId( sched.getId() );
	
	if( sched.getCurrentState().equalsIgnoreCase( Schedule.STATE_DISABLED ) )
	{
		request.setAction( com.cannontech.message.macs.message.OverrideRequest.OVERRIDE_ENABLE );
		request.setStart( new java.util.Date() ); //current time
	}
	else
	{
		request.setAction( com.cannontech.message.macs.message.OverrideRequest.OVERRIDE_DISABLE );
		request.setStop( new java.util.Date() ); //current time
	}
	
	write( request );
}
/**
 * This method was created in VisualAge.
 * @param sched com.cannontech.macs.Schedule
 * @exception java.io.IOException The exception description.
 */
public void sendRetrieveAllSchedules() throws java.io.IOException 
{
	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.RetrieveSchedule newSchedules = new com.cannontech.message.macs.message.RetrieveSchedule();
	newSchedules.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	newSchedules.setScheduleId( com.cannontech.message.macs.message.RetrieveSchedule.ALL_SCHEDULES );

	write( newSchedules );
}
/**
 * This method was created in VisualAge.
 * @param sched com.cannontech.macs.Schedule
 * @exception java.io.IOException The exception description.
 */
public void sendRetrieveOneSchedule( int schedId ) throws java.io.IOException 
{
	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.RetrieveSchedule schedule = new com.cannontech.message.macs.message.RetrieveSchedule();
	schedule.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	schedule.setScheduleId( schedId );

	write( schedule );
}
/**
 * Insert the method's description here.
 * Creation date: (3/17/00 2:25:59 PM)
 * @return com.cannontech.macs.CommandFile
 * @param sched com.cannontech.macs.Schedule
 */
public void sendRetrieveScriptText(String scriptFileName) throws java.io.IOException
{
	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.RetrieveScript request = new com.cannontech.message.macs.message.RetrieveScript();
	request.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	request.setScriptName( scriptFileName );
	
	write( request );
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 5:15:49 PM)
 * @param file com.cannontech.message.macs.message.ScriptFile
 */
public void sendScriptFile(com.cannontech.message.macs.message.ScriptFile file) throws java.io.IOException
{
	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.ScriptFile script = new com.cannontech.message.macs.message.ScriptFile();
	script.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	script.setFileName( file.getFileName() );
	script.setFileContents( file.getFileContents() );
	
	write( script );
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param sched com.cannontech.macs.Schedule
 */
public void sendStartStopSchedule(Schedule sched, java.util.Date startTime, java.util.Date stopTime, int command ) throws java.io.IOException
{
	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	/* Strange behavior here.  If we have to send a start AND stop time, */
	/*  we must send two seperate messages to the server? Server fix later. */

	// send the first message
	OverrideRequest request = new OverrideRequest();
	request.setSchedId( sched.getId() );
	request.setAction( command );
	
	if( startTime != null )
		request.setStart( startTime );

	if( stopTime != null )
		request.setStop( stopTime );		
		
	write( request );

	// send the second message if needed
	if( stopTime != null 
		 && (request.getAction() == OverrideRequest.OVERRIDE_START
		    || request.getAction() == OverrideRequest.OVERRIDE_START_NOW) )
	{
		OverrideRequest secondRequest = new OverrideRequest();
		secondRequest.setSchedId( request.getSchedId() );
		
		secondRequest.setStart( request.getStart() );
		secondRequest.setStop( request.getStop() );			
		secondRequest.setAction( OverrideRequest.OVERRIDE_STOP );

		write( secondRequest );
	}

}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param sched com.cannontech.macs.Schedule
 * @exception java.io.IOException The exception description.
 */
public void sendUpdateSchedule(Schedule sched ) throws java.io.IOException 
{

	if( !(isValid()) )
		throw new java.io.IOException("Not connected to MACSServer.");

	com.cannontech.message.macs.message.UpdateSchedule modifiedSchedule = new com.cannontech.message.macs.message.UpdateSchedule();
	modifiedSchedule.setTimeStamp( new java.util.Date() );
	modifiedSchedule.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	modifiedSchedule.setSchedule( sched );
	modifiedSchedule.setScript( sched.getNonPersistantData().getScript().getFileContents() );
	
	write( modifiedSchedule );	
}
}

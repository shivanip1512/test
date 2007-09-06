package com.cannontech.loadcontrol;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * LoadControl.  Specifically it registers LoadControl specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import java.util.HashMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.dynamic.receive.LMControlAreaChanged;
import com.cannontech.loadcontrol.events.LCChangeEvent;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.messages.LMControlAreaMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.roguewave.vsj.CollectableStreamer;

public class LoadControlClientConnection extends com.cannontech.message.util.ClientConnection implements MessageListener {
	private static LoadControlClientConnection staticLoadControlClientConnection = null;
	
	private HashMap<Integer, LMControlArea> controlAreas = null;

    protected LoadControlClientConnection() {
    	super("LC");
    	initialize();
    } 

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
    		LoadControlClientConnection.getInstance().queue( cmd );
    	}
    	
    }
    /**
     * Insert the method's description here.
     * Creation date: (2/23/2001 11:21:10 AM)
     */
    public void clearControlAreas() 
    {
    	getControlAreas().clear();
    
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
    public void disconnect()
    {
    	super.disconnect();
    
    	deleteObservers();
    
    	staticLoadControlClientConnection = null;
    }
    
    private void handleDeletedAreas( LMControlAreaMsg msg ) {
        for( int j = 0; j  < msg.getNumberOfLMControlAreas(); j++ ) {
            getControlAreas().remove(msg.getLMControlArea(j).getYukonID());
    		setChanged();
    		notifyObservers( new LCChangeEvent(this, LCChangeEvent.DELETE, msg.getLMControlArea(j)) );	
    	}
    }
    
    public LMControlArea[] getAllLMControlAreas() {	
		synchronized( getControlAreas() ) {
			LMControlArea[] areas = new LMControlArea[ getControlAreas().size() ];
			getControlAreas().values().toArray( areas );
			return areas;
		}
    }
    
    public int getControlAreaCount() {
    	return getControlAreas().size();
    }
    
    private HashMap<Integer, LMControlArea> getControlAreas() {
    	if( controlAreas == null )
    		controlAreas = new HashMap<Integer, LMControlArea>();
    		
    	return controlAreas;
    }
    
    public synchronized static LoadControlClientConnection getInstance() {
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
    	synchronized( getControlAreas() ) {
    		for( int i = 0; i < getControlAreaCount(); i++ ) {
    			if( lmControlAreaID == ((LMControlArea)getControlAreas().get(i)).getYukonID().intValue() )
    				return (LMProgramBase[])( ((LMControlArea)getControlAreas().get(i)).getLmProgramVector().toArray() );
    		}
    
    		return null;
    	}
    }
    
    private synchronized void handleLMControlArea(LMControlArea controlArea) {
    	CTILogger.debug( " ---> Received a control area named " + controlArea.getYukonName() );
    
        //could use some of the new concurrency code here to be fancy, but for now
    	synchronized ( getControlAreas() ) {
    		boolean newInsert = getControlAreas().get(controlArea.getYukonID()) == null;
            
            getControlAreas().put( controlArea.getYukonID(), controlArea );
    				
    		// tell all listeners that we received an updated LMControlArea
    		setChanged();
    		notifyObservers( new LCChangeEvent(	this, (newInsert ? LCChangeEvent.INSERT : LCChangeEvent.UPDATE), controlArea) );				
    		return;
    	}
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (2/21/2001 5:56:32 PM)
     */
    private void initialize() {
    	addMessageListener( this );
        LMCommand cmd = new LMCommand();
        cmd.setCommand( LMCommand.RETRIEVE_ALL_CONTROL_AREAS );
        setRegistrationMsg(cmd);
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (7/30/2001 4:05:08 PM)
     * @return boolean
     */
    public boolean needInitConn() {
    	//return true if there hasn't been any Observers set to watch this connection
    	return (countObservers() <= 0);
    }
    
    public synchronized void removeClient(Object client) {
    	if( client instanceof java.util.Observer )
    		deleteObserver( (java.util.Observer)client );
    
    	if( countObservers() == 0 ) {
        	super.disconnect();
        	staticLoadControlClientConnection = null;
        }
    }
    
    public synchronized void messageReceived( MessageEvent e ) {
    	Object obj = e.getMessage();
    	if( obj instanceof LMControlArea ) {
    		handleLMControlArea( (LMControlArea)obj );
    	}
        /**
         * The server only sends this type of message for minor control area changes
         * This helps prevent heavy messages constantly flowing on every little change
         */
        else if( obj instanceof LMControlAreaChanged ) {
            handleLMControlAreaChange((LMControlAreaChanged)obj);
        }
    	else if( obj instanceof LMControlAreaMsg ) {
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
    	    //CTILogger.debug("Received a ServerResponseMsg, ignoring it since I didn't send a request");
    	}
    
    }
    
    public void registerMappings(CollectableStreamer streamer ) {
    	super.registerMappings( streamer );
    
    	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();
    
    	for( int i = 0; i < mappings.length; i++ )
    		streamer.register( mappings[i] );
    }
    
    private synchronized void handleLMControlAreaChange(LMControlAreaChanged changedArea) {
        synchronized ( getControlAreas() ) {
            LMControlArea currentArea = getControlAreas().get( changedArea.getPaoID());
            
            currentArea.setDisableFlag(changedArea.getDisableFlag());
            currentArea.setNextCheckTime(changedArea.getNextCheckTime());
            currentArea.setControlAreaState(changedArea.getControlAreaState());
            currentArea.setCurrentPriority(changedArea.getCurrentPriority());
            currentArea.setCurrentDailyStartTime(changedArea.getCurrentDailyStartTime());
            currentArea.setCurrentDailyStopTime(changedArea.getCurrentDailyStopTime());
            // tell all listeners that we received an updated LMControlArea
            setChanged();
            notifyObservers( new LCChangeEvent( this, LCChangeEvent.UPDATE, currentArea) );                
            return;
        }
    }

}

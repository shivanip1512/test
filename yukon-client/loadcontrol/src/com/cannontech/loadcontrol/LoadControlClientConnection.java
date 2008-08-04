package com.cannontech.loadcontrol;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * LoadControl.  Specifically it registers LoadControl specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import java.util.HashMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.dynamic.receive.LMControlAreaChanged;
import com.cannontech.loadcontrol.dynamic.receive.LMGroupChanged;
import com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged;
import com.cannontech.loadcontrol.dynamic.receive.LMTriggerChanged;
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
    private HashMap<Integer, LMProgramBase> programs = null;
    private HashMap<Integer, LMGroupBase> groups = null;
    private HashMap<Integer, LMControlAreaTrigger> triggers = null;

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
    	getGroups().clear();
        getPrograms().clear();
        getTriggers().clear();
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
            //TODO: Is this truly necessary?  If it is should probably find a better way to do this.
            for(int i = 0; i < msg.getLMControlArea(j).getLmProgramVector().size(); i++) {
                LMProgramBase currentProgram = (LMProgramBase)msg.getLMControlArea(j).getLmProgramVector().get(i);
                for(int x = 0; x < currentProgram.getLoadControlGroupVector().size(); x++) { 
                    getGroups().remove(((LMGroupBase)currentProgram.getLoadControlGroupVector().get(x)).getYukonID());
                }
                getPrograms().remove(currentProgram.getYukonID());
            }
            
            for(int k = 0; k < msg.getLMControlArea(j).getTriggerVector().size(); k++) {
                getTriggers().remove(((LMControlAreaTrigger)msg.getLMControlArea(j).getTriggerVector().get(k)).getYukonID());
            }
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
    
    public HashMap<Integer, LMControlArea> getControlAreas() {
    	if( controlAreas == null )
    		controlAreas = new HashMap<Integer, LMControlArea>();
    		
    	return controlAreas;
    }
    
    public HashMap<Integer, LMProgramBase> getPrograms() {
        if( programs == null )
            programs = new HashMap<Integer, LMProgramBase>();
            
        return programs;
    }
    
    public HashMap<Integer, LMGroupBase> getGroups() {
        if( groups == null )
            groups = new HashMap<Integer, LMGroupBase>();
            
        return groups;
    }
    
    public HashMap<Integer, LMControlAreaTrigger> getTriggers() {
        if( triggers == null )
            triggers = new HashMap<Integer, LMControlAreaTrigger>();
            
        return triggers;
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
    			if( lmControlAreaID == getControlAreas().get(i).getYukonID().intValue() )
    				return (LMProgramBase[])( getControlAreas().get(i).getLmProgramVector().toArray() );
    		}
    
    		return null;
    	}
    }
    
    private synchronized void handleLMControlArea(LMControlArea controlArea) {
    	CTILogger.debug( " ---> Received a control area named " + controlArea.getYukonName() );
    
        //could use some of the new concurrency code here to be fancy, but for now...
    	synchronized ( getControlAreas() ) {
    		boolean newInsert = getControlAreas().get(controlArea.getYukonID()) == null;
            
            getControlAreas().put( controlArea.getYukonID(), controlArea );
    		/*
            getPrograms().clear();
            getGroups().clear();
            getTriggers().clear();*/
            
            /*Build up hashMaps of references for all these different objects, so we don't have
             *to iterate so much later when the new dynamic update messages come through. 
    		 */
            for(int i = 0; i < controlArea.getLmProgramVector().size(); i++) {
                LMProgramBase currentProgram = (LMProgramBase)controlArea.getLmProgramVector().get(i);
                for(int j = 0; j < currentProgram.getLoadControlGroupVector().size(); j++) { 
                    getGroups().put(((LMGroupBase)currentProgram.getLoadControlGroupVector().get(j)).getYukonID(), (LMGroupBase)currentProgram.getLoadControlGroupVector().get(j));
                }
                getPrograms().put(currentProgram.getYukonID(), currentProgram);
            }
            
            for(int k = 0; k < controlArea.getTriggerVector().size(); k++) {
                getTriggers().put(((LMControlAreaTrigger)controlArea.getTriggerVector().get(k)).getYukonID(), (LMControlAreaTrigger)controlArea.getTriggerVector().get(k));
            }
            
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
        else if( obj instanceof LMProgramChanged ) {
            handleLMProgramChange((LMProgramChanged)obj);
        }
        else if( obj instanceof LMGroupChanged ) {
            handleLMGroupChange((LMGroupChanged)obj);
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
            
            for(LMTriggerChanged changedTrigger : changedArea.getTriggers()) {
                handleLMTriggerChange(changedTrigger);
            }
            
            // tell all listeners that we received an updated LMControlArea
            setChanged();
            notifyObservers( new LCChangeEvent( this, LCChangeEvent.UPDATE, currentArea) );                
            return;
        }
    }
    
    private synchronized void handleLMProgramChange(LMProgramChanged changedProgram) {
        synchronized ( getPrograms() ) {
            LMProgramBase currentProgram = getPrograms().get( changedProgram.getPaoID());
            
            currentProgram.setDisableFlag(changedProgram.getDisableFlag());
            if(currentProgram instanceof LMProgramDirect) {
                ((LMProgramDirect)currentProgram).setCurrentGearNumber(changedProgram.getCurrentGearNumber()); 
                ((LMProgramDirect)currentProgram).setLastGroupControlled(changedProgram.getLastGroupControlled());
                ((LMProgramDirect)currentProgram).setProgramStatus(changedProgram.getProgramState());
                ((LMProgramDirect)currentProgram).setReductionTotal(changedProgram.getReductionTotal());
                ((LMProgramDirect)currentProgram).setDirectStartTime(changedProgram.getDirectStartTime());
                ((LMProgramDirect)currentProgram).setDirectStopTime(changedProgram.getDirectStopTime());
                ((LMProgramDirect)currentProgram).setNotifyActiveTime(changedProgram.getNotifyActiveTime());
                ((LMProgramDirect)currentProgram).setNotifyInactiveTime(changedProgram.getNotifyInactiveTime());
                ((LMProgramDirect)currentProgram).setStartedRampingOut(changedProgram.getStartedRampingOutTime());
            }
            // tell all listeners that we had an update
            setChanged();
            //TODO: should this be the program passed in or the control area on the update event?
            notifyObservers( new LCChangeEvent( this, LCChangeEvent.UPDATE, currentProgram) );                
            return;
        }
    }
    
    private synchronized void handleLMGroupChange(LMGroupChanged changedGroup) {
        synchronized ( getGroups() ) {
            LMGroupBase currentGroup = getGroups().get( changedGroup.getPaoID());
            
            if(currentGroup instanceof LMDirectGroupBase) {
                ((LMDirectGroupBase)currentGroup).setDisableFlag(changedGroup.getDisableFlag());
                ((LMDirectGroupBase)currentGroup).setGroupControlState(changedGroup.getGroupControlState());
                ((LMDirectGroupBase)currentGroup).setCurrentHoursDaily(changedGroup.getCurrentHoursDaily());
                ((LMDirectGroupBase)currentGroup).setCurrentHoursMonthly(changedGroup.getCurrentHoursMonthly());
                ((LMDirectGroupBase)currentGroup).setCurrentHoursSeasonal(changedGroup.getCurrentHoursSeasonal());
                ((LMDirectGroupBase)currentGroup).setCurrentHoursAnnually(changedGroup.getCurrentHoursAnnually());
                ((LMDirectGroupBase)currentGroup).setLastControlSent(changedGroup.getLastControlSent());
                ((LMDirectGroupBase)currentGroup).setControlStartTime(changedGroup.getControlStartTime().getTime());
                ((LMDirectGroupBase)currentGroup).setControlCompleteTime(changedGroup.getControlCompleteTime().getTime());
                ((LMDirectGroupBase)currentGroup).setNextControlTime(changedGroup.getNextControlTime().getTime());
                ((LMDirectGroupBase)currentGroup).setInternalState(changedGroup.getInternalState());
                ((LMDirectGroupBase)currentGroup).setDailyOps(changedGroup.getDailyOps());
            }
            // tell all listeners that we received an update
            setChanged();
            //TODO: should this be the group passed in or the control area on the update event?
            notifyObservers( new LCChangeEvent( this, LCChangeEvent.UPDATE, currentGroup) );                
            return;
        }
    }
    
    private synchronized void handleLMTriggerChange(LMTriggerChanged changedTrigger) {
        synchronized ( getTriggers() ) {
            LMControlAreaTrigger currentTrigger = getTriggers().get( changedTrigger.getPaoID());
            
            if(currentTrigger != null) {
                currentTrigger.setTriggerNumber(changedTrigger.getTriggerNumber());
                currentTrigger.setPointValue(changedTrigger.getPointValue());
                currentTrigger.setLastPointValueTimeStamp(changedTrigger.getLastPointValueTimestamp().getTime());
                currentTrigger.setNormalState(changedTrigger.getNormalState());
                currentTrigger.setThreshold(changedTrigger.getThreshold());
                currentTrigger.setPeakPointValue(changedTrigger.getPeakPointValue());
                currentTrigger.setLastPeakPointValueTimeStamp(changedTrigger.getLastPeakPointValueTimestamp().getTime());
                currentTrigger.setProjectedPointValue(changedTrigger.getProjectedPointValue());
                // tell all listeners that we received an update
                setChanged();
                //TODO: should this be the trigger passed in or the control area on the update event?
                notifyObservers( new LCChangeEvent( this, LCChangeEvent.UPDATE, currentTrigger) );   
            }
            return;
        }
    }
}

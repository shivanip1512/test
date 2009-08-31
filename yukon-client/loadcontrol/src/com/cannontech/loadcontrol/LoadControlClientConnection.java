package com.cannontech.loadcontrol;

/**
 * ClientConnection adds functionality necessary to handles connections with
 * LoadControl.  Specifically it registers LoadControl specific 'Collectable' messages, otherwise
 * the base class does all the work.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.DatedObject;
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
import com.cannontech.loadcontrol.messages.LMControlAreaMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.roguewave.vsj.CollectableStreamer;

public class LoadControlClientConnection extends com.cannontech.message.util.ClientConnection implements MessageListener {
	
	private Map<Integer, DatedObject<LMControlArea>> controlAreas = new ConcurrentHashMap<Integer, DatedObject<LMControlArea>>();
    private Map<Integer, DatedObject<LMProgramBase>> programs = new ConcurrentHashMap<Integer, DatedObject<LMProgramBase>>();
    private Map<Integer, DatedObject<LMGroupBase>> groups = new ConcurrentHashMap<Integer, DatedObject<LMGroupBase>>();
    private Map<Integer, DatedObject<LMControlAreaTrigger>> triggers = new ConcurrentHashMap<Integer, DatedObject<LMControlAreaTrigger>>();

    protected LoadControlClientConnection() {
    	super("LC");
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
    		queue( cmd );
    	}
    	
    }
    /**
     * Insert the method's description here.
     * Creation date: (2/23/2001 11:21:10 AM)
     */
    public void clearControlAreas() 
    {
        groups.clear();
    	programs.clear();
    	triggers.clear();
        controlAreas.clear();
    
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
    }
    
    private void handleDeletedAreas( LMControlAreaMsg msg ) {
        for( int j = 0; j  < msg.getNumberOfLMControlAreas(); j++ ) {
            //TODO: Is this truly necessary?  If it is should probably find a better way to do this.
            for(int i = 0; i < msg.getLMControlArea(j).getLmProgramVector().size(); i++) {
                LMProgramBase currentProgram = (LMProgramBase)msg.getLMControlArea(j).getLmProgramVector().get(i);
                for(int x = 0; x < currentProgram.getLoadControlGroupVector().size(); x++) { 
                    groups.remove(((LMGroupBase)currentProgram.getLoadControlGroupVector().get(x)).getYukonID());
                }
                programs.remove(currentProgram.getYukonID());
            }
            
            for(int k = 0; k < msg.getLMControlArea(j).getTriggerVector().size(); k++) {
                triggers.remove(((LMControlAreaTrigger)msg.getLMControlArea(j).getTriggerVector().get(k)).getYukonID());
            }
            controlAreas.remove(msg.getLMControlArea(j).getYukonID());
    		setChanged();
    		notifyObservers( new LCChangeEvent(this, LCChangeEvent.DELETE, msg.getLMControlArea(j)) );	
    	}
    }

    @Deprecated
    public LMControlArea[] getAllLMControlAreas() {
        LMControlArea[] retVal = new LMControlArea[controlAreas.size()];
        int index = 0;
        for (DatedObject<LMControlArea> datedControlArea : controlAreas.values()) {
            retVal[index++] = datedControlArea.getObject();
        }
        return retVal;
    }
    
    /**
     * Returns the control area that a program belongs to.
     * If the program does not belong to any control area, null is returned.
     * @param programId
     * @return
     */
    public LMControlArea findControlAreaForProgram(int programId) {
    	
    	LMControlArea[] allControlAreas = getAllLMControlAreas();
    	for (LMControlArea controlArea : allControlAreas) {
    		Vector<LMProgramBase> programs = controlArea.getLmProgramVector();
    		for (LMProgramBase program : programs) {
    			if (program.getYukonID().intValue() == programId) {
    				return controlArea;
    			}
    		}
    	}
    	
    	return null;
    }
    
    public int getControlAreaCount() {
    	return controlAreas.size();
    }
    
    public Map<Integer, LMControlArea> getControlAreas() {
    	return unwrapDatedMap(controlAreas);
    }
    
    /**
	 * @deprecated - The returned map may be out of date because it cannot be
	 *             properly updated when a LM program is deleted. See the note in
	 *             the handleLMControlArea() method on this class.
	 */
    public Map<Integer, LMProgramBase> getPrograms() {
        return unwrapDatedMap(programs);
    }
    
    /**
     * @deprecated - The returned map may be out of date because it cannot be
     *             properly updated when a LM program or group is deleted. See the 
     *             note in the handleLMControlArea() method on this class.
     */
    public Map<Integer, LMGroupBase> getGroups() {
        return unwrapDatedMap(groups);
    }
    
    /**
     * @deprecated - The returned map may be out of date because it cannot be
     *             properly updated when a trigger is deleted. See the note in
	 *             the handleLMControlArea() method on this class.
     */
    public Map<Integer, LMControlAreaTrigger> getTriggers() {
        return unwrapDatedMap(triggers);
    }

    /**
     * @return an already connected clientConnection
     * @deprecated code should use Spring injection, or if absolutely necessary, YukonSpringHook directly
     */
    @Deprecated
    public static LoadControlClientConnection getInstance() {
    	LoadControlClientConnection clientConnection = YukonSpringHook.getBean("loadControlClientConnection", LoadControlClientConnection.class);
    	return clientConnection;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (4/6/2001 9:11:31 AM)
     * @return com.cannontech.loadcontrol.LoadControlProgram[]
     * @param lmControlAreaID int
     */
    public LMProgramBase getProgram(int programId) 
    {
	    LMProgramBase program = null;
	    
        for (DatedObject<LMControlArea> controlArea : controlAreas.values()) {
        	List<LMProgramBase> programs = controlArea.getObject().getLmProgramVector();
            for(LMProgramBase p : programs) {
                if (p.getYukonID() == programId) {
                    program = p;
                }
            }
        }
		return program;
    }

    public DatedObject<LMProgramBase> getDatedProgram(int programId) {
        return programs.get(programId);
    }

    public List<LMProgramBase> getProgramsForProgramIds(List<Integer> programIds) {
        
        List<LMProgramBase> programs = new ArrayList<LMProgramBase>(programIds.size());
        for (int programId : programIds) {
            LMProgramBase program = getProgram(programId);
            if (program != null) {
                programs.add(program);
            }
        }
        
        return programs;
    }


    private void handleLMControlArea(LMControlArea controlArea) {
    	CTILogger.debug( " ---> Received a control area named " + controlArea.getYukonName() );
    
        //could use some of the new concurrency code here to be fancy, but for now...
		boolean newInsert = controlAreas.put(controlArea.getYukonID(),
		                                      new DatedObject<LMControlArea>(controlArea)) == null;

        /*Build up hashMaps of references for all these different objects, so we don't have
         *to iterate so much later when the new dynamic update messages come through.
         *
         * NOTE: The for loops below do NOT remove deleted items which causes the 
         * group, program and trigger maps to be potentially out of date. Updates are handled
         * correctly but deletes are not.  There is no easy fix since we only get the already 
         * updated controlArea as input to this method so we cannot determine what (if anything) 
         * has been deleted.  This is why the methods to get those maps have been deprecated.
		 */
        for (int i = 0; i < controlArea.getLmProgramVector().size(); i++) {
            LMProgramBase currentProgram = (LMProgramBase) controlArea.getLmProgramVector()
                                                                      .get(i);
            for (int j = 0; j < currentProgram.getLoadControlGroupVector()
                                              .size(); j++) {
                LMGroupBase group = (LMGroupBase) currentProgram.getLoadControlGroupVector().get(j);
                groups.put(group.getYukonID(), new DatedObject<LMGroupBase>(group));
            }
            programs.put(currentProgram.getYukonID(), new DatedObject<LMProgramBase>(currentProgram));
        }
        
        for (int k = 0; k < controlArea.getTriggerVector().size(); k++) {
            LMControlAreaTrigger controlAreaTrigger = (LMControlAreaTrigger) controlArea.getTriggerVector().get(k);
            triggers.put(controlAreaTrigger.getYukonID(), new DatedObject<LMControlAreaTrigger>(controlAreaTrigger));
        }
            
    	// tell all listeners that we received an updated LMControlArea
    	setChanged();
    	notifyObservers( new LCChangeEvent(	this, (newInsert ? LCChangeEvent.INSERT : LCChangeEvent.UPDATE), controlArea) );				
    	return;
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
    
    public void messageReceived( MessageEvent e ) {
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
    
    private void handleLMControlAreaChange(LMControlAreaChanged changedArea) {
        DatedObject<LMControlArea> datedArea = controlAreas.get(changedArea.getPaoID());
        LMControlArea currentArea = datedArea.getObject();
        datedArea.touch();
        
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
    
    private void handleLMProgramChange(LMProgramChanged changedProgram) {
        DatedObject<LMProgramBase> datedProgram = programs.get(changedProgram.getPaoID());
    	LMProgramBase currentProgram = datedProgram.getObject();
    	datedProgram.touch();

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
    
    private void handleLMGroupChange(LMGroupChanged changedGroup) {
        DatedObject<LMGroupBase> datedLoadGroup = groups.get(changedGroup.getPaoID());
    	LMGroupBase currentGroup = datedLoadGroup.getObject();
    	datedLoadGroup.touch();

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
    
    private void handleLMTriggerChange(LMTriggerChanged changedTrigger) {
        DatedObject<LMControlAreaTrigger> datedTrigger = triggers.get(changedTrigger.getPaoID());
    	LMControlAreaTrigger currentTrigger = datedTrigger.getObject();
    	datedTrigger.touch();

        if(currentTrigger != null) {
            currentTrigger.setTriggerNumber(changedTrigger.getTriggerNumber());
            currentTrigger.setPointValue(changedTrigger.getPointValue());
            currentTrigger.setLastPointValueTimeStamp(changedTrigger.getLastPointValueTimestamp().getTime());
            currentTrigger.setNormalState(changedTrigger.getNormalState());
            currentTrigger.setThreshold(changedTrigger.getThreshold());
            currentTrigger.setPeakPointValue(changedTrigger.getPeakPointValue());
            currentTrigger.setLastPeakPointValueTimeStamp(changedTrigger.getLastPeakPointValueTimestamp().getTime());
            currentTrigger.setProjectedPointValue(changedTrigger.getProjectedPointValue());
            
        }
        
        // tell all listeners that we received an update
        setChanged();
        //TODO: should this be the trigger passed in or the control area on the update event?
        notifyObservers( new LCChangeEvent( this, LCChangeEvent.UPDATE, currentTrigger) ); 
        return;
    }

    // temporary method
    @Deprecated
    private <T> Map<Integer, T> unwrapDatedMap(Map<Integer, DatedObject<T>> datedMap) {
        Map<Integer, T> retVal = new HashMap<Integer, T>(datedMap.size());
        for (Map.Entry<Integer, DatedObject<T>> entry : datedMap.entrySet()) {
            retVal.put(entry.getKey(), entry.getValue().getObject());
        }
        return retVal;
    }
}

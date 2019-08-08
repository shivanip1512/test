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
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
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
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class LoadControlClientConnection extends com.cannontech.message.util.ClientConnection implements MessageListener {
    private final Logger log = YukonLogManager.getLogger(LoadControlClientConnection.class);
	
	private Map<Integer, DatedObject<LMControlArea>> controlAreas = new ConcurrentHashMap<Integer, DatedObject<LMControlArea>>();
    private Map<Integer, DatedObject<LMProgramBase>> programs = new ConcurrentHashMap<Integer, DatedObject<LMProgramBase>>();
    private Map<Integer, DatedObject<LMGroupBase>> groups = new ConcurrentHashMap<Integer, DatedObject<LMGroupBase>>();

    private Map<Integer, Integer> controlAreaByProgram = Maps.newHashMap();
    private Multimap<Integer, Integer> programsByLoadGroup = HashMultimap.create();

    protected LoadControlClientConnection() {
    	super("LC");
    } 
    
    public void addObserver(Observer obs) {
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

    @PreDestroy
    public void disconnect()
    {
    	super.disconnect();
    
    	deleteObservers();
    }
    
    private void handleDeletedItems(LMControlAreaMsg msg) {
        // When a message comes in with the "deleted" flag set, it contains all
        // of the control areas EXCEPT the deleted one(s).
        Set<Integer> controlAreasToKeep = Sets.newHashSet();
        Set<Integer> programsToKeep = Sets.newHashSet();
        Set<Integer> loadGroupsToKeep = Sets.newHashSet();

        for (LMControlArea controlArea : msg.getLMControlAreaVector()) {
            controlAreasToKeep.add(controlArea.getYukonID());
            for (LMProgramBase program : controlArea.getLmProgramVector()) {
                programsToKeep.add(program.getYukonID());
                for (LMGroupBase group : program.getLoadControlGroupVector()) {
                    loadGroupsToKeep.add(group.getYukonID());
                }
            }
        }

        for (Integer controlAreaId : controlAreas.keySet()) {
            if (!controlAreasToKeep.contains(controlAreaId)) {
                DatedObject<LMControlArea> removed = controlAreas.remove(controlAreaId);
                setChanged();
                notifyObservers(new LCChangeEvent(this,
                                                  LCChangeEvent.DELETE,
                                                  removed.getObject()));
            }
        }

        for (Integer programId : programs.keySet()) {
            if (!programsToKeep.contains(programId)) {
                DatedObject<LMProgramBase> removed = programs.remove(programId);
                setChanged();
                notifyObservers(new LCChangeEvent(this,
                                                  LCChangeEvent.DELETE,
                                                  removed.getObject()));
            }
        }

        for (Integer loadGroupId : groups.keySet()) {
            if (!loadGroupsToKeep.contains(loadGroupId)) {
                DatedObject<LMGroupBase> removed = groups.remove(loadGroupId);
                setChanged();
                notifyObservers(new LCChangeEvent(this,
                                                  LCChangeEvent.DELETE,
                                                  removed.getObject()));
            }
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
    
    public LMControlArea getControlArea(int controlAreaId) {
        LMControlArea controlArea = null;
        DatedObject<LMControlArea> datedControlArea = controlAreas.get(controlAreaId);
        if (datedControlArea != null) {
            controlArea = datedControlArea.getObject();
        }
        return controlArea;
    }
    
    /**
     * Returns the control area that a program belongs to.
     * If the program does not belong to any control area, null is returned.
     * @param programId
     * @return
     */
    public LMControlArea findControlAreaForProgram(int programId) {
        Integer controlAreaId = controlAreaByProgram.get(programId);
        if(controlAreaId == null) {
            return null;
        }
        DatedObject<LMControlArea> datedControlArea = controlAreas.get(controlAreaId);
        return datedControlArea == null ? null : datedControlArea.getObject();
    }
    
    public int getControlAreaCount() {
    	return controlAreas.size();
    }
    
    @Deprecated
    public Map<Integer, LMControlArea> getControlAreas() {
    	return unwrapDatedMap(controlAreas);
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

    public LMProgramBase getProgram(int programId) 
    {
        DatedObject<LMProgramBase> datedProgram = programs.get(programId);
        return datedProgram == null ? null : datedProgram.getObject();
    }
    
    /**
     * Returns a LMProgramBase object for the given program id. Throws if the connection is not valid
     * or if the program cannot be found based on the id.
     * 
     * @throws ConnectionException
     * @throws NotFoundException
     */
    public LMProgramBase getProgramSafe(int programId) throws ConnectionException, NotFoundException
    {
        if(!isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }
        
        DatedObject<LMProgramBase> datedProgram = programs.get(programId);
        
        if(datedProgram == null) {
            throw new NotFoundException("The requested program with id " + programId + " was not found.");
        } else {
            return datedProgram.getObject();
        }
    }
    
    /**
     * Returns an ImmutableSet of all LMProgramBase objects. Throws ConnectionException if the connection is not valid.
     *
     * @throws ConnectionException
     */
    public Set<LMProgramBase> getAllProgramsSet() throws ConnectionException {
        
        if(!isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }
    	
    	List<DatedObject<LMProgramBase>> datedPrograms = new ArrayList<DatedObject<LMProgramBase>>(programs.values());
    	
    	List<LMProgramBase> programs = Lists.transform(datedPrograms, new Function<DatedObject<LMProgramBase>, LMProgramBase>() {
								    		@Override
								    		public LMProgramBase apply(DatedObject<LMProgramBase> from) {
								    			return from.getObject();
								    		}
										});
    	
    	ImmutableSet<LMProgramBase> programSet = ImmutableSet.copyOf(programs);
    	
        return programSet;
    }

    public List<LMProgramBase> getProgramsForProgramIds(List<Integer> programIds) throws ConnectionException,
            NotFoundException {

        List<LMProgramBase> programs = new ArrayList<LMProgramBase>(programIds.size());
        for (int programId : programIds) {
            LMProgramBase program = getProgramSafe(programId);
            if (program != null) {
                programs.add(program);
            }
        }
        
        return programs;
    }

    public LMGroupBase getGroup(int groupId) {
        LMGroupBase group = null;
        DatedObject<LMGroupBase> datedGroup = groups.get(groupId);
        if (datedGroup != null) {
            group = datedGroup.getObject();
        }
        return group;
    }    

    public DatedObject<LMControlArea> getDatedControlArea(int controlAreaId) {
        return controlAreas.get(controlAreaId);
    }
    
    public DatedObject<LMProgramBase> getDatedProgram(int programId) {
        return programs.get(programId);
    }
    
    public DatedObject<LMGroupBase> getDatedGroup(int groupId) {
        return groups.get(groupId);
    }
    
    private void handleLMControlArea(LMControlArea controlArea) {
        log.debug(" ---> Received a control area named " + controlArea.getYukonName());

        /* Build up hashMaps of references for all these different objects, so we don't have
         * to iterate so much later when the new dynamic update messages come through.
         *
         * NOTE: The for loops below do NOT remove deleted items which causes the 
         * group and program maps to be potentially out of date. Updates are handled
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
                programsByLoadGroup.put(group.getYukonID(), currentProgram.getYukonID());
            }
            programs.put(currentProgram.getYukonID(), new DatedObject<LMProgramBase>(currentProgram));
            controlAreaByProgram.put(currentProgram.getYukonID(), controlArea.getYukonID());
        }

        // We wait to add the control area until its programs have been added
        // so we have don't have a control area in the map which references
        // programs/groups which aren't.
        boolean newInsert = controlAreas.put(controlArea.getYukonID(),
                                             new DatedObject<LMControlArea>(controlArea)) == null;

    	// tell all listeners that we received an updated LMControlArea
    	setChanged();
    	notifyObservers( new LCChangeEvent(	this, (newInsert ? LCChangeEvent.INSERT : LCChangeEvent.UPDATE), controlArea) );				
    }
    
    public boolean needInitConn() {
    	//return true if there hasn't been any Observers set to watch this connection
    	return (countObservers() <= 0);
    }

    public void messageReceived(MessageEvent msgEvent) {
        log.debug("messageReceived: " + msgEvent);
        Message obj = msgEvent.getMessage();
        if (obj instanceof LMControlAreaChanged) {
            // The server only sends this type of message for minor control area changes
            // This helps prevent heavy messages constantly flowing on every little change
            handleLMControlAreaChange((LMControlAreaChanged)obj);
        } else if (obj instanceof LMProgramChanged) {
            handleLMProgramChange((LMProgramChanged)obj);
        } else if (obj instanceof LMGroupChanged) {
            handleLMGroupChange((LMGroupChanged) obj);
        } else if (obj instanceof LMControlAreaMsg) {
            // This message type contains a list of everything load management
            // knows about.
            LMControlAreaMsg msg = (LMControlAreaMsg) obj;

            // We need to remove control areas, programs and load groups which
            // aren't used any more.
            handleDeletedItems(msg);
            for (int i = 0; i < msg.getNumberOfLMControlAreas(); i++) {
                handleLMControlArea(msg.getLMControlArea(i));
            }
        } else if (obj instanceof ServerResponseMsg) {
    	    //log.debug("Received a ServerResponseMsg, ignoring it since I didn't send a request");
    	}
    }
    
    private void handleLMControlAreaChange(LMControlAreaChanged changedArea) {
        DatedObject<LMControlArea> datedArea = controlAreas.get(changedArea.getPaoID());
        // LMControlAreaChanged doesn't contain changes to programs
        LMControlArea newControlArea = (LMControlArea) datedArea.getObject().cloneKeepingPrograms();

        newControlArea.setDisableFlag(changedArea.getDisableFlag());
        newControlArea.setNextCheckTime(changedArea.getNextCheckTime());
        newControlArea.setControlAreaState(changedArea.getControlAreaState());
        newControlArea.setCurrentPriority(changedArea.getCurrentPriority());
        newControlArea.setCurrentDailyStartTime(changedArea.getCurrentDailyStartTime());
        newControlArea.setCurrentDailyStopTime(changedArea.getCurrentDailyStopTime());

        for(LMTriggerChanged changedTrigger : changedArea.getTriggers()) {
            log.debug("processing " + changedTrigger);
            LMControlAreaTrigger currentTrigger = newControlArea.getTrigger(changedTrigger.getTriggerNumber());

            if(currentTrigger != null) {
                currentTrigger.setTriggerNumber(changedTrigger.getTriggerNumber());
                currentTrigger.setPointValue(changedTrigger.getPointValue());
                currentTrigger.setLastPointValueTimeStamp(changedTrigger.getLastPointValueTimestamp().getTime());
                currentTrigger.setNormalState(changedTrigger.getNormalState());
                currentTrigger.setThreshold(changedTrigger.getThreshold());
                currentTrigger.setPeakPointValue(changedTrigger.getPeakPointValue());
                currentTrigger.setLastPeakPointValueTimeStamp(changedTrigger.getLastPeakPointValueTimestamp().getTime());
                currentTrigger.setProjectedPointValue(changedTrigger.getProjectedPointValue());
            } else {
                log.error("got trigger change information for control area "
                          + changedArea.getPaoID() + ", trigger number "
                          + changedTrigger.getTriggerNumber()
                          + " but there was no trigger to change");
            }
        }
        controlAreas.put(newControlArea.getYukonID(),
                         new DatedObject<LMControlArea>(newControlArea));

        // tell all listeners that we received an updated LMControlArea
        setChanged();
        notifyObservers(new LCChangeEvent(this, LCChangeEvent.UPDATE, newControlArea));
    }

    private void updateControlAreasForProgram(LMProgramBase program) {
        LMControlArea parentControlArea = findControlAreaForProgram(program.getYukonID());
        LMControlArea newControlArea = parentControlArea.cloneUpdatingProgram(program);
        controlAreas.put(newControlArea.getYukonID(), new DatedObject<LMControlArea>(newControlArea));
    }

    private void handleLMProgramChange(LMProgramChanged changedProgram) {
        DatedObject<LMProgramBase> datedProgram = programs.get(changedProgram.getPaoID());
        LMProgramBase newProgram = datedProgram.getObject().cloneKeepingLoadGroups();

        newProgram.setDisableFlag(changedProgram.getDisableFlag());
        if (newProgram instanceof LMProgramDirect) {
            LMProgramDirect directProgram = (LMProgramDirect) newProgram;
            directProgram.setCurrentGearNumber(changedProgram.getCurrentGearNumber()); 
            directProgram.setLastGroupControlled(changedProgram.getLastGroupControlled());
            directProgram.setProgramStatus(changedProgram.getProgramState());
            directProgram.setReductionTotal(changedProgram.getReductionTotal());
            directProgram.setDirectStartTime(changedProgram.getDirectStartTime());
            directProgram.setDirectStopTime(changedProgram.getDirectStopTime());
            directProgram.setNotifyActiveTime(changedProgram.getNotifyActiveTime());
            directProgram.setNotifyInactiveTime(changedProgram.getNotifyInactiveTime());
            directProgram.setStartedRampingOut(changedProgram.getStartedRampingOutTime());
            directProgram.setOriginSource(changedProgram.getOriginSource());
        }

        programs.put(newProgram.getYukonID(), new DatedObject<LMProgramBase>(newProgram));
        updateControlAreasForProgram(newProgram);

        // tell all listeners that we had an update
        setChanged();
        notifyObservers(new LCChangeEvent(this,
                                          LCChangeEvent.UPDATE,
                                          newProgram));
    }
    
    private void handleLMGroupChange(LMGroupChanged changedGroup) {
        DatedObject<LMGroupBase> datedLoadGroup = groups.get(changedGroup.getPaoID());
    	LMGroupBase newGroup = datedLoadGroup.getObject().clone();

        if (newGroup instanceof LMDirectGroupBase) {
            LMDirectGroupBase directGroup = (LMDirectGroupBase) newGroup;
            directGroup.setDisableFlag(changedGroup.getDisableFlag());
            directGroup.setGroupControlState(changedGroup.getGroupControlState());
            directGroup.setCurrentHoursDaily(changedGroup.getCurrentHoursDaily());
            directGroup.setCurrentHoursMonthly(changedGroup.getCurrentHoursMonthly());
            directGroup.setCurrentHoursSeasonal(changedGroup.getCurrentHoursSeasonal());
            directGroup.setCurrentHoursAnnually(changedGroup.getCurrentHoursAnnually());
            directGroup.setLastControlSent(changedGroup.getLastControlSent());
            directGroup.setControlStartTime(changedGroup.getControlStartTime().getTime());
            directGroup.setControlCompleteTime(changedGroup.getControlCompleteTime().getTime());
            directGroup.setNextControlTime(changedGroup.getNextControlTime().getTime());
            directGroup.setInternalState(changedGroup.getInternalState());
            directGroup.setDailyOps(changedGroup.getDailyOps());
        }

        groups.put(newGroup.getYukonID(), new DatedObject<LMGroupBase>(newGroup));
        for (Integer programId : programsByLoadGroup.get(newGroup.getYukonID())) {
            LMProgramBase parentProgram = programs.get(programId).getObject();
            LMProgramBase newProgram = parentProgram.cloneUpdatingLoadGroup(newGroup);
            programs.put(newProgram.getYukonID(), new DatedObject<LMProgramBase>(newProgram));
            updateControlAreasForProgram(newProgram);
        }

        // tell all listeners that we received an update
        setChanged();
        notifyObservers(new LCChangeEvent(this, LCChangeEvent.UPDATE, newGroup));
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

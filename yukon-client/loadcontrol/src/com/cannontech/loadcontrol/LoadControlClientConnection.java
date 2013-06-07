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

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.events.LCChangeEvent;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.loadcontrol.ControlAreaMessage;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaTriggerItem;
import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupBase;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirect;
import com.cannontech.messaging.message.loadcontrol.dynamic.receive.ControlAreaChanged;
import com.cannontech.messaging.message.loadcontrol.dynamic.receive.GroupChanged;
import com.cannontech.messaging.message.loadcontrol.dynamic.receive.ProgramChanged;
import com.cannontech.messaging.message.loadcontrol.dynamic.receive.TriggerChanged;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class LoadControlClientConnection extends com.cannontech.messaging.util.ClientConnection implements MessageListener {
    private final Logger log = YukonLogManager.getLogger(LoadControlClientConnection.class);
	
	private Map<Integer, DatedObject<ControlAreaItem>> controlAreas = new ConcurrentHashMap<Integer, DatedObject<ControlAreaItem>>();
    private Map<Integer, DatedObject<Program>> programs = new ConcurrentHashMap<Integer, DatedObject<Program>>();
    private Map<Integer, DatedObject<GroupBase>> groups = new ConcurrentHashMap<Integer, DatedObject<GroupBase>>();

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
    		com.cannontech.messaging.message.loadcontrol.CommandMessage cmd = new com.cannontech.messaging.message.loadcontrol.CommandMessage();
    		cmd.setCommand( com.cannontech.messaging.message.loadcontrol.CommandMessage.RETRIEVE_ALL_CONTROL_AREAS );
    		
    		//tell the server we need all the ControlAreas sent to the new registered object
    		queue( cmd );
    	}
    }

    public void disconnect()
    {
    	super.disconnect();
    
    	deleteObservers();
    }
    
    private void handleDeletedItems(ControlAreaMessage msg) {
        // When a message comes in with the "deleted" flag set, it contains all
        // of the control areas EXCEPT the deleted one(s).
        Set<Integer> controlAreasToKeep = Sets.newHashSet();
        Set<Integer> programsToKeep = Sets.newHashSet();
        Set<Integer> loadGroupsToKeep = Sets.newHashSet();

        for (ControlAreaItem controlArea : msg.getLMControlAreaVector()) {
            controlAreasToKeep.add(controlArea.getYukonId());
            for (Program program : controlArea.getProgramVector()) {
                programsToKeep.add(program.getYukonId());
                for (GroupBase group : program.getLoadControlGroupVector()) {
                    loadGroupsToKeep.add(group.getYukonId());
                }
            }
        }

        for (Integer controlAreaId : controlAreas.keySet()) {
            if (!controlAreasToKeep.contains(controlAreaId)) {
                DatedObject<ControlAreaItem> removed = controlAreas.remove(controlAreaId);
                setChanged();
                notifyObservers(new LCChangeEvent(this,
                                                  LCChangeEvent.DELETE,
                                                  removed.getObject()));
            }
        }

        for (Integer programId : programs.keySet()) {
            if (!programsToKeep.contains(programId)) {
                DatedObject<Program> removed = programs.remove(programId);
                setChanged();
                notifyObservers(new LCChangeEvent(this,
                                                  LCChangeEvent.DELETE,
                                                  removed.getObject()));
            }
        }

        for (Integer loadGroupId : groups.keySet()) {
            if (!loadGroupsToKeep.contains(loadGroupId)) {
                DatedObject<GroupBase> removed = groups.remove(loadGroupId);
                setChanged();
                notifyObservers(new LCChangeEvent(this,
                                                  LCChangeEvent.DELETE,
                                                  removed.getObject()));
            }
        }
    }

    @Deprecated
    public ControlAreaItem[] getAllLMControlAreas() {
        ControlAreaItem[] retVal = new ControlAreaItem[controlAreas.size()];
        int index = 0;
        for (DatedObject<ControlAreaItem> datedControlArea : controlAreas.values()) {
            retVal[index++] = datedControlArea.getObject();
        }
        return retVal;
    }
    
    public ControlAreaItem getControlArea(int controlAreaId) {
        ControlAreaItem controlArea = null;
        DatedObject<ControlAreaItem> datedControlArea = controlAreas.get(controlAreaId);
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
    public ControlAreaItem findControlAreaForProgram(int programId) {
        Integer controlAreaId = controlAreaByProgram.get(programId);
        if(controlAreaId == null) {
            return null;
        }
        DatedObject<ControlAreaItem> datedControlArea = controlAreas.get(controlAreaId);
        return datedControlArea == null ? null : datedControlArea.getObject();
    }
    
    public int getControlAreaCount() {
    	return controlAreas.size();
    }
    
    @Deprecated
    public Map<Integer, ControlAreaItem> getControlAreas() {
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

    public Program getProgram(int programId) 
    {
        DatedObject<Program> datedProgram = programs.get(programId);
        return datedProgram == null ? null : datedProgram.getObject();
    }
    
    /**
     * Returns a LMProgramBase object for the given program id. Throws if the connection is not valid
     * or if the program cannot be found based on the id.
     * 
     * @throws ConnectionException
     * @throws NotFoundException
     */
    public Program getProgramSafe(int programId) throws ConnectionException, NotFoundException
    {
        if(!isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }
        
        DatedObject<Program> datedProgram = programs.get(programId);
        
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
    public Set<Program> getAllProgramsSet() throws ConnectionException {
        
        if(!isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }
    	
    	List<DatedObject<Program>> datedPrograms = new ArrayList<DatedObject<Program>>(programs.values());
    	
    	List<Program> programs = Lists.transform(datedPrograms, new Function<DatedObject<Program>, Program>() {
								    		@Override
								    		public Program apply(DatedObject<Program> from) {
								    			return from.getObject();
								    		}
										});
    	
    	ImmutableSet<Program> programSet = ImmutableSet.copyOf(programs);
    	
        return programSet;
    }

    public List<Program> getProgramsForProgramIds(List<Integer> programIds) throws ConnectionException,
            NotFoundException {

        List<Program> programs = new ArrayList<Program>(programIds.size());
        for (int programId : programIds) {
            Program program = getProgramSafe(programId);
            if (program != null) {
                programs.add(program);
            }
        }
        
        return programs;
    }

    public GroupBase getGroup(int groupId) {
        GroupBase group = null;
        DatedObject<GroupBase> datedGroup = groups.get(groupId);
        if (datedGroup != null) {
            group = datedGroup.getObject();
        }
        return group;
    }    

    public DatedObject<ControlAreaItem> getDatedControlArea(int controlAreaId) {
        return controlAreas.get(controlAreaId);
    }
    
    public DatedObject<Program> getDatedProgram(int programId) {
        return programs.get(programId);
    }
    
    public DatedObject<GroupBase> getDatedGroup(int groupId) {
        return groups.get(groupId);
    }
    
    private void handleLMControlArea(ControlAreaItem controlArea) {
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
        for (int i = 0; i < controlArea.getProgramVector().size(); i++) {
            Program currentProgram = (Program) controlArea.getProgramVector()
                                                                      .get(i);
            for (int j = 0; j < currentProgram.getLoadControlGroupVector()
                                              .size(); j++) {
                GroupBase group = (GroupBase) currentProgram.getLoadControlGroupVector().get(j);
                groups.put(group.getYukonId(), new DatedObject<GroupBase>(group));
                programsByLoadGroup.put(group.getYukonId(), currentProgram.getYukonId());
            }
            programs.put(currentProgram.getYukonId(), new DatedObject<Program>(currentProgram));
            controlAreaByProgram.put(currentProgram.getYukonId(), controlArea.getYukonId());
        }

        // We wait to add the control area until its programs have been added
        // so we have don't have a control area in the map which references
        // programs/groups which aren't.
        boolean newInsert = controlAreas.put(controlArea.getYukonId(),
                                             new DatedObject<ControlAreaItem>(controlArea)) == null;

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
        BaseMessage obj = msgEvent.getMessage();
        if (obj instanceof ControlAreaChanged) {
            // The server only sends this type of message for minor control area changes
            // This helps prevent heavy messages constantly flowing on every little change
            handleLMControlAreaChange((ControlAreaChanged)obj);
        } else if (obj instanceof ProgramChanged) {
            handleLMProgramChange((ProgramChanged)obj);
        } else if (obj instanceof GroupChanged) {
            handleLMGroupChange((GroupChanged) obj);
        } else if (obj instanceof ControlAreaMessage) {
            // This message type contains a list of everything load management
            // knows about.
            ControlAreaMessage msg = (ControlAreaMessage) obj;

            // We need to remove control areas, programs and load groups which
            // aren't used any more.
            handleDeletedItems(msg);
            for (int i = 0; i < msg.getNumberOfLMControlAreas(); i++) {
                handleLMControlArea(msg.getLMControlArea(i));
            }
        } else if (obj instanceof ServerResponseMessage) {
    	    //log.debug("Received a ServerResponseMsg, ignoring it since I didn't send a request");
    	}
    }
    
   
    private void handleLMControlAreaChange(ControlAreaChanged changedArea) {
        DatedObject<ControlAreaItem> datedArea = controlAreas.get(changedArea.getPaoId());
        // LMControlAreaChanged doesn't contain changes to programs
        ControlAreaItem newControlArea = (ControlAreaItem) datedArea.getObject().cloneKeepingPrograms();

        newControlArea.setDisableFlag(changedArea.getDisableFlag());
        newControlArea.setNextCheckTime(changedArea.getNextCheckTime());
        newControlArea.setControlAreaState(changedArea.getControlAreaState());
        newControlArea.setCurrentPriority(changedArea.getCurrentPriority());
        newControlArea.setCurrentDailyStartTime(changedArea.getCurrentDailyStartTime());
        newControlArea.setCurrentDailyStopTime(changedArea.getCurrentDailyStopTime());

        for(TriggerChanged changedTrigger : changedArea.getTriggers()) {
            log.debug("processing " + changedTrigger);
            ControlAreaTriggerItem currentTrigger = newControlArea.getTrigger(changedTrigger.getTriggerNumber());

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
                          + changedArea.getPaoId() + ", trigger number "
                          + changedTrigger.getTriggerNumber()
                          + " but there was no trigger to change");
            }
        }
        controlAreas.put(newControlArea.getYukonId(),
                         new DatedObject<ControlAreaItem>(newControlArea));

        // tell all listeners that we received an updated LMControlArea
        setChanged();
        notifyObservers(new LCChangeEvent(this, LCChangeEvent.UPDATE, newControlArea));
    }

    private void updateControlAreasForProgram(Program program) {
        ControlAreaItem parentControlArea = findControlAreaForProgram(program.getYukonId());
        ControlAreaItem newControlArea = parentControlArea.cloneUpdatingProgram(program);
        controlAreas.put(newControlArea.getYukonId(), new DatedObject<ControlAreaItem>(newControlArea));
    }

    private void handleLMProgramChange(ProgramChanged changedProgram) {
        DatedObject<Program> datedProgram = programs.get(changedProgram.getPaoId());
    	Program newProgram = datedProgram.getObject().cloneKeepingLoadGroups();

        newProgram.setDisableFlag(changedProgram.getDisableFlag());
        if (newProgram instanceof ProgramDirect) {
            ProgramDirect directProgram = (ProgramDirect) newProgram;
            directProgram.setCurrentGearNumber(changedProgram.getCurrentGearNumber()); 
            directProgram.setLastGroupControlled(changedProgram.getLastGroupControlled());
            directProgram.setProgramStatus(changedProgram.getProgramState());
            directProgram.setReductionTotal(changedProgram.getReductionTotal());
            directProgram.setDirectStartTime(changedProgram.getDirectStartTime());
            directProgram.setDirectStopTime(changedProgram.getDirectStopTime());
            directProgram.setNotifyActiveTime(changedProgram.getNotifyActiveTime());
            directProgram.setNotifyInactiveTime(changedProgram.getNotifyInactiveTime());
            directProgram.setStartedRampingOut(changedProgram.getStartedRampingOutTime());
        }

        programs.put(newProgram.getYukonId(), new DatedObject<Program>(newProgram));
        updateControlAreasForProgram(newProgram);

        // tell all listeners that we had an update
        setChanged();
        notifyObservers(new LCChangeEvent(this,
                                          LCChangeEvent.UPDATE,
                                          newProgram));
    }
    
    private void handleLMGroupChange(GroupChanged changedGroup) {
        DatedObject<GroupBase> datedLoadGroup = groups.get(changedGroup.getPaoId());
    	GroupBase newGroup = datedLoadGroup.getObject().clone();

        if (newGroup instanceof DirectGroupBase) {
            DirectGroupBase directGroup = (DirectGroupBase) newGroup;
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

        groups.put(newGroup.getYukonId(), new DatedObject<GroupBase>(newGroup));
        for (Integer programId : programsByLoadGroup.get(newGroup.getYukonId())) {
            Program parentProgram = programs.get(programId).getObject();
            Program newProgram = parentProgram.cloneUpdatingLoadGroup(newGroup);
            programs.put(newProgram.getYukonId(), new DatedObject<Program>(newProgram));
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

package com.cannontech.common.point;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.StateControlType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.state.State;
import com.cannontech.database.db.state.StateGroup;

public class StatusPointBuilder extends PointBuilder {
    private final Logger log = YukonLogManager.getLogger(StatusPointBuilder.class);
    private int pointOffset = 0; //no physical point offset unless specified
    private String stateGroupName = "TwoStateStatus";
    private String initialStateName = "Open";
    private ControlType controlType = ControlType.LATCH;
    private boolean isArchive = false;
    private boolean isControlInhibit = false;
    
    //these are all dependent on the controlType
    private Integer closeTime1 = null;
    private Integer closeTime2 = null;
    private Integer commandTimeout = null;
    private int controlOffset = 1;
    private StateControlType state1Command = StateControlType.OPEN;
    private StateControlType state2Command = StateControlType.CLOSE;
    
    protected StatusPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, PointPropertyValueDao pointPropertyValueDao) {
        super(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Builds a StatusPoint object from this populated builder.
     * @throws IllegalStateException if the specified state group or initial state are not valid
     */
    @Override
    public StatusPoint build() throws IllegalStateException {
        Integer stateGroupId = null;
        try {
            for(StateGroup stateGroup : StateGroup.getStateGroups()) {
                if(stateGroup.getName().equalsIgnoreCase(stateGroupName)) {
                    stateGroupId = stateGroup.getStateGroupID();
                }
            }
        } catch(SQLException e) {
            throw new IllegalStateException("No state group found with name " + stateGroupName, e);
        }
        if(stateGroupId == null) {
            throw new IllegalStateException("No state group found with name " + stateGroupName);
        }
        
        Integer initialStateId = null;
        try {
            for(State state: State.getStates(stateGroupId)) {
                if(state.getText().equalsIgnoreCase(initialStateName)) {
                    initialStateId = state.getRawState();
                }
            }
        } catch(SQLException e) {
            throw new IllegalStateException("No state \"" + initialStateName + "\" in state group \"" + stateGroupName + "\".", e);
        }
        if(initialStateId == null) {
            throw new IllegalStateException("No state \"" + initialStateName + "\" in state group \"" + stateGroupName + "\".");
        }
        
        PointArchiveType archiveType = PointArchiveType.NONE;
        PointArchiveInterval archiveInterval = PointArchiveInterval.ZERO;
        if(isArchive) {
            archiveType = PointArchiveType.ON_CHANGE;
        }
        
        StatusPoint point = (StatusPoint) PointFactory.createStatusPoint(pointName, paoId, pointId, 
                                                                         pointOffset,  stateGroupId, 
                                                                         initialStateId, controlOffset, 
                                                                         controlType, state1Command, 
                                                                         state2Command, archiveType, 
                                                                         archiveInterval);
        if(isDisabled) {
            point.getPoint().setServiceFlag(CtiUtilities.getTrueCharacter());
        }
        
        if(isControlInhibit) {
            point.getPointStatusControl().setControlInhibited(true);
        }
        
        if(controlType.hasSettings()) {
            point.getPointStatusControl().setControlType(controlType.getControlName());
            if(closeTime1 != null) point.getPointStatusControl().setCloseTime1(closeTime1);
            if(closeTime1 != null) point.getPointStatusControl().setCloseTime2(closeTime2);
            if(commandTimeout != null) point.getPointStatusControl().setCommandTimeOut(commandTimeout);
        } else {
            if(closeTime1 != null || closeTime2 != null) {
                throw new IllegalStateException("Close Time can not be specified for control type \"" + controlType.getControlName() + "\".");
            }
            if(commandTimeout != null) {
                throw new IllegalStateException("Command Timeout can not be specified for control type \"" + controlType.getControlName() + "\".");
            }
        }
        
        if(staleDataTime != null) {
            log.warn("Build() called on builder with Stale Data settings. Stale data settings" +
                     " will be lost unless Insert() is used.");
        }
        
        return point;
    }
    
    /**
     * Builds a StatusPoint object from this populated builder and inserts it into the database.
     */
    @Override
    public StatusPoint insert() {
        return (StatusPoint) super.insert();
    }
    
    public void pointOffset(int pointOffset) {
        if(pointOffset < 0) throw new IllegalArgumentException("Point Offset cannot be negative.");
        this.pointOffset = pointOffset;
    }
    
    public void stateGroup(String stateGroupName) {
        this.stateGroupName = stateGroupName;
    }
    
    public void initialState(String stateName) {
        this.initialStateName = stateName;
    }
    
    public void controlOffset(int controlOffset) {
        this.controlOffset = controlOffset;
    }
    
    public void controlType(ControlType controlType) {
        if(controlType == null) throw new IllegalArgumentException("Control Type cannot be null.");
        this.controlType = controlType;
    }
    
    public void state1Command(StateControlType state1Command) {
        this.state1Command = state1Command;
    }
    
    public void state2Command(StateControlType state2Command) {
        this.state2Command = state2Command;
    }
    
    public void archive(boolean isArchive) {
        this.isArchive = isArchive;
    }
    
    public void controlInhibit(boolean isControlInhibit) {
        this.isControlInhibit = isControlInhibit;
    }
    
    public void closeTime1(int closeTime1) {
        this.closeTime1 = closeTime1;
    }
    
    public void closeTime2(int closeTime2) {
        this.closeTime2 = closeTime2;
    }
    
    public void commandTimeout(int commandTimeout) {
        this.commandTimeout = commandTimeout;
    }
}

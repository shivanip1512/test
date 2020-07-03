package com.cannontech.common.point;

import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.model.PointPeriodicRate;
import com.cannontech.common.bulk.model.StatusPointUpdateType;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.state.State;
import com.cannontech.database.db.state.StateGroup;

public class CalcStatusPointBuilder extends PointBuilder {
    private final Logger log = YukonLogManager.getLogger(CalcStatusPointBuilder.class);
    private PointCalculation calculation = new PointCalculation();
    private StatusPointUpdateType updateType = StatusPointUpdateType.ON_FIRST_CHANGE;
    private PointPeriodicRate updateRate = PointPeriodicRate.ONE_SECOND;
    private String stateGroupName = "TwoStateStatus";
    private String initialStateName = "Open";
    private Boolean archiveData = false;
    
    protected CalcStatusPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, PointPropertyValueDao pointPropertyValueDao) {
        super(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Builds an CalcStatusPoint object from this populated builder and inserts it into the database.
     * Stale data time and update values are inserted, but are not accessible through the
     * returned CalcStatusPoint.
     */
    @Override
    public CalcStatusPoint insert() {
        return (CalcStatusPoint) super.insert();
    }
    
    /**
     * Builds a CalcStatusPoint object from this populated builder.
     * Stale data time and update values are ignored in this case, because the DBPersistents
     * do not support them.
     * @throws IllegalStateException if the specified state group or initial state are not valid
     */
    @Override
    public CalcStatusPoint build() throws IllegalStateException{
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
        
        CalcStatusPoint point = (CalcStatusPoint) PointFactory.createCalcStatusPoint(paoId, pointName, stateGroupId);
        
        point.getPointStatus().setInitialState(initialStateId);
        
        point.setCalcComponents(calculation.copyComponentsAndInsertPointId(pointId));
        point.getCalcBase().setUpdateType(updateType.getDatabaseRepresentation());
        point.getCalcBase().setPeriodicRate(updateRate.getSeconds());
        
        if(staleDataTime != null) {
            log.warn("Build() called on builder with Stale Data settings. Stale data settings" +
                     " will be lost unless Insert() is used.");
        }

        if (archiveData) { // true = 'On Change'
            point.getPoint().setArchiveType(PointArchiveType.ON_CHANGE);
        } else {
            point.getPoint().setArchiveType(PointArchiveType.NONE);
        }

        return point;
    }
    
    public void setCalculation(PointCalculation calculation) {
        this.calculation = calculation;
    }
    
    public void setUpdateType(StatusPointUpdateType updateType) {
        this.updateType = updateType;
    }
    
    public void setUpdateRate(PointPeriodicRate updateRate) {
        this.updateRate = updateRate;
    }
    
    public void setStateGroup(String stateGroupName) {
        this.stateGroupName = stateGroupName;
    }
    
    public void setInitialState(String initialStateName) {
        this.initialStateName = initialStateName;
    }
    
    public void setArchiveData(boolean archiveData) {
        this.archiveData = archiveData;
    }
}

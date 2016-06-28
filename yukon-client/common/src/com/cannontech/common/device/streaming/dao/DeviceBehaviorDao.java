package com.cannontech.common.device.streaming.dao;

import java.util.List;

import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.device.streaming.model.LiteBehavior;

public interface DeviceBehaviorDao {

    void assignBehavior(int behaviorId, List<Integer> deviceIds);

    /**
     * Attempts to find a behavior by BehaviorId.
     * If found, updates the behavior. Otherwise creates a new behavior.
     * 
     *  @return BehaviorId
     */
    
    int saveBehavior(Behavior behavior);

    /**
     * Returns the list of Behaviors by BehaviorType. This list doesn't have the BehaviorItem information.
     */
    List<LiteBehavior> getLiteBehaviorsByType(BehaviorType type);

    /**
     * Deletes Behavior
     */
    void deleteBehavior(int behaviorId);

    /**
     * Finds Behavior by Id.
     */
    Behavior getBehaviorById(int behaviorId);

    /**
     * Attempts to find a Behavior report for BehaviorType and DeviceId.
     * If report is found, updates the report. Otherwise creates new report.
     * 
     * @return BehaviorReportId
     */
    int saveBehaviorReport(BehaviorReport report);

    /**
     * Deletes all Behaviors that have no devices associated with them.
     */
    void deleteUnusedBehaviors();
}

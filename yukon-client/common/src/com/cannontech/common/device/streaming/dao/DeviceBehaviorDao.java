package com.cannontech.common.device.streaming.dao;

import java.util.List;

import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorType;

public interface DeviceBehaviorDao {

    /**
     * Attempts to find a behavior by BehaviorId.
     * If found, updates the behavior. Otherwise creates a new behavior.
     * 
     *  @return BehaviorId
     */
    
    int saveBehavior(Behavior behavior);

    /**
     * Returns the list of Behaviors by BehaviorType.
     */
    List<Behavior> getBehaviorsByType(BehaviorType type);

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

    /**
     * Assigns behavior from devices.
     */
    void unassignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds);
    
    /**
     * Assigns behavior to devices.
     */
    void assignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds);

}

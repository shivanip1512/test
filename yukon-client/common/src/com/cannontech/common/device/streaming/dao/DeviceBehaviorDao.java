package com.cannontech.common.device.streaming.dao;

import java.util.List;

import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorReportStatus;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.Multimap;

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
     * Assigns behavior from devices and deletes unused behaviors.
     */
    void unassignBehavior(BehaviorType type, List<Integer> deviceIds);
    
    /**
     * Assigns behavior to devices and deletes unused behaviors..
     */
    void assignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds);
    
    /**
     * Finds the Behavior of the specified type that is assigned to the device.
     * @throws NotFoundException if no behavior of the specified type is assigned to the device.
     */
    Behavior getBehaviorByDeviceIdAndType(int deviceId, BehaviorType type);
    
    /**
     * Updates behavior report status and timestamp.
     * @param status - status to update to
     */
    void updateBehaviorReportStatus(BehaviorType type, BehaviorReportStatus status, List<Integer> deviceIds);

    BehaviorReport getBehaviorReportByDeviceIdAndType(int deviceId, BehaviorType type);

  
    /**
     * Returns a multimap of behavior ids to device ids.
     */
    Multimap<Integer, Integer> getBehaviorIdsToDevicesIdMap(Iterable<Integer> behaviorIds);

    /**
     * Returns a multimap of device ids to behavior ids.
     */
    Multimap<Integer, Integer> getDeviceIdsToBehaviorIdMap(Iterable<Integer> deviceIds, BehaviorType type,
            List<BuiltInAttribute> attributes, Integer interval, Integer behaviorId);
}

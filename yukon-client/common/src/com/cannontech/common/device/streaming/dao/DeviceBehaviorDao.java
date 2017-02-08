package com.cannontech.common.device.streaming.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorReportStatus;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface DeviceBehaviorDao extends DeviceBehaviorStrings {
    
    /**
     * Attempts to find a behavior by BehaviorId.
     * If found, updates the behavior. Otherwise creates a new behavior.
     * 
     * @return BehaviorId
     */
    int saveBehavior(Behavior behavior);

    /**
     * Returns the list of Behaviors by BehaviorType.
     */
    List<Behavior> getAllBehaviorsByType(BehaviorType type);

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
     * Assigns behavior to devices and deletes unused behaviors if deleteUnusedBehaviors is true.
     */
    void assignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds, boolean deleteUnusedBehaviors);

    /**
     * Finds the Behavior of the specified type that is assigned to the device.
     */
    Behavior findBehaviorByDeviceIdAndType(int deviceId, BehaviorType type);

    /**
     * Updates behavior report status and timestamp.
     * 
     * @param status - status to update to
     */
    void updateBehaviorReportStatus(BehaviorType type, BehaviorReportStatus status, List<Integer> deviceIds);

    /**
     * Returns a map of device ids to behavior ids.
     */
    Map<Integer, Integer> getDeviceIdsToBehaviorIdMap(BehaviorType type, List<BuiltInAttribute> attributes,
            Integer interval, Integer behaviorId);

    /**
     * Returns a map of device ids to behavior report.
     */
    Map<Integer, BehaviorReport> getBehaviorReportsByTypeAndDeviceIds(BehaviorType type, List<Integer> deviceIds);

    /**
     * Returns a map of all device ids to behavior report.
     */
    Map<Integer, BehaviorReport> getBehaviorReportsByType(BehaviorType type);

    /**
     * Returns a map of device ids to behavior.
     */
    Map<Integer, Behavior> getBehaviorsByTypeAndDeviceIds(BehaviorType type, List<Integer> deviceIds);

    /**
     * Returns a map of all device ids to behavior.
     */
    Map<Integer, Behavior> getBehaviorsByType(BehaviorType type);

    /**
     * Deletes behavior report
     */
    void deleteBehaviorReport(int behaviorReportId);

    class DiscrepancyInfo{
        private Behavior behavior;
        private BehaviorReport behaviorReport;
        private Instant lastCommunicated;
        private int deviceId;
        
        public BehaviorReport getBehaviorReport() {
            return behaviorReport;
        }
        public void setBehaviorReport(BehaviorReport behaviorReport) {
            this.behaviorReport = behaviorReport;
        }
        public Behavior getBehavior() {
            return behavior;
        }
        public void setBehavior(Behavior behavior) {
            this.behavior = behavior;
        }
        public Instant getLastCommunicated() {
            return lastCommunicated;
        }
        public void setLastCommunicated(Instant lastCommunicated) {
            this.lastCommunicated = lastCommunicated;
        }
        public int getDeviceId() {
            return deviceId;
        }
        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }
    }
    
    /**
     * Finds discrepancies. If deviceId us NULL return all discrepancies otherwise returns discrepancies for the device
     * id specified.
     */
    Iterable<DiscrepancyInfo> findDiscrepancies(BehaviorType type, Integer deviceId);

}

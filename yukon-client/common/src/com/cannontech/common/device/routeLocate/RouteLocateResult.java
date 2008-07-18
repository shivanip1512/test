package com.cannontech.common.device.routeLocate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.util.Completable;

public class RouteLocateResult implements Completable {

    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
    private String resultId;
    private List<Integer> routeIds;
    private boolean autoUpdateRoute;
    private DeviceCollection deviceCollection;
    private StoredDeviceGroup successGroup;
    private StoredDeviceGroup failureGroup;
    
    private Date startTime;
    private Date stopTime;
    private boolean isComplete = false;
    
    private int locatedCount = 0;
    private int notFoundCount = 0;
    
    private Map<Integer, DeviceRouteLocation> pendingDeviceRouteLocations = new HashMap<Integer, DeviceRouteLocation>();
    private Map<Integer, DeviceRouteLocation> completedDeviceRouteLocations = new HashMap<Integer, DeviceRouteLocation>();
    
    public RouteLocateResult(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao, DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
        this.startTime = new Date();
    }
    
    @Override
    public boolean isComplete() {
        return isComplete;
    }
    
    // METHODS
    public synchronized void addPendingDeviceRouteLocation(DeviceRouteLocation deviceRouteLocation) {
        this.pendingDeviceRouteLocations.put(deviceRouteLocation.getId(), deviceRouteLocation);
    }
    public synchronized void addCompletedDeviceRouteLocation(DeviceRouteLocation deviceRouteLocation) {
        
        this.pendingDeviceRouteLocations.remove(deviceRouteLocation.getId());
        this.completedDeviceRouteLocations.put(deviceRouteLocation.getId(), deviceRouteLocation);
    }
    
    public void addDeviceToSuccessGroup(YukonDevice device) {
        locatedCount++;
        deviceGroupMemberEditorDao.addDevices(getSuccessGroup(), device);
    }
    
    public void addDeviceToFailureGroup(YukonDevice device) {
        notFoundCount++;
        deviceGroupMemberEditorDao.addDevices(getFailureGroup(), device);
    }
    
    public DeviceCollection getSuccessDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(getSuccessGroup());
    }
    
    public DeviceCollection getFailureDeviceCollection() {
        return deviceGroupCollectionHelper.buildDeviceCollection(getFailureGroup());
    }
    
    public int getLocatedCount() {
        return locatedCount;
    }
    public int getNotFoundCount() {
        return notFoundCount;
    }
    
    // SETTERS GETTERS
    
    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public List<Integer> getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(List<Integer> routeIds) {
        this.routeIds = routeIds;
    }
    
    public boolean isAutoUpdateRoute() {
        return autoUpdateRoute;
    }
    
    public void setAutoUpdateRoute(boolean autoUpdateRoute) {
        this.autoUpdateRoute = autoUpdateRoute;
    }
    
    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public StoredDeviceGroup getSuccessGroup() {
        return successGroup;
    }

    public void setSuccessGroup(StoredDeviceGroup successGroup) {
        this.successGroup = successGroup;
    }

    public StoredDeviceGroup getFailureGroup() {
        return failureGroup;
    }

    public void setFailureGroup(StoredDeviceGroup failureGroup) {
        this.failureGroup = failureGroup;
    }

    public Map<Integer, DeviceRouteLocation> getCompletedDeviceRouteLocations() {
        return this.completedDeviceRouteLocations;
    }

    public Map<Integer, DeviceRouteLocation> getPendingDeviceRouteLocations() {
        return pendingDeviceRouteLocations;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

}

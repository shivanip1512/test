package com.cannontech.common.device.routeLocate.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.routeLocate.DeviceRouteLocation;
import com.cannontech.common.device.routeLocate.RouteLocateExecutor;
import com.cannontech.common.device.routeLocate.RouteLocateResult;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.device.service.RouteDiscoveryService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;

public class RouteLocateExecutorImpl implements RouteLocateExecutor {

    private RouteDiscoveryService routeDiscoveryService = null;
    private TemporaryDeviceGroupService temporaryDeviceGroupService = null;
    private PaoDao paoDao = null;
    private DeviceUpdateService deviceUpdateService = null;
    private RecentResultsCache<RouteLocateResult> routeLocateResultsCache = null;

    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
    @Override
    public String execute(final DeviceCollection deviceCollection, final List<Integer> routeIds, boolean autoUpdateRoute, final SimpleCallback<RouteLocateResult> callback, final LiteYukonUser user) {
        return execute(deviceCollection, routeIds, autoUpdateRoute, callback, user, "ping");
    }
    
    
    @Override
    public String execute(final DeviceCollection deviceCollection, final List<Integer> routeIds, boolean autoUpdateRoute, final SimpleCallback<RouteLocateResult> callback, final LiteYukonUser user, final String command) {

        final RouteLocateResult routeLocateResult = new RouteLocateResult(deviceGroupMemberEditorDao, deviceGroupCollectionHelper);
        routeLocateResult.setDeviceCollection(deviceCollection);
        routeLocateResult.setRouteIds(routeIds);
        routeLocateResult.setAutoUpdateRoute(autoUpdateRoute);
        routeLocateResult.setSuccessGroup(temporaryDeviceGroupService.createTempGroup());
        routeLocateResult.setFailureGroup(temporaryDeviceGroupService.createTempGroup());

        for (final SimpleDevice device : deviceCollection) {

            List<Integer> orderedRouteIds = new ArrayList<Integer>(routeIds);
        
            // setup DeviceRouteLocation
            final DeviceRouteLocation deviceRouteLocation = new DeviceRouteLocation(device);
            
            // set initial route name (if it has one already)
            addInitialRoute(device, deviceRouteLocation, orderedRouteIds);
            
            // callback
            SimpleCallback<Integer> routeFoundCallback = new SimpleCallback<Integer> () {
                
                @Override
                public void handle(Integer routeId) throws Exception {
                    if(routeLocateResult.isCanceled()) 
                        return;
                    
                    // not found
                    if (routeId == null) {
                        
                        deviceRouteLocation.setLocated(false);
                        
                        // failure DeviceGroup
                        routeLocateResult.addDeviceToFailureGroup(device);
                    
                    // found
                    } else {
                        
                        deviceRouteLocation.setLocated(true);
                        deviceRouteLocation.setRouteId(routeId);
                        
                        // do this here once now..
                        deviceRouteLocation.setRouteName(paoDao.getYukonPAOName(routeId));
                        
                        
                        // auto update
                        if (routeLocateResult.isAutoUpdateRoute()) {
                            deviceUpdateService.changeRoute(device, routeId);
                            
                            deviceRouteLocation.setRouteUpdated(true);
                        }
                        
                        // success DeviceGroup
                        routeLocateResult.addDeviceToSuccessGroup(device);
                    }
                    
                    // completed
                    routeLocateResult.addCompletedDeviceRouteLocation(deviceRouteLocation);
                    
                    // run final callback
                    if (routeLocateResult.getPendingDeviceRouteLocations().size() == 0) {
                        routeLocateResult.setStopTime(new Date());
                        routeLocateResult.setComplete(true);
                        callback.handle(routeLocateResult);
                    }
                    
                }
            };
            
            routeLocateResult.addPendingDeviceRouteLocation(deviceRouteLocation);
            routeDiscoveryService.routeDiscovery(device, orderedRouteIds, routeFoundCallback, user, command);
            routeLocateResult.addRouteFoundCallback(routeFoundCallback);
            
        }
        
        String resultId = routeLocateResultsCache.addResult(routeLocateResult);
        routeLocateResult.setResultId(resultId);
        
        return resultId;
    }

    public void cancelExecution(String resultId, final LiteYukonUser user) {
        RouteLocateResult routeLocateResult = routeLocateResultsCache.getResult(resultId);
        List<SimpleCallback<Integer>> routeFoundCallbacks = routeLocateResult.getRouteFoundCallbacks();

        routeDiscoveryService.cancelRouteDiscovery(routeFoundCallbacks, user);
        
        routeLocateResult.setCanceled(true);
        routeLocateResult.setStopTime(new Date());
        routeLocateResult.clearPendingDeviceRouteLocations();
    }
    
    private void addInitialRoute(YukonDevice device, 
                                 DeviceRouteLocation deviceRouteLocation,
                                 List<Integer> orderedRouteIds){
        LiteYukonPAObject devicePaoObj = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
        Integer initialRouteId = devicePaoObj.getRouteID();
        if (initialRouteId != PAOGroups.INVALID) {
            deviceRouteLocation.setInitialRouteId(initialRouteId);
            deviceRouteLocation.setInitialRouteName(paoDao.getYukonPAOName(initialRouteId));
            
            boolean removed = orderedRouteIds.remove(initialRouteId);
            if (removed) {
                orderedRouteIds.add(0, initialRouteId);
            }
        }
        
        deviceRouteLocation.setDeviceName(devicePaoObj.getPaoName());
    }
    
    @Override
    public List<RouteLocateResult> getCompleted() {
        return routeLocateResultsCache.getCompleted();
    }
    
    @Override
    public List<RouteLocateResult> getPending() {
        return routeLocateResultsCache.getPending();
    }

    @Override
    public RouteLocateResult getResult(String id) {
        return routeLocateResultsCache.getResult(id);
    }

    
    @Required
    public void setRouteDiscoveryService(RouteDiscoveryService routeDiscoveryService) {
        this.routeDiscoveryService = routeDiscoveryService;
    }
    
    @Required
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
        this.deviceUpdateService = deviceUpdateService;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Required
    public void setDeviceGroupCollectionHelper(
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Required
    public void setRouteLocateResultsCache(RecentResultsCache<RouteLocateResult> routeLocateResultsCache) {
        this.routeLocateResultsCache = routeLocateResultsCache;
    }

}

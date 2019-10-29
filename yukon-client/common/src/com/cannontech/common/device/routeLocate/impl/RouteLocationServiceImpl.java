package com.cannontech.common.device.routeLocate.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.FAILURE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.SUCCESS;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.UNSUPPORTED;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.routeLocate.DeviceRouteLocation;
import com.cannontech.common.device.routeLocate.RouteLocationService;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.device.service.RouteDiscoveryService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class RouteLocationServiceImpl implements RouteLocationService, CollectionActionCancellationService {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private RouteDiscoveryService routeDiscoveryService;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private ConnPool connPool;
    
    private final Logger log = YukonLogManager.getLogger(RouteLocationServiceImpl.class);
    
    private Cache<Integer, List<DeviceRouteLocation>> deviceRouteLocationCache =
            CacheBuilder.newBuilder().expireAfterAccess(7, TimeUnit.DAYS).build();
    
    @Override
    public int locate(LinkedHashMap<String, String> inputs, DeviceCollection deviceCollection, List<Integer> routeIds,
            boolean autoUpdateRoute, String command, SimpleCallback<CollectionActionResult> alertCallback,
            YukonUserContext context) {

        CollectionActionResult result = collectionActionService.createResult(CollectionAction.LOCATE_ROUTE, inputs,
            deviceCollection, CommandRequestType.DEVICE_ROUTE, DeviceRequestType.PING_DEVICE_ON_ROUTE_COMMAND, context);
        List<SimpleDevice> unsupportedDevices = deviceCollection.getDeviceList().stream()
                .filter(d-> !paoDefinitionDao.isTagSupported(d.getDeviceType(), PaoTag.LOCATE_ROUTE)).collect(Collectors.toList());
        List<SimpleDevice> supportedDevices = new ArrayList<>(deviceCollection.getDeviceList());
        supportedDevices.removeAll(unsupportedDevices);
        collectionActionService.addUnsupportedToResult(UNSUPPORTED, result, unsupportedDevices);
        deviceRouteLocationCache.put(result.getCacheKey(), new ArrayList<>());
        
        if(supportedDevices.isEmpty()) {
            collectionActionService.updateResult(result, CommandRequestExecutionStatus.COMPLETE);
            return result.getCacheKey();
        }
        
        if(!connPool.getDefPorterConn().isValid()) {
            result.setExecutionExceptionText("No porter connection.");
            collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
            return result.getCacheKey();
        }
        ArrayList<SimpleDevice> allDevices = Lists.newArrayList(supportedDevices);
        
        for (SimpleDevice device : supportedDevices) {

            List<Integer> orderedRouteIds = new ArrayList<>(routeIds);
        
            // setup DeviceRouteLocation
            DeviceRouteLocation deviceRouteLocation = new DeviceRouteLocation(device);
            deviceRouteLocationCache.getIfPresent(result.getCacheKey()).add(deviceRouteLocation);
            
            // set initial route name (if it has one already)
            addInitialRoute(device, deviceRouteLocation, orderedRouteIds);
            
            // callback
            SimpleCallback<Integer> routeFoundCallback = new SimpleCallback<Integer> () {
                
                @Override
                public void handle(Integer routeId) throws Exception {
                    // not found
                    if (routeId == null) {
                        
                        deviceRouteLocation.setLocated(false);
                        
                        // failure DeviceGroup
                        CollectionActionLogDetail detail = new CollectionActionLogDetail(device, FAILURE);
                        result.addDeviceToGroup(FAILURE, device, detail);
                    
                    // found
                    } else {
                        
                        deviceRouteLocation.setLocated(true);
                        deviceRouteLocation.setRouteId(routeId);
                        
                        // do this here once now..
                        LiteYukonPAObject route = databaseCache.getAllPaosMap().get(routeId);
                        deviceRouteLocation.setRouteName(route.getPaoName());
                        
                        
                        CollectionActionLogDetail detail = new CollectionActionLogDetail(device, SUCCESS);
                        // auto update
                        if (autoUpdateRoute) {
                            deviceUpdateService.changeRoute(device, routeId);
                            
                            deviceRouteLocation.setRouteUpdated(true);
                            detail.setLastValue("Initial route:" + deviceRouteLocation.getInitialRouteName()
                                + " New route:" + deviceRouteLocation.getRouteName());
                        } else {
                            detail.setLastValue("Route:"+route.getPaoName()); 
                        }
                        
                        // success DeviceGroup
                        result.addDeviceToGroup(SUCCESS, device, detail);
                    }
                    
                    complete(device);
                }
                
                private synchronized void complete(SimpleDevice device) {
                    allDevices.remove(device);
                    if (allDevices.isEmpty() && !result.isComplete()) {
                        collectionActionService.updateResult(result, CommandRequestExecutionStatus.COMPLETE);
                        try {
                            alertCallback.handle(result);
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            };
            
            routeDiscoveryService.routeDiscovery(device, orderedRouteIds, routeFoundCallback, context.getYukonUser(),
                command, result);  
        }
        
        return result.getCacheKey();
    }
    
    private void addInitialRoute(YukonDevice device, 
                                 DeviceRouteLocation deviceRouteLocation,
                                 List<Integer> orderedRouteIds){
        LiteYukonPAObject devicePaoObj = databaseCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
        Integer initialRouteId = devicePaoObj.getRouteID();
        if (initialRouteId != DefaultRouteService.INVALID_ROUTE_ID) {
            deviceRouteLocation.setInitialRouteId(initialRouteId);
            LiteYukonPAObject initialRoute = databaseCache.getAllPaosMap().get(initialRouteId);
            deviceRouteLocation.setInitialRouteName(initialRoute.getPaoName());
            
            boolean removed = orderedRouteIds.remove(initialRouteId);
            if (removed) {
                orderedRouteIds.add(0, initialRouteId);
            }
        }
        
        deviceRouteLocation.setDeviceName(devicePaoObj.getPaoName());
    }
    
    @Override
    public List<DeviceRouteLocation> getLocations(int cacheKey) {
        return deviceRouteLocationCache.getIfPresent(cacheKey);
    }

    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.LOCATE_ROUTE;
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult result = collectionActionService.getResult(key);
        if (result.isCancelable()) {
            result.setCanceled(true);
            collectionActionService.updateResult(result, CommandRequestExecutionStatus.CANCELING);
            result.getCancellationCallbacks(StrategyType.PORTER).forEach(callback -> {
                commandExecutionService.cancelExecution(callback.getCommandCompletionCallback(), user, false);
            });
            collectionActionService.updateResult(result, CommandRequestExecutionStatus.CANCELLED);
        }
    }
}
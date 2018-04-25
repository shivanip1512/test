package com.cannontech.common.device.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class RouteDiscoveryServiceImpl implements RouteDiscoveryService {
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private PaoDao paoDao;
    @Autowired private ScheduledExecutor scheduledExecutor;
    @Autowired private CommandExecutionService commandRequestService;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    
    private List<SimpleCallback<Integer>> cancelationCallbackList = Collections.synchronizedList(new ArrayList<SimpleCallback<Integer>>()); 

    private static int MAX_ROUTE_RETRY = 10;
    private static int NEXT_ATTEMPT_WAIT = 5;
    private Logger log = YukonLogManager.getLogger(RouteDiscoveryServiceImpl.class);
    
    private Cache<Integer, Integer> collectionActionResultCacheKey =
            CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build();

    @Override
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> routeFoundCallback, LiteYukonUser user) {
        routeDiscovery(device, routeIds, routeFoundCallback, user, "ping", null);
    }
    
    @Override
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> routeFoundCallback,
            LiteYukonUser user, String command, CollectionActionResult result) {
      
        // init state for first route
        RouteDiscoveryState state = new RouteDiscoveryState();
        state.setRouteIds(routeIds);
        state.setAttemptCount(1);
        state.setRouteFoundCallback(routeFoundCallback);
        state.setUser(user);
        // run
        doNextDiscoveryRequest(device, state, command, result);
    }
    
    private void doNextDiscoveryRequest(final YukonDevice device, final RouteDiscoveryState state, final String commandString, CollectionActionResult result) {

        final String deviceLogStr = " DEVICE: " + paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()) + "' (" + device.getPaoIdentifier() + ")";
        
        // The Callback has been canceled.  Ending the recursive call for that given Callback.
        if(cancelationCallbackList.contains(state.getRouteFoundCallback())) {
            return;
        }
        
        // NO MORE ROUTES
        if (!state.isRoutesRemaining()) {
            
            // run callback with null routeId parameter
            scheduledExecutor.schedule(new Runnable() {
                
                @Override
                public void run() {
                    runCallbackWithNull(state, "No remaining routes.", deviceLogStr, "", null);
                }
            }, 1, TimeUnit.SECONDS);
        
        } else {

            // RUN COMMAND
            scheduledExecutor.schedule(new Runnable() {

                @Override
                public void run() {

                    // cmd
                    CommandRequestRouteAndDevice cmdReq = new CommandRequestRouteAndDevice(commandString,
                        new SimpleDevice(device.getPaoIdentifier()), state.getRouteIds().get(state.getRouteIdx()));

                    // callback
                    CommandCompletionCallback<CommandRequestRouteAndDevice> callback = new CommandCompletionCallback<CommandRequestRouteAndDevice>() {

                        @Override
                        public void processingExceptionOccured(String reason) {
                        
                    		runCallbackWithNull(state, "Processing exception: " + reason, deviceLogStr, "", null);
                    	}
                    	
                        @Override
                        public void receivedLastError(CommandRequestRouteAndDevice command, SpecificDeviceErrorDescription error) {

                            int errorCode = error.getErrorCode();
                            int currentAttemptCount = state.getAttemptCount();
                            int currentRouteId = state.getRouteIds().get(state.getRouteIdx());
                            int currentDeviceId = command.getDevice().getPaoIdentifier().getPaoId();
                            String routeLogStr = " ROUTE: " + databaseCache.getAllPaosMap().get(currentRouteId).getPaoName() + "' (" + currentRouteId + ")";
                            String deviceLogStr = " DEVICE: " + paoDao.getYukonPAOName(currentDeviceId) + "' (" + currentDeviceId + ")";

                            try {

                                // log that command failed
                                log.debug("Command " + commandString + " failed." + routeLogStr + deviceLogStr);

                                // error 54 means device does not exist, which likely means porter hasn't
                                // recieved the dbupdate msg for it yet, will try same route again
                                if (errorCode == 54 && currentAttemptCount < MAX_ROUTE_RETRY) {

                                    state.setAttemptCount(currentAttemptCount + 1);

                                    // log intent to retry on same reoute
                                    log.debug("Got error 54 (no device) during command " + commandString + "." + routeLogStr + deviceLogStr +
                                              ". Will attempt " + commandString + " on same route again. This was attempt #" + currentAttemptCount + ".");
                                }

                                // could not execute command on device on this route, try next route
                                else {

                                    // log intent to try next route due to excessive error 54
                                    if (errorCode == 54) {
                                        log.debug("Got error 54 (no device) during " + commandString + "." + routeLogStr + deviceLogStr +
                                                  ". The device still does not exists after " + currentAttemptCount + " attempts, something might be wrong. Moving on to next route.");
                                    }

                                    // setup state to run command on next route
                                    state.setAttemptCount(1);
                                    state.incrementRoute();

                                    log.debug("Will attempt " + commandString + " on next route." + routeLogStr + deviceLogStr);
                                }

                                // run next request
                                doNextDiscoveryRequest(device, state, commandString, result);

                            } catch (Exception e) {
                                runCallbackWithNull(state, "doNextDiscoveryRequest failed.", deviceLogStr, routeLogStr, e);
                            }

                        }

                        @Override
                        public void receivedLastResultString(CommandRequestRouteAndDevice command, String value) {

                            int foundRouteId = command.getRouteId();
                            int foundDeviceId = command.getDevice().getPaoIdentifier().getPaoId();
                            String routeLogStr = " ROUTE: " + databaseCache.getAllPaosMap().get(foundRouteId).getPaoName() + "' (" + foundRouteId + ")";
                            String deviceLogStr = " DEVICE: " + paoDao.getYukonPAOName(foundDeviceId) + "' (" + foundDeviceId + ")";
                            
                            // route found! run routeFoundCallback

                            // log route found
                            log.debug("Command " + commandString + " successful." + routeLogStr + deviceLogStr +
                                      ". Running route found callback " + state.getRouteFoundCallback() + ".");

                            runCallbackWithFoundRouteId(state, deviceLogStr, routeLogStr, foundRouteId);
                        }

                    };
                    
                    // execute
                    try {
                        List<CommandRequestRouteAndDevice> commands = Collections.singletonList(cmdReq);
                        if (result != null) {
                            // collecting cancellation callbacks
                            result.addCancellationCallback(
                                new CollectionActionCancellationCallback(StrategyType.PORTER, null, callback));
                            result.getExecution().setRequestCount(
                                result.getExecution().getRequestCount() + commands.size());
                            commandRequestExecutionDao.saveOrUpdate(result.getExecution());
                            collectionActionResultCacheKey.put(result.getCacheKey(), result.getCacheKey());
                            commandRequestService.execute(commands, callback, result.getExecution(), false,
                                state.getUser());

                        } else {
                            commandRequestService.execute(commands, callback,
                                DeviceRequestType.PING_DEVICE_ON_ROUTE_COMMAND, state.getUser());
                        }
                    } catch (Exception e) {
                        runCallbackWithNull(state, "Unknown exception.", deviceLogStr, "", e);
                    }
                }

            }, NEXT_ATTEMPT_WAIT, TimeUnit.SECONDS);
        }
    }

    private void runCallbackWithFoundRouteId(final RouteDiscoveryState state, final String deviceLogStr, final String routeLogStr, final int foundRouteId) {
        
    	scheduledExecutor.execute(new Runnable() {
            
            @Override
            public void run() {
            	try {
            		state.getRouteFoundCallback().handle(foundRouteId);
            	} catch (Exception e) {
            		runCallbackWithNull(state, "Route found callback failed.", deviceLogStr, routeLogStr, e);
            	}
            }
        });
    }
    
    private void runCallbackWithNull(final RouteDiscoveryState state, final String reasonForNull, final String deviceLogStr, final String routeLogStr, final Exception nullReasonException) {
        
        final String callbackLogStr = " CALLBACK: " + state.getRouteFoundCallback().toString();
        
        scheduledExecutor.execute(new Runnable() {
            
            @Override
            public void run() {
            	try {
            		state.getRouteFoundCallback().handle(null);
            		log.debug("Ran callback with null due to: " + reasonForNull + deviceLogStr + routeLogStr + callbackLogStr, nullReasonException);
            	} catch (Exception e) {
            		log.error("Failed to run callback with null. Original reason for null callback was: " + reasonForNull + deviceLogStr + routeLogStr + callbackLogStr, e);
            	}
            }
        });
    }
}
package com.cannontech.common.device.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.CommandRequestRouteAndDeviceExecutor;
import com.cannontech.common.device.commands.impl.StringCommandCallback;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class RouteDiscoveryServiceImpl implements RouteDiscoveryService {

    private CommandRequestRouteAndDeviceExecutor commandRequestRouteAndDeviceExecutor = null;
    private ScheduledExecutor scheduledExecutor = null;
    private PaoDao paoDao = null;
    private Map<SimpleCallback<Integer>, List<CommandCompletionCallback<CommandRequestRouteAndDevice>>> simpleCallbacksToCommandCompleteCallbacks = 
        new HashMap<SimpleCallback<Integer>, List<CommandCompletionCallback<CommandRequestRouteAndDevice>>>();
    private List<SimpleCallback<Integer>> cancelationCallbackList = Collections.synchronizedList(new ArrayList<SimpleCallback<Integer>>()); 
    

    private static int MAX_ROUTE_RETRY = 10;
    private static int NEXT_ATTEMPT_WAIT = 5;
    private Logger log = YukonLogManager.getLogger(RouteDiscoveryServiceImpl.class);
    
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> routeFoundCallback, LiteYukonUser user) {
        
        // init state for first route
        RouteDiscoveryState state = new RouteDiscoveryState();
        state.setRouteIds(routeIds);
        state.setAttemptCount(1);
        state.setRouteFoundCallback(routeFoundCallback);
        state.setUser(user);
        // run
        doNextDiscoveryRequest(device, state);
    }
    
    private void doNextDiscoveryRequest(final YukonDevice device, final RouteDiscoveryState state) {

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

            // RUN PING COMMAND
            scheduledExecutor.schedule(new Runnable() {

                @Override
                public void run() {

                    // cmd
                    CommandRequestRouteAndDevice cmdReq = new CommandRequestRouteAndDevice();
                    cmdReq.setCommandCallback(new StringCommandCallback("ping"));
                    cmdReq.setDevice(new SimpleDevice(device.getPaoIdentifier()));
                    cmdReq.setRouteId(state.getRouteIds().get(state.getRouteIdx()));

                    // callback
                    CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice>() {

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
                            String routeLogStr = " ROUTE: " + paoDao.getYukonPAOName(currentRouteId) + "' (" + currentRouteId + ")";
                            String deviceLogStr = " DEVICE: " + paoDao.getYukonPAOName(currentDeviceId) + "' (" + currentDeviceId + ")";

                            try {

                                // log that ping failed
                                log.debug("Ping failed." + routeLogStr + deviceLogStr);

                                // error 54 means device does not exist, which likely means porter hasn't
                                // recieved the dbupdate msg for it yet, will try same route again
                                if (errorCode == 54 && currentAttemptCount < MAX_ROUTE_RETRY) {

                                    state.setAttemptCount(currentAttemptCount + 1);

                                    // log intent to retry on same reoute
                                    log.debug("Got error 54 (no device) during ping." + routeLogStr + deviceLogStr +
                                              ". Will attempt ping on same route again. This was attempt #" + currentAttemptCount + ".");
                                }

                                // could not ping device on this route, try next route
                                else {

                                    // log intent to try next route due to excessive error 54
                                    if (errorCode == 54) {
                                        log.debug("Got error 54 (no device) during ping." + routeLogStr + deviceLogStr +
                                                  ". The device still does not exists after " + currentAttemptCount + " attempts, something might be wrong. Moving on to next route.");
                                    }

                                    // setup state to ping next route
                                    state.setAttemptCount(1);
                                    state.incrementRoute();

                                    log.debug("Will attempt ping on next route." + routeLogStr + deviceLogStr);
                                }

                                // run next request
                                doNextDiscoveryRequest(device, state);

                            } catch (Exception e) {
                                runCallbackWithNull(state, "doNextDiscoveryRequest failed.", deviceLogStr, routeLogStr, e);
                            }

                        }

                        @Override
                        public void receivedLastResultString(CommandRequestRouteAndDevice command, String value) {

                            int foundRouteId = command.getRouteId();
                            int foundDeviceId = command.getDevice().getPaoIdentifier().getPaoId();
                            String routeLogStr = " ROUTE: " + paoDao.getYukonPAOName(foundRouteId) + "' (" + foundRouteId + ")";
                            String deviceLogStr = " DEVICE: " +paoDao.getYukonPAOName(foundDeviceId) + "' (" + foundDeviceId + ")";
                            
                            // route found! run routeFoundCallback

                            // log route found
                            log.debug("Ping successful." + routeLogStr + deviceLogStr +
                                      ". Running route found callback " + state.getRouteFoundCallback() + ".");

                            runCallbackWithFoundRouteId(state, deviceLogStr, routeLogStr, foundRouteId);
                        }

                    };

                    synchronized(simpleCallbacksToCommandCompleteCallbacks){
                        List<CommandCompletionCallback<CommandRequestRouteAndDevice>> commandCompletionCallbackList = 
                            simpleCallbacksToCommandCompleteCallbacks.get(state.getRouteFoundCallback());
                        
                        if(commandCompletionCallbackList == null){
                            commandCompletionCallbackList = new ArrayList<CommandCompletionCallback<CommandRequestRouteAndDevice>>();
                            simpleCallbacksToCommandCompleteCallbacks.put(state.getRouteFoundCallback(),commandCompletionCallbackList);
                        }
                        commandCompletionCallbackList.add(callback);
                    }
                    
                    // execute
                    try {
                        commandRequestRouteAndDeviceExecutor.execute(Collections.singletonList(cmdReq), callback, DeviceRequestType.PING_DEVICE_ON_ROUTE_COMMAND, state.getUser());
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
    
    public void cancelRouteDiscovery(final List<SimpleCallback<Integer>> routeFoundCallbacks, final LiteYukonUser user) {
        
        // Stops any further commands from being sent out by currently running callbacks.
        for (SimpleCallback<Integer> routeFoundCallback : routeFoundCallbacks) {
            this.cancelationCallbackList.add(routeFoundCallback);
        }        
        
        // Sends out a cancel request for all the pings that have not had responses
        scheduledExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (SimpleCallback<Integer> routeFoundCallback : routeFoundCallbacks) {
                    synchronized(simpleCallbacksToCommandCompleteCallbacks){
                        List<CommandCompletionCallback<CommandRequestRouteAndDevice>> commandCompletionCallbacks = 
                            simpleCallbacksToCommandCompleteCallbacks.get(routeFoundCallback);
                        
                        // Sends a cancel command to all the commands that have been sent out.
                        if(commandCompletionCallbacks != null){
                            for ( CommandCompletionCallback<CommandRequestRouteAndDevice> commandCompletionCallback : commandCompletionCallbacks) {
                                commandRequestRouteAndDeviceExecutor.cancelExecution(commandCompletionCallback, user);
                            }
                        }
                    }
                }
            }
        });
    }

    @Required
    public void setCommandRequestRouteAndDeviceExecutor(
            CommandRequestRouteAndDeviceExecutor commandRequestRouteAndDeviceExecutor) {
        this.commandRequestRouteAndDeviceExecutor = commandRequestRouteAndDeviceExecutor;
    }
    
    @Required
    public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
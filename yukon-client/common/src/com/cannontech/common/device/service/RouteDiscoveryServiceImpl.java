package com.cannontech.common.device.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.CommandRequestRouteAndDeviceExecutor;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class RouteDiscoveryServiceImpl implements RouteDiscoveryService {

    private CommandRequestRouteAndDeviceExecutor commandRequestRouteAndDeviceExecutor = null;
    private ScheduledExecutor scheduledExecutor = null;
    private PaoDao paoDao = null;
    
    private static int MAX_ROUTE_RETRY = 10;
    private static int NEXT_ATTEMPT_WAIT = 5;
    private Logger log = YukonLogManager.getLogger(RouteDiscoveryServiceImpl.class);
    
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, SimpleCallback<Integer> routeFoundCallback, LiteYukonUser user) {
        
        // init state for first route
        RouteDiscoveryState state = new RouteDiscoveryState();
        state.setRouteIds(routeIds);
        state.setRouteIdx(0);
        state.setAttemptCount(1);
        state.setRouteFoundCallback(routeFoundCallback);
        
        // run
        doNextDiscoveryRequest(device, state);
    }
    
    private void doNextDiscoveryRequest(final YukonDevice device, final RouteDiscoveryState state) {
        
        final boolean infoEnabled = log.isInfoEnabled();
        
        // NO MORE ROUTES
        if (state.getRouteIdx() > state.getRouteIds().size()-1) {

            if (infoEnabled) {
                log.info("No more routes to attempt ping on for device '" + paoDao.getYukonPAOName(device.getDeviceId()) + "' (" + device.getDeviceId() + ").");
            }
            
            // run callback with null routeId parameter
            scheduledExecutor.schedule(new Runnable() {
                
                @Override
                public void run() {
            
                    try {
                        state.getRouteFoundCallback().handle(null);
                    } catch (Exception e) {
                        log.warn("Caught exception on result handler", e);
                    }
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
        
        
        // RUN PING COMMAND
        scheduledExecutor.schedule(new Runnable() {
            
            @Override
            public void run() {
                
                // cmd
                CommandRequestRouteAndDevice cmdReq = new CommandRequestRouteAndDevice();
                cmdReq.setCommand("ping");
                cmdReq.setDevice(device);
                cmdReq.setRouteId(state.getRouteIds().get(state.getRouteIdx()));
                
                // callback
                CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice>() {

                    @Override
                    public void receivedLastError(CommandRequestRouteAndDevice command, DeviceErrorDescription error) {
                        
                        int errorCode = error.getErrorCode();
                        int currentRouteIdx = state.getRouteIdx();
                        int currentAttemptCount = state.getAttemptCount();
                        int currentRouteId = state.getRouteIds().get(state.getRouteIdx());
                        
                        try {
                            
                            // log that ping failed
                            if (infoEnabled) {
                                log.info("Ping on route '" + paoDao.getYukonPAOName(currentRouteId) + "' (" + currentRouteId + ") " +
                                          "for device '" + paoDao.getYukonPAOName(device.getDeviceId()) + "' (" + device.getDeviceId() + ") " +
                                          "failed. ");
                            }
                            
                            // error 54 means device does not exist, which likely means porter hasn't
                            // recieved the dbupdate msg for it yet, will try same route again
                            if (errorCode == 54 && currentAttemptCount < MAX_ROUTE_RETRY) {
                                
                                state.setAttemptCount(currentAttemptCount + 1);
                                
                                // log intent to retry on same reoute
                                if (infoEnabled) {
                                    log.info("Attempted to ping on route '" + paoDao.getYukonPAOName(currentRouteId) + "' (" + currentRouteId + ") " +
                                             "for device '" + paoDao.getYukonPAOName(device.getDeviceId()) + "' (" + device.getDeviceId() + ") but got errorCode 54 - device \"does not exist\". " +
                                             "Going to attempt ping on same route again. This was attempt #" + currentAttemptCount + ".");
                                }
                            }
                            
                            // could not ping device on this route, try next route
                            else {
                                
                                // log intent to try next route due to excessive error 54
                                if (errorCode == 54) {
                                    if (infoEnabled) {
                                        log.info("Attempted to ping on route '" + paoDao.getYukonPAOName(currentRouteId) + "' (" + currentRouteId + ") " +
                                                 "for device '" + paoDao.getYukonPAOName(device.getDeviceId()) + "' (" + device.getDeviceId() + ") but got errorCode 54 - device \"does not exist\". " +
                                                 "The device still does not exists after " + currentAttemptCount + " attempts, something might be wrong. Moving on to next route.");
                                        
                                    }
                                }
                                
                                // setup state to ping next route
                                state.setAttemptCount(1);
                                state.setRouteIdx(currentRouteIdx + 1);
                                
                                if (infoEnabled) {
                                    log.info("Will attempt ping on next route for device '" + paoDao.getYukonPAOName(device.getDeviceId()) + "' (" + device.getDeviceId() + ").");
                                }
                            }
                            
                            // run next request
                            doNextDiscoveryRequest(device, state);
                            
                        } catch (Exception e) {
                            log.error("Unable to perform discovery request: " + e.getMessage());
                        }
                        
                    }

                    @Override
                    public void receivedLastResultString(CommandRequestRouteAndDevice command, String value) {

                        // route found! run routeFoundCallback
                        try {
                            
                            int foundRouteId = command.getRouteId();
                            int foundDeviceId = command.getDevice().getDeviceId();
                            
                            // log route found
                            if (infoEnabled) {
                                log.info("Ping successful on route '" + paoDao.getYukonPAOName(foundRouteId) + "' (" + foundRouteId + ") " +
                                          "for device '" + paoDao.getYukonPAOName(foundDeviceId) + "' (" + foundDeviceId + "). " +
                                          "Calling callback " + state.getRouteFoundCallback() + ".");
                            }
                            
                            state.getRouteFoundCallback().handle(foundRouteId);
                            
                        } catch (Exception e) {
                            log.error("Error running routeFoundCallback: " + e.getMessage());
                        }
                    }

                };
                
                // execute
                try {
                    commandRequestRouteAndDeviceExecutor.execute(Collections.singletonList(cmdReq), callback, state.getUser());
                } catch (PaoAuthorizationException e) {
                    log.error("User not authorized to run command: " + e.getMessage());
                }
                
            }
            
        }, NEXT_ATTEMPT_WAIT, TimeUnit.SECONDS);
        
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
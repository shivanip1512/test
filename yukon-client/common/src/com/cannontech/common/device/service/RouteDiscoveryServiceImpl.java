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
        state.setAttemptCount(1);
        state.setRouteFoundCallback(routeFoundCallback);
        
        // run
        doNextDiscoveryRequest(device, state);
    }
    
    private void doNextDiscoveryRequest(final YukonDevice device, final RouteDiscoveryState state) {
        
        final String deviceLogStr = " DEVICE: " + paoDao.getYukonPAOName(device.getDeviceId()) + "' (" + device.getDeviceId() + ")";
        
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
                    cmdReq.setCommand("ping");
                    cmdReq.setDevice(device);
                    cmdReq.setRouteId(state.getRouteIds().get(state.getRouteIdx()));

                    // callback
                    CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestRouteAndDevice>() {

                        @Override
                        public void receivedLastError(CommandRequestRouteAndDevice command, DeviceErrorDescription error) {

                            int errorCode = error.getErrorCode();
                            int currentAttemptCount = state.getAttemptCount();
                            int currentRouteId = state.getRouteIds().get(state.getRouteIdx());
                            int currentDeviceId = command.getDevice().getDeviceId();
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
                            int foundDeviceId = command.getDevice().getDeviceId();
                            String routeLogStr = " ROUTE: " + paoDao.getYukonPAOName(foundRouteId) + "' (" + foundRouteId + ")";
                            String deviceLogStr = " DEVICE: " +paoDao.getYukonPAOName(foundDeviceId) + "' (" + foundDeviceId + ")";
                            
                            // route found! run routeFoundCallback
                            try {

                                // log route found
                                log.debug("Ping successful." + routeLogStr + deviceLogStr +
                                          ". Running route found callback " + state.getRouteFoundCallback() + ".");

                                state.getRouteFoundCallback().handle(foundRouteId);

                            } catch (Exception e) {
                                runCallbackWithNull(state, "Route found callback failed.", deviceLogStr, routeLogStr, e);
                            }
                        }

                    };

                    // execute
                    try {
                        commandRequestRouteAndDeviceExecutor.execute(Collections.singletonList(cmdReq), callback, state.getUser());
                    } catch (PaoAuthorizationException e) {
                        runCallbackWithNull(state, "User not authorized to run command for device.", deviceLogStr, "", e);
                        
                    } catch (Exception e) {
                        runCallbackWithNull(state, "Unknown exception.", deviceLogStr, "", e);
                    }

                }

            }, NEXT_ATTEMPT_WAIT, TimeUnit.SECONDS);
        }
    }

    private void runCallbackWithNull(RouteDiscoveryState state, String reasonForNull, String deviceLogStr, String routeLogStr, Exception e) {
        
        String callbackLogStr = " CALLBACK: " + state.getRouteFoundCallback().toString();
        
        try{
            state.getRouteFoundCallback().handle(null);
            log.debug("Ran callback with null due to: " + reasonForNull + deviceLogStr + routeLogStr + callbackLogStr, e);
        } catch (Exception ex) {
            log.error("Failed to run callback with null. Original reason for null callback was: " + reasonForNull + deviceLogStr + routeLogStr + callbackLogStr, ex);
        }
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
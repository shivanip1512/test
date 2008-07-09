package com.cannontech.common.device.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

public class RouteDiscoveryServiceImpl implements RouteDiscoveryService, MessageListener, InitializingBean {

    private DeviceDao deviceDao = null;
    private DeviceUpdateService deviceUpdateService = null;
    private BasicServerConnection porterConnection = null;
    private ScheduledExecutor scheduledExecutor = null;

    private Map<Long, Event> eventListenerMap = new HashMap<Long, Event>();
    private Logger log = YukonLogManager.getLogger(this.getClass());

    @Override
    public void afterPropertiesSet() throws Exception {
        porterConnection.addMessageListener(this);
    }
    
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, Boolean doPutconfig) throws Exception {

        sendRouteDiscoveryRequest(device, routeIds, doPutconfig, 0, 1);
    }

    private void sendRouteDiscoveryRequest(YukonDevice device, List<Integer> routeIds, Boolean doPutconfig, int routeIdx, int attemptCount) throws Exception {

        // don't attempt discovery if there are no more routes to try
        if (routeIdx < routeIds.size()) {

            if (!porterConnection.isValid()) {
                throw new Exception("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
            }

            // pull next routeId from list
            int routeId = routeIds.get(routeIdx);

            // setup discovery event
            try {

                // this is the event listener that will respond to the request and either send another
                // for the next route if not found, or send config command to device if on this route
                long id = RandomUtils.nextInt();
                RouteDiscoveryEvent event = new RouteDiscoveryEvent(id, routeIds, doPutconfig, routeIdx, attemptCount);
                eventListenerMap.put(id, event);

                // write pil request
                writePilRequest(device.getDeviceId(), "ping", id, 13, routeId); 
            } 
            catch (NotFoundException e) {
                throw new Exception(e.getMessage());
            }
        }
        else {
            throw new Exception("Unable to ping meter on any of the available routes.");
        }
    }
    
    void sendPutconfigRequest(YukonDevice device, int routeId) throws Exception {

        if (!porterConnection.isValid()) {
            throw new Exception("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        // setup putconfig event
        long id = RandomUtils.nextInt(); 
        PutconfigEvent event = new PutconfigEvent(id);
        eventListenerMap.put(id, event);

        // write pil request
        writePilRequest(device.getDeviceId(), "putconfig emetcon intervals", id, 13, routeId); 
    }
    

    public void messageReceived(MessageEvent e)
    {
        Message in = e.getMessage();        
        if(in instanceof Return)
        {
            final Return returnMsg = (Return)in;
            final Event event = eventListenerMap.get(returnMsg.getUserMessageID());

            if(event != null) {

                Runnable eventRunner = new Runnable() {

                    @Override
                    public void run() {

                        log.info("Message Received [ID:"+ returnMsg.getUserMessageID() + 
                                 " DevID:" + returnMsg.getDeviceID() + 
                                 " Command:" + returnMsg.getCommandString() +
                                 " Result:" + returnMsg.getResultString() + 
                                 " Status:" + returnMsg.getStatus() +
                                 " More:" + returnMsg.getExpectMore()+"]");

                        if(returnMsg.getExpectMore() == 0) {

                            log.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());

                            try {

                                Boolean doneProcessing = event.messageReceived(returnMsg);
                                if (doneProcessing) {
                                    eventListenerMap.remove(event.getId());
                                }

                            } catch (Exception e) {
                                log.error("Unable to process message.", e);
                            }
                        }
                    }
                };

                eventRunner.run();
            }
        }
    }

    private interface Event {

        public Boolean messageReceived(Return returnMsg) throws Exception;
        public Long getId();
    }

    private class RouteDiscoveryEvent implements Event  {

        private Long id;
        private List<Integer> routeIds;
        private Boolean doPutconfig;
        private int currentRouteIdx;
        private int currentAttemptCount;

        public RouteDiscoveryEvent(Long id, List<Integer> routeIds, Boolean doPutConfig, int routeIdx, int attemptCount) {

            this.id = id;
            this.routeIds = routeIds;
            this.doPutconfig = doPutConfig;
            this.currentRouteIdx = routeIdx;
            this.currentAttemptCount = attemptCount;
        }

        public Boolean messageReceived(Return returnMsg) throws Exception {

            int deviceId = returnMsg.getDeviceID();
            int returnStatus = returnMsg.getStatus();
            final YukonDevice device = deviceDao.getYukonDevice(deviceId);

            // responded to ping on route
            if(returnStatus == 0) {   

                log.info("RouteDiscoveryEvent - Ping Successful (deviceId = " + deviceId + " status=" + returnStatus + ")");

                // set route
                int routeId = routeIds.get(currentRouteIdx);
                deviceUpdateService.changeRoute(device, routeId);

                // send putconfig command
                if (doPutconfig && DeviceTypesFuncs.isMCT410(device.getType())) {
                    sendPutconfigRequest(device, routeId);
                }
                
                return true;
            }
            else {   
                
                log.info("RouteDiscoveryEvent - Ping Failed (deviceId = " + deviceId + " status=" + returnStatus + ")");
                
                // not a success, try next route
                final int nextRouteIdxToTry;
                final int attemptCount;
                
                // meter was not found to exist yet, wait 5 secs and try that same route again
                // if hasn't responded in an 20min, move to next route. If there was a problem
                // getting porter to see new meter at least we'll eventually exit this retry loop
                if (returnStatus == 54 && currentAttemptCount < 240) {
                    
                    log.info("RouteDiscoveryEvent - Meter does not yet exist, retry in 5 sec. (deviceId = " + deviceId + " status=" + returnStatus + ")");
                    
                    nextRouteIdxToTry = currentRouteIdx;
                    attemptCount = currentAttemptCount + 1;
                    
                    // schedule to try again in 5 min
                    scheduledExecutor.schedule(new Runnable() {
                         
                            @Override
                            public void run() {
                                
                                try {
                                    sendRouteDiscoveryRequest(device, routeIds, doPutconfig, nextRouteIdxToTry, attemptCount);
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                }
                            }
                        
                        },
                    5, TimeUnit.SECONDS);
                    
                    return true;
                    
                }
                else {
                    
                    if (currentAttemptCount >= 240) {
                        log.info("RouteDiscoveryEvent - Status says meter still does not exist, moving to next route. (deviceId = " + deviceId + " status=" + returnStatus + ")");
                    }
                    
                    nextRouteIdxToTry = currentRouteIdx + 1;
                    attemptCount = 1;
                    
                    sendRouteDiscoveryRequest(device, routeIds, doPutconfig, nextRouteIdxToTry, attemptCount);
                    return true;
                }
            }
        }

        public Long getId() {
            return id;
        }
    }

    private class PutconfigEvent implements Event {

        private Long id;

        public PutconfigEvent(Long id) {

            this.id = id;
        }

        public Boolean messageReceived(Return returnMsg) {

            int deviceId = returnMsg.getDeviceID();
            int returnStatus = returnMsg.getStatus();

            // responded to ping on route
            if(returnStatus == 0) {   
                log.info("PutconfigEvent - Putconfig Successful (deviceId = " + deviceId + " status=" + returnStatus + ")");
            }
            else {
                log.info("PutconfigEvent - Putconfig Failed (deviceId = " + deviceId + " status=" + returnStatus + ")");
            }

            return true;
        }

        public Long getId() {
            return id;
        }
    }


    private void writePilRequest(int deviceId, String commandStr, long id, int priority, int routeId) {

        Request pilRequest = null;
        commandStr += " update";
        commandStr += " noqueue";
        pilRequest = new Request(deviceId, commandStr, id);
        pilRequest.setPriority(priority);

        pilRequest.setRouteID(routeId);
        porterConnection.write(pilRequest);
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Required
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
        this.deviceUpdateService = deviceUpdateService;
    }
    
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    
    @Required
    public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }
}

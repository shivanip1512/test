package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

public class LongLoadProfileServiceImpl implements LongLoadProfileService {
    private BasicServerConnection porterConnection;
    private PorterQueueDataService queueDataService;
    Logger log = YukonLogManager.getLogger(LongLoadProfileServiceImpl.class);
    private ScheduledExecutor executor;
    
    private Map<Long, Integer> outstandingCancelRequestIds = new HashMap<Long, Integer>();
    private Map<Long, Long> recentlyCanceledRequestIds = new HashMap<Long, Long>();
    private Set<Long> receivedCancelRequestIds = new HashSet<Long>();
    
    private Set<Long> recentlyReceivedRequestIds = new HashSet<Long>();
    private Map<Long, LongLoadProfileService.CompletionCallback> currentRequestIds = new HashMap<Long, LongLoadProfileService.CompletionCallback>();
    private MapQueue<Integer,ProfileRequestInfo> pendingDeviceRequests = new MapQueue<Integer,ProfileRequestInfo>();
    private Map<Integer,ProfileRequestInfo> currentDeviceRequests = new HashMap<Integer,ProfileRequestInfo>();
    private DateFormat cmdDateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public void initialize() {
        porterConnection.addMessageListener(new MessageListener() {
            public void messageReceived(MessageEvent e) {
                Message message = e.getMessage();
                if (message instanceof Return) {
                    Return returnMsg = (Return) message;
                    handleReturnMessage(returnMsg);
                }
            }
        });
    }
    
    public synchronized void initiateLongLoadProfile(LiteYukonPAObject device, int channel, Date start, Date stop, LongLoadProfileService.CompletionCallback runner) {
        Validate.isTrue(channel <= 4, "channel must be less than or equal to 4");
        Validate.isTrue(channel > 0, "channel must be greater than 0");
        Validate.isTrue(DeviceTypesFuncs.isLoadProfile4Channel(device.getType()), "Device must support 4 channel load profile (DeviceTypesFuncs.isLoadProfile4Channel)");

        Request req = new Request();
        StringBuilder formatString = new StringBuilder("getvalue lp channel ");
        formatString.append(channel);
        formatString.append(" ");
        if (start != null) {
            formatString.append(cmdDateFormatter.format(start));
            formatString.append(" ");
        }
        if (stop == null) {
            stop = new Date();
        }
        formatString.append(cmdDateFormatter.format(stop));
        if (runner == null) {
            formatString.append(" background");
        }
        req.setCommandString(formatString.toString());
        req.setDeviceID(device.getLiteID());
        long requestId = RandomUtils.nextInt();
        req.setUserMessageID(requestId);
        ProfileRequestInfo info = new ProfileRequestInfo();
        info.from = start;
        info.to = stop;
        info.request = req;
        info.runner = runner;
        info.requestId = requestId;
        info.channel = channel;
        handleOutgoingMessage(info);
        // if write fails, we don't want this to happen
        currentRequestIds.put(requestId, runner);
    }

    private synchronized void handleOutgoingMessage(ProfileRequestInfo info) {
        int deviceId = info.request.getDeviceID();
        if (currentDeviceRequests.containsKey(deviceId)) {
            log.info("ongoing request for device id " + deviceId + " already in progress, queueing command");
            pendingDeviceRequests.offer(deviceId,info);
        } else {
            queueRequest(deviceId, info, false);
        }
    }

    /**
     * Sends the request to porter, fills out the start variables, and sets the timer.
     * 
     * <p>The queue parameter should be set to false when this method is called in
     * response to a user action (clicking the start button). When this method
     * is called in one of the callbacks, it should be set to true. When used in 
     * a callback situation, we don't care if porter is down, we just don't want to
     * stall processing.
     * @param deviceId
     * @param info
     * @param queue
     */
    private void queueRequest(int deviceId, final ProfileRequestInfo info, boolean queue) {
        try {
            log.debug("sending request id " + info.request.getUserMessageID() 
                      + " for device id " + deviceId + ": " + info.request.getCommandString());
            if (queue) {
                porterConnection.queue(info.request);
            } else {
                porterConnection.write(info.request);
            }
            // if write fails we don't want this to happen
            currentDeviceRequests.put(deviceId, info);
            // start timer to monitor request
            executor.schedule(new Runnable() {
                public void run() {
                    checkRequestStatus(info);
                }
            }, 5 * 60, TimeUnit.SECONDS);
        } catch (RuntimeException e) {
            // clear out pending requests
            currentRequestIds.remove(info.request.getUserMessageID());
            throw e;
        }        
    }
    
    public synchronized void checkRequestStatus(final ProfileRequestInfo info) {
        long requestId = info.request.getUserMessageID();
        // see if request is still pending
        if (!currentRequestIds.containsKey(requestId)) {
            // request has already completed
            return;
        }
        
        // see if a message was heard recently
        boolean containedIt = recentlyReceivedRequestIds.remove(requestId);
        if (!containedIt) {
            // haven't heard anything, lets ask porter about our request
            long countForRequest;
            try {
                countForRequest = queueDataService.getMessageCountForRequest(requestId);
            } catch (RuntimeException e) {
                log.error("Exception from porter in timer thread",e);
                countForRequest = 0;
            }
            if (countForRequest == 0) {
                // our request has probably died, cancel it
                currentRequestIds.remove(requestId);
                int deviceId = info.request.getDeviceID();
                currentDeviceRequests.remove(deviceId);
                executor.execute(new Runnable() {
                    public void run() {
                        info.runner.onFailure(-1, "Unknown Error");
                    }
                });
                
                // see if we have a pending command to send
                queueNextPendingRequest(deviceId);
                
                return;
            }
        }
        // good news, lets restart the timer for another 5 minutes
        executor.schedule(new Runnable() {
            public void run() {
                checkRequestStatus(info);
            }
        }, 5 * 60, TimeUnit.SECONDS);

    }

    

    private synchronized void handleReturnMessage(Return returnMsg) {
        
        // unique requestId of the request this return is for
        long requestId = returnMsg.getUserMessageID();
        
        // was this request recently canceled?
        if(recentlyCanceledRequestIds.containsKey(requestId)){
            
            long  requestIdOfCancelCommand = recentlyCanceledRequestIds.get(requestId);
            
            // have we heard back fromt he cancel command yet? 
            // if so, this is the last we'll hear from this request, ok to request the next pending
            if(receivedCancelRequestIds.contains(requestIdOfCancelCommand)){
             
                recentlyCanceledRequestIds.remove(requestId);
                receivedCancelRequestIds.remove(requestIdOfCancelCommand);
                
                queueNextPendingRequest(outstandingCancelRequestIds.remove(requestIdOfCancelCommand));
                
            }
            
        // this return is for one of our currently executing requests, check if it is finished yet
        }
        else if (currentRequestIds.containsKey(requestId)) {
            log.debug("received return message for request id " + requestId);
            
            //TODO restore 
            recentlyReceivedRequestIds.add(requestId);
            // check for expect more
            boolean finished = returnMsg.getExpectMore() == 0;
            if (finished) {
                final int deviceId = returnMsg.getDeviceID();
                recentlyReceivedRequestIds.remove(requestId);
                currentDeviceRequests.remove(deviceId);
                // get runner and execute it on the global thread pool
                final LongLoadProfileService.CompletionCallback runnable = currentRequestIds.remove(requestId);
                final int returnStatus = returnMsg.getStatus();
                final String resultString = returnMsg.getResultString();
                final long requestIdToRemove = requestId;
                executor.execute(new Runnable() {
                    public void run() {

                        // success
                        if(returnStatus == 0){
                            runnable.onSuccess("");
                        }

                        // failure - onFailure will take status and resultString and come up with a message for the email
                        else{
                            runnable.onFailure(returnStatus, resultString);
                        }
                    }
                });

                // if finished and ok see if we have a pending command to send, otherwise let cancel handle it
                if(returnStatus == 0){
                    queueNextPendingRequest(deviceId);
                }
                else{
                    // send it through the cancel process to be sure it has been cleaned up
                    sendCancelCommand(requestIdToRemove, deviceId);
                }
                   

            }
            
        // this return is for a cancel command that we sent out
        }
        else if(outstandingCancelRequestIds.containsKey(requestId)){
            
            receivedCancelRequestIds.add(requestId);
            
        }
    }
    

    
    public synchronized boolean removePendingLongLoadProfileRequest(LiteYukonPAObject device, long requestId, LiteYukonUser user) {
        
        boolean removed = false;
        int deviceId = device.getLiteID();
        final LiteYukonUser cancelUser = user;
        
        // first place to to look is the currect device request, if it is there we will have to send kill command to porter and clean up
        final ProfileRequestInfo info = currentDeviceRequests.get(deviceId);
        if(info != null){

            if(info.requestId == requestId){
                
                // cleanup
                recentlyReceivedRequestIds.remove(requestId);
                currentDeviceRequests.remove(deviceId);
                // get runner and execute it on the global thread pool
                final LongLoadProfileService.CompletionCallback runnable = currentRequestIds.remove(requestId);
                executor.execute(new Runnable() {
                    public void run() {
                        runnable.onCancel(cancelUser);
                    }
                });
                
                // send command to porter
                sendCancelCommand(requestId, deviceId);

                removed = true;
            }
        }
        
        // our device was not in current request, look for it in the pending requests and remove it if found
        if(!removed){
            
            List<ProfileRequestInfo> pendingRequestsForDevice = pendingDeviceRequests.get(deviceId);
            
            for(ProfileRequestInfo pendingInfo :  pendingRequestsForDevice){
                
                if(pendingInfo.requestId == requestId){
                    
                    pendingDeviceRequests.removeValue(deviceId,pendingInfo);
                    // get runner and execute it on the global thread pool
                    final LongLoadProfileService.CompletionCallback runnable = currentRequestIds.remove(requestId);
                    executor.execute(new Runnable() {
                        public void run() {
                            runnable.onCancel(cancelUser);
                        }
                    });
                    
                    removed = true;
                    break;
                }
            }
        }

        return removed;
    }
    
    
    
    public synchronized void sendCancelCommand(long requestIdToBeCanceled, int deviceId){
        
        Request req = new Request();
        StringBuilder formatString = new StringBuilder("getvalue lp cancel update noqueue");
        req.setCommandString(formatString.toString());
        
        req.setDeviceID(deviceId);
        long requestIdOfCancelCommand = RandomUtils.nextInt();
        req.setUserMessageID(requestIdOfCancelCommand);
        
        // run cancel command
        porterConnection.write(req);
        
        // save requestId and callback to map
        int deviceIdForWhomToGrabNextPendingWhenFinishedCanceling = deviceId;
        outstandingCancelRequestIds.put(requestIdOfCancelCommand, deviceIdForWhomToGrabNextPendingWhenFinishedCanceling);
        recentlyCanceledRequestIds.put(requestIdToBeCanceled, requestIdOfCancelCommand);
        
    }
    
    
    private synchronized boolean queueNextPendingRequest(int deviceId) {
        // see if we have a pending command to send
        boolean queuedRequest = false;
        
        ProfileRequestInfo foundInfo = pendingDeviceRequests.poll(deviceId);
        if (foundInfo != null) {
            log.info("sending queued request for device id " + deviceId);
            queueRequest(deviceId, foundInfo, true);
            queuedRequest = true;
        }
        
        return queuedRequest;
    }

    
    public synchronized List<ProfileRequestInfo> getPendingLongLoadProfileRequests(LiteYukonPAObject device) {
        int deviceId = device.getLiteID();
        ProfileRequestInfo info = currentDeviceRequests.get(deviceId);
        List<ProfileRequestInfo> result = new ArrayList<ProfileRequestInfo>();
        
        if(info != null){
            result.add(info);
        }
        
        List<ProfileRequestInfo> pending = pendingDeviceRequests.get(deviceId);
        result.addAll(pending);
        
        return result;
        
    }
        
    int debugSizeOfCollections() {
        return recentlyReceivedRequestIds.size()
            + currentRequestIds.size()
            + pendingDeviceRequests.size()
            + currentDeviceRequests.size();
    }
    
    public void printSizeOfCollections(int deviceId) {
        System.out.println("recentlyReceivedRequestIds = " + recentlyReceivedRequestIds.size());
        System.out.println("currentRequestIds = " + currentRequestIds.size());
        System.out.println("- " + currentRequestIds.toString());
        System.out.println("pendingDeviceRequests = " +pendingDeviceRequests.size(deviceId));
        System.out.println("- " + pendingDeviceRequests.get(deviceId).toString());
        System.out.println("currentDeviceRequests = " +currentDeviceRequests.size());
        System.out.println("- " + currentDeviceRequests.toString());
        
    }
    
    @Required
    public void setExecutor(ScheduledExecutor executor) {
        this.executor = executor;
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    
    @Required
    public void setQueueDataService(PorterQueueDataService queueDataService) {
        this.queueDataService = queueDataService;
    }
}

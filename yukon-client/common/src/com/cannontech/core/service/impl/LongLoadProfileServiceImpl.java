package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import com.cannontech.common.util.CompletionCallback;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
    private Set<Long> recentlyReceivedRequestIds = new HashSet<Long>();
    private Map<Long, CompletionCallback> currentRequestIds = new HashMap<Long, CompletionCallback>();
    private MapQueue<Integer,ProfileRequestInfo> pendingDeviceRequests = new MapQueue<Integer,ProfileRequestInfo>();
    private Map<Integer,ProfileRequestInfo> currentDeviceRequests = new HashMap<Integer,ProfileRequestInfo>();
    private DateFormat cmdDateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public synchronized void initiateLongLoadProfile(LiteYukonPAObject device, int channel, Date start, Date stop, CompletionCallback runner) {
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
                        info.runner.onFailure();
                    }
                });
                
                // see if we have a pending command to send
                ProfileRequestInfo foundInfo = pendingDeviceRequests.poll(deviceId);
                if (foundInfo != null) {
                    log.info("sending queued request for device id " + deviceId);
                    queueRequest(deviceId, foundInfo, true);
                }
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

    private synchronized void handleReturnMessage(Return returnMsg) {
        // see if we're interested
        long requestId = returnMsg.getUserMessageID();
        if (currentRequestIds.containsKey(requestId)) {
            log.debug("received return message for request id " + requestId);
            //TODO restore 
            recentlyReceivedRequestIds.add(requestId);
            // check for expect more
            boolean expectMore = returnMsg.getExpectMore() == 1;
            if (!expectMore) {
                int deviceId = returnMsg.getDeviceID();
                recentlyReceivedRequestIds.remove(requestId);
                currentDeviceRequests.remove(deviceId);
                // get runner and execute it on the global thread pool
                final CompletionCallback runnable = currentRequestIds.remove(requestId);
                executor.execute(new Runnable() {
                    public void run() {
                        runnable.onSuccess();
                    }
                });

                // see if we have a pending command to send
                ProfileRequestInfo foundInfo = pendingDeviceRequests.poll(deviceId);
                if (foundInfo != null) {
                    log.info("sending queued request for device id " + deviceId);
                    queueRequest(deviceId, foundInfo, true);
                }
            }
        }
    }
    
    public synchronized List<ProfileRequestInfo> getPendingLongLoadProfileRequests(LiteYukonPAObject device) {
        int deviceId = device.getLiteID();
        ProfileRequestInfo info = currentDeviceRequests.get(deviceId);
        if (info == null) {
            return Collections.emptyList();
        }
        
        List<ProfileRequestInfo> result = new ArrayList<ProfileRequestInfo>();
        
        result.add(info);
        
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
    
    @Required
    public void setExecutor(ScheduledExecutor executor) {
        this.executor = executor;
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    public void setQueueDataService(PorterQueueDataService queueDataService) {
        this.queueDataService = queueDataService;
    }

}

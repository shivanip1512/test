package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.toggleProfiling.model.RfnVoltageProfile;
import com.cannontech.amr.toggleProfiling.model.RfnVoltageProfile.ProfilingStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.InitiateLoadProfileRequestException;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.service.ActivityLoggerService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.PorterDynamicPaoInfoService;
import com.cannontech.core.service.PorterDynamicPaoInfoService.VoltageProfileDetails;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.core.service.SystemDateFormattingService.DateFormatEnum;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;
public class LoadProfileServiceImpl implements LoadProfileService {
    private final static Logger log = YukonLogManager.getLogger(LoadProfileServiceImpl.class);
    private final static Random random = new Random();
    private final static Pattern blockCount = Pattern.compile("Reading (\\d+) blocks");

    @Autowired private PorterQueueDataService queueDataService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private SystemDateFormattingService systemDateFormattingService;
    @Autowired private ActivityLoggerService activityLoggerService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private @Qualifier("main") ScheduledExecutor executor;
    private BasicServerConnection porterConnection;

    private Map<Long, Integer> outstandingCancelRequestIds = new HashMap<Long, Integer>();
    private Map<Long, Long> recentlyCanceledRequestIds = new HashMap<Long, Long>();
    private Set<Long> receivedCancelRequestIds = new HashSet<Long>();
    
    private Map<Long, Long> expectedReturnCount = new HashMap<Long, Long>();
    private Map<Long, Long> receivedReturnsCount = new HashMap<Long, Long>();
    private Set<Long> completedRequestIds = new HashSet<Long>();
    private Map<Long, String> lastReturnMsgs = new HashMap<Long, String>();
    
    private Set<Long> recentlyReceivedRequestIds = new HashSet<Long>();
    private Map<Long, CompletionCallback> currentRequestIds = new HashMap<Long, CompletionCallback>();
    private MapQueue<Integer,ProfileRequestInfo> pendingDeviceRequests = new MapQueue<Integer,ProfileRequestInfo>();
    private Map<Integer,ProfileRequestInfo> currentDeviceRequests = new HashMap<Integer,ProfileRequestInfo>();
    private Map<Long, Integer> enableDisableVoltageProfileRequestIds = new HashMap<Long, Integer>();
    
    @Autowired private PorterDynamicPaoInfoService porterDynamicPaoInfoService;
    
    private Map<Integer, RfnVoltageProfile> rfnVoltageProfileCache = new HashMap<Integer, RfnVoltageProfile>();
    private Map<Integer, RfnVoltageProfile> dynamicRfnVoltageProfileCache  = new HashMap<Integer, RfnVoltageProfile>();
   
    @PostConstruct
    public void initialize() {
        porterConnection.addMessageListener(new MessageListener() {
            @Override
            public void messageReceived(MessageEvent e) {
                Message message = e.getMessage();
                if (message instanceof Return) {
                    Return returnMsg = (Return) message;
                    handleReturnMessage(returnMsg);
                }
            }
        });
    }
    
    @Override
    public synchronized long initiateLoadProfile(LiteYukonPAObject device, int channel, Date start,
            Date stop, CompletionCallback callback, YukonUserContext userContext) {
        if (device.getPaoType().isRfMeter()) {
            return initiateLoadProfileRfn(device, start, stop, callback, userContext);
        } else {
            return initiateLoadProfilePlc(device, channel, start, stop, callback, userContext);
        }
    }
   
    private synchronized long initiateLoadProfilePlc(LiteYukonPAObject device, int channel,
            Date start, Date stop, CompletionCallback callback, YukonUserContext userContext) {
        Validate.isTrue(channel <= 4, "channel must be less than or equal to 4");
        Validate.isTrue(channel > 0, "channel must be greater than 0");
        Validate.isTrue(paoDefinitionDao.isTagSupported(device.getPaoType(), PaoTag.LOAD_PROFILE), "Device must support 4 channel load profile (DeviceTypesFuncs.isLoadProfile4Channel)");

        // build command
        Request req = new Request();
        StringBuilder formatString = new StringBuilder("getvalue lp channel ");
        formatString.append(channel);
        formatString.append(" ");
        if (start != null) {
            DateFormat cmdFormatter = systemDateFormattingService.getSystemDateFormat(DateFormatEnum.LoadProfile);
            formatString.append(cmdFormatter.format(start));
            formatString.append(" ");
        }
        if (stop == null) {
            stop = new Date();
        }
        DateFormat cmdFormatter = systemDateFormattingService.getSystemDateFormat(DateFormatEnum.LoadProfile);
        formatString.append(cmdFormatter.format(stop));

        
        // setup request
        req.setCommandString(formatString.toString());
        req.setDeviceID(device.getLiteID());
        long requestId = random.nextInt();
        req.setUserMessageID(requestId);
        
        //  expectedReturnCount is populated by Porter's return value
        receivedReturnsCount.put(requestId, 0L);
        
        // setup profile request info
        ProfileRequestInfo info = new ProfileRequestInfo();
        info.from = start;
        info.to = stop;
        info.request = req;
        info.callback = callback;
        info.requestId = requestId;
        info.channel = channel;
        info.userName = userContext.getYukonUser().getUsername();
        info.percentDone = 0.00;
        
        // activity log
        activityLoggerService.logEvent(userContext.getYukonUser().getUserID(), ActivityLogActions.INITIATE_PROFILE_REQUEST_ACTION, req.getCommandString());
        
        // pass off to handleOutgoingMessage
        handleOutgoingMessage(info);
        // if write fails, we don't want this to happen
        currentRequestIds.put(requestId, callback);
        return requestId;
    }
    
    
    private synchronized long initiateLoadProfileRfn(LiteYukonPAObject device,
            Date start, Date stop, CompletionCallback callback, YukonUserContext userContext) {
        Validate.isTrue(paoDefinitionDao.isTagSupported(device.getPaoType(), PaoTag.VOLTAGE_PROFILE),
                        "Device must support voltage profile");

        // build command
        Request req = new Request();
        StringBuilder formatString = new StringBuilder("getvalue voltage profile ");
        if (start != null) {
            DateFormat cmdFormatter = systemDateFormattingService.getSystemDateFormat(DateFormatEnum.LoadProfile);
            formatString.append(cmdFormatter.format(start));
            formatString.append(" ");
        }
        if (stop == null) {
            stop = new Date();
        }
        DateFormat cmdFormatter = systemDateFormattingService.getSystemDateFormat(DateFormatEnum.LoadProfile);
        formatString.append(cmdFormatter.format(stop));

        // setup request
        req.setCommandString(formatString.toString());
        req.setDeviceID(device.getLiteID());
        long requestId = random.nextInt();
        req.setUserMessageID(requestId);

        // setup profile request info
        ProfileRequestInfo info = new ProfileRequestInfo();
        info.from = start;
        info.to = stop;
        info.request = req;
        info.callback = callback;
        info.requestId = requestId;
        info.userName = userContext.getYukonUser().getUsername();
        info.percentDone = 0.00;

        // activity log
        activityLoggerService.logEvent(userContext.getYukonUser().getUserID(),
                                       ActivityLogActions.INITIATE_PROFILE_REQUEST_ACTION,
                                       req.getCommandString());

        // pass off to handleOutgoingMessage
        handleOutgoingMessage(info);
        // if write fails, we don't want this to happen
        currentRequestIds.put(requestId, callback);
        return requestId;
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
     * Sends a voltage profile enable/disable command to porter.
     * 
     * @param deviceId
     * @enableProfiling - Indicates whether to enable/disable voltage profiling.
     */
    private void enableVoltageProfile(int device, boolean enableProfiling) {

        // build command
        Request req = new Request();
        StringBuilder formatString = new StringBuilder("putconfig voltage profile");
        if (enableProfiling) {
            formatString.append(" enable");
        } else {
            formatString.append(" disable");
        }
        formatString.append(" ");

        // setup request
        req.setCommandString(formatString.toString());
        req.setDeviceID(device);

        long requestId = random.nextInt();
        req.setUserMessageID(requestId);

        // run enable/disable command
        try {
            porterConnection.write(req);
            enableDisableVoltageProfileRequestIds.put(requestId, device);
        } catch (Exception e) {
            throw new InitiateLoadProfileRequestException();
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
            info.request.setTimeStamp(new Date());
            log.debug("updated request time to " + info.request.getTimeStamp() + " for request id " 
                      + info.request.getUserMessageID() + " for device id " + deviceId + ": " 
                      + info.request.getCommandString());
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
                @Override
                public void run() {
                    checkRequestStatus(info);
                }
            }, 5 * 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            // clear out pending requests
            currentRequestIds.remove(info.requestId);
            expectedReturnCount.remove(info.requestId);
            receivedReturnsCount.remove(info.requestId);
            throw new InitiateLoadProfileRequestException();
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
            log.debug("discovered possible abandoned request id " + requestId + ", asking porter");
            // haven't heard anything, lets ask porter about our request
            long countForRequest;
            try {
                countForRequest = queueDataService.getMessageCountForRequest(requestId);
            } catch (RuntimeException e) {
                log.error("Exception from porter in timer thread for request id " + requestId,e);
                countForRequest = 0;
            }
            if (countForRequest == 0) {
                // our request has probably died, cancel it
                log.warn("porter indicates abandonded request id " + requestId + ", failing");
                currentRequestIds.remove(requestId);
                int deviceId = info.request.getDeviceID();
                currentDeviceRequests.remove(deviceId);
                expectedReturnCount.remove(info.requestId);
                receivedReturnsCount.remove(info.requestId);
                if(info.callback != null){
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            info.callback.onFailure(-1, "Load Profile command has not responded, request has been abandoned.");
                        }
                    });
                }
                
                // see if we have a pending command to send
                queueNextPendingRequest(deviceId);
                
                return;
            }
            log.debug("porter returned countForRequest of " + countForRequest + ", continuing");
        }
        // good news, lets restart the timer for another 5 minutes
        executor.schedule(new Runnable() {
            @Override
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
            
            
            if (returnMsg.getMessages().size() != 0) {
                if (receivedReturnsCount.get(requestId) != null) {
                    receivedReturnsCount.put(requestId, receivedReturnsCount.get(requestId) + 1);
                }
            }
            
            // check for expect more
            boolean finished = returnMsg.getExpectMore() == 0;
            if (!finished) {
            
                if (!expectedReturnCount.containsKey(requestId)) {
                    if (returnMsg.getResultString() != null) {
                        Matcher m = blockCount.matcher(returnMsg.getResultString());
                        
                        if (m.find()) {
                            expectedReturnCount.put(requestId, Long.parseLong(m.group(1)));
                        }
                    }
                }
                
            } else {
                
                int deviceId = returnMsg.getDeviceID();
                
                final CompletionCallback runnable = currentRequestIds.remove(requestId);
                final int returnStatus = returnMsg.getStatus();
                final String resultString = returnMsg.getResultString();
                
                recentlyReceivedRequestIds.remove(requestId);
                currentDeviceRequests.remove(deviceId);
                
                if(returnStatus == 0){
                    completedRequestIds.add(requestId);
                }
                expectedReturnCount.remove(requestId);
                receivedReturnsCount.remove(requestId);
                lastReturnMsgs.put(requestId, resultString);
                
                log.debug("received last return for request id " + requestId + ", status was " + returnStatus + ", response was " + returnMsg.getResultString());
                // get callback and execute it on the global thread pool
                if (runnable != null) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {

                            // success
                            if (returnStatus == 0) {
                                runnable.onSuccess("");
                            }

                            // failure - onFailure will take status and resultString and come up
                            // with a message for the email
                            else {
                                runnable.onFailure(returnStatus, resultString);
                            }
                        }
                    });
                }

                // if finished and ok see if we have a pending command to send, otherwise let cancel handle it
                if(returnStatus == 0){
                    queueNextPendingRequest(deviceId);
                }
                else{
                    // send it through the cancel process to be sure it has been cleaned up
                    sendCancelCommand(requestId, deviceId);
                }
                   

            }
            
        // this return is for a cancel command that we sent out
        }
        else if(outstandingCancelRequestIds.containsKey(requestId)){
                    
            receivedCancelRequestIds.add(requestId);
            
        } else if (enableDisableVoltageProfileRequestIds.containsKey(requestId)) {
            int deviceId = returnMsg.getDeviceID();
            log.debug("received response for request id " + requestId + ",response was " + returnMsg.getResultString());
            boolean finished = returnMsg.getExpectMore() == 0;
            if(finished) {
                rfnVoltageProfileCache.remove(deviceId);
                enableDisableVoltageProfileRequestIds.remove(requestId);
            }
            
        }  else {
            log.debug("received unwanted return for request id " + requestId);
        }
    }

    @Override
    public synchronized boolean removePendingLoadProfileRequest(YukonPao device,
                                                                long requestId,
                                                                YukonUserContext userContext) {
        boolean removed = false;
        int deviceId = device.getPaoIdentifier().getPaoId();
        final LiteYukonUser cancelUser = userContext.getYukonUser();
        
        // first place to to look is the correct device request, if it is there we will have to send kill command to porter and clean up
        final ProfileRequestInfo info = currentDeviceRequests.get(deviceId);
        if(info != null){

            if(info.requestId == requestId){
                
                // cleanup
                recentlyReceivedRequestIds.remove(requestId);
                currentDeviceRequests.remove(deviceId);
                expectedReturnCount.remove(requestId);
                receivedReturnsCount.remove(requestId);
                // get callback and execute it on the global thread pool
                final CompletionCallback runnable = currentRequestIds.remove(requestId);
                if (runnable != null) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            runnable.onCancel(cancelUser);
                        }
                    });
                }
                
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
                    expectedReturnCount.remove(requestId);
                    receivedReturnsCount.remove(requestId);
                    // get callback and execute it on the global thread pool
                    final CompletionCallback runnable = currentRequestIds.remove(requestId);
                    if (runnable != null) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                runnable.onCancel(cancelUser);
                            }
                        });
                    }
                    
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
        long requestIdOfCancelCommand = random.nextInt();
        req.setUserMessageID(requestIdOfCancelCommand);
        
        log.info("sending cancel command for device id " + deviceId + ", request id " + requestIdToBeCanceled + "; this is request id " + requestIdOfCancelCommand);
        
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

    @Override
    public synchronized List<ProfileRequestInfo> getPendingLoadProfileRequests(YukonPao device) {
        int deviceId = device.getPaoIdentifier().getPaoId();
        ProfileRequestInfo info = currentDeviceRequests.get(deviceId);
        List<ProfileRequestInfo> result = new ArrayList<ProfileRequestInfo>();
        
        if(info != null){
            
            info.percentDone =  calculatePercentDone(info.requestId);
            result.add(info);
        }
        
        List<ProfileRequestInfo> pending = pendingDeviceRequests.get(deviceId);
        
        for(ProfileRequestInfo pendinginfo : pending){
            
            pendinginfo.percentDone =  calculatePercentDone(pendinginfo.requestId);
            result.add(pendinginfo);
        }
        
        return result;
        
    }
        
    @Override
    public Double calculatePercentDone(Long requestId){
        
        Double percentDone = 0.0;
        
        if(completedRequestIds.contains(requestId)){
            return 100.0;
        }
        else if (!currentRequestIds.containsKey(requestId)) {
            return null;
        }
        else if(receivedReturnsCount.containsKey(requestId) && expectedReturnCount.containsKey(requestId)){
            percentDone =  ((double)receivedReturnsCount.get(requestId) / (double)expectedReturnCount.get(requestId)) * 100.0;    
        }

        return percentDone;
    }

    @Override
    public String getLastReturnMsg(long requestId){
        return lastReturnMsgs.get(requestId);
    }
    
    int debugSizeOfCollections() {
        return recentlyReceivedRequestIds.size()
            + currentRequestIds.size()
            + pendingDeviceRequests.size()
            + currentDeviceRequests.size();
    }

    @Override
    public List<Map<String, String>> getPendingRequests(LiteYukonPAObject device,  YukonUserContext userContext) {
        
        //  pending past profiles
        List<Map<String, String>> pendingRequests = new ArrayList<Map<String, String>>();
        Collection<ProfileRequestInfo> loadProfileRequests = getPendingLoadProfileRequests(device);
        for (ProfileRequestInfo info : loadProfileRequests) {
            HashMap<String, String> data = new HashMap<String, String>();
            
            String email = "";
            if (info.callback != null) {
                email = info.callback.toString();
            }
            data.put("email", email);
            data.put("from",
                     dateFormattingService.format(info.from,
                                                  DateFormattingService.DateFormatEnum.DATE,
                                                  userContext));
            data.put("to", dateFormattingService.format(info.to,
                                                        DateFormattingService.DateFormatEnum.DATE,
                                                        userContext));
            data.put("command", info.request.getCommandString());
            data.put("requestId", Long.toString(info.request.getUserMessageID()));
            data.put("channel", ((Integer) info.channel).toString());
            data.put("userName", info.userName);
            data.put("percentDone", info.percentDone.toString());
            pendingRequests.add(data);
        }

        return pendingRequests;
    }

    @Override
    public void startVoltageProfilingForDevice(int deviceId) {
        enableProfilingForDevice(deviceId, true);
    }

    @Override
    public void stopVoltageProfilingForDevice(int deviceId) {
        enableProfilingForDevice(deviceId, false);
    }

    private void enableProfilingForDevice(int deviceId, boolean enableProfiling) {
        RfnVoltageProfile rfnVoltageProfile = new RfnVoltageProfile();
        enableVoltageProfile(deviceId, enableProfiling);
        
        if (enableProfiling) {
            Instant stopDate = new DateTime().plusWeeks(2).toInstant();
            rfnVoltageProfile.setProfilingStatus(ProfilingStatus.ENABLED);
            rfnVoltageProfile.setStopDate(stopDate);
            rfnVoltageProfile.setVoltageProfilingRate(getRfnVoltageProfileDetails(deviceId).getVoltageProfilingRate());
        } else {
            rfnVoltageProfile.setProfilingStatus(ProfilingStatus.DISABLED);
            rfnVoltageProfile.setVoltageProfilingRate(getRfnVoltageProfileDetails(deviceId).getVoltageProfilingRate());
        }
        rfnVoltageProfileCache.put(deviceId, rfnVoltageProfile);
    }
    
    @Override
    public void loadDynamicRfnVoltageProfileCache(int deviceId) {
        VoltageProfileDetails voltageDetails = porterDynamicPaoInfoService.getVoltageProfileDetails(deviceId);

        RfnVoltageProfile rfnVoltageProfile = new RfnVoltageProfile();
        rfnVoltageProfile.setDeviceID(deviceId);
        rfnVoltageProfile.setProfilingStatus(voltageDetails.enabledUntil == null ? ProfilingStatus.DISABLED
                : ProfilingStatus.ENABLED);
        rfnVoltageProfile.setStopDate(voltageDetails.enabledUntil);
        rfnVoltageProfile.setVoltageProfilingRate(voltageDetails.profileInterval);
        dynamicRfnVoltageProfileCache .put(deviceId, rfnVoltageProfile);
    }

    @Override
    public RfnVoltageProfile getRfnVoltageProfileDetails(int deviceId) {

        if (rfnVoltageProfileCache.containsKey(deviceId)) {
            return rfnVoltageProfileCache.get(deviceId);
        } else {
            if (dynamicRfnVoltageProfileCache .get(deviceId) == null) {
                RfnVoltageProfile rfnVoltageProfile = new RfnVoltageProfile();
                rfnVoltageProfile.setProfilingStatus(ProfilingStatus.UNKNOWN);
                dynamicRfnVoltageProfileCache.put(deviceId, rfnVoltageProfile);
            }
            return dynamicRfnVoltageProfileCache .get(deviceId);
        }
    }

   
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
}

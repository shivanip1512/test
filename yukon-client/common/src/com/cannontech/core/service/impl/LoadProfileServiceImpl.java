package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.InitiateLoadProfileRequestException;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.service.ActivityLoggerService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.core.service.SystemDateFormattingService.DateFormatEnum;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;

public class LoadProfileServiceImpl implements LoadProfileService {
    private BasicServerConnection porterConnection;
    private PorterQueueDataService queueDataService;
    private DBPersistentDao dbPersistentDao;
    private DateFormattingService dateFormattingService;
    private SystemDateFormattingService systemDateFormattingService;
    private ActivityLoggerService activityLoggerService = null;
    private Logger log = YukonLogManager.getLogger(LoadProfileServiceImpl.class);
    private ScheduledExecutor executor;
    private PaoDefinitionDao paoDefinitionDao;
    
    private Map<Long, Integer> outstandingCancelRequestIds = new HashMap<Long, Integer>();
    private Map<Long, Long> recentlyCanceledRequestIds = new HashMap<Long, Long>();
    private Set<Long> receivedCancelRequestIds = new HashSet<Long>();
    
    private Map<Long, Long> expectedReturnCount = new HashMap<Long, Long>();
    private Map<Long, Long> receivedReturnsCount = new HashMap<Long, Long>();
    private Set<Long> completedRequestIds = new HashSet<Long>();
    private Map<Long, String> lastReturnMsgs = new HashMap<Long, String>();
    
    private Set<Long> recentlyReceivedRequestIds = new HashSet<Long>();
    private Map<Long, LoadProfileService.CompletionCallback> currentRequestIds = new HashMap<Long, LoadProfileService.CompletionCallback>();
    private MapQueue<Integer,ProfileRequestInfo> pendingDeviceRequests = new MapQueue<Integer,ProfileRequestInfo>();
    private Map<Integer,ProfileRequestInfo> currentDeviceRequests = new HashMap<Integer,ProfileRequestInfo>();

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
    
    public synchronized void initiateLoadProfile(LiteYukonPAObject device, int channel, Date start, Date stop, LoadProfileService.CompletionCallback runner, YukonUserContext userContext) {
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
        if (runner == null) {
            formatString.append(" background");
        }
        
        // setup reuqest
        req.setCommandString(formatString.toString());
        req.setDeviceID(device.getLiteID());
        long requestId = RandomUtils.nextInt();
        req.setUserMessageID(requestId);
        
        // get interval
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        int loadProfileDemandRate = deviceLoadProfile.getLoadProfileDemandRate();
        int voltageDemandRate = deviceLoadProfile.getVoltageDmdRate();
        
        int minutesPerInterval = 1;
        if(channel == 1){
            minutesPerInterval = loadProfileDemandRate / 60;
        }
        else if (channel == 4){
            minutesPerInterval = voltageDemandRate / 60;
        }
        
        Calendar startCal = dateFormattingService.getCalendar(userContext);
        startCal.setTime(start);
        
        Calendar stopCal = dateFormattingService.getCalendar(userContext);
        stopCal.setTime(stop);
        
        long minutesBetween = (stopCal.getTimeInMillis() - startCal.getTimeInMillis()) / 60000;

        long expectedIntervals = minutesBetween / minutesPerInterval;
        long expectedPorterReturns = expectedIntervals / 6; //porter gets 6 per request by definition
        
        expectedReturnCount.put(requestId, expectedPorterReturns);
        receivedReturnsCount.put(requestId, 0L);
        
        // setup profile request info
        ProfileRequestInfo info = new ProfileRequestInfo();
        info.from = start;
        info.to = stop;
        info.request = req;
        info.runner = runner;
        info.requestId = requestId;
        info.channel = channel;
        info.userName = userContext.getYukonUser().getUsername();
        info.percentDone = 0.00;
        
        // activity log
        activityLoggerService.logEvent(userContext.getYukonUser().getUserID(), ActivityLogActions.INITIATE_PROFILE_REQUEST_ACTION, req.getCommandString());
        
        // pass off to handleOutgoingMessage
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
                executor.execute(new Runnable() {
                    public void run() {
                        info.runner.onFailure(-1, "Load Profile command has not responded, request has been abandoned.");
                    }
                });
                
                // see if we have a pending command to send
                queueNextPendingRequest(deviceId);
                
                return;
            }
            log.debug("porter returned countForRequest of " + countForRequest + ", continuing");
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
            
            
            if(returnMsg.getVector().size() != 0){
                receivedReturnsCount.put(requestId, receivedReturnsCount.get(requestId) + 1);
            }
            
            // check for expect more
            boolean finished = returnMsg.getExpectMore() == 0;
            if (finished) {
                
                int deviceId = returnMsg.getDeviceID();
                
                final LoadProfileService.CompletionCallback runnable = currentRequestIds.remove(requestId);
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
                // get runner and execute it on the global thread pool
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
                    sendCancelCommand(requestId, deviceId);
                }
                   

            }
            
        // this return is for a cancel command that we sent out
        }
        else if(outstandingCancelRequestIds.containsKey(requestId)){
            
            receivedCancelRequestIds.add(requestId);
            
        } else {
            log.debug("received unwanted return for request id " + requestId);
        }
    }
    

    
    public synchronized boolean removePendingLoadProfileRequest(LiteYukonPAObject device, long requestId, YukonUserContext userContext) {
        
        boolean removed = false;
        int deviceId = device.getLiteID();
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
                // get runner and execute it on the global thread pool
                final LoadProfileService.CompletionCallback runnable = currentRequestIds.remove(requestId);
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
                    expectedReturnCount.remove(requestId);
                    receivedReturnsCount.remove(requestId);
                    // get runner and execute it on the global thread pool
                    final LoadProfileService.CompletionCallback runnable = currentRequestIds.remove(requestId);
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

    
    
    
    public synchronized List<ProfileRequestInfo> getPendingLoadProfileRequests(LiteYukonPAObject device) {
        int deviceId = device.getLiteID();
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

    public String getLastReturnMsg(long requestId){
        return lastReturnMsgs.get(requestId);
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
    
    public List<Map<String, String>> getPendingRequests(LiteYukonPAObject device,  YukonUserContext userContext) {
        
        //  pending past profiles
        List<Map<String, String>> pendingRequests = new ArrayList<Map<String, String>>();
        Collection<ProfileRequestInfo> loadProfileRequests = getPendingLoadProfileRequests(device);
        for (ProfileRequestInfo info : loadProfileRequests) {
            HashMap<String, String> data = new HashMap<String, String>();
            
            String email = "";
            if (info.runner != null) {
                email = info.runner.toString();
            }
            data.put("email", email);
            data.put("from",
                     dateFormattingService.format(info.from,
                                                      DateFormattingService.DateFormatEnum.DATE,
                                                      userContext));
            data.put("to",
                     dateFormattingService.format(DateUtils.addDays(info.to,
                                                                        -1),
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

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Required
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }
    
    @Required
    public void setActivityLoggerService(ActivityLoggerService activityLoggerService) {
        this.activityLoggerService = activityLoggerService;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}

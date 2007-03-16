package com.cannontech.core.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.mail.MessagingException;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.MapQueue;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.tools.email.DelayedEmailRunner;
import com.cannontech.tools.email.EmailService;
import com.cannontech.yukon.BasicServerConnection;

public class PorterConnectionServiceImpl implements LongLoadProfileService {
    private BasicServerConnection porterConnection;
    private EmailService emailService;
    Logger log = YukonLogManager.getLogger(PorterConnectionServiceImpl.class);
    private Executor executor;
    private Map<Long, Runnable> currentRequestIds = new HashMap<Long, Runnable>();
    private MapQueue<Integer,ProfileRequestInfo> pendingDeviceRequests = new MapQueue<Integer,ProfileRequestInfo>();
    private Map<Integer,ProfileRequestInfo> currentDeviceRequests = new HashMap<Integer,ProfileRequestInfo>();
    private DateFormat cmdDateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public void initiateLongLoadProfile(final LiteYukonPAObject device, int channel, Date start, Date stop, String emailAddress) {
        try {
            DelayedEmailRunner runner = new DelayedEmailRunner(emailService);
            runner.setRecipient(emailAddress);
            runner.setSubject("Long Load Profile is Complete");
            runner.setBody("Long load profile collection for " + device.getPaoName() + " is complete.");
            initiateLongLoadProfile(device, channel, start, stop, runner);
        } catch (MessagingException e) {
            log.error(e);
        }
    }

    public synchronized void initiateLongLoadProfile(LiteYukonPAObject device, int channel, Date start, Date stop, Runnable runner) {
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
            queueRequest(deviceId, info);
        }
    }

    private void queueRequest(int deviceId, ProfileRequestInfo info) {
        try {
            log.debug("sending request id " + info.request.getUserMessageID() 
                      + " for device id " + deviceId + ": " + info.request.getCommandString());
            porterConnection.write(info.request);
            // if write fails we don't want this to happen
            currentDeviceRequests.put(deviceId, info);
        } catch (RuntimeException e) {
            // clear out pending requests
            currentRequestIds.remove(info.request.getUserMessageID());
            throw e;
        }        
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
            // check for expect more
            boolean expectMore = returnMsg.getExpectMore() == 1;
            if (!expectMore) {
                int deviceId = returnMsg.getDeviceID();
                currentDeviceRequests.remove(deviceId);
                // get runner and execute it on the global thread pool
                Runnable runnable = currentRequestIds.remove(requestId);
                executor.execute(runnable);

                // see if we have a pending command to send
                ProfileRequestInfo foundInfo = pendingDeviceRequests.poll(deviceId);
                if (foundInfo != null) {
                    log.info("sending queued request for device id " + deviceId);
                    queueRequest(deviceId, foundInfo);
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

    @Required
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Required
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

}

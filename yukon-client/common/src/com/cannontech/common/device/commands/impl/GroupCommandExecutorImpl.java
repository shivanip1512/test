package com.cannontech.common.device.commands.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequest;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailService;

public class GroupCommandExecutorImpl implements GroupCommandExecutor {

    private EmailService emailService = null;
    private CommandRequestExecutor commandRequestExecutor = null;
    private MeterDao meterDao = null;
    private ScheduledExecutor executor = null;
    private PointFormattingService pointFormattingService = null;
    private Logger logger = YukonLogManager.getLogger(GroupCommandExecutorImpl.class);
    
    private static final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setCommandRequestExecutor(CommandRequestExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }

    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    public void setExecutor(ScheduledExecutor executor) {
        this.executor = executor;
    }
    
    public void setPointFormattingService(PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }

    public void execute(final Set<Integer> deviceIds, final String command, 
                        final String emailAddresses, LiteYukonUser user)
            throws MessagingException, PaoAuthorizationException {

        final Date startTime = new Date();
        
        final StringBuffer resultBuffer = new StringBuffer();
        final List<Integer> missedList = new ArrayList<Integer>();
        final StringBuffer successBuffer = new StringBuffer();

        // Make a list of all of the device ids to execute command for
        final Set<Integer> pendingIdList = new HashSet<Integer>();
        pendingIdList.addAll(deviceIds);

        // Execute the command for all device ids
        for (final Integer id : deviceIds) {
            CommandRequest request = new CommandRequest();
            request.setDeviceId(id);
            request.setCommand(command);

            CollectingCommandCompletionCallback collectingCommandCompletionCallback = new CollectingCommandCompletionCallback() {

                public void complete() {
                    
                    synchronized (resultBuffer) {

                        Meter device = meterDao.getForId(id);
                        String formattedDeviceName = meterDao.getFormattedDeviceName(device);
                        resultBuffer.append("\n\n" + format.format(new Date()) + 
                                            " - " + formattedDeviceName + "\n");


                        // Add any errors to the result string buffer
                        List<DeviceErrorDescription> errors = getErrors();
                        for (DeviceErrorDescription ded : errors) {
                            resultBuffer.append("  " + ded.toString() + "\n");

                        }

                        // An error means we missed this device
                        if (errors.size() > 0) {
                            missedList.add(id);
                        }

                        // Add any successful readings to the success buffer
                        List<PointValueHolder> values = getValues();
                        if(values.size() > 0){
                            successBuffer.append("  " + formattedDeviceName + 
                                                 " (" + device.getMeterNumber() + ") - ");
                            boolean first = true;
                            for(PointValueHolder value : values){
                                
                                String valueString = 
                                    pointFormattingService.getValueString(value, 
                                                                          PointFormattingService.Format.FULL);

                                if(!first){
                                    successBuffer.append("; ");
                                }
                                successBuffer.append(valueString);
                                
                                first = false;
                            }
                            successBuffer.append("\n");
                        }
                        
                        // Add devices with no errors and no point data to the success list -
                        // means that the reading was successful but no point exists to 
                        // archive the data for
                        if(errors.size() == 0 && values.size() == 0){
                            successBuffer.append("  " + formattedDeviceName + 
                                                 " (" + device.getMeterNumber() + 
                                                 ") - No data archived. See log below.");
                            successBuffer.append("\n");
                        }

                        // Add any results strings to the result string buffer
                        List<String> resultStrings = getResultStrings();
                        for (String result : resultStrings) {
                            resultBuffer.append("  " + result + "\n");
                        }

                        pendingIdList.remove(id);

                        if (pendingIdList.size() == 0) {
                            sendResultEmail(command,
                                            resultBuffer.toString(),
                                            successBuffer.toString(),
                                            missedList,
                                            emailAddresses,
                                            startTime,
                                            deviceIds.size());
                        }

                    }

                }
            };
            commandRequestExecutor.execute(
                   Collections.singletonList(request),
                   collectingCommandCompletionCallback,
                   user);
        }

    }

    private void sendResultEmail(final String command, final String results, final String success, 
            final List<Integer> missedList, final String emailAddresses, final Date startTime, 
            final int numberOfDevices) {
        
        executor.execute(new Runnable() {
            public void run() {
        
                DefaultEmailMessage emailMessage = new DefaultEmailMessage();
                emailMessage.setSubject("Group Request completed");
        
                StringBuffer body = new StringBuffer();
        
                // Add summary to email body
                body.append("Results Summary for group request '" + command + "':");
                body.append("\n\n");
                
                Date now = new Date();
                body.append("Started group processing: " + format.format(startTime));
                body.append("  ");
                body.append("Finished group processing: " + format.format(now) + "\n\n");

                body.append("Total number of devices: " + numberOfDevices + "\n");
                body.append("Successful: " + (numberOfDevices - missedList.size()) + "\n");
                body.append("Missed: " + missedList.size() + "\n\n");

                // Success list to email body
                body.append("Success:");
                body.append("\n\n");
                body.append(success);
                body.append("\n\n");
                
                
                // Add missed list to email body
                body.append("Missed:");
                body.append("\n\n");
                for (Integer id : missedList) {
                    String formattedDeviceName = meterDao.getFormattedDeviceName(meterDao.getForId(id));
                    body.append("  " + formattedDeviceName + "\n");
                }
                body.append("\n\n");

                // Add porter results to email body
                body.append("Results:");
                body.append(results);
                body.append("\n\n");
        
                body.append("You have received this notification because your email address was " +
                        "added to the notification list of a Yukon group processing event. ");
        
                emailMessage.setBody(body.toString());

                // Send email to all email addresses supplied
                String[] addresses = emailAddresses.split(",");
                for(String address : addresses){
                    
                    try {
                        emailMessage.setRecipient(address);
                        emailService.sendMessage(emailMessage);
                    } catch (Exception e) {
                        logger.error("There was a problem sending a group request completion email to: " + 
                                     address, e);
                    }
                }
            }});
    }
}

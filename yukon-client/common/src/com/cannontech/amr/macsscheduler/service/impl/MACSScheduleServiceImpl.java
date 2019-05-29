package com.cannontech.amr.macsscheduler.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.macsscheduler.model.MacsException;
import com.cannontech.amr.macsscheduler.model.MacsException.MACSExceptionType;
import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.amr.macsscheduler.model.MacsScheduleHelper;
import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.macs.message.AddSchedule;
import com.cannontech.message.macs.message.DeleteSchedule;
import com.cannontech.message.macs.message.Info;
import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.message.macs.message.RetrieveScript;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.macs.message.ScriptFile;
import com.cannontech.message.macs.message.UpdateSchedule;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.IMACSConnection;
import com.google.common.base.Strings;

public class MACSScheduleServiceImpl implements MACSScheduleService, MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(MACSScheduleServiceImpl.class);
    @Resource(name="macsConnection")
    private IMACSConnection connection;
    @Autowired private ToolsEventLogService eventLog;
    @Autowired private NextValueHelper nextValueHelper;
    //seconds to wait for reply from MACS Service.
    private final Seconds secondsToWait = Seconds.seconds(10);
    //SOE_Tag/Message - messages received from MACS Service.
    private Map<Integer, Message> cachedMessages = new ConcurrentHashMap<>();
    //SOE_Tag waiting for reply from MACS Service.
    private Queue<Integer> waiting = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void initialize() {
        connection.addMessageListener(this);
    }
   
    @Override
    public boolean isScheduleNameExists(String name, int id){
        return connection.isScheduleNameExists(name, id);
    }

    @Override
    public void start(int scheduleId, Date startDate, Date stopDate, LiteYukonUser user) throws MacsException {
        Schedule schedule = getScheduleById(scheduleId);
        schedule.setUpdatingState(true);
        try {
            connection.sendStartStopSchedule(schedule, startDate, stopDate, OverrideRequest.OVERRIDE_START,
                user.getUsername());
            eventLog.macsScriptStarted(user, schedule.getScheduleName(), startDate, stopDate);
        } catch (IOException e) {
            log.error(e);
            throw new MacsException(MACSExceptionType.ERROR, e);
        }
    }

    @Override
    public void stop(int scheduleId, Date stopDate, LiteYukonUser user) throws MacsException {
        Schedule schedule = getScheduleById(scheduleId);
        schedule.setUpdatingState(true);
        try {
            connection.sendStartStopSchedule(schedule, null, stopDate, OverrideRequest.OVERRIDE_STOP,
                user.getUsername());
            eventLog.macsScriptStopped(user, schedule.getScheduleName(), stopDate);
        } catch (IOException e) {
            log.error(e);
            throw new MacsException(MACSExceptionType.ERROR, e);
        }
    }

    @Override
    public void enableDisableSchedule(int scheduleId, LiteYukonUser user) throws MacsException {
        Schedule schedule = getScheduleById(scheduleId);
        schedule.setUpdatingState(true);
        try {
            connection.sendEnableDisableSchedule(schedule, user.getUsername());
            eventLog.macsScriptEnabled(user, schedule.getScheduleName(), schedule.getCurrentState());
        } catch (IOException e) {
            log.error(e);
            throw new MacsException(MACSExceptionType.ERROR, e);
        };
    }
    
    @Override
    public void delete(int scheduleId, LiteYukonUser user) throws MacsException {
        DateTime now = DateTime.now();
        Schedule schedule = getScheduleById(scheduleId);
        
        DeleteSchedule deleteSchedule = new DeleteSchedule();
        deleteSchedule.setScheduleId(scheduleId);
        int soeTag = sendMessage(deleteSchedule, user.getUsername(), now);
        
        String description = getDescription("Delete schedule (" + schedule.getScheduleName() + ")", soeTag, now);
        getMessage(soeTag, now, description);
        // got the confirmation message
        eventLog.macsScriptDeleted(user, schedule.getScheduleName());
    }
    
    @Override
    public MacsSchedule getMacsScheduleById(int scheduleId) {
        return MacsScheduleHelper.convert(getScheduleById(scheduleId));
    }
    
    private Schedule getScheduleById(int scheduleId) {
       return  Arrays.asList(connection.retrieveSchedules()).stream()
                                         .filter(s -> s.getId() == scheduleId)
                                         .findFirst()
                                         .orElseThrow(() -> new NotFoundException("Could not find schedule with id " + scheduleId));
    }

    @Override
    public List<MacsSchedule> getAllSchedules() {
        return Arrays.asList(connection.retrieveSchedules()).stream()
                .map(s -> MacsScheduleHelper.convert(s)).collect(Collectors.toList());
    }
    
    @Override
    public List<MacsSchedule> getSchedulesByCategory(String category) {
        return Arrays.asList(connection.getCategories(category)).stream()
                .map(s -> MacsScheduleHelper.convert(s)).collect(Collectors.toList());
    }
    
    @Override
    public void updateSchedule(MacsSchedule macsSchedule, LiteYukonUser user) throws DuplicateException, MacsException {
        if (isScheduleNameExists(macsSchedule.getScheduleName(), macsSchedule.getId())) {
           throw new DuplicateException("Schedule "+ macsSchedule.getScheduleName()+ " already exists.");
        }
        
        DateTime now = DateTime.now();
        Schedule schedule =  MacsScheduleHelper.convert(macsSchedule);
        
        UpdateSchedule modifiedSchedule = new UpdateSchedule();
        modifiedSchedule.setSchedule(schedule);
        if(macsSchedule.isScript()){
            modifiedSchedule.setScript(macsSchedule.getScriptOptions().getScriptText());
        }

        int soeTag = sendMessage(modifiedSchedule, user.getUsername(), now);
        
        String description = getDescription("Update schedule (" + schedule.getScheduleName() + ")", soeTag, now);
        getMessage(soeTag, now, description);
        // got the confirmation message
        eventLog.macsScriptUpdated(user, schedule.getScheduleName());
    }
    
    @Override
    public int createSchedule(MacsSchedule macsSchedule, LiteYukonUser user) throws DuplicateException, MacsException{
        if (isScheduleNameExists(macsSchedule.getScheduleName(), macsSchedule.getId())) {
            throw new DuplicateException("Schedule " + macsSchedule.getScheduleName() + " already exists.");
        }
        
        DateTime now = DateTime.now();
        Schedule schedule =  MacsScheduleHelper.convert(macsSchedule);
        
        AddSchedule newSchedule = new AddSchedule();
        newSchedule.setSchedule(schedule);
        if (macsSchedule.isScript()) {
            newSchedule.setScript(macsSchedule.getScriptOptions().getScriptText());
        }
        int soeTag = sendMessage(newSchedule, user.getUsername(), now);

        String description = getDescription("Create schedule (" + schedule.getScheduleName() + ")", soeTag, now);
        Schedule createdSchedule = (Schedule) getMessage(soeTag, now, description);
        eventLog.macsScriptCreated(user, schedule.getScheduleName());
        return createdSchedule.getId();
    }
    
    @Override
    public String getScript(int scheduleId, LiteYukonUser user) throws MacsException {
        Schedule schedule = getScheduleById(scheduleId);
        if(Strings.isNullOrEmpty(schedule.getScriptFileName())){
            return null;
        }
        DateTime now = DateTime.now();
        
        RetrieveScript request = new RetrieveScript();
        request.setScriptName(schedule.getScriptFileName());        
        int soeTag = sendMessage(request, user.getUsername(), now);

        String description = getDescription("Retrieve macs script file (" + schedule.getScriptFileName() + ")", soeTag, now);
        ScriptFile file = (ScriptFile) getMessage(soeTag, now, description);
        return file.getFileContents();
    }
    
    private String getDescription(String description, int soeTag, DateTime now) {
        return description + " message was send to MACS Service at " + now.toString("MM/dd/yyyy HH:mm:ss")
            + " with the soeTag=" + soeTag + ".";
        
    }
    
    /**
     * Returns the Message from the MACs Service. Waits for the message if it hasn't arrived.
     */
    private Message getMessage(int soeTag, DateTime start, String description) throws MacsException {
        log.debug("Waiting for message: " + description);
        while (true) {
            if (cachedMessages.containsKey(soeTag)) {
                Message message = cachedMessages.get(soeTag);
                log.debug("Proccessing message " + message.getClass().getName() + getDebugMessageText(message)
                    + " from MACS Service for soeTag=" + soeTag + " [" + message + "]");
                if (message instanceof Info) {
                    MacsException error = new MacsException(MACSExceptionType.PROCESSING_ERROR,
                        description + " Received error from MACS Service: " + ((Info) message).getInfo());
                    log.error(error);
                    throw error;
                }
                return message;
            } else {
                boolean stopWaiting = Seconds.secondsBetween(start, DateTime.now()).isGreaterThan(secondsToWait);
                if (stopWaiting) {
                    cachedMessages.remove(soeTag);
                    MacsException error = new MacsException(MACSExceptionType.NO_REPLY,
                        description + " Response was not received from MACS Service.");
                    log.error(error);
                    throw error;
                }
            }
        }
    }
    
    @Override
    public List<String> getCategories() {
        return Collections.list(connection.getCategoryNames().keys());
    }

    @Override
    public void messageReceived(MessageEvent e) {
        Message message = e.getMessage();
        log.debug("messageReceived " + message.getClass().getName()  + getDebugMessageText(message)
            + " from MACS Service " + message);
        if (waiting.contains(message.getSoeTag())) {
            waiting.remove(message.getSoeTag());
            cachedMessages.put(message.getSoeTag(), message);
        }
    }
    
    private String getDebugMessageText(Message message) {
        if (message instanceof Info) {
            return " (Info: " + ((Info) message).getInfo() + ")";
        } else if (message instanceof Schedule) {
            return " (Schedule: " + ((Schedule) message).getScheduleName() + ")";
        } else if (message instanceof ScriptFile) {
            return " (ScriptFile: " + ((ScriptFile) message).getFileName() + ")";
        }
        return "";
    }
    
    /**
     * Sends message to the MACS Service.
     */
    private int sendMessage(Message message, String userName, DateTime now) throws MacsException {
        int soeTag = nextValueHelper.getNextValue("MACSServiceRequest");
        waiting.add(soeTag);
        message.setSoeTag(soeTag);
        message.setUserName(userName);
        message.setTimeStamp(now.toDate());
        try {
            connection.writeMsg(message);
        } catch (IOException e) {
            waiting.remove(soeTag);
            log.error(e);
            throw new MacsException(MACSExceptionType.ERROR, e);
        }
        return soeTag;
    }
}

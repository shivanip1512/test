package com.cannontech.stars.dr.jms.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory.GearAction;
import com.cannontech.loadcontrol.service.data.ProgramStatusType;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.message.DrProgramStatusJmsMessage;
import com.cannontech.stars.dr.jms.service.DrJmsMessagingService;
import com.cannontech.stars.dr.jms.service.DrProgramStatusService;

public class DrJmsProgramStatusServiceImpl implements DrProgramStatusService {

    private final Logger log = YukonLogManager.getLogger(DrJmsProgramStatusServiceImpl.class);

    @Autowired private DrJmsMessagingService drJmsMessagingService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private LoadControlProgramDao loadControlProgramDao;

    private static DateTime lastRuntime = null;

    @PostConstruct
    public void init() {
        scheduledExecutor.scheduleAtFixedRate(this::sendProgramStatus, 5, 5, TimeUnit.MINUTES);
        log.info("Initialized executor for Sending Program Status with frequency of 5 minutes.");
    }
    
    @Override
    public void sendProgramStatus() {
        DateTime from = null;
        if (lastRuntime == null) {
            lastRuntime = DateTime.now();
            from = lastRuntime.minusMinutes(5);
        } else {
            from = lastRuntime;
            lastRuntime = DateTime.now();
        }
        List<LmProgramGearHistory> lmProgramGearHistories = loadControlProgramDao.getProgramHistoryDetails(from, lastRuntime);

        List<DrProgramStatusJmsMessage> programStatusMessages = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(lmProgramGearHistories)) {

            for (LmProgramGearHistory programHistory : lmProgramGearHistories) {
                DrProgramStatusJmsMessage message = buildProgramStatusJmsMessage(programHistory);
                programStatusMessages.add(message);
            }

            sendProgramStatus(programStatusMessages);
        } else {
            log.debug("Not sending any data as there is no load program change history");
        }

    }

    /**
     * Build Program Status messages that include start/stop time, gear change time, status, program/gear name,
     * correlationId(programGearHistoryId).
     */
    private DrProgramStatusJmsMessage buildProgramStatusJmsMessage(LmProgramGearHistory programHistory) {

        DrProgramStatusJmsMessage message = new DrProgramStatusJmsMessage();
        // correlationId
        message.setProgramGearHistId(programHistory.getProgramGearHistoryId());
        message.setProgramName(programHistory.getProgramName());
        message.setGearName(programHistory.getGearName());
        message.setMessageType(DrJmsMessageType.PROGRAMSTATUS);

        if (GearAction.START == programHistory.getAction() || GearAction.UPDATE == programHistory.getAction()) {
            message.setStartDateTime(programHistory.getEventTime());
            message.setProgramStatusType(ProgramStatusType.ACTIVE);
        } else if (GearAction.GEAR_CHANGE == programHistory.getAction()) {
            message.setGearChangeTime(programHistory.getEventTime());
            message.setProgramStatusType(ProgramStatusType.ACTIVE);
        } else if (GearAction.STOP == programHistory.getAction()) {
            message.setStopDateTime(programHistory.getEventTime());
            Date startedTime = getProgramStartedDateTime(programHistory.getProgramHistoryId());
            if (startedTime != null) {
                message.setStartDateTime(startedTime);
            }
            message.setProgramStatusType(ProgramStatusType.INACTIVE);
        }
        return message;
    }

    /**
     * Publish program status messages to queue.
     */
    private void sendProgramStatus(List<DrProgramStatusJmsMessage> programStatusMessages) {
        for (DrProgramStatusJmsMessage message : programStatusMessages) {
            drJmsMessagingService.publishProgramStatusNotice(message);
        }
    }

    /**
     * Get Program Start time from database corresponding to programHistoryId
     */

    private Date getProgramStartedDateTime(Integer programHistoryId) {

        LmProgramGearHistory startedProgramGearHistory = loadControlProgramDao.getProgramHistoryDetail(programHistoryId, GearAction.START);
        return startedProgramGearHistory.getEventTime();

    }

}

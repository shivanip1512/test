package com.cannontech.dr.eatonCloud.job.service.impl;

import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Minutes;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.dr.eatonCloud.model.EatonCloudError;
import com.cannontech.dr.eatonCloud.service.EatonCloudSendControlService.CommandParam;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.google.common.math.IntMath;

public final class EventSummary {    
    private AtomicInteger currentTry = new AtomicInteger(1);
    private Instant currentTryTime = new Instant();
    private int period;
    private int numberOfTimesToRetry;

    private LMEatonCloudScheduledCycleCommand command;
    private int eventId;
    private int programId;
    private Logger log;
    private RecentEventParticipationDao recentEventParticipationDao;
    private String failReason;
    
    public String getLogSummary(boolean displayTryInfo) {
        return "[SHED id:" + eventId + getTryText(displayTryInfo) + "] relay:" + command.getVirtualRelayId() + " ";
    }

    private String getTryText(boolean displayTryInfo) {
        String tryText = "";
        if(displayTryInfo) {
            tryText =  " (" + currentTry.get() + " of " + numberOfTimesToRetry + ")";
        }
        return tryText; 
    }

    public String getLogSummary(String jobGuid, boolean displayTryInfo) {
        return "[SHED id:" + eventId + ":" + jobGuid + "]" + getTryText(displayTryInfo) + " relay:" + command.getVirtualRelayId()
                + " ";
    }

    EventSummary(int eventId, int programId, LMEatonCloudScheduledCycleCommand command, Logger log,
            RecentEventParticipationDao recentEventParticipationDao, DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.eventId = eventId;
        this.command = command;
        this.programId = programId;
        this.log = log;
        this.recentEventParticipationDao = recentEventParticipationDao;
        this.failReason = deviceErrorTranslatorDao.translateErrorCode(EatonCloudError.NO_RESPONSE_FROM_DEVICE.getDeviceError()).getDescription();

        // If it is a 4 hour control with a 30 minute period, it would have a cycle count of 8.
        // The resend service should only attempt to send to failed devices for the first 4 cycles, or 2 hours.

        Map<String, Object> params = ShedParamHeper.getShedParams(command, eventId);
        period = (Integer) params.get(CommandParam.CYCLE_PERIOD.getParamName());
        numberOfTimesToRetry = IntMath.divide((Integer) params.get(CommandParam.CYCLE_COUNT.getParamName()), 2,
                RoundingMode.CEILING);
    }

    /*
     * try#1 first time the job was started
     * try#2 7 minutes after the first command has been send (5 minutes waiting for the poll + 2 minutes)
     * try#3... "period" minutes after the previous command has been send
     */
    EventSummary setupNextTry(Minutes minutes) {
        if (numberOfTimesToRetry > currentTry.get()) {
            currentTry.incrementAndGet();
            currentTryTime = DateTime.now().plus(minutes).toInstant();
            log.info("[SHED id:{}] NEXT TRY:{} after {}", eventId, currentTry.get(),
                    currentTryTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss"));
            return this;
        }
        return null;
    }

    public void failWillRetryDevices() {
        // we ran out of retries and will mark all FAILED_WILL_RETRY or UNKNOWN devices as FAILED
        int affectedRows = recentEventParticipationDao.failWillRetryDevices(getEventId(), failReason);
        log.info(
                "[SHED id:{}] No more retries available, changed {} FAILED_WILL_RETRY and UNKNOWN devices to FAILED.",
                getEventId(), affectedRows);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public int getProgramId() {
        return programId;
    }

    public int getEventId() {
        return eventId;
    }

    public LMEatonCloudScheduledCycleCommand getCommand() {
        return command;
    }

    public AtomicInteger getCurrentTry() {
        return currentTry;
    }

    public Instant getCurrentTryTime() {
        return currentTryTime;
    }

    public int getPeriod() {
        return period;
    }

    public int getNumberOfTimesToRetry() {
        return numberOfTimesToRetry;
    }
}

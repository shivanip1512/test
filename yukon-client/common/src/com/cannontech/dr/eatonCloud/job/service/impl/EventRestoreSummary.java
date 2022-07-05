package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Logger;

import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;

public final class EventRestoreSummary {    
    private LMEatonCloudStopCommand command;
    private Integer eventId;
    private Integer programId;

    public String getLogSummary() {
        return "[RESTORE id:" + eventId + "] relay:" + command.getVirtualRelayId() + " ";
    }


    public String getLogSummary(String jobGuid) {
        return "[RESTORE id:" + eventId + ":" + jobGuid + "] relay:" + command.getVirtualRelayId()
                + " ";
    }

    EventRestoreSummary(Integer eventId, int programId, LMEatonCloudStopCommand command, Logger log) {
        this.eventId = eventId;
        this.command = command;
        this.programId = programId;
        Map<String, Object> params = ShedParamHeper.getRestoreParams(command, eventId);
      
        log.info(
                "[RESTORE id:{}] Sending restore params:{} command:{}",
                eventId, params, command);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public Integer getProgramId() {
        return programId;
    }

    public Integer getEventId() {
        return eventId;
    }


    public LMEatonCloudStopCommand getCommand() {
        return command;
    }
    
}

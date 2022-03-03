package com.cannontech.web.tools.device.config.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;

public class DeviceConfigActionHistoryDetail {
    private LastAction action;
    private LastActionStatus status;
    private Instant actionStart;
    private Instant actionEnd;

    public LastAction getAction() {
        return action;
    }

    public void setAction(LastAction action) {
        this.action = action;
    }

    public LastActionStatus getStatus() {
        return status;
    }

    public void setStatus(LastActionStatus status) {
        this.status = status;
    }

    public Instant getActionStart() {
        return actionStart;
    }

    public void setActionStart(Instant actionStart) {
        this.actionStart = actionStart;
    }

    public Instant getActionEnd() {
        return actionEnd;
    }

    public void setActionEnd(Instant actionEnd) {
        this.actionEnd = actionEnd;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
}

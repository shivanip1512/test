package com.cannontech.web.tools.device.config.model;

import org.joda.time.Instant;

import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastAction;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastActionStatus;

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
}

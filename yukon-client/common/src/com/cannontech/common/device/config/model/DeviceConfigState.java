package com.cannontech.common.device.config.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;

public class DeviceConfigState {
    private int deviceId;
    private ConfigState state;
    private LastAction action;
    private LastActionStatus status;
    private Instant actionStart;
    private Instant actionEnd;
    private Integer creId;

    public DeviceConfigState(int deviceId, ConfigState state, LastAction action, LastActionStatus status, Instant actionStart,
            Instant actionEnd, Integer creId) {
        this.deviceId = deviceId;
        this.state = state;
        this.action = action;
        this.status = status;
        this.actionStart = actionStart;
        this.actionEnd = actionEnd;
        this.creId = creId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public ConfigState getState() {
        return state;
    }

    public LastAction getAction() {
        return action;
    }

    public LastActionStatus getStatus() {
        return status;
    }

    public Instant getActionStart() {
        return actionStart;
    }

    public Instant getActionEnd() {
        return actionEnd;
    }

    public Integer getCreId() {
        return creId;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}

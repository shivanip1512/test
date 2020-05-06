package com.cannontech.common.device.config.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;

public class DeviceConfigState implements Serializable{
    private static final long serialVersionUID = 1L;
    private int deviceId;
    private ConfigState state;
    private LastAction action;
    private LastActionStatus status;
    private Instant actionStart;
    private Instant actionEnd;
    private Integer creId;

    public DeviceConfigState(int deviceId, ConfigState state, LastAction action, LastActionStatus status, Instant actionStart,
            Instant actionEnd, Integer creId) {
        this.setDeviceId(deviceId);
        this.setState(state);
        this.setAction(action);
        this.setStatus(status);
        this.setActionStart(actionStart);
        this.setActionEnd(actionEnd);
        this.setCreId(creId);
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public ConfigState getState() {
        return state;
    }

    public void setState(ConfigState state) {
        this.state = state;
    }

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

    public Integer getCreId() {
        return creId;
    }

    public void setCreId(Integer creId) {
        this.creId = creId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
}

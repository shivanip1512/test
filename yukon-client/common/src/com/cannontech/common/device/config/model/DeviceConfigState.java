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
    private ConfigState currentState;
    private LastAction lastAction;
    private LastActionStatus lastActionStatus;
    private Instant actionStart;
    private Instant actionEnd;
    private Integer creId;

    public DeviceConfigState(int deviceId, ConfigState state, LastAction action, LastActionStatus status, Instant actionStart,
            Instant actionEnd, Integer creId) {
        this.setDeviceId(deviceId);
        this.setCurrentState(state);
        this.setLastAction(action);
        this.setLastActionStatus(status);
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

    public LastAction getLastAction() {
        return lastAction;
    }

    public void setLastAction(LastAction lastAction) {
        this.lastAction = lastAction;
    }

    public ConfigState getCurrentState() {
        return currentState;
    }

    public LastActionStatus getLastActionStatus() {
        return lastActionStatus;
    }

    public void setLastActionStatus(LastActionStatus lastActionStatus) {
        this.lastActionStatus = lastActionStatus;
    }

    public void setCurrentState(ConfigState currentState) {
        this.currentState = currentState;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
}

package com.cannontech.web.tools.device.config.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao.StateSelection;

public class DeviceConfigSummaryDetail {   
    private DisplayableDevice device;
    private LightDeviceConfiguration deviceConfig;
    private LastAction action;
    private LastActionStatus status;
    private ConfigState state;
    private Instant actionStart;
    private Instant actionEnd;
    private Integer errorCode;
    
    public DisplayableDevice getDevice() {
        return device;
    }

    public void setDevice(DisplayableDevice device) {
        this.device = device;
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

    public LightDeviceConfiguration getDeviceConfig() {
        return deviceConfig;
    }

    public void setDeviceConfig(LightDeviceConfiguration deviceConfig) {
        this.deviceConfig = deviceConfig;
    }

    public boolean isDisplayUpload() {
        return status != LastActionStatus.IN_PROGRESS && deviceConfig != null;
    }

    public boolean isDisplayValidate() {
        return status != LastActionStatus.IN_PROGRESS && deviceConfig != null;
    }
    
    public boolean needsUpload() {
        return StateSelection.NEEDS_UPLOAD.getStates().contains(state);
    }
    
    public boolean needValidation() {
        return StateSelection.NEEDS_VALIDATION.getStates().contains(state);
    }
    
    public boolean isDisplayOutOfSyncPopup() {
        return status != LastActionStatus.IN_PROGRESS && needsUpload();
    }
    
    public boolean isDisplayFailurePopup() {
        return status == LastActionStatus.FAILURE && errorCode != null ;
    }
    
    public boolean isInProgress() {
        return status == LastActionStatus.IN_PROGRESS;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
    
    public ConfigState getState() {
        return state;
    }

    public void setState(ConfigState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
}

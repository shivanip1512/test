package com.cannontech.web.tools.device.config.model;

import org.joda.time.Instant;

import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.InSync;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastAction;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastActionStatus;

public class DeviceConfigSummaryDetail {

    private DisplayableDevice device;
    private LightDeviceConfiguration deviceConfig;
    private LastAction action;
    private LastActionStatus status;
    private InSync inSync;
    private Instant actionStart;
    private Instant actionEnd;
    private boolean displayRead;
    private boolean displaySend;
    private boolean displayVerify;
    
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

    public InSync getInSync() {
        return inSync;
    }

    public void setInSync(InSync inSync) {
        this.inSync = inSync;
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

    public boolean isDisplayRead() {
        return displayRead;
    }

    public void setDisplayRead(boolean displayRead) {
        this.displayRead = displayRead;
    }

    public boolean isDisplaySend() {
        return displaySend;
    }

    public void setDisplaySend(boolean displaySend) {
        this.displaySend = displaySend;
    }

    public boolean isDisplayVerify() {
        return displayVerify;
    }

    public void setDisplayVerify(boolean displayVerify) {
        this.displayVerify = displayVerify;
    }
}

package com.cannontech.common.device.commands.dao.model;

public class CommandRequestUnsupported {
    private Integer id;
    private Integer deviceId;
    private long CommandRequestExecId;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }
    public long getCommandRequestExecId() {
        return CommandRequestExecId;
    }
    public void setCommandRequestExecId(long commandRequestExecId) {
        CommandRequestExecId = commandRequestExecId;
    }
}

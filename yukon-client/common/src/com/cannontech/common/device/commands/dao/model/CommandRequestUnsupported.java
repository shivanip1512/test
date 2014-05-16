package com.cannontech.common.device.commands.dao.model;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;

public class CommandRequestUnsupported {
    private Integer id;
    private Integer deviceId;
    private long CommandRequestExecId;
    private CommandRequestUnsupportedType type;

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
    public CommandRequestUnsupportedType getType() {
        return type;
    }
    public void setType(CommandRequestUnsupportedType type) {
        this.type = type;
    }
}

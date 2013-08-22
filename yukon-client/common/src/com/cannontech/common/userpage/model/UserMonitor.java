package com.cannontech.common.userpage.model;


public class UserMonitor {

    private final Integer id;
    private final Integer userId;
    private final String name;
    private final MonitorType type;
    private final Integer monitorId;

    public enum MonitorType{
        DEVICE_DATA,
        OUTAGE,
        TAMPER_FLAG,
        STATUS_POINT,
        PORTER_RESPONSE,
        VALIDATION,
    }

    public UserMonitor(Integer userId, String name, MonitorType type, Integer monitorId, Integer id) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.monitorId = monitorId;
    }

    public UserMonitor updateId(Integer id) {
        if (this.id == id) {
            return this;
        } else {
            return new UserMonitor(this.userId, this.name, this.type, this.monitorId, id);
        }
    }

    public final Integer getId() {
        return id;
    }
    public final Integer getUserId() {
        return userId;
    }
    public final String getName() {
        return name;
    }
    public final MonitorType getType() {
        return type;
    }
    public final Integer getMonitorId() {
        return monitorId;
    }
}
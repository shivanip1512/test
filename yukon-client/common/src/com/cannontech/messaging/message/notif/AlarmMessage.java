package com.cannontech.messaging.message.notif;

import java.util.Date;

import com.cannontech.messaging.message.BaseMessage;

public class AlarmMessage extends BaseMessage {

    private int[] notifGroupIds = new int[0];
    private int alarmCategoryId;
    private int pointId;
    private int condition;
    private double value;
    private Date alarmTimestamp;
    private boolean acknowledged;
    private boolean abnormal;

    public int[] getNotifGroupIds() {
        return notifGroupIds;
    }

    public void setNotifGroupIds(int[] notifGroupIds) {
        this.notifGroupIds = notifGroupIds;
    }

    public int getAlarmCategoryId() {
        return alarmCategoryId;
    }

    public void setAlarmCategoryId(int alarmCategoryId) {
        this.alarmCategoryId = alarmCategoryId;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getAlarmTimestamp() {
        return alarmTimestamp;
    }

    public void setAlarmTimestamp(Date alarmTimestamp) {
        this.alarmTimestamp = alarmTimestamp;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public boolean isAbnormal() {
        return abnormal;
    }

    public void setAbnormal(boolean abnormal) {
        this.abnormal = abnormal;
    }
}

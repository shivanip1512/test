package com.amdswireless.messages.rx;

public interface AlarmMessageInterface {

    public abstract int getTimeSinceEvent();

    public abstract java.util.Date getTimestampOfEvent();

    public abstract int getClickCount();

    public abstract int getCurrentReading();

    public abstract int getDeviceTemperature();

}

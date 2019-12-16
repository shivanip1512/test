package com.cannontech.web.dev.model;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class RelayStateInjectionParams {
    private String groupName;
    private boolean startAfterLastReading = true;
    private Instant start = Instant.now().minus(Duration.standardDays(1));
    private Instant stop = Instant.now();
    private String period = "1h";
    private Instant injectionStart = null;
    private Instant injectionEnd = null;
    private Integer deviceCount = 0;
    private volatile Integer devicesComplete = 0;

    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public boolean isStartAfterLastReading() {
        return startAfterLastReading;
    }
    
    public void setStartAfterLastReading(boolean startAfterLastReading) {
        this.startAfterLastReading = startAfterLastReading;
    }
    
    public Instant getStart() {
        return start;
    }
    
    public void setStart(Instant start) {
        this.start = start;
    }
    
    public Instant getStop() {
        return stop;
    }
    
    public void setStop(Instant stop) {
        this.stop = stop;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public Instant getInjectionStart() {
        return injectionStart;
    }

    public void setInjectionStart(Instant injectionStart) {
        this.injectionStart = injectionStart;
    }

    public Instant getInjectionEnd() {
        return injectionEnd;
    }

    public void setInjectionEnd(Instant injectionEnd) {
        this.injectionEnd = injectionEnd;
    }

    public Integer getDevicesComplete() {
        return devicesComplete;
    }

    public void setDevicesComplete(Integer devicesComplete) {
        this.devicesComplete = devicesComplete;
    }

    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }

    public Integer getDeviceCount() {
        return deviceCount;
    }
    
    public void incrementDevicesComplete() {
        devicesComplete = devicesComplete + 1;
    }

    public RelayStateInjectionParams copy() {
        RelayStateInjectionParams copy = new RelayStateInjectionParams();
        copy.setGroupName(groupName);
        copy.setPeriod(period);
        copy.setStart(start);
        copy.setStop(stop);
        copy.setStartAfterLastReading(startAfterLastReading);
        copy.setInjectionStart(injectionStart);
        copy.setInjectionEnd(injectionEnd);
        copy.setDeviceCount(deviceCount);
        copy.setDevicesComplete(devicesComplete);
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RelayStateInjectionParams [groupName=")
               .append(groupName)
               .append(", startAfterLastReading=")
               .append(startAfterLastReading)
               .append(", start=")
               .append(start)
               .append(", stop=")
               .append(stop)
               .append(", period=")
               .append(period)
               .append(", injectionStart=")
               .append(injectionStart)
               .append(", injectionEnd=")
               .append(injectionEnd)
               .append(", deviceCount=")
               .append(deviceCount)
               .append(", devicesComplete=")
               .append(devicesComplete)
               .append("]");
        return builder.toString();
    }
    
}

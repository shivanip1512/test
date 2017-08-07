package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.Date;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.macs.message.Schedule;

public class MacsStopPolicy {
    public enum StopPolicy {
        UNTILCOMPLETE(Schedule.UNTILCOMPLETE_STOP),
        ABSOLUTETIME(Schedule.ABSOLUTETIME_STOP),
        DURATION(Schedule.DURATION_STOP),
        MANUAL(Schedule.MANUAL_STOP);

        private String policy;

        StopPolicy(String policy) {
            this.policy = policy;
        }

        public String getPolicyString() {
            return policy;
        }
        
        public static StopPolicy getPolicy(String value) {
            return Arrays.stream(StopPolicy.values())
                    .filter(s -> s.getPolicyString().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Could not find policy=" + value));
        }
    }
    
    private StopPolicy policy;
    private int duration;
    private Date manualStopTime;
    
    public StopPolicy getPolicy() {
        return policy;
    }
    public void setPolicy(StopPolicy policy) {
        this.policy = policy;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    /**
     * @return the stopTime (must be 8 chars)
     */
    public String getStopTime() {
        return "00:00:00";
    }
    public Date getManualStopTime() {
        return manualStopTime;
    }
    public void setManualStopTime(Date manualStopTime) {
        this.manualStopTime = manualStopTime;
    }
}

package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;

import org.joda.time.DateTime;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.macs.message.Schedule;

public class MacsStopPolicy {
    public enum StopPolicy implements DisplayableEnum {
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

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.schedule.stopPolicy.type." + name();
        }
    }
    
    private StopPolicy policy;
    private int duration = 60;
    private MacsTimeField time;
    
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
    
    public String getStopTime() {
        return time.getTimeString();
    }
    
    public void buildStopTimeDate(String timeString){
        if (policy == StopPolicy.ABSOLUTETIME) {
            DateTime now = DateTime.now();
            DateTime parsedDate = MacsTimeField.parseDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), timeString);
            time = MacsTimeField.getTimeField(parsedDate);
        }
    }
    
    public MacsTimeField getTime() {
        return time;
    }
    public void setTime(MacsTimeField time) {
        this.time = time;
    }
}

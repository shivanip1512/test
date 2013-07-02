package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList.Builder;

@Component
public final class ScheduleInput implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.schedules";
    
    private static final List<DisplayableValue> validSchedules;
    
    public enum Schedule {
        SCHEDULE_1("Schedule 1", ".schedule1"),
        SCHEDULE_2("Schedule 2", ".schedule2"),
        SCHEDULE_3("Schedule 3", ".schedule3"),
        SCHEDULE_4("Schedule 4", ".schedule4"),
        ;
        
        private final String dbValue;
        private final String messageKey;
        
        private Schedule(String dbValue, String messageKey) {
            this.dbValue = dbValue;
            this.messageKey = messageKey;
        }
    }
    
    static {
        Builder<DisplayableValue> builder = new Builder<>();
        
        for (Schedule schedule : Schedule.values()) {
            builder.add(new DisplayableValue(schedule.dbValue, baseKey + schedule.messageKey));
        }
        
        validSchedules = builder.build();
    }
    
    @Override
    public String getEnumOptionName() {
        return "Schedule";
    }

    @Override
    public List<DisplayableValue> getDisplayableValues() {
        return validSchedules;
    }
}

package com.cannontech.common.rfn.message.alarm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public enum AlarmType {
    SECURITY_ALARM(1L, AlarmCategory.GW_ALARM, BuiltInAttribute.SECURITY_ALARM),
    POWER_ALARM(2L, AlarmCategory.GW_ALARM, BuiltInAttribute.POWER_FAILURE),
    RADIO_ALARM(3L, AlarmCategory.GW_ALARM, BuiltInAttribute.RADIO_FAILURE),
    TIME_SYNCHRONIZATION_ALARM(4L, AlarmCategory.GW_ALARM, BuiltInAttribute.TIME_SYNC_FAILED),
    EXTERNAL_ALARM(5L, AlarmCategory.GW_ALARM, BuiltInAttribute.DOOR_OPEN),
    NODE_COUNT_ALARM(6L, AlarmCategory.GW_ALARM, BuiltInAttribute.NODE_COUNT_EXCEEDED),
    ;
    
    private static Map<Long, AlarmType> GwAlarmMap = new HashMap<>();
    static {
        for (AlarmType alarm : values()) {
            GwAlarmMap.put(alarm.alarmCodeId, alarm);
        }
    }
    
    BuiltInAttribute attribute = null;
    Long alarmCodeId = null;
    AlarmCategory category = null;
    
    AlarmType(Long alarmCodeId, AlarmCategory category, BuiltInAttribute attribute) {
        this.alarmCodeId = alarmCodeId;
        this.category = category;
        this.attribute = attribute;
    }
    
    public BuiltInAttribute getAttribute() {
        return attribute;
    }
    
    public static Optional<AlarmType> of(AlarmCategory category, long alarmCode) {
        if (AlarmCategory.GW_ALARM == category) {
            return Optional.of(GwAlarmMap.get(alarmCode));
        }
        return Optional.empty();
    }
}

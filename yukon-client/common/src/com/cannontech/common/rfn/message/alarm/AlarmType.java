package com.cannontech.common.rfn.message.alarm;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public enum AlarmType {
    SECURITY_ALARM(1L, AlarmCategory.GW_ALARM, BuiltInAttribute.SECURITY_ALARM),
    POWER_ALARM(2L, AlarmCategory.GW_ALARM, BuiltInAttribute.POWER_FAILURE),
    RADIO_ALARM(3L, AlarmCategory.GW_ALARM, BuiltInAttribute.RADIO_FAILURE),
    TIME_SYNCHRONIZATION_ALARM(4L, AlarmCategory.GW_ALARM, BuiltInAttribute.TIME_SYNC_FAILED),
    EXTERNAL_ALARM(5L, AlarmCategory.GW_ALARM, BuiltInAttribute.DOOR_OPEN),
    NODE_COUNT_ALARM(6L, AlarmCategory.GW_ALARM, BuiltInAttribute.NODE_COUNT_EXCEEDED),
    ;
    
    private static Map<Long, AlarmType> GwAlarmMap = new HashMap<>() {
        {
            put(SECURITY_ALARM.alarmCodeId, SECURITY_ALARM);
            put(POWER_ALARM.alarmCodeId, POWER_ALARM);
            put(RADIO_ALARM.alarmCodeId, RADIO_ALARM);
            put(TIME_SYNCHRONIZATION_ALARM.alarmCodeId, TIME_SYNCHRONIZATION_ALARM);
            put(EXTERNAL_ALARM.alarmCodeId, EXTERNAL_ALARM);
            put(NODE_COUNT_ALARM.alarmCodeId, NODE_COUNT_ALARM);
        }
    };
    
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
    
    public static AlarmType of(AlarmCategory category, long alarmCode) {
        if (AlarmCategory.GW_ALARM == category) {
            return GwAlarmMap.get(alarmCode);
        }
        return null;
    }
}

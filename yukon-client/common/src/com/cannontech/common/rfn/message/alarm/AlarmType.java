package com.cannontech.common.rfn.message.alarm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public enum AlarmType {
    POWER_ALARM(0L, AlarmCategory.GW_ALARM, BuiltInAttribute.POWER_FAILURE),
    SECURITY_ALARM(1L, AlarmCategory.GW_ALARM, BuiltInAttribute.SECURITY_ALARM),
    RADIO_ALARM(2L, AlarmCategory.GW_ALARM, BuiltInAttribute.RADIO_FAILURE),
    TIME_SYNCHRONIZATION_ALARM(3L, AlarmCategory.GW_ALARM, BuiltInAttribute.TIME_SYNC_FAILED),
    EXTERNAL_ALARM(4L, AlarmCategory.GW_ALARM, BuiltInAttribute.DOOR_OPEN),
    NODE_COUNT_ALARM(5L, AlarmCategory.GW_ALARM, BuiltInAttribute.NODE_COUNT_EXCEEDED),
    EXTERNAL2_ALARM(6L, AlarmCategory.GW_ALARM, BuiltInAttribute.UPS_BATTERY_VOLTAGE_LOW),
    CERTIFICATE_EXPIRATION_ALARM(7L, AlarmCategory.GW_ALARM, BuiltInAttribute.CERTIFICATE_EXPIRATION),
    HIGH_DISK_USAGE_ALARM(8L, AlarmCategory.GW_ALARM, BuiltInAttribute.HIGH_DISK_USAGE),
    RTC_BATTERY_FAILURE_ALARM(9L, AlarmCategory.GW_ALARM, BuiltInAttribute.RTC_BATTERY_FAILURE),
    AC_POWER_FAILURE_ALARM(10L, AlarmCategory.GW_ALARM, BuiltInAttribute.AC_POWER_FAILURE),
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

package com.cannontech.amr.rfn.message.event;

/**
 * Type of events and alarms. These values map directly to their BuiltInAttribute counterpart
 * (except for OUTAGE, OUTAGE_BLINK, and RESTORE)
 */
public enum RfnConditionType {
    
    ALTERNATE_MODE_ENTRY,
    ANSI_SECURITY_FAILED,
    BAD_UPGRADE_SECURITY_PARAM,
    BATTERY_END_OF_LIFE,
    CELLULAR_CONNECTION_FAILED,
    CELLULAR_MODEM_DISABLED,
    CELLULAR_APN_CHANGED,
    CELLULAR_SIM_CARD_INSERTED_REMOVED,
    CONFIGURATION_ERROR,
    CLOCK_ERROR,
    CRC_FAILURE_MEMORY_CORRUPT,
    CRYSTAL_OSCILLATOR_ERROR,
    CURRENT_LOSS,
    CURRENT_WAVEFORM_DISTORTION,
    DEMAND_OVERLOAD,
    DEMAND_READS_AND_RESET,
    DEMAND_THRESHOLD_EXCEEDED_WARNING,
    DISPLAY_LOCKED_BY_WARNING,
    DNP3_ADDRESS_CHANGED,
    EEPROM_ACCESS_ERROR,
    EMPTY_PIPE,
    ENCODER,
    ENCRYPTION_KEY_TABLE_CRC_ERROR,
    END_OF_CALENDAR_WARNING,
    ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE,
    EXCEEDING_MAXIMUM_FLOW,
    FAILED_UPGRADE_SIGNATURE_VERIF,
    FIELD_EXCEEDED_MAXIMUM_DIGITS,
    IMPROPER_METER_ENGINE_OPERATION_WARNING,
    INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR,
    INTERNAL_COMMUNICATION_ERROR,
    INVALID_SERVICE,
    LINE_FREQUENCY_WARNING,
    LOAD_SIDE_VOLTAGE_DETECTED_WHILE_DISCONNECTED,
    LOAD_SIDE_VOLTAGE_IS_MISSING,
    LOSS_OF_ALL_CURRENT,
    LOSS_OF_PHASE_A_CURRENT,
    LOSS_OF_PHASE_C_CURRENT,
    LOST_ASSOCIATION,
    LOW_BATTERY_WARNING,
    LOW_LOSS_POTENTIAL,
    MAGNET_TAMPERING,
    MASS_MEMORY_ERROR,
    MEASUREMENT_ERROR,
    METER_FUNCTIONING_CORRECTLY,
    METER_READ_CHECKSUM_ERROR,
    METER_READ_ERROR_READING_SERIAL_NUMBER,
    METER_READ_FIELD_EXCEEDED_MAXIMUM_DIGITS,
    METER_READ_NO_ENCODER_FOUND,
    METER_READ_NO_EOF_DETECTED,
    METER_READ_PARITY_ERROR,
    METER_READ_PROTOCOL_CANNOT_BE_DETERMINED,
    METER_RECONFIGURE,
    METROLOGY_COMM_FAILURE,
    NODE_COMMISSION_EVENT,
    NON_VOLATILE_MEM_FAILURE,
    OUTAGE,
    OUTAGE_BLINK,
    OUTSTATION_DNP3_SERCOMM_LOCKED,
    OVER_VOLTAGE,
    PASSWORD_TABLE_CRC_ERROR,
    PHASE_ANGLE_DISPLACEMENT,
    PHASE_LOSS,
    POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC,
    POTENTIAL_INDICATOR_WARNING,
    POWER_FAIL_DATA_SAVE_ERROR,
    POWER_FAILURE,
    PQM_TEST_FAILURE_WARNING,
    PULSE_1_SWITCH_FAILURE,
    PULSE_2_SWITCH_FAILURE,
    RAM_ERROR,
    REGISTER_FULL_SCALE_EXCEEDED,
    REGISTER_REMOVAL,
    REMOTE_METER_CONFIGURATION_FAILURE,
    REMOTE_METER_CONFIGURATION_FINISHED,
    RESTORE,
    RESTORE_BLINK,
    REVERSE_FLOW,
    REVERSE_ROTATION,
    REVERSED_AGGREGATE,
    REVERSED_PHASE_A,
    REVERSED_PHASE_C,
    RFN_TEMPERATURE_ALARM,
    ROM_ERROR,
    SEASON_CHANGE,
    SECURITY_CONFIGURATION_ERROR,
    SELF_CHECK_ERROR,
    SENSOR_ERROR,
    SERVICE_CURRENT_TEST_FAILURE_WARNING,
    SERVICE_DISCONNECT_SWITCH_ERROR,
    SERVICE_DISCONNECT_SWITCH_OPEN,
    SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR,
    SITESCAN_ERROR,
    STORAGE_MODE,
    STUCK_SWITCH,
    SUSPECTED_LEAK,
    TABLE_CRC_ERROR,
    TAMPER_CABLE_CUT,
    TAMPER_DETECT,
    TAMPER_NO_USAGE_OVER_24_HOURS,
    TAMPER_REVERSE_WH_DETECTED,
    TAMPER_LARGE_INCREASE_AFTER_OUTAGE,
    TAMPER_LARGE_DECREASE_AFTER_OUTAGE,
    TEMPERATURE_OUT_OF_RANGE,
    THD_V_OR_TDD_I_ERROR,
    THIRTY_DAYS_NO_USAGE,
    TIME_ADJUSTMENT,
    TIME_SYNC_FAILED,
    TOU_SCHEDULE_CHANGE,
    TOU_SCHEDULE_ERROR,
    ULTRA_CAPACITOR_BAD,
    UNCONFIGURED,
    UNDER_VOLTAGE,
    UNPROGRAMMED,
    USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED,
    VIBRATION_TILT_TAMPER_DETECTED,
    VOLTAGE_ALERTS,
    VOLTAGE_LOSS,
    VOLTAGE_PHASE_A_OUT,
    VOLTAGE_PHASE_B_OUT,
    VOLTAGE_PHASE_C_OUT,
    VOLTAGE_PHASE_ERROR,
    WATT_HOUR_PULSE_FAILURE,
    WIFI_AP_CONNECTION_FAILURE,
    WIFI_BSSID_CHANGE,
    WIFI_PASSPHRASE_CHANGE,
    WIFI_SSID_CHANGE;
}
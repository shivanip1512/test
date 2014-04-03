package com.cannontech.amr.rfn.message.event;

/**
 * Type of events and alarms. These values map directly to their BuiltInAttribute counterpart
 * (except for OUTAGE, OUTAGE_BLINK, and RESTORE)
 */
public enum RfnConditionType {
    
    ALTERNATE_MODE_ENTRY,
    ANSI_SECURITY_FAILED,
    BAD_UPGRADE_SECURITY_PARAM,
    CONFIGURATION_ERROR,
    CLOCK_ERROR,
    CRYSTAL_OSCILLATOR_ERROR,
    CURRENT_LOSS,
    CURRENT_WAVEFORM_DISTORTION,
    DEMAND_OVERLOAD,
    DEMAND_READS_AND_RESET,
    DEMAND_THRESHOLD_EXCEEDED_WARNING,
    DISPLAY_LOCKED_BY_WARNING,
    DNP3_ADDRESS_CHANGED,
    EEPROM_ACCESS_ERROR,
    ENCRYPTION_KEY_TABLE_CRC_ERROR,
    END_OF_CALENDAR_WARNING,
    ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE,
    FAILED_UPGRADE_SIGNATURE_VERIF,
    IMPROPER_METER_ENGINE_OPERATION_WARNING,
    INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR,
    INTERNAL_COMMUNICATION_ERROR,
    INVALID_SERVICE,
    LINE_FREQUENCY_WARNING,
    LOAD_SIDE_VOLTAGE_IS_MISSING,
    LOSS_OF_ALL_CURRENT,
    LOSS_OF_PHASE_A_CURRENT,
    LOSS_OF_PHASE_C_CURRENT,
    LOW_BATTERY_WARNING,
    LOW_LOSS_POTENTIAL,
    MASS_MEMORY_ERROR,
    MEASUREMENT_ERROR,
    METER_RECONFIGURE,
    METROLOGY_COMM_FAILURE,
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
    RAM_ERROR,
    REGISTER_FULL_SCALE_EXCEEDED,
    RESTORE,
    RESTORE_BLINK,
    REVERSE_ROTATION,
    REVERSED_AGGREGATE,
    REVERSED_PHASE_A,
    REVERSED_PHASE_C,
    RFN_TEMPERATURE_ALARM,
    ROM_ERROR,
    SEASON_CHANGE,
    SECURITY_CONFIGURATION_ERROR,
    SELF_CHECK_ERROR,
    SERVICE_CURRENT_TEST_FAILURE_WARNING,
    SERVICE_DISCONNECT_SWITCH_ERROR,
    SERVICE_DISCONNECT_SWITCH_OPEN,
    SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR,
    SITESCAN_ERROR,
    STUCK_SWITCH,
    TABLE_CRC_ERROR,
    TAMPER_DETECT,
    THD_V_OR_TDD_I_ERROR,
    TIME_ADJUSTMENT,
    TIME_SYNC_FAILED,
    TOU_SCHEDULE_ERROR,
    UNCONFIGURED,
    UNDER_VOLTAGE,
    UNPROGRAMMED,
    USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED,
    VOLTAGE_ALERTS,
    VOLTAGE_LOSS,
    VOLTAGE_PHASE_A_OUT,
    VOLTAGE_PHASE_B_OUT,
    VOLTAGE_PHASE_C_OUT,
    VOLTAGE_PHASE_ERROR,
    WATT_HOUR_PULSE_FAILURE;
    
}
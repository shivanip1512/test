package com.cannontech.common.pao.attribute.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;

public enum BuiltInAttribute implements Attribute, DisplayableEnum {
    // NOTE: Remember to add any new attributes to point.xml for i18n'ing, too

    // This ordering is shared by our C++ code. 
    // Any changes/additions here need to be reflected there as well.
    
    // yukon-server/COMMON/Attribute.cpp
    // yukon-server/COMMON/include/Attribute.h

     /** 
       * Point Name naming convention: 
       * [Net|Sum] [Delivered|Received] [Coincident] [Cumulative] [Peak] [UOM] [(Quadrants # #)] [Frozen] [Rate #|Phase X|Channel #]
       * Examples: 
       * Net kWh OR Net Delivered kWh 
       * Received kWh Rate A 
       * Note: [Delivered] - can be omitted/assumed where it make sense; Peak kW or kVar for examples  
       * Note: For attributes that reference the modifiers of "Quadrant" directly, the modifiers will follow the UOM in format "(Quadrants # #)". Example: kVA (Quadrants 1 2)
       */
    BLINK_COUNT("Blink Count", AttributeGroup.BLINK_AND_OUTAGE),
    COMM_STATUS("Communication Status", AttributeGroup.STATUS, false),   // updated directly by port activity only
    CONTROL_STATUS("Control Status", AttributeGroup.STATUS),
    NEUTRAL_CURRENT("Current (Neutral)", AttributeGroup.CURRENT),
    CURRENT("Current", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_PHASE_A("Current (Phase A)", AttributeGroup.CURRENT),
    CURRENT_PHASE_B("Current (Phase B)", AttributeGroup.CURRENT),
    CURRENT_PHASE_C("Current (Phase C)", AttributeGroup.CURRENT),
    CURRENT_ANGLE("Current Angle", AttributeGroup.CURRENT, false),   
    CURRENT_ANGLE_PHASE_A("Current Angle (Phase A)", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_ANGLE_PHASE_B("Current Angle (Phase B)", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_ANGLE_PHASE_C("Current Angle (Phase C)", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_WITHOUT_VOLTAGE_FLAG("Current Without Voltage", AttributeGroup.STATUS, false),   //440 types only
    DEMAND("Demand", AttributeGroup.DEMAND), // instantaneous
    DELIVERED_DEMAND("Delivered Demand", AttributeGroup.DEMAND),
    RECEIVED_DEMAND("Received Demand", AttributeGroup.DEMAND),
    DEMAND_PEAK_KVA_COIN("Demand at Peak kVa Coincidental", AttributeGroup.DEMAND),
    INSTANTANEOUS_KW("Instantaneous kW", AttributeGroup.DEMAND),
    SUM_KW("Sum kW", AttributeGroup.DEMAND),
    NET_KW("Net kW", AttributeGroup.DEMAND),
    DISCONNECT_STATUS("Disconnect Status", AttributeGroup.STATUS),
    FAULT_STATUS("Fault Status", AttributeGroup.STATUS, false),
    FORWARD_INDUCTIVE_KVARH("Forward Inductive kVArh", AttributeGroup.REACTIVE, false),   //440 types only
    GENERAL_ALARM_FLAG("General Alarm Flag", AttributeGroup.STATUS),
    IED_DEMAND_RESET_COUNT("IED Demand Reset Count", AttributeGroup.DEMAND),
    // Treating "kVAh" as "Delivered kVAh". May need to created separate attributes in the future.
    KVAH("kVAh", AttributeGroup.USAGE),
    KVAH_RATE_A("kVAh Rate A", AttributeGroup.USAGE),
    KVAH_RATE_B("kVAh Rate B", AttributeGroup.USAGE),
    KVAH_RATE_C("kVAh Rate C", AttributeGroup.USAGE),
    KVAH_RATE_D("kVAh Rate D", AttributeGroup.USAGE),
    NET_KVAH("Net kVAh", AttributeGroup.USAGE),
    NET_KVAH_RATE_A("Net kVAh Rate A", AttributeGroup.USAGE),
    NET_KVAH_RATE_B("Net kVAh Rate B", AttributeGroup.USAGE),
    NET_KVAH_RATE_C("Net kVAh Rate C", AttributeGroup.USAGE),
    NET_KVAH_RATE_D("Net kVAh Rate D", AttributeGroup.USAGE),
    RECEIVED_KVAH_RATE_A("Received kVAh Rate A", AttributeGroup.USAGE),
    RECEIVED_KVAH_RATE_B("Received kVAh Rate B", AttributeGroup.USAGE),
    RECEIVED_KVAH_RATE_C("Received kVAh Rate C", AttributeGroup.USAGE),
    RECEIVED_KVAH_RATE_D("Received kVAh Rate D", AttributeGroup.USAGE),
    SUM_KVAH_RATE_A("Sum kVAh Rate A", AttributeGroup.USAGE),
    SUM_KVAH_RATE_B("Sum kVAh Rate B", AttributeGroup.USAGE),
    SUM_KVAH_RATE_C("Sum kVAh Rate C", AttributeGroup.USAGE),
    SUM_KVAH_RATE_D("Sum kVAh Rate D", AttributeGroup.USAGE),
    KVAR("kVAr", AttributeGroup.REACTIVE),  // instantaneous
    DELIVERED_KVAR("Delivered kVAr", AttributeGroup.REACTIVE),
    RECEIVED_KVAR("Received kVAr", AttributeGroup.REACTIVE),
    KVARH("kVArh", AttributeGroup.REACTIVE),    // Delivered kVArh
    KVARH_RATE_A("kVArh Rate A", AttributeGroup.REACTIVE),
    KVARH_RATE_B("kVArh Rate B", AttributeGroup.REACTIVE),
    KVARH_RATE_C("kVArh Rate C", AttributeGroup.REACTIVE),
    KVARH_RATE_D("kVArh Rate D", AttributeGroup.REACTIVE),
    LM_GROUP_STATUS("LM Group Status", AttributeGroup.STATUS, false),
    LOAD_PROFILE("Load Profile", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    LOAD_SIDE_VOLTAGE_DETECTED_WHILE_DISCONNECTED("Load Side Voltage Detected", AttributeGroup.RFN_METERING_EVENT, false),   
    METER_BOX_COVER_REMOVAL_FLAG("Meter Box Cover Removal", AttributeGroup.STATUS, false),   //440 types only
    MAXIMUM_VOLTAGE("Maximum Voltage", AttributeGroup.VOLTAGE),
    MAXIMUM_VOLTAGE_FROZEN("Maximum Voltage Frozen", AttributeGroup.VOLTAGE),
    MAXIMUM_VOLTAGE_PHASE_A("Maximum Voltage (Phase A)", AttributeGroup.VOLTAGE),
    MAXIMUM_VOLTAGE_PHASE_B("Maximum Voltage (Phase B)", AttributeGroup.VOLTAGE),
    MAXIMUM_VOLTAGE_PHASE_C("Maximum Voltage (Phase C)", AttributeGroup.VOLTAGE),
    MINIMUM_VOLTAGE("Minimum Voltage", AttributeGroup.VOLTAGE),
    MINIMUM_VOLTAGE_FROZEN("Minimum Voltage Frozen", AttributeGroup.VOLTAGE),
    MINIMUM_VOLTAGE_PHASE_A("Minimum Voltage (Phase A)", AttributeGroup.VOLTAGE),
    MINIMUM_VOLTAGE_PHASE_B("Minimum Voltage (Phase B)", AttributeGroup.VOLTAGE),
    MINIMUM_VOLTAGE_PHASE_C("Minimum Voltage (Phase C)", AttributeGroup.VOLTAGE),
    AVERAGE_VOLTAGE("Average Voltage", AttributeGroup.VOLTAGE),
    AVERAGE_VOLTAGE_PHASE_A("Average Voltage (Phase A)", AttributeGroup.VOLTAGE),
    AVERAGE_VOLTAGE_PHASE_B("Average Voltage (Phase B)", AttributeGroup.VOLTAGE),
    AVERAGE_VOLTAGE_PHASE_C("Average Voltage (Phase C)", AttributeGroup.VOLTAGE),
    
    OUTAGE_LOG("Outage Log", AttributeGroup.BLINK_AND_OUTAGE),
    OUTAGE_STATUS("Outage Status", AttributeGroup.STATUS),
    OUT_OF_VOLTAGE_FLAG("Out of Voltage", AttributeGroup.STATUS, false),   //440 types only
    VOLTAGE_OUT_OF_LIMITS_FLAG("Voltage Out of Limits", AttributeGroup.STATUS, false),   //440 types only
    OVER_VOLTAGE("Over Voltage", AttributeGroup.RFN_HARDWARE_EVENT, false),
    OVER_VOLTAGE_THRESHOLD("Over Voltage Threshold", AttributeGroup.RFN_HARDWARE_EVENT, false),
    PEAK_DEMAND("Peak Demand", AttributeGroup.DEMAND),
    PEAK_DEMAND_DAILY("Peak Demand Daily", AttributeGroup.DEMAND),
    NET_PEAK_DEMAND("Net Peak Demand", AttributeGroup.DEMAND),
    SUM_PEAK_DEMAND("Sum Peak Demand", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN("Peak Demand Frozen", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_A("Peak Demand Rate A", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_A("Peak Demand Frozen Rate A", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_B("Peak Demand Rate B", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_B("Peak Demand Frozen Rate B", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_C("Peak Demand Rate C", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_C("Peak Demand Frozen Rate C", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_D("Peak Demand Rate D", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_D("Peak Demand Frozen Rate D", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_E("Peak Demand Rate E", AttributeGroup.DEMAND),
    NET_PEAK_DEMAND_RATE_A("Net Peak Demand Rate A", AttributeGroup.DEMAND),
    NET_PEAK_DEMAND_RATE_B("Net Peak Demand Rate B", AttributeGroup.DEMAND),
    NET_PEAK_DEMAND_RATE_C("Net Peak Demand Rate C", AttributeGroup.DEMAND),
    NET_PEAK_DEMAND_RATE_D("Net Peak Demand Rate D", AttributeGroup.DEMAND),
    SUM_PEAK_DEMAND_RATE_A("Sum Peak Demand Rate A", AttributeGroup.DEMAND),
    SUM_PEAK_DEMAND_RATE_B("Sum Peak Demand Rate B", AttributeGroup.DEMAND),
    SUM_PEAK_DEMAND_RATE_C("Sum Peak Demand Rate C", AttributeGroup.DEMAND),
    SUM_PEAK_DEMAND_RATE_D("Sum Peak Demand Rate D", AttributeGroup.DEMAND),
    RECEIVED_PEAK_DEMAND("Received Peak Demand", AttributeGroup.DEMAND),
    RECEIVED_PEAK_DEMAND_RATE_A("Received Peak Demand Rate A", AttributeGroup.DEMAND),
    RECEIVED_PEAK_DEMAND_RATE_B("Received Peak Demand Rate B", AttributeGroup.DEMAND),
    RECEIVED_PEAK_DEMAND_RATE_C("Received Peak Demand Rate C", AttributeGroup.DEMAND),
    RECEIVED_PEAK_DEMAND_RATE_D("Received Peak Demand Rate D", AttributeGroup.DEMAND),
    KVA("kVA", AttributeGroup.REACTIVE),    
    DELIVERED_KVA("Delivered kVA", AttributeGroup.REACTIVE),
    RECEIVED_KVA("Received kVA", AttributeGroup.REACTIVE),
    SUM_KVA("Sum kVA", AttributeGroup.REACTIVE),
    NET_KVA("Net kVA", AttributeGroup.REACTIVE),
    KVA_PEAK_DEMAND_COIN("kVA at Peak Demand Coincidental", AttributeGroup.REACTIVE),
    PEAK_KVA("Peak kVA", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_A("Peak kVA Rate A", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_B("Peak kVA Rate B", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_C("Peak kVA Rate C", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_D("Peak kVA Rate D", AttributeGroup.REACTIVE),
    PEAK_KVA_FROZEN("Peak kVA Frozen", AttributeGroup.REACTIVE),
    PEAK_KVA_FROZEN_RATE_A("Peak kVA Frozen Rate A", AttributeGroup.REACTIVE),
    PEAK_KVA_FROZEN_RATE_B("Peak kVA Frozen Rate B", AttributeGroup.REACTIVE),
    PEAK_KVA_FROZEN_RATE_C("Peak kVA Frozen Rate C", AttributeGroup.REACTIVE),
    PEAK_KVA_FROZEN_RATE_D("Peak kVA Frozen Rate D", AttributeGroup.REACTIVE),
    PEAK_KVA_COIN("Peak kVA Coincidental", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVA("Received Peak kVA", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVA_RATE_A("Received Peak kVA Rate A", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVA_RATE_B("Received Peak kVA Rate B", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVA_RATE_C("Received Peak kVA Rate C", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVA_RATE_D("Received Peak kVA Rate D", AttributeGroup.REACTIVE),
    SUM_PEAK_KVA("Sum Peak kVA", AttributeGroup.REACTIVE),
    SUM_PEAK_KVA_RATE_A("Sum Peak kVA Rate A", AttributeGroup.REACTIVE),
    SUM_PEAK_KVA_RATE_B("Sum Peak kVA Rate B", AttributeGroup.REACTIVE),
    SUM_PEAK_KVA_RATE_C("Sum Peak kVA Rate C", AttributeGroup.REACTIVE),
    SUM_PEAK_KVA_RATE_D("Sum Peak kVA Rate D", AttributeGroup.REACTIVE),
    KVA_Q12("kVA (Quadrants 1 2)", AttributeGroup.REACTIVE),
    KVA_Q34("kVA (Quadrants 3 4)", AttributeGroup.REACTIVE),
    KVA_Q13("kVA (Quadrants 1 3)", AttributeGroup.REACTIVE),
    KVA_Q24("kVA (Quadrants 2 4)", AttributeGroup.REACTIVE),
    SUM_KVAR("Sum kVAr", AttributeGroup.REACTIVE),
    NET_KVAR("Net kVAr", AttributeGroup.REACTIVE),
    PEAK_KVAR("Peak kVAr", AttributeGroup.REACTIVE),
    PEAK_KVAR_COIN("Peak kVAr Coincidental", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_A("Peak kVAr Rate A", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_B("Peak kVAr Rate B", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_C("Peak kVAr Rate C", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_D("Peak kVAr Rate D", AttributeGroup.REACTIVE),
    PEAK_KVAR_Q14("Peak kVAr (Quadrants 1 4)", AttributeGroup.REACTIVE),
    PEAK_KVAR_Q23("Peak kVAr (Quadrants 2 3)", AttributeGroup.REACTIVE),
    COIN_PEAK_KVAR_Q23("Coincident Peak kVAr (Quadrants 2 3)", AttributeGroup.REACTIVE),
    COIN_CUMU_PEAK_KVAR_Q14("Coincident Cumulative Peak kVAr (Quadrants 1 4)", AttributeGroup.REACTIVE),
    COIN_CUMU_PEAK_KVAR_Q23("Coincident Cumulative Peak kVAr (Quadrants 2 3)", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVAR("Received Peak kVAr", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVAR_RATE_A("Received Peak kVAr Rate A", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVAR_RATE_B("Received Peak kVAr Rate B", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVAR_RATE_C("Received Peak kVAr Rate C", AttributeGroup.REACTIVE),
    RECEIVED_PEAK_KVAR_RATE_D("Received Peak kVAr Rate D", AttributeGroup.REACTIVE),
    RECEIVED_COIN_PEAK_KVAR("Received Coincident Peak kVAr", AttributeGroup.REACTIVE),
    SUM_PEAK_KVAR("Sum Peak kVAr", AttributeGroup.REACTIVE),
    SUM_PEAK_KVAR_RATE_A("Sum Peak kVAr Rate A", AttributeGroup.REACTIVE),
    SUM_PEAK_KVAR_RATE_B("Sum Peak kVAr Rate B", AttributeGroup.REACTIVE),
    SUM_PEAK_KVAR_RATE_C("Sum Peak kVAr Rate C", AttributeGroup.REACTIVE),
    SUM_PEAK_KVAR_RATE_D("Sum Peak kVAr Rate D", AttributeGroup.REACTIVE),
    KVAR_Q13("kVAr (Quadrants 1 3)", AttributeGroup.REACTIVE),
    KVAR_Q24("kVAr (Quadrants 2 4)", AttributeGroup.REACTIVE),
    KVAR_Q14("kVAr (Quadrants 1 4)", AttributeGroup.REACTIVE),
    KVAR_Q23("kVAr (Quadrants 2 3)", AttributeGroup.REACTIVE),
    PEAK_KVARH("Peak kVArh", AttributeGroup.REACTIVE),
    PEAK_KVARH_COIN("Peak kVArh Coincidental", AttributeGroup.REACTIVE),
    PHASE("Phase", AttributeGroup.OTHER),
    AVERAGE_DELIVERED_POWER_FACTOR("Average Delivered Power Factor", AttributeGroup.REACTIVE),
    AVERAGE_RECEIVED_POWER_FACTOR("Average Received Power Factor", AttributeGroup.REACTIVE),
    POWER_FACTOR("Power Factor", AttributeGroup.REACTIVE),
    POWER_FACTOR_COIN("Power Factor Coincidental", AttributeGroup.REACTIVE),
    AVERAGE_POWER_FACTOR_Q124("Average Power Factor (Quadrants 1 2 4)", AttributeGroup.REACTIVE),
    AVERAGE_POWER_FACTOR_Q234("Average Power Factor (Quadrants 2 3 4)", AttributeGroup.REACTIVE),
    AVERAGE_POWER_FACTOR("Average Power Factor", AttributeGroup.REACTIVE),
    POWER_FACTOR_ANGLE_PHASE_A("Power Factor Angle (Phase A)", AttributeGroup.REACTIVE),
    POWER_FACTOR_ANGLE_PHASE_B("Power Factor Angle (Phase B)", AttributeGroup.REACTIVE),
    POWER_FACTOR_ANGLE_PHASE_C("Power Factor Angle (Phase C)", AttributeGroup.REACTIVE),
    POWER_FACTOR_PHASE_A("Power Factor (Phase A)", AttributeGroup.REACTIVE),
    POWER_FACTOR_PHASE_B("Power Factor (Phase B)", AttributeGroup.REACTIVE),
    POWER_FACTOR_PHASE_C("Power Factor (Phase C)", AttributeGroup.REACTIVE),
    POWER_FAIL_FLAG("Power Fail Flag", AttributeGroup.STATUS),
    PROFILE_CHANNEL_2("Profile Channel 2", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    PROFILE_CHANNEL_3("Profile Channel 3", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    RECORDING_INTERVAL("Recording Interval", AttributeGroup.OTHER),
    HUMIDITY("Relative Humidity", AttributeGroup.OTHER, false),  //timer pulled data
    RELAY_1_CALL_FOR_COOL("Relay 1 Call for Cool", AttributeGroup.RELAY),
    RELAY_1_LOAD_SIZE("Relay 1 kW Load Size", AttributeGroup.RELAY),
    RELAY_1_REMAINING_CONTROL("Relay 1 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_1_RUN_TIME_DATA_LOG("Relay 1 Run Time", AttributeGroup.RELAY),
    RELAY_1_RUN_TIME_DATA_LOG_5_MIN("Relay 1 Run Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_1_RUN_TIME_DATA_LOG_15_MIN("Relay 1 Run Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_1_RUN_TIME_DATA_LOG_30_MIN("Relay 1 Run Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_1_SHED_STATUS("Relay 1 Shed Status", AttributeGroup.RELAY),
    RELAY_1_SHED_TIME_DATA_LOG("Relay 1 Shed Time", AttributeGroup.RELAY),
    RELAY_1_SHED_TIME_DATA_LOG_5_MIN("Relay 1 Shed Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_1_SHED_TIME_DATA_LOG_15_MIN("Relay 1 Shed Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_1_SHED_TIME_DATA_LOG_30_MIN("Relay 1 Shed Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_1_RELAY_STATE("Relay 1 Relay State", AttributeGroup.RELAY),
    RELAY_2_CALL_FOR_COOL("Relay 2 Call for Cool", AttributeGroup.RELAY),
    RELAY_2_LOAD_SIZE("Relay 2 kW Load Size", AttributeGroup.RELAY),
    RELAY_2_REMAINING_CONTROL("Relay 2 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_2_RUN_TIME_DATA_LOG("Relay 2 Run Time", AttributeGroup.RELAY),
    RELAY_2_RUN_TIME_DATA_LOG_5_MIN("Relay 2 Run Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_2_RUN_TIME_DATA_LOG_15_MIN("Relay 2 Run Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_2_RUN_TIME_DATA_LOG_30_MIN("Relay 2 Run Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_2_SHED_STATUS("Relay 2 Shed Status", AttributeGroup.RELAY),
    RELAY_2_SHED_TIME_DATA_LOG("Relay 2 Shed Time", AttributeGroup.RELAY),
    RELAY_2_SHED_TIME_DATA_LOG_5_MIN("Relay 2 Shed Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_2_SHED_TIME_DATA_LOG_15_MIN("Relay 2 Shed Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_2_SHED_TIME_DATA_LOG_30_MIN("Relay 2 Shed Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_2_RELAY_STATE("Relay 2 Relay State", AttributeGroup.RELAY),
    RELAY_3_CALL_FOR_COOL("Relay 3 Call for Cool", AttributeGroup.RELAY),
    RELAY_3_LOAD_SIZE("Relay 3 kW Load Size", AttributeGroup.RELAY),
    RELAY_3_REMAINING_CONTROL("Relay 3 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_3_RUN_TIME_DATA_LOG("Relay 3 Run Time", AttributeGroup.RELAY),
    RELAY_3_RUN_TIME_DATA_LOG_5_MIN("Relay 3 Run Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_3_RUN_TIME_DATA_LOG_15_MIN("Relay 3 Run Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_3_RUN_TIME_DATA_LOG_30_MIN("Relay 3 Run Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_3_SHED_STATUS("Relay 3 Shed Status", AttributeGroup.RELAY),
    RELAY_3_SHED_TIME_DATA_LOG("Relay 3 Shed Time", AttributeGroup.RELAY),
    RELAY_3_SHED_TIME_DATA_LOG_5_MIN("Relay 3 Shed Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_3_SHED_TIME_DATA_LOG_15_MIN("Relay 3 Shed Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_3_SHED_TIME_DATA_LOG_30_MIN("Relay 3 Shed Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_3_RELAY_STATE("Relay 3 Relay State", AttributeGroup.RELAY),
    RELAY_4_CALL_FOR_COOL("Relay 4 Call for Cool", AttributeGroup.RELAY),
    RELAY_4_REMAINING_CONTROL("Relay 4 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_4_RUN_TIME_DATA_LOG("Relay 4 Run Time", AttributeGroup.RELAY),
    RELAY_4_RUN_TIME_DATA_LOG_5_MIN("Relay 4 Run Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_4_RUN_TIME_DATA_LOG_15_MIN("Relay 4 Run Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_4_RUN_TIME_DATA_LOG_30_MIN("Relay 4 Run Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_4_SHED_STATUS("Relay 4 Shed Status", AttributeGroup.RELAY),
    RELAY_4_SHED_TIME_DATA_LOG("Relay 4 Shed Time", AttributeGroup.RELAY),
    RELAY_4_SHED_TIME_DATA_LOG_5_MIN("Relay 4 Shed Time Data Log 5 Minutes", AttributeGroup.RELAY),
    RELAY_4_SHED_TIME_DATA_LOG_15_MIN("Relay 4 Shed Time Data Log 15 Minutes", AttributeGroup.RELAY),
    RELAY_4_SHED_TIME_DATA_LOG_30_MIN("Relay 4 Shed Time Data Log 30 Minutes", AttributeGroup.RELAY),
    RELAY_4_RELAY_STATE("Relay 4 Relay State", AttributeGroup.RELAY),
    REPORTING_INTERVAL("Reporting Interval", AttributeGroup.OTHER),
    REVERSE_INDUCTIVE_KVARH("Reverse Inductive kVArh", AttributeGroup.REACTIVE, false),   //440 types only
    REVERSE_POWER_FLAG("Reverse Power Flag", AttributeGroup.STATUS),
    RF_DEMAND_RESET_STATUS("RF Demand Reset Status", AttributeGroup.STATUS, false),
    SERVICE_STATUS("Service Status", AttributeGroup.STATUS),
    TAMPER_FLAG("Tamper Flag", AttributeGroup.STATUS),
    TEMPERATURE("Temperature", AttributeGroup.OTHER, false), //timer pulled data
    TEMPERATURE_DEVICE("Temperature of Device", AttributeGroup.RFN_HARDWARE_EVENT, false),
    TOTAL_LUF_COUNT("Total LUF Event Count", AttributeGroup.OTHER),
    TOTAL_LUV_COUNT("Total LUV Event Count", AttributeGroup.OTHER),
    TOTAL_LOF_COUNT("Total LOF Event Count", AttributeGroup.OTHER),
    TOTAL_LOV_COUNT("Total LOV Event Count", AttributeGroup.OTHER),
    LOF_TRIGGER("LOF Trigger Period", AttributeGroup.OTHER),
    LOF_RESTORE("LOF Restore Period", AttributeGroup.OTHER),
    LOF_TRIGGER_TIME("LOF Trigger Time", AttributeGroup.OTHER),
    LOF_RESTORE_TIME("LOF Restore Time", AttributeGroup.OTHER),
    LOF_START_RANDOM_TIME("LOF Start Randomization Time", AttributeGroup.OTHER),
    LOF_END_RANDOM_TIME("LOF End Randomization Time", AttributeGroup.OTHER),
    LOF_MIN_EVENT_DURATION("LOF Event Minimum Duration", AttributeGroup.OTHER),
    LOF_MAX_EVENT_DURATION("LOF Event Maximum Duration", AttributeGroup.OTHER),
    LOV_TRIGGER("LOV Trigger", AttributeGroup.OTHER),
    LOV_RESTORE("LOV Restore", AttributeGroup.OTHER),
    LOV_TRIGGER_TIME("LOV Trigger Time", AttributeGroup.OTHER),
    LOV_RESTORE_TIME("LOV Restore Time", AttributeGroup.OTHER),
    LOV_START_RANDOM_TIME("LOV Start Randomization Time", AttributeGroup.OTHER),
    LOV_END_RANDOM_TIME("LOV End Randomization Time", AttributeGroup.OTHER),
    LOV_MIN_EVENT_DURATION("LOV Minimum Event Duration", AttributeGroup.OTHER),
    LOV_MAX_EVENT_DURATION("LOV Maximum Event Duration", AttributeGroup.OTHER),
    MINIMUM_EVENT_SEPARATION("Minimum PQR Event Separation", AttributeGroup.OTHER),
    POWER_QUALITY_RESPONSE_ENABLED("Power Quality Response Enabled", AttributeGroup.OTHER),
    UNDER_VOLTAGE("Under Voltage", AttributeGroup.RFN_HARDWARE_EVENT, false),
    UNDER_VOLTAGE_THRESHOLD("Under Voltage Threshold", AttributeGroup.RFN_HARDWARE_EVENT, false),
    USAGE("Usage Reading", AttributeGroup.USAGE),
    USAGE_FROZEN("Usage Frozen", AttributeGroup.USAGE),
    USAGE_RATE_A("Usage Rate A", AttributeGroup.USAGE),
    USAGE_RATE_B("Usage Rate B", AttributeGroup.USAGE),
    USAGE_RATE_C("Usage Rate C", AttributeGroup.USAGE),
    USAGE_RATE_D("Usage Rate D", AttributeGroup.USAGE),
    USAGE_RATE_E("Usage Rate E", AttributeGroup.USAGE),
    USAGE_WATER("Water Usage Reading", AttributeGroup.USAGE, false),    //water not readable
    USAGE_GAS("Gas Usage Reading", AttributeGroup.USAGE, false),    //gas not readable
    VOLTAGE("Voltage", AttributeGroup.VOLTAGE),
    VOLTAGE_PHASE_A("Voltage (Phase A)", AttributeGroup.VOLTAGE),
    VOLTAGE_PHASE_B("Voltage (Phase B)", AttributeGroup.VOLTAGE),
    VOLTAGE_PHASE_C("Voltage (Phase C)", AttributeGroup.VOLTAGE),
    VOLTAGE_PROFILE("Voltage Profile", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    WATT_HOUR_PULSE_FAILURE("Watt-Hour Pulse Failure", AttributeGroup.RFN_HARDWARE_EVENT, false),
    ZERO_USAGE_FLAG("Zero Usage Flag", AttributeGroup.STATUS),
    ZIGBEE_LINK_STATUS("ZigBee Link Status", AttributeGroup.STATUS, false),
    TERMINAL_BLOCK_COVER_REMOVAL_FLAG("Terminal Block Cover Removal", AttributeGroup.STATUS, false),   //440 types only
    INDOOR_TEMPERATURE("Indoor Temperature", AttributeGroup.OTHER),
    OUTDOOR_TEMPERATURE("Outdoor Temperature", AttributeGroup.OTHER),
    COOL_SET_TEMPERATURE("Cool Set Temperature", AttributeGroup.OTHER),
    HEAT_SET_TEMPERATURE("Heat Set Temperature", AttributeGroup.OTHER),

    DELIVERED_KWH("Delivered kWh", AttributeGroup.USAGE),
    // Delivered Rate x kWh is currently using USAGE_RATE_X attributes
    RECEIVED_KWH("Received kWh", AttributeGroup.USAGE),
    RECEIVED_KWH_FROZEN("Received kWh Frozen", AttributeGroup.USAGE, false),   //440 types only
    RECEIVED_KWH_RATE_A("Received kWh Rate A", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_B("Received kWh Rate B", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_C("Received kWh Rate C", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_D("Received kWh Rate D", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_E("Received kWh Rate E", AttributeGroup.USAGE),
    RECEIVED_KVAH("Received kVAh", AttributeGroup.USAGE),

    NET_KWH("Net kWh", AttributeGroup.USAGE),
    NET_KWH_RATE_A("Net kWh Rate A", AttributeGroup.USAGE),
    NET_KWH_RATE_B("Net kWh Rate B", AttributeGroup.USAGE),
    NET_KWH_RATE_C("Net kWh Rate C", AttributeGroup.USAGE),
    NET_KWH_RATE_D("Net kWh Rate D", AttributeGroup.USAGE),
    NET_KWH_RATE_E("Net kWh Rate E", AttributeGroup.USAGE),

    SUM_KWH("Sum kWh", AttributeGroup.USAGE),
    SUM_KWH_RATE_A("Sum kWh Rate A", AttributeGroup.USAGE),
    SUM_KWH_RATE_B("Sum kWh Rate B", AttributeGroup.USAGE),
    SUM_KWH_RATE_C("Sum kWh Rate C", AttributeGroup.USAGE),
    SUM_KWH_RATE_D("Sum kWh Rate D", AttributeGroup.USAGE),
    SUM_KVAH("Sum kVAh", AttributeGroup.REACTIVE),
    SUM_KVARH("Sum kVArh", AttributeGroup.REACTIVE),
    SUM_KVARH_RATE_A("Sum kVArh Rate A", AttributeGroup.REACTIVE),
    SUM_KVARH_RATE_B("Sum kVArh Rate B", AttributeGroup.REACTIVE),
    SUM_KVARH_RATE_C("Sum kVArh Rate C", AttributeGroup.REACTIVE),
    SUM_KVARH_RATE_D("Sum kVArh Rate D", AttributeGroup.REACTIVE),

    USAGE_PER_INTERVAL("Usage per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    DELIVERED_KWH_PER_INTERVAL("Delivered kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    RECEIVED_KWH_PER_INTERVAL("Received kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    SUM_KWH_PER_INTERVAL("Sum kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    NET_KWH_PER_INTERVAL("Net kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    SUM_KVAH_PER_INTERVAL("Sum kVAh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    DELIVERED_KVARH_PER_INTERVAL("Delivered kVArh per Interval", AttributeGroup.REACTIVE, false),   //calculated, not readable
    SUM_KVARH_PER_INTERVAL("Sum kVArh per Interval", AttributeGroup.REACTIVE, false),   //calculated, not readable
    WATER_USAGE_PER_INTERVAL("Water Usage per Interval", AttributeGroup.USAGE, false),   //calculated, water not readable
    GAS_USAGE_PER_INTERVAL("GAS Usage per Interval", AttributeGroup.USAGE, false),   //calculated, gas not readable
    FORWARD_INDUCTIVE_KVARH_PER_INTERVAL("Forward Inductive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only
    FORWARD_CAPACITIVE_KVARH_PER_INTERVAL("Forward Capacitive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only
    REVERSE_INDUCTIVE_KVARH_PER_INTERVAL("Reverse Inductive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only
    REVERSE_CAPACITIVE_KVARH_PER_INTERVAL("Reverse Capacitive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only

    DELIVERED_KW_LOAD_PROFILE("Delivered kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    DELIVERED_KVAR_LOAD_PROFILE("Delivered kVAr Load Profile", AttributeGroup.PROFILE, false),//calculated, not readable
    RECEIVED_KW_LOAD_PROFILE("Received kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    SUM_KW_LOAD_PROFILE("Sum kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    NET_KW_LOAD_PROFILE("Net kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    SUM_KVA_LOAD_PROFILE("Sum kVA Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    SUM_KVAR_LOAD_PROFILE("Sum kVAr Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    KVA_LOAD_PROFILE("kVA Load Profile", AttributeGroup.PROFILE, false), //calculated, not readable

    NET_KVARH("Net kVArh", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH("Net Delivered kVArh", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_A("Net Delivered kVArh Rate A", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_B("Net Delivered kVArh Rate B", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_C("Net Delivered kVArh Rate C", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_D("Net Delivered kVArh Rate D", AttributeGroup.REACTIVE),

    RECEIVED_KVARH("Received kVArh", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_A("Received kVArh Rate A", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_B("Received kVArh Rate B", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_C("Received kVArh Rate C", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_D("Received kVArh Rate D", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH("Net Received kVArh", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_A("Net Received kVArh Rate A", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_B("Net Received kVArh Rate B", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_C("Net Received kVArh Rate C", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_D("Net Received kVArh Rate D", AttributeGroup.REACTIVE),

    // RFN Events that map to Event Status points (this list must be kept in sync with both
    // our RFN set below AND its version in RfnConditionType.java. Outages and restores are
    // the only exception to this rule (as in the names don't exactly match))
    ALTERNATE_MODE_ENTRY("Alternate Mode Entry", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    ANSI_SECURITY_FAILED("ANSI Security Failed", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    BAD_UPGRADE_SECURITY_PARAM("Bad Upgrade Security Parameter", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    BATTERY_END_OF_LIFE("Battery End Of Life", AttributeGroup.RFN_HARDWARE_EVENT, false),
    CONFIGURATION_ERROR("Configuration Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    CLOCK_ERROR("Clock Error", AttributeGroup.RFN_OTHER_EVENT, false),
    CRC_FAILURE_MEMORY_CORRUPT("CRC Failure Memory Corrupt", AttributeGroup.RFN_METERING_EVENT, false),
    CRYSTAL_OSCILLATOR_ERROR("Crystal Oscillator Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    CURRENT_LOSS("Current Loss", AttributeGroup.RFN_CURRENT_EVENT, false),
    CURRENT_WAVEFORM_DISTORTION("Current Waveform Distortion", AttributeGroup.RFN_OTHER_EVENT, false),
    DEMAND_OVERLOAD("Demand Overload", AttributeGroup.RFN_DEMAND_EVENT, false),
    DEMAND_READS_AND_RESET("Demand Reads And Reset", AttributeGroup.RFN_DEMAND_EVENT, false),
    DEMAND_THRESHOLD_EXCEEDED_WARNING("Demand Threshold Exceeded Warning", AttributeGroup.RFN_DEMAND_EVENT, false),
    DNP3_ADDRESS_CHANGED("DNP3 Address Changed", AttributeGroup.RFN_OTHER_EVENT, false),
    DISPLAY_LOCKED_BY_WARNING("Display Locked By Warning", AttributeGroup.RFN_OTHER_EVENT, false),
    EEPROM_ACCESS_ERROR("Eeprom Access Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    EMPTY_PIPE("Empty Pipe", AttributeGroup.RFN_OTHER_EVENT, false),
    ENCODER("Encoder", AttributeGroup.RFN_HARDWARE_EVENT, false),
    ENCRYPTION_KEY_TABLE_CRC_ERROR("Encryption Key Table Crc Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    END_OF_CALENDAR_WARNING("End Of Calendar Warning", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE("Energy Accumulated While In Standby Mode", AttributeGroup.RFN_METERING_EVENT, false),
    EXCEEDING_MAXIMUM_FLOW("Exceeding Maximum Flow", AttributeGroup.RFN_OTHER_EVENT, false),
    FAILED_UPGRADE_SIGNATURE_VERIF("Failed Upgrade Signature Verification", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    IMPROPER_METER_ENGINE_OPERATION_WARNING("Improper Meter Engine Operation Warning", AttributeGroup.RFN_HARDWARE_EVENT, false),
    INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR("Inactive Phase Current Diagnostic Error", AttributeGroup.RFN_OTHER_EVENT, false),
    INTERNAL_COMMUNICATION_ERROR("Internal Communication Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    INTERNAL_ERROR_FLAG("Internal Error Flag", AttributeGroup.RFN_HARDWARE_EVENT, false),   //440 types only
    INVALID_SERVICE("Invalid Service", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    LINE_FREQUENCY_WARNING("Line Frequency Warning", AttributeGroup.RFN_METERING_EVENT, false),
    LOAD_SIDE_VOLTAGE_IS_MISSING("Load Side Voltage Is Missing", AttributeGroup.RFN_METERING_EVENT, false),
    LOSS_OF_ALL_CURRENT("Loss Of All Current", AttributeGroup.RFN_CURRENT_EVENT, false),
    LOSS_OF_PHASE_A_CURRENT("Loss Of Phase A Current", AttributeGroup.RFN_CURRENT_EVENT, false),
    LOSS_OF_PHASE_C_CURRENT("Loss Of Phase C Current", AttributeGroup.RFN_CURRENT_EVENT, false),
    LOW_BATTERY_WARNING("Low Battery Warning", AttributeGroup.RFN_HARDWARE_EVENT, false),
    LOW_LOSS_POTENTIAL("Low Loss Potential", AttributeGroup.RFN_METERING_EVENT, false),
    MASS_MEMORY_ERROR("Mass Memory Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    MEASUREMENT_ERROR("Measurement Error", AttributeGroup.RFN_METERING_EVENT, false),
    METER_FUNCTIONING_CORRECTLY("Meter Functioning Correctly", AttributeGroup.RFN_HARDWARE_EVENT, false),
    METER_RECONFIGURE("Meter Reconfigure", AttributeGroup.RFN_HARDWARE_EVENT, false),
    METROLOGY_COMM_FAILURE("Metrology Communication Failure", AttributeGroup.RFN_OTHER_EVENT, false),
    NON_VOLATILE_MEM_FAILURE("Non Volatile Mem Failure", AttributeGroup.RFN_HARDWARE_EVENT, false),
    OUTSTATION_DNP3_SERCOMM_LOCKED("Outstation DNP3 SerComm Locked", AttributeGroup.RFN_OTHER_EVENT, false),
    PASSWORD_TABLE_CRC_ERROR("Password Table Crc Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    PHASE_ANGLE_DISPLACEMENT("Phase Angle Displacement", AttributeGroup.RFN_OTHER_EVENT, false),
    PHASE_LOSS("Phase Loss", AttributeGroup.RFN_OTHER_EVENT, false),
    POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC("Polarity Cross-phase and Energy Flow Diagnostic", AttributeGroup.RFN_OTHER_EVENT, false),
    POTENTIAL_INDICATOR_WARNING("Potential Indicator Warning", AttributeGroup.RFN_METERING_EVENT, false),
    POWER_FAIL_DATA_SAVE_ERROR("Power Fail Data Save Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    PQM_TEST_FAILURE_WARNING("Pqm Test Failure Warning", AttributeGroup.RFN_METERING_EVENT, false),
    RAM_ERROR("Ram Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    REGISTER_FULL_SCALE_EXCEEDED("Register Full-scale Exceeded", AttributeGroup.RFN_OTHER_EVENT, false),
    REVERSE_FLOW("Reverse Flow", AttributeGroup.RFN_OTHER_EVENT, false),
    REVERSED_AGGREGATE("Reversed Aggregate", AttributeGroup.RFN_METERING_EVENT, false),
    REVERSED_PHASE_A("Reversed Phase A", AttributeGroup.RFN_METERING_EVENT, false),
    REVERSED_PHASE_C("Reversed Phase C", AttributeGroup.RFN_METERING_EVENT, false),
    RFN_BLINK_COUNT("Rfn Blink Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    RFN_BLINK_RESTORE_COUNT("Rfn Blink Restore Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    RFN_TEMPERATURE_ALARM("RFN High Temperature Alarm", AttributeGroup.RFN_HARDWARE_EVENT, false),
    RFN_OUTAGE_COUNT("Rfn Outage Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    RFN_OUTAGE_RESTORE_COUNT("Rfn Outage Restore Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    ROM_ERROR("Rom Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SEASON_CHANGE("Season Change", AttributeGroup.RFN_OTHER_EVENT, false),
    SECURITY_CONFIGURATION_ERROR("Security Configuration Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    SELF_CHECK_ERROR("Self Check Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    SENSOR_ERROR("Sensor Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SERVICE_CURRENT_TEST_FAILURE_WARNING("Service Current Test Failure Warning", AttributeGroup.RFN_METERING_EVENT, false),
    SERVICE_DISCONNECT_SWITCH_ERROR("Service Disconnect Switch Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SERVICE_DISCONNECT_SWITCH_OPEN("Service Disconnect Switch Open", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR("Service Disconnect Switch Sensor Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SITESCAN_ERROR("SiteScan Error", AttributeGroup.RFN_OTHER_EVENT, false),
    STORAGE_MODE("Storage Mode", AttributeGroup.RFN_OTHER_EVENT, false),
    STUCK_SWITCH("Stuck Switch", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SUSPECTED_LEAK("Suspected Leak", AttributeGroup.RFN_OTHER_EVENT, false),
    TABLE_CRC_ERROR("Table Crc Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    TEMPERATURE_OUT_OF_RANGE("Temperature Out Of Range", AttributeGroup.RFN_HARDWARE_EVENT, false),
    THD_V_OR_TDD_I_ERROR("THD V or TDD I Error", AttributeGroup.RFN_OTHER_EVENT, false),
    THIRTY_DAYS_NO_USAGE("Thirty Days No Usage", AttributeGroup.RFN_OTHER_EVENT, false),
    TIME_ADJUSTMENT("Time Adjustment", AttributeGroup.RFN_OTHER_EVENT, false),
    TIME_SYNC_FAILED("Time Sync Failed", AttributeGroup.RFN_OTHER_EVENT, false),
    TOU_SCHEDULE_CHANGE("TOU Schedule Change", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    TOU_SCHEDULE_ERROR("TOU Schedule Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    UNCONFIGURED("Unconfigured", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    UNPROGRAMMED("Unprogrammed", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED("User Programmable Temperature Threshold Exceeded", AttributeGroup.RFN_METERING_EVENT, false),
    VIBRATION_TILT_TAMPER_DETECTED("Vibration or Tilt or Insertion - Tamper Detect", AttributeGroup.RFN_METERING_EVENT, false),
    VOLTAGE_ALERTS("Voltage Alerts", AttributeGroup.RFN_METERING_EVENT, false),
    VOLTAGE_LOSS("Voltage Loss", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_A_OUT("Voltage Phase A Out", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_B_OUT("Voltage Phase B Out", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_C_OUT("Voltage Phase C Out", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_ERROR("Voltage Phase Error", AttributeGroup.RFN_METERING_EVENT, false),
    
    // RFN Gen 2 Water/Gas Meter Events
    METER_READ_NO_ENCODER_FOUND("Meter Read - No encoder found", AttributeGroup.RFN_METERING_EVENT, false),
    METER_READ_PARITY_ERROR("Meter Read - Parity error", AttributeGroup.RFN_METERING_EVENT, false),
    METER_READ_NO_EOF_DETECTED("Meter Read - No EOF detected", AttributeGroup.RFN_METERING_EVENT, false),
    METER_READ_PROTOCOL_CANNOT_BE_DETERMINED("Meter Read - Protocol cannot be determined", AttributeGroup.RFN_METERING_EVENT, false),
    METER_READ_FIELD_EXCEEDED_MAXIMUM_DIGITS("Meter Read - Field exceeded maximum digits", AttributeGroup.RFN_METERING_EVENT, false),
    METER_READ_ERROR_READING_SERIAL_NUMBER("Meter Read - Error reading serial number", AttributeGroup.RFN_METERING_EVENT, false),
    METER_READ_CHECKSUM_ERROR("Meter Read - Checksum error", AttributeGroup.RFN_METERING_EVENT, false),
    TAMPER_CABLE_CUT("Tamper - Cable Cut", AttributeGroup.RFN_HARDWARE_EVENT, false),
    
    // RFN Integrated Gas Meter Events
    MAGNET_TAMPERING("Magnet Tampering", AttributeGroup.RFN_METERING_EVENT, false),
    REGISTER_REMOVAL("Register Removal", AttributeGroup.RFN_METERING_EVENT, false),
    
    // RFN Tamper Alarm/Event
    TAMPER_NO_USAGE_OVER_24_HOURS("Tamper - No Usage over 24 hours", AttributeGroup.RFN_METERING_EVENT, false),
    TAMPER_REVERSE_WH_DETECTED("Tamper - Reverse Wh Detected", AttributeGroup.RFN_METERING_EVENT, false),
    TAMPER_LARGE_INCREASE_AFTER_OUTAGE("Tamper - Large Increase After Outage", AttributeGroup.RFN_METERING_EVENT, false),
    TAMPER_LARGE_DECREASE_AFTER_OUTAGE("Tamper - Large Decrease After Outage", AttributeGroup.RFN_METERING_EVENT, false),
    
    // Gateway Events
    SECURITY_ALARM("Security Alarm", AttributeGroup.RFN_OTHER_EVENT, false),
    POWER_FAILURE("Power Failure", AttributeGroup.RFN_OTHER_EVENT, false),
    RADIO_FAILURE("Radio Failure", AttributeGroup.RFN_OTHER_EVENT, false),
    DOOR_OPEN("Door Open", AttributeGroup.RFN_OTHER_EVENT, false),
    NODE_COUNT_EXCEEDED("Node Count Exceeded", AttributeGroup.RFN_OTHER_EVENT, false),
    
    // Gateway Statistics
    STREAMING_CAPABLE_DEVICE_COUNT("Connected Device Count", AttributeGroup.GATEWAY_STATISTICS, false),
    STREAMING_ACTIVE_DEVICE_COUNT("Streaming Device Count", AttributeGroup.GATEWAY_STATISTICS, false),
    DATA_STREAMING_LOAD("Data Streaming Load", AttributeGroup.GATEWAY_STATISTICS, false),
    READY_NODES("Ready Nodes", AttributeGroup.GATEWAY_STATISTICS, false),

    FIRMWARE_VERSION("Firmware Version", AttributeGroup.OTHER),
    FIRMWARE_VERSION_MAJOR("Firmware Major Version", AttributeGroup.OTHER),
    FIRMWARE_VERSION_MINOR("Firmware Minor Version", AttributeGroup.OTHER),
    IGNORED_CONTROL_REASON("Ignored Control Reason", AttributeGroup.OTHER),
    IP_ADDRESS("IP Address", AttributeGroup.OTHER),
    LAST_CONTROL_REASON("Last Control Reason", AttributeGroup.OTHER),
    NEUTRAL_CURRENT_SENSOR("Neutral Current Sensor", AttributeGroup.OTHER),
    SERIAL_NUMBER("Serial Number", AttributeGroup.OTHER),
    UDP_PORT("UDP Port", AttributeGroup.OTHER),
    
    // Estimated load
    CONNECTED_LOAD("Connected Load", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    DIVERSIFIED_LOAD("Diversified Load", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    MAX_LOAD_REDUCTION("Max Load Reduction", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    AVAILABLE_LOAD_REDUCTION("Available Load Reduction", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    
    PORTER_CPU_UTILIZATION("Porter CPU Utilization", AttributeGroup.SYSTEM, false),
    DISPATCH_CPU_UTILIZATION("Dispatch CPU Utilization", AttributeGroup.SYSTEM, false),
    SCANNER_CPU_UTILIZATION("Scanner CPU Utilization", AttributeGroup.SYSTEM, false),
    CALC_CPU_UTILIZATION("Calc CPU Utilization", AttributeGroup.SYSTEM, false),
    CAPCONTROL_CPU_UTILIZATION("CapControl CPU Utilization", AttributeGroup.SYSTEM, false),
    FDR_CPU_UTILIZATION("FDR CPU Utilization", AttributeGroup.SYSTEM, false),
    MACS_CPU_UTILIZATION("MACS CPU Utilization", AttributeGroup.SYSTEM, false),
    
    NOTIFICATION_SERVER_CPU_UTILIZATION("Notification Server CPU Utilization", AttributeGroup.SYSTEM, false),
    SERVICE_MANAGER_CPU_UTILIZATION("Service Manager CPU Utilization", AttributeGroup.SYSTEM, false),
    WEB_SERVICE_CPU_UTILIZATION("Web Service CPU Utilization", AttributeGroup.SYSTEM, false),
    MESSAGE_BROKER_CPU_UTILIZATION("Message Broker CPU Utilization", AttributeGroup.SYSTEM, false),

    PORTER_MEMORY_UTILIZATION("Porter Memory Utilization", AttributeGroup.SYSTEM, false),
    DISPATCH_MEMORY_UTILIZATION("Dispatch Memory Utilization", AttributeGroup.SYSTEM, false),
    SCANNER_MEMORY_UTILIZATION("Scanner Memory Utilization", AttributeGroup.SYSTEM, false),
    CALC_MEMORY_UTILIZATION("Calc Memory Utilization", AttributeGroup.SYSTEM, false),
    CAPCONTROL_MEMORY_UTILIZATION("CapControl Memory Utilization", AttributeGroup.SYSTEM, false),
    FDR_MEMORY_UTILIZATION("FDR Memory Utilization", AttributeGroup.SYSTEM, false),
    MACS_MEMORY_UTILIZATION("MACS Memory Utilization", AttributeGroup.SYSTEM, false),
    
    NOTIFICATION_SERVER_MEMORY_UTILIZATION("Notification Server Memory Utilization", AttributeGroup.SYSTEM, false),
    SERVICE_MANAGER_MEMORY_UTILIZATION("Service Manager Memory Utilization", AttributeGroup.SYSTEM, false),
    WEB_SERVICE_MEMORY_UTILIZATION("Web Service Memory Utilization", AttributeGroup.SYSTEM, false),
    MESSAGE_BROKER_MEMORY_UTILIZATION("Message Broker Memory Utilization", AttributeGroup.SYSTEM, false),
    
    LOAD_MANAGEMENT_CPU_UTILIZATION("Load Management CPU Utilization", AttributeGroup.SYSTEM, false),
    LOAD_MANAGEMENT_MEMORY_UTILIZATION("Load Management Memory Utilization", AttributeGroup.SYSTEM, false),

    THERMOSTAT_RELAY_STATE("Thermostat Relay State", AttributeGroup.RELAY, false),
   
    ANALOG_INPUT_ONE("Analog Input 1", AttributeGroup.CAPCONTROL, false),
    AUTO_BLOCK_ENABLE("Auto Block Enable", AttributeGroup.CAPCONTROL, false),
    AUTO_REMOTE_CONTROL("Auto Remote Control", AttributeGroup.CAPCONTROL, false),
    AUTO_VOLTAGE_CONTROL("Auto Voltage Control", AttributeGroup.CAPCONTROL, false),
    BAD_RELAY("Bad Relay", AttributeGroup.CAPCONTROL, false),
    CLOSE_OPERATION_COUNT("Close Operation Count", AttributeGroup.CAPCONTROL, false),
    CONTROL_MODE("Control Mode", AttributeGroup.CAPCONTROL, false),
    CONTROL_POINT("Control Point", AttributeGroup.CAPCONTROL, false),
    DAILY_MAX_OPERATION("Daily Max Operation", AttributeGroup.CAPCONTROL, false),
    DELTA_VOLTAGE("Delta Voltage", AttributeGroup.CAPCONTROL, false),
    DST_ACTIVE("DST Active", AttributeGroup.CAPCONTROL, false),
    ENABLE_OVUV_CONTROL("Enable OVUV Control", AttributeGroup.CAPCONTROL, false),
    ENABLE_VAR_CONTROL("Enable VAR Control", AttributeGroup.CAPCONTROL, false),
    ENABLE_TEMPERATURE_CONTROL("Enable Temperature Control", AttributeGroup.CAPCONTROL, false),
    ENABLE_TIME_CONTROL("Enable Time Control", AttributeGroup.CAPCONTROL, false),
    FORWARD_BANDWIDTH("Forward Bandwidth", AttributeGroup.CAPCONTROL, false),
    FORWARD_SET_POINT("Forward Set Point", AttributeGroup.CAPCONTROL, false),
    HEARTBEAT_TIMER_CONFIG("Heartbeat Timer Config", AttributeGroup.CAPCONTROL, false),
    HIGH_VOLTAGE("High Voltage", AttributeGroup.CAPCONTROL, false),
    IGNORED_INDICATOR("Ignored Indicator", AttributeGroup.CAPCONTROL, false),
    KEEP_ALIVE("Keep Alive", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_ANALOG("Last Control Reason - Analog", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_DIGITAL("Last Control Reason - Digital", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_LOCAL("Last Control Reason - Local", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_NEUTRAL_FAULT("Last Control Reason - Neutral Fault", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_OVUV("Last Control Reason - OvUv", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_REMOTE("Last Control Reason - Remote", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_SCHEDULED("Last Control Reason - Scheduled", AttributeGroup.CAPCONTROL, false),
    LAST_CONTROL_REASON_TEMPERATURE("Last Control Reason - Temperature", AttributeGroup.CAPCONTROL, false),
    LOW_VOLTAGE("Low Voltage", AttributeGroup.CAPCONTROL, false),
    NEUTRAL_CURRENT_ALARM_THRESHOLD("Neutral Current Alarm Threshold", AttributeGroup.CAPCONTROL, false),
    NEUTRAL_CURRENT_FAULT("Neutral Current Fault", AttributeGroup.CAPCONTROL, false),
    NEUTRAL_LOCKOUT("Neutral Lockout", AttributeGroup.CAPCONTROL, false),
    OPEN_OPERATION_COUNT("Open Operation Count", AttributeGroup.CAPCONTROL, false),
    OPERATION_FAILED_NEUTRAL_CURRENT("Operation Failed Neutral Current", AttributeGroup.CAPCONTROL, false),
    OVER_UNDER_VOLTAGE_TRACKING_TIME("OvUv Tracking Time", AttributeGroup.CAPCONTROL, false),
    OVER_VOLTAGE_COUNT("Over Voltage Count", AttributeGroup.CAPCONTROL, false),
    RADIO_SIGNAL_STRENGTH_INDICATOR("RSSI", AttributeGroup.CAPCONTROL, false),
    RECLOSE_BLOCKED("Reclose Blocked", AttributeGroup.CAPCONTROL, false),
    SCADA_OVERRIDE_CLEAR("SCADA Override Clear", AttributeGroup.CAPCONTROL, false),
    SCADA_OVERRIDE_CONTROL_POINT("SCADA Override Control Point", AttributeGroup.CAPCONTROL, false),
    SCADA_OVERRIDE_COUNTDOWN_TIMER("SCADA Override Countdown Timer", AttributeGroup.CAPCONTROL, false),
    SCADA_OVERRIDE_ENABLE("SCADA Override Enable", AttributeGroup.CAPCONTROL, false),
    SCADA_OVERRIDE_HEARTBEAT("SCADA Override Heartbeat", AttributeGroup.CAPCONTROL, false),
    SCADA_OVERRIDE_MODE("SCADA Override Mode", AttributeGroup.CAPCONTROL, false),
    SOURCE_VOLTAGE("Source Voltage", AttributeGroup.CAPCONTROL, false),
    TAP_DOWN("Tap Down", AttributeGroup.CAPCONTROL, false),
    TAP_POSITION("Tap Position", AttributeGroup.CAPCONTROL, false),
    TAP_UP("Tap Up", AttributeGroup.CAPCONTROL, false),
    TEMPERATURE_ALARM("Temperature Alarm", AttributeGroup.CAPCONTROL, false),
    TERMINATE("Terminate", AttributeGroup.CAPCONTROL, false),
    TIME_TEMP_CONTROL_SEASON_ONE("TimeTemp Control - Season 1", AttributeGroup.CAPCONTROL, false),
    TIME_TEMP_CONTROL_SEASON_TWO("TimeTemp Control - Season 2", AttributeGroup.CAPCONTROL, false),
    TOTAL_OPERATION_COUNT("Total Operation Count", AttributeGroup.CAPCONTROL, false),
    UNDER_VOLTAGE_COUNT("Under Voltage Count", AttributeGroup.CAPCONTROL, false),
    VAR_CONTROL("VAr Control", AttributeGroup.CAPCONTROL, false),
    VOLTAGE_CONTROL("Voltage Control", AttributeGroup.CAPCONTROL, false),
    VOLTAGE_DELTA_ABNORMAL("Voltage Delta Abnormal", AttributeGroup.CAPCONTROL, false),
    PORT_QUEUE_COUNT("Port Queue Count", AttributeGroup.OTHER, false),
    REVERSE_BANDWIDTH("Reverse Bandwidth", AttributeGroup.CAPCONTROL, false),
    REVERSE_SET_POINT("Reverse Set Point", AttributeGroup.CAPCONTROL, false),
    REVERSE_FLOW_INDICATOR("Reverse Flow Indicator", AttributeGroup.CAPCONTROL, false),
    
    EVENT_SUPERSEDED("Event Superseded", AttributeGroup.ITRON, false),
    MEMORY_MAP_LOST("Memory Map Lost", AttributeGroup.ITRON, false),
    TIME_SYNC("Time Sync", AttributeGroup.ITRON, false),
    COLD_START("Cold Start", AttributeGroup.ITRON, false),
    MAX_CONTROL_EXCEEDED("Max Control Exceeded", AttributeGroup.ITRON, false),
    NETWORK_TIMEOUT_CANCEL("Network Timeout Cancel", AttributeGroup.ITRON, false),
    CONFIGURATION_UPDATED_HASH("Configuration Updated Hash", AttributeGroup.ITRON, false),
    CONFIGURATION_PROCESSED("Configuration Processed", AttributeGroup.ITRON, false),
    TIME_LOST("Time Lost", AttributeGroup.ITRON, false),
    SELF_CHECK_FAIL("Self Check Fail", AttributeGroup.ITRON, false),
    INACTIVE_APPLIANCE("Inactive Appliance", AttributeGroup.ITRON, false),
    RADIO_LINK_QUALITY("Radio Link Quality", AttributeGroup.ITRON, false),
    INCORRECT_TLS_IDENTITY("Incorrect TLS Identity", AttributeGroup.ITRON, false),
    KEY_UPDATE("Key Update", AttributeGroup.ITRON, false),
    KEY_UPDATE_FAIL("Key Update Fail", AttributeGroup.ITRON, false),
    TLS_FAIL("TLS Fail", AttributeGroup.ITRON, false),
    BAD_HDLC("Bad HDLC", AttributeGroup.ITRON, false),
    TLS_ALERT("TLS Alert", AttributeGroup.ITRON, false),
    OPTIMIZE_INTELLIGENT_CONTROL("Optimize Intelligent Control", AttributeGroup.ITRON, false),
    FLUSH_LOG("Flush Log", AttributeGroup.ITRON, false),
    SNAP_TO_GOLD("Snap To Gold", AttributeGroup.ITRON, false),
    EVENT_RECEIVED("Event Received", AttributeGroup.ITRON, false),
    LOAD_STATUS("Load Status", AttributeGroup.ITRON, false),
    ;

    private final String keyPrefix = "yukon.common.attribute.builtInAttribute.";

    // These are informational sets not used for display group purposes.
    private static Set<BuiltInAttribute> rfnEventTypes;
    private static Set<BuiltInAttribute> rfnEventAnalogTypes;

    // These are both informational and used for display group purposes.
    private static Set<BuiltInAttribute> readableProfileAttributes;

    private static Set<BuiltInAttribute> readableAttributes;
    
    private static Set<BuiltInAttribute> nonIntervalAttributes;

    // The following maps and sets are used for displaying grouped attribute lists.
    private static Map<AttributeGroup, Set<BuiltInAttribute>> groupedDataAttributes;

    private static Map<AttributeGroup, Set<BuiltInAttribute>> groupedRfnEventAttributes;

    private static Map<AttributeGroup, Set<BuiltInAttribute>> allGroupedAttributes;

    private static final ImmutableSetMultimap<AttributeGroup, BuiltInAttribute> lookupByGroup;
    
    private static Map<AttributeGroup, Set<BuiltInAttribute>> allGroupedStatusTypeAttributes;
    
    private static Map<AttributeGroup, Set<BuiltInAttribute>> allGroupedValueAttributes;
    
    
    static {

        ImmutableSetMultimap.Builder<AttributeGroup, BuiltInAttribute> builder = ImmutableSetMultimap.builder();
        ImmutableSet.Builder<BuiltInAttribute> readableBuilder = ImmutableSet.builder();
        
        for (BuiltInAttribute attribute : values()) {
            builder.put(attribute.getAttributeGroup(), attribute);
            
            if (attribute.isOnDemandReadable) {
                readableBuilder.add(attribute);
            }
        }
        lookupByGroup = builder.build();
        readableAttributes = readableBuilder.build();
        
        buildDataAttributeSets();
        buildRfnEventAttributeSets();
        buildAllAttributeGroups();
        buildAllStatusTypeAttributeGroups();
    }

    /**
     * Builds sets of data attributes.  A data attribute is an attribute that holds a value
     * or measurement.  Data attributes are sometimes called readable attributes since
     * they have numeric values which can be read.
     */
    private static void buildDataAttributeSets() {

        readableProfileAttributes = ImmutableSet.of(
                LOAD_PROFILE,
                PROFILE_CHANNEL_2,
                PROFILE_CHANNEL_3,
                VOLTAGE_PROFILE);

        // This map defines how attributes are grouped in drop downs and list selectors.
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> groupedDataAttributesBuilder =
                ImmutableMap.builder();

        groupedDataAttributesBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, lookupByGroup.get(AttributeGroup.BLINK_AND_OUTAGE));
        groupedDataAttributesBuilder.put(AttributeGroup.CURRENT, lookupByGroup.get(AttributeGroup.CURRENT));
        groupedDataAttributesBuilder.put(AttributeGroup.DEMAND, lookupByGroup.get(AttributeGroup.DEMAND));
        groupedDataAttributesBuilder.put(AttributeGroup.ESTIMATED_LOAD, lookupByGroup.get(AttributeGroup.ESTIMATED_LOAD));
        groupedDataAttributesBuilder.put(AttributeGroup.ITRON, lookupByGroup.get(AttributeGroup.ITRON));
        groupedDataAttributesBuilder.put(AttributeGroup.OTHER, lookupByGroup.get(AttributeGroup.OTHER));
        groupedDataAttributesBuilder.put(AttributeGroup.PROFILE, lookupByGroup.get(AttributeGroup.PROFILE));
        groupedDataAttributesBuilder.put(AttributeGroup.REACTIVE, lookupByGroup.get(AttributeGroup.REACTIVE));
        groupedDataAttributesBuilder.put(AttributeGroup.RELAY, lookupByGroup.get(AttributeGroup.RELAY));
        groupedDataAttributesBuilder.put(AttributeGroup.STATUS, lookupByGroup.get(AttributeGroup.STATUS));
        groupedDataAttributesBuilder.put(AttributeGroup.USAGE, lookupByGroup.get(AttributeGroup.USAGE));
        groupedDataAttributesBuilder.put(AttributeGroup.VOLTAGE, lookupByGroup.get(AttributeGroup.VOLTAGE));

        groupedDataAttributes = groupedDataAttributesBuilder.build();
        
        // This map is used for Device Data Monitor Value Processors.
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> groupedValueAttributesBuilder =
                ImmutableMap.builder();

        groupedValueAttributesBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, lookupByGroup.get(AttributeGroup.BLINK_AND_OUTAGE));
        groupedValueAttributesBuilder.put(AttributeGroup.CURRENT, lookupByGroup.get(AttributeGroup.CURRENT));
        groupedValueAttributesBuilder.put(AttributeGroup.DEMAND, lookupByGroup.get(AttributeGroup.DEMAND));
        groupedValueAttributesBuilder.put(AttributeGroup.PROFILE, lookupByGroup.get(AttributeGroup.PROFILE));
        groupedValueAttributesBuilder.put(AttributeGroup.REACTIVE, lookupByGroup.get(AttributeGroup.REACTIVE));
        groupedValueAttributesBuilder.put(AttributeGroup.USAGE, lookupByGroup.get(AttributeGroup.USAGE));
        groupedValueAttributesBuilder.put(AttributeGroup.VOLTAGE, lookupByGroup.get(AttributeGroup.VOLTAGE));

        allGroupedValueAttributes = groupedValueAttributesBuilder.build();
    }

    /**
     * This method builds ImmutableSet's of RFN event attributes.  An event attribute
     * indicates that some significant event has occurred on a device.
     */
    private static void buildRfnEventAttributeSets() {

        // rfn "events" that are analog, not status
        rfnEventAnalogTypes = ImmutableSet.of(
                TEMPERATURE_DEVICE);

        nonIntervalAttributes = ImmutableSet.of(
                MAXIMUM_VOLTAGE,
                MINIMUM_VOLTAGE,
                PEAK_DEMAND,
                PEAK_DEMAND_DAILY,
                PEAK_DEMAND_FROZEN,
                PEAK_DEMAND_FROZEN_RATE_A,
                PEAK_DEMAND_FROZEN_RATE_B,
                PEAK_DEMAND_FROZEN_RATE_C,
                PEAK_DEMAND_FROZEN_RATE_D,
                PEAK_DEMAND_RATE_A,
                PEAK_DEMAND_RATE_B,
                PEAK_DEMAND_RATE_C,
                PEAK_DEMAND_RATE_D,
                PEAK_KVA,
                PEAK_KVA_FROZEN,
                PEAK_KVA_FROZEN_RATE_A,
                PEAK_KVA_FROZEN_RATE_B,
                PEAK_KVA_FROZEN_RATE_C,
                PEAK_KVA_FROZEN_RATE_D,
                PEAK_KVA_RATE_A,
                PEAK_KVA_RATE_B,
                PEAK_KVA_RATE_C,
                PEAK_KVA_RATE_D,
                PEAK_KVAR,
                PEAK_KVAR_COIN,
                PEAK_KVAR_Q14,
                PEAK_KVAR_Q23,
                PEAK_KVAR_RATE_A,
                PEAK_KVAR_RATE_B,
                PEAK_KVAR_RATE_C,
                PEAK_KVAR_RATE_D,
                RECEIVED_PEAK_DEMAND,
                RECEIVED_PEAK_DEMAND_RATE_A,
                RECEIVED_PEAK_DEMAND_RATE_B,
                RECEIVED_PEAK_DEMAND_RATE_C,
                RECEIVED_PEAK_DEMAND_RATE_D,
                RECEIVED_PEAK_KVA,
                RECEIVED_PEAK_KVA_RATE_A,
                RECEIVED_PEAK_KVA_RATE_B,
                RECEIVED_PEAK_KVA_RATE_C,
                RECEIVED_PEAK_KVA_RATE_D,
                SUM_PEAK_DEMAND,
                SUM_PEAK_DEMAND_RATE_A,
                SUM_PEAK_DEMAND_RATE_B,
                SUM_PEAK_DEMAND_RATE_C,
                SUM_PEAK_DEMAND_RATE_D,
                SUM_PEAK_KVA,
                SUM_PEAK_KVA_RATE_A,
                SUM_PEAK_KVA_RATE_B,
                SUM_PEAK_KVA_RATE_C,
                SUM_PEAK_KVA_RATE_D,
                SUM_PEAK_KVAR,
                SUM_PEAK_KVAR_RATE_A,
                SUM_PEAK_KVAR_RATE_B,
                SUM_PEAK_KVAR_RATE_C,
                SUM_PEAK_KVAR_RATE_D);

        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.addAll(lookupByGroup.get(AttributeGroup.RFN_HARDWARE_EVENT));
        builder.addAll(lookupByGroup.get(AttributeGroup.RFN_SOFTWARE_EVENT));
        builder.addAll(lookupByGroup.get(AttributeGroup.RFN_VOLTAGE_EVENT));
        builder.addAll(lookupByGroup.get(AttributeGroup.RFN_CURRENT_EVENT));
        builder.addAll(lookupByGroup.get(AttributeGroup.RFN_DEMAND_EVENT));
        builder.addAll(lookupByGroup.get(AttributeGroup.RFN_OTHER_EVENT));
        builder.addAll(lookupByGroup.get(AttributeGroup.RFN_METERING_EVENT));
        rfnEventTypes = builder.build();


        // This map defines how attributes are grouped in drop downs and list selectors.
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> groupedRfnEventBuilder = ImmutableMap.builder();

        groupedRfnEventBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_HARDWARE_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_SOFTWARE_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_VOLTAGE_EVENT, lookupByGroup.get(AttributeGroup.RFN_VOLTAGE_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_CURRENT_EVENT, lookupByGroup.get(AttributeGroup.RFN_CURRENT_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_DEMAND_EVENT, lookupByGroup.get(AttributeGroup.RFN_DEMAND_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_OTHER_EVENT, lookupByGroup.get(AttributeGroup.RFN_OTHER_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_METERING_EVENT, lookupByGroup.get(AttributeGroup.RFN_METERING_EVENT));

        groupedRfnEventAttributes = groupedRfnEventBuilder.build();
    }

    /**
     * Group attributes in categories so they can be more easily displayed and
     * found in the UI.  Some data attribute groups and event attribute groups
     * are made into a composite group where there is some logical overlap as
     * in the case of the voltage & current groups.
     */
    private static void buildAllAttributeGroups() {
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> allGroupedBuilder = ImmutableMap.builder();

        ImmutableSet.Builder<BuiltInAttribute> allCurrentAttributes = ImmutableSet.builder();
        allCurrentAttributes.addAll(lookupByGroup.get(AttributeGroup.CURRENT));
        allCurrentAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_CURRENT_EVENT));
        allGroupedBuilder.put(AttributeGroup.CURRENT, allCurrentAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allVoltageAttributes = ImmutableSet.builder();
        allVoltageAttributes.addAll(lookupByGroup.get(AttributeGroup.VOLTAGE));
        allVoltageAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_VOLTAGE_EVENT));
        allGroupedBuilder.put(AttributeGroup.VOLTAGE, allVoltageAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allDemandAttributes = ImmutableSet.builder();
        allDemandAttributes.addAll(lookupByGroup.get(AttributeGroup.DEMAND));
        allDemandAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_DEMAND_EVENT));
        allGroupedBuilder.put(AttributeGroup.DEMAND, allDemandAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allOtherAttributes = ImmutableSet.builder();
        allOtherAttributes.addAll(lookupByGroup.get(AttributeGroup.OTHER));
        allOtherAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_OTHER_EVENT));
        allOtherAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_METERING_EVENT));
        allOtherAttributes.addAll(lookupByGroup.get(AttributeGroup.SYSTEM));
        allGroupedBuilder.put(AttributeGroup.OTHER, allOtherAttributes.build());

        allGroupedBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, lookupByGroup.get(AttributeGroup.BLINK_AND_OUTAGE));
        allGroupedBuilder.put(AttributeGroup.PROFILE, lookupByGroup.get(AttributeGroup.PROFILE));
        allGroupedBuilder.put(AttributeGroup.REACTIVE, lookupByGroup.get(AttributeGroup.REACTIVE));
        allGroupedBuilder.put(AttributeGroup.RELAY, lookupByGroup.get(AttributeGroup.RELAY));
        allGroupedBuilder.put(AttributeGroup.STATUS, lookupByGroup.get(AttributeGroup.STATUS));
        allGroupedBuilder.put(AttributeGroup.USAGE, lookupByGroup.get(AttributeGroup.USAGE));
        allGroupedBuilder.put(AttributeGroup.ESTIMATED_LOAD, lookupByGroup.get(AttributeGroup.ESTIMATED_LOAD));
        allGroupedBuilder.put(AttributeGroup.ITRON, lookupByGroup.get(AttributeGroup.ITRON));

        allGroupedBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_HARDWARE_EVENT));
        allGroupedBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_SOFTWARE_EVENT));
        
        allGroupedBuilder.put(AttributeGroup.GATEWAY_STATISTICS, lookupByGroup.get(AttributeGroup.GATEWAY_STATISTICS));

        allGroupedBuilder.put(AttributeGroup.CAPCONTROL, lookupByGroup.get(AttributeGroup.CAPCONTROL));

        // The attribute group map that is created can be used in conjunction with
        // the selectNameValue tag and groupItems="true".
        allGroupedAttributes = allGroupedBuilder.build();
    }
    
    /**
     * Method builds the Map of Status and event type attributes , excluding analog type attributes
     * 
     */
    private static void buildAllStatusTypeAttributeGroups() {
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> allGroupedStatusTypeBuilder =
            ImmutableMap.builder();

        allGroupedStatusTypeBuilder.put(AttributeGroup.RFN_CURRENT_EVENT,
            lookupByGroup.get(AttributeGroup.RFN_CURRENT_EVENT));
        allGroupedStatusTypeBuilder.put(AttributeGroup.RFN_VOLTAGE_EVENT,
            lookupByGroup.get(AttributeGroup.RFN_VOLTAGE_EVENT));
        allGroupedStatusTypeBuilder.put(AttributeGroup.RFN_DEMAND_EVENT,
            lookupByGroup.get(AttributeGroup.RFN_DEMAND_EVENT));
        allGroupedStatusTypeBuilder.put(AttributeGroup.RFN_OTHER_EVENT,
            lookupByGroup.get(AttributeGroup.RFN_OTHER_EVENT));
        allGroupedStatusTypeBuilder.put(AttributeGroup.RFN_METERING_EVENT,
            lookupByGroup.get(AttributeGroup.RFN_METERING_EVENT));
        allGroupedStatusTypeBuilder.put(AttributeGroup.STATUS, lookupByGroup.get(AttributeGroup.STATUS));

        // All the rfn analog "events" are from the RFN_HARDWARE_EVENTS group. So removing from here
        Set<BuiltInAttribute> rfnHardwareEventsExculdeAnalog =
            Sets.difference(lookupByGroup.get(AttributeGroup.RFN_HARDWARE_EVENT), rfnEventAnalogTypes);
        allGroupedStatusTypeBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, rfnHardwareEventsExculdeAnalog);
        allGroupedStatusTypeBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT,
            lookupByGroup.get(AttributeGroup.RFN_SOFTWARE_EVENT));

        // The attribute group map that is created can be used in conjunction with
        // the selectNameValue tag and groupItems="true".
        allGroupedStatusTypeAttributes = allGroupedStatusTypeBuilder.build();

    }

    private String defaultDescription;
    private AttributeGroup attributeGroup;
    private boolean isOnDemandReadable;

    /**
     * Defaults isOnDemandReadable to true
     */
    private BuiltInAttribute(String defaultDescription, AttributeGroup attributeGroup) {
        this(defaultDescription, attributeGroup, true);
    }

    private BuiltInAttribute(String defaultDescription, AttributeGroup attributeGroup, boolean isOnDemandReadable) {
        this.defaultDescription = defaultDescription;
        this.attributeGroup = attributeGroup;
        this.isOnDemandReadable = isOnDemandReadable;
    }

    public boolean isReadableProfile() {
        return readableProfileAttributes.contains(this);
    }

    public static Map<AttributeGroup, Set<BuiltInAttribute>> getStandardGroupedAttributes() {
        return groupedDataAttributes;
    }
    
    public static Map<AttributeGroup, Set<BuiltInAttribute>> getValueGroupedAttributes() {
        return allGroupedValueAttributes;
    }

    public static Map<AttributeGroup, Set<BuiltInAttribute>> getRfnEventGroupedAttributes() {
        return groupedRfnEventAttributes;
    }

    public static Map<AttributeGroup, Set<BuiltInAttribute>> getAllGroupedAttributes() {
        return allGroupedAttributes;
    }

    public static Set<BuiltInAttribute> getAccumulatorAttributes() {
        return lookupByGroup.get(AttributeGroup.USAGE);
    }

    /**
     * All status and event types (excluding analog events)
     * @return
     */
    public static Set<BuiltInAttribute> getAllStatusTypes() {
        return Sets.union(lookupByGroup.get(AttributeGroup.STATUS), Sets.difference(getRfnEventTypes(), rfnEventAnalogTypes));
    }
    
    /**
     * Returns a set of analog event types.
     * Most often, this method will be used to get a set of events to _exclude_ from some other set.
     */
    public static Set<BuiltInAttribute> getRfnEventAnalogTypes() {
        return rfnEventAnalogTypes;
    }
    
    /**
     * All status and event types (excluding analog events)
     * 
     * @return Map<AttributeGroup, Set<BuiltInAttribute>>
     */
    public static Map<AttributeGroup, Set<BuiltInAttribute>> getAllGroupedStatusTypeAttributes() {
        return allGroupedStatusTypeAttributes;
    }

    /** 
     * Is status or event type (excluding analog events)
     * @return
     */
    public boolean isStatusType() {
        return getAllStatusTypes().contains(this);
    }
    /**
     * All RFN event types (including status and analog)
     * @return
     */
    public static Set<BuiltInAttribute> getRfnEventTypes() {
        return rfnEventTypes;
    }

    public static Set<BuiltInAttribute> getAttributesForGroup(AttributeGroup attributeGroup) {
        return lookupByGroup.get(attributeGroup);
    }

    /** Readable attributes (excludes profile attributes)*/
    public static Set<BuiltInAttribute> getReadableAttributes() {
        return readableAttributes;
    }

    /** Readable attributes + readable profile attributes */
    public static Set<BuiltInAttribute> getAdvancedReadableAttributes() {
        return Sets.union(readableAttributes, readableProfileAttributes);
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefault(keyPrefix + name(), defaultDescription);
    }

    public static void sort(List<BuiltInAttribute> attributes, final MessageSourceAccessor accessor) {
        Collections.sort(attributes, (o1, o2) -> accessor.getMessage(o1.getMessage()).compareTo(accessor.getMessage(o2.getMessage())));
    }

    /**
     * This method should not be used to display the name. Use getMessage() for i18n. This method
     * is used internally for logic that relies on these values remaining the same.
     */
    public String getDescription() {
        return defaultDescription;
    }
    
    public AttributeGroup getAttributeGroup() {
        return attributeGroup;
    }

    /**
     * Indicates whether a given attribute supports RFN interval recording, or only supports
     * midnight/billing readings.
     * @return
     */
    public boolean isIntervalApplicable() {
        return !nonIntervalAttributes.contains(this);
    }
}
package com.cannontech.dr.eatonCloud.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * This is the list of enums used to map LCR 6200C and 6600C channels to BuiltInAttributes
 * It was created as part of the PXMW (PXMiddleWare) project for the Eaton Cloud / Cellular LCR
 */

public enum EatonCloudChannel {
    ACTIVATION_STATUS_R1(110739, "Activation Status R1", BuiltInAttribute.RELAY_1_ACTIVATION_STATUS),
    ACTIVATION_STATUS_R2(112058, "Activation Status R2", BuiltInAttribute.RELAY_2_ACTIVATION_STATUS),
    ACTIVATION_STATUS_R3(112059, "Activation Status R3", BuiltInAttribute.RELAY_3_ACTIVATION_STATUS),
    ACTIVATION_STATUS_R4(112060, "Activation Status R4", BuiltInAttribute.RELAY_4_ACTIVATION_STATUS),
    CLP_TIME_R1(110744, "CLP Time R1", BuiltInAttribute.RELAY_1_COLD_LOAD_PICKUP_TIME),
    CLP_TIME_R2(112067, "CLP Time R2", BuiltInAttribute.RELAY_2_COLD_LOAD_PICKUP_TIME),
    CLP_TIME_R3(112068, "CLP Time R3", BuiltInAttribute.RELAY_3_COLD_LOAD_PICKUP_TIME),
    CLP_TIME_R4(112069, "CLP Time R4", BuiltInAttribute.RELAY_4_COLD_LOAD_PICKUP_TIME),
    COMMS_LOSS_COUNTER(111870, "Comms Loss Counter", BuiltInAttribute.COMMS_LOSS_COUNT),
    DEVICE_TYPE(110745, "Device Type", null),
    DIAGNOSTIC(110752, "Diagnostic", null),
    EVENT_STATE(110597, "Event State", null),
    EVENT_STATE_R1(110596, "Event State R1", BuiltInAttribute.RELAY_1_SHED_STATUS),
    EVENT_STATE_R2(112064, "Event State R2", BuiltInAttribute.RELAY_2_SHED_STATUS),
    EVENT_STATE_R3(112065, "Event State R3", BuiltInAttribute.RELAY_3_SHED_STATUS),
    EVENT_STATE_R4(112066, "Event State R4", BuiltInAttribute.RELAY_4_SHED_STATUS),
    FIRMWARE_UPDATE(110751, "Firmware Update", BuiltInAttribute.FIRMWARE_UPDATE_STATUS),
    FREQUENCY(110741, "Frequency", BuiltInAttribute.FREQUENCY, 0.001),
    LOAD_STATUS_R1(110595, "Load Status R1", BuiltInAttribute.RELAY_1_LOAD_STATE),
    LOAD_STATUS_R2(112061, "Load Status R2", BuiltInAttribute.RELAY_2_LOAD_STATE),
    LOAD_STATUS_R3(112062, "Load Status R3", BuiltInAttribute.RELAY_3_LOAD_STATE),
    LOAD_STATUS_R4(112063, "Load Status R4", BuiltInAttribute.RELAY_4_LOAD_STATE),
    LUF_COUNT(110749, "LUF Count", BuiltInAttribute.TOTAL_LUF_COUNT),
    LUV_COUNT(110750, "LUV Count", BuiltInAttribute.TOTAL_LUV_COUNT),
    POWER_FAIL_COUNT(110746, "Power Fail Count", BuiltInAttribute.BLINK_COUNT),
    RELAY_MAPPING(110743, "Relay Mapping", null),
    RSSI(110599, "RSSI", BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR),
    RUNTIME_R1(110747, "Runtime R1", BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG),
    RUNTIME_R2(112070, "Runtime R2", BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG),
    RUNTIME_R3(112071, "Runtime R3", BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG),
    RUNTIME_R4(112072, "Runtime R4", BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG),
    SHEDTIME_R1(110748, "Shedtime R1", BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG),
    SHEDTIME_R2(112073, "Shedtime R2", BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG),
    SHEDTIME_R3(112074, "Shedtime R3", BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG),
    SHEDTIME_R4(112075, "Shedtime R4", BuiltInAttribute.RELAY_4_SHED_TIME_DATA_LOG),
    VERSION(110600, "Version", null),
    VOLTAGE(110742, "Voltage", BuiltInAttribute.VOLTAGE, 0.1),
    IMEI(110598, "IMEI", null),
    ICCID(112077, "ICCID", null);
   

    private Integer channelId;
    private String shortName;
    private BuiltInAttribute builtInAttribute;
    private double pointMultiplier;

    private static Map<Integer, EatonCloudChannel> channelLookup = Maps.uniqueIndex(Arrays.asList(values()), EatonCloudChannel::getChannelId);
    private static Map<BuiltInAttribute, EatonCloudChannel> attributeChannelLookup;
    private static List<BuiltInAttribute> attributeList = Arrays.stream(EatonCloudChannel.values())
                                                                  .map(EatonCloudChannel::getBuiltInAttribute)
                                                                  .filter(attribute -> attribute != null)
                                                                  .collect(Collectors.toList());
    private static Set<EatonCloudChannel> booleanChannels;
    private static Set<EatonCloudChannel> integerChannels;
    private static Set<EatonCloudChannel> floatChannels;

    static {
        buildAttributeChannelLookup();
        buildBooleanChannels();
        buildIntegerChannels();
        buildFloatChannels();
    }

    private static void buildBooleanChannels() {
        booleanChannels = ImmutableSet.of(ACTIVATION_STATUS_R1,
                                          ACTIVATION_STATUS_R2,
                                          ACTIVATION_STATUS_R3,
                                          ACTIVATION_STATUS_R4,
                                          LOAD_STATUS_R1,
                                          LOAD_STATUS_R2,
                                          LOAD_STATUS_R3,
                                          LOAD_STATUS_R4,
                                          EVENT_STATE_R1,
                                          EVENT_STATE_R2,
                                          EVENT_STATE_R3,
                                          EVENT_STATE_R4);
    }

    private static void buildIntegerChannels() {
        integerChannels = ImmutableSet.of(RSSI,
                                          CLP_TIME_R1,
                                          CLP_TIME_R2,
                                          CLP_TIME_R3,
                                          CLP_TIME_R4,
                                          POWER_FAIL_COUNT,
                                          RUNTIME_R1,
                                          RUNTIME_R2,
                                          RUNTIME_R3,
                                          RUNTIME_R4,
                                          SHEDTIME_R1,
                                          SHEDTIME_R2,
                                          SHEDTIME_R3,
                                          SHEDTIME_R4,
                                          LUF_COUNT,
                                          LUV_COUNT,
                                          FIRMWARE_UPDATE,
                                          COMMS_LOSS_COUNTER);
    }

    private static void buildFloatChannels() {
        floatChannels = ImmutableSet.of(VOLTAGE,
                                        FREQUENCY);
    }

    private static void buildAttributeChannelLookup() {
        ImmutableMap.Builder<BuiltInAttribute, EatonCloudChannel> builder = ImmutableMap.builder();
        for (EatonCloudChannel channel : values()) {
            if (channel.getBuiltInAttribute() != null) {
                builder.put(channel.getBuiltInAttribute(), channel);
            }
        }
        
        attributeChannelLookup = builder.build();
    }

    private EatonCloudChannel(Integer channelId, String shortName, BuiltInAttribute builtInAttribute) {
        this.channelId = channelId;
        this.shortName = shortName;
        this.builtInAttribute = builtInAttribute;
        this.pointMultiplier = 1.0;
    }

    private EatonCloudChannel(Integer channelId, String shortName, BuiltInAttribute builtInAttribute, double pointMultiplier) {
        this.channelId = channelId;
        this.shortName = shortName;
        this.builtInAttribute = builtInAttribute;
        this.pointMultiplier = pointMultiplier;
    }

    public static Map<Integer, EatonCloudChannel> getChannelLookup() {
        return channelLookup;
    }
    
    public static List<BuiltInAttribute> getAttributeList() {
        return attributeList;
    }

    public static Map<BuiltInAttribute, EatonCloudChannel> getAttributeChannelLookup() {
        return attributeChannelLookup;
    }

    public static Set<EatonCloudChannel> getBooleanChannels() {
        return booleanChannels;
    }

    public static Set<EatonCloudChannel> getIntegerChannels() {
        return integerChannels;
    }

    public static Set<EatonCloudChannel> getFloatChannels() {
        return floatChannels;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public String getShortName() {
        return shortName;
    }

    public BuiltInAttribute getBuiltInAttribute() {
        return builtInAttribute;
    }

    public Double getPointMultiplier() {
        return pointMultiplier;
    }

    public static EatonCloudChannel getMWChannel(BuiltInAttribute attribute) {
        return getAttributeChannelLookup().get(attribute);
    }

    /**
     * Returns a List of tags for all the BuiltInAttributes. 
     * If a built in attribute has no associated tag it will be ignored
     */
    public static Set<String> getTagsForAttributes(Set<BuiltInAttribute> attributes) {
        Set<String> channels = attributes.stream()
                .map(attribute -> {
                    EatonCloudChannel channel = EatonCloudChannel.getMWChannel(attribute);
                    return channel != null ? channel.getChannelId().toString() : null;
                })
                .filter(t -> t != null)
                .collect(Collectors.toSet());
        //required for Event Participation - always get this value
        channels.add(EVENT_STATE.channelId.toString());
        //stored in DynamicPAOInfo
        channels.add(VERSION.channelId.toString());
        channels.add(IMEI.channelId.toString());
        channels.add(ICCID.channelId.toString());
        return channels;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EatonCloudChannel getMWChannelID(String mWChannelJsonString) {
        try {
            return EatonCloudChannel.valueOf(mWChannelJsonString);
        } catch (IllegalArgumentException e) {
            throw new TypeNotSupportedException(mWChannelJsonString + " mWChannel is not valid.");
        }
    }
    
    public Integer getRelayNumberByRuntime() {
        switch (this) {
        case RUNTIME_R1:
            return 1;
        case RUNTIME_R2:
            return 2;
        case RUNTIME_R3:
            return 3;
        case RUNTIME_R4:
            return 4;
        default:
            return null;
        }
    }
}

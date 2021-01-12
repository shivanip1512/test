package com.cannontech.dr.pxmw.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

//TODO YUK-23414 Add New/Existing BuiltInAttributes to MWChannel for LCR 6600

/**
 * This is the list of enums used to map LCR 6200C and 6600C channels to BuiltInAttributes
 * It was created as part of the PXMW (PXMiddleWare) project for the Eaton Cloud / Cellular LCR
 */

public enum MWChannel {
    ACTIVATION_STATUS_R1(110739, "Activation Status R1", BuiltInAttribute.RELAY_1_ACTIVATION_STATUS),
    CLP_TIME_R1(110744, "CLP Time R1", null),
    COMMS_LOSS_COUNTER(111870, "Comms Loss Counter", BuiltInAttribute.COMMS_LOSS_COUNT),
    DEVICE_TYPE(110745, "Device Type", null),
    DIAGNOSTIC(110752, "Diagnostic", null),
    EVENT_STATE(110597, "Event State", null),
    EVENT_STATE_R1(110596, "Event State R1", BuiltInAttribute.RELAY_1_SHED_STATUS),
    FIRMWARE_UPDATE(110751, "Firmware Update", BuiltInAttribute.FIRMWARE_UPDATE_STATUS),
    FREQUENCY(110741, "Frequency", BuiltInAttribute.FREQUENCY),
    LOAD_STATUS_R1(110595, "Load Status R1", BuiltInAttribute.RELAY_1_LOAD_STATE),
    LUF_COUNT(110749, "LUF Count", BuiltInAttribute.TOTAL_LUF_COUNT),
    LUV_COUNT(110750, "LUV Count", BuiltInAttribute.TOTAL_LUV_COUNT),
    POWER_FAIL_COUNT(110746, "Power Fail Count", BuiltInAttribute.BLINK_COUNT),
    RELAY_MAPPING(110743, "Relay Mapping", null),
    RSSI(110599, "RSSI", BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR),
    RUNTIME_R1(110747, "Runtime R1", BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG),
    SHEDTIME_R1(110748, "Shedtime R1", BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG),
    VERSION(110600, "Version", BuiltInAttribute.FIRMWARE_VERSION),
    VOLTAGE(110742, "Voltage", BuiltInAttribute.VOLTAGE);

    private Integer channelId;
    private String shortName;
    private BuiltInAttribute builtInAttribute;

    private static Map<Integer, MWChannel> channelLookup = Maps.uniqueIndex(Arrays.asList(values()), MWChannel::getChannelId);
    private static List<BuiltInAttribute> attributeList = Arrays.stream(MWChannel.values())
                                                                  .map(MWChannel::getBuiltInAttribute)
                                                                  .collect(Collectors.toList());

    private MWChannel(Integer channelId, String shortName, BuiltInAttribute builtInAttribute) {
        this.channelId = channelId;
        this.shortName = shortName;
        this.builtInAttribute = builtInAttribute;
    }

    public static Map<Integer, MWChannel> getChannelLookup() {
        return channelLookup;
    }
    
    public static List<BuiltInAttribute> getAttributeList() {
        return attributeList;
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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MWChannel getMWChannelID(String mWChannelJsonString) {
        try {
            return MWChannel.valueOf(mWChannelJsonString);
        } catch (IllegalArgumentException e) {
            throw new TypeNotSupportedException(mWChannelJsonString + " mWChannel is not valid.");
        }
    }
}

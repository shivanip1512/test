package com.cannontech.dr.pxmw.model;

import java.util.Arrays;
import java.util.Map;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

//TODO YUK-23351 Create and Add new BuiltInAttributes
//TODO YUK-23414 Add New/Existing BuiltInAttributes to MWChannel for LCR 6600
public enum MWChannel {
    LOAD_STATUS_R1(110595, "Load Status R1", BuiltInAttribute.SUM_KW),
    EVENT_STATE_R1(110596, "Event State R1", BuiltInAttribute.SUM_KW),
    EVENT_STATE(110597, "Event State", BuiltInAttribute.SUM_KW),
    RSSI(110599, "RSSI", BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR),
    VERSION(110600, "Version", BuiltInAttribute.FIRMWARE_VERSION),
    ACTIVATION_STATUS_R1(110739, "Activation Status R1", BuiltInAttribute.SUM_KW),
    FREQUENCY(110741, "Frequency", BuiltInAttribute.SUM_KW),
    VOLTAGE(110742, "Voltage", BuiltInAttribute.VOLTAGE),
    RELAY_MAPPING(110743, "Relay Mapping", BuiltInAttribute.SUM_KW),
    CLP_TIME_R1(110744, "CLP Time R1", BuiltInAttribute.SUM_KW),
    DEVICE_TYPE(110745, "Device Type", BuiltInAttribute.SUM_KW),
    POWER_FAIL_COUNT(110746, "Power Fail Count", BuiltInAttribute.SUM_KW),
    RUNTIME_R1(110747, "Runtime R1", BuiltInAttribute.SUM_KW),
    SHEDTIME_R1(110748, "Shedtime R1", BuiltInAttribute.SUM_KW),
    LUF_COUNT(110749, "LUF Count", BuiltInAttribute.TOTAL_LUF_COUNT),
    LUV_COUNT(110750, "LUV Count", BuiltInAttribute.TOTAL_LUV_COUNT),
    FIRMWARE_UPDATE(110751, "Firmware Update", BuiltInAttribute.SUM_KW),
    DIAGNOSTIC(110752, "Diagnostic", BuiltInAttribute.SUM_KW),
    COMMS_LOSS_COUNTER(111870, "Comms Loss Counter", BuiltInAttribute.SUM_KW);

    private Integer channelId;
    private String shortName;
    private BuiltInAttribute builtInAttribute;

    private static Map<Integer, MWChannel> channelLookup = Maps.uniqueIndex(Arrays.asList(values()), MWChannel::getChannelId);

    private MWChannel(Integer channelId, String shortName, BuiltInAttribute builtInAttribute) {
        this.channelId = channelId;
        this.shortName = shortName;
        this.builtInAttribute = builtInAttribute;
    }

    public static Map<Integer, MWChannel> getChannelLookup() {
        return channelLookup;
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

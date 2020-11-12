package com.cannontech.dr.pxmw.helper;

import java.util.Arrays;
import java.util.Map;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

// I have not been able to find the attributes that are supposed to be mapped to these in documentation
public enum MWChannel {
    S_RELAY_STATUS(12343, "sRelayStatus", BuiltInAttribute.SUM_KW),
    LOAD_STATE(12939, "LoadState", BuiltInAttribute.SUM_KW),
    XCOM(12877, "XCOM", BuiltInAttribute.SUM_KW),
    LUF_RESTORE(12386, "LUF Restore", BuiltInAttribute.SUM_KW),
    LUF_TRIGGER(12387, "LUF Trigger", BuiltInAttribute.SUM_KW),
    LUV_RESTORE(12940, "LUV Restore", BuiltInAttribute.SUM_KW),
    LUV_TRIGGER(12941, "LUV Trigger", BuiltInAttribute.SUM_KW);

    private Integer channelID;
    private String shortName;
    private BuiltInAttribute builtInAttribute;

    private static Map<Integer, MWChannel> channelLookup;

    private MWChannel(Integer channelID, String shortName, BuiltInAttribute builtInAttribute) {
        this.channelID = channelID;
        this.shortName = shortName;
        this.builtInAttribute = builtInAttribute;
    }

    public static Map<Integer, MWChannel> getChannelLookup() {
        channelLookup = Maps.uniqueIndex(Arrays.asList(values()), MWChannel::getChannelID);
        return channelLookup;
    }

    public Integer getChannelID() {
        return channelID;
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

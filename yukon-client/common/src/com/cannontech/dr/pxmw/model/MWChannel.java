package com.cannontech.dr.pxmw.model;

import java.util.Arrays;
import java.util.Map;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

//TODO YUK-23284 Replace/update attributes mapped to PxMWChannel 
public enum MWChannel {
    S_RELAY_STATUS(12343, "sRelayStatus", BuiltInAttribute.SUM_KW),
    LOAD_STATE(12939, "LoadState", BuiltInAttribute.SUM_KW),
    XCOM(12877, "XCOM", BuiltInAttribute.SUM_KW),
    LUF_RESTORE(12386, "LUF Restore", BuiltInAttribute.SUM_KW),
    LUF_TRIGGER(12387, "LUF Trigger", BuiltInAttribute.SUM_KW),
    LUV_RESTORE(12940, "LUV Restore", BuiltInAttribute.SUM_KW),
    LUV_TRIGGER(12941, "LUV Trigger", BuiltInAttribute.SUM_KW);

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

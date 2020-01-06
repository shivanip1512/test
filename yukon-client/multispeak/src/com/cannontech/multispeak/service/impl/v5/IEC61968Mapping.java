package com.cannontech.multispeak.service.impl.v5;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomain;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventDomainPart;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventIndex;
import com.cannontech.multispeak.constants.iec61689_9.EndDeviceEventType;
import com.google.common.collect.ImmutableMap;

public enum IEC61968Mapping {

    BLINK_COUNT(BuiltInAttribute.BLINK_COUNT, EndDeviceEventDomain.DEVICE_ASSET, EndDeviceEventDomainPart.POWER_QUALITY, EndDeviceEventType.OUTAGE, EndDeviceEventIndex.MOMENTARY_EVENTS),
    TOTAL_LUF_COUNT(BuiltInAttribute.TOTAL_LUF_COUNT, EndDeviceEventDomain.DEVICE_ASSET, EndDeviceEventDomainPart.FREQUENCY, EndDeviceEventType.QUALITY_FLAG, EndDeviceEventIndex.LOW_LIMIT),
    TOTAL_LUV_COUNT(BuiltInAttribute.TOTAL_LUV_COUNT, EndDeviceEventDomain.DEVICE_ASSET, EndDeviceEventDomainPart.VOLTAGE, EndDeviceEventType.QUALITY_FLAG, EndDeviceEventIndex.LOW_LIMIT);
    
    //TODO
    /*FIRMWARE_VERSION, 
    RADIO_LINK_QUALITY,
    EVENT_RECEIVED,
    CONTROL_STATUS,
    AVERAGE_VOLTAGE,
    SERVICE_STATUS,
    MEMORY_MAP_LOST*/

    private BuiltInAttribute attribute;
    private EndDeviceEventDomain endDeviceDomain;
    private EndDeviceEventDomainPart endDeviceEventDomainPart;
    private EndDeviceEventType endDeviceEventType;
    private EndDeviceEventIndex endDeviceEventIndex;

    private IEC61968Mapping(BuiltInAttribute attribute, EndDeviceEventDomain endDeviceDomain, EndDeviceEventDomainPart endDeviceEventDomainPart,
            EndDeviceEventType endDeviceEventType, EndDeviceEventIndex endDeviceEventIndex) {
        this.attribute = attribute;
        this.endDeviceDomain = endDeviceDomain;
        this.endDeviceEventDomainPart = endDeviceEventDomainPart;
        this.endDeviceEventType = endDeviceEventType;
        this.endDeviceEventIndex = endDeviceEventIndex;
    }

    private static ImmutableMap<BuiltInAttribute, IEC61968Mapping> lookupByAttribute;

    static {
        ImmutableMap.Builder<BuiltInAttribute, IEC61968Mapping> idBuilder = ImmutableMap.builder();
        for (IEC61968Mapping mappingTypeCode : values()) {
            idBuilder.put(mappingTypeCode.getAttribute(), mappingTypeCode);
        }
        lookupByAttribute = idBuilder.build();

    }

    public static IEC61968Mapping getIEC61968MappingData(BuiltInAttribute attribute) {
        return lookupByAttribute.get(attribute);
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public EndDeviceEventDomain getEndDeviceDomain() {
        return endDeviceDomain;
    }

    public EndDeviceEventDomainPart getEndDeviceEventDomainPart() {
        return endDeviceEventDomainPart;
    }

    public EndDeviceEventType getEndDeviceEventType() {
        return endDeviceEventType;
    }

    public EndDeviceEventIndex getEndDeviceEventIndex() {
        return endDeviceEventIndex;
    }

}

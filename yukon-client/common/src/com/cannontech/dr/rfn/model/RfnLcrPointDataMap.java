package com.cannontech.dr.rfn.model;

import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RfnLcrPointDataMap {
    BLINK_COUNT(BuiltInAttribute.BLINK_COUNT, "/DRReport/Info/BlinkCount", null, null),
    CONTROL_STATUS(BuiltInAttribute.CONTROL_STATUS, "/DRReport/Info/Flags", 0x2, 1),
    RECORDING_INTERVAL(BuiltInAttribute.RECORDING_INTERVAL, "/DRReport/Info/RecordingInterval", null, null),
    RELAY_1_LOAD_SIZE(BuiltInAttribute.RELAY_1_LOAD_SIZE, "/DRReport/Relays/Relay[@id=0]/KwRating", null, null),
    RELAY_1_REMAINING_CONTROL(BuiltInAttribute.RELAY_1_REMAINING_CONTROL, "/DRReport/Relays/Relay[@id=0]/RemainingControlTime", null, null),
    RELAY_2_LOAD_SIZE(BuiltInAttribute.RELAY_2_LOAD_SIZE, "/DRReport/Relays/Relay[@id=1]/KwRating", null, null),
    RELAY_2_REMAINING_CONTROL(BuiltInAttribute.RELAY_2_REMAINING_CONTROL, "/DRReport/Relays/Relay[@id=1]/RemainingControlTime", null, null),
    RELAY_3_LOAD_SIZE(BuiltInAttribute.RELAY_3_LOAD_SIZE, "/DRReport/Relays/Relay[@id=2]/KwRating", null, null),
    RELAY_3_REMAINING_CONTROL(BuiltInAttribute.RELAY_3_REMAINING_CONTROL, "/DRReport/Relays/Relay[@id=2]/RemainingControlTime", null, null),
    REPORTING_INTERVAL(BuiltInAttribute.REPORTING_INTERVAL, "/DRReport/Info/ReportingInterval", null, null),
    SERVICE_STATUS(BuiltInAttribute.SERVICE_STATUS, "/DRReport/Info/Flags", 0xC, 2),
    TAMPER_FLAG(BuiltInAttribute.TAMPER_FLAG, "/DRReport/Info/Flags", 0x10, 4),
    TOTAL_LUF_EVENT(BuiltInAttribute.TOTAL_LUF_COUNT, "/DRReport/Info/TotalLUFEvents", null, null),
    TOTAL_LUV_EVENT(BuiltInAttribute.TOTAL_LUV_COUNT, "/DRReport/Info/TotalLUVEvents", null, null);

    private final BuiltInAttribute attribute;
    private final String xPathQuery;
    private final Integer mask;
    private final Integer shift;
    
    private static final Logger log = YukonLogManager.getLogger(RfnLcrPointDataMap.class);
    private static final Set<RfnLcrPointDataMap> lcr6200PointDataMap;
    private static final Set<RfnLcrPointDataMap> lcr6600PointDataMap;
    
    RfnLcrPointDataMap(BuiltInAttribute attribute, String xPathQuery, Integer mask, Integer shift) {
        this.attribute = attribute;
        this.xPathQuery = xPathQuery;
        this.mask = mask;
        this.shift = shift;
    }
    static {
        Builder<RfnLcrPointDataMap> builder = ImmutableSet.builder();
        builder.add(BLINK_COUNT);
        builder.add(CONTROL_STATUS);
        builder.add(RECORDING_INTERVAL);
        builder.add(REPORTING_INTERVAL);
        builder.add(SERVICE_STATUS);
        builder.add(TAMPER_FLAG);
        builder.add(TOTAL_LUF_EVENT);
        builder.add(TOTAL_LUV_EVENT);
        builder.add(RELAY_1_LOAD_SIZE);
        builder.add(RELAY_1_REMAINING_CONTROL);
        lcr6200PointDataMap = builder.build();

        builder.add(RELAY_2_LOAD_SIZE);
        builder.add(RELAY_2_REMAINING_CONTROL);
        builder.add(RELAY_3_LOAD_SIZE);
        builder.add(RELAY_3_REMAINING_CONTROL);
        lcr6600PointDataMap = builder.build();
    }
    
    public static Set<RfnLcrPointDataMap> getRelayMapByPaoType(PaoType paoType) {
        if (paoType == PaoType.LCR6200_RFN) {
            return getLcr6200PointDataMap();
        } else if (paoType == PaoType.LCR6600_RFN) {
            return getLcr6600PointDataMap();
        } else {
            log.error("No RFN LCR point mapping data found for pao type: " + paoType.getPaoTypeName());
            throw new IllegalArgumentException();
        }
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }
    public String getxPathQuery() {
        return xPathQuery;
    }
    public Integer getMask() {
        return mask;
    }
    public Integer getShift() {
        return shift;
    }
    public static Set<RfnLcrPointDataMap> getLcr6200PointDataMap() {
        return lcr6200PointDataMap;
    }
    public static Set<RfnLcrPointDataMap> getLcr6600PointDataMap() {
        return lcr6600PointDataMap;
    }
}

package com.cannontech.dr.rfn.model;

import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/* This specifies the data and translations needed to pull RF LCR information from the XML received.
 * 
 * This includes offsets, bitshifts, and multipliers similar to what Porter would do to incoming data. Point Multipliers
 * are not applied to this data but the multipliers specified here are.
 */
public enum RfnLcrPointDataMap {
    BLINK_COUNT(BuiltInAttribute.BLINK_COUNT, "/DRReport/Info/BlinkCount"),
    CONTROL_STATUS(BuiltInAttribute.CONTROL_STATUS, "/DRReport/Info/Flags", 0x2, 1, null),
    RECORDING_INTERVAL(BuiltInAttribute.RECORDING_INTERVAL, "/DRReport/Info/RecordingInterval"),
    RELAY_1_LOAD_SIZE(BuiltInAttribute.RELAY_1_LOAD_SIZE, "/DRReport/Relays/Relay[@id=0]/KwRating", null, null, .001),
    RELAY_1_REMAINING_CONTROL(BuiltInAttribute.RELAY_1_REMAINING_CONTROL, "/DRReport/Relays/Relay[@id=0]/RemainingControlTime"),
    RELAY_2_LOAD_SIZE(BuiltInAttribute.RELAY_2_LOAD_SIZE, "/DRReport/Relays/Relay[@id=1]/KwRating", null, null, .001),
    RELAY_2_REMAINING_CONTROL(BuiltInAttribute.RELAY_2_REMAINING_CONTROL, "/DRReport/Relays/Relay[@id=1]/RemainingControlTime"),
    RELAY_3_LOAD_SIZE(BuiltInAttribute.RELAY_3_LOAD_SIZE, "/DRReport/Relays/Relay[@id=2]/KwRating", null, null, .001),
    RELAY_3_REMAINING_CONTROL(BuiltInAttribute.RELAY_3_REMAINING_CONTROL, "/DRReport/Relays/Relay[@id=2]/RemainingControlTime"),
    REPORTING_INTERVAL(BuiltInAttribute.REPORTING_INTERVAL, "/DRReport/Info/ReportingInterval"),
    SERVICE_STATUS(BuiltInAttribute.SERVICE_STATUS, "/DRReport/Info/Flags", 0xC, 2, null),
    TOTAL_LUF_EVENT(BuiltInAttribute.TOTAL_LUF_COUNT, "/DRReport/Info/TotalLUFEvents"),
    TOTAL_LUV_EVENT(BuiltInAttribute.TOTAL_LUV_COUNT, "/DRReport/Info/TotalLUVEvents"),
    AVERAGE_VOLTAGE(BuiltInAttribute.AVERAGE_VOLTAGE, ""),
    MINIMUM_VOLTAGE(BuiltInAttribute.MINIMUM_VOLTAGE, ""),
    MAXIMUM_VOLTAGE(BuiltInAttribute.MAXIMUM_VOLTAGE, "");

    private final BuiltInAttribute attribute;
    private final String xPathQuery;
    private final Integer mask;
    private final Integer shift;
    private final Double multiplier;
    
    private static final Logger log = YukonLogManager.getLogger(RfnLcrPointDataMap.class);
    private static final Set<RfnLcrPointDataMap> lcr6200PointDataMap;
    private static final Set<RfnLcrPointDataMap> lcr6600PointDataMap;
    private static final Set<RfnLcrPointDataMap> lcr6601SPointDataMap;
    
    RfnLcrPointDataMap(BuiltInAttribute attribute, String xPathQuery) {
        this(attribute, xPathQuery, null, null, null);
    }

    RfnLcrPointDataMap(BuiltInAttribute attribute, String xPathQuery, Integer mask, Integer shift, Double multiplier) {
        this.attribute = attribute;
        this.xPathQuery = xPathQuery;
        this.mask = mask;
        this.shift = shift;
        this.multiplier = multiplier;
    }
    
    static {
        Builder<RfnLcrPointDataMap> builder = ImmutableSet.builder();
        builder.add(BLINK_COUNT);
        builder.add(CONTROL_STATUS);
        builder.add(RECORDING_INTERVAL);
        builder.add(REPORTING_INTERVAL);
        builder.add(SERVICE_STATUS);
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

        builder.add(AVERAGE_VOLTAGE);
        builder.add(MINIMUM_VOLTAGE);
        builder.add(MAXIMUM_VOLTAGE);
        lcr6601SPointDataMap = builder.build();
    }
    
    public static Set<RfnLcrPointDataMap> getRelayMapByPaoType(PaoType paoType) {
        if (paoType == PaoType.LCR6200_RFN) {
            return getLcr6200PointDataMap();
        } else if (paoType == PaoType.LCR6600_RFN) {
            return getLcr6600PointDataMap();
        } else if (paoType == PaoType.LCR6601S_RFN) {
            return getLcr6601sPointDataMap();
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
    
    private static Set<RfnLcrPointDataMap> getLcr6601sPointDataMap() {
        return lcr6601SPointDataMap;
    }

    public Double getMultiplier() {
        return multiplier;
    }
}

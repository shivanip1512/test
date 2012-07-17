package com.cannontech.dr.rfn.model;

import java.text.ParseException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RfnLcrRelayDataMap {
    RELAY_1("[@id=0]", BuiltInAttribute.RELAY_1_RUN_TIME, BuiltInAttribute.RELAY_1_SHED_TIME, BuiltInAttribute.RELAY_1_CONTROL_TIME),
    RELAY_2("[@id=1]", BuiltInAttribute.RELAY_2_RUN_TIME, BuiltInAttribute.RELAY_2_SHED_TIME, BuiltInAttribute.RELAY_2_CONTROL_TIME),
    RELAY_3("[@id=2]", BuiltInAttribute.RELAY_3_RUN_TIME, BuiltInAttribute.RELAY_3_SHED_TIME, BuiltInAttribute.RELAY_3_CONTROL_TIME);
    
    private String relayIdXPathString;
    private BuiltInAttribute runTimeAttribute;
    private BuiltInAttribute shedTimeAttribute;
    private BuiltInAttribute controlTimeAttribute;
    
    private static final Logger log = YukonLogManager.getLogger(RfnLcrRelayDataMap.class);
    private static final Set<RfnLcrRelayDataMap> lcr6200RelayMap;
    private static final Set<RfnLcrRelayDataMap> lcr6600RelayMap;
    
    RfnLcrRelayDataMap(String xPathQuery, BuiltInAttribute runTime, 
            BuiltInAttribute shedTime, BuiltInAttribute controlTime) {
        this.relayIdXPathString = xPathQuery;
        this.runTimeAttribute = runTime;
        this.shedTimeAttribute = shedTime;
        this.controlTimeAttribute = controlTime;
    }

    static {
        Builder<RfnLcrRelayDataMap> builder = ImmutableSet.builder();
        builder.add(RELAY_1);
        lcr6200RelayMap = builder.build();
        
        builder.add(RELAY_2);
        builder.add(RELAY_3);
        lcr6600RelayMap = builder.build();
    }
    
    public static Set<RfnLcrRelayDataMap> getRelayMapByPaoType(PaoType paoType) throws ParseException {
        if (paoType == PaoType.LCR6200_RFN) {
            return getLcr6200RelayMap();
        } else if (paoType == PaoType.LCR6600_RFN) {
            return getLcr6600RelayMap();
        } else {
            log.error("No RFN LCR relay mapping data found for pao type: " + paoType.getPaoTypeName());
            throw new ParseException("No RFN LCR relay mapping data found for pao type: " + paoType.getPaoTypeName(), 0);
        }
    }

    public String getRelayIdXPathString() {
        return relayIdXPathString;
    }
    public BuiltInAttribute getRunTimeAttribute() {
        return runTimeAttribute;
    }
    public BuiltInAttribute getShedTimeAttribute() {
        return shedTimeAttribute;
    }
    public BuiltInAttribute getControlTimeAttribute() {
        return controlTimeAttribute;
    }
    public static Set<RfnLcrRelayDataMap> getLcr6200RelayMap() {
        return lcr6200RelayMap;
    }
    public static Set<RfnLcrRelayDataMap> getLcr6600RelayMap() {
        return lcr6600RelayMap;
    }
}

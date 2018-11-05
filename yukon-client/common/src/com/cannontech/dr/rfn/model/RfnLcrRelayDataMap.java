package com.cannontech.dr.rfn.model;

import java.text.ParseException;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RfnLcrRelayDataMap {
    RELAY_1("[@id=0]", BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG, 1),
    RELAY_2("[@id=1]", BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG, BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG, 2),
    RELAY_3("[@id=2]", BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG, BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG, 3);
    
    private final String relayIdXPathString;
    private final BuiltInAttribute runTimeAttribute;
    private final BuiltInAttribute shedTimeAttribute;
    private final int relayIndex;
    
    private static final Logger log = YukonLogManager.getLogger(RfnLcrRelayDataMap.class);
    private static final Set<RfnLcrRelayDataMap> lcr6200RelayMap;
    private static final Set<RfnLcrRelayDataMap> lcr6600RelayMap;
    
    RfnLcrRelayDataMap(String xPathQuery, BuiltInAttribute runTime, BuiltInAttribute shedTime, int relayIndex) {
        relayIdXPathString = xPathQuery;
        runTimeAttribute = runTime;
        shedTimeAttribute = shedTime;
        this.relayIndex = relayIndex;
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
    
    public int getIndex() {
        return relayIndex;
    }
    
    public static Set<RfnLcrRelayDataMap> getLcr6200RelayMap() {
        return lcr6200RelayMap;
    }
    
    public static Set<RfnLcrRelayDataMap> getLcr6600RelayMap() {
        return lcr6600RelayMap;
    }
}

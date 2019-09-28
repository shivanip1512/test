package com.cannontech.dr.rfn.model;

import java.text.ParseException;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableSet;
import com.cannontech.dr.rfn.tlv.FieldType;

public enum RfnLcr6700RelayMap {
    RELAY_RUNTIME_1(0, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, 1, FieldType.RELAY_N_RUNTIME),
    RELAY_RUNTIME_2(1, BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG, 2, FieldType.RELAY_N_RUNTIME),
    RELAY_RUNTIME_3(2, BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG, 3, FieldType.RELAY_N_RUNTIME),
    RELAY_RUNTIME_4(3, BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG, 4, FieldType.RELAY_N_RUNTIME),
    RELAY_SHEDTIME_1(0, BuiltInAttribute.RELAY_1_SHED_TIME_DATA_LOG, null, FieldType.RELAY_N_SHEDTIME),
    RELAY_SHEDTIME_2(1, BuiltInAttribute.RELAY_2_SHED_TIME_DATA_LOG, null, FieldType.RELAY_N_SHEDTIME),
    RELAY_SHEDTIME_3(2, BuiltInAttribute.RELAY_3_SHED_TIME_DATA_LOG, null, FieldType.RELAY_N_SHEDTIME),
    RELAY_SHEDTIME_4(3, BuiltInAttribute.RELAY_4_SHED_TIME_DATA_LOG, null, FieldType.RELAY_N_SHEDTIME);

    private final Integer relayNumber;
    private final BuiltInAttribute relayAttribute;
    private final Integer relayRunTimeIndex;
    private final FieldType fieldType;

    private static final Logger log = YukonLogManager.getLogger(RfnLcr6700RelayMap.class);
    private static final Set<RfnLcr6700RelayMap> lcr6700RelayMap = ImmutableSet.copyOf(values());

    RfnLcr6700RelayMap(Integer relayNumber, BuiltInAttribute relayAttribute, Integer relayRunTimeIndex, FieldType fieldType) {
        this.relayNumber = relayNumber;
        this.relayAttribute = relayAttribute;
        this.relayRunTimeIndex = relayRunTimeIndex;
        this.fieldType = fieldType;
    }

    public static Set<RfnLcr6700RelayMap> getRelayMapByPaoType(PaoType paoType) throws ParseException {
        if (paoType == PaoType.LCR6700_RFN) {
            return getLcr6700RelayMap();
        }
        log.error("No RFN LCR relay mapping data found for pao type: " + paoType.getPaoTypeName());
        throw new ParseException("No RFN LCR relay mapping data found for pao type: " + paoType.getPaoTypeName(), 0);
    }

    public Integer getRelayNumber() {
        return relayNumber;
    }

    public BuiltInAttribute getAttribute() {
        return relayAttribute;
    }

    public Integer getIndex() {
        return relayRunTimeIndex;
    }

    public static Set<RfnLcr6700RelayMap> getLcr6700RelayMap() {
        return lcr6700RelayMap;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}

package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

public enum MeterClass {

    ITRON_CENTRON("Itron Centron"),
    ITRON_SENTINEL("Itron Sentinel"),
    LGYR_FOCUS_KWH("Landis and Gyr Focus kWh"),
    LGYR_FOCUS_AX("Landis and Gyr Focus AX"),
    LGYR_FOCUS_AX_RX_500("Landis and Gyr Focus AX-RX-500"),
    LGYR_S4("Landis and Gyr S4"),
    ELSTER_A3("Elster A3"),
    ELO("ELO"),
    NEXT_GEN_WATER_NODE("Next Gen Water Node");
    
    private static Map<String, MeterClass> nameLookup;
    
    static {
        nameLookup = Maps.uniqueIndex(Arrays.asList(values()), MeterClass::getDisplayName);
    }
    
    private MeterClass(String displayName) {
        this.displayName = displayName;
    }
    

    @JsonCreator
    public static MeterClass getByDisplayName(String displayName) {
        return Optional.ofNullable(nameLookup.getOrDefault(displayName, null))
                .orElseThrow(() -> new IllegalArgumentException("Unknown Meter Class " + displayName));
    }

    public String getDisplayName() {
        return displayName;
    }

    private String displayName;
}
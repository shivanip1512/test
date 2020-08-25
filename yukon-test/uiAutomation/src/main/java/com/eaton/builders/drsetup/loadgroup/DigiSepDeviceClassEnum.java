package com.eaton.builders.drsetup.loadgroup;

import java.util.Random;

public enum DigiSepDeviceClassEnum {

    HVAC_COMPRESSOR_FURNACE("HVAC_COMPRESSOR_FURNACE"),
    BASEBOARD_HEAT("BASEBOARD_HEAT"),
    WATER_HEATER("WATER_HEATER"),
    POOL_PUMP("POOL_PUMP"),
    SMART_APPLIANCE("SMART_APPLIANCE"),
    IRRIGATION_PUMP("IRRIGATION_PUMP"),
    MANAGED_COMMERCIAL_INDUSTRIAL("IRRIGATION_PUMP"),
    SIMPLE_RESIDENTIAL_ON_OFF("SIMPLE_RESIDENTIAL_ON_OFF"),
    EXTERIOR_LIGHTING("EXTERIOR_LIGHTING"),
    INTERIOR_LIGHTING("INTERIOR_LIGHTING"),
    ELECTRIC_VEHICLE("ELECTRIC_VEHICLE"),
    GENERATION_SYSTEMS("GENERATION_SYSTEMS");

    private final String deviceClassSet;

    DigiSepDeviceClassEnum(String deviceClassSet) {
        this.deviceClassSet = deviceClassSet;
    }

    public String getDeviceClassSet() {
        return this.deviceClassSet;
    }

    public static DigiSepDeviceClassEnum getRandomDeviceClassSet() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}

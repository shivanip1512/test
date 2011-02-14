package com.cannontech.stars.dr.appliance.model;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;

public enum ApplianceCategoryIcon implements IconEnum {
    NONE(null),
    AC("AC"),
    DUAL_FUEL("DualFuel"),
    ELECTRIC("Electric"),
    GENERATION("Generation"),
    GRAIN_DRYER("GrainDryer"),
    HEAT_PUMP("HeatPump"),
    HOT_TUB("HotTub"),
    IRRIGATION("Irrigation"),
    LOAD("Load"),
    POOL("Pool"),
    SETBACK("Setback"),
    STORAGE_HEAT("StorageHeat"),
    WATER_HEATER("WaterHeater"),
    OTHER(null);

    private String filename;

    private final static String keyPrefix = "yukon.dr.appliance.category.iconName.";
    private final static ImmutableMap<String, ApplianceCategoryIcon> lookupByFilename;

    static {
        lookupByFilename = IconEnumUtil.buildByFilenameMap(values());
    }

    private ApplianceCategoryIcon(String baseFilename) {
        filename = baseFilename == null
            ? null : "yukon/Icons/" + baseFilename + ".png";
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    @Override
    public String getFilename() {
        return filename;
    }

    public static ApplianceCategoryIcon getByFilename(String filename) {
        ApplianceCategoryIcon retVal = lookupByFilename.get(filename);
        if (retVal == null) {
            if (StringUtils.isBlank(filename)) {
                retVal = NONE;
            } else {
                retVal = OTHER;
            }
        }
        return retVal;
    }
}

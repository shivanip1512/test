package com.cannontech.stars.dr.appliance.model;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;

public enum ControlPercentIcon implements IconEnum {
    NONE(null),
    SIXTH("SixthSm"),
    QUARTER("QuarterSm"),
    THIRD("ThirdSm"),
    HALF("HalfSm"),
    OTHER(null);

    private String filename;

    private final static String keyPrefix = "yukon.dr.appliance.controlPercent.iconName.";
    private final static ImmutableMap<String, ControlPercentIcon> lookupByFilename;

    static {
        lookupByFilename = IconEnumUtil.buildByFilenameMap(values());
    }

    private ControlPercentIcon(String baseFilename) {
        filename = baseFilename == null ? null : baseFilename + ".gif";
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    @Override
    public String getFilename() {
        return filename;
    }

    public static ControlPercentIcon getByFilename(String filename) {
        ControlPercentIcon retVal = lookupByFilename.get(filename);
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

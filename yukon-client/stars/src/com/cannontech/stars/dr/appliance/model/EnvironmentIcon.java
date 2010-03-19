package com.cannontech.stars.dr.appliance.model;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;

public enum EnvironmentIcon implements IconEnum {
    NONE(null),
    ONE_TREE("Tree1Sm"),
    TWO_TREES("Tree2Sm"),
    THREE_TREES("Tree3Sm"),
    OTHER(null);

    private String filename;

    private final static String keyPrefix = "yukon.dr.appliance.environment.iconName.";
    private final static ImmutableMap<String, EnvironmentIcon> lookupByFilename;

    static {
        lookupByFilename = IconEnumUtil.buildByFilenameMap(values());
    }

    private EnvironmentIcon(String baseFilename) {
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

    public static EnvironmentIcon getByFilename(String filename) {
        EnvironmentIcon retVal = lookupByFilename.get(filename);
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

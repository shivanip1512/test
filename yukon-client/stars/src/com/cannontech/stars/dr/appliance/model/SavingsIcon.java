package com.cannontech.stars.dr.appliance.model;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum SavingsIcon implements IconEnum {
    NONE(null),
    LOW("$Sm"),
    MEDIUM("$$Sm"),
    HIGH("$$$Sm"),
    OTHER(null);

    private String filename;

    private final static String keyPrefix = "yukon.dr.appliance.savings.iconName.";
    private final static ImmutableMap<String, SavingsIcon> lookupByFilename;

    static {
        Builder<String, SavingsIcon> byFilenameBuilder =
            ImmutableMap.builder();

        for (SavingsIcon icon : values()) {
            if (icon != NONE && icon != OTHER) {
                byFilenameBuilder.put(icon.getFilename(), icon);
            }
        }
        lookupByFilename = byFilenameBuilder.build();
    }

    private SavingsIcon(String baseFilename) {
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

    public static SavingsIcon getByFilename(String filename) {
        SavingsIcon retVal = lookupByFilename.get(filename);
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

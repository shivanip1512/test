package com.cannontech.common.trend.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum TrendAxis implements DisplayableEnum {
    LEFT('L'),
    RIGHT('R');

    private Character abbreviation;
    private String baseKey = "yukon.web.modules.tools.trend.axis.";

    private final static ImmutableMap<Character, TrendAxis> lookupAbbreviation;
    static {
        Builder<Character, TrendAxis> dbBuilder = ImmutableMap.builder();
        for (TrendAxis axis : values()) {
            dbBuilder.put(axis.abbreviation, axis);
        }
        lookupAbbreviation = dbBuilder.build();
    }

    TrendAxis(Character abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Character getAbbreviation() {
        return abbreviation;
    }

    public static TrendAxis getAxis(Character abbreviation) {
        return lookupAbbreviation.get(abbreviation);
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
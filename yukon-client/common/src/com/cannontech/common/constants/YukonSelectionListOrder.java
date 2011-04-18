package com.cannontech.common.constants;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum YukonSelectionListOrder implements DisplayableEnum, DatabaseRepresentationSource {
    ALPHABETICAL("A"),
    ENTRY_ORDER("O"),
    NONE("N");

    private final static String keyPrefix = "yukon.dr.selectionListOrder.";
    private final static ImmutableMap<String, YukonSelectionListOrder> lookupByDbString;

    private final String dbString;

    static {
        Builder<String, YukonSelectionListOrder> dbStringBuilder = ImmutableMap.builder();
        for (YukonSelectionListOrder list : values()) {
            dbStringBuilder.put(list.dbString, list);
        }
        lookupByDbString = dbStringBuilder.build();
    }

    public static YukonSelectionListOrder getForDbString(String dbString) {
        return lookupByDbString.get(dbString);
    }

    YukonSelectionListOrder(String dbString) {
        this.dbString = dbString;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }

    public String getDbString() {
        return dbString;
    }
}

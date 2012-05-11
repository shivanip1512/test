package com.cannontech.common.token;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum TokenType {
    PROFILE_COLLECTION("PC"),
    CAP_CONTROL_IMPORT("CCI"),
    ;

    private final static ImmutableMap<String, TokenType> byShortName;
    static {
        Builder<String, TokenType> builder = ImmutableMap.builder();
        for (TokenType type : values()) {
            builder.put(type.shortName, type);
        }
        byShortName = builder.build();
    }

    private final String shortName;

    private TokenType(String shortName) {
        this.shortName = shortName;
    }

    public static TokenType fromShortName(String shortName) {
        return byShortName.get(shortName);
    }

    public String getShortName() {
        return shortName;
    }
}

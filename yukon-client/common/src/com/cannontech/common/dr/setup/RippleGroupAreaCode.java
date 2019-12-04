package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;


public enum RippleGroupAreaCode implements DisplayableEnum, DatabaseRepresentationSource {
    
    UNIVERSAL("000000"),
    MINNKOTA("100110"),
    BELTRAMI("100101"),
    CASS_COUNTY("100011"),
    CAVALIER_RURAL("010110"),
    CLEARWATER_POLK("010101"),
    NODAK_RURAL("010011"),
    NORTH_STAR("001110"),
    PKM_ELECTRIC("001101"),
    RED_LAKE("001011"),
    RED_RIVER_VALLEY("110100"),
    ROSEAU_ELECTRIC("101100"),
    SHEYENNE_VALLEY("011100"),
    WILD_RICE("110010"),
    NMPA("101010");
    
    private String rippleGroupAreaCodeType;
    private final static ImmutableMap<String, RippleGroupAreaCode> lookupByRippleGroupAreaCode;
    static {
        try {
            ImmutableMap.Builder<String, RippleGroupAreaCode> rippleGroupAreaCodeBilder = ImmutableMap.builder();
            for (RippleGroupAreaCode rippleGroupAreaCode : values()) {
                rippleGroupAreaCodeBilder.put(rippleGroupAreaCode.rippleGroupAreaCodeType, rippleGroupAreaCode);
            }
            lookupByRippleGroupAreaCode = rippleGroupAreaCodeBilder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private RippleGroupAreaCode(String rippleGroupAreaCodeType) {
        this.rippleGroupAreaCodeType = rippleGroupAreaCodeType;
    }

    public String getRippelGroupAreaCodeValue() {
        return rippleGroupAreaCodeType;
    }

    public static RippleGroupAreaCode getRippleAreaCodeValue(String value) {
        RippleGroupAreaCode rippleGroupAreaCodeType = lookupByRippleGroupAreaCode.get(value);
        return rippleGroupAreaCodeType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return rippleGroupAreaCodeType;
    }
}
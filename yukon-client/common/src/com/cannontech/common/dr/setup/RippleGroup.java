package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;


public enum RippleGroup implements DisplayableEnum, DatabaseRepresentationSource {
    
    TST("1000000000"),
    ONE_00("0100100100"),
    ONE_01("0110100000"),
    ONE_02("0000110100"),
    TWO_00("0010010010"),
    TWO_01("0000011010"),
    TWO_02("0010000011"),
    TWO_03("0011010000"),
    TWO_04("0110000010"),
    THREE_00("0001001001"),
    THREE_01("0001000101"),
    THREE_06("0011000001"),
    THREE_07("0100001001"),
    THREE_09("0001001100"),
    THREE_01_AND_THREE_09("0001001101"),
    FOUR_00("0100010001"),
    FOUR_01("0000110001"),
    FOUR_02("0100000011"),
    SIX_00("0010001100"),
    SIX_01("0110001000"),
    SIX_06("0010101000");
    
    private String rippleGroup;
    private final static ImmutableMap<String, RippleGroup> lookupByRippleGroup;
    static {
        try {
            ImmutableMap.Builder<String, RippleGroup> rippleGroupBilder = ImmutableMap.builder();
            for (RippleGroup rippleGroupType : values()) {
                rippleGroupBilder.put(rippleGroupType.rippleGroup, rippleGroupType);
            }
            lookupByRippleGroup = rippleGroupBilder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private RippleGroup(String rippleGroup) {
        this.rippleGroup = rippleGroup;
    }

    public String getRippelGroupValue() {
        return rippleGroup;
    }

    public static RippleGroup getRippleGroupValue(String value) {
        RippleGroup rippleGroupType = lookupByRippleGroup.get(value);
        return rippleGroupType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return rippleGroup;
    }
}
package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum VersacomAddressUsage implements DisplayableEnum {
    UTILITY('U'),
    SECTION('S'),
    CLASS('C'),
    DIVISION('D'),
    SERIAL(' ');

    private final static ImmutableSet<VersacomAddressUsage> addressUsage;
    private final static ImmutableMap<Character, VersacomAddressUsage> lookupByAbbreviation;
    static {
        addressUsage = ImmutableSet.of(
            UTILITY, 
            SECTION, 
            CLASS, 
            DIVISION, 
            SERIAL);

        ImmutableMap.Builder<Character, VersacomAddressUsage> builder = ImmutableMap.builder();
        for (VersacomAddressUsage addressUsage : values()) {
            builder.put(addressUsage.getAbbreviation(), addressUsage);
        }
        lookupByAbbreviation = builder.build();
    }

    public static ImmutableSet<VersacomAddressUsage> getAddressUsage() {
        return addressUsage;
    }
    private Character abbreviation;

    public Character getAbbreviation() {
       return abbreviation;
    }

    VersacomAddressUsage(Character abbreviation){
        this.abbreviation = abbreviation;
    }

    public static VersacomAddressUsage getDisplayValue(Character abbreviation) {
        VersacomAddressUsage addressUsage = lookupByAbbreviation.get(abbreviation);
        checkArgument(addressUsage != null, abbreviation);
        return addressUsage;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }

}

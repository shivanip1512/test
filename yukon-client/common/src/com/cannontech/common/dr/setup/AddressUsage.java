package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableSet;

public enum AddressUsage implements DisplayableEnum {
    GEO('G'),
    SUBSTATION('S'),
    FEEDER('F'),
    ZIP('Z'),
    USER('U'),
    SERIAL('T'),
    LOAD('L'),
    PROGRAM('P'),
    SPLINTER('R'),
    SERVICE('N');

    private final static ImmutableSet<AddressUsage> loadAddressUsage;
    private final static ImmutableSet<AddressUsage> geoAddressUsage;
    private Character abbreviation;
    static {
        loadAddressUsage = ImmutableSet.of(
            LOAD,
            PROGRAM,
            SPLINTER);
        
        geoAddressUsage = ImmutableSet.of(
            GEO,
            SUBSTATION,
            FEEDER,
            ZIP,
            USER,
            SERIAL);
    }
    
    AddressUsage(Character abbreviation) {
        this.abbreviation = abbreviation;
    }
    public static ImmutableSet<AddressUsage> getLoadAddressUsage() {
        return loadAddressUsage;
    }
    
    public static ImmutableSet<AddressUsage> getGeoAddressUsage() {
        return geoAddressUsage;
    }
    
    public boolean isLoadAddressUsage() {
        return loadAddressUsage.contains(this);
    }
    
    public Character getAbbreviation() {
        return abbreviation;
    }
    
    public static AddressUsage getDisplayValue(Character abbreviation) {
        for (AddressUsage addressUsage : AddressUsage.values()) {
            if (addressUsage.getAbbreviation() == abbreviation) {
                return addressUsage;
            }
        }
        return null;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }
}

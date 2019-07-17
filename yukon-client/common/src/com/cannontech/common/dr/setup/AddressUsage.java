package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum AddressUsage implements DisplayableEnum {
    SERVICE('S'),
    GEO('G'),
    SUBSTATION('B'),
    FEEDER('F'),
    ZIP('Z'),
    USER('U'),
    LOAD('L'),
    PROGRAM('P'),
    SPLINTER('R'),
    SERIAL('T');

    private final static Logger log = YukonLogManager.getLogger(AddressUsage.class);
    private final static ImmutableSet<AddressUsage> loadAddressUsage;
    private final static ImmutableSet<AddressUsage> geoAddressUsage;
    private final static ImmutableMap<Character, AddressUsage> lookupByAbbreviation;
    
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

    static {
        try {
            ImmutableMap.Builder<Character, AddressUsage> abbrBuilder = ImmutableMap.builder();
            for (AddressUsage addressUsage : values()) {
                abbrBuilder.put(addressUsage.abbreviation, addressUsage);
            }
            lookupByAbbreviation = abbrBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate abbreviation.", e);
            throw e;
        }
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
    
    public static AddressUsage getForAbbreviation(Character abbreviation) {
        AddressUsage addressUsage = lookupByAbbreviation.get(abbreviation);
        checkArgument(addressUsage != null, addressUsage);
        return addressUsage;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }
}

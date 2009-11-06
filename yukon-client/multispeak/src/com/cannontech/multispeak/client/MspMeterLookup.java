package com.cannontech.multispeak.client;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum MspMeterLookup {
    METER_NUMBER("Meter Number"),
    DEVICE_NAME("Device Name"),
    ADDRESS("Address")
    ;
    
    
    private final String rolePropertyString;
    
    private final static ImmutableMap<String, MspMeterLookup> lookupByRolePropertyString;
    
    static {
        Builder<String, MspMeterLookup> dbBuilder = ImmutableMap.builder();
        for (MspMeterLookup mspMeterLookup : values()) {
            dbBuilder.put(mspMeterLookup.rolePropertyString.toLowerCase(), mspMeterLookup);
        }
        lookupByRolePropertyString = dbBuilder.build();
    }

    
    /**
     * Looks up the the MspMeterLookup based on the string that is stored in the roleProperty value.
     * 
     * @param dbString - type name to lookup, case insensitive
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static MspMeterLookup getForRolePropertyString(String rolePropertyString) throws IllegalArgumentException {
        MspMeterLookup mspMeterLookup = lookupByRolePropertyString.get(rolePropertyString.toLowerCase()); 
        Validate.notNull(mspMeterLookup, rolePropertyString);
        return mspMeterLookup;
    }

    private MspMeterLookup(String rolePropertyString) {
        this.rolePropertyString = rolePropertyString;
    }

    public String getRolePropertyString() {
        return rolePropertyString;
    }
}

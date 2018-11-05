package com.cannontech.common.pao;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum PaoClass implements DatabaseRepresentationSource {
    
    TRANSMITTER(1000, "TRANSMITTER"),
    RTU(1001, "RTU"),
    IED(1002, "IED"),
    METER(1003, "METER"),
    CARRIER(1004, "CARRIER"),
    GROUP(1005, "GROUP"),
    VIRTUAL(1007, "VIRTUAL"),
    LOADMANAGEMENT(1008, "LOADMANAGEMENT"),
    SYSTEM(1009, "SYSTEM"),
    GRID(1010, "GRIDADVISOR"),
    ROUTE(1, "ROUTE"),
    PORT(2, "PORT"),
    CUSTOMER(3, "CUSTOMER"),
    CAPCONTROL(4, "CAPCONTROL"),
    // Schedule doesn't seem have a constant already defined anywhere.
    SCHEDULE(0, "Schedule"),
    RFMESH(1012, "RFMESH"),
    THERMOSTAT(1013, "THERMOSTAT"),
    ITRON(1014, "ITRON"), //A.K.A. SilverSpring or INSI
    ;

    // legacy class id
    private final int paoClassId;
    private final String dbString;

    private final static ImmutableMap<Integer, PaoClass> lookupById;
    private final static ImmutableMap<String, PaoClass> lookupByDbString;

    static {
        Builder<Integer, PaoClass> idBuilder = ImmutableMap.builder();
        Builder<String, PaoClass> dbBuilder = ImmutableMap.builder();
        for (PaoClass paoClass : values()) {
            idBuilder.put(paoClass.paoClassId, paoClass);
            dbBuilder.put(paoClass.dbString.toUpperCase(), paoClass);
        }
        lookupById = idBuilder.build();
        lookupByDbString = dbBuilder.build();
    }

    private PaoClass(int paoClassId, String dbString) {
        this.paoClassId = paoClassId;
        this.dbString = dbString;
    }

    /**
     * Looks up the PaoClass based on its Java constant ID.
     * @param paoClassId
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static PaoClass getForId(int paoClassId) throws IllegalArgumentException {
        PaoClass paoClass = lookupById.get(paoClassId);
        Validate.notNull(paoClass, Integer.toString(paoClassId));
        return paoClass;
    }

    /**
     * Looks up the the PaoClass based on the string that is stored in the
     * PAObject table.
     * @param dbString - type name to lookup
     * @return
     * @throws IllegalArgumentException - if no match
     * @deprecated It is suggest to use YukonJdbcConnection or YukonResultSet.getEnum instead.
     */
    @Deprecated
    public static PaoClass getForDbString(String dbString) throws IllegalArgumentException {
        PaoClass paoClass = lookupByDbString.get(dbString.toUpperCase().trim());
        Validate.notNull(paoClass, dbString);
        return paoClass;
    }
    
    public boolean isCore() {
        return CARRIER == this || IED == this || METER == this || RFMESH == this ||
                RTU == this || TRANSMITTER == this || VIRTUAL == this || GRID == this ||
                THERMOSTAT == this;
    }
    public int getPaoClassId() {
        return paoClassId;
    }

    public String getDbString() {
        return dbString;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }
}

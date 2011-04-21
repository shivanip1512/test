package com.cannontech.common.pao;

import org.apache.commons.lang.Validate;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum PaoClass implements DatabaseRepresentationSource {
    TRANSMITTER(DeviceClasses.TRANSMITTER, DeviceClasses.STRING_CLASS_TRANSMITTER),
    RTU(DeviceClasses.RTU, DeviceClasses.STRING_CLASS_RTU),
    IED(DeviceClasses.IED, DeviceClasses.STRING_CLASS_IED),
    METER(DeviceClasses.METER, DeviceClasses.STRING_CLASS_METER),
    CARRIER(DeviceClasses.CARRIER, DeviceClasses.STRING_CLASS_CARRIER),
    GROUP(DeviceClasses.GROUP, DeviceClasses.STRING_CLASS_GROUP),
    VIRTUAL(DeviceClasses.VIRTUAL, DeviceClasses.STRING_CLASS_VIRTUAL),
    LOADMANAGEMENT(DeviceClasses.LOADMANAGEMENT, DeviceClasses.STRING_CLASS_LOADMANAGER),
    SYSTEM(DeviceClasses.SYSTEM, DeviceClasses.STRING_CLASS_SYSTEM),
    GRID(DeviceClasses.GRID, DeviceClasses.STRING_CLASS_GRID),
    ROUTE(PAOGroups.CLASS_ROUTE, PAOGroups.STRING_CAT_ROUTE),
    PORT(PAOGroups.CLASS_PORT, PAOGroups.STRING_CAT_PORT),
    CUSTOMER(PAOGroups.CLASS_CUSTOMER, PAOGroups.STRING_CAT_CUSTOMER),
    CAPCONTROL(PAOGroups.CLASS_CAPCONTROL, PAOGroups.STRING_CAT_CAPCONTROL),
    // Schedule doesn't seem have a constant already defined anywhere.
    SCHEDULE(0, "SCHEDULE"),
    RFMESH(DeviceClasses.RFMESH, "RFMESH");
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
            dbBuilder.put(paoClass.dbString, paoClass);
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
    public static PaoClass getForId(int paoClassId) {
        PaoClass paoClass = lookupById.get(paoClassId);
        Validate.notNull(paoClass, Integer.toString(paoClassId));
        return paoClass;
    }

    /**
     * Looks up the the PaoClass based on the string that is stored in the
     * PAObject table.
     * @param dbString - type name to lookup, case insensitive
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static PaoClass getForDbString(String dbString) throws IllegalArgumentException {
        PaoClass paoClass = lookupByDbString.get(dbString);
        Validate.notNull(paoClass, dbString);
        return paoClass;
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

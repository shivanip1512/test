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
    SCHEDULE(0, "Schedule"),
    RFMESH(DeviceClasses.RFMESH, "RFMESH");
    ;

    // legacy class id
    private final int paoClassId;
    private final String dbString;
    private final static int INVALID = -1;
    private final static String STRING_INVALID = "(invalid)";

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
    public static PaoClass getForId(int paoClassId) throws IllegalArgumentException {
        PaoClass paoClass = lookupById.get(paoClassId);
        Validate.notNull(paoClass, Integer.toString(paoClassId));
        return paoClass;
    }

    /**
     * Returns PaoClass string of specified int ID.
     * @param classId
     * @return If classId does not match an integer ID, an IllegalArgumentException
     * will be thrown by getForId().
     */
    public static String getPaoClass(int classId) {
        PaoClass foundClass = getForId(classId);
        return foundClass.getDbString();
    }


    /**
     * Looks up the the PaoClass based on the string that is stored in the
     * PAObject table.
     * @param dbString - type name to lookup
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static PaoClass getForDbString(String dbString) throws IllegalArgumentException {
        PaoClass paoClass = lookupByDbString.get(dbString.toUpperCase().trim());
        Validate.notNull(paoClass, dbString);
        return paoClass;
    }
    
    /**
     * Return integer ID of specified PaoClass string.
     * @param stringId
     * @return If stringId does not match a PaoClass string, an IllegalArgumentException
     * will be thrown by getForDbString(). 
     */
    public static int getPaoClass(String stringId) {
        PaoClass foundClass = getForDbString(stringId);
        return foundClass.getPaoClassId();
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

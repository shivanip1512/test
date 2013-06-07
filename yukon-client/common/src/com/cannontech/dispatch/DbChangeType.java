package com.cannontech.dispatch;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum DbChangeType {

    NONE (-1),
    ADD (0),
    DELETE (1),
    UPDATE (2),
    ;
    
    // The int value of the db change message type - (See CtiDBChangedType_t in yukon.h)
    private final int typeOfChange;

    private final static ImmutableMap<Integer, DbChangeType> lookupByInt;
    
    static {
        Builder<Integer, DbChangeType> opBuilder = ImmutableMap.builder();
        for (DbChangeType dbChangeType : values()) {
            opBuilder.put(dbChangeType.getTypeOfChange(), dbChangeType);
        }
        lookupByInt = opBuilder.build();
    }
    private DbChangeType(int typeOfChange) {
        this.typeOfChange = typeOfChange;
    }
    
    public int getTypeOfChange() {
        return typeOfChange;
    }
    
    /**
     * Looks up the DBChange type by DBChangeMsg typeOfChange constant.
     * @param typeOfChange
     * @return
     * @throws IllegalArgumentException
     */
    public static DbChangeType getForType(int typeOfChange) throws IllegalArgumentException {
        DbChangeType dbChangeType = lookupByInt.get(typeOfChange);
        Validate.notNull(dbChangeType, Integer.toString(typeOfChange));
        return dbChangeType;
    }
}

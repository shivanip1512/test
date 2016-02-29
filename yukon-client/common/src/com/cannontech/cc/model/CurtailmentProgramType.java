package com.cannontech.cc.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Enumeration of the types of programs/events used in C&I Curtailment.
 */
public enum CurtailmentProgramType {
    CAPACITY_CONTINGENCY(1),
    DIRECT_CONTROL(2),
    ACCOUNTING(3),
    ECONOMIC(4);
    
    private static final ImmutableMap<Integer, CurtailmentProgramType> lookupById;
    private int typeId;
    
    static {
        Builder<Integer, CurtailmentProgramType> lookupBuilder = ImmutableMap.builder();
        for (CurtailmentProgramType type : values()) {
            lookupBuilder.put(type.getId(), type);
        }
        lookupById = lookupBuilder.build();
    }
    
    private CurtailmentProgramType(int typeId) {
        this.typeId = typeId;
    }
    
    public int getId() {
        return typeId;
    }
    
    /**
     * @return The enum type with the specified numeric ID.
     * @throws IllegalArgumentException if the specified ID does not correspond to any enum.
     */
    public static CurtailmentProgramType withId(int typeId) {
        
        CurtailmentProgramType type = lookupById.get(typeId);
        if (type == null) {
            throw new IllegalArgumentException("No ProgramTypeEnum exists with id: " + typeId);
        }
        return type;
    }
}

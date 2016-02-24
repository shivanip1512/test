package com.cannontech.cc.model;

public enum ProgramTypeEnum {
    CAPACITY_CONTINGENCY(1),
    DIRECT_CONTROL(2),
    ACCOUNTING(3),
    ECONOMIC(4);
    
    private int typeId;
    
    private ProgramTypeEnum(int typeId) {
        this.typeId = typeId;
    }
    
    public int getId() {
        return typeId;
    }
    
    public static ProgramTypeEnum withId(int typeId) {
        for (ProgramTypeEnum type : values()) {
            if (type.getId() == typeId) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ProgramTypeEnum exists with id: " + typeId);
    }
}

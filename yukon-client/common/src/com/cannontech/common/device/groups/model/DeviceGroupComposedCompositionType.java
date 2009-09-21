package com.cannontech.common.device.groups.model;

public enum DeviceGroupComposedCompositionType {

    UNION("OR", "At Least One"),
    INTERSECTION("AND", "Every One");
    
    private String sqlFragmentCombiner;
    private String description;
    
    DeviceGroupComposedCompositionType(String sqlFragmentCombiner, String description) {
        this.sqlFragmentCombiner = sqlFragmentCombiner;
        this.description = description;
    }
    
    public String getSqlFragmentCombiner() {
        return this.sqlFragmentCombiner;
    }
    
    public String getDescription() {
        return this.description;
    }
}

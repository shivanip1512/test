package com.cannontech.core.dao;

import com.cannontech.enums.RegulatorPointMapping;

public class ExtraPaoPointMapping {
    
    private RegulatorPointMapping regulatorPointMapping;
    private int pointId;

    public RegulatorPointMapping getRegulatorPointMapping() {
        return regulatorPointMapping;
    }
    
    public void setRegulatorPointMapping(RegulatorPointMapping regulatorPointMapping) {
        this.regulatorPointMapping = regulatorPointMapping;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
    
    public int getPointId() {
        return pointId;
    }
    
}
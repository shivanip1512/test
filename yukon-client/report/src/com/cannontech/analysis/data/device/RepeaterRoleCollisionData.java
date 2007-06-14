package com.cannontech.analysis.data.device;

public class RepeaterRoleCollisionData {

    private String ccuName = null;
    private String routeName = null;
    private Integer fixedBit = null;
    private Integer variableBit = null;
    
    public RepeaterRoleCollisionData() {
        super();
    }
    
    public RepeaterRoleCollisionData(String ccuName_, String routeName_, Integer fixedBit_, Integer variableBit_ ) {
        ccuName = ccuName_;
        routeName = routeName_;
        fixedBit = fixedBit_;
        variableBit = variableBit_;
    }
    
    public String getCCUName() {
        return ccuName;
    }
    
    public String getRouteName() {
        return routeName;
    }
    
    public Integer getFixedBit() {
        return fixedBit;
    }
    
    public Integer getVariableBit() {
        return variableBit;
    }
    
}

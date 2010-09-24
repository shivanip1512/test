package com.cannontech.core.roleproperties;



public class RolePropertyValue {
    private YukonRoleProperty yukonRoleProperty;
    private Object value;
    
    public RolePropertyValue(YukonRoleProperty yukonRoleProperty) {
        this.yukonRoleProperty = yukonRoleProperty;
    }
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public YukonRoleProperty getYukonRoleProperty() {
        return yukonRoleProperty;
    }

}

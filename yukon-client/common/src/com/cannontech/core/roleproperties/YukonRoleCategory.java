package com.cannontech.core.roleproperties;


public enum YukonRoleCategory {
    Application, CapControl, CiCustomer, Consumer, LoadControl, Notifications, Operator, System;

    public boolean isSystem() {
        return this.equals(System);
    }
}

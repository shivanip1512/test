package com.cannontech.common.device.groups.editor.dao;

public enum SystemGroupEnum {
    METERS(1,"Meters Group"), 
    BILLING(2,"Billing Group"), 
    COLLECTION(3,"Collection Group"), 
    ALTERNATE(4,"Alternate Group"), 
    CUSTOM_GROUP_ONE(5,"Custom Group One"), 
    CUSTOM_GROUP_TWO(6,"Custom Group Two"), 
    CUSTOM_GROUP_THREE(7,"Custom Group Three"), 
    FLAGS(8,"Flags Group"), 
    INVENTORY(9,"Inventory Group"), 
    DISCONNECTSTATUS(10,"Disconnect Status Group"), 
    USAGEMONITORING(11,"Usage Monitoring Group");

    private int id;
    private String name;

    SystemGroupEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }

}
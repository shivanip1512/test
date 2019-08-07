package com.cannontech.web.dr.setup;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class GearInfo {
    private String id;
    private String name;
    private GearControlMethod controlMethod; 

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GearControlMethod getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(GearControlMethod controlMethod) {
        this.controlMethod = controlMethod;
    }

    
}

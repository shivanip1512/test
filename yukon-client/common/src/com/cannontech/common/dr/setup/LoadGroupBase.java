package com.cannontech.common.dr.setup;

import com.cannontech.common.pao.PaoType;

public class LoadGroupBase {

    private Integer id;
    private String name;
    private PaoType type;
    private float kWCapacity;
    private boolean disableGroup;
    private boolean disableControl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public float getkWCapacity() {
        return kWCapacity;
    }

    public void setkWCapacity(float kWCapacity) {
        this.kWCapacity = kWCapacity;
    }

    public boolean isDisableGroup() {
        return disableGroup;
    }

    public void setDisableGroup(boolean disableGroup) {
        this.disableGroup = disableGroup;
    }

    public boolean isDisableControl() {
        return disableControl;
    }

    public void setDisableControl(boolean disableControl) {
        this.disableControl = disableControl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

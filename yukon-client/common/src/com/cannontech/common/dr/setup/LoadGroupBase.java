package com.cannontech.common.dr.setup;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteLoadGroupBase;

public class LoadGroupBase {

    private Integer id;
    private String name;
    private PaoType type;
    private double kWCapacity;
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

    public double getkWCapacity() {
        return kWCapacity;
    }

    public void setkWCapacity(double kWCapacity) {
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
    
    public static LoadGroupBase of(CompleteLoadGroupBase loadGroup) {
        LoadGroupBase group = new LoadGroupBase();
        group.setName(loadGroup.getPaoName());
        group.setId(loadGroup.getPaObjectId());
        group.setType(loadGroup.getPaoType());
        group.setkWCapacity(loadGroup.getkWCapacity());
        group.setDisableControl(loadGroup.isControlInhibit());
        group.setDisableGroup(loadGroup.isDisabled());
        
        return group;
    }
    
    public CompleteLoadGroupBase asCompletePao() {
        CompleteLoadGroupBase completeLoadGroup = new CompleteLoadGroupBase();
        if (id != null) {
            PaoIdentifier paoId = new PaoIdentifier(id, type);
            completeLoadGroup.setPaoIdentifier(paoId);
        }
        completeLoadGroup.setPaoName(name);
        completeLoadGroup.setDisabled(disableGroup);
        completeLoadGroup.setControlInhibit(disableControl);
        completeLoadGroup.setkWCapacity(kWCapacity);

        return completeLoadGroup;
    }
}

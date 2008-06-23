package com.cannontech.stars.xml.serialize;

public class DualStageAC {
    private Tonnage tonnage;
    private Tonnage stageTwoTonnage;
    private ACType ACType;

    public DualStageAC() {
        
    }

    public ACType getACType() {
        return this.ACType;
    } 

    public Tonnage getTonnage() {
        return this.tonnage;
    } 

    public void setACType(ACType ACType) {
        this.ACType = ACType;
    } 

    public void setTonnage(Tonnage tonnage) {
        this.tonnage = tonnage;
    }

    public Tonnage getStageTwoTonnage() {
        return stageTwoTonnage;
    }

    public void setStageTwoTonnage(Tonnage stagetwo_tonnage) {
        stageTwoTonnage = stagetwo_tonnage;
    }

}

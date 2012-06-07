package com.cannontech.stars.xml.serialize;

public class DualStageAC {
    private Tonnage tonnage;
    private Tonnage stageTwoTonnage;
    private ACType acType;

    public DualStageAC() {
        this.tonnage = new Tonnage();
        this.stageTwoTonnage = new Tonnage();
        this.acType = new ACType();
    }

    public ACType getAcType() {
        return this.acType;
    } 

    public Tonnage getTonnage() {
        return this.tonnage;
    } 

    public void setAcType(ACType acType) {
        this.acType = acType;
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

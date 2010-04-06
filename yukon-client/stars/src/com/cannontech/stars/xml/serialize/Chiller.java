package com.cannontech.stars.xml.serialize;

public class Chiller {
    private Tonnage tonnage;
    private ACType acType;

    public Chiller() {
        this.tonnage = new Tonnage();
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

}

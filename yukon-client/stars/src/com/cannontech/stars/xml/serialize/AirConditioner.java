package com.cannontech.stars.xml.serialize;

public class AirConditioner {
    private Tonnage tonnage;
    private ACType ACType;

    public AirConditioner() {

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

}

package com.cannontech.importer.progress.capcontrol;


public class ProgCCCapbank {

    String opCenterAbv;
    String geoWorkArea;
    String equipmentID;//capbank name
    String address; //driveDirections ( capbank additional table )
    String lat;
    String lon; //latitude longitude  ( capbankadditional table )
    String bankSize; // banksize  ( capbank table )
    String bankType;//capbankConfig  ( capbankadditional table )
    String ctlType;// switch manufacturer ( capbank table )
    
    private int id;
    
    public ProgCCCapbank(){
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankSize() {
        return bankSize;
    }

    public void setBankSize(String bankSize) {
        this.bankSize = bankSize;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getCtlType() {
        return ctlType;
    }

    public void setCtlType(String ctlType) {
        this.ctlType = ctlType;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getGeoWorkArea() {
        return geoWorkArea;
    }

    public void setGeoWorkArea(String geoWorkArea) {
        this.geoWorkArea = geoWorkArea;
    }

    public String getOpCenterAbv() {
        return opCenterAbv;
    }

    public void setOpCenterAbv(String opCenterAbv) {
        this.opCenterAbv = opCenterAbv;
    }
    public String getString(){
        return new String(opCenterAbv + " " + geoWorkArea + " " + equipmentID + " " + address+ " " + lat + " " + lon + " " + bankSize + " " + bankType + " " + ctlType);
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}

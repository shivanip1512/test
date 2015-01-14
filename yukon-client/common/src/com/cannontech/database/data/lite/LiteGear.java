package com.cannontech.database.data.lite;


public class LiteGear extends LiteBase {
    private String gearName;
    private String gearType;
    private int ownerID;
    private int gearNumber;

    public LiteGear() {
        super();
        setLiteType(LiteTypes.GEAR);
    }

    public LiteGear(int gearID) {
        super();
        setLiteID(gearID);
        setLiteType(LiteTypes.GEAR);
    }

    public LiteGear(int gearID, String conName_) {
        this(gearID);
        setGearName(conName_);
    }

    public LiteGear(int gearID, String conName_, String type) {
        this(gearID);
        setGearName(conName_);
        setGearType(type);
    }

    public int getGearID() {
        return getLiteID();
    }

    public String getGearName() {
        return gearName;
    }

    public String getGearType() {
        return gearType;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public int getGearNumber() {
        return gearNumber;
    }

    public void setGearID(int conID) {
        setLiteID(conID);
    }

    public void setGearName(String name) {
        gearName = name;
    }

    public void setGearType(String type) {
        gearType = type;
    }

    public void setOwnerID(int id) {
        ownerID = id;
    }

    public void setGearNumber(int newNum) {
        gearNumber = newNum;
    }

    @Override
    public String toString() {
        return getGearName();
    }
}
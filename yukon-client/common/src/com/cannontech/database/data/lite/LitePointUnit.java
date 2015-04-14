package com.cannontech.database.data.lite;

public class LitePointUnit extends LiteBase {
    private int uomID = 0;
    private int decimalPlaces = 0;

    public LitePointUnit(int pntID) {
        super();
        setLiteID(pntID);
        setLiteType(LiteTypes.POINT_UNIT);
    }

    public LitePointUnit(int pntID, int uomid, int decimals) {
        super();
        setLiteID(pntID);
        uomID = uomid;
        decimalPlaces = decimals;
        setLiteType(LiteTypes.POINT_UNIT);
    }

    public int getUomID() {
        return uomID;
    }

    public int getPointID() {
        return getLiteID();
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setUomID(int newUomID) {
        uomID = newUomID;
    }

    public void setPointID(int newValue) {
        setLiteID(newValue);
    }

    public void setDecimalPlaces(int newDecimals) {
        this.decimalPlaces = newDecimals;
    }
}
package com.cannontech.database.data.lite;

import com.cannontech.common.util.NativeIntVector;

public class LiteEnergyCompany extends LiteBase {
    private String name;
    private int primaryContactID;
    private int userID;

    private NativeIntVector ciCustumerIDs = null;
    
    public LiteEnergyCompany() {
        this(0, null, 0, 0);
    }
    
    public LiteEnergyCompany(int id) {
        this(id, null, 0, 0);
    }
    
    public LiteEnergyCompany(int id, String name, int primaryContactID, int userID) {
        setLiteType(LiteTypes.ENERGY_COMPANY);
        setLiteID(id);
        this.name = name;
        this.primaryContactID = primaryContactID;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public int getEnergyCompanyID() {
        return getLiteID();
    }

    public int getPrimaryContactID() {
        return primaryContactID;
    }

    public int getUserID() {
        return userID;
    }

    public NativeIntVector getCiCustumerIDs() {
        if( ciCustumerIDs == null ) {
            ciCustumerIDs = new NativeIntVector(16);
        }
        return ciCustumerIDs;
    }

    @Override
    public String toString() {
        return getName();
    }
}

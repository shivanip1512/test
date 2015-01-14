package com.cannontech.database.data.lite;

import com.cannontech.database.db.device.lm.LMProgramDirectGear;

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

    /**
     * retrieve method comment.
     */
    public void retrieve(String databaseAlias) {

        com.cannontech.database.SqlStatement s = 
                new com.cannontech.database.SqlStatement(
                   "SELECT gearID, gearName, controlMethod, deviceID, gearNumber "  + 
                      "FROM " + LMProgramDirectGear.TABLE_NAME +
                      " where gearID = " + getGearID(),
                   com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try {
            s.execute();

            if (s.getRowCount() <= 0)
                throw new IllegalStateException("Unable to find gear with ID = " + getLiteID());

            setGearID(new Integer(s.getRow(0)[0].toString()).intValue());
            setGearName(s.getRow(0)[1].toString());
            setGearType(s.getRow(0)[2].toString());
            setOwnerID(new Integer(s.getRow(0)[3].toString()).intValue());
            setGearNumber(new Integer(s.getRow(0)[4].toString()).intValue());
        } catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
        }

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
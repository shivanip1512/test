package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMGroupDigiSep extends LMGroup {
    private static final long serialVersionUID = 8698587802311192815L;
    private com.cannontech.database.db.device.lm.LMGroupSep lmGroupSep = null;

    /**
     * LMGroupEmetcon constructor comment.
     */
    public LMGroupDigiSep() {
        super();

        getYukonPAObject().setType(com.cannontech.database.data.pao.PAOGroups.STRING_DIGI_SEP_GROUP[0]);
    }

    /**
     * add method comment.
     */
    public void add() throws java.sql.SQLException {
        super.add();
        getLmGroupSep().add();
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/20/2001 2:19:57 PM)
     */
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLmGroupSepDefaults().add();
    }

    /**
     * delete method comment.
     */
    public void delete() throws java.sql.SQLException {
        getLmGroupSep().delete();
        super.delete();
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/20/2001 3:02:23 PM)
     * @exception java.sql.SQLException The exception description.
     */
    public void deletePartial() throws java.sql.SQLException {

        super.deletePartial();
    }

    /**
     * This method was created in VisualAge.
     * @return com.cannontech.database.db.device.lm.LMGroupEmetcon
     */
    public com.cannontech.database.db.device.lm.LMGroupSep getLmGroupSep() {
        if (lmGroupSep == null) {
            lmGroupSep = new com.cannontech.database.db.device.lm.LMGroupSep();
        }

        return lmGroupSep;
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/20/2001 4:14:06 PM)
     * @return com.cannontech.database.data.device.lm.LMGroupEmetcon
     */
    public com.cannontech.database.db.device.lm.LMGroupSep getLmGroupSepDefaults() {

        getLmGroupSep().addDeviceClass(SepDeviceClass.HVAC_COMPRESSOR_FURNACE);
        getLmGroupSep().setUtilityEnrollmentGroup(new Integer(1));

        return getLmGroupSep();
    }

    /**
     * retrieve method comment.
     */
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLmGroupSep().retrieve();
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/4/00 3:32:03 PM)
     * @param conn java.sql.Connection
     */
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getLmGroupSep().setDbConnection(conn);
    }

    /**
     * This method was created in VisualAge.
     * @param deviceID java.lang.Integer
     */
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroupSep().setDeviceId(deviceID);
    }

    /**
     * This method was created in VisualAge.
     * @param newValue com.cannontech.database.db.device.lm.LMGroupEmetcon
     */
    public void setLmGroupSep(com.cannontech.database.db.device.lm.LMGroupSep newValue) {
        this.lmGroupSep = newValue;
    }

    /**
     * update method comment.
     */
    public void update() throws java.sql.SQLException {
        super.update();
        getLmGroupSep().update();
    }
}

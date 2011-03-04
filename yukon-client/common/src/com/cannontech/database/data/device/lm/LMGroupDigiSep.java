package com.cannontech.database.data.device.lm;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.lm.LMGroupSep;

public class LMGroupDigiSep extends LMGroup {
    private static final long serialVersionUID = 8698587802311192815L;
    private LMGroupSep lmGroupSep = null;

    public LMGroupDigiSep() {
        super();

        getYukonPAObject().setType(PAOGroups.STRING_DIGI_SEP_GROUP[0]);
    }

    public void add() throws SQLException {
        super.add();
        getLmGroupSep().add();
    }

    public void addPartial() throws SQLException {
        super.addPartial();
        getLmGroupSepDefaults().add();
    }

    public void delete() throws SQLException {
        getLmGroupSep().delete();
        super.delete();
    }

    public void deletePartial() throws SQLException {

        super.deletePartial();
    }

    public LMGroupSep getLmGroupSep() {
        if (lmGroupSep == null) {
            lmGroupSep = new LMGroupSep();
        }

        return lmGroupSep;
    }

    public LMGroupSep getLmGroupSepDefaults() {

        getLmGroupSep().addDeviceClass(SepDeviceClass.HVAC_COMPRESSOR_FURNACE);
        getLmGroupSep().setUtilityEnrollmentGroup(new Integer(1));

        return getLmGroupSep();
    }

    public void retrieve() throws SQLException {
        super.retrieve();
        getLmGroupSep().retrieve();
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);

        getLmGroupSep().setDbConnection(conn);
    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroupSep().setDeviceId(deviceID);
    }

    public void setLmGroupSep(LMGroupSep newValue) {
        this.lmGroupSep = newValue;
    }

    public void update() throws SQLException {
        super.update();
        getLmGroupSep().update();
    }
}

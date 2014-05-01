package com.cannontech.database.data.device.lm;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.lm.LMGroupSep;

public class LMGroupDigiSep extends LMGroup {
    private static final long serialVersionUID = 8698587802311192815L;
    private LMGroupSep lmGroupSep = null;

    public LMGroupDigiSep() {
        super();

        getYukonPAObject().setType(PaoType.LM_GROUP_DIGI_SEP.getDbString());
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getLmGroupSep().add();
    }

    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
        getLmGroupSepDefaults().add();
    }

    @Override
    public void delete() throws SQLException {
        getLmGroupSep().delete();
        super.delete();
    }

    @Override
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
        getLmGroupSep().setUtilityEnrollmentGroup(1);

        return getLmGroupSep();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getLmGroupSep().retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);

        getLmGroupSep().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroupSep().setDeviceId(deviceID);
    }

    public void setLmGroupSep(LMGroupSep newValue) {
        this.lmGroupSep = newValue;
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getLmGroupSep().update();
    }
}

package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupEmetcon extends LMGroup implements IGroupRoute {
    private com.cannontech.database.db.device.lm.LMGroupEmetcon lmGroupEmetcon = null;

    public LMGroupEmetcon() {
        super(PaoType.LM_GROUP_EMETCON);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLmGroupEmetcon().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLmGroupEmetconDefaults().add();
    }

    @Override
    public void setRouteID(Integer rtID_) {
        getLmGroupEmetcon().setRouteID(rtID_);
    }

    @Override
    public Integer getRouteID() {
        return getLmGroupEmetcon().getRouteID();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLmGroupEmetcon().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {
        super.deletePartial();
    }

    public com.cannontech.database.db.device.lm.LMGroupEmetcon getLmGroupEmetcon() {
        if (lmGroupEmetcon == null) {
            lmGroupEmetcon = new com.cannontech.database.db.device.lm.LMGroupEmetcon();
        }

        return lmGroupEmetcon;
    }

    public com.cannontech.database.db.device.lm.LMGroupEmetcon getLmGroupEmetconDefaults() {

        getLmGroupEmetcon().setAddressUsage(new Character('G'));
        getLmGroupEmetcon().setGoldAddress(new Integer(0));
        getLmGroupEmetcon().setSilverAddress(new Integer(0));
        getLmGroupEmetcon().setRelayUsage(new Character('A'));

        return getLmGroupEmetcon();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLmGroupEmetcon().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getLmGroupEmetcon().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroupEmetcon().setDeviceID(deviceID);
    }

    public void setLmGroupEmetcon(com.cannontech.database.db.device.lm.LMGroupEmetcon newValue) {
        this.lmGroupEmetcon = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLmGroupEmetcon().update();
    }
}

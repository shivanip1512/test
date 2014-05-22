package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupGolay extends LMGroup implements IGroupRoute {

    private com.cannontech.database.db.device.lm.LMGroupSASimple lmGroupSASimple = null;

    public LMGroupGolay() {
        super(PaoType.LM_GROUP_GOLAY);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLMGroupSASimple().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLMGroupSASimple().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLMGroupSASimple().delete();
        super.delete();
    }

    @Override
    public void setRouteID(Integer rtID_) {
        getLMGroupSASimple().setRouteID(rtID_);
    }

    @Override
    public Integer getRouteID() {
        return getLMGroupSASimple().getRouteID();
    }

    public com.cannontech.database.db.device.lm.LMGroupSASimple getLMGroupSASimple() {
        if (lmGroupSASimple == null)
            lmGroupSASimple = new com.cannontech.database.db.device.lm.LMGroupSASimple();

        return lmGroupSASimple;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLMGroupSASimple().retrieve();

    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMGroupSASimple().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLMGroupSASimple().setGroupID(deviceID);
    }

    public void setLMGroupSASimple(com.cannontech.database.db.device.lm.LMGroupSASimple newValue) {
        this.lmGroupSASimple = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLMGroupSASimple().update();
    }
}

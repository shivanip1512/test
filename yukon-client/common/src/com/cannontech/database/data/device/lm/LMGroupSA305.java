package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupSA305 extends LMGroup implements IGroupRoute {

    private com.cannontech.database.db.device.lm.LMGroupSA305 lmGroupSA305 = null;

    public LMGroupSA305() {
        super(PaoType.LM_GROUP_SA305);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLMGroupSA305().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLMGroupSA305().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLMGroupSA305().delete();
        super.delete();
    }

    @Override
    public void setRouteID(Integer rtID_) {
        getLMGroupSA305().setRouteID(rtID_);
    }

    @Override
    public Integer getRouteID() {
        return getLMGroupSA305().getRouteID();
    }

    public com.cannontech.database.db.device.lm.LMGroupSA305 getLMGroupSA305() {
        if (lmGroupSA305 == null)
            lmGroupSA305 = new com.cannontech.database.db.device.lm.LMGroupSA305();

        return lmGroupSA305;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLMGroupSA305().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMGroupSA305().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLMGroupSA305().setGroupID(deviceID);
    }

    public void setLMGroupSA305(com.cannontech.database.db.device.lm.LMGroupSA305 newValue) {
        this.lmGroupSA305 = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLMGroupSA305().update();
    }
}

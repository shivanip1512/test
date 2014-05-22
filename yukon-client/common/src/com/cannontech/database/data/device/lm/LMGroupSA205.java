package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupSA205 extends LMGroup implements IGroupRoute {

    private com.cannontech.database.db.device.lm.LMGroupSA205105 lmGroupSA205105 = null;

    public LMGroupSA205() {
        super(PaoType.LM_GROUP_SA205);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLMGroupSA205105().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLMGroupSA205105().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLMGroupSA205105().delete();
        super.delete();
    }

    @Override
    public void setRouteID(Integer rtID_) {
        getLMGroupSA205105().setRouteID(rtID_);
    }

    @Override
    public Integer getRouteID() {
        return getLMGroupSA205105().getRouteID();
    }

    public com.cannontech.database.db.device.lm.LMGroupSA205105 getLMGroupSA205105() {
        if (lmGroupSA205105 == null)
            lmGroupSA205105 = new com.cannontech.database.db.device.lm.LMGroupSA205105();
        return lmGroupSA205105;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLMGroupSA205105().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMGroupSA205105().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLMGroupSA205105().setGroupID(deviceID);
    }

    public void setLMGroupSA205105(com.cannontech.database.db.device.lm.LMGroupSA205105 newValue) {
        this.lmGroupSA205105 = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLMGroupSA205105().update();
    }
}

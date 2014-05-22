package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupMCT extends LMGroup implements IGroupRoute {
    private com.cannontech.database.db.device.lm.LMGroupMCT lmGroupMCT = null;

    public LMGroupMCT() {
        super(PaoType.LM_GROUP_MCT);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLmGroupMCT().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLmGroupMCT().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLmGroupMCT().delete();
        super.delete();
    }

    @Override
    public void setRouteID(Integer rtID_) {
        getLmGroupMCT().setRouteID(rtID_);
    }

    @Override
    public Integer getRouteID() {
        return getLmGroupMCT().getRouteID();
    }

    public com.cannontech.database.db.device.lm.LMGroupMCT getLmGroupMCT() {
        if (lmGroupMCT == null)
            lmGroupMCT = new com.cannontech.database.db.device.lm.LMGroupMCT();

        return lmGroupMCT;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLmGroupMCT().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getLmGroupMCT().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroupMCT().setDeviceID(deviceID);
    }

    public void setLmGroupMCT(
            com.cannontech.database.db.device.lm.LMGroupMCT newValue) {
        this.lmGroupMCT = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLmGroupMCT().update();
    }
}

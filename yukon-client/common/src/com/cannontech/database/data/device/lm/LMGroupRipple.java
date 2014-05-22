package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupRipple extends LMGroup implements IGroupRoute {
    private com.cannontech.database.db.device.lm.LMGroupRipple lmGroupRipple = null;

    public LMGroupRipple() {
        super(PaoType.LM_GROUP_RIPPLE);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLmGroupRipple().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLmGroupRippleDefaults().add();
    }

    @Override
    public void setRouteID(Integer rtID_) {
        getLmGroupRipple().setRouteID(rtID_);
    }

    @Override
    public Integer getRouteID() {
        return getLmGroupRipple().getRouteID();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLmGroupRipple().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {

        super.deletePartial();
    }

    public com.cannontech.database.db.device.lm.LMGroupRipple getLmGroupRipple() {
        if (lmGroupRipple == null) {
            lmGroupRipple = new com.cannontech.database.db.device.lm.LMGroupRipple();
        }
        return lmGroupRipple;
    }

    public com.cannontech.database.db.device.lm.LMGroupRipple getLmGroupRippleDefaults() {

        getLmGroupRipple().setShedTime(new Integer(450));
        getLmGroupRipple().setControl("0000000000 0000000000 0000000000 00000000000 00000000000");
        getLmGroupRipple().setRestore("0000000000 0000000000 0000000000 00000000000 00000000000");

        return getLmGroupRipple();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLmGroupRipple().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getLmGroupRipple().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroupRipple().setDeviceID(deviceID);
    }

    public void setLmGroupRipple(
            com.cannontech.database.db.device.lm.LMGroupRipple newValue) {
        this.lmGroupRipple = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLmGroupRipple().update();
    }
}

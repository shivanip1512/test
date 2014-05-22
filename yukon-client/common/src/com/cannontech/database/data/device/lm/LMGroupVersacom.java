package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupVersacom extends LMGroup implements IGroupRoute {
    private com.cannontech.database.db.device.lm.LMGroupVersacom lmGroupVersacom = null;

    public LMGroupVersacom() {
        super(PaoType.LM_GROUP_VERSACOM);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLmGroupVersacom().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLmGroupVersacom().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLmGroupVersacom().delete();
        super.delete();
    }

    @Override
    public void setRouteID(Integer rtID_) {
        getLmGroupVersacom().setRouteID(rtID_);
    }

    @Override
    public Integer getRouteID() {
        return getLmGroupVersacom().getRouteID();
    }

    public com.cannontech.database.db.device.lm.LMGroupVersacom getLmGroupVersacom() {
        if (lmGroupVersacom == null) {
            lmGroupVersacom = new com.cannontech.database.db.device.lm.LMGroupVersacom();
        }
        return lmGroupVersacom;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLmGroupVersacom().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getLmGroupVersacom().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroupVersacom().setDeviceID(deviceID);
    }

    public void setLmGroupVersacom(
            com.cannontech.database.db.device.lm.LMGroupVersacom newValue) {
        this.lmGroupVersacom = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLmGroupVersacom().update();
    }
}

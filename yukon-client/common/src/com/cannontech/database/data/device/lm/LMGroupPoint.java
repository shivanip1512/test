package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupPoint extends LMGroup {
    private com.cannontech.database.db.device.lm.LMGroupPoint lmGroupPoint = null;

    public LMGroupPoint() {
        super(PaoType.LM_GROUP_POINT);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLMGroupPoint().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLMGroupPoint().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {

        getLMGroupPoint().delete();
        super.delete();
    }

    public com.cannontech.database.db.device.lm.LMGroupPoint getLMGroupPoint() {
        if (lmGroupPoint == null)
            lmGroupPoint = new com.cannontech.database.db.device.lm.LMGroupPoint();

        return lmGroupPoint;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLMGroupPoint().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMGroupPoint().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLMGroupPoint().setDeviceID(deviceID);
    }

    public void setLmGroupVersacom(com.cannontech.database.db.device.lm.LMGroupPoint newValue) {
        lmGroupPoint = newValue;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLMGroupPoint().update();
    }
}

package com.cannontech.database.data.device.lm;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;


public class LMGroupEcoBee extends LMGroup {
    private static final long serialVersionUID = 8698587802311192815L;

    public LMGroupEcoBee() {
        super();
        getYukonPAObject().setType(PaoType.LM_GROUP_ECOBEE.getDbString());
    }

    @Override
    public void add() throws SQLException {
        super.add();
    }

    @Override
    public void addPartial() throws SQLException {
        super.addPartial();
    }

    @Override
    public void delete() throws SQLException {
        super.delete();
    }

    @Override
    public void deletePartial() throws SQLException {
        super.deletePartial();
    }


    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }

    @Override
    public void update() throws SQLException {
        super.update();
    }
}

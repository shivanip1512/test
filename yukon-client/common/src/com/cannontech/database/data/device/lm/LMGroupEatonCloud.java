package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupEatonCloud extends LMGroup {
    private final static long serialVersionUID = 1L;
    private com.cannontech.database.db.device.lm.LMGroupEatonCloud mapping = new com.cannontech.database.db.device.lm.LMGroupEatonCloud();

    public LMGroupEatonCloud() {
        super(PaoType.LM_GROUP_EATON_CLOUD);
    }

    public void setRelayUsage(String relayUsage) {
        mapping.setRelayUsage(relayUsage);
    }

    public String getRelayUsage() {
        return mapping.getRelayUsage();
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        mapping.add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        mapping.delete();
        super.delete();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        mapping.add();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        mapping.setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceId) {
        super.setDeviceID(deviceId);
        mapping.setYukonGroupId(deviceId);
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        mapping.retrieve();
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        mapping.update();
    }
}

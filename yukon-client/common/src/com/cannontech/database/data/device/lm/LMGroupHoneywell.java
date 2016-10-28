package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

public class LMGroupHoneywell extends LMGroup {
    private com.cannontech.database.db.device.lm.LMGroupHoneywell lmGroupHoneywell = null;
    private final static long serialVersionUID = 1L;

    public LMGroupHoneywell() {
        super(PaoType.LM_GROUP_HONEYWELL);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLMGroupHoneywell().add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        getLMGroupHoneywell().delete();
        super.delete();
    }

    public com.cannontech.database.db.device.lm.LMGroupHoneywell getLMGroupHoneywell() {
        if (lmGroupHoneywell == null)
            lmGroupHoneywell = new com.cannontech.database.db.device.lm.LMGroupHoneywell();

        return lmGroupHoneywell;
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLMGroupHoneywell().add();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMGroupHoneywell().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLMGroupHoneywell().setDeviceID(deviceID);
    }

    public void setLMGroupHoneywell(com.cannontech.database.db.device.lm.LMGroupHoneywell newValue) {
        this.lmGroupHoneywell = newValue;
    }

}

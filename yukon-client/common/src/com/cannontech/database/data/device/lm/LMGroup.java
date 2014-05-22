package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;

public abstract class LMGroup extends DeviceBase {
    private com.cannontech.database.db.device.lm.LMGroup lmGroup = null;

    public LMGroup(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getLmGroup().add();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getLmGroup().add();

    }

    @Override
    public void delete() throws java.sql.SQLException {
        delete("DynamicLMControlHistory", "PAObjectID", getDevice().getDeviceID());

        delete("DynamicLMGroup", "DeviceID", getDevice().getDeviceID());

        delete(com.cannontech.database.db.macro.GenericMacro.TABLE_NAME, "childID", getDevice().getDeviceID());

        LMProgramDirectGroup.deleteGroupsFromProgram(getDevice().getDeviceID(), getDbConnection());

        getLmGroup().delete();
        super.delete();
    }

    @Override
    public void deletePartial() throws java.sql.SQLException {

        super.deletePartial();

    }

    public com.cannontech.database.db.device.lm.LMGroup getLmGroup() {
        if (lmGroup == null) {
            lmGroup = new com.cannontech.database.db.device.lm.LMGroup();
        }

        return lmGroup;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLmGroup().retrieve();
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        getLmGroup().setDbConnection(conn);
    }

    @Override
    public void setPAObjectID(Integer paoID) {
        super.setPAObjectID(paoID);
        setDeviceID(paoID);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getLmGroup().setDeviceID(deviceID);
    }

    public void setLmGroup(com.cannontech.database.db.device.lm.LMGroup newLmGroup) {
        lmGroup = newLmGroup;
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getLmGroup().update();
    }
}

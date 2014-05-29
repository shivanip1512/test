package com.cannontech.database.data.device.lm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.device.lm.DeviceListItem;
import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;

public abstract class LMProgramDirectBase extends LMProgramBase {

    public LMProgramDirectBase(PaoType paoType) {
        super(paoType);
    }

    private com.cannontech.database.db.device.lm.LMProgramDirect directProgram = null;
    private Vector<LMProgramDirectGear> lmProgramDirectGearVector = null;
    private Vector<LMDirectNotificationGroupList> lmProgramDirectNotifyGroupVector = null;

    @Override
    public void add() throws SQLException {
        super.add();
        getDirectProgram().add();

        // add the gears
        for (LMProgramDirectGear lmProgramDirectGear : getLmProgramDirectGearVector()) {
            lmProgramDirectGear.add();
        }

        // add all the Groups for this DirectProgram
        for (DeviceListItem deviceListItem : getLmProgramStorageVector()) {
            if (deviceListItem instanceof LMProgramDirectGroup) {
                LMProgramDirectGroup lmProgramDirectGroup = (LMProgramDirectGroup) deviceListItem;
                lmProgramDirectGroup.setDeviceID(getPAObjectID());
                lmProgramDirectGroup.setDbConnection(getDbConnection());
                lmProgramDirectGroup.add();
            }
        }

        // add the customers
        for (LMDirectNotificationGroupList lmDirectNotificationGroupList : getLmProgramDirectNotifyGroupVector()) {
            lmDirectNotificationGroupList.add();
        }
    }

    @Override
    public void delete() throws SQLException {
        // delete all of our direct customers first
        delete(LMDirectNotificationGroupList.TABLE_NAME, LMDirectNotificationGroupList.CONSTRAINT_COLUMNS[0],
            getPAObjectID());

        LMProgramDirectGear.deleteAllDirectGears(getPAObjectID(), getDbConnection());

        LMProgramDirectGroup.deleteAllDirectGroups(getPAObjectID(), getDbConnection());

        deleteFromDynamicTables();

        getDirectProgram().delete();
        super.delete();
    }

    private void deleteFromDynamicTables() throws SQLException {
        delete("DynamicLMProgramDirect", "deviceID", getPAObjectID());
        // delete("DynamicLMGroup", "LMProgramID", getPAObjectID() );
    }

    public com.cannontech.database.db.device.lm.LMProgramDirect getDirectProgram() {
        if (directProgram == null) {
            directProgram = new com.cannontech.database.db.device.lm.LMProgramDirect();
        }

        return directProgram;
    }

    public Vector<LMProgramDirectGear> getLmProgramDirectGearVector() {
        if (lmProgramDirectGearVector == null) {
            lmProgramDirectGearVector = new Vector<LMProgramDirectGear>(10);
        }

        return lmProgramDirectGearVector;
    }

    public Vector<LMDirectNotificationGroupList> getLmProgramDirectNotifyGroupVector() {
        if (lmProgramDirectNotifyGroupVector == null) {
            lmProgramDirectNotifyGroupVector = new Vector<LMDirectNotificationGroupList>();
        }

        return lmProgramDirectNotifyGroupVector;
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDirectProgram().retrieve();

        // retrieve all the gears for this Program
        Vector<LMProgramDirectGear> gears = LMProgramDirectGear.getAllDirectGears(getPAObjectID(), getDbConnection());
        getLmProgramDirectGearVector().clear();
        for (LMProgramDirectGear gear : gears) {
            getLmProgramDirectGearVector().add(gear);
        }

        // retrieve all the Groups for this Program
        LMProgramDirectGroup[] groups = LMProgramDirectGroup.getAllDirectGroups(getPAObjectID());
        getLmProgramStorageVector().clear();
        for (int i = 0; i < groups.length; i++) {
            getLmProgramStorageVector().add(groups[i]);
        }

        LMDirectNotificationGroupList[] customers =
            com.cannontech.database.db.device.lm.LMProgramDirect.getAllNotificationGroupsList(getPAObjectID(),
                getDbConnection());
        getLmProgramDirectNotifyGroupVector().clear();
        for (LMDirectNotificationGroupList customer : customers) {
            getLmProgramDirectNotifyGroupVector().add(customer);
        }
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDirectProgram().setDbConnection(conn);

        for (LMProgramDirectGear lmProgramDirectGear : getLmProgramDirectGearVector()) {
            lmProgramDirectGear.setDbConnection(conn);
        }

        for (LMDirectNotificationGroupList lmDirectNotificationGroupList : getLmProgramDirectNotifyGroupVector()) {
            lmDirectNotificationGroupList.setDbConnection(conn);
        }
    }

    public void setDirectProgram(com.cannontech.database.db.device.lm.LMProgramDirect directProgram) {
        this.directProgram = directProgram;
    }

    public void setLmProgramDirectGearVector(Vector<LMProgramDirectGear> lmProgramDirectGearVector) {
        this.lmProgramDirectGearVector = lmProgramDirectGearVector;
    }

    @Override
    public void setPAObjectID(Integer paoID) {
        super.setPAObjectID(paoID);
        getDirectProgram().setDeviceID(paoID);

        for (LMProgramDirectGear lmProgramDirectGear : getLmProgramDirectGearVector()) {
            lmProgramDirectGear.setDeviceID(paoID);
        }

        for (LMDirectNotificationGroupList lmDirectNotificationGroupList : getLmProgramDirectNotifyGroupVector()) {
            lmDirectNotificationGroupList.setDeviceID(paoID);
        }
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getDirectProgram().update();
        Vector<LMProgramDirectGear> gearVector = new Vector<LMProgramDirectGear>();

        // grab all the previous gear entries for this program
        Vector<LMProgramDirectGear> oldGears =
            LMProgramDirectGear.getAllDirectGears(getPAObjectID(), getDbConnection());

        // unleash the power of the NestedDBPersistent
        gearVector =
            NestedDBPersistentComparators.NestedDBPersistentCompare(oldGears, getLmProgramDirectGearVector(),
                NestedDBPersistentComparators.lmDirectGearComparator);

        // throw the gears into the Db
        for (LMProgramDirectGear lmProgramDirectGear : gearVector) {
            lmProgramDirectGear.setDeviceID(getPAObjectID());
            lmProgramDirectGear.executeNestedOp();

        }

        // delete all the current associated groups from the DB
        LMProgramDirectGroup.deleteAllDirectGroups(getPAObjectID(), getDbConnection());

        // add the groups
        for (DeviceListItem deviceListItem : getLmProgramStorageVector()) {
            if (deviceListItem instanceof LMProgramDirectGroup) {
                LMProgramDirectGroup lmProgramDirectGroup = (LMProgramDirectGroup) deviceListItem;
                lmProgramDirectGroup.setDeviceID(getPAObjectID());
                lmProgramDirectGroup.setDbConnection(getDbConnection());
                lmProgramDirectGroup.add();
            }
        }

        // delete all of our energy exchange customers first
        delete(LMDirectNotificationGroupList.TABLE_NAME, LMDirectNotificationGroupList.CONSTRAINT_COLUMNS[0],
            getPAObjectID());

        for (LMDirectNotificationGroupList lmDirectNotificationGroupList : getLmProgramDirectNotifyGroupVector()) {
            lmDirectNotificationGroupList.setDeviceID(getDirectProgram().getDeviceID());
            lmDirectNotificationGroupList.add();
        }
    }
}

package com.cannontech.database.data.starshardware;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareBase extends InventoryBase {

    private com.cannontech.database.db.starshardware.LMHardwareBase _LMHardwareBase = null;

    public LMHardwareBase() {
        super();
    }

    public void setInventoryID(Integer newID) {
        super.setInventoryID(newID);
        getLMHardwareBase().setInventoryID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMHardwareBase().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from LMHardwareActivity
        com.cannontech.database.db.starsevent.LMHardwareActivity.deleteAllHardwareActivities(
            getLMHardwareBase().getInventoryID(), getDbConnection() );

        // delete from LMHardwareConfiguration
        com.cannontech.database.db.starshardware.LMHardwareConfiguration.deleteAllLMHardwareConfiguration(
            getLMHardwareBase().getInventoryID(), getDbConnection() );

        getLMHardwareBase().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
        super.add();
        getLMHardwareBase().add();
    }

    public void update() throws java.sql.SQLException {
        super.update();
        getLMHardwareBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLMHardwareBase().retrieve();
    }

    public com.cannontech.database.db.starshardware.LMHardwareBase getLMHardwareBase() {
        if (_LMHardwareBase == null)
            _LMHardwareBase = new com.cannontech.database.db.starshardware.LMHardwareBase();
        return _LMHardwareBase;
    }

    public void setLMHardwareBase(com.cannontech.database.db.starshardware.LMHardwareBase newLMHardwareBase) {
        _LMHardwareBase = newLMHardwareBase;
    }
}
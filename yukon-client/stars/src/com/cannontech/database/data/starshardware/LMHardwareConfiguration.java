package com.cannontech.database.data.starshardware;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareConfiguration extends DBPersistent {

    private com.cannontech.database.db.starshardware.LMHardwareConfiguration _LMHardwareConfiguration = null;

    public LMHardwareConfiguration() {
        super();
    }

    public void setInventoryID(Integer newID) {
        getLMHardwareConfiguration().setInventoryID(newID);
    }

    public void setApplianceID(Integer newID) {
        getLMHardwareConfiguration().setApplianceID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getLMHardwareConfiguration().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getLMHardwareConfiguration().delete();
    }

    public void add() throws java.sql.SQLException {
        getLMHardwareConfiguration().add();
    }

    public void update() throws java.sql.SQLException {
        getLMHardwareConfiguration().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getLMHardwareConfiguration().retrieve();
    }

    public com.cannontech.database.db.starshardware.LMHardwareConfiguration getLMHardwareConfiguration() {
        if (_LMHardwareConfiguration == null)
            _LMHardwareConfiguration = new com.cannontech.database.db.starshardware.LMHardwareConfiguration();
        return _LMHardwareConfiguration;
    }

    public void set_LMHardwareConfiguration(com.cannontech.database.db.starshardware.LMHardwareConfiguration newLMHardwareConfiguration) {
        _LMHardwareConfiguration = newLMHardwareConfiguration;
    }
}
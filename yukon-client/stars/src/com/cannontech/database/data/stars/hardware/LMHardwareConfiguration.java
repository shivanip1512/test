package com.cannontech.database.data.stars.hardware;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareConfiguration extends DBPersistent {

    private com.cannontech.database.db.stars.hardware.LMHardwareConfiguration lmHardwareConfiguration = null;

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
	/**
	 * Returns the lmHardwareConfiguration.
	 * @return com.cannontech.database.db.stars.hardware.LMHardwareConfiguration
	 */
	public com.cannontech.database.db.stars.hardware.LMHardwareConfiguration getLMHardwareConfiguration() {
		return lmHardwareConfiguration;
	}

	/**
	 * Sets the lmHardwareConfiguration.
	 * @param lmHardwareConfiguration The lmHardwareConfiguration to set
	 */
	public void setLMHardwareConfiguration(
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration lmHardwareConfiguration) {
		this.lmHardwareConfiguration = lmHardwareConfiguration;
	}

}
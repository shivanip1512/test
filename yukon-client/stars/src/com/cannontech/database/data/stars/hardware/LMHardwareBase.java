package com.cannontech.database.data.stars.hardware;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareBase extends InventoryBase {

    private com.cannontech.database.db.stars.hardware.LMHardwareBase lmHardwareBase = null;
    private com.cannontech.database.db.stars.CustomerListEntry lmHardwareType = null;

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
        getLMHardwareType().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from LMHardwareEvent
        com.cannontech.database.db.stars.event.LMHardwareEvent.deleteAllLMHardwareEvents(
            getLMHardwareBase().getInventoryID(), getDbConnection() );

        // delete from LMHardwareConfiguration
        com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteAllLMHardwareConfiguration(
            getLMHardwareBase().getInventoryID(), getDbConnection() );

        getLMHardwareBase().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
        super.add();
        
        getLMHardwareBase().setInventoryID( getInventoryBase().getInventoryID() );
        getLMHardwareBase().add();
    }

    public void update() throws java.sql.SQLException {
        super.update();
        getLMHardwareBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getLMHardwareBase().retrieve();
        
        getLMHardwareType().setEntryID( getLMHardwareBase().getLMHardwareTypeID() );
        getLMHardwareType().retrieve();
    }

    public com.cannontech.database.db.stars.hardware.LMHardwareBase getLMHardwareBase() {
        if (lmHardwareBase == null)
            lmHardwareBase = new com.cannontech.database.db.stars.hardware.LMHardwareBase();
        return lmHardwareBase;
    }

    public void setLMHardwareBase(com.cannontech.database.db.stars.hardware.LMHardwareBase lmHardwareBase) {
        this.lmHardwareBase = lmHardwareBase;
    }
	/**
	 * Returns the lmHardwareType.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getLMHardwareType() {
		if (lmHardwareType == null)
			lmHardwareType = new com.cannontech.database.db.stars.CustomerListEntry();
		return lmHardwareType;
	}

	/**
	 * Sets the lmHardwareType.
	 * @param lmHardwareType The lmHardwareType to set
	 */
	public void setLMHardwareType(
		com.cannontech.database.db.stars.CustomerListEntry lmHardwareType) {
		this.lmHardwareType = lmHardwareType;
	}

}
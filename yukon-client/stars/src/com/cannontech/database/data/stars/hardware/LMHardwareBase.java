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
    
	/**
	 * When deleteHwInfo = false, this method is used to remove a hardware from a customer account.
	 * This method should not be used alone with deleteHwInfo = true, instead the delete() method
	 * should be used, which will invoke this method with deleteHwInfo set to true. 
	 */
	public void deleteLMHardwareBase(boolean deleteHwInfo) throws java.sql.SQLException {
		// delete from LMHardwareConfiguration
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteAllLMHardwareConfiguration(
				getInventoryBase().getInventoryID(), getDbConnection() );
    	
		// delete from LMThermostatSeason
		LMThermostatSeason[] thermSeasons = LMThermostatSeason.getAllLMThermostatSeasons(
				getInventoryBase().getInventoryID().intValue(), getDbConnection() );
		if (thermSeasons != null) {
			for (int i = 0; i < thermSeasons.length; i++) {
				thermSeasons[i].setDbConnection( getDbConnection() );
				thermSeasons[i].delete();
			}
		}
    	
		// delete from LMThermostatManualEvent
		com.cannontech.database.data.stars.event.LMThermostatManualEvent.deleteAllLMThermostatManualEvents(
				getInventoryBase().getInventoryID().intValue(), getDbConnection() );
		
		if (deleteHwInfo)
			getLMHardwareBase().delete();
	}

	public void delete() throws java.sql.SQLException {
		deleteLMHardwareBase( true );
		super.deleteInventoryBase();
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
	}

	public com.cannontech.database.db.stars.hardware.LMHardwareBase getLMHardwareBase() {
		if (lmHardwareBase == null)
			lmHardwareBase = new com.cannontech.database.db.stars.hardware.LMHardwareBase();
		return lmHardwareBase;
	}

	public void setLMHardwareBase(com.cannontech.database.db.stars.hardware.LMHardwareBase lmHardwareBase) {
		this.lmHardwareBase = lmHardwareBase;
	}

}
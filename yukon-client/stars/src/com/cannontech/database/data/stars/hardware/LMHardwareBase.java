package com.cannontech.database.data.stars.hardware;

import com.cannontech.common.util.CtiUtilities;

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
	 * Clear entries in other tables related with LMhardwareBase
	 */
	public void clearLMHardware() throws java.sql.SQLException {
		// delete from LMHardwareConfiguration
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteAllLMHardwareConfiguration(
				getInventoryBase().getInventoryID() );
		
		// delete from LMThermostatSchedule if it's "unnamed"
		com.cannontech.database.db.stars.hardware.LMThermostatSchedule scheduleDB =
				com.cannontech.database.db.stars.hardware.LMThermostatSchedule.getThermostatSchedule(
					getInventoryBase().getInventoryID().intValue() );
		
		if (scheduleDB != null && scheduleDB.getScheduleName().equals( CtiUtilities.STRING_NONE )) {
			LMThermostatSchedule schedule = new LMThermostatSchedule();
			schedule.setScheduleID( scheduleDB.getScheduleID() );
			schedule.setDbConnection( getDbConnection() );
			schedule.delete();
		}
		
		// delete from LMThermostatManualEvent
		com.cannontech.database.data.stars.event.LMThermostatManualEvent.deleteAllLMThermostatManualEvents(
				getInventoryBase().getInventoryID().intValue() );
	}

	public void delete() throws java.sql.SQLException {
		clearLMHardware();
		getLMHardwareBase().delete();
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
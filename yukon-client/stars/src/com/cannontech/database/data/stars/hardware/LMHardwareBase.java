package com.cannontech.database.data.stars.hardware;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;

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
	 * Clear hardware information related with customer account
	 */
	public static void clearLMHardware(int inventoryID) {
		// delete from LMHardwareConfiguration
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteAllLMHardwareConfiguration( inventoryID );
		
		// delete from LMThermostatSchedule
		ThermostatScheduleDao thermostatScheduleDao = 
    		YukonSpringHook.getBean("thermostatScheduleDao", ThermostatScheduleDao.class);
		thermostatScheduleDao.deleteScheduleForInventory(inventoryID);
		
		// delete from LMThermostatManualEvent
		com.cannontech.database.data.stars.event.LMThermostatManualEvent.deleteAllLMThermostatManualEvents( inventoryID );
        
        // remove any entries in the LMHardwareToMeterMapping table if this was a switch assigned to a meter
		String stmt = "DELETE FROM LMHardwareToMeterMapping WHERE LMHardwareInventoryID = " + inventoryID;
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        
        try {
            jdbcOps.execute(stmt);
        }
        catch (Exception e) {}
	}
	
	public void deleteLMHardwareBase() throws java.sql.SQLException {
		clearLMHardware( getInventoryBase().getInventoryID().intValue() );
		getLMHardwareBase().delete();
		
		// delete from LMConfigurationBase
		if (getLMHardwareBase().getConfigurationID().intValue() > 0) {
			com.cannontech.database.data.stars.hardware.LMConfigurationBase config =
					new com.cannontech.database.data.stars.hardware.LMConfigurationBase();
			config.setConfigurationID( getLMHardwareBase().getConfigurationID() );
			config.setDbConnection( getDbConnection() );
			config.delete();
		}
	}

	public void delete() throws java.sql.SQLException {
		delete( "LMHardwareToMeterMapping", "LMHardwareInventoryID", getInventoryBase().getInventoryID() );
		deleteLMHardwareBase();
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
package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMThermostatManualOption extends DBPersistent {
	
	private Integer inventoryID = null;
	private Integer previousTemperature = new Integer(0);
	private String holdTemperature = new String("N");
	private Integer operationStateID = new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	private Integer fanOperationID = new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
	
	public static final String[] SETTER_COLUMNS = {
		"PreviousTemperature", "HoldTemperature", "OperationStateID", "FanOperationID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };
	
	public static final String TABLE_NAME = "LMThermostatManualOption";
	
	public LMThermostatManualOption() {
		super();
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getInventoryID(), getPreviousTemperature(), getHoldTemperature(),
			getOperationStateID(), getFanOperationID()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getInventoryID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getInventoryID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setPreviousTemperature( (Integer) results[0] );
			setHoldTemperature( (String) results[1] );
			setOperationStateID( (Integer) results[2] );
			setFanOperationID( (Integer) results[3] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getPreviousTemperature(), getHoldTemperature(), getOperationStateID(), getFanOperationID()
		};
		Object[] constraintValues = { getInventoryID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static LMThermostatManualOption getLMThermostatManualOption(Integer inventoryID) {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE InventoryID = " + inventoryID;
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() > 0) {
				Object[] row = stmt.getRow(0);
				LMThermostatManualOption option = new LMThermostatManualOption();
				
				option.setInventoryID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				option.setPreviousTemperature( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				option.setHoldTemperature( (String) row[2] );
				option.setOperationStateID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				option.setFanOperationID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				
				return option;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Returns the fanOperationID.
	 * @return Integer
	 */
	public Integer getFanOperationID() {
		return fanOperationID;
	}

	/**
	 * Returns the holdTemperature.
	 * @return String
	 */
	public String getHoldTemperature() {
		return holdTemperature;
	}

	/**
	 * Returns the inventoryID.
	 * @return Integer
	 */
	public Integer getInventoryID() {
		return inventoryID;
	}

	/**
	 * Returns the operationStateID.
	 * @return Integer
	 */
	public Integer getOperationStateID() {
		return operationStateID;
	}

	/**
	 * Returns the previousTemperature.
	 * @return Integer
	 */
	public Integer getPreviousTemperature() {
		return previousTemperature;
	}

	/**
	 * Sets the fanOperationID.
	 * @param fanOperationID The fanOperationID to set
	 */
	public void setFanOperationID(Integer fanOperationID) {
		this.fanOperationID = fanOperationID;
	}

	/**
	 * Sets the holdTemperature.
	 * @param holdTemperature The holdTemperature to set
	 */
	public void setHoldTemperature(String holdTemperature) {
		this.holdTemperature = holdTemperature;
	}

	/**
	 * Sets the inventoryID.
	 * @param inventoryID The inventoryID to set
	 */
	public void setInventoryID(Integer inventoryID) {
		this.inventoryID = inventoryID;
	}

	/**
	 * Sets the operationStateID.
	 * @param operationStateID The operationStateID to set
	 */
	public void setOperationStateID(Integer operationStateID) {
		this.operationStateID = operationStateID;
	}

	/**
	 * Sets the previousTemperature.
	 * @param previousTemperature The previousTemperature to set
	 */
	public void setPreviousTemperature(Integer previousTemperature) {
		this.previousTemperature = previousTemperature;
	}

}

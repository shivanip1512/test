package com.cannontech.database.db.stars.event;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMThermostatManualEvent extends DBPersistent {
	
	private Integer eventID = null;
	private Integer inventoryID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer previousTemperature = new Integer(0);
	private String holdTemperature = new String("N");
	private Integer operationStateID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer fanOperationID = new Integer(CtiUtilities.NONE_ZERO_ID);
	
	public static final String[] SETTER_COLUMNS = {
		"InventoryID", "PreviousTemperature", "HoldTemperature", "OperationStateID", "FanOperationID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "EventID" };
	
	public static final String TABLE_NAME = "LMThermostatManualEvent";

    public static final String GET_NEXT_EVENT_ID_SQL =
        "SELECT MAX(EventID) FROM " + TABLE_NAME;

	
	public LMThermostatManualEvent() {
		super();
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getEventID(), getInventoryID(), getPreviousTemperature(),
			getHoldTemperature(), getOperationStateID(), getFanOperationID()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getEventID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getEventID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setInventoryID( (Integer) results[0] );
			setPreviousTemperature( (Integer) results[1] );
			setHoldTemperature( (String) results[2] );
			setOperationStateID( (Integer) results[3] );
			setFanOperationID( (Integer) results[4] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getInventoryID(), getPreviousTemperature(), getHoldTemperature(),
			getOperationStateID(), getFanOperationID()
		};
		Object[] constraintValues = { getEventID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

    public static Integer[] getAllLMThermostatManualEventIDs(int inventoryID) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE InventoryID=" + inventoryID + " ORDER BY EventID";
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );

        try {
        	stmt.execute();
            
			Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				eventIDs[i] = new Integer( ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue() );
			
			return eventIDs;
        }
        catch( Exception e ) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }
    
    public static LMThermostatManualEvent getLastLMThermostatManualEvent(Integer inventoryID) {
        String sql = "SELECT EventID, InventoryID, PreviousTemperature, HoldTemperature, OperationStateID, FanOperationID "
        		   + "FROM " + TABLE_NAME + " WHERE EventID = ("
        		   + "SELECT MAX(EventID) FROM " + TABLE_NAME + " WHERE InventoryID=" + inventoryID + ")";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );

        try {
        	stmt.execute();
        	
            if (stmt.getRowCount() > 0) {
            	Object[] row = stmt.getRow(0);
            	
                LMThermostatManualEvent event = new LMThermostatManualEvent();
                event.setEventID( new Integer(((java.math.BigDecimal)row[0]).intValue()) );
                event.setInventoryID( new Integer(((java.math.BigDecimal)row[1]).intValue()) );
                event.setPreviousTemperature( new Integer(((java.math.BigDecimal)row[2]).intValue()) );
                event.setHoldTemperature( (String) row[3] );
                event.setOperationStateID( new Integer(((java.math.BigDecimal)row[4]).intValue()) );
                event.setFanOperationID( new Integer(((java.math.BigDecimal)row[5]).intValue()) );
                
                return event;
            }
        }
        catch( Exception e ) {
            CTILogger.error( e.getMessage(), e );
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

	/**
	 * Returns the eventID.
	 * @return Integer
	 */
	public Integer getEventID() {
		return eventID;
	}

	/**
	 * Sets the eventID.
	 * @param eventID The eventID to set
	 */
	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}

}

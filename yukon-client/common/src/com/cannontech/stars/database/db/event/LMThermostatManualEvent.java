package com.cannontech.stars.database.db.event;

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
	private String holdTemperature = new String("N");
	private Integer operationStateID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer fanOperationID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer previousCoolTemperature = new Integer(0);
    private Integer previousHeatTemperature = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"InventoryID", "HoldTemperature", "OperationStateID", "FanOperationID", "PreviousCoolTemperature", "PreviousHeatTemperature"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "EventID" };
	
	public static final String TABLE_NAME = "LMThermostatManualEvent";
	
	public LMThermostatManualEvent() {
		super();
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	@Override
    public void add() throws SQLException {
		Object[] addValues = {
			getEventID(), getInventoryID(), getHoldTemperature(), getOperationStateID(), getFanOperationID(),
			getPreviousCoolTemperature(), getPreviousHeatTemperature()
		};
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	@Override
    public void delete() throws SQLException {
		Object[] constraintValues = { getEventID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	@Override
    public void retrieve() throws SQLException {
		Object[] constraintValues = { getEventID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setInventoryID( (Integer) results[0] );
			setHoldTemperature( (String) results[1] );
			setOperationStateID( (Integer) results[2] );
			setFanOperationID( (Integer) results[3] );
			setPreviousCoolTemperature( (Integer) results[4] );
            setPreviousHeatTemperature( (Integer) results[5] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	@Override
    public void update() throws SQLException {
		Object[] setValues = {
			getInventoryID(), getHoldTemperature(), getOperationStateID(), getFanOperationID(),
			getPreviousCoolTemperature(),  getPreviousHeatTemperature()
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
        String sql = "SELECT EventID, InventoryID,  HoldTemperature, OperationStateID, FanOperationID, PreviousCoolTemperature, PreviousHeatTemperature"
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
                event.setHoldTemperature( (String) row[2] );
                event.setOperationStateID( new Integer(((java.math.BigDecimal)row[3]).intValue()) );
                event.setFanOperationID( new Integer(((java.math.BigDecimal)row[4]).intValue()) );
                event.setPreviousCoolTemperature( new Integer(((java.math.BigDecimal)row[5]).intValue()) );
                event.setPreviousHeatTemperature( new Integer(((java.math.BigDecimal)row[6]).intValue()) );
                
                return event;
            }
        }
        catch( Exception e ) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }

	public Integer getFanOperationID() {
		return fanOperationID;
	}

	public String getHoldTemperature() {
		return holdTemperature;
	}

	public Integer getInventoryID() {
		return inventoryID;
	}

	public Integer getOperationStateID() {
		return operationStateID;
	}

	public void setFanOperationID(Integer fanOperationID) {
		this.fanOperationID = fanOperationID;
	}

	public void setHoldTemperature(String holdTemperature) {
		this.holdTemperature = holdTemperature;
	}

	public void setInventoryID(Integer inventoryID) {
		this.inventoryID = inventoryID;
	}

	public void setOperationStateID(Integer operationStateID) {
		this.operationStateID = operationStateID;
	}

	public Integer getEventID() {
		return eventID;
	}

	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}

    public Integer getPreviousCoolTemperature() {
        return previousCoolTemperature;
    }
    public void setPreviousCoolTemperature(Integer previousCoolTemperature) {
        this.previousCoolTemperature = previousCoolTemperature;
    }

    public Integer getPreviousHeatTemperature() {
        return previousHeatTemperature;
    }
    public void setPreviousHeatTemperature(Integer previousHeatTemperature) {
        this.previousHeatTemperature = previousHeatTemperature;
    }
}
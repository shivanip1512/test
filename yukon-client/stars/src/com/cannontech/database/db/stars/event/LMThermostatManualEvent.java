package com.cannontech.database.db.stars.event;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
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
	private Integer inventoryID = new Integer(CtiUtilities.NONE_ID);
	private Integer previousTemperature = new Integer(0);
	private String holdTemperature = new String("N");
	private Integer operationStateID = new Integer(CtiUtilities.NONE_ID);
	private Integer fanOperationID = new Integer(CtiUtilities.NONE_ID);
	
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

    public static Integer[] getAllLMThermostatManualEventIDs(int inventoryID, java.sql.Connection conn) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE InventoryID = ? ORDER BY EventID";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList eventList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, inventoryID );
                rset = pstmt.executeQuery();

                while (rset.next())
                    eventList.add( new Integer(rset.getInt("EventID")) );
            }
        }
        catch( java.sql.SQLException e )
        {
                e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        Integer[] eventIDs = new Integer[ eventList.size() ];
        eventList.toArray( eventIDs );
        return eventIDs;
    }

    public static void deleteAllLMThermostatManualEvents(int inventoryID, java.sql.Connection conn) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE InventoryID = ?";

        java.sql.PreparedStatement pstmt = null;
        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, inventoryID );
                pstmt.execute();
            }
        }
        catch( java.sql.SQLException e )
        {
                e.printStackTrace();
        }
        finally
        {
            try
            {
                if( pstmt != null ) pstmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }
    }
    
    public static LMThermostatManualEvent getLastLMThermostatManualEvent(Integer inventoryID, java.sql.Connection conn) {
        String sql = "SELECT EventID, InventoryID, PreviousTemperature, HoldTemperature, OperationStateID, FanOperationID "
        		   + "FROM " + TABLE_NAME + " WHERE EventID = ("
        		   + "SELECT MAX(EventID) FROM " + TABLE_NAME + " WHERE InventoryID = ?)";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, inventoryID.intValue() );
                rset = pstmt.executeQuery();

                if (rset.next()) {
                    LMThermostatManualEvent event = new LMThermostatManualEvent();
                    event.setEventID( new Integer(rset.getInt(0)) );
                    event.setInventoryID( new Integer(rset.getInt(1)) );
                    event.setPreviousTemperature( new Integer(rset.getInt(2)) );
                    event.setHoldTemperature( (String) rset.getString(3) );
                    event.setOperationStateID( new Integer(rset.getInt(4)) );
                    event.setFanOperationID( new Integer(rset.getInt(5)) );
                    
                    return event;
                }
            }
        }
        catch( java.sql.SQLException e )
        {
                e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
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

package com.cannontech.database.db.stars.event;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareEvent extends DBPersistent {

    private Integer eventID = null;
    private Integer inventoryID = new Integer( CtiUtilities.NONE_ID );

    public static final String[] SETTER_COLUMNS = { "InventoryID" };

    public static final String[] CONSTRAINT_COLUMNS = { "EventID" };

    public static final String TABLE_NAME = "LMHardwareEvent";

    public static final String GET_NEXT_EVENT_ID_SQL =
        "SELECT MAX(EventID) FROM " + TABLE_NAME;

    public LMHardwareEvent() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getEventID(), getInventoryID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = { getInventoryID() };

        Object[] constraintValues = { getEventID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setInventoryID( (Integer) results[0] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public static Integer[] getAllLMHardwareEventIDs(Integer inventoryID) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE InventoryID =" + inventoryID + " ORDER BY EventID";
        
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;
        
        try {
        	conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
        	stmt = conn.createStatement();
        	rset = stmt.executeQuery( sql );
        	
			java.util.ArrayList eventList = new java.util.ArrayList();
            while (rset.next())
                eventList.add( new Integer(rset.getInt("EventID")) );
			
			Integer[] eventIDs = new Integer[ eventList.size() ];
			eventList.toArray( eventIDs );
			return eventIDs;
        }
        catch( java.sql.SQLException e ) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch( java.sql.SQLException e2 ) {}
        }
        
        return null;
    }

    public static void deleteAllLMHardwareEvents(Integer inventoryID) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE InventoryID=" + inventoryID;
        
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        
        try {
        	conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
        	stmt = conn.createStatement();
        	stmt.execute( sql );
        }
        catch( java.sql.SQLException e ) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch( java.sql.SQLException e2 ) {}
        }
    }
    
    public static LMHardwareEvent getLastLMHardwareEvent(Integer inventoryID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE EventID = (" +
        		"SELECT MAX(EventID) FROM " + TABLE_NAME + " WHERE InventoryID=" + inventoryID + ")";
        
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rset = null;
        
        try {
        	conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
        	stmt = conn.createStatement();
            rset = stmt.executeQuery( sql );
            
            if (rset.next()) {
                LMHardwareEvent event = new LMHardwareEvent();
                event.setEventID( new Integer(rset.getInt("EventID")) );
                event.setInventoryID( new Integer(rset.getInt("InventoryID")) );
                return event;
            }
        }
        catch( java.sql.SQLException e ) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch( java.sql.SQLException e2 ) {}
        }
        
        return null;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer newEventID) {
        eventID = newEventID;
    }

    public Integer getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(Integer newInventoryID) {
        inventoryID = newInventoryID;
    }
}
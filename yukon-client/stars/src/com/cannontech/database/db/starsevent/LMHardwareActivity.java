package com.cannontech.database.db.starsevent;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareActivity extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer eventID = null;
    private Integer inventoryID = new Integer( com.cannontech.database.db.starsappliance.ApplianceBase.NONE_INT );
    private Integer actionID = new Integer( com.cannontech.database.db.starscustomer.CustomerAction.NONE_INT );
    private java.util.Date eventDateTime = null;
    private String notes = null;

    public static final String[] SETTER_COLUMNS = {
        "InventoryID", "EventDateTime", "Notes", "ActionID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "EventID" };

    public static final String TABLE_NAME = "LMHardwareActivity";

    public static final String GET_NEXT_EVENT_ID_SQL =
        "SELECT MAX(EventID) FROM " + TABLE_NAME;

    public LMHardwareActivity() {
        super();
    }
    
    public static LMHardwareActivity getLastHardwareActivity(Integer inventoryID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE EventID = ("
        		   + "SELECT MAX(EventID) FROM " + TABLE_NAME + " WHERE InventoryID = ?)";

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
                pstmt.setInt( 1, inventoryID.intValue() );
                rset = pstmt.executeQuery();

                if (rset.next()) {
                    LMHardwareActivity event = new LMHardwareActivity();

                    event.setEventID( new Integer(rset.getInt("EventID")) );
                    event.setInventoryID( new Integer(rset.getInt("InventoryID")) );
                    event.setEventDateTime( new java.util.Date(rset.getTimestamp("EventDateTime").getTime()) );
                    event.setNotes( rset.getString("Notes") );
                    event.setActionID( new Integer(rset.getInt("ActionID")) );

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
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

		return null;
    }

    public static LMHardwareActivity[] getAllHardwareActivities(Integer inventoryID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE InventoryID = ? "
        		   + "ORDER BY EventID DESC";

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
                pstmt.setInt( 1, inventoryID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    LMHardwareActivity event = new LMHardwareActivity();

                    event.setEventID( new Integer(rset.getInt("EventID")) );
                    event.setInventoryID( new Integer(rset.getInt("InventoryID")) );
                    event.setEventDateTime( new java.util.Date(rset.getTimestamp("EventDateTime").getTime()) );
                    event.setNotes( rset.getString("Notes") );
                    event.setActionID( new Integer(rset.getInt("ActionID")) );

                    eventList.add(event);
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
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        LMHardwareActivity[] events = new LMHardwareActivity[ eventList.size() ];
        eventList.toArray( events );
        return events;
    }

    public static void deleteAllHardwareActivities(Integer inventoryID, java.sql.Connection conn) {
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
                pstmt.setInt( 1, inventoryID.intValue() );
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

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getEventID() == null)
    		setEventID( getNextEventID() );
    		
        Object[] addValues = {
            getEventID(), getInventoryID(), getEventDateTime(),
            getNotes(), getActionID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getInventoryID(), getEventDateTime(), getNotes(), getActionID()
        };

        Object[] constraintValues = { getEventID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setInventoryID( (Integer) results[0] );
            setEventDateTime( new java.util.Date(((java.sql.Timestamp) results[1]).getTime()) );
            setNotes( (String) results[2] );
            setActionID( (Integer) results[3] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextEventID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextEventID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_EVENT_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextEventID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextEventID );
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

    public Integer getActionID() {
        return actionID;
    }

    public void setActionID(Integer newActionID) {
        actionID = newActionID;
    }

    public java.util.Date getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(java.util.Date newEventDateTime) {
        eventDateTime = newEventDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String newNotes) {
        notes = newNotes;
    }
}
package com.cannontech.database.db.stars.event;

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
    private Integer inventoryID = new Integer( com.cannontech.database.db.stars.appliance.ApplianceBase.NONE_INT );

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

    public static LMHardwareEvent[] getAllLMHardwareEvents(Integer inventoryID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE InventoryID = ?";

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
                    LMHardwareEvent event = new LMHardwareEvent();

                    event.setEventID( new Integer(rset.getInt("EventID")) );
                    event.setInventoryID( new Integer(rset.getInt("InventoryID")) );

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

        LMHardwareEvent[] events = new LMHardwareEvent[ eventList.size() ];
        eventList.toArray( events );
        return events;
    }

    public static void deleteAllLMHardwareEvents(Integer inventoryID, java.sql.Connection conn) {
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
    
    public static LMHardwareEvent getLastLMHardwareEvent(Integer inventoryID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE EventID = ("
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
                    LMHardwareEvent event = new LMHardwareEvent();
                    event.setEventID( new Integer(rset.getInt("EventID")) );
                    event.setInventoryID( new Integer(rset.getInt("InventoryID")) );
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
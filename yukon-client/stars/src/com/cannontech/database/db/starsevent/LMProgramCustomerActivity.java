package com.cannontech.database.db.starsevent;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMProgramCustomerActivity extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer eventID = null;
    private Integer accountID = new Integer( com.cannontech.database.db.starscustomer.CustomerAccount.NONE_INT );
    private Integer actionID = new Integer( com.cannontech.database.db.starscustomer.CustomerAction.NONE_INT );
    private Integer _LMProgramID = new Integer( NONE_INT );
    private java.util.Date eventDateTime = null;
    private String notes = null;

    public static final String[] SETTER_COLUMNS = {
        "AccountID", "ActionID", "LMProgramID", "EventDateTime", "Notes"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "EventID" };

    public static final String TABLE_NAME = "LMProgramCustomerActivity";

    public static final String GET_NEXT_EVENT_ID_SQL =
        "SELECT MAX(EventID) FROM " + TABLE_NAME;

    public LMProgramCustomerActivity() {
        super();
    }

    public static LMProgramCustomerActivity[] getAllCustomerActivities(Integer accountID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = ?";

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
                pstmt.setInt( 1, accountID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    LMProgramCustomerActivity event = new LMProgramCustomerActivity();

                    event.setEventID( new Integer(rset.getInt("EventID")) );
                    event.setAccountID( new Integer(rset.getInt("AccountID")) );
                    event.setActionID( new Integer(rset.getInt("ActionID")) );
                    event.setLMProgramID( new Integer(rset.getInt("LMProgramID")) );
                    event.setEventDateTime( new java.util.Date(rset.getTimestamp("EventDateTime").getTime()) );
                    event.setNotes( rset.getString("Notes") );

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

        LMProgramCustomerActivity[] events = new LMProgramCustomerActivity[ eventList.size() ];
        eventList.toArray( events );
        return events;
    }

    public static void deleteAllCustomerActivities(Integer accountID, java.sql.Connection conn) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE AccountID = ?";

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
                pstmt.setInt( 1, accountID.intValue() );
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
        Object[] addValues = {
            getEventID(), getAccountID(), getActionID(), getLMProgramID(),
            getEventDateTime(), getNotes()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getActionID(), getLMProgramID(),
            getEventDateTime(), getNotes()
        };

        Object[] constraintValues = { getEventID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAccountID( (Integer) results[0] );
            setActionID( (Integer) results[1] );
            setLMProgramID( (Integer) results[2] );
            setEventDateTime( new java.util.Date(((java.sql.Timestamp) results[3]).getTime()) );
            setNotes( (String) results[4] );
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

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer newAccountID) {
        accountID = newAccountID;
    }

    public Integer getActionID() {
        return actionID;
    }

    public void setActionID(Integer newActionID) {
        actionID = newActionID;
    }

    public Integer getLMProgramID() {
        return _LMProgramID;
    }

    public void setLMProgramID(Integer newLMProgramID) {
        _LMProgramID = newLMProgramID;
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
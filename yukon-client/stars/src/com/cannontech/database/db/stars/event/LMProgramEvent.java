package com.cannontech.database.db.stars.event;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.xml.StarsCustListEntryFactory;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMProgramEvent extends DBPersistent {

    private Integer eventID = null;
    private Integer accountID = new Integer( com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT );
    private Integer lmProgramID = new Integer(0);

    public static final String[] SETTER_COLUMNS = {
        "AccountID", "LMProgramID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "EventID" };

    public static final String TABLE_NAME = "LMProgramEvent";

    public LMProgramEvent() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getEventID(), getAccountID(), getLMProgramID(),
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getLMProgramID()
        };

        Object[] constraintValues = { getEventID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAccountID( (Integer) results[0] );
            setLMProgramID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public static LMProgramEvent[] getAllLMProgramEvents(Integer accountID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = ? ORDER BY EventID";

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
                    LMProgramEvent event = new LMProgramEvent();

                    event.setEventID( new Integer(rset.getInt("EventID")) );
                    event.setAccountID( new Integer(rset.getInt("AccountID")) );
                    event.setLMProgramID( new Integer(rset.getInt("LMProgramID")) );

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
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        LMProgramEvent[] events = new LMProgramEvent[ eventList.size() ];
        eventList.toArray( events );
        return events;
    }
    
    public static LMProgramEvent[] getAllLMProgramEvents(Integer accountID, Integer programID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = " + accountID.toString()
        		   + " AND LMProgramID = " + programID.toString() + " ORDER BY EventID";

        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try
        {
        	stmt.execute();
        	LMProgramEvent[] events = new LMProgramEvent[ stmt.getRowCount() ];

            for (int i = 0; i < events.length; i++) {
            	Object[] row = stmt.getRow(i);
                events[i] = new LMProgramEvent();

                events[i].setEventID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                events[i].setAccountID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
                events[i].setLMProgramID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
            }
            
            return events;
        }
        catch( Exception e )
        {
                e.printStackTrace();
        }

        return null;
    }

    public static void deleteAllLMProgramEvents(Integer accountID, java.sql.Connection conn) {
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
    
    public static LMProgramEvent getLastLMProgramEvent(Integer accountID, Integer programID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE EventID = ("
        		   + "SELECT MAX(EventID) FROM " + TABLE_NAME + " WHERE AccountID = ? AND LMProgramID = ?)";

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
                pstmt.setInt( 1, accountID.intValue() );
                pstmt.setInt( 2, programID.intValue() );
                rset = pstmt.executeQuery();

                if (rset.next()) {
                    LMProgramEvent event = new LMProgramEvent();
                    event.setEventID( new Integer(rset.getInt("EventID")) );
                    event.setAccountID( new Integer(rset.getInt("AccountID")) );
                    event.setLMProgramID( new Integer(rset.getInt("LMProgramID")) );
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
	/**
	 * Returns the lmProgramID.
	 * @return Integer
	 */
	public Integer getLMProgramID() {
		return lmProgramID;
	}

	/**
	 * Sets the lmProgramID.
	 * @param lmProgramID The lmProgramID to set
	 */
	public void setLMProgramID(Integer lmProgramID) {
		this.lmProgramID = lmProgramID;
	}

}
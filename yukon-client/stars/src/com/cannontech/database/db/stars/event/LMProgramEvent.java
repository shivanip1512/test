package com.cannontech.database.db.stars.event;

import java.util.ArrayList;

import com.cannontech.database.db.DBPersistent;

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

    public static Integer[] getAllLMProgramEventIDs(Integer accountID, java.sql.Connection conn) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE AccountID = ? ORDER BY EventID";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        ArrayList eventList = new ArrayList();

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
    
    public static Integer[] getLMProgramEventIDs(Integer accountID, Integer programID) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE AccountID = " + accountID.toString()
        		   + " AND LMProgramID = " + programID.toString() + " ORDER BY EventID";

        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try
        {
        	stmt.execute();
        	Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
            for (int i = 0; i < eventIDs.length; i++)
            	eventIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() );
            
            return eventIDs;
        }
        catch( Exception e )
        {
                e.printStackTrace();
        }

        return null;
    }
    
    public static Integer[] getAllLMProgramEventIDs(Integer energyCompanyID, Integer programID, java.sql.Connection conn) {
    	String sql = "SELECT event.EventID FROM " +
    			TABLE_NAME + " event, ECToAccountMapping map " +
				"WHERE event.LMProgramID = " + programID +
				" AND event.AccountID = map.AccountID" +
				" AND map.EnergyCompanyID = " + energyCompanyID;
		
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		ArrayList eventList = new ArrayList();
		
		try {
			if( conn == null ) {
				throw new IllegalStateException("Database connection should not be null.");
			}
			else {
				stmt = conn.createStatement();
				rset = stmt.executeQuery( sql );
				while (rset.next())
					eventList.add( new Integer(rset.getInt(1)) );
			}
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
			}
			catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		
		Integer[] eventIDs = new Integer[ eventList.size() ];
		eventList.toArray( eventIDs );
		return eventIDs;
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
package com.cannontech.database.db.stars.event;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
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
    private Integer accountID = new Integer( CtiUtilities.NONE_ID );
    private Integer lmProgramID = new Integer( CtiUtilities.NONE_ID );
    
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

    public static Integer[] getAllLMProgramEventIDs(Integer accountID) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE AccountID=" + accountID + " ORDER BY EventID";
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
        	
			Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				eventIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() );
			
			return eventIDs;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }
    
    public static Integer[] getLMProgramEventIDs(Integer accountID, Integer programID) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE AccountID = " + accountID
        		   + " AND LMProgramID = " + programID + " ORDER BY EventID";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
        	
			Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				eventIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() );
			
			return eventIDs;
        }
		catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
    }
    
    public static Integer[] getAllLMProgramEventIDs(Integer energyCompanyID, Integer programID) {
    	String sql = "SELECT event.EventID FROM " +
    			TABLE_NAME + " event, ECToAccountMapping map " +
				"WHERE event.LMProgramID = " + programID +
				" AND event.AccountID = map.AccountID" +
				" AND map.EnergyCompanyID = " + energyCompanyID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
		try {
			stmt.execute();
        	
			Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				eventIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() );
			
			return eventIDs;
		}
		catch (Exception e) {
			e.printStackTrace();
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
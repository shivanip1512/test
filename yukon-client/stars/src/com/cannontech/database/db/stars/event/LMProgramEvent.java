package com.cannontech.database.db.stars.event;

import com.cannontech.clientutils.CTILogger;
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
    private Integer accountID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private Integer programID = new Integer( CtiUtilities.NONE_ZERO_ID );
    
    public static final String[] SETTER_COLUMNS = {
        "AccountID", "ProgramID"
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
            getEventID(), getAccountID(), getProgramID(),
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getProgramID()
        };

        Object[] constraintValues = { getEventID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAccountID( (Integer) results[0] );
            setProgramID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public static Integer[] getLMProgramEventIDs(int accountID) {
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
        	CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }
    
    public static Integer[] getLMProgramEventIDs(int accountID, int programID) {
        String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE AccountID = " + accountID
        		   + " AND ProgramID = " + programID + " ORDER BY EventID";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
        	
			Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				eventIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() );
			
			return eventIDs;
        }
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
        
		return null;
    }
    
    public static Integer[] getAllLMProgramEventIDs(int programID) {
    	String sql = "SELECT EventID FROM " + TABLE_NAME + " WHERE ProgramID = " + programID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
		try {
			stmt.execute();
        	
			Integer[] eventIDs = new Integer[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				eventIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() );
			
			return eventIDs;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
	 * Returns the programID.
	 * @return Integer
	 */
	public Integer getProgramID() {
		return programID;
	}

	/**
	 * Sets the programID.
	 * @param programID The programID to set
	 */
	public void setProgramID(Integer programID) {
		this.programID = programID;
	}

}
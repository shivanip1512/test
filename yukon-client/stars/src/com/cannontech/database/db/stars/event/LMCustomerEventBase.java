package com.cannontech.database.db.stars.event;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: LMCustomerEventBase.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 12, 2002 2:39:45 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class LMCustomerEventBase extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer eventID = null;
    private Integer eventTypeID = new Integer( com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID );
    private Integer actionID = new Integer( com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID );
    private java.util.Date eventDateTime = new java.util.Date(0);
    private String notes = "";
    private String authorizedBy = "";

    public static final String[] SETTER_COLUMNS = {
        "EventTypeID", "ActionID", "EventDateTime", "Notes", "AuthorizedBy"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "EventID" };

    public static final String TABLE_NAME = "LMCustomerEventBase";

    public static final String GET_NEXT_EVENT_ID_SQL =
        "SELECT MAX(EventID) FROM " + TABLE_NAME;

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getEventID() == null)
    		setEventID( getNextEventID() );
    		
        Object[] addValues = {
            getEventID(), getEventTypeID(), getActionID(), getEventDateTime(),
            getNotes(), getAuthorizedBy()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getEventTypeID(), getActionID(), getEventDateTime(), getNotes(), getAuthorizedBy()
        };

        Object[] constraintValues = { getEventID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEventID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setEventTypeID( (Integer) results[0] );
            setActionID( (Integer) results[1] );
            setEventDateTime( new java.util.Date(((java.sql.Timestamp) results[2]).getTime()) );
            setNotes( (String) results[3] );
            setAuthorizedBy( (String) results[4] );
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
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {}
        }

        return new Integer( nextEventID );
    }
    
    public static LMCustomerEventBase[] getAllCustomerEvents(int eventTypeID, int actionID) {
    	String sql = "SELECT * FROM " + TABLE_NAME + " WHERE EventTypeID = " + String.valueOf(eventTypeID)
    			   + " AND ActionID = " + String.valueOf(actionID);
    			   
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		
    		LMCustomerEventBase[] events = new LMCustomerEventBase[ stmt.getRowCount() ];
    		for (int i = 0; i < stmt.getRowCount(); i++) {
    			events[i] = new LMCustomerEventBase();
    			Object[] row = stmt.getRow( i );
    			events[i].setEventID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    			events[i].setEventTypeID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
    			events[i].setActionID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    			events[i].setEventDateTime( (java.util.Date) row[3] );
    			events[i].setNotes( (String) row[4] );
    			events[i].setAuthorizedBy( (String) row[5] );
    		}
    		
    		return events;
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }

	/**
	 * Returns the actionID.
	 * @return Integer
	 */
	public Integer getActionID() {
		return actionID;
	}

	/**
	 * Returns the authorizedBy.
	 * @return String
	 */
	public String getAuthorizedBy() {
		return authorizedBy;
	}

	/**
	 * Returns the eventDateTime.
	 * @return java.util.Date
	 */
	public java.util.Date getEventDateTime() {
		return eventDateTime;
	}

	/**
	 * Returns the eventID.
	 * @return Integer
	 */
	public Integer getEventID() {
		return eventID;
	}

	/**
	 * Returns the eventTypeID.
	 * @return Integer
	 */
	public Integer getEventTypeID() {
		return eventTypeID;
	}

	/**
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the actionID.
	 * @param actionID The actionID to set
	 */
	public void setActionID(Integer actionID) {
		this.actionID = actionID;
	}

	/**
	 * Sets the authorizedBy.
	 * @param authorizedBy The authorizedBy to set
	 */
	public void setAuthorizedBy(String authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	/**
	 * Sets the eventDateTime.
	 * @param eventDateTime The eventDateTime to set
	 */
	public void setEventDateTime(java.util.Date eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

	/**
	 * Sets the eventID.
	 * @param eventID The eventID to set
	 */
	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}

	/**
	 * Sets the eventTypeID.
	 * @param eventTypeID The eventTypeID to set
	 */
	public void setEventTypeID(Integer eventTypeID) {
		this.eventTypeID = eventTypeID;
	}

	/**
	 * Sets the notes.
	 * @param notes The notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

}

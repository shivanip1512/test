package com.cannontech.database.db.stars.report;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CallReportBase extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer callID = null;
    private String callNumber = "";
    private Integer callTypeID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );
    private java.util.Date dateTaken = new java.util.Date(0);
    private String takenBy = "";
    private String description = "";
    private Integer accountID = new Integer( com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "CallNumber", "CallTypeID", "DateTaken", "TakenBy", "Description", "AccountID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "CallID" };

    public static final String TABLE_NAME = "CallReportBase";

    public static final String GET_NEXT_CALL_ID_SQL =
        "SELECT MAX(CallID) FROM " + TABLE_NAME;

    public CallReportBase() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getCallID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getCallID() == null)
    		setCallID( getNextCallID() );
    		
        Object[] addValues = {
            getCallID(), getCallNumber(), getCallTypeID(), getDateTaken(),
            getTakenBy(), getDescription(), getAccountID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getCallNumber(), getCallTypeID(), getDateTaken(),
            getTakenBy(), getDescription(), getAccountID()
        };

        Object[] constraintValues = { getCallID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getCallID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setCallNumber( (String) results[0] );
            setCallTypeID( (Integer) results[1] );
            setDateTaken( new java.util.Date(((java.sql.Timestamp) results[2]).getTime()) );
            setTakenBy( (String) results[3] );
            setDescription( (String) results[4] );
            setAccountID( (Integer) results[5] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextCallID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextCallID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_CALL_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextCallID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
				if( rset != null ) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextCallID );
    }

    public static CallReportBase[] getAllAccountCallReports(Integer accountID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = " + accountID.toString()
        		   + " ORDER BY DateTaken DESC";
        
        try {		   
	        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement( sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	        stmt.execute();
	        
	        CallReportBase[] reports = new CallReportBase[ stmt.getRowCount() ];
	        for (int i = 0; i < stmt.getRowCount(); i++) {
	        	Object[] row = stmt.getRow(i);
                reports[i] = new CallReportBase();

                reports[i].setCallID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                reports[i].setCallNumber( (String) row[1] );
                reports[i].setCallTypeID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
                reports[i].setDateTaken( (java.util.Date) row[3] );
                reports[i].setTakenBy( (String) row[4] );
                reports[i].setDescription( (String) row[5] );
                reports[i].setAccountID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
            }
            
            return reports;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return null;
    }

    public static CallReportBase[] getAllCustomerCallReports(Integer customerID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " call, " + com.cannontech.database.db.stars.customer.CustomerAccount.TABLE_NAME + " account "
        		   + "WHERE call.AccountID = account.AccountID AND account.CustomerID = " + customerID.toString()
        		   + " ORDER BY DateTaken DESC";

        try {		   
	        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement( sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	        stmt.execute();
	        
	        CallReportBase[] reports = new CallReportBase[ stmt.getRowCount() ];
	        for (int i = 0; i < stmt.getRowCount(); i++) {
	        	Object[] row = stmt.getRow(i);
                reports[i] = new CallReportBase();

                reports[i].setCallID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                reports[i].setCallNumber( (String) row[1] );
                reports[i].setCallTypeID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
                reports[i].setDateTaken( (java.util.Date) row[3] );
                reports[i].setTakenBy( (String) row[4] );
                reports[i].setDescription( (String) row[5] );
                reports[i].setAccountID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
            }
            
            return reports;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return null;
    }

    public static void deleteAllAccountCallReports(Integer accountID, java.sql.Connection conn) {
        CallReportBase[] reports = getAllAccountCallReports(accountID);

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE AccountID = ?";
        java.sql.PreparedStatement pstmt = null;
        java.sql.Statement stmt = null;

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

                if (reports.length > 0) {
                    StringBuffer sql2 = new StringBuffer("DELETE FROM ECToCallReportMapping WHERE CallReportID = ");
                    for (int i = 0; i < reports.length; i++) {
                        sql2.append( reports[i].getCallID() );
                        if (i < reports.length - 1)
                            sql2.append( " OR CallReportID = " );
                    }

                    stmt = conn.createStatement();
                    stmt.execute( sql2.toString() );
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
                if (stmt != null) stmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }
    }

    public Integer getCallID() {
        return callID;
    }

    public void setCallID(Integer newCallID) {
        callID = newCallID;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String newCallNumber) {
        callNumber = newCallNumber;
    }

    public java.util.Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(java.util.Date newDateTaken) {
        dateTaken = newDateTaken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

	/**
	 * Returns the accountID.
	 * @return Integer
	 */
	public Integer getAccountID() {
		return accountID;
	}

	/**
	 * Returns the callTypeID.
	 * @return Integer
	 */
	public Integer getCallTypeID() {
		return callTypeID;
	}

	/**
	 * Sets the accountID.
	 * @param accountID The accountID to set
	 */
	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
	}

	/**
	 * Sets the callTypeID.
	 * @param callTypeID The callTypeID to set
	 */
	public void setCallTypeID(Integer callTypeID) {
		this.callTypeID = callTypeID;
	}

	/**
	 * Returns the takenBy.
	 * @return String
	 */
	public String getTakenBy() {
		return takenBy;
	}

	/**
	 * Sets the takenBy.
	 * @param takenBy The takenBy to set
	 */
	public void setTakenBy(String takenBy) {
		this.takenBy = takenBy;
	}

}
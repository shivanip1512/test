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
    private String description = "";
    private Integer accountID = new Integer( com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT );
    private Integer customerID = new Integer( com.cannontech.database.db.stars.customer.CustomerBase.NONE_INT );
    private String takenBy = "";

    public static final String[] SETTER_COLUMNS = {
        "CallNumber", "CallTypeID", "DateTaken", "Description", "AccountID", "CustomerID", "TakenBy"
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
            getDescription(), getAccountID(), getCustomerID(), getTakenBy()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getCallNumber(), getCallTypeID(), getDateTaken(),
            getDescription(), getAccountID(), getCustomerID(), getTakenBy()
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
            setDescription( (String) results[3] );
            setAccountID( (Integer) results[4] );
            setCustomerID( (Integer) results[5] );
            setTakenBy( (String) results[6] );
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
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextCallID );
    }

    public static CallReportBase[] getAllAccountCallReports(Integer accountID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = ? "
        		   + "ORDER BY DateTaken DESC";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList reportList = new java.util.ArrayList();

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
                    CallReportBase report = new CallReportBase();

                    report.setCallID( new Integer(rset.getInt("CallID")) );
                    report.setCallNumber( rset.getString("CallNumber") );
                    report.setCallTypeID( new Integer(rset.getInt("CallTypeID")) );
                    report.setDateTaken( new java.util.Date(rset.getTimestamp("DateTaken").getTime()) );
                    report.setDescription( rset.getString("Description") );
                    report.setAccountID( new Integer(rset.getInt("AccountID")) );
                    report.setCustomerID( new Integer(rset.getInt("CustomerID")) );
                    report.setTakenBy( rset.getString("TakenBy") );

                    reportList.add( report );
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

        CallReportBase[] reports = new CallReportBase[ reportList.size() ];
        reportList.toArray( reports );
        return reports;
    }

    public static CallReportBase[] getAllCustomerCallReports(Integer customerID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE CustomerID = ? "
        		   + "ORDER BY DateTaken DESC";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList reportList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, customerID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    CallReportBase report = new CallReportBase();

                    report.setCallID( new Integer(rset.getInt("CallID")) );
                    report.setCallNumber( rset.getString("CallNumber") );
                    report.setCallTypeID( new Integer(rset.getInt("CallTypeID")) );
                    report.setDateTaken( new java.util.Date(rset.getTimestamp("DateTaken").getTime()) );
                    report.setDescription( rset.getString("Description") );
                    report.setAccountID( new Integer(rset.getInt("AccountID")) );
                    report.setCustomerID( new Integer(rset.getInt("CustomerID")) );
                    report.setTakenBy( rset.getString("TakenBy") );

                    reportList.add( report );
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

        CallReportBase[] reports = new CallReportBase[ reportList.size() ];
        reportList.toArray( reports );
        return reports;
    }

    public static void deleteAllAccountCallReports(Integer accountID, java.sql.Connection conn) {
        CallReportBase[] reports = getAllAccountCallReports(accountID, conn);

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
                    StringBuffer sql2 = new StringBuffer("DELETE FROM ECToServiceOrderMapping WHERE CallReportID = ");
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

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer newCustomerID) {
        customerID = newCustomerID;
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
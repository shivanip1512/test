package com.cannontech.database.db.starsreport;

import com.cannontech.database.db.*;


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
    private String callNumber = null;
    private String callType = null;
    java.util.Date dateTaken = null;
    private String description = null;
    private String actionItems = null;
    private String relatedToAccountNumber = null;
    private Integer customerID = new Integer( com.cannontech.database.db.starscustomer.CustomerBase.NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "CallNumber", "CallType", "DateTaken", "Description", "ActionItems",
        "RelatedToAccountNumber", "CustomerID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "CallID" };

    public static final String TABLE_NAME = "CallReportBase";

    public static final String GET_NEXT_CALL_ID_SQL =
        "SELECT MAX(CallID) FROM " + TABLE_NAME;

    public CallReportBase() {
        super();
    }

    public static CallReportBase[] getAllCallReports(Integer customerID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE CustomerID = ?";

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
                    report.setCallType( rset.getString("CallType") );
                    report.setDateTaken( new java.util.Date(rset.getTimestamp("DateTaken").getTime()) );
                    report.setDescription( rset.getString("Description") );
                    report.setActionItems( rset.getString("ActionItems") );
                    report.setRelatedToAccountNumber( rset.getString("RelatedToAccountNumber") );
                    report.setCustomerID( new Integer(rset.getInt("CustomerID")) );

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

    public static void deleteAllCallReports(Integer customerID, java.sql.Connection conn) {
        CallReportBase[] reports = getAllCallReports(customerID, conn);

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE CustomerID = ?";
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
                pstmt.setInt( 1, customerID.intValue() );
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

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getCallID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getCallID() == null)
    		setCallID( getNextCallID() );
    		
        Object[] addValues = {
            getCallID(), getCallNumber(), getCallType(), getDateTaken(),
            getDescription(), getActionItems(), getRelatedToAccountNumber(),
            getCustomerID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getCallNumber(), getCallType(), getDateTaken(), getDescription(),
            getActionItems(), getRelatedToAccountNumber(), getCustomerID()
        };

        Object[] constraintValues = { getCallID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getCallID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setCallNumber( (String) results[0] );
            setCallType( (String) results[1] );
            setDateTaken( new java.util.Date(((java.sql.Timestamp) results[2]).getTime()) );
            setDescription( (String) results[3] );
            setActionItems( (String) results[4] );
            setRelatedToAccountNumber( (String) results[5] );
            setCustomerID( (Integer) results[6] );
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

    public String getCallType() {
        return callType;
    }

    public void setCallType(String newCallType) {
        callType = newCallType;
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

    public String getActionItems() {
        return actionItems;
    }

    public void setActionItems(String newActionItems) {
        actionItems = newActionItems;
    }

    public String getRelatedToAccountNumber() {
        return relatedToAccountNumber;
    }

    public void setRelatedToAccountNumber(String newRelatedToAccountNumber) {
        relatedToAccountNumber = newRelatedToAccountNumber;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer newCustomerID) {
        customerID = newCustomerID;
    }
}
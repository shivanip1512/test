package com.cannontech.database.db.starscustomer;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CustomerBase extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer customerID = null;
    private Integer primaryContactID = new Integer(NONE_INT);
    private String customerType = null;
    private String timeZone = null;
    private Integer paoID = new Integer(NONE_INT);

    public static final String[] SETTER_COLUMNS = {
        "PrimaryContactID", "CustomerType", "TimeZone", "PaoID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "CustomerID" };

    public static final String TABLE_NAME = "CustomerBase";

    public static final String GET_NEXT_CUSTOMER_ID_SQL =
        "SELECT MAX(CustomerID) FROM " + TABLE_NAME;

    public CustomerBase() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getCustomerID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getCustomerID(), getPrimaryContactID(), getCustomerType(),
            getTimeZone(), getPaoID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getPrimaryContactID(), getCustomerType(), getTimeZone(), getPaoID()
        };

        Object[] constraintValues = { getCustomerID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getCustomerID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setPrimaryContactID( (Integer) results[0] );
            setCustomerType( (String) results[1] );
            setTimeZone( (String) results[2] );
            setPaoID( (Integer) results[3] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextCustomerID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextCustomerID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_CUSTOMER_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextCustomerID = rset.getInt(1) + 1;
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

        return new Integer( nextCustomerID );
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer newCustomerID) {
        customerID = newCustomerID;
    }

    public Integer getPrimaryContactID() {
        return primaryContactID;
    }

    public void setPrimaryContactID(Integer newPrimaryContactID) {
        primaryContactID = newPrimaryContactID;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String newCustomerType) {
        customerType = newCustomerType;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String newTimeZone) {
        timeZone = newTimeZone;
    }

    public Integer getPaoID() {
        return paoID;
    }

    public void setPaoID(Integer newPaoID) {
        paoID = newPaoID;
    }
}
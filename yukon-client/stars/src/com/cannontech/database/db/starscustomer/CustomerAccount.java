package com.cannontech.database.db.starscustomer;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CustomerAccount extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer accountID = null;
    private Integer accountSiteID = new Integer( AccountSite.NONE_INT );
    private String accountNumber = null;
    private Integer customerID = new Integer( com.cannontech.database.db.starscustomer.CustomerBase.NONE_INT );
    private Integer billingAddressID = new Integer( com.cannontech.database.db.customer.CustomerAddress.NONE_INT );
    private String accountNotes = null;

    public static final String[] SETTER_COLUMNS = {
        "AccountSiteID", "AccountNumber", "CustomerID",
        "BillingAddressID", "AccountNotes"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "AccountID" };

    public static final String TABLE_NAME = "CustomerAccount";

    public static final String GET_NEXT_ACCOUNT_ID_SQL =
        "SELECT MAX(AccountID) FROM " + TABLE_NAME;

    public CustomerAccount() {
        super();
    }

    public static CustomerAccount searchByAccountNumber(Integer energyCompanyID, String accountNumber, java.sql.Connection conn) {
        String sql = "SELECT account.* FROM ECToAccountMapping map, CustomerAccount account "
                   + "WHERE map.EnergyCompanyID = ? AND account.AccountNumber = ? "
                   + "AND map.AccountID = account.AccountID";

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
                pstmt.setInt( 1, energyCompanyID.intValue() );
                pstmt.setString( 2, accountNumber );
                rset = pstmt.executeQuery();

                if (rset.next()) {
                    CustomerAccount account = new CustomerAccount();

                    account.setAccountID( new Integer(rset.getInt("AccountID")) );
                    account.setAccountSiteID( new Integer(rset.getInt("AccountSiteID")) );
                    account.setAccountNumber( rset.getString("AccountNumber") );
                    account.setCustomerID( new Integer(rset.getInt("CustomerID")) );
                    account.setBillingAddressID( new Integer(rset.getInt("BillingAddressID")) );
                    account.setAccountNotes( rset.getString("AccountNotes") );

                    return account;
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

        return null;
    }

    public static CustomerAccount[] getAllCustomerAccounts(Integer customerID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE CustomerID = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList accountList = new java.util.ArrayList();

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
                    CustomerAccount account = new CustomerAccount();

                    account.setAccountID( new Integer(rset.getInt("AccountID")) );
                    account.setAccountSiteID( new Integer(rset.getInt("AccountSiteID")) );
                    account.setAccountNumber( rset.getString("AccountNumber") );
                    account.setCustomerID( new Integer(rset.getInt("CustomerID")) );
                    account.setBillingAddressID( new Integer(rset.getInt("BillingAddressID")) );
                    account.setAccountNotes( rset.getString("AccountNotes") );

                    accountList.add( account );
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

        CustomerAccount[] accounts = new CustomerAccount[ accountList.size() ];
        accountList.toArray( accounts );
        return accounts;
    }
/*
    public static void clearAccountSite(Integer accountSiteID, java.sql.Connection conn) {
        String sql = "UPDATE " + TABLE_NAME + " SET AccountSiteID = " +
                     AccountSite.NONE_INT + " WHERE AccountSiteID = ?";

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
                pstmt.setInt( 1, accountSiteID.intValue() );
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
    }*/

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getAccountID() == null)
    		setAccountID( getNextAccountID() );
    		
        Object[] addValues = {
            getAccountID(), getAccountSiteID(), getAccountNumber(),
            getCustomerID(), getBillingAddressID(), getAccountNotes()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountSiteID(), getAccountNumber(), getCustomerID(),
            getBillingAddressID(), getAccountNotes()
        };

        Object[] constraintValues = { getAccountID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAccountSiteID( (Integer) results[0] );
            setAccountNumber( (String) results[1] );
            setCustomerID( (Integer) results[2] );
            setBillingAddressID( (Integer) results[3] );
            setAccountNotes( (String) results[4] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextAccountID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextAccountID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_ACCOUNT_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextAccountID = rset.getInt(1) + 1;
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

        return new Integer( nextAccountID );
    }

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer newAccountID) {
        accountID = newAccountID;
    }

    public Integer getAccountSiteID() {
        return accountSiteID;
    }

    public void setAccountSiteID(Integer newAccountSiteID) {
        accountSiteID = newAccountSiteID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String newAccountNumber) {
        accountNumber = newAccountNumber;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer newCustomerID) {
        customerID = newCustomerID;
    }

    public Integer getBillingAddressID() {
        return billingAddressID;
    }

    public void setBillingAddressID(Integer newBillingAddressID) {
        billingAddressID = newBillingAddressID;
    }

    public String getAccountNotes() {
        return accountNotes;
    }

    public void setAccountNotes(String newAccountNotes) {
        accountNotes = newAccountNotes;
    }
}
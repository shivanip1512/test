package com.cannontech.database.db.stars.customer;

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
    private String accountNumber = "";
    private Integer customerID = new Integer( 0 );
    private Integer billingAddressID = new Integer( 0 );
    private String accountNotes = "";
    private Integer loginID = new Integer( com.cannontech.user.UserUtils.USER_YUKON_ID );

    public static final String[] SETTER_COLUMNS = {
        "AccountSiteID", "AccountNumber", "CustomerID", "BillingAddressID", "AccountNotes", "LoginID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "AccountID" };

    public static final String TABLE_NAME = "CustomerAccount";

    public static final String GET_NEXT_ACCOUNT_ID_SQL =
        "SELECT MAX(AccountID) FROM " + TABLE_NAME;

    public CustomerAccount() {
        super();
    }

    public static CustomerAccount[] searchByAccountNumber(Integer energyCompanyID, String accountNumber) {
        String sql = "SELECT acct.AccountID, acct.AccountSiteID, acct.AccountNumber, acct.CustomerID, acct.BillingAddressID, acct.AccountNotes, acct.LoginID "
        		   + "FROM ECToAccountMapping map, " + TABLE_NAME + " acct "
                   + "WHERE map.EnergyCompanyID = " + energyCompanyID.toString()
                   + " AND UPPER(acct.AccountNumber) LIKE UPPER('" + accountNumber + "') AND map.AccountID = acct.AccountID";
        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try
        {
			stmt.execute();
    		CustomerAccount[] accounts = new CustomerAccount[ stmt.getRowCount() ];
    		
    		for (int i = 0; i < accounts.length; i++) {
				Object[] row = stmt.getRow(i);
				accounts[i] = new CustomerAccount();

                accounts[i].setAccountID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                accounts[i].setAccountSiteID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
                accounts[i].setAccountNumber( (String) row[2] );
                accounts[i].setCustomerID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
                accounts[i].setBillingAddressID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
                accounts[i].setAccountNotes( (String) row[5] );
                accounts[i].setLoginID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
            }
    		
    		return accounts;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }
    
    private static CustomerAccount[] searchByPrimaryContactIDs(Integer energyCompanyID, int[] contactIDs) {
    	if (contactIDs == null || contactIDs.length == 0) return null;
    	
        String sql = "SELECT DISTINCT acct.AccountID, acct.AccountSiteID, acct.AccountNumber, acct.CustomerID, acct.BillingAddressID, acct.AccountNotes, acct.LoginID "
    			   + "FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.customer.Customer.TABLE_NAME + " cust "
    			   + "WHERE map.EnergyCompanyID = " + energyCompanyID.toString() + " AND map.AccountID = acct.AccountID "
    			   + "AND acct.CustomerID = cust.CustomerID AND (cust.PrimaryContactID = " + String.valueOf(contactIDs[0]);
    	for (int i = 1; i < contactIDs.length; i++)
    		sql += " OR cust.PrimaryContactID = " + String.valueOf(contactIDs[i]);
    	sql += ")";
    	
        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		CustomerAccount[] accounts = new CustomerAccount[ stmt.getRowCount() ];
    		
    		for (int i = 0; i < accounts.length; i++) {
				Object[] row = stmt.getRow(i);
				accounts[i] = new CustomerAccount();

                accounts[i].setAccountID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                accounts[i].setAccountSiteID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
                accounts[i].setAccountNumber( (String) row[2] );
                accounts[i].setCustomerID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
                accounts[i].setBillingAddressID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
                accounts[i].setAccountNotes( (String) row[5] );
                accounts[i].setLoginID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
    		}
    		
    		return accounts;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    public static CustomerAccount[] searchByPhoneNumber(Integer energyCompanyID, String phoneNumber) {
		String sql = "SELECT DISTINCT ContactID FROM " + com.cannontech.database.db.contact.ContactNotification.TABLE_NAME
				   + " WHERE Notification = '" + phoneNumber + "' AND ("
				   + "NotificationCategoryID = " + com.cannontech.stars.web.servlet.SOAPServer.YUK_LIST_ENTRY_ID_HOME_PHONE
				   + " OR NotificationCategoryID = " + com.cannontech.stars.web.servlet.SOAPServer.YUK_LIST_ENTRY_ID_WORK_PHONE + ")";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() == 0) return new CustomerAccount[0];
			
			int[] contactIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < contactIDs.length; i++)
				contactIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			
		    return searchByPrimaryContactIDs( energyCompanyID, contactIDs );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
        return null;
    }
    
    public static CustomerAccount[] searchByLastName(Integer energyCompanyID, String lastName) {
		String sql = "SELECT ContactID FROM " + com.cannontech.database.db.contact.Contact.TABLE_NAME
				   + " WHERE UPPER(ContLastName) = UPPER('" + lastName + "')";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() == 0) return new CustomerAccount[0];
			
			int[] contactIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < contactIDs.length; i++)
				contactIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			
	        return searchByPrimaryContactIDs( energyCompanyID, contactIDs );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
    }
    
    public static CustomerAccount[] searchBySerialNumber(Integer energyCompanyID, String serialNo) {
    	int[] invIDs = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySerialNumber( serialNo );
    	if (invIDs == null) return null;
    	if (invIDs.length == 0) return new CustomerAccount[0];
    	
        String sql = "SELECT DISTINCT acct.AccountID, acct.AccountSiteID, acct.AccountNumber, acct.CustomerID, acct.BillingAddressID, acct.AccountNotes, acct.LoginID "
    			   + "FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " inv "
    			   + "WHERE map.EnergyCompanyID = " + energyCompanyID.toString() + " AND map.AccountID = acct.AccountID AND acct.AccountID = inv.AccountID AND (inv.InventoryID = " + String.valueOf(invIDs[0]);
    	for (int i = 1; i < invIDs.length; i++)
    		sql += " OR inv.InventoryID = " + String.valueOf(invIDs[i]);
    	sql += ")";
    	
        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try
        {
    		stmt.execute();
    		CustomerAccount[] accounts = new CustomerAccount[ stmt.getRowCount() ];
    		
    		for (int i = 0; i < accounts.length; i++) {
				Object[] row = stmt.getRow(i);
				accounts[i] = new CustomerAccount();

                accounts[i].setAccountID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                accounts[i].setAccountSiteID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
                accounts[i].setAccountNumber( (String) row[2] );
                accounts[i].setCustomerID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
                accounts[i].setBillingAddressID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
                accounts[i].setAccountNotes( (String) row[5] );
                accounts[i].setLoginID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
    		}
    		
    		return accounts;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public static CustomerAccount[] getAllCustomerAccounts(Integer customerID, java.sql.Connection conn) {
        String sql = "SELECT AccountID, AccountSiteID, AccountNumber, CustomerID, BillingAddressID, AccountNotes, LoginID "
        		   + "FROM " + TABLE_NAME + " WHERE CustomerID = ?";

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

                    account.setAccountID( new Integer(rset.getInt(1)) );
                    account.setAccountSiteID( new Integer(rset.getInt(2)) );
                    account.setAccountNumber( rset.getString(3) );
                    account.setCustomerID( new Integer(rset.getInt(4)) );
                    account.setBillingAddressID( new Integer(rset.getInt(5)) );
                    account.setAccountNotes( rset.getString(6) );
                    account.setLoginID( new Integer(rset.getInt(7)) );

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
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
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

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getAccountID() == null)
    		setAccountID( getNextAccountID() );
    		
        Object[] addValues = {
            getAccountID(), getAccountSiteID(), getAccountNumber(),
            getCustomerID(), getBillingAddressID(), getAccountNotes(), getLoginID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountSiteID(), getAccountNumber(), getCustomerID(),
            getBillingAddressID(), getAccountNotes(), getLoginID()
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
            setLoginID( (Integer) results[5] );
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
                if (rset != null) rset.close();
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
	/**
	 * Returns the loginID.
	 * @return Integer
	 */
	public Integer getLoginID() {
		return loginID;
	}

	/**
	 * Sets the loginID.
	 * @param loginID The loginID to set
	 */
	public void setLoginID(Integer loginID) {
		this.loginID = loginID;
	}

}
package com.cannontech.database.db.stars.customer;

import com.cannontech.common.constants.YukonListEntryTypes;
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

    public static int[] searchByAccountNumber(Integer energyCompanyID, String accountNumber) {
        String sql = "SELECT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct "
                   + "WHERE map.EnergyCompanyID = " + energyCompanyID.toString()
                   + " AND UPPER(acct.AccountNumber) LIKE UPPER('" + accountNumber + "') AND map.AccountID = acct.AccountID";
        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try
        {
			stmt.execute();
			int[] accountIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				accountIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			
			return accountIDs;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }
    
    private static int[] searchByPrimaryContactIDs(Integer energyCompanyID, int[] contactIDs) {
    	if (contactIDs == null || contactIDs.length == 0) return null;
    	
        String sql = "SELECT DISTINCT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.customer.Customer.TABLE_NAME + " cust "
    			   + "WHERE map.EnergyCompanyID = " + energyCompanyID.toString() + " AND map.AccountID = acct.AccountID "
    			   + "AND acct.CustomerID = cust.CustomerID AND (cust.PrimaryContactID = " + String.valueOf(contactIDs[0]);
    	for (int i = 1; i < contactIDs.length; i++)
    		sql += " OR cust.PrimaryContactID = " + String.valueOf(contactIDs[i]);
    	sql += ")";
    	
        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
			int[] accountIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				accountIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			
			return accountIDs;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    public static int[] searchByPhoneNumber(Integer energyCompanyID, String phoneNumber) {
		String sql = "SELECT DISTINCT ContactID FROM " + com.cannontech.database.db.contact.ContactNotification.TABLE_NAME
				   + " WHERE Notification = '" + phoneNumber + "' AND ("
				   + "NotificationCategoryID = " + YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE
				   + " OR NotificationCategoryID = " + YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE + ")";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() == 0)
				return new int[0];
			
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
    
    public static int[] searchByLastName(Integer energyCompanyID, String lastName) {
		String sql = "SELECT ContactID FROM " + com.cannontech.database.db.contact.Contact.TABLE_NAME
				   + " WHERE UPPER(ContLastName) = UPPER('" + lastName + "')";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() == 0)
				return new int[0];
			
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
    
    public static int[] searchBySerialNumber(String serialNo, int energyCompanyID) {
    	java.sql.Connection conn = null;
    	
    	try {
    		conn = com.cannontech.database.PoolManager.getInstance().getConnection(
    				com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    				
			int[] invIDs = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySerialNumber( serialNo, energyCompanyID, conn );
			if (invIDs.length == 0) return new int[0];
    	
			String sql = "SELECT DISTINCT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " inv "
					   + "WHERE map.EnergyCompanyID = " + energyCompanyID + " AND map.AccountID = acct.AccountID AND acct.AccountID = inv.AccountID AND (inv.InventoryID = " + invIDs[0];
			for (int i = 1; i < invIDs.length; i++)
				sql += " OR inv.InventoryID = " + invIDs[i];
			sql += ")";
			
			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet rset = stmt.executeQuery( sql );
			
			int rowCnt = (rset.last())? rset.getRow() : 0;
			int[] accountIDs = new int[rowCnt];
			
			rset.beforeFirst();
			int i = 0;
			while (rset.next())
				accountIDs[i++] = rset.getInt(1);
			
			return accountIDs;
    	}
    	catch (java.sql.SQLException e) {
    		e.printStackTrace();
    		return null;
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (java.sql.SQLException e) {}
    	}
    }

    public static CustomerAccount getCustomerAccount(Integer accountID) {
        String sql = "SELECT AccountID, AccountSiteID, AccountNumber, CustomerID, BillingAddressID, AccountNotes, LoginID "
        		   + "FROM " + TABLE_NAME + " WHERE AccountID = " + accountID;
        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        		sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try
        {
        	stmt.execute();
        	
        	if (stmt.getRowCount() > 0) {
        		Object[] row = stmt.getRow(0);
                CustomerAccount account = new CustomerAccount();

                account.setAccountID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                account.setAccountSiteID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
                account.setAccountNumber( (String) row[2] );
                account.setCustomerID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
                account.setBillingAddressID( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
                account.setAccountNotes( (String) row[5] );
                account.setLoginID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
                
                return account;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Search by LocationAddress1, must be an exact match (case-insensitive)
     */
    public static int[] searchByAddress(Integer energyCompanyID, String addr1) {
		String sql = "SELECT Contact.ContactID FROM Contact, Address "
				+ "WHERE UPPER(Address.LocationAddress1) LIKE '%" + addr1.toUpperCase() + "%' "
				+ "AND Contact.AddressID = Address.AddressID";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() == 0)
				return new int[0];
			
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
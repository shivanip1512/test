package com.cannontech.database.db.stars.customer;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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

public class CustomerAccount extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer accountID = null;
    private Integer accountSiteID = new Integer( AccountSite.NONE_INT );
    private String accountNumber = "";
    private Integer customerID = new Integer( 0 );
    private Integer billingAddressID = new Integer( 0 );
    private String accountNotes = "";

    public static final String[] SETTER_COLUMNS = {
        "AccountSiteID", "AccountNumber", "CustomerID", "BillingAddressID", "AccountNotes"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "AccountID" };

    public static final String TABLE_NAME = "CustomerAccount";

    public static final String GET_NEXT_ACCOUNT_ID_SQL =
        "SELECT MAX(AccountID) FROM " + TABLE_NAME;

    public CustomerAccount() {
        super();
    }
    
    /**
     * @return Array of {Account ID(Integer), Account #(String)}
     */
	public static Object[][] getAllCustomerAccounts(Integer energyCompanyID) {
		String sql = "SELECT acct.AccountID, acct.AccountNumber " +
				"FROM " + TABLE_NAME + " acct, ECToAccountMapping map " +
				"WHERE map.EnergyCompanyID = " + energyCompanyID + " AND map.AccountID = acct.AccountID";
		
		try {
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			Object[][] res = new Object[ stmt.getRowCount() ][];
			for (int i = 0; i < stmt.getRowCount(); i++) {
				res[i] = new Object[2];
				res[i][0] = new Integer( ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue() );
				res[i][1] = (String) stmt.getRow(i)[1];
			}
			
			return res;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

    public static int[] searchByAccountNumber(Integer energyCompanyID, String accountNumber) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
	        String sql = "SELECT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct "
	                   + "WHERE map.EnergyCompanyID = ? AND map.AccountID = acct.AccountID"
	                   + " AND UPPER(acct.AccountNumber) LIKE UPPER(?)";
	        
	        stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, energyCompanyID.intValue());
	        stmt.setString(2, accountNumber);
	        rset = stmt.executeQuery();
	        
	        ArrayList acctIDList = new ArrayList();
	        while (rset.next())
	        	acctIDList.add( new Integer(rset.getInt(1)) );
	        
			int[] accountIDs = new int[ acctIDList.size() ];
			for (int i = 0; i < accountIDs.length; i++)
				accountIDs[i] = ((Integer) acctIDList.get(i)).intValue();
			
			return accountIDs;
        }
        catch( java.sql.SQLException e ) {
            CTILogger.error( e.getMessage(), e );
        }
		finally {
			try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
        return null;
    }
    
    public static int[] searchByPrimaryContactIDs(int[] contactIDs, int energyCompanyID) {
    	if (contactIDs == null || contactIDs.length == 0) return null;
    	
        String sql = "SELECT DISTINCT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.customer.Customer.TABLE_NAME + " cust "
    			   + "WHERE map.EnergyCompanyID = " + energyCompanyID + " AND map.AccountID = acct.AccountID "
    			   + "AND acct.CustomerID = cust.CustomerID AND (cust.PrimaryContactID = " + String.valueOf(contactIDs[0]);
    	for (int i = 1; i < contactIDs.length; i++)
    		sql += " OR cust.PrimaryContactID = " + String.valueOf(contactIDs[i]);
    	sql += ")";
    	
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		
			int[] accountIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				accountIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			
			return accountIDs;
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }
    
    public static int[] searchBySerialNumber(String serialNo, int energyCompanyID) {
    	try {
			com.cannontech.database.db.stars.hardware.LMHardwareBase[] hardwares =
					com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySerialNumber( serialNo, energyCompanyID );
			if (hardwares.length == 0) return new int[0];
    		
			String sql = "SELECT DISTINCT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " inv "
					   + "WHERE map.EnergyCompanyID = " + energyCompanyID + " AND map.AccountID = acct.AccountID AND acct.AccountID = inv.AccountID AND (inv.InventoryID = " + hardwares[0].getInventoryID();
			for (int i = 1; i < hardwares.length; i++)
				sql += " OR inv.InventoryID = " + hardwares[i].getInventoryID();
			sql += ")";
			
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			int[] accountIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				accountIDs[i] = ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue();
			
			return accountIDs;
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	
		return null;
    }
    
    public static int[] searchByAddress(String address, int energyCompanyID) {
    	String sql = "SELECT DISTINCT acct.AccountID " +
    			"FROM CustomerAccount acct, ECToAccountMapping map, AccountSite site, Address addr " +
    			"WHERE map.EnergyCompanyID = ? AND map.AccountID = acct.AccountID " +
    			"AND acct.AccountSiteID = site.AccountSiteID AND site.StreetAddressID = addr.AddressID " +
    			"AND UPPER(addr.LocationAddress1) LIKE UPPER(?)";
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
	        
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, energyCompanyID);
			stmt.setString(2, address + "%");
			rset = stmt.executeQuery();
	        
			ArrayList acctIDList = new ArrayList();
			while (rset.next())
				acctIDList.add( new Integer(rset.getInt(1)) );
	        
			int[] accountIDs = new int[ acctIDList.size() ];
			for (int i = 0; i < accountIDs.length; i++)
				accountIDs[i] = ((Integer) acctIDList.get(i)).intValue();
			
			return accountIDs;
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		return null;
    }

    public static CustomerAccount getCustomerAccount(Integer accountID) {
        String sql = "SELECT AccountID, AccountSiteID, AccountNumber, CustomerID, BillingAddressID, AccountNotes "
        		   + "FROM " + TABLE_NAME + " WHERE AccountID = " + accountID;
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );

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
                
                return account;
            }
        }
        catch( Exception e )
        {
            CTILogger.error( e.getMessage(), e );
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
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {}
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
package com.cannontech.stars.database.db.customer;

import java.util.ArrayList;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.db.hardware.InventoryBase;
import com.cannontech.stars.database.db.hardware.LMHardwareBase;

public class CustomerAccount extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer accountID = null;
    private Integer accountSiteID = AccountSite.NONE_INT;
    private String accountNumber = "";
    private Integer customerID = 0;
    private Integer billingAddressID = 0;
    private String accountNotes = "";

    public static final String[] SETTER_COLUMNS = {
        "AccountSiteID", "AccountNumber", "CustomerID", "BillingAddressID", "AccountNotes"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "AccountID" };

    public static final String TABLE_NAME = "CustomerAccount";

    public CustomerAccount() {
        super();
    }

    public static int[] searchByPrimaryContactIDs(int[] contactIDs, int energyCompanyID) {
    	if (contactIDs == null || contactIDs.length == 0) {
            return null;
        }
        int [] accountIDs = new int[0];
    	Date timerStart = new Date();
        String sql = "SELECT DISTINCT acct.AccountID, acct.AccountNumber FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.customer.Customer.TABLE_NAME + " cust "
    			   + "WHERE map.EnergyCompanyID = " + energyCompanyID + " AND map.AccountID = acct.AccountID "
    			   + "AND acct.CustomerID = cust.CustomerID AND (cust.PrimaryContactID = " + String.valueOf(contactIDs[0]);
    	for (int i = 1; i < contactIDs.length; i++) {
            sql += " OR cust.PrimaryContactID = " + String.valueOf(contactIDs[i]);
        }
    	sql += ")";
    	sql += "ORDER BY acct.AccountNumber ASC";
    	
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
        
        try {
            stmt.execute();
            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
            accountIDs = new int[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++) {
                accountIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
            }

        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for searchByPrimaryContactIDs (" + accountIDs.length  + " AccountIDS loaded)" );
        return accountIDs;
    }

    /**
     * Use this method when the energycompany is not known.
     * Returns an arrayList of CustomerAccount objects for the entire databse.
     * @param serialNo
     * @return
     */
    public static ArrayList<CustomerAccount> searchBySerialNumber(String serialNo) {

    	ArrayList<CustomerAccount> customerAccts = new ArrayList<CustomerAccount>();
    	
    	String sql = "SELECT DISTINCT ACCT.ACCOUNTID, ACCT.ACCOUNTSITEID, ACCT.ACCOUNTNUMBER, ACCT.CUSTOMERID, ACCT.BILLINGADDRESSID, ACCOUNTNOTES " + 
    				" FROM " + TABLE_NAME + " ACCT, " + InventoryBase.TABLE_NAME + " INV, " + LMHardwareBase.TABLE_NAME + " LMHB " +
					" WHERE ACCT.ACCOUNTID = INV.ACCOUNTID " +
					" AND INV.INVENTORYID = LMHB.INVENTORYID " + 
					" AND UPPER(LMHB.MANUFACTURERSERIALNUMBER) = UPPER(?) " + 
					" AND LMHB.INVENTORYID >= 0 ";

		java.sql.Connection conn = null;
    	java.sql.PreparedStatement stmt = null;
    	java.sql.ResultSet rset = null;

	    try {
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
    		stmt = conn.prepareStatement(sql);
			stmt.setString(1, serialNo);
			
			rset = stmt.executeQuery();
			
			while (rset.next()) {
                CustomerAccount account = new CustomerAccount();
                account.setAccountID( new Integer(rset.getInt(1)) );
                account.setAccountSiteID( new Integer(rset.getInt(2)) );
                account.setAccountNumber( rset.getString(3) );
                account.setCustomerID( new Integer(rset.getInt(4)) );
                account.setBillingAddressID( new Integer(rset.getInt(5)) );
                account.setAccountNotes( rset.getString(6));
                customerAccts.add(account);
			}
    	}
    	catch (java.sql.SQLException e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	finally {
    		try {
				if (rset != null) {
                    rset.close();
                }
				if (stmt != null) {
                    stmt.close();
                }
				if (conn != null) {
                    conn.close();
                }
    		}
    		catch (java.sql.SQLException e) {}
    	}
    	return customerAccts;
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getAccountID() == null) {
            setAccountID( getNextAccountID() );
        }
    		
        Object[] addValues = {
            getAccountID(), getAccountSiteID(), getAccountNumber(),
            getCustomerID(), getBillingAddressID(), getAccountNotes()
        };

        add( TABLE_NAME, addValues );
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountSiteID(), getAccountNumber(), getCustomerID(),
            getBillingAddressID(), getAccountNotes()
        };

        Object[] constraintValues = { getAccountID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAccountSiteID( (Integer) results[0] );
            setAccountNumber( (String) results[1] );
            setCustomerID( (Integer) results[2] );
            setBillingAddressID( (Integer) results[3] );
            setAccountNotes( (String) results[4] );
        } else {
            throw new Error(getClass() + " - Incorrect number of results retrieved");
        }
    }

    private Integer getNextAccountID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
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
package com.cannontech.stars.database.db.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.stars.database.db.hardware.InventoryBase;
import com.cannontech.stars.database.db.hardware.LMHardwareBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

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
				res[i][1] = stmt.getRow(i)[1];
			}
			
			return res;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

    public static int[] searchByAccountNumber(String accountNumber, int energyCompanyID) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
	        String sql = "SELECT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct "
	                   + "WHERE map.EnergyCompanyID = ? AND map.AccountID = acct.AccountID"
	                   + " AND UPPER(acct.AccountNumber) LIKE UPPER(?)";
	        
	        stmt = conn.prepareStatement(sql);
	        stmt.setInt( 1, energyCompanyID );
	        stmt.setString( 2, accountNumber );
	        rset = stmt.executeQuery();
	        
	        ArrayList<Integer> acctIDList = new ArrayList<Integer>();
	        while (rset.next())
	        	acctIDList.add( new Integer(rset.getInt(1)) );
	        
			int[] accountIDs = new int[ acctIDList.size() ];
			for (int i = 0; i < accountIDs.length; i++)
				accountIDs[i] = acctIDList.get(i).intValue();
			
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
        int [] accountIDs = new int[0];
    	Date timerStart = new Date();
        String sql = "SELECT DISTINCT acct.AccountID, acct.AccountNumber FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + com.cannontech.database.db.customer.Customer.TABLE_NAME + " cust "
    			   + "WHERE map.EnergyCompanyID = " + energyCompanyID + " AND map.AccountID = acct.AccountID "
    			   + "AND acct.CustomerID = cust.CustomerID AND (cust.PrimaryContactID = " + String.valueOf(contactIDs[0]);
    	for (int i = 1; i < contactIDs.length; i++)
    		sql += " OR cust.PrimaryContactID = " + String.valueOf(contactIDs[i]);
    	sql += ")";
    	sql += "ORDER BY acct.AccountNumber ASC";
    	
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
        
        try {
            stmt.execute();
            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
            accountIDs = new int[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++)
                accountIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();

        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for searchByPrimaryContactIDs (" + accountIDs.length  + " AccountIDS loaded)" );
        return accountIDs;
    }
    
    public static int[] searchByPrimaryContactLastName(String lastName_, int energyCompanyID, boolean partialMatch) {
        if (lastName_ == null || lastName_.length() == 0) return null;
        int [] accountIDs = new int[0];
        Date timerStart = new Date();
        String lastName = lastName_.trim();
        String firstName = null;
        int commaIndex = lastName_.indexOf(",");
        if( commaIndex > 0 )
        {
            firstName = lastName_.substring(commaIndex+1).trim();
            lastName = lastName_.substring(0, commaIndex).trim();
        }
        String sql = "SELECT DISTINCT acct.AccountID " +
                    " FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + 
                    Customer.TABLE_NAME + " cust, " +  Contact.TABLE_NAME + " cont " + 
                    " WHERE map.EnergyCompanyID = ? " + 
                    " AND map.AccountID = acct.AccountID " +
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND cust.primarycontactid = cont.contactid " +
                    " AND UPPER(cont.contlastname) like ?" ;
                    if (firstName != null && firstName.length() > 0)
                        sql += " AND UPPER(CONTFIRSTNAME) LIKE ? ";

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt( 1, energyCompanyID);
            pstmt.setString( 2, lastName.toUpperCase()+(partialMatch? "%":""));
            if (firstName != null && firstName.length() > 0)
                pstmt.setString(3, firstName.toUpperCase() + "%");
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
            ArrayList<Integer> accountIDList = new ArrayList<Integer>();
            while(rset.next())
            {
                accountIDList.add(new Integer(rset.getInt(1)));
            }
            accountIDs = new int[accountIDList.size()];
            for (int i = 0; i < accountIDList.size(); i++)
                accountIDs[i] = accountIDList.get(i).intValue();
            
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + lastName+ ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for searchByPrimaryContactLastName (" + accountIDs.length  + " AccountIDS loaded)" );
        return accountIDs;
    }
    
    
    public static Map<Integer, List<Integer>> searchByPrimaryContactLastName(String lastName_, boolean partialMatch, ArrayList<Integer> energyCompanyIDList) {
        if (lastName_ == null || lastName_.length() == 0) return null;
        if (energyCompanyIDList == null || energyCompanyIDList.size() == 0) return null;
        
        //Contains EnergyCompanyID<Integer> to accountID array <int[]> objects.
        Map<Integer, List<Integer>> ecToAccountIDMap = new HashMap<Integer, List<Integer>>();

        Date timerStart = new Date();
        String lastName = lastName_.trim();
        String firstName = null;
        int commaIndex = lastName_.indexOf(",");
        if( commaIndex > 0 )
        {
            firstName = lastName_.substring(commaIndex+1).trim();
            lastName = lastName_.substring(0, commaIndex).trim();
        }
        String sql = "SELECT DISTINCT map.energycompanyID, acct.AccountID " +
                    " , contlastname, contfirstname " + //these are for the order by functionality
                    " FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + 
                    Customer.TABLE_NAME + " cust, " +  Contact.TABLE_NAME + " cont " +
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND cust.primarycontactid = cont.contactid " +
                    " AND UPPER(cont.contlastname) like ?" ;
                    if (firstName != null && firstName.length() > 0)
                        sql += " AND UPPER(CONTFIRSTNAME) LIKE ? ";

                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size());
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++)
                             sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        sql += " ) ";
                    }
                    sql += " order by contlastname, contfirstname";
                    

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, lastName.toUpperCase()+(partialMatch? "%":""));
            if (firstName != null && firstName.length() > 0)
                pstmt.setString(2, firstName.toUpperCase() + "%");
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
            
            while(rset.next())
            {
                Integer energyCompanyID = new Integer(rset.getInt(1));
                Integer accountID = new Integer(rset.getInt(2));
                ArrayList<Integer> accountIDs = (ArrayList<Integer>)ecToAccountIDMap.get(energyCompanyID);
                if (accountIDs == null){
                    accountIDs = new ArrayList<Integer>();
                    ecToAccountIDMap.put(energyCompanyID, accountIDs);
                }
                accountIDs.add(accountID);
                count++;
            }
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + lastName+ ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + lastName + "' Search (" + count + " AccountIDS loaded; EC=" + (energyCompanyIDList.size() == StarsDatabaseCache.getInstance().getAllEnergyCompanies().size()? "ALL" : energyCompanyIDList.toString()) + ")" );
        return ecToAccountIDMap;
    }
    
    public static int[] searchBySerialNumber(String serialNo, int energyCompanyID) {
			LMHardwareBase[] hardwares = LMHardwareBase.searchBySerialNumber( serialNo, energyCompanyID );
			if (hardwares.length == 0) return new int[0];

            int[] returnAcctIDs = null;
            String sql = "SELECT DISTINCT acct.AccountID " +
                        " FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + InventoryBase.TABLE_NAME + " inv " + 
                        " WHERE map.EnergyCompanyID = ?" + 
                        " AND map.AccountID = acct.AccountID " +
                        " AND acct.AccountID = inv.AccountID " +
                        " AND (inv.InventoryID = ?" ;
                        for (int i = 1; i < hardwares.length; i++)
                            sql += " OR inv.InventoryID = ?";
                        sql += ")";
            
            PreparedStatement pstmt = null;
            Connection conn = null;
            ResultSet rset = null;
            
            try {
                conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, energyCompanyID);
                for(int i = 0; i < hardwares.length; i++)
                    pstmt.setInt(i+2, hardwares[i].getInventoryID().intValue());

                rset = pstmt.executeQuery();
                
                ArrayList<Integer> accountIDs = new ArrayList<Integer>();
                while(rset.next())
                    accountIDs.add(rset.getInt(1));
                
                returnAcctIDs = new int[accountIDs.size()];
                for(int i = 0; i < accountIDs.size(); i++)
                    returnAcctIDs[i] = accountIDs.get(i).intValue();
            }
            catch (Exception e) {
                CTILogger.error( e.getMessage(), e );
            }
            finally
            {
            	SqlUtils.close(rset, pstmt, conn );
            }
            return returnAcctIDs;
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
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
    		}
    		catch (java.sql.SQLException e) {}
    	}
    	return customerAccts;
    }
    
    public static List<Object> searchByCompanyName(String searchName, List<Integer> energyCompanyIdList, boolean searchMembers) 
    {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ACCT.AccountId, CICB.CompanyName, MAP.EnergyCompanyId ");
        sql.append("FROM ECToAccountMapping MAP, CustomerAccount ACCT, CICustomerBase CICB ");
        sql.append("WHERE MAP.EnergyCompanyID IN (",energyCompanyIdList,") ");
        sql.append("AND MAP.AccountId = ACCT.AccountId ");
        sql.append("AND ACCT.CustomerId = CICB.CustomerId "); 
        sql.append("AND UPPER(CICB.CompanyName) LIKE UPPER(?) ");
        sql.append("ORDER BY CICB.CompanyName ASC ");

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, searchName);
            
            rset = pstmt.executeQuery();
            
            ArrayList<Object> accountIds = new ArrayList<Object>();
            while(rset.next()) {
            	int accountId = rset.getInt(1);
            	int energyCompanyId = rset.getInt(3);
            	LiteStarsEnergyCompany liteSarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyId);
            	
            	 if (searchMembers)
            		 accountIds.add(new Pair<Integer,LiteStarsEnergyCompany>(accountId, liteSarsEC) );
                 else
                	 accountIds.add(accountId);
            }
            return accountIds;
            
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally
        {
        	SqlUtils.close(rset, pstmt, conn );
        }
        return null;
    }
    
    public static int[] searchByCustomerNumber(String searchNumber, int energyCompanyID) {
        int[] returnAcctIDs = null;
        String sql = "SELECT DISTINCT acct.AccountID " +
                    " FROM ECToAccountMapping map, " + TABLE_NAME + " acct, " + Customer.TABLE_NAME + " cust " + 
                    " WHERE map.EnergyCompanyID = ?" + 
                    " AND map.AccountID = acct.AccountID " +
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND UPPER(cust.CustomerNumber) LIKE UPPER(?)";

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, energyCompanyID);
            pstmt.setString(2, searchNumber);

            rset = pstmt.executeQuery();
            
            ArrayList<Integer> accountIDs = new ArrayList<Integer>();
            while(rset.next())
                accountIDs.add(rset.getInt(1));
            
            returnAcctIDs = new int[accountIDs.size()];
            for(int i = 0; i < accountIDs.size(); i++)
                returnAcctIDs[i] = accountIDs.get(i).intValue();
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            SqlUtils.close(rset, pstmt, conn );
        }
        return returnAcctIDs;
    }
    
    public static List<Integer> searchByCustomerAltTrackingNumber(String searchNumber, List<Integer> energyCompanyIdList) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ACCT.AccountId, CUST.AltTrackNum ");
        sql.append("FROM ECToAccountMapping MAP, CustomerAccount ACCT, Customer CUST ");
        sql.append("WHERE MAP.EnergyCompanyId IN (",energyCompanyIdList,") ");
        sql.append("AND MAP.AccountId = ACCT.AccountId ");
        sql.append("AND ACCT.CustomerId = CUST.CustomerId "); 
        sql.append("AND UPPER(CUST.AltTrackNum) LIKE UPPER(?) ");
        sql.append("ORDER BY CUST.AltTrackNum ASC ");

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, searchNumber);

            rset = pstmt.executeQuery();
            
            ArrayList<Integer> accountIds = new ArrayList<Integer>();
            while(rset.next())
                accountIds.add(rset.getInt(1));

            return accountIds;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            SqlUtils.close(rset, pstmt, conn );
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
			stmt.setInt( 1, energyCompanyID );
			stmt.setString( 2, address );
			rset = stmt.executeQuery();
	        
			ArrayList<Integer> acctIDList = new ArrayList<Integer>();
			while (rset.next())
				acctIDList.add( new Integer(rset.getInt(1)) );
	        
			int[] accountIDs = new int[ acctIDList.size() ];
			for (int i = 0; i < accountIDs.length; i++)
				accountIDs[i] = acctIDList.get(i).intValue();
			
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
	
	public static List<Integer> searchBySiteNumber(String mapNo, List<Integer> energyCompanyIdList) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT acct.AccountID FROM ");
		sql.append("AccountSite site, CustomerAccount acct, ECToAccountMapping map ");
		sql.append("WHERE UPPER(site.SiteNumber) LIKE UPPER(?) ");
		sql.append("AND site.AccountSiteID = acct.AccountSiteID ");
		sql.append("AND acct.AccountID = map.AccountID ");
		sql.append("AND map.EnergyCompanyID IN (",energyCompanyIdList,") ");
		sql.append("ORDER BY site.SiteNumber ASC");
    	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
			stmt = conn.prepareStatement( sql.toString() );
			stmt.setString( 1, mapNo);
			rset = stmt.executeQuery();
			
			ArrayList<Integer> acctIdList = new ArrayList<Integer>();
			while (rset.next())
				acctIdList.add( new Integer(rset.getInt(1)) );
	        return acctIdList;
		}
		catch (java.sql.SQLException e) {
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
        CustomerAccount account = null;
        String sql = "SELECT AccountID, AccountSiteID, AccountNumber, CustomerID, BillingAddressID, AccountNotes " +
                    " FROM " + TABLE_NAME + 
                    " WHERE AccountID = ?"; 
            
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountID.intValue());
    
            rset = pstmt.executeQuery();
            
            while(rset.next())
            {
                account = new CustomerAccount();
                account.setAccountID( new Integer(rset.getInt(1)));
                account.setAccountSiteID( new Integer(rset.getInt(2)) );
                account.setAccountNumber( rset.getString(3));
                account.setCustomerID( new Integer(rset.getInt(4)) );
                account.setBillingAddressID( new Integer(rset.getInt(5)) );
                account.setAccountNotes( rset.getString(6) );
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally
        {
        	SqlUtils.close(rset, pstmt, conn );
        }
        return account;
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getAccountID() == null)
    		setAccountID( getNextAccountID() );
    		
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
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
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
    
    /*
     * For use in inventory filtering
     */
    public static HashMap<Integer, Integer> getAccountIDsFromZipCode(String zipCode) 
    {
        if (zipCode == null || zipCode.length() == 0) return null;
        
        HashMap<Integer, Integer> accounts = new HashMap<Integer, Integer>();
        
        Date timerStart = new Date();
        
        String sql = "SELECT AccountID FROM " + TABLE_NAME + ", " + AccountSite.TABLE_NAME + 
                ", " + Address.TABLE_NAME + " WHERE " + CustomerAccount.TABLE_NAME + ".AccountSiteID = " + AccountSite.TABLE_NAME +
                ".AccountSiteID AND " + AccountSite.TABLE_NAME + ".StreetAddressID = " + Address.TABLE_NAME + ".AddressID AND " +
                "ZipCode LIKE ?";   

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        
        try 
        {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, zipCode + "%");
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After accountIDFromZipCode execute" );
  
            while(rset.next())
            {
                Integer newID = new Integer(rset.getInt(1));
                accounts.put(newID, newID);
                count++;
            }
        }
        catch( Exception e )
        {
            CTILogger.error( "Error retrieving accounts with zipcode " + zipCode + ": " + e.getMessage(), e );
        }
        finally 
        {
            try 
            {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + zipCode + "' zip to account load (" + count + " AccountIDS loaded...)" );
        return accounts;
    }
    
    /*
     * For use in inventory filtering
     */
    public static HashMap<Integer, Integer> getAccountIDsFromCustomerType(int ciCustType) 
    {
        HashMap<Integer, Integer> accounts = new HashMap<Integer, Integer>();
        
        Date timerStart = new Date();
        
        String sql = "SELECT AccountID FROM " + TABLE_NAME + ", " + CICustomerBase.TABLE_NAME + 
        " WHERE " + CustomerAccount.TABLE_NAME + ".CustomerID = " + CICustomerBase.TABLE_NAME +
        ".CustomerID AND CICustType = ?"; 

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try 
        {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt( 1, ciCustType);
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After accountIDFromCICustomerType execute" );
  
            while(rset.next())
            {
                Integer newID = new Integer(rset.getInt(1));
                accounts.put(newID, newID);
                count++;
            }
        }
        catch( Exception e )
        {
            CTILogger.error( "Error retrieving accounts with ciCustomerType " + ciCustType + ": " + e.getMessage(), e );
        }
        finally 
        {
            try 
            {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + ciCustType + "' cicusttype to account load (" + count + " AccountIDS loaded...)" );
        return accounts;
    }
    
    /*
     * For use in inventory filtering
     */
    public static HashMap<Integer, Integer> getAccountIDsNonCommercial(int ecID) 
    {
        HashMap<Integer, Integer> accounts = new HashMap<Integer, Integer>();
        
        Date timerStart = new Date();
        
        String sql;
        if(ecID > -1)
        {
            sql = "SELECT ecAc.AccountID FROM " + TABLE_NAME + " ca, " + Customer.TABLE_NAME + " cust, ECToAccountMapping ecAc" +
            " WHERE ecAc.AccountID = ca.AccountID AND ecAc.EnergyCompanyID = ? AND ca.CustomerID = cust" +
            ".CustomerID AND cust.CustomerTypeID = " + CustomerTypes.CUSTOMER_RESIDENTIAL;
        }
        else
        {
            sql = "SELECT AccountID FROM " + TABLE_NAME + ", " + Customer.TABLE_NAME +
            " WHERE " + CustomerAccount.TABLE_NAME + ".CustomerID = " + Customer.TABLE_NAME +
            ".CustomerID AND " + Customer.TABLE_NAME + ".CustomerTypeID = " + CustomerTypes.CUSTOMER_RESIDENTIAL;
        }
       
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try 
        {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            if(ecID > -1)
                pstmt.setInt( 1, ecID);
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After accountIDFromCICustomerType execute" );
  
            while(rset.next())
            {
                Integer newID = new Integer(rset.getInt(1));
                accounts.put(newID, newID);
                count++;
            }
        }
        catch( Exception e )
        {
            CTILogger.error( "Error retrieving accounts with res customer type: " + e.getMessage(), e );
        }
        finally 
        {
            try 
            {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for res ci customer type to account load (" + count + " AccountIDS loaded...)" );
        return accounts;
    }
}
package com.cannontech.stars.web;

import com.cannontech.database.data.web.User;
import com.cannontech.database.db.stars.customer.CustomerAccount;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsUser extends User {
	
	int energyCompanyID = 0;
	private CustomerAccount[] accounts = null;
	private Hashtable attributes = new Hashtable();
	
	public int getEnergyCompanyID() {
		return energyCompanyID;
	}
	
	public CustomerAccount[] getCustomerAccounts() {
		return accounts;
	}
	
	public Object getAttribute(Object name) {
		return attributes.get( name );
	}
	
	public Enumeration getAttributeNames() {
		return attributes.keys();
	}
	
	public void setAttribute(Object name, Object value) {
		attributes.put( name, value );
	}
	
	public void removeAttribute(Object name) {
		attributes.remove( name );
	}
	
	public synchronized Integer getIncAttribute(Object name) {
		Integer value = (Integer) attributes.get( name );
		if (value != null) {
			Integer newValue = new Integer( value.intValue()+1 );
			attributes.put( name, newValue );
		}
		return value;
	}
	
	public void retrieve() throws SQLException {
		String sql = null;
		Connection conn = getDbConnection();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		try {
			sql = "SELECT map.EnergyCompanyID, acct.AccountID, acct.AccountSiteID, acct.AccountNumber, acct.CustomerID, acct.BillingAddressID, acct.AccountNotes "
				+ "FROM CustomerAccount acct, CustomerBase cust, CustomerContact cont, ECToAccountMapping map "
				+ "WHERE cont.LogInID = ? AND cust.PrimaryContactID = cont.ContactID AND acct.CustomerID = cust.CustomerID AND acct.AccountID = map.AccountID";
					   
			pstmt = conn.prepareStatement( sql );
			pstmt.setLong( 1, getId() );
			rset = pstmt.executeQuery();			
			ArrayList accountList = new ArrayList();
			
			while (rset.next()) {
				energyCompanyID = rset.getInt("EnergyCompanyID");
				
				CustomerAccount account = new CustomerAccount();
				account.setAccountID( new Integer(rset.getInt("AccountID")) );
				account.setAccountSiteID( new Integer(rset.getInt("AccountSiteID")) );
				account.setAccountNumber( rset.getString("AccountNumber") );
				account.setCustomerID( new Integer(rset.getInt("CustomerID")) );
				account.setBillingAddressID( new Integer(rset.getInt("BillingAddressID")) );
				account.setAccountNotes( rset.getString("AccountNotes") );
				
				accountList.add( account );
			}
			
			accounts = new CustomerAccount[ accountList.size() ];
			accountList.toArray( accounts );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rset != null) rset.close();
				if (pstmt != null) pstmt.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

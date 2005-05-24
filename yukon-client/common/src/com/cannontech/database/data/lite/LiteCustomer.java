package com.cannontech.database.data.lite;

import com.cannontech.common.version.VersionTools;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.db.customer.Customer;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteCustomer extends LiteBase {
	
	private int primaryContactID = 0;
	private int customerTypeID = com.cannontech.database.data.customer.CustomerTypes.INVALID;
	private String timeZone = null;
	
	//non-persistent data, 
	//contains com.cannontech.database.data.lite.LiteContact
	private java.util.Vector additionalContacts = null;
	
	// contains int ,Used for residential customers only
	private java.util.Vector accountIDs = null;
	private int energyCompanyID = -1;
	
	public LiteCustomer() {
		super();
		setLiteType( LiteTypes.CUSTOMER );
	}
	
	public LiteCustomer(int customerID) {
		super();
		setCustomerID( customerID );
		setLiteType( LiteTypes.CUSTOMER );
	}
	
	public int getCustomerID() {
		return getLiteID();
	}
	
	public void setCustomerID(int customerID) {
		setLiteID( customerID );
	}
	
	public void retrieve(String dbAlias) {
		java.sql.Connection conn = null;
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( dbAlias );
			
			com.cannontech.database.SqlStatement stat = new com.cannontech.database.SqlStatement(
					"SELECT PrimaryContactID, CustomerTypeID, TimeZone" +
					" FROM " + Customer.TABLE_NAME +
					" WHERE CustomerID = " + getCustomerID(),
					conn );
			
			stat.execute();
			
			if( stat.getRowCount() <= 0 )
				throw new IllegalStateException("Unable to find the Customer with CustomerID = " + getCustomerID() );
			
			Object[] objs = stat.getRow(0);
			
			setPrimaryContactID( ((java.math.BigDecimal) objs[0]).intValue() );
			setCustomerTypeID( ((java.math.BigDecimal) objs[1]).intValue() );
			setTimeZone( objs[2].toString() );
			
			stat = new com.cannontech.database.SqlStatement(
					"SELECT ca.ContactID " + 
					"FROM CustomerAdditionalContact ca, " + 
					Customer.TABLE_NAME + " c " +
					"WHERE " +
					"c.CustomerID=" + getCustomerID() + " " +
					"AND c.CustomerID=ca.CustomerID " +
					"ORDER BY ca.Ordering",
					conn );
			
			stat.execute();
			
			getAdditionalContacts().removeAllElements();
			
			for( int i = 0; i < stat.getRowCount(); i++ ) {
				//add the LiteContact to this Customer
				getAdditionalContacts().add(
					ContactFuncs.getContact(((java.math.BigDecimal) stat.getRow(i)[0]).intValue()) );

			}

			if (VersionTools.starsExists()) {
				stat = new com.cannontech.database.SqlStatement(
						"SELECT acct.AccountID, map.EnergyCompanyID " +
						"FROM CustomerAccount acct, ECToAccountMapping map " +
						"WHERE acct.CustomerID=" + getCustomerID() +
						" AND acct.AccountID = map.AccountID",
						conn );
				
				stat.execute();
				
				getAccountIDs().removeAllElements();
				
				for (int i = 0; i < stat.getRowCount(); i++) {
					getAccountIDs().add(
						new Integer(((java.math.BigDecimal) stat.getRow(i)[0]).intValue()) );
					energyCompanyID = ((java.math.BigDecimal) stat.getRow(i)[1]).intValue();
				}
			}
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}

	/**
	 * Returns the customerTypeID.
	 * @return int
	 */
	public int getCustomerTypeID() {
		return customerTypeID;
	}

	/**
	 * Returns the primaryContactID.
	 * @return int
	 */
	public int getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Sets the additionalContacts.
	 * @param additionalContacts The additionalContacts to set
	 */
	public void setAdditionalContacts(java.util.Vector additionalContacts) {
		this.additionalContacts = additionalContacts;
	}

	/**
	 * Sets the customerTypeID.
	 * @param customerTypeID The customerTypeID to set
	 */
	public void setCustomerTypeID(int customerTypeID) {
		this.customerTypeID = customerTypeID;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(int primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * Returns the timeZone.
	 * @return String
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the timeZone.
	 * @param timeZone The timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	/**
	 * Returns the additionalContacts.
	 * @return java.util.Vector
	 */
	public java.util.Vector getAdditionalContacts() {
		if (additionalContacts == null)
			additionalContacts = new java.util.Vector(10);
		return additionalContacts;
	}
	
	/**
	 * Returns the customer account IDs
	 * @return java.util.Vector
	 */
	public java.util.Vector getAccountIDs() {
		if (accountIDs == null)
			accountIDs = new java.util.Vector();
		return accountIDs;
	}
	
	/**
	 * Used for residential customers only. A residential customer
	 * should belong to only one energy company.
	 */
	public int getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * @param vector
	 */
	public void setAccountIDs(java.util.Vector vector)
	{
		accountIDs = vector;
	}

	/**
	 * @param i
	 */
	public void setEnergyCompanyID(int i)
	{
		energyCompanyID = i;
	}

}

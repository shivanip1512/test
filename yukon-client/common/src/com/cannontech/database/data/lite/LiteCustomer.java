package com.cannontech.database.data.lite;

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
		try {
			com.cannontech.database.SqlStatement stat = new com.cannontech.database.SqlStatement(
					"SELECT PrimaryContactID, CustomerTypeID, TimeZone" +
					" FROM " + Customer.TABLE_NAME +
					" WHERE CustomerID = " + getCustomerID(),
					dbAlias );
			
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
					"ORDER BY c.CustomerID",
					dbAlias );
			
			stat.execute();
			
			getAdditionalContacts().removeAllElements();
			
			for( int i = 0; i < stat.getRowCount(); i++ ) {
				//add the LiteContact to this CICustomer
				getAdditionalContacts().add(
					ContactFuncs.getContact(((java.math.BigDecimal) stat.getRow(i)[0]).intValue()) );

			}
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
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

}

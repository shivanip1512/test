package com.cannontech.database.db.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CustomerContact extends DBPersistent {
	
	private com.cannontech.database.db.customer.CustomerContact customerContact = null;
	private com.cannontech.database.db.stars.customer.CustomerBase customerBase = null;
	
	public CustomerContact(com.cannontech.database.db.customer.CustomerContact contact) {
		customerContact = contact;
	}
	
	public CustomerContact(com.cannontech.database.db.customer.CustomerContact contact,
							com.cannontech.database.db.stars.customer.CustomerBase customer) {
		customerContact = contact;
		customerBase = customer;
	}
	
	public CustomerContact() {
	}

	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		if (customerContact != null)
			customerContact.setDbConnection( conn );
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (customerContact != null) {
			if (customerContact.getContactID() == null)
				customerContact.setContactID( customerContact.getNextContactID2() );
			customerContact.add();
			
			if (customerBase != null) {
				Object[] addValues = {
					customerBase.getCustomerID(), customerContact.getContactID()
				};
				add( "CustomerAdditionalContact", addValues );
			}
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		if (customerContact != null) {
			delete( "CustomerAdditionalContact", "ContactID", customerContact.getContactID() );
			
			delete( com.cannontech.database.db.customer.CustomerContact.TABLE_NAME, "ContactID", customerContact.getContactID() );
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		if (customerContact != null)
			customerContact.retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		if (customerContact != null) {
			Object setValues[] = {
							customerContact.getContFirstName(),
							customerContact.getContLastName(),
							customerContact.getContPhone1(),
							customerContact.getContPhone2(),
							customerContact.getLocationID(),
							customerContact.getLogInID() };
			Object constraintValues[] = { customerContact.getContactID() };
			update( customerContact.TABLE_NAME, customerContact.SETTER_COLUMNS, setValues, customerContact.CONSTRAINT_COLUMNS, constraintValues );
		}
	}
	
	public static int[] searchByPhoneNumber(String phoneNo) {
		String sql = "SELECT ContactID FROM " + com.cannontech.database.db.customer.CustomerContact.TABLE_NAME
				   + " WHERE ContPhone1 = '" + phoneNo + "' OR ContPhone2 = '" + phoneNo + "'";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			int[] contactIDs = new int[ stmt.getRowCount() ];
			
			for (int i = 0; i < contactIDs.length; i++)
				contactIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
				
			return contactIDs;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static int[] searchByLastName(String lastName) {
		String sql = "SELECT ContactID FROM " + com.cannontech.database.db.customer.CustomerContact.TABLE_NAME
				   + " WHERE UPPER(ContLastName) = UPPER('" + lastName + "')";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			int[] contactIDs = new int[ stmt.getRowCount() ];
			
			for (int i = 0; i < contactIDs.length; i++)
				contactIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
				
			return contactIDs;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Returns the customerContact.
	 * @return com.cannontech.database.db.customer.CustomerContact
	 */
	public com.cannontech.database.db.customer.CustomerContact getCustomerContact() {
		return customerContact;
	}

	/**
	 * Sets the customerContact.
	 * @param customerContact The customerContact to set
	 */
	public void setCustomerContact(
		com.cannontech.database.db.customer.CustomerContact customerContact) {
		this.customerContact = customerContact;
	}

	/**
	 * Returns the customerBase.
	 * @return com.cannontech.database.db.stars.customer.CustomerBase
	 */
	public com.cannontech.database.db.stars.customer.CustomerBase getCustomerBase() {
		return customerBase;
	}

	/**
	 * Sets the customerBase.
	 * @param customerBase The customerBase to set
	 */
	public void setCustomerBase(com.cannontech.database.db.stars.customer.CustomerBase customerBase) {
		this.customerBase = customerBase;
	}

}

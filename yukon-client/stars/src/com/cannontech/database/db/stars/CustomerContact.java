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
	
	public CustomerContact(com.cannontech.database.db.customer.CustomerContact contact) {
		customerContact = contact;
	}

	public void setDbConnection(java.sql.Connection conn) {
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
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		if (customerContact != null)
			customerContact.delete();
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

}

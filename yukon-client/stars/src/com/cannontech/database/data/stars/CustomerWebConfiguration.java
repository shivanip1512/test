package com.cannontech.database.data.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: CustomerWebConfiguration.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 13, 2002 4:06:07 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CustomerWebConfiguration extends DBPersistent {
	
	private com.cannontech.database.db.stars.CustomerWebConfiguration customerWebConfiguration = null;
	
	public void setConfigurationID(Integer newID) {
		getCustomerWebConfiguration().setConfigurationID( newID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getCustomerWebConfiguration().setDbConnection(conn);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getCustomerWebConfiguration().add();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		getCustomerWebConfiguration().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getCustomerWebConfiguration().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getCustomerWebConfiguration().update();
	}

	/**
	 * Returns the customerWebConfiguration.
	 * @return com.cannontech.database.db.stars.CustomerWebConfiguration
	 */
	public com.cannontech.database.db.stars.CustomerWebConfiguration getCustomerWebConfiguration() {
		if (customerWebConfiguration == null)
			customerWebConfiguration = new com.cannontech.database.db.stars.CustomerWebConfiguration();
		return customerWebConfiguration;
	}

	/**
	 * Sets the customerWebConfiguration.
	 * @param customerWebConfiguration The customerWebConfiguration to set
	 */
	public void setCustomerWebConfiguration(
		com
			.cannontech
			.database
			.db
			.stars
			.CustomerWebConfiguration customerWebConfiguration) {
		this.customerWebConfiguration = customerWebConfiguration;
	}

}

package com.cannontech.database.data.stars;

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
public class CustomerFAQ extends DBPersistent {
	
	private com.cannontech.database.db.stars.CustomerFAQ customerFAQ = null;
	private Integer energyCompanyID = null;
	
	public void setQuestionID(Integer questionID) {
		getCustomerFAQ().setQuestionID( questionID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getCustomerFAQ().setDbConnection( conn );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    	
    	getCustomerFAQ().add();
        
    	// Add to mapping table
    	Object[] addValues = {
    		getEnergyCompanyID(),
    		getCustomerFAQ().getQuestionID(),
    		com.cannontech.database.db.stars.CustomerFAQ.TABLE_NAME
    	};
    	add("ECToGenericMapping", addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
    	// delete from mapping table
    	String[] constraintColumns = {
    		"ItemID", "MappingCategory"
    	};
    	Object[] constraintValues = {
    		getCustomerFAQ().getQuestionID(),
    		com.cannontech.database.db.stars.CustomerFAQ.TABLE_NAME
    	};
    	delete("ECToGenericMapping", constraintColumns, constraintValues);
    	
    	getCustomerFAQ().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getCustomerFAQ().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getCustomerFAQ().update();
	}

	/**
	 * Returns the customerFAQ.
	 * @return com.cannontech.database.db.stars.CustomerFAQ
	 */
	public com.cannontech.database.db.stars.CustomerFAQ getCustomerFAQ() {
		if (customerFAQ == null)
			customerFAQ = new com.cannontech.database.db.stars.CustomerFAQ();
		return customerFAQ;
	}

	/**
	 * Sets the customerFAQ.
	 * @param customerFAQ The customerFAQ to set
	 */
	public void setCustomerFAQ(com.cannontech.database.db.stars.CustomerFAQ customerFAQ) {
		this.customerFAQ = customerFAQ;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

}

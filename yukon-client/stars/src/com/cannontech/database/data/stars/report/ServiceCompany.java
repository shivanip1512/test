package com.cannontech.database.data.stars.report;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ServiceCompany extends DBPersistent {

    private com.cannontech.database.db.stars.report.ServiceCompany serviceCompany = null;
    private com.cannontech.database.db.customer.Address address = null;
    private com.cannontech.database.db.contact.Contact primaryContact = null;
    
    private com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase = null;

    public ServiceCompany() {
        super();
    }

    public void setCompanyID(Integer newID) {
        getServiceCompany().setCompanyID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getServiceCompany().setDbConnection(conn);
        getAddress().setDbConnection(conn);
        getPrimaryContact().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
    	// delete from mapping table
    	delete("ECToGenericMapping", "ItemID", getServiceCompany().getCompanyID());
    	
    	getAddress().delete();
    	getPrimaryContact().delete();
        getServiceCompany().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getEnergyCompanyBase() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyBase() must be called before this function");
    		
        getServiceCompany().add();
        getAddress().add();
        getPrimaryContact().add();
        
        if (getEnergyCompanyBase().getEnergyCompany() != null) {
        	// Add to mapping table
        	Object[] addValues = {
        		getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
        		getServiceCompany().getCompanyID(),
        		getServiceCompany().TABLE_NAME
        	};
        	add("ECToGenericMapping", addValues);
        }
    }

    public void update() throws java.sql.SQLException {
        getServiceCompany().update();
        getAddress().update();
        getPrimaryContact().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getServiceCompany().retrieve();
        
        getAddress().setAddressID( getServiceCompany().getAddressID() );
        getAddress().retrieve();
        
        getPrimaryContact().setContactID( getServiceCompany().getPrimaryContactID() );
        getPrimaryContact().retrieve();
    }
	/**
	 * Returns the serviceCompany.
	 * @return com.cannontech.database.db.stars.report.ServiceCompany
	 */
	public com.cannontech.database.db.stars.report.ServiceCompany getServiceCompany() {
		if (serviceCompany == null)
			serviceCompany = new com.cannontech.database.db.stars.report.ServiceCompany();
		return serviceCompany;
	}

	/**
	 * Sets the serviceCompany.
	 * @param serviceCompany The serviceCompany to set
	 */
	public void setServiceCompany(
		com.cannontech.database.db.stars.report.ServiceCompany serviceCompany) {
		this.serviceCompany = serviceCompany;
	}

	/**
	 * Returns the address.
	 * @return com.cannontech.database.db.customer.CustomerAddress
	 */
	public com.cannontech.database.db.customer.Address getAddress() {
		if (address == null)
			address = new com.cannontech.database.db.customer.Address();
		return address;
	}

	/**
	 * Returns the primaryContact.
	 * @return com.cannontech.database.db.customer.CustomerContact
	 */
	public com.cannontech.database.db.contact.Contact getPrimaryContact() {
		if (primaryContact == null)
			primaryContact = new com.cannontech.database.db.contact.Contact();
		return primaryContact;
	}

	/**
	 * Sets the address.
	 * @param address The address to set
	 */
	public void setAddress(
		com.cannontech.database.db.customer.Address address) {
		this.address = address;
	}

	/**
	 * Sets the primaryContact.
	 * @param primaryContact The primaryContact to set
	 */
	public void setPrimaryContact(
		com.cannontech.database.db.contact.Contact primaryContact) {
		this.primaryContact = primaryContact;
	}

	/**
	 * Returns the energyCompanyBase.
	 * @return com.cannontech.database.data.company.EnergyCompanyBase
	 */
	public com.cannontech.database.data.company.EnergyCompanyBase getEnergyCompanyBase() {
		return energyCompanyBase;
	}

	/**
	 * Sets the energyCompanyBase.
	 * @param energyCompanyBase The energyCompanyBase to set
	 */
	public void setEnergyCompanyBase(
		com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase) {
		this.energyCompanyBase = energyCompanyBase;
	}

}
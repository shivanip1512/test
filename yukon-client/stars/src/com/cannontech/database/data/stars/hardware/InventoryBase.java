package com.cannontech.database.data.stars.hardware;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class InventoryBase extends DBPersistent {

    private com.cannontech.database.db.stars.hardware.InventoryBase inventoryBase = null;
    private com.cannontech.database.db.stars.report.ServiceCompany installationCompany = null;
    private com.cannontech.database.db.stars.CustomerListEntry category = null;
    private com.cannontech.database.db.stars.CustomerListEntry voltage = null;

    private com.cannontech.database.data.stars.customer.CustomerAccount customerAccount = null;
    private Integer energyCompanyID = null;

    public InventoryBase() {
        super();
    }

    public void setInventoryID(Integer newID) {
        getInventoryBase().setInventoryID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getInventoryBase().setDbConnection(conn);
        getInstallationCompany().setDbConnection(conn);
        getCategory().setDbConnection(conn);
        getVoltage().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from the mapping table
        delete( "ECToInventoryMapping", "InventoryID", getInventoryBase().getInventoryID() );

        getInventoryBase().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException( "setEnergyCompanyID() must be called before this function" );
    		
        getInventoryBase().add();

        // add to the mapping table
        Object[] addValues = {
            getEnergyCompanyID(),
            getInventoryBase().getInventoryID()
        };
        add( "ECToInventoryMapping", addValues );
    }

    public void update() throws java.sql.SQLException {
        getInventoryBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getInventoryBase().retrieve();

/*
 * Commented out since cache is used now
 * 
        getInstallationCompany().setCompanyID( getInventoryBase().getInstallationCompanyID() );
        getInstallationCompany().retrieve();
        
        getCategory().setEntryID( getInventoryBase().getCategoryID() );
        getCategory().retrieve();
        
        getVoltage().setEntryID( getInventoryBase().getVoltageID() );
        getVoltage().retrieve();
*/
    }

    public com.cannontech.database.db.stars.hardware.InventoryBase getInventoryBase() {
        if (inventoryBase == null)
            inventoryBase = new com.cannontech.database.db.stars.hardware.InventoryBase();
        return inventoryBase;
    }

    public void setInventoryBase(com.cannontech.database.db.stars.hardware.InventoryBase newInventoryBase) {
        inventoryBase = newInventoryBase;
    }

    public com.cannontech.database.data.stars.customer.CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.database.data.stars.customer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.database.db.stars.report.ServiceCompany getInstallationCompany() {
        if (installationCompany == null)
            installationCompany = new com.cannontech.database.db.stars.report.ServiceCompany();
        return installationCompany;
    }

    public void setInstallationCompany(com.cannontech.database.db.stars.report.ServiceCompany newInstallationCompany) {
        installationCompany = newInstallationCompany;
    }
	/**
	 * Returns the category.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getCategory() {
		if (category == null)
			category = new com.cannontech.database.db.stars.CustomerListEntry();
		return category;
	}

	/**
	 * Returns the voltage.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getVoltage() {
		if (voltage == null)
			voltage = new com.cannontech.database.db.stars.CustomerListEntry();
		return voltage;
	}

	/**
	 * Sets the category.
	 * @param category The category to set
	 */
	public void setCategory(
		com.cannontech.database.db.stars.CustomerListEntry category) {
		this.category = category;
	}

	/**
	 * Sets the voltage.
	 * @param voltage The voltage to set
	 */
	public void setVoltage(
		com.cannontech.database.db.stars.CustomerListEntry voltage) {
		this.voltage = voltage;
	}

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}
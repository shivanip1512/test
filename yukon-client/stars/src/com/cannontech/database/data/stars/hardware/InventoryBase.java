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
    }
    
    /**
     * This method is to be called from the delete() method of all subclasses of InventoryBase (e.g. com.cannontech.database.data.stars.hardware.LMHardwareBase)
     */
    public void deleteInventoryBase() throws java.sql.SQLException {
        delete( "ECToInventoryMapping", "InventoryID", getInventoryBase().getInventoryID() );
        getInventoryBase().delete();
    }

    public void delete() throws java.sql.SQLException {
    	// Call customized delete method from all subclasses of InventoryBase, now there is only LMHardwareBase
    	LMHardwareBase hw = new LMHardwareBase();
    	hw.setInventoryID( getInventoryBase().getInventoryID() );
    	hw.setDbConnection( getDbConnection() );
    	hw.deleteLMHardwareBase();
    	
    	LMThermostatSeason[] thermSeasons = LMThermostatSeason.getAllLMThermostatSeasons( getInventoryBase().getInventoryID() );
    	if (thermSeasons != null) {
	    	for (int i = 0; i < thermSeasons.length; i++) {
	    		thermSeasons[i].setDbConnection( getDbConnection() );
	    		thermSeasons[i].delete();
	    	}
    	}
    	
    	com.cannontech.database.db.stars.event.LMThermostatManualEvent thermOption =
    			new com.cannontech.database.db.stars.event.LMThermostatManualEvent();
    	thermOption.setInventoryID( getInventoryBase().getInventoryID() );
    	thermOption.setDbConnection( getDbConnection() );
    	thermOption.delete();
    	
    	deleteInventoryBase();
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
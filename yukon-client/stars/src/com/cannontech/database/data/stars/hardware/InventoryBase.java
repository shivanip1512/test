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
     * When deleteInv = false, this method is used to remove inventory from a customer account.
     * This method should not be used alone with deleteInv = true, instead it will be invoked
     * from the delete() method of this class and all its subclasses with deleteInv set to true
     * (e.g. com.cannontech.database.data.stars.hardware.LMHardwareBase).
     */
    public void deleteInventoryBase(boolean deleteInv) throws java.sql.SQLException {
    	if (deleteInv) {
			// delete from LMHardwareEvent
			com.cannontech.database.data.stars.event.LMHardwareEvent.deleteAllLMHardwareEvents(
					getInventoryBase().getInventoryID(), getDbConnection() );
			
			delete( "ECToInventoryMapping", "InventoryID", getInventoryBase().getInventoryID() );
			getInventoryBase().delete();
    	}
    	else {
			getInventoryBase().setAccountID( new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID) );
			getInventoryBase().setRemoveDate( new java.util.Date() );
			getInventoryBase().update();
    	}
    }

    public void delete() throws java.sql.SQLException {
    	// Call customized delete method from all subclasses of InventoryBase, now there is only LMHardwareBase
    	LMHardwareBase hw = new LMHardwareBase();
    	hw.setInventoryID( getInventoryBase().getInventoryID() );
    	hw.setDbConnection( getDbConnection() );
    	hw.deleteLMHardwareBase( true );
    	
    	deleteInventoryBase( true );
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
package com.cannontech.stars.database.data.hardware;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.database.data.customer.CustomerAccount;
import com.cannontech.stars.database.data.event.EventInventory;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class InventoryBase extends DBPersistent {

    private com.cannontech.stars.database.db.hardware.InventoryBase inventoryBase = null;
    private com.cannontech.stars.database.db.report.ServiceCompany installationCompany = null;
    private com.cannontech.stars.database.data.customer.CustomerAccount customerAccount = null;
    private Integer energyCompanyID = null;

    public InventoryBase() {
        super();
    }

    public InventoryBase(Integer newID) {
        super();
        getInventoryBase().setInventoryID(newID);
    }
    
    public void setInventoryID(Integer newID) {
        getInventoryBase().setInventoryID(newID);
    }
    
    public void setAccountID(Integer accountID)
    {
    	getCustomerAccount().getCustomerAccount().setAccountID(accountID);
    	getInventoryBase().setAccountID(accountID);
    }
    
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getInventoryBase().setDbConnection(conn);
        getInstallationCompany().setDbConnection(conn);
    }
    
    public void deleteInventoryBase() throws java.sql.SQLException {
		// delete from LMHardwareEvent
		com.cannontech.stars.database.data.event.LMHardwareEvent.deleteAllLMHardwareEvents(
				getInventoryBase().getInventoryID() );
		
        ArrayList<EventInventory> stateChanges = EventInventory.retrieveEventInventories(getInventoryBase().getInventoryID());
        for(int i = 0; i < stateChanges.size(); i++)
        {
            try
            {
                Transaction.createTransaction( Transaction.DELETE, stateChanges.get(i) ).execute();
            }
            catch(TransactionException e)
            {
                CTILogger.error( e.getMessage(), e );
            }
        }
        
		delete( "ECToInventoryMapping", "InventoryID", getInventoryBase().getInventoryID() );
        delete( "InventoryToWarehouseMapping", "InventoryID", getInventoryBase().getInventoryID());
        delete( "LMHardwareToMeterMapping", "LMHardwareInventoryID", getInventoryBase().getInventoryID());
		
        getInventoryBase().delete();
    }

    public void delete() throws java.sql.SQLException {
    	// Call customized delete method from all subclasses of InventoryBase, now there is only LMHardwareBase
    	LMHardwareBase hw = new LMHardwareBase();
    	hw.setInventoryID( getInventoryBase().getInventoryID() );
    	hw.setDbConnection( getDbConnection() );
    	hw.deleteLMHardwareBase();
    	
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

    public com.cannontech.stars.database.db.hardware.InventoryBase getInventoryBase() {
        if (inventoryBase == null)
            inventoryBase = new com.cannontech.stars.database.db.hardware.InventoryBase();
        return inventoryBase;
    }

    public void setInventoryBase(com.cannontech.stars.database.db.hardware.InventoryBase newInventoryBase) {
        inventoryBase = newInventoryBase;
    }

    public com.cannontech.stars.database.data.customer.CustomerAccount getCustomerAccount() {
    	if(customerAccount == null)
    		customerAccount = new CustomerAccount();
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.stars.database.data.customer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.stars.database.db.report.ServiceCompany getInstallationCompany() {
        if (installationCompany == null)
            installationCompany = new com.cannontech.stars.database.db.report.ServiceCompany();
        return installationCompany;
    }

    public void setInstallationCompany(com.cannontech.stars.database.db.report.ServiceCompany newInstallationCompany) {
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
package com.cannontech.database.data.stars.customer;

import java.util.Vector;

import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CustomerAccount extends DBPersistent {

    private com.cannontech.database.db.stars.customer.CustomerAccount customerAccount = null;
    private com.cannontech.database.db.customer.Address billingAddress = null;

    private com.cannontech.database.data.stars.customer.AccountSite accountSite = null;
    private com.cannontech.database.data.customer.Customer customer = null;
    private Integer energyCompanyID = null;

    private Vector applianceVector = null;	// Vector of appliance IDs (Integer)
    private Vector inventoryVector = null;	// Vector of inventory IDs (Integer)

    public CustomerAccount() {
        super();
    }

    public void setAccountID(Integer newID) {
        getCustomerAccount().setAccountID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getCustomerAccount().setDbConnection(conn);
        getAccountSite().setDbConnection(conn);
        getBillingAddress().setDbConnection(conn);
        if (getCustomer() != null) getCustomer().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from the mapping table
        delete( "ECToAccountMapping", "AccountID", getCustomerAccount().getAccountID() );
    	
        com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
        		new com.cannontech.database.data.stars.hardware.LMHardwareBase();
		for (int i = 0; i < getInventoryVector().size(); i++) {
			Integer invID = (Integer) getInventoryVector().get(i);
			hw.setInventoryID( invID );
			hw.setDbConnection( getDbConnection() );
			
			// Don't delete hardware information from the database
			hw.deleteLMHardwareBase( false );
			
			com.cannontech.database.db.stars.hardware.InventoryBase invDB = hw.getInventoryBase();
			invDB.retrieve();
			invDB.setAccountID( new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID) );
			invDB.setRemoveDate( new java.util.Date() );
			invDB.update();
		}
		
        com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents(
            getCustomerAccount().getAccountID(), getDbConnection() );

    	// hardware configuration has already been deleted, so we just need to use the DB object here
		com.cannontech.database.data.stars.appliance.ApplianceBase app =
    			new com.cannontech.database.data.stars.appliance.ApplianceBase();
        for (int i = 0; i < getApplianceVector().size(); i++) {
        	Integer appID = (Integer) getApplianceVector().get(i);
        	app.setApplianceID( appID );
            app.setDbConnection( getDbConnection() );
            app.delete();
        }
    	
    	// Delete work orders
    	com.cannontech.database.data.stars.report.WorkOrderBase.deleteAllWorkOrders(
    			getCustomerAccount().getAccountID().intValue(), getDbConnection() );
    	
    	// Delete call reports
    	com.cannontech.database.data.stars.report.CallReportBase.deleteAllCallReports(
    			getCustomerAccount().getAccountID(), getDbConnection() );
        
        getCustomerAccount().delete();
        getAccountSite().delete();
        getBillingAddress().delete();
        if (getCustomer() != null) getCustomer().delete();

        setDbConnection(null);
    }

    public void add() throws java.sql.SQLException {
    	if (energyCompanyID == null)
    		throw new java.sql.SQLException( "setEnergyCompanyID() must be called before this function" );
    	
        getBillingAddress().add();
        
        getCustomerAccount().setBillingAddressID( getBillingAddress().getAddressID() );
        int addrID = getBillingAddress().getAddressID().intValue();
        
    	if (getCustomerAccount().getCustomerID().intValue() == 0) {
    		if (getCustomer() instanceof com.cannontech.database.data.customer.CICustomerBase)
    			((com.cannontech.database.data.customer.CICustomerBase)getCustomer()).getAddress().setAddressID( new Integer(++addrID) );
    		
    		getCustomer().add();
    		getCustomerAccount().setCustomerID( getCustomer().getCustomer().getCustomerID() );
    	}

		if (getCustomerAccount().getAccountSiteID().intValue() == com.cannontech.database.db.stars.customer.AccountSite.NONE_INT) {
			getAccountSite().getStreetAddress().setAddressID( new Integer(++addrID) );
        	getAccountSite().add();
        	getCustomerAccount().setAccountSiteID( getAccountSite().getAccountSite().getAccountSiteID() );
		}
		
        getCustomerAccount().add();

        // add to the mapping table
        Object[] addValues = {
            getEnergyCompanyID(),
            getCustomerAccount().getAccountID()
        };
        add( "ECToAccountMapping", addValues );

        setDbConnection(null);
    }

    public void update() throws java.sql.SQLException {
        getCustomerAccount().update();

		if (getCustomer() != null)
			getCustomer().update();
		
        getAccountSite().update();
        getBillingAddress().update();

        setDbConnection(null);
    }

    public void retrieve() throws java.sql.SQLException {
        getCustomerAccount().retrieve();

        getBillingAddress().setAddressID( getCustomerAccount().getBillingAddressID() );
        getBillingAddress().retrieve();

        getAccountSite().setAccountSiteID( getCustomerAccount().getAccountSiteID() );
        getAccountSite().retrieve();

        if (getCustomer() == null) {
			com.cannontech.database.db.customer.Customer customerDB = new com.cannontech.database.db.customer.Customer();
            customerDB.setCustomerID( getCustomerAccount().getCustomerID() );
            customerDB.setDbConnection( getDbConnection() );
            customerDB.retrieve();
            
            if (customerDB.getCustomerTypeID().intValue() == CustomerTypes.CUSTOMER_CI) {
            	customer = new com.cannontech.database.data.customer.CICustomerBase();
            	customer.setCustomer( customerDB );
            	customer.setCustomerID( customerDB.getCustomerID() );
            	customer.setDbConnection( getDbConnection() );
            	customer.retrieve();
            }
            else {
            	customer = new com.cannontech.database.data.customer.Customer();
            	customer.setCustomer( customerDB );
            	customer.setDbConnection( getDbConnection() );
            	customer.retrieve();
            }
        }
		
		setApplianceVector( com.cannontech.database.db.stars.appliance.ApplianceBase.getApplianceIDs(
				getCustomerAccount().getAccountID(), getDbConnection()) );
		
		setInventoryVector( com.cannontech.database.db.stars.hardware.InventoryBase.getInventoryIDs(
				getCustomerAccount().getAccountID(), getDbConnection()) );

        setDbConnection(null);
    }

    public com.cannontech.database.db.stars.customer.CustomerAccount getCustomerAccount() {
        if (customerAccount == null)
            customerAccount = new com.cannontech.database.db.stars.customer.CustomerAccount();
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.database.db.stars.customer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.database.data.stars.customer.AccountSite getAccountSite() {
        if (accountSite == null)
            accountSite = new com.cannontech.database.data.stars.customer.AccountSite();
        return accountSite;
    }

    public void setAccountSite(com.cannontech.database.data.stars.customer.AccountSite newAccountSite) {
        accountSite = newAccountSite;
    }

    public com.cannontech.database.db.customer.Address getBillingAddress() {
        if (billingAddress == null)
            billingAddress = new com.cannontech.database.db.customer.Address();
        return billingAddress;
    }

    public void setBillingAddress(com.cannontech.database.db.customer.Address newBillingAddress) {
        billingAddress = newBillingAddress;
    }

    public com.cannontech.database.data.customer.Customer getCustomer() {
        return customer;
    }

    public void setCustomer(com.cannontech.database.data.customer.Customer newCustomer) {
        customer = newCustomer;
    }

    public Vector getApplianceVector() {
        if (applianceVector == null)
            applianceVector = new Vector(3);
        return applianceVector;
    }

    public void setApplianceVector(Vector newApplianceVector) {
        applianceVector = newApplianceVector;
    }

    public Vector getInventoryVector() {
        if (inventoryVector == null)
            inventoryVector = new Vector(3);
        return inventoryVector;
    }

    public void setInventoryVector(Vector newInventoryVector) {
        inventoryVector = newInventoryVector;
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
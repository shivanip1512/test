package com.cannontech.database.data.stars.customer;

import java.util.Vector;

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
    private com.cannontech.database.db.customer.CustomerAddress billingAddress = null;
    private com.cannontech.database.db.customer.CustomerLogin customerLogin = null;		// Put it here temporarily

    private com.cannontech.database.data.stars.customer.AccountSite accountSite = null;
    private com.cannontech.database.data.stars.customer.CustomerBase customerBase = null;
    private com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase = null;

    private Vector applianceVector = null;
    private Vector inventoryVector = null;

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

        if (getCustomerBase() != null)
            getCustomerBase().setDbConnection(conn);
    }

    public static CustomerAccount searchByAccountNumber(Integer energyCompanyID, String accountNumber) {
        java.sql.Connection conn = null;
        CustomerAccount accountData = null;

        try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;

            com.cannontech.database.data.company.EnergyCompanyBase energyCompany =
                        new com.cannontech.database.data.company.EnergyCompanyBase();
            energyCompany.setEnergyCompanyID( energyCompanyID );
            energyCompany.setDbConnection( conn );
            energyCompany.retrieve();

            com.cannontech.database.db.stars.customer.CustomerAccount accountDB =
                        com.cannontech.database.db.stars.customer.CustomerAccount.searchByAccountNumber(
                            energyCompanyID, accountNumber, conn );
            if (accountDB == null) return null;

            accountData = new CustomerAccount();
            accountData.setAccountID( accountDB.getAccountID() );
            accountData.setDbConnection( conn );
            accountData.retrieve();
            accountData.setEnergyCompanyBase( energyCompany );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null) conn.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return accountData;
    }

    public void delete() throws java.sql.SQLException {
        getAccountSite().delete();
        getBillingAddress().delete();
        
    	getCustomerBase().delete();
    	//getCustomerLogin().delete();

        com.cannontech.database.db.stars.hardware.InventoryBase[] hws = com.cannontech.database.db.stars.hardware.InventoryBase.getAllInventories(
            getCustomerAccount().getAccountID(), getDbConnection() );
        com.cannontech.database.data.stars.hardware.InventoryBase hw = new com.cannontech.database.data.stars.hardware.InventoryBase();
        for (int i = 0; i < hws.length; i++) {
            hw.setInventoryBase( hws[i] );
            hw.setDbConnection( getDbConnection() );
            hw.delete();
        }

        com.cannontech.database.db.stars.appliance.ApplianceBase[] loads = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAppliances(
            getCustomerAccount().getAccountID(), getDbConnection() );
        com.cannontech.database.data.stars.appliance.ApplianceBase load = new com.cannontech.database.data.stars.appliance.ApplianceBase();
        for (int i = 0; i < loads.length; i++) {
            load.setApplianceBase( loads[i] );
            load.setDbConnection( getDbConnection() );
            load.delete();
        }

        com.cannontech.database.db.stars.event.LMProgramEvent.deleteAllProgramEvents(
            getCustomerAccount().getAccountID(), getDbConnection() );

        // delete from the mapping table
        delete( "ECToAccountMapping", "AccountID", getCustomerAccount().getAccountID() );

        getCustomerAccount().delete();

        setDbConnection(null);
    }

    public void add() throws java.sql.SQLException {
        getBillingAddress().add();
        getCustomerAccount().setBillingAddressID( getBillingAddress().getAddressID() );
        int addrID = getBillingAddress().getAddressID().intValue();
        
        //getCustomerLogin().add();
        //getCustomerBase().getPrimaryContact().setLogInID( getCustomerLogin().getLoginID() );
        
    	if (getCustomerAccount().getCustomerID().intValue() == com.cannontech.database.db.stars.customer.CustomerBase.NONE_INT) {
    		getCustomerBase().add();
    		getCustomerAccount().setCustomerID( getCustomerBase().getCustomerBase().getCustomerID() );
    	}

		if (getCustomerAccount().getAccountSiteID().intValue() == com.cannontech.database.db.stars.customer.AccountSite.NONE_INT) {
			getAccountSite().getStreetAddress().setAddressID( new Integer(++addrID) );
        	getAccountSite().add();
        	getCustomerAccount().setAccountSiteID( getAccountSite().getAccountSite().getAccountSiteID() );
		}
		
        getCustomerAccount().add();

        if (getCustomerBase() != null && getCustomerBase().getEnergyCompanyBase() != null) {
            // add to the mapping table
            Object[] addValues = {
                getCustomerBase().getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
                getCustomerAccount().getAccountID()
            };
            add( "ECToAccountMapping", addValues );
        }

        setDbConnection(null);
    }

    public void update() throws java.sql.SQLException {
        getCustomerAccount().update();

		if (getCustomerBase() != null)
			getCustomerBase().update();
		
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

        if (getCustomerBase() == null) {
            customerBase = new com.cannontech.database.data.stars.customer.CustomerBase();
            customerBase.setCustomerID( getCustomerAccount().getCustomerID() );
            customerBase.setDbConnection( getDbConnection() );
            customerBase.retrieve();
        }

        com.cannontech.database.db.stars.appliance.ApplianceBase[] appliances =
                    com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAppliances(
                        getCustomerAccount().getAccountID(), getDbConnection() );
        for (int i = 0; i < appliances.length; i++) {
            com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                        new com.cannontech.database.data.stars.appliance.ApplianceBase();
            appliance.setApplianceBase( appliances[i] );
            appliance.setDbConnection( getDbConnection() );
            appliance.retrieve();
            getApplianceVector().addElement( appliance );
        }

        com.cannontech.database.db.stars.hardware.InventoryBase[] inventories =
                    com.cannontech.database.db.stars.hardware.InventoryBase.getAllInventories(
                        getCustomerAccount().getAccountID(), getDbConnection() );
        for (int i = 0; i < inventories.length; i++) {
            // May check for whether this is a LM hardware later
            com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
                        new com.cannontech.database.data.stars.hardware.LMHardwareBase();
            hardware.setInventoryID( inventories[i].getInventoryID() );
            hardware.setDbConnection( getDbConnection() );
            hardware.retrieve();
            getInventoryVector().addElement( hardware );
        }

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

    public com.cannontech.database.db.customer.CustomerAddress getBillingAddress() {
        if (billingAddress == null)
            billingAddress = new com.cannontech.database.db.customer.CustomerAddress();
        return billingAddress;
    }

    public void setBillingAddress(com.cannontech.database.db.customer.CustomerAddress newBillingAddress) {
        billingAddress = newBillingAddress;
    }

    public com.cannontech.database.data.stars.customer.CustomerBase getCustomerBase() {
        return customerBase;
    }

    public void setCustomerBase(com.cannontech.database.data.stars.customer.CustomerBase newCustomerBase) {
        customerBase = newCustomerBase;
    }

    public com.cannontech.database.data.company.EnergyCompanyBase getEnergyCompanyBase() {
        return energyCompanyBase;
    }

    public void setEnergyCompanyBase(com.cannontech.database.data.company.EnergyCompanyBase newEnergyCompanyBase) {
        energyCompanyBase = newEnergyCompanyBase;
        if (getCustomerBase() != null)
            getCustomerBase().setEnergyCompanyBase( newEnergyCompanyBase );
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
	 * Returns the customerLogin.
	 * @return com.cannontech.database.db.customer.CustomerLogin
	 */
	public com.cannontech.database.db.customer.CustomerLogin getCustomerLogin() {
		if (customerLogin == null)
			customerLogin = new com.cannontech.database.db.customer.CustomerLogin();
		return customerLogin;
	}

	/**
	 * Sets the customerLogin.
	 * @param customerLogin The customerLogin to set
	 */
	public void setCustomerLogin(
		com.cannontech.database.db.customer.CustomerLogin customerLogin) {
		this.customerLogin = customerLogin;
	}

}
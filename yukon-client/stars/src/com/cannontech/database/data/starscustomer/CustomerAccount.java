package com.cannontech.database.data.starscustomer;

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

    private com.cannontech.database.db.starscustomer.CustomerAccount customerAccount = null;
    private com.cannontech.database.db.customer.CustomerAddress billingAddress = null;

    private com.cannontech.database.data.starscustomer.AccountSite accountSite = null;
    private com.cannontech.database.data.starscustomer.CustomerBase customerBase = null;
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

            com.cannontech.database.db.starscustomer.CustomerAccount accountDB =
                        com.cannontech.database.db.starscustomer.CustomerAccount.searchByAccountNumber(
                            energyCompanyID, accountNumber, conn );
            if (accountDB == null) return null;

            accountData = new CustomerAccount();
            accountData.setAccountID( accountDB.getAccountID() );
            accountData.setEnergyCompanyBase( energyCompany );
            accountData.setDbConnection( conn );
            accountData.retrieve();
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

        com.cannontech.database.db.starshardware.InventoryBase[] hws = com.cannontech.database.db.starshardware.InventoryBase.getAllInventories(
            getCustomerAccount().getAccountID(), getDbConnection() );
        com.cannontech.database.data.starshardware.InventoryBase hw = new com.cannontech.database.data.starshardware.InventoryBase();
        for (int i = 0; i < hws.length; i++) {
            hw.setInventoryBase( hws[i] );
            hw.setDbConnection( getDbConnection() );
            hw.delete();
        }

        com.cannontech.database.db.starsappliance.ApplianceBase[] loads = com.cannontech.database.db.starsappliance.ApplianceBase.getAllAppliances(
            getCustomerAccount().getAccountID(), getDbConnection() );
        com.cannontech.database.data.starsappliance.ApplianceBase load = new com.cannontech.database.data.starsappliance.ApplianceBase();
        for (int i = 0; i < loads.length; i++) {
            load.setApplianceBase( loads[i] );
            load.setDbConnection( getDbConnection() );
            load.delete();
        }

        com.cannontech.database.db.starsevent.LMProgramCustomerActivity.deleteAllCustomerActivities(
            getCustomerAccount().getAccountID(), getDbConnection() );

        // delete from the mapping table
        delete( "ECToAccountMapping", "AccountID", getCustomerAccount().getAccountID() );

        getCustomerAccount().delete();

        setDbConnection(null);
    }

    public void add() throws java.sql.SQLException {
        getCustomerAccount().add();

        //getAccountSite().add();
        getBillingAddress().add();

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

        getAccountSite().update();
        getBillingAddress().update();

        setDbConnection(null);
    }

    public void retrieve() throws java.sql.SQLException {
        // setEnergyCompanyBase() should be called before this function
        getCustomerAccount().retrieve();

        getBillingAddress().setAddressID( getCustomerAccount().getBillingAddressID() );
        getBillingAddress().retrieve();

        getAccountSite().setAccountSiteID( getCustomerAccount().getAccountSiteID() );
        getAccountSite().retrieve();

        if (getCustomerBase() == null) {
            com.cannontech.database.data.starscustomer.CustomerBase customer = new com.cannontech.database.data.starscustomer.CustomerBase();
            customer.setCustomerID( getCustomerAccount().getCustomerID() );
            customer.setEnergyCompanyBase( getEnergyCompanyBase() );
            customer.setDbConnection( getDbConnection() );
            customer.retrieve();
            setCustomerBase( customer );
        }

        com.cannontech.database.db.starsappliance.ApplianceBase[] appliances =
                    com.cannontech.database.db.starsappliance.ApplianceBase.getAllAppliances(
                        getCustomerAccount().getAccountID(), getDbConnection() );
        for (int i = 0; i < appliances.length; i++) {
            com.cannontech.database.data.starsappliance.ApplianceBase appliance =
                        new com.cannontech.database.data.starsappliance.ApplianceBase();
            appliance.setApplianceBase( appliances[i] );
            appliance.setDbConnection( getDbConnection() );
            appliance.retrieve();
            getApplianceVector().addElement( appliance );
        }

        com.cannontech.database.db.starshardware.InventoryBase[] inventories =
                    com.cannontech.database.db.starshardware.InventoryBase.getAllInventories(
                        getCustomerAccount().getAccountID(), getDbConnection() );
        for (int i = 0; i < inventories.length; i++) {
            if (com.cannontech.database.db.starshardware.LMHardwareBase.isLMHardwareBase( inventories[i].getInventoryID(), getDbConnection() )) {
                // This is a LM hardware
                com.cannontech.database.data.starshardware.LMHardwareBase hardware =
                            new com.cannontech.database.data.starshardware.LMHardwareBase();
                hardware.setInventoryID( inventories[i].getInventoryID() );
                hardware.setDbConnection( getDbConnection() );
                hardware.retrieve();
                getInventoryVector().addElement( hardware );
            }
        }

        setDbConnection(null);
    }

    public com.cannontech.database.db.starscustomer.CustomerAccount getCustomerAccount() {
        if (customerAccount == null)
            customerAccount = new com.cannontech.database.db.starscustomer.CustomerAccount();
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.database.db.starscustomer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.database.data.starscustomer.AccountSite getAccountSite() {
        if (accountSite == null)
            accountSite = new com.cannontech.database.data.starscustomer.AccountSite();
        return accountSite;
    }

    public void setAccountSite(com.cannontech.database.data.starscustomer.AccountSite newAccountSite) {
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

    public com.cannontech.database.data.starscustomer.CustomerBase getCustomerBase() {
        return customerBase;
    }

    public void setCustomerBase(com.cannontech.database.data.starscustomer.CustomerBase newCustomerBase) {
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

    public static void main(String[] args) {
        //CustomerAccount account = searchByAccountNumber(new Integer(1), "12345");
        CustomerAccount account = new com.cannontech.database.data.starscustomer.CustomerAccount();
        account.setAccountID( new Integer(1) );
        try {
            com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, account).execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Account #: " + account.getCustomerAccount().getAccountNumber());
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
}
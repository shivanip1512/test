package com.cannontech.database.data.starshardware;

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

    private com.cannontech.database.db.starshardware.InventoryBase inventoryBase = null;
    private com.cannontech.database.db.starshardware.ServiceCompany installationCompany = null;

    private com.cannontech.database.data.starscustomer.CustomerAccount customerAccount = null;

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

    public void delete() throws java.sql.SQLException {
        // delete from the mapping table
        delete( "ECToInventoryMapping", "InventoryID", getInventoryBase().getInventoryID() );

        getInventoryBase().delete();

        setDbConnection(null);
    }

    public void add() throws java.sql.SQLException {
        getInventoryBase().add();

        if (getCustomerAccount() != null && getCustomerAccount().getCustomerBase() != null
            && getCustomerAccount().getCustomerBase().getEnergyCompanyBase() != null) {
            // add to the mapping table
            Object[] addValues = {
                getCustomerAccount().getCustomerBase().getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
                getInventoryBase().getInventoryID()
            };
            add( "ECToInventoryMapping", addValues );
        }

        setDbConnection(null);
    }

    public void update() throws java.sql.SQLException {
        getInventoryBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getInventoryBase().retrieve();

        getInstallationCompany().setCompanyID( getInventoryBase().getInstallationCompanyID() );
        getInstallationCompany().retrieve();
    }

    public com.cannontech.database.db.starshardware.InventoryBase getInventoryBase() {
        if (inventoryBase == null)
            inventoryBase = new com.cannontech.database.db.starshardware.InventoryBase();
        return inventoryBase;
    }

    public void setInventoryBase(com.cannontech.database.db.starshardware.InventoryBase newInventoryBase) {
        inventoryBase = newInventoryBase;
    }

    public com.cannontech.database.data.starscustomer.CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.database.data.starscustomer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.database.db.starshardware.ServiceCompany getInstallationCompany() {
        if (installationCompany == null)
            installationCompany = new com.cannontech.database.db.starshardware.ServiceCompany();
        return installationCompany;
    }

    public void setInstallationCompany(com.cannontech.database.db.starshardware.ServiceCompany newInstallationCompany) {
        installationCompany = newInstallationCompany;
    }
}
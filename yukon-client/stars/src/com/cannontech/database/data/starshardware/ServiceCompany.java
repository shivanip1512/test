package com.cannontech.database.data.starshardware;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ServiceCompany extends DBPersistent {

    private com.cannontech.database.db.starshardware.ServiceCompany serviceCompany = null;

    public ServiceCompany() {
        super();
    }

    public void setCompanyID(Integer newID) {
        getServiceCompany().setCompanyID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getServiceCompany().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // Set the InstallationCompanyID to NONE_INT for all the inventories referencing this one
        com.cannontech.database.db.starshardware.InventoryBase.clearInstallationCompany(
            getServiceCompany().getCompanyID(), getDbConnection() );
        getServiceCompany().delete();
    }

    public void add() throws java.sql.SQLException {
        getServiceCompany().add();
    }

    public void update() throws java.sql.SQLException {
        getServiceCompany().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getServiceCompany().retrieve();
    }

    public com.cannontech.database.db.starshardware.ServiceCompany getServiceCompany() {
        if (serviceCompany == null)
            serviceCompany = new com.cannontech.database.db.starshardware.ServiceCompany();
        return serviceCompany;
    }

    public void setServiceCompany(com.cannontech.database.db.starshardware.ServiceCompany newServiceCompany) {
        serviceCompany = newServiceCompany;
    }
}
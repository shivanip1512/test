package com.cannontech.database.data.starsappliance;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ApplianceBase extends DBPersistent {

    private com.cannontech.database.db.starsappliance.ApplianceBase applianceBase = null;
    private com.cannontech.database.db.starsappliance.ApplianceCategory applianceCategory = null;
    private com.cannontech.database.db.starshardware.LMHardwareConfiguration _LMHardwareConfig = null;

    private com.cannontech.database.data.starscustomer.CustomerAccount customerAccount = null;
    private com.cannontech.database.data.device.lm.LMProgramBase _LMProgram = null;

    public ApplianceBase() {
        super();
    }

    public void setApplianceID(Integer newID) {
        getApplianceBase().setApplianceID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getApplianceBase().setDbConnection(conn);
        getApplianceCategory().setDbConnection(conn);
        getLMHardwareConfig().setDbConnection(conn);
        getLMProgram().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from LMHardwareConfiguration
        com.cannontech.database.db.starshardware.LMHardwareConfiguration.deleteLMHardwareConfiguration(
            getApplianceBase().getApplianceID(), getDbConnection() );
        getApplianceBase().delete();
    }

    public void add() throws java.sql.SQLException {
        getApplianceBase().add();
    }

    public void update() throws java.sql.SQLException {
        getApplianceBase().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getApplianceBase().retrieve();

        getApplianceCategory().setApplianceCategoryID( getApplianceBase().getApplianceCategoryID() );
        getApplianceCategory().retrieve();

        setLMHardwareConfig( com.cannontech.database.db.starshardware.LMHardwareConfiguration.getLMHardwareConfiguration(
                    getApplianceBase().getApplianceID(), getDbConnection()) );

        getLMProgram().setPAObjectID( getApplianceBase().getLMProgramID() );
        getLMProgram().retrieve();
    }

    public com.cannontech.database.db.starsappliance.ApplianceBase getApplianceBase() {
        if (applianceBase == null)
            applianceBase = new com.cannontech.database.db.starsappliance.ApplianceBase();
        return applianceBase;
    }

    public void setApplianceBase(com.cannontech.database.db.starsappliance.ApplianceBase newApplianceBase) {
        applianceBase = newApplianceBase;
    }

    public com.cannontech.database.data.starscustomer.CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.database.data.starscustomer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.database.db.starsappliance.ApplianceCategory getApplianceCategory() {
        if (applianceCategory == null)
            applianceCategory = new com.cannontech.database.db.starsappliance.ApplianceCategory();
        return applianceCategory;
    }

    public void setApplianceCategory(com.cannontech.database.db.starsappliance.ApplianceCategory newApplianceCategory) {
        applianceCategory = newApplianceCategory;
    }

    public com.cannontech.database.db.starshardware.LMHardwareConfiguration getLMHardwareConfig() {
        if (_LMHardwareConfig == null)
            _LMHardwareConfig = new com.cannontech.database.db.starshardware.LMHardwareConfiguration();
        return _LMHardwareConfig;
    }

    public void setLMHardwareConfig(com.cannontech.database.db.starshardware.LMHardwareConfiguration newLMHardwareConfig) {
        _LMHardwareConfig = newLMHardwareConfig;
    }

    public com.cannontech.database.data.device.lm.LMProgramBase getLMProgram() {
        if (_LMProgram == null)
            _LMProgram = new com.cannontech.database.data.device.lm.LMProgramDirect();
        return _LMProgram;
    }

    public void setLMProgram(com.cannontech.database.data.device.lm.LMProgramBase newLMProgram) {
        _LMProgram = newLMProgram;
    }
}
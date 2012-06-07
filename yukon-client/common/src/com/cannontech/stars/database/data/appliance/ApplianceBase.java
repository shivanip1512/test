package com.cannontech.stars.database.data.appliance;

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

    private com.cannontech.stars.database.db.appliance.ApplianceBase applianceBase = null;
    private com.cannontech.stars.database.db.appliance.ApplianceCategory applianceCategory = null;
    private com.cannontech.stars.database.db.hardware.LMHardwareConfiguration lmHardwareConfig = null;
    private com.cannontech.stars.database.data.customer.CustomerAccount customerAccount = null;
    private com.cannontech.database.data.device.lm.LMProgramBase lmProgram = null;

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
        com.cannontech.stars.database.db.hardware.LMHardwareConfiguration.deleteLMHardwareConfiguration(
	            getApplianceBase().getApplianceID() );
        
        // delete from tables of each appliance type
        String[] tables = com.cannontech.stars.database.db.appliance.ApplianceBase.DEPENDENT_TABLES;
        for (int i = 0; i < tables.length; i++)
        	delete( tables[i], "ApplianceID", getApplianceBase().getApplianceID() );
        
        getApplianceBase().delete();
    }

    public void add() throws java.sql.SQLException {
        getApplianceBase().add();
        if (getLMHardwareConfig().getInventoryID() != null) {
        	getLMHardwareConfig().setApplianceID( getApplianceBase().getApplianceID() );
        	getLMHardwareConfig().add();
        }
    }

    public void update() throws java.sql.SQLException {
        getApplianceBase().update();
        
        com.cannontech.stars.database.db.hardware.LMHardwareConfiguration.deleteLMHardwareConfiguration(
	            getApplianceBase().getApplianceID() );
        
        if (getLMHardwareConfig().getInventoryID() != null)
        	getLMHardwareConfig().add();
    }

    public void retrieve() throws java.sql.SQLException {
        getApplianceBase().retrieve();

        setLMHardwareConfig( com.cannontech.stars.database.db.hardware.LMHardwareConfiguration.getLMHardwareConfiguration(
                    getApplianceBase().getApplianceID()) );
    }

    public com.cannontech.stars.database.db.appliance.ApplianceBase getApplianceBase() {
        if (applianceBase == null)
            applianceBase = new com.cannontech.stars.database.db.appliance.ApplianceBase();
        return applianceBase;
    }

    public void setApplianceBase(com.cannontech.stars.database.db.appliance.ApplianceBase newApplianceBase) {
        applianceBase = newApplianceBase;
    }

    public com.cannontech.stars.database.data.customer.CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.stars.database.data.customer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.stars.database.db.appliance.ApplianceCategory getApplianceCategory() {
        if (applianceCategory == null)
            applianceCategory = new com.cannontech.stars.database.db.appliance.ApplianceCategory();
        return applianceCategory;
    }

    public void setApplianceCategory(com.cannontech.stars.database.db.appliance.ApplianceCategory newApplianceCategory) {
        applianceCategory = newApplianceCategory;
    }
	/**
	 * Returns the lmHardwareConfig.
	 * @return com.cannontech.stars.database.db.hardware.LMHardwareConfiguration
	 */
	public com.cannontech.stars.database.db.hardware.LMHardwareConfiguration getLMHardwareConfig() {
		if (lmHardwareConfig == null)
			lmHardwareConfig = new com.cannontech.stars.database.db.hardware.LMHardwareConfiguration();
		return lmHardwareConfig;
	}

	/**
	 * Returns the lmProgram.
	 * @return com.cannontech.database.data.device.lm.LMProgramBase
	 */
	public com.cannontech.database.data.device.lm.LMProgramBase getLMProgram() {
		if (lmProgram == null)
			lmProgram = new com.cannontech.database.data.device.lm.LMProgramDirect();
		return lmProgram;
	}

	/**
	 * Sets the lmHardwareConfig.
	 * @param lmHardwareConfig The lmHardwareConfig to set
	 */
	public void setLMHardwareConfig(
		com.cannontech.stars.database.db.hardware.LMHardwareConfiguration lmHardwareConfig) {
		this.lmHardwareConfig = lmHardwareConfig;
	}

	/**
	 * Sets the lmProgram.
	 * @param lmProgram The lmProgram to set
	 */
	public void setLMProgram(
		com.cannontech.database.data.device.lm.LMProgramBase lmProgram) {
		this.lmProgram = lmProgram;
	}

}
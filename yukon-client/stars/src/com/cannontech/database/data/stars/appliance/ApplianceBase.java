package com.cannontech.database.data.stars.appliance;

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

    private com.cannontech.database.db.stars.appliance.ApplianceBase applianceBase = null;
    private com.cannontech.database.db.stars.appliance.ApplianceCategory applianceCategory = null;
    private com.cannontech.database.db.stars.hardware.LMHardwareConfiguration lmHardwareConfig = null;
    private com.cannontech.database.data.stars.customer.CustomerAccount customerAccount = null;
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
        com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteLMHardwareConfiguration(
	            getApplianceBase().getApplianceID() );
        
        // delete from tables of each appliance type
        delete( "ApplianceAirConditioner", "ApplianceID", getApplianceBase().getApplianceID() );
		delete( "ApplianceDualFuel", "ApplianceID", getApplianceBase().getApplianceID() );
		delete( "ApplianceGenerator", "ApplianceID", getApplianceBase().getApplianceID() );
		delete( "ApplianceGrainDryer", "ApplianceID", getApplianceBase().getApplianceID() );
		delete( "ApplianceHeatPump", "ApplianceID", getApplianceBase().getApplianceID() );
		delete( "ApplianceIrrigation", "ApplianceID", getApplianceBase().getApplianceID() );
		delete( "ApplianceStorageHeat", "ApplianceID", getApplianceBase().getApplianceID() );
		delete( "ApplianceWaterHeater", "ApplianceID", getApplianceBase().getApplianceID() );
        
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
        
        com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteLMHardwareConfiguration(
	            getApplianceBase().getApplianceID() );
        
        if (getLMHardwareConfig().getInventoryID() != null)
        	getLMHardwareConfig().add();
    }

    public void retrieve() throws java.sql.SQLException {
        getApplianceBase().retrieve();

        setLMHardwareConfig( com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getLMHardwareConfiguration(
                    getApplianceBase().getApplianceID()) );
    }

    public com.cannontech.database.db.stars.appliance.ApplianceBase getApplianceBase() {
        if (applianceBase == null)
            applianceBase = new com.cannontech.database.db.stars.appliance.ApplianceBase();
        return applianceBase;
    }

    public void setApplianceBase(com.cannontech.database.db.stars.appliance.ApplianceBase newApplianceBase) {
        applianceBase = newApplianceBase;
    }

    public com.cannontech.database.data.stars.customer.CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(com.cannontech.database.data.stars.customer.CustomerAccount newCustomerAccount) {
        customerAccount = newCustomerAccount;
    }

    public com.cannontech.database.db.stars.appliance.ApplianceCategory getApplianceCategory() {
        if (applianceCategory == null)
            applianceCategory = new com.cannontech.database.db.stars.appliance.ApplianceCategory();
        return applianceCategory;
    }

    public void setApplianceCategory(com.cannontech.database.db.stars.appliance.ApplianceCategory newApplianceCategory) {
        applianceCategory = newApplianceCategory;
    }
	/**
	 * Returns the lmHardwareConfig.
	 * @return com.cannontech.database.db.stars.hardware.LMHardwareConfiguration
	 */
	public com.cannontech.database.db.stars.hardware.LMHardwareConfiguration getLMHardwareConfig() {
		if (lmHardwareConfig == null)
			lmHardwareConfig = new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
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
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration lmHardwareConfig) {
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
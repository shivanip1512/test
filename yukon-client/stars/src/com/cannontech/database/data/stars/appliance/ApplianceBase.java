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
    private com.cannontech.database.db.stars.CustomerListEntry manufacturer = null;
    private com.cannontech.database.db.stars.CustomerListEntry location = null;

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
        getManufacturer().setDbConnection(conn);
        getLocation().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // delete from LMHardwareConfiguration
        com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteLMHardwareConfiguration(
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

        setLMHardwareConfig( com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getLMHardwareConfiguration(
                    getApplianceBase().getApplianceID(), getDbConnection()) );

/*
 * Commented out since cache is used now
 * 
        getApplianceCategory().setApplianceCategoryID( getApplianceBase().getApplianceCategoryID() );
        getApplianceCategory().retrieve();

        getLMProgram().setPAObjectID( getApplianceBase().getLMProgramID() );
        getLMProgram().retrieve();
        
		getManufacturer().setEntryID( getApplianceBase().getManufacturerID() );
		getManufacturer().retrieve();
		
		getLocation().setEntryID( getApplianceBase().getLocationID() );
		getLocation().retrieve();
*/
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

	/**
	 * Returns the location.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getLocation() {
		if (location == null)
			location = new com.cannontech.database.db.stars.CustomerListEntry();
		return location;
	}

	/**
	 * Returns the manufacturer.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getManufacturer() {
		if (manufacturer == null)
			manufacturer = new com.cannontech.database.db.stars.CustomerListEntry();
		return manufacturer;
	}

	/**
	 * Sets the location.
	 * @param location The location to set
	 */
	public void setLocation(com.cannontech.database.db.stars.CustomerListEntry location) {
		this.location = location;
	}

	/**
	 * Sets the manufacturer.
	 * @param manufacturer The manufacturer to set
	 */
	public void setManufacturer(com.cannontech.database.db.stars.CustomerListEntry manufacturer) {
		this.manufacturer = manufacturer;
	}

}
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

public class ApplianceCategory extends DBPersistent {

    private com.cannontech.database.db.stars.appliance.ApplianceCategory applianceCategory = null;
    private com.cannontech.database.db.stars.CustomerListEntry category = null;
    private com.cannontech.database.db.stars.CustomerWebConfiguration webConfiguration = null;

    public ApplianceCategory() {
        super();
    }

    public void setApplianceCategoryID(Integer newID) {
        getApplianceCategory().setApplianceCategoryID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getApplianceCategory().setDbConnection(conn);
        getCategory().setDbConnection(conn);
        getWebConfiguration().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
    	// Delete from mapping table
        delete( "LMProgramWebPublishing", "ApplianceCategoryID", getApplianceCategory().getApplianceCategoryID() );

        getApplianceCategory().delete();
    }

    public void add() throws java.sql.SQLException {
        getApplianceCategory().add();
    }

    public void update() throws java.sql.SQLException {
        getApplianceCategory().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getApplianceCategory().retrieve();
        
/*
 * Commented out since cached is used now
 * 
        getCategory().setEntryID( getApplianceCategory().getCategoryID() );
        getCategory().retrieve();
        
        getWebConfiguration().setConfigurationID( getApplianceCategory().getWebConfigurationID() );
        getWebConfiguration().retrieve();
*/
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
	 * Returns the category.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getCategory() {
		if (category == null)
			category = new com.cannontech.database.db.stars.CustomerListEntry();
		return category;
	}

	/**
	 * Sets the category.
	 * @param category The category to set
	 */
	public void setCategory(
		com.cannontech.database.db.stars.CustomerListEntry category) {
		this.category = category;
	}

	/**
	 * Returns the webConfiguration.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerWebConfiguration getWebConfiguration() {
		if (webConfiguration == null)
			webConfiguration = new com.cannontech.database.db.stars.CustomerWebConfiguration();
		return webConfiguration;
	}

	/**
	 * Sets the webConfiguration.
	 * @param webConfiguration The webConfiguration to set
	 */
	public void setWebConfiguration(
		com.cannontech.database.db.stars.CustomerWebConfiguration webConfiguration) {
		this.webConfiguration = webConfiguration;
	}

}
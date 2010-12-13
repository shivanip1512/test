package com.cannontech.database.data.stars.appliance;

import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeHelper;
import com.cannontech.message.dispatch.message.DbChangeType;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ApplianceCategory extends DBPersistent implements CTIDbChange {

    private com.cannontech.database.db.stars.appliance.ApplianceCategory applianceCategory = null;
    private com.cannontech.database.db.web.YukonWebConfiguration webConfiguration = null;
    
    private Integer energyCompanyID = null;

    public ApplianceCategory() {
        super();
    }

    public void setApplianceCategoryID(Integer newID) {
        getApplianceCategory().setApplianceCategoryID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getApplianceCategory().setDbConnection(conn);
        getWebConfiguration().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
    	// Delete from mapping table
        delete( "ECToGenericMapping",
        		new String[] {"ItemID", "MappingCategory"},
        		new Object[] {getApplianceCategory().getApplianceCategoryID(), "ApplianceCategory"} );
        
        getApplianceCategory().delete();
        
        getWebConfiguration().setConfigurationID(
        		getApplianceCategory().getWebConfigurationID() );
        getWebConfiguration().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException( "setEnergyCompanyID() must be called before this function" );
    		
    	getWebConfiguration().add();
    	getApplianceCategory().setWebConfigurationID(
    			getWebConfiguration().getConfigurationID() );
    	
        getApplianceCategory().add();

        // add to the mapping table
        Object[] addValues = {
            getEnergyCompanyID(),
            getApplianceCategory().getApplianceCategoryID(),
            "ApplianceCategory"
        };
        add( "ECToGenericMapping", addValues );
    }

    public void update() throws java.sql.SQLException {
        getApplianceCategory().update();
        
        getWebConfiguration().setConfigurationID(
        		getApplianceCategory().getWebConfigurationID() );
        getWebConfiguration().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getApplianceCategory().retrieve();
        
        getWebConfiguration().setConfigurationID(
        		getApplianceCategory().getWebConfigurationID() );
        getWebConfiguration().retrieve();
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
	 * Returns the webConfiguration.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.web.YukonWebConfiguration getWebConfiguration() {
		if (webConfiguration == null)
			webConfiguration = new com.cannontech.database.db.web.YukonWebConfiguration();
		return webConfiguration;
	}

	/**
	 * Sets the webConfiguration.
	 * @param webConfiguration The webConfiguration to set
	 */
	public void setWebConfiguration(
		com.cannontech.database.db.web.YukonWebConfiguration webConfiguration) {
		this.webConfiguration = webConfiguration;
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

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs = {
                DbChangeHelper.newDbChange(dbChangeType, DbChangeCategory.APPLIANCE_CATEGORY, 
                                           applianceCategory.getApplianceCategoryID())
        };
        
        return msgs;
    }

}
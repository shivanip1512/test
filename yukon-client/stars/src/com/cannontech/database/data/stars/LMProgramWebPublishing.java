package com.cannontech.database.data.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: LMProgramWebPublishing.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 13, 2002 4:09:38 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class LMProgramWebPublishing extends DBPersistent {
	
	private com.cannontech.database.db.stars.LMProgramWebPublishing lmProgramWebPublishing = null;
	private com.cannontech.database.db.stars.CustomerWebConfiguration webConfiguration = null;
	
	private com.cannontech.database.data.stars.appliance.ApplianceCategory applianceCategory = null;
	private com.cannontech.database.data.device.lm.LMProgramDirect lmProgram = null;
	
	public void setApplianceCategoryID(Integer newID) {
		getLMProgramWebPublishing().setApplianceCategoryID( newID );
	}
	
	public void setLMProgramID(Integer newID) {
		getLMProgramWebPublishing().setLMProgramID( newID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getLMProgramWebPublishing().setDbConnection(conn);
		getWebConfiguration().setDbConnection(conn);
		getApplianceCategory().setDbConnection(conn);
		getLMProgram().setDbConnection(conn);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getLMProgramWebPublishing().add();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		getLMProgramWebPublishing().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getLMProgramWebPublishing().retrieve();
		
		getWebConfiguration().setConfigurationID( getLMProgramWebPublishing().getWebSettingsID() );
		getWebConfiguration().retrieve();
		
		getApplianceCategory().setApplianceCategoryID( getLMProgramWebPublishing().getApplianceCategoryID() );
		getApplianceCategory().retrieve();
		
		getLMProgram().setPAObjectID( getLMProgramWebPublishing().getLMProgramID() );
		getLMProgram().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLMProgramWebPublishing().update();
	}
	
	public static LMProgramWebPublishing getLMProgramWebPublishing(Integer programID) {
        java.sql.Connection conn = null;
        
        try {
            com.cannontech.database.db.stars.LMProgramWebPublishing webPubDB =
            		com.cannontech.database.db.stars.LMProgramWebPublishing.getLMProgramWebPublishing( programID );
            if (webPubDB == null) return null;
            
            com.cannontech.database.data.stars.LMProgramWebPublishing webPub =
            		new com.cannontech.database.data.stars.LMProgramWebPublishing();
            webPub.setLmProgramWebPublishing( webPubDB );
            webPub.setDbConnection(conn);
            webPub.retrieve();
            
            return webPub;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
	}

	/**
	 * Returns the lmProgramWebPublishing.
	 * @return com.cannontech.database.db.stars.LMProgramWebPublishing
	 */
	public com.cannontech.database.db.stars.LMProgramWebPublishing getLMProgramWebPublishing() {
		if (lmProgramWebPublishing == null)
			lmProgramWebPublishing = new com.cannontech.database.db.stars.LMProgramWebPublishing();
		return lmProgramWebPublishing;
	}

	/**
	 * Sets the lmProgramWebPublishing.
	 * @param lmProgramWebPublishing The lmProgramWebPublishing to set
	 */
	public void setLmProgramWebPublishing(
		com.cannontech.database.db.stars.LMProgramWebPublishing lmProgramWebPublishing) {
		this.lmProgramWebPublishing = lmProgramWebPublishing;
	}

	/**
	 * Returns the applianceCategory.
	 * @return com.cannontech.database.data.stars.appliance.ApplianceCategory
	 */
	public com.cannontech.database.data.stars.appliance.ApplianceCategory getApplianceCategory() {
		if (applianceCategory == null)
			applianceCategory = new com.cannontech.database.data.stars.appliance.ApplianceCategory();
		return applianceCategory;
	}

	/**
	 * Returns the lmProgram.
	 * @return com.cannontech.database.data.device.lm.LMProgramDirect
	 */
	public com.cannontech.database.data.device.lm.LMProgramDirect getLMProgram() {
		if (lmProgram == null)
			lmProgram = new com.cannontech.database.data.device.lm.LMProgramDirect();
		return lmProgram;
	}

	/**
	 * Returns the webConfiguration.
	 * @return com.cannontech.database.db.stars.CustomerWebConfiguration
	 */
	public com.cannontech.database.db.stars.CustomerWebConfiguration getWebConfiguration() {
		if (webConfiguration == null)
			webConfiguration = new com.cannontech.database.db.stars.CustomerWebConfiguration();
		return webConfiguration;
	}

	/**
	 * Sets the applianceCategory.
	 * @param applianceCategory The applianceCategory to set
	 */
	public void setApplianceCategory(
		com.cannontech.database.data.stars.appliance.ApplianceCategory applianceCategory) {
		this.applianceCategory = applianceCategory;
	}

	/**
	 * Sets the lmProgram.
	 * @param lmProgram The lmProgram to set
	 */
	public void setLMProgram(
		com.cannontech.database.data.device.lm.LMProgramDirect lmProgram) {
		this.lmProgram = lmProgram;
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

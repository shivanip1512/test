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
	private com.cannontech.database.db.web.YukonWebConfiguration webConfiguration = null;
	
	public void setProgramID(Integer programID) {
		getLMProgramWebPublishing().setProgramID( programID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getLMProgramWebPublishing().setDbConnection(conn);
		getWebConfiguration().setDbConnection(conn);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getWebConfiguration().add();
		getLMProgramWebPublishing().setWebSettingsID(
				getWebConfiguration().getConfigurationID() );
				
		getLMProgramWebPublishing().add();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		getLMProgramWebPublishing().delete();
		
		getWebConfiguration().setConfigurationID(
				getLMProgramWebPublishing().getWebSettingsID() );
		getWebConfiguration().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getLMProgramWebPublishing().retrieve();
		
		getWebConfiguration().setConfigurationID(
				getLMProgramWebPublishing().getWebSettingsID() );
		getWebConfiguration().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLMProgramWebPublishing().update();
		
		getWebConfiguration().setConfigurationID(
				getLMProgramWebPublishing().getWebSettingsID() );
		getWebConfiguration().update();
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
	 * Returns the webConfiguration.
	 * @return com.cannontech.database.db.stars.CustomerWebConfiguration
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

}

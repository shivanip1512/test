/*
 * Created on Jan 18, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author Yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConfigurationSASimple extends DBPersistent {

	private Integer configurationID = null;
	private String operationalAddress = "";
	
	public static final String[] SETTER_COLUMNS = {
		"OperationalAddress"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ConfigurationID" };
	
	public static final String TABLE_NAME = "LMConfigurationSASimple";
	
	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getConfigurationID() == null)
			throw new SQLException( "ConfigurationID is not set yet" );
		
		Object[] addValues = {
			getConfigurationID(), getOperationalAddress()
		};
		add( TABLE_NAME, addValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getConfigurationID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getConfigurationID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setOperationalAddress( (String) results[0] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getOperationalAddress()
		};
		Object[] constraintValues = { getConfigurationID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public Integer getConfigurationID() {
		return configurationID;
	}

	/**
	 * @return
	 */
	public String getOperationalAddress() {
		return operationalAddress;
	}

	/**
	 * @param integer
	 */
	public void setConfigurationID(Integer integer) {
		configurationID = integer;
	}

	/**
	 * @param integer
	 */
	public void setOperationalAddress(String string) {
		operationalAddress = string;
	}

}

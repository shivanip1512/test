/*
 * Created on Jul 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConfigurationVersaCom extends DBPersistent {
	
	private Integer configurationID = null;
	private Integer utilityID = new Integer(0);
	private Integer section = new Integer(0);
	private Integer classAddress = new Integer(0);
	private Integer divisionAddress = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"UtilityID", "Section", "ClassAddress", "DivisionAddress"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ConfigurationID" };
	
	public static final String TABLE_NAME = "LMConfigurationVersaCom";

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getConfigurationID() == null)
			throw new SQLException( "ConfigurationID is not set yet" );
		
		Object[] addValues = {
			getConfigurationID(), getUtilityID(), getSection(),
			getClassAddress(), getDivisionAddress()
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
			setUtilityID( (Integer) results[0] );
			setSection( (Integer) results[1] );
			setClassAddress( (Integer) results[2] );
			setDivisionAddress( (Integer) results[3] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getUtilityID(), getSection(), getClassAddress(), getDivisionAddress()
		};
		Object[] constraintValues = { getConfigurationID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public Integer getClassAddress() {
		return classAddress;
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
	public Integer getDivisionAddress() {
		return divisionAddress;
	}

	/**
	 * @return
	 */
	public Integer getSection() {
		return section;
	}

	/**
	 * @return
	 */
	public Integer getUtilityID() {
		return utilityID;
	}

	/**
	 * @param integer
	 */
	public void setClassAddress(Integer integer) {
		classAddress = integer;
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
	public void setDivisionAddress(Integer integer) {
		divisionAddress = integer;
	}

	/**
	 * @param integer
	 */
	public void setSection(Integer integer) {
		section = integer;
	}

	/**
	 * @param integer
	 */
	public void setUtilityID(Integer integer) {
		utilityID = integer;
	}

}

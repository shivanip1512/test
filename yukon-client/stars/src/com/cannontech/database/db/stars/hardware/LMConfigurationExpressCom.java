/*
 * Created on Jul 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConfigurationExpressCom extends DBPersistent {
	
	private Integer configurationID = null;
	private Integer serviceProvider = new Integer(0);
	private Integer geo = new Integer(0);
	private Integer substation = new Integer(0);
	private Integer feeder = new Integer(0);
	private Integer zip = new Integer(0);
	private Integer userAddress = new Integer(0);
	private String program = CtiUtilities.STRING_NONE;
	private String splinter = CtiUtilities.STRING_NONE;
	
	public static final String[] SETTER_COLUMNS = {
		"ServiceProvider", "GEO", "Substation", "Feeder",
		"Zip", "UserAddress", "Program", "Splinter"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ConfigurationID" };
	
	public static final String TABLE_NAME = "LMConfigurationExpressCom";

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getConfigurationID() == null)
			throw new SQLException( "ConfigurationID is not set yet" );
		
		Object[] addValues = {
			getConfigurationID(), getServiceProvider(), getGEO(), getSubstation(),
			getFeeder(), getZip(), getUserAddress(), getProgram(), getSplinter()
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
			setServiceProvider( (Integer) results[0] );
			setGEO( (Integer) results[1] );
			setSubstation( (Integer) results[2] );
			setFeeder( (Integer) results[3] );
			setZip( (Integer) results[4] );
			setUserAddress( (Integer) results[5] );
			setProgram( (String) results[6] );
			setSplinter( (String) results[7] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getServiceProvider(), getGEO(), getSubstation(), getFeeder(),
			getZip(), getUserAddress(), getProgram(), getSplinter()
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
	public Integer getFeeder() {
		return feeder;
	}

	/**
	 * @return
	 */
	public Integer getGEO() {
		return geo;
	}

	/**
	 * @return
	 */
	public String getProgram() {
		return program;
	}

	/**
	 * @return
	 */
	public Integer getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * @return
	 */
	public String getSplinter() {
		return splinter;
	}

	/**
	 * @return
	 */
	public Integer getSubstation() {
		return substation;
	}

	/**
	 * @return
	 */
	public Integer getUserAddress() {
		return userAddress;
	}

	/**
	 * @return
	 */
	public Integer getZip() {
		return zip;
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
	public void setFeeder(Integer integer) {
		feeder = integer;
	}

	/**
	 * @param integer
	 */
	public void setGEO(Integer integer) {
		geo = integer;
	}

	/**
	 * @param string
	 */
	public void setProgram(String string) {
		program = string;
	}

	/**
	 * @param integer
	 */
	public void setServiceProvider(Integer integer) {
		serviceProvider = integer;
	}

	/**
	 * @param string
	 */
	public void setSplinter(String string) {
		splinter = string;
	}

	/**
	 * @param integer
	 */
	public void setSubstation(Integer integer) {
		substation = integer;
	}

	/**
	 * @param integer
	 */
	public void setUserAddress(Integer integer) {
		userAddress = integer;
	}

	/**
	 * @param integer
	 */
	public void setZip(Integer integer) {
		zip = integer;
	}

}

/*
 * Created on Jun 23, 2004
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
public class LMConfigurationSA305 extends DBPersistent {
	
	private Integer configurationID = null;
	private Integer utility = new Integer(0);
	private Integer groupAddress = new Integer(0);
	private Integer division = new Integer(0);
	private Integer substation = new Integer(0);
	private Integer rateFamily = new Integer(0);
	private Integer rateMember = new Integer(0);
	private Integer rateHierarchy = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"Utility", "GroupAddress", "Division", "Substation",
		"RateFamily", "RateMember", "RateHierarchy"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ConfigurationID" };
	
	public static final String TABLE_NAME = "LMConfigurationSA305";

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getConfigurationID() == null)
			throw new SQLException( "ConfigurationID is not set yet" );
		
		Object[] addValues = {
			getConfigurationID(), getUtility(), getGroupAddress(),
			getDivision(), getSubstation(), getRateFamily(),
			getRateMember(), getRateHierarchy()
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
			setConfigurationID( (Integer) results[0] );
			setUtility( (Integer) results[1] );
			setGroupAddress( (Integer) results[2] );
			setDivision( (Integer) results[3] );
			setSubstation( (Integer) results[4] );
			setRateFamily( (Integer) results[5] );
			setRateMember( (Integer) results[6] );
			setRateHierarchy( (Integer) results[7] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getUtility(), getGroupAddress(), getDivision(), getSubstation(),
			getRateFamily(), getRateMember(), getRateHierarchy()
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
	public Integer getDivision() {
		return division;
	}

	/**
	 * @return
	 */
	public Integer getGroupAddress() {
		return groupAddress;
	}

	/**
	 * @return
	 */
	public Integer getRateFamily() {
		return rateFamily;
	}

	/**
	 * @return
	 */
	public Integer getRateHierarchy() {
		return rateHierarchy;
	}

	/**
	 * @return
	 */
	public Integer getRateMember() {
		return rateMember;
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
	public Integer getUtility() {
		return utility;
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
	public void setDivision(Integer integer) {
		division = integer;
	}

	/**
	 * @param integer
	 */
	public void setGroupAddress(Integer integer) {
		groupAddress = integer;
	}

	/**
	 * @param integer
	 */
	public void setRateFamily(Integer integer) {
		rateFamily = integer;
	}

	/**
	 * @param integer
	 */
	public void setRateHierarchy(Integer integer) {
		rateHierarchy = integer;
	}

	/**
	 * @param integer
	 */
	public void setRateMember(Integer integer) {
		rateMember = integer;
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
	public void setUtility(Integer integer) {
		utility = integer;
	}

}

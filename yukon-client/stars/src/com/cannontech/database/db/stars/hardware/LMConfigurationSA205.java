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
public class LMConfigurationSA205 extends DBPersistent {
	
	private Integer configurationID = null;
	private Integer slot1 = new Integer(0);
	private Integer slot2 = new Integer(0);
	private Integer slot3 = new Integer(0);
	private Integer slot4 = new Integer(0);
	private Integer slot5 = new Integer(0);
	private Integer slot6 = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"Slot1", "Slot2", "Slot3", "Slot4", "Slot5", "Slot6"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ConfigurationID" };
	
	public static final String TABLE_NAME = "LMConfigurationSA205";

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getConfigurationID() == null)
			throw new SQLException( "ConfigurationID is not set yet" );
		
		Object[] addValues = {
			getConfigurationID(), getSlot1(), getSlot2(),
			getSlot3(), getSlot4(), getSlot5(), getSlot6()
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
			setSlot1( (Integer) results[0] );
			setSlot2( (Integer) results[1] );
			setSlot3( (Integer) results[2] );
			setSlot4( (Integer) results[3] );
			setSlot5( (Integer) results[4] );
			setSlot6( (Integer) results[5] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getSlot1(), getSlot2(), getSlot3(),
			getSlot4(), getSlot5(), getSlot6()
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
	public Integer getSlot1() {
		return slot1;
	}

	/**
	 * @return
	 */
	public Integer getSlot2() {
		return slot2;
	}

	/**
	 * @return
	 */
	public Integer getSlot3() {
		return slot3;
	}

	/**
	 * @return
	 */
	public Integer getSlot4() {
		return slot4;
	}

	/**
	 * @return
	 */
	public Integer getSlot5() {
		return slot5;
	}

	/**
	 * @return
	 */
	public Integer getSlot6() {
		return slot6;
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
	public void setSlot1(Integer integer) {
		slot1 = integer;
	}

	/**
	 * @param integer
	 */
	public void setSlot2(Integer integer) {
		slot2 = integer;
	}

	/**
	 * @param integer
	 */
	public void setSlot3(Integer integer) {
		slot3 = integer;
	}

	/**
	 * @param integer
	 */
	public void setSlot4(Integer integer) {
		slot4 = integer;
	}

	/**
	 * @param integer
	 */
	public void setSlot5(Integer integer) {
		slot5 = integer;
	}

	/**
	 * @param integer
	 */
	public void setSlot6(Integer integer) {
		slot6 = integer;
	}

}

package com.cannontech.database.db.stars.appliance;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ApplianceHeatPump extends DBPersistent {
	
	private Integer applianceID = null;
	private Integer pumpTypeID = new Integer( CtiUtilities.NONE_ID );
	private Integer standBySourceID = new Integer( CtiUtilities.NONE_ID );
	private Integer secondsDelayToRestart = new Integer(0);
	
	public static final String[] SETTER_COLUMNS = {
		"PumpTypeID", "StandBySourceID", "SecondsDelayToRestart"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };
	
	public static final String TABLE_NAME = "ApplianceHeatPump";
	
	public ApplianceHeatPump() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceID(), getPumpTypeID(), getStandBySourceID(), getSecondsDelayToRestart()
		};
		
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getApplianceID() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getApplianceID() };
		
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setPumpTypeID( (Integer) results[0] );
			setStandBySourceID( (Integer) results[1] );
			setSecondsDelayToRestart( (Integer) results[2] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getPumpTypeID(), getStandBySourceID(), getSecondsDelayToRestart()
		};
		Object[] constraintValues = { getApplianceID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * Returns the applianceID.
	 * @return Integer
	 */
	public Integer getApplianceID() {
		return applianceID;
	}

	/**
	 * Returns the pumpTypeID.
	 * @return Integer
	 */
	public Integer getPumpTypeID() {
		return pumpTypeID;
	}

	/**
	 * Returns the secondsDelayToRestart.
	 * @return Integer
	 */
	public Integer getSecondsDelayToRestart() {
		return secondsDelayToRestart;
	}

	/**
	 * Returns the standBySourceID.
	 * @return Integer
	 */
	public Integer getStandBySourceID() {
		return standBySourceID;
	}

	/**
	 * Sets the pumpTypeID.
	 * @param pumpTypeID The pumpTypeID to set
	 */
	public void setPumpTypeID(Integer pumpTypeID) {
		this.pumpTypeID = pumpTypeID;
	}

	/**
	 * Sets the secondsDelayToRestart.
	 * @param secondsDelayToRestart The secondsDelayToRestart to set
	 */
	public void setSecondsDelayToRestart(Integer secondsDelayToRestart) {
		this.secondsDelayToRestart = secondsDelayToRestart;
	}

	/**
	 * Sets the standBySourceID.
	 * @param standBySourceID The standBySourceID to set
	 */
	public void setStandBySourceID(Integer standBySourceID) {
		this.standBySourceID = standBySourceID;
	}

}

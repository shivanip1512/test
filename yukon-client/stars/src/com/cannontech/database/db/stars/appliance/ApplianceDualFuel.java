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
public class ApplianceDualFuel extends DBPersistent {
	
	private Integer applianceID = null;
	private Integer swithOverTypeID = new Integer( CtiUtilities.NONE_ID );
	private Integer secondaryKWCapacity = new Integer(0);
	private Integer secondaryEnergySourceID = new Integer( CtiUtilities.NONE_ID );
	
	public static final String[] SETTER_COLUMNS = {
		"SwitchOverTypeID", "SecondaryKWCapacity", "SecondaryEnergySourceID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };
	
	public static final String TABLE_NAME = "ApplianceDualFuel";
	
	public ApplianceDualFuel() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceID(), getSwithOverTypeID(), getSecondaryKWCapacity(), getSecondaryEnergySourceID()
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
			setSwithOverTypeID( (Integer) results[0] );
			setSecondaryKWCapacity( (Integer) results[1] );
			setSecondaryEnergySourceID( (Integer) results[2] );
		}
		else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getSwithOverTypeID(), getSecondaryKWCapacity(), getSecondaryEnergySourceID()
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
	 * Returns the secondaryEnergySourceID.
	 * @return Integer
	 */
	public Integer getSecondaryEnergySourceID() {
		return secondaryEnergySourceID;
	}

	/**
	 * Returns the secondaryKWCapacity.
	 * @return Integer
	 */
	public Integer getSecondaryKWCapacity() {
		return secondaryKWCapacity;
	}

	/**
	 * Returns the swithOverTypeID.
	 * @return Integer
	 */
	public Integer getSwithOverTypeID() {
		return swithOverTypeID;
	}

	/**
	 * Sets the applianceID.
	 * @param applianceID The applianceID to set
	 */
	public void setApplianceID(Integer applianceID) {
		this.applianceID = applianceID;
	}

	/**
	 * Sets the secondaryEnergySourceID.
	 * @param secondaryEnergySourceID The secondaryEnergySourceID to set
	 */
	public void setSecondaryEnergySourceID(Integer secondaryEnergySourceID) {
		this.secondaryEnergySourceID = secondaryEnergySourceID;
	}

	/**
	 * Sets the secondaryKWCapacity.
	 * @param secondaryKWCapacity The secondaryKWCapacity to set
	 */
	public void setSecondaryKWCapacity(Integer secondaryKWCapacity) {
		this.secondaryKWCapacity = secondaryKWCapacity;
	}

	/**
	 * Sets the swithOverTypeID.
	 * @param swithOverTypeID The swithOverTypeID to set
	 */
	public void setSwithOverTypeID(Integer swithOverTypeID) {
		this.swithOverTypeID = swithOverTypeID;
	}

}

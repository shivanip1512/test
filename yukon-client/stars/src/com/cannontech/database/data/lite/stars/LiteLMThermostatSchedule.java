/*
 * Created on May 11, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteLMThermostatSchedule extends LiteBase {
	
	private String scheduleName = null;
	private int thermostatTypeID = 0;
	private int accountID = CtiUtilities.NONE_ZERO_ID;
	private int inventoryID = CtiUtilities.NONE_ZERO_ID;
	
	private ArrayList thermostatSeasons = null;		// List of LiteLMThermostatSeason
	
	public LiteLMThermostatSchedule() {
		super();
		setLiteType( LiteTypes.STARS_THERMOSTAT_SCHEDULE );
	}
	
	public LiteLMThermostatSchedule(int scheduleID) {
		super();
		setScheduleID( scheduleID );
		setLiteType( LiteTypes.STARS_THERMOSTAT_SCHEDULE );
	}
	
	public int getScheduleID() {
		return getLiteID();
	}
	
	public void setScheduleID(int scheduleID) {
		setLiteID( scheduleID );
	}

	/**
	 * @return
	 */
	public int getAccountID() {
		return accountID;
	}

	/**
	 * @return
	 */
	public int getInventoryID() {
		return inventoryID;
	}

	/**
	 * @return
	 */
	public String getScheduleName() {
		return scheduleName;
	}

	/**
	 * @return
	 */
	public int getThermostatTypeID() {
		return thermostatTypeID;
	}

	/**
	 * @param i
	 */
	public void setAccountID(int i) {
		accountID = i;
	}

	/**
	 * @param i
	 */
	public void setInventoryID(int i) {
		inventoryID = i;
	}

	/**
	 * @param string
	 */
	public void setScheduleName(String string) {
		scheduleName = string;
	}

	/**
	 * @param i
	 */
	public void setThermostatTypeID(int i) {
		thermostatTypeID = i;
	}

	/**
	 * Returns the thermostatSeasons.
	 * @return java.util.ArrayList
	 */
	public ArrayList getThermostatSeasons() {
		if (thermostatSeasons == null)
			thermostatSeasons = new java.util.ArrayList();
		return thermostatSeasons;
	}

	/**
	 * Sets the thermostatSeasons.
	 * @param thermostatSeasons The thermostatSeasons to set
	 */
	public void setThermostatSeasons(ArrayList thermostatSeasons) {
		this.thermostatSeasons = thermostatSeasons;
	}

}

package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteLMThermostatSeasonEntry extends LiteBase {
	
	private int entryID = com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry.NONE_INT;
	private int seasonID = com.cannontech.database.db.stars.hardware.LMThermostatSeason.NONE_INT;
	private int timeOfWeekID = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private int startTime = 0;
	private int temperature = 0;

	public LiteLMThermostatSeasonEntry() {
		super();
		setLiteType( LiteTypes.STARS_THERMOSTAT_SEASON_ENTRY );
	}

	public LiteLMThermostatSeasonEntry(int entryID) {
		super();
		setEntryID( entryID );
		setLiteType( LiteTypes.STARS_THERMOSTAT_SEASON_ENTRY );
	}
	
	public int getEntryID() {
		return getLiteID();
	}
	
	public void setEntryID(int entryID) {
		setLiteID( entryID );
	}
	
	/**
	 * Returns the seasonID.
	 * @return int
	 */
	public int getSeasonID() {
		return seasonID;
	}

	/**
	 * Returns the startTime.
	 * @return int
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * Returns the temperature.
	 * @return int
	 */
	public int getTemperature() {
		return temperature;
	}

	/**
	 * Returns the timeOfWeekID.
	 * @return int
	 */
	public int getTimeOfWeekID() {
		return timeOfWeekID;
	}

	/**
	 * Sets the seasonID.
	 * @param seasonID The seasonID to set
	 */
	public void setSeasonID(int seasonID) {
		this.seasonID = seasonID;
	}

	/**
	 * Sets the startTime.
	 * @param startTime The startTime to set
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	/**
	 * Sets the temperature.
	 * @param temperature The temperature to set
	 */
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	/**
	 * Sets the timeOfWeekID.
	 * @param timeOfWeekID The timeOfWeekID to set
	 */
	public void setTimeOfWeekID(int timeOfWeekID) {
		this.timeOfWeekID = timeOfWeekID;
	}

}

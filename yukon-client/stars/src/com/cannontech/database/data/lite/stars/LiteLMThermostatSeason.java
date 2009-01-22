package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
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
public class LiteLMThermostatSeason extends LiteBase {
	
	private int scheduleID = CtiUtilities.NONE_ZERO_ID;
	private int webConfigurationID = CtiUtilities.NONE_ZERO_ID;
	private long coolStartDate = 0;
	private long heatStartDate = 0;
	private List<LiteLMThermostatSeasonEntry> seasonEntries = null;
	
	public LiteLMThermostatSeason() {
		super();
		setLiteType( LiteTypes.STARS_THERMOSTAT_SEASON );
	}
	
	public LiteLMThermostatSeason(int seasonID) {
		super();
		setSeasonID( seasonID );
		setLiteType( LiteTypes.STARS_THERMOSTAT_SEASON );
	}
	
	public int getSeasonID() {
		return getLiteID();
	}
	
	public void setSeasonID(int seasonID) {
		setLiteID( seasonID );
	}

	/**
	 * Returns the scheduleID.
	 * @return int
	 */
	public int getScheduleID() {
		return scheduleID;
	}

	/**
	 * Returns the startDate.
	 * @return long
	 */
	public long getCoolStartDate() {
		return coolStartDate;
	}
	
	public long getHeatStartDate() {
		return heatStartDate;
	}

	/**
	 * Returns the webConfigurationID.
	 * @return int
	 */
	public int getWebConfigurationID() {
		return webConfigurationID;
	}

	/**
	 * Sets the scheduleID.
	 * @param scheduleID The scheduleID to set
	 */
	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	/**
	 * Sets the startDate.
	 * @param coolStartDate The startDate to set
	 */
	public void setCoolStartDate(long coolStartDate) {
		this.coolStartDate = coolStartDate;
	}
	
	public void setHeatStartDate(long heatStartDate) {
		this.heatStartDate = heatStartDate;
	}

	/**
	 * Sets the webConfigurationID.
	 * @param webConfigurationID The webConfigurationID to set
	 */
	public void setWebConfigurationID(int webConfigurationID) {
		this.webConfigurationID = webConfigurationID;
	}

	/**
	 * Returns the seasonEntries.
	 * @return java.util.ArrayList
	 */
	public List<LiteLMThermostatSeasonEntry> getSeasonEntries() {
		if (seasonEntries == null)
			seasonEntries = new ArrayList<LiteLMThermostatSeasonEntry>();
		return seasonEntries;
	}

	/**
	 * Sets the seasonEntries.
	 * @param seasonEntries The seasonEntries to set
	 */
	public void setSeasonEntries(List<LiteLMThermostatSeasonEntry> seasonEntries) {
		this.seasonEntries = seasonEntries;
	}

}

package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import com.cannontech.database.data.lite.LiteBase;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsThermostatSettings extends LiteBase {
	
	private ArrayList thermostatSeasons = null;		// List of LiteLMThermostatSeason
	private ArrayList thermostatManualEvents = null;	// List of LMThermostatManualEvent
	
	public LiteStarsThermostatSettings() {
		super();
	}
	
	public LiteStarsThermostatSettings(int inventoryID) {
		super();
		setInventoryID( inventoryID );
	}
	
	public int getInventoryID() {
		return getLiteID();
	}
	
	public void setInventoryID(int inventoryID) {
		setLiteID( inventoryID );
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

	/**
	 * Returns the thermostatOption.
	 * @return LiteLMThermostatManualOption
	 */
	public ArrayList getThermostatManualEvents() {
		if (thermostatManualEvents == null)
			thermostatManualEvents = new ArrayList();
		return thermostatManualEvents;
	}

	/**
	 * Sets the thermostatOption.
	 * @param thermostatOption The thermostatOption to set
	 */
	public void setThermostatManualEvents(ArrayList thermostatManualEvents) {
		this.thermostatManualEvents = thermostatManualEvents;
	}

}

package com.cannontech.database.data.lite.stars;

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
	
	private java.util.ArrayList thermostatSeasons = null;	// List of LiteLMThermostatSeason
	private LiteLMThermostatManualOption thermostatOption = null;
	
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
	public java.util.ArrayList getThermostatSeasons() {
		return thermostatSeasons;
	}

	/**
	 * Sets the thermostatSeasons.
	 * @param thermostatSeasons The thermostatSeasons to set
	 */
	public void setThermostatSeasons(java.util.ArrayList thermostatSeasons) {
		this.thermostatSeasons = thermostatSeasons;
	}

	/**
	 * Returns the thermostatOption.
	 * @return LiteLMThermostatManualOption
	 */
	public LiteLMThermostatManualOption getThermostatOption() {
		return thermostatOption;
	}

	/**
	 * Sets the thermostatOption.
	 * @param thermostatOption The thermostatOption to set
	 */
	public void setThermostatOption(LiteLMThermostatManualOption thermostatOption) {
		this.thermostatOption = thermostatOption;
	}

}

package com.cannontech.stars.database.data.lite;

import java.util.ArrayList;
import java.util.List;

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
	
	private List<LiteLMThermostatManualEvent> thermostatManualEvents = null;	// List of LMThermostatManualEvent
	
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
	 * Returns the thermostatOption.
	 * @return LiteLMThermostatManualOption
	 */
	public List<LiteLMThermostatManualEvent> getThermostatManualEvents() {
		if (thermostatManualEvents == null)
			thermostatManualEvents = new ArrayList<LiteLMThermostatManualEvent>();
		return thermostatManualEvents;
	}

	/**
	 * Sets the thermostatOption.
	 * @param thermostatOption The thermostatOption to set
	 */
	public void setThermostatManualEvents(List<LiteLMThermostatManualEvent> thermostatManualEvents) {
		this.thermostatManualEvents = thermostatManualEvents;
	}
}

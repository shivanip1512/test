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
public class LiteLMThermostatSeason extends LiteBase {
	
	private int inventoryID = com.cannontech.database.db.stars.hardware.InventoryBase.NONE_INT;
	private int webConfigurationID = com.cannontech.database.db.web.YukonWebConfiguration.NONE_INT;
	private long startDate = 0;
	private int displayOrder = 0;
	private java.util.ArrayList seasonEntries = null;	// List of LiteLMThermostatSeasonEntry
	
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
	 * Returns the displayOrder.
	 * @return int
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * Returns the inventoryID.
	 * @return int
	 */
	public int getInventoryID() {
		return inventoryID;
	}

	/**
	 * Returns the startDate.
	 * @return long
	 */
	public long getStartDate() {
		return startDate;
	}

	/**
	 * Returns the webConfigurationID.
	 * @return int
	 */
	public int getWebConfigurationID() {
		return webConfigurationID;
	}

	/**
	 * Sets the displayOrder.
	 * @param displayOrder The displayOrder to set
	 */
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	/**
	 * Sets the inventoryID.
	 * @param inventoryID The inventoryID to set
	 */
	public void setInventoryID(int inventoryID) {
		this.inventoryID = inventoryID;
	}

	/**
	 * Sets the startDate.
	 * @param startDate The startDate to set
	 */
	public void setStartDate(long startDate) {
		this.startDate = startDate;
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
	public java.util.ArrayList getSeasonEntries() {
		if (seasonEntries == null)
			seasonEntries = new java.util.ArrayList();
		return seasonEntries;
	}

	/**
	 * Sets the seasonEntries.
	 * @param seasonEntries The seasonEntries to set
	 */
	public void setSeasonEntries(java.util.ArrayList seasonEntries) {
		this.seasonEntries = seasonEntries;
	}

}

package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonListFuncs;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsLMHardware extends LiteLMHardwareBase {
	
	public static final int THERMOSTAT_TYPE_UNKNOWN = -1;
	public static final int THERMOSTAT_TYPE_IS_NOT = 0;
	public static final int THERMOSTAT_TYPE_ONE_WAY = 1;
	public static final int THERMOSTAT_TYPE_TWO_WAY = 2;
	
	private ArrayList lmHardwareHistory = null;	// List of LiteLMCustomerEvent
	private int deviceStatus = CtiUtilities.NONE_ID;
	private int thermostatType = THERMOSTAT_TYPE_UNKNOWN;
	private LiteStarsThermostatSettings thermostatSettings = null;
	
	private boolean extended = false;
	
	public LiteStarsLMHardware() {
		super();
	}
	
	public LiteStarsLMHardware(int invID) {
		super(invID);
	}

	/**
	 * Returns the hardwareHistory.
	 * @return com.cannontech.stars.xml.serialize.StarsLMHardwareHistory
	 */
	public ArrayList getLmHardwareHistory() {
		if (lmHardwareHistory == null)
			lmHardwareHistory = new ArrayList();
		return lmHardwareHistory;
	}

	/**
	 * Sets the hardwareHistory.
	 * @param hardwareHistory The hardwareHistory to set
	 */
	public void setLmHardwareHistory(ArrayList lmHardwareHistory) {
		this.lmHardwareHistory = lmHardwareHistory;
	}

	/**
	 * Returns the deviceStatus.
	 * @return int
	 */
	public int getDeviceStatus() {
		if (deviceStatus == CtiUtilities.NONE_ID)
			updateDeviceStatus();
		return deviceStatus;
	}
	
	public void updateDeviceStatus() {
		ArrayList hwHist = getLmHardwareHistory();
		
		for (int i = hwHist.size() - 1; i >= 0; i--) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) hwHist.get(i);
			YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
			
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL)
			{
				deviceStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;
				return;
			}
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION)
			{
				deviceStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL;
				return;
			}
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
			{
				deviceStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
				return;
			}
		}
		
		deviceStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
	}

	/**
	 * Returns the thermostatSettings.
	 * @return LiteStarsThermostatSettings
	 */
	public LiteStarsThermostatSettings getThermostatSettings() {
		return thermostatSettings;
	}

	/**
	 * Sets the thermostatSettings.
	 * @param thermostatSettings The thermostatSettings to set
	 */
	public void setThermostatSettings(LiteStarsThermostatSettings thermostatSettings) {
		this.thermostatSettings = thermostatSettings;
	}

	/**
	 * Returns the extended.
	 * @return boolean
	 */
	public boolean isExtended() {
		return extended;
	}

	/**
	 * Sets the extended.
	 * @param extended The extended to set
	 */
	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	/**
	 * Returns the thermostatType.
	 * @return int
	 */
	public int getThermostatType() {
		if (thermostatType == THERMOSTAT_TYPE_UNKNOWN)
			updateThermostatType();
		return thermostatType;
	}
	
	public void updateThermostatType() {
		if (getInventoryID() < 0) {
			// Default hardware is one-way thermostat
			thermostatType = THERMOSTAT_TYPE_ONE_WAY;
		}
		else {
			YukonListEntry invCatEntry = YukonListFuncs.getYukonListEntry( getCategoryID() );
			YukonListEntry devTypeEntry = YukonListFuncs.getYukonListEntry( getLmHardwareTypeID() );
			if (invCatEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC &&
				devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_THERMOSTAT)
			{
				thermostatType = THERMOSTAT_TYPE_ONE_WAY;
			}
			else if (invCatEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC &&
				devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
			{
				thermostatType = THERMOSTAT_TYPE_TWO_WAY;
			}
			else {
				thermostatType = THERMOSTAT_TYPE_IS_NOT;
			}
		}
	}
	
	public boolean isOneWayThermostat() {
		return getThermostatType() == THERMOSTAT_TYPE_ONE_WAY;
	}
	
	public boolean isTwoWayThermostat() {
		return getThermostatType() == THERMOSTAT_TYPE_TWO_WAY;
	}
	
	public boolean isThermostat() {
		return isOneWayThermostat() || isTwoWayThermostat();
	}

}

package com.cannontech.database.data.lite.stars;

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
public class LiteStarsLMHardware extends LiteInventoryBase {
	
	public static final int THERMOSTAT_TYPE_UNKNOWN = -1;
	public static final int THERMOSTAT_TYPE_IS_NOT = 0;
	public static final int THERMOSTAT_TYPE_ONE_WAY = 1;
	public static final int THERMOSTAT_TYPE_TWO_WAY = 2;
	
	private String manufactureSerialNumber = null;
	private int lmHardwareTypeID = CtiUtilities.NONE_ID;
	
	// Extended fields
	private int thermostatType = THERMOSTAT_TYPE_UNKNOWN;
	private LiteStarsThermostatSettings thermostatSettings = null;
	
	public LiteStarsLMHardware() {
		super();
	}
	
	public LiteStarsLMHardware(int invID) {
		super(invID);
	}

	/**
	 * @return
	 */
	public int getLmHardwareTypeID() {
		return lmHardwareTypeID;
	}

	/**
	 * @return
	 */
	public String getManufactureSerialNumber() {
		return manufactureSerialNumber;
	}

	/**
	 * @param i
	 */
	public void setLmHardwareTypeID(int i) {
		lmHardwareTypeID = i;
	}

	/**
	 * @param string
	 */
	public void setManufactureSerialNumber(String string) {
		manufactureSerialNumber = string;
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

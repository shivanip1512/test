package com.cannontech.database.data.lite.stars;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.stars.hardware.LMConfigurationBase;

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
	
	private String manufacturerSerialNumber = null;
	private int lmHardwareTypeID = CtiUtilities.NONE_ID;
	private int routeID = CtiUtilities.NONE_ID;
	private int configurationID = CtiUtilities.NONE_ID;
	
	// Extended fields
	private int thermostatType = THERMOSTAT_TYPE_UNKNOWN;
	private LiteStarsThermostatSettings thermostatSettings = null;
	private LiteLMConfiguration lmConfig_ = null;
	
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
	public String getManufacturerSerialNumber() {
		return manufacturerSerialNumber;
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
	public void setManufacturerSerialNumber(String string) {
		manufacturerSerialNumber = string;
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
	 * @return
	 */
	public int getRouteID() {
		return routeID;
	}

	/**
	 * @param i
	 */
	public void setRouteID(int i) {
		routeID = i;
	}

	/**
	 * @return
	 */
	public int getConfigurationID() {
		return configurationID;
	}

	/**
	 * @param i
	 */
	public void setConfigurationID(int i) {
		configurationID = i;
	}
	
	public LiteLMConfiguration getLMConfiguration() {
		if (lmConfig_ == null && getConfigurationID() > 0) {
			LMConfigurationBase config = LMConfigurationBase.getLMConfiguration( getConfigurationID(), getLmHardwareTypeID() );
			lmConfig_ = new LiteLMConfiguration();
			StarsLiteFactory.setLiteLMConfiguration( lmConfig_, config );
		}
		return lmConfig_;
	}
	
	public void setLMConfiguration(LiteLMConfiguration lmConfig) {
		lmConfig_ = lmConfig;
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
		YukonListEntry invCatEntry = YukonListFuncs.getYukonListEntry( getCategoryID() );
		YukonListEntry devTypeEntry = YukonListFuncs.getYukonListEntry( getLmHardwareTypeID() );
		
		if (invCatEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC &&
			(devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT ||
			devTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT))
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

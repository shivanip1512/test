package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import com.cannontech.common.constants.*;
import com.cannontech.common.util.CtiUtilities;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsLMHardware extends LiteLMHardwareBase {

	private ArrayList lmHardwareHistory = null;	// List of LiteLMCustomerEvent
	private int deviceStatus = CtiUtilities.NONE_ID;
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

}

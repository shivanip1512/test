/*
 * Created on Nov 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteInventoryBase extends LiteBase {

	private int accountID = CtiUtilities.NONE_ID;
	private int categoryID = CtiUtilities.NONE_ID;
	private int installationCompanyID = CtiUtilities.NONE_ID;
	private long receiveDate = 0;
	private long installDate = 0;
	private long removeDate = 0;
	private String alternateTrackingNumber = null;
	private int voltageID = CtiUtilities.NONE_ID;
	private String notes = null;
	private int deviceID = CtiUtilities.NONE_ID;
	private String deviceLabel = null;
	
	// Extended fields
	private ArrayList inventoryHistory = null;		// List of LiteLMHardwareEvent
	private int deviceStatus = CtiUtilities.NONE_ID;
	
	private boolean extended = false;
	
	public LiteInventoryBase() {
		super();
		setLiteType( LiteTypes.STARS_LMHARDWARE );
	}
	
	public LiteInventoryBase(int invID) {
		super();
		setInventoryID( invID );
		setLiteType( LiteTypes.STARS_LMHARDWARE );
	}
	
	public int getInventoryID() {
		return getLiteID();
	}
	
	public void setInventoryID(int invID) {
		setLiteID( invID );
	}

	/**
	 * @return
	 */
	public int getAccountID() {
		return accountID;
	}

	/**
	 * @return
	 */
	public String getAlternateTrackingNumber() {
		return alternateTrackingNumber;
	}

	/**
	 * @return
	 */
	public int getCategoryID() {
		return categoryID;
	}

	/**
	 * @return
	 */
	public String getDeviceLabel() {
		return deviceLabel;
	}

	/**
	 * @return
	 */
	public int getInstallationCompanyID() {
		return installationCompanyID;
	}

	/**
	 * @return
	 */
	public long getInstallDate() {
		return installDate;
	}

	/**
	 * @return
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @return
	 */
	public long getReceiveDate() {
		return receiveDate;
	}

	/**
	 * @return
	 */
	public long getRemoveDate() {
		return removeDate;
	}

	/**
	 * @return
	 */
	public int getVoltageID() {
		return voltageID;
	}

	/**
	 * @param i
	 */
	public void setAccountID(int i) {
		accountID = i;
	}

	/**
	 * @param string
	 */
	public void setAlternateTrackingNumber(String string) {
		alternateTrackingNumber = string;
	}

	/**
	 * @param i
	 */
	public void setCategoryID(int i) {
		categoryID = i;
	}

	/**
	 * @param string
	 */
	public void setDeviceLabel(String string) {
		deviceLabel = string;
	}

	/**
	 * @param i
	 */
	public void setInstallationCompanyID(int i) {
		installationCompanyID = i;
	}

	/**
	 * @param l
	 */
	public void setInstallDate(long l) {
		installDate = l;
	}

	/**
	 * @param string
	 */
	public void setNotes(String string) {
		notes = string;
	}

	/**
	 * @param l
	 */
	public void setReceiveDate(long l) {
		receiveDate = l;
	}

	/**
	 * @param l
	 */
	public void setRemoveDate(long l) {
		removeDate = l;
	}

	/**
	 * @param i
	 */
	public void setVoltageID(int i) {
		voltageID = i;
	}

	/**
	 * @return
	 */
	public int getDeviceID() {
		return deviceID;
	}

	/**
	 * @param i
	 */
	public void setDeviceID(int i) {
		deviceID = i;
	}

	/**
	 * Returns the hardwareHistory.
	 * @return com.cannontech.stars.xml.serialize.StarsLMHardwareHistory
	 */
	public ArrayList getInventoryHistory() {
		if (inventoryHistory == null)
			inventoryHistory = new ArrayList();
		return inventoryHistory;
	}

	/**
	 * Sets the hardwareHistory.
	 * @param hardwareHistory The hardwareHistory to set
	 */
	public void setInventoryHistory(ArrayList inventoryHistory) {
		this.inventoryHistory = inventoryHistory;
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
		ArrayList invHist = getInventoryHistory();
		
		for (int i = invHist.size() - 1; i >= 0; i--) {
			LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) invHist.get(i);
			YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
			
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED)
			{
				deviceStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;
				return;
			}
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION)
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
		
		deviceStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;
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

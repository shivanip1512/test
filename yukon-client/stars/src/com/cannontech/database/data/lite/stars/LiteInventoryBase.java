/*
 * Created on Nov 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite.stars;

import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.util.InventoryUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteInventoryBase extends LiteBase {
    public static final int NONE_SERVICE_COMPANY_ID = 0;
    
	private int accountID = CtiUtilities.NONE_ZERO_ID;
	private int categoryID = CtiUtilities.NONE_ZERO_ID;
	private int installationCompanyID = NONE_SERVICE_COMPANY_ID;
	private long receiveDate = 0;
	private long installDate = 0;
	private long removeDate = 0;
	private String alternateTrackingNumber = null;
	private int voltageID = CtiUtilities.NONE_ZERO_ID;
	private String notes = null;
	private int deviceID = CtiUtilities.NONE_ZERO_ID;
	private String deviceLabel = null;
    private int currentStateID = CtiUtilities.NONE_ZERO_ID;
    
    private int energyCompanyId;
	
	// Extended fields
	private int deviceStatus = CtiUtilities.NONE_ZERO_ID;
	
	private boolean extended = false;
	
	public LiteInventoryBase() {
		super();
	}
	
	public LiteInventoryBase(int invID) {
		super();
		setInventoryID( invID );
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
	 * Returns the deviceStatus.
	 * @return int
	 */
	public int getDeviceStatus() {
		if (deviceStatus == CtiUtilities.NONE_ZERO_ID)
			updateDeviceStatus();
		return deviceStatus;
	}
	
	public void setDeviceStatus(int deviceStatus) {
	    this.deviceStatus = deviceStatus;
	}
	
	public void updateDeviceStatus() {
		LMHardwareEventDao hardwareEventDao = YukonSpringHook.getBean("hardwareEventDao", LMHardwareEventDao.class);
	    List<LiteLMHardwareEvent> invHist = hardwareEventDao.getByInventoryId(this.getLiteID());
	    updateDeviceStatus(invHist);
	}
	    
	public void updateDeviceStatus(final List<LiteLMHardwareEvent> invHist) {
		boolean isSA = false;
		if (this instanceof LiteStarsLMHardware) {
			int hwConfigType = InventoryUtils.getHardwareConfigType( ((LiteStarsLMHardware)this).getLmHardwareTypeID() );
			isSA = hwConfigType == HardwareConfigType.SA205.getHardwareConfigTypeId() || hwConfigType == HardwareConfigType.SA305.getHardwareConfigTypeId();
		}
		
		for (LiteLMHardwareEvent liteEvent : invHist) {
			YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( liteEvent.getActionID() );
			
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED
				|| isSA && entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG)
			{
				deviceStatus = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;
				return;
			}
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION
				|| entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION)
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

    public int getCurrentStateID() {
        return currentStateID;
    }

    public void setCurrentStateID(int currentStateID) {
        this.currentStateID = currentStateID;
    }
    
    public Integer getEnergyCompanyId() {
		return energyCompanyId;
	}
    
    public void setEnergyCompanyId(Integer energyCompanyId) {
		this.energyCompanyId = energyCompanyId;
	}

}

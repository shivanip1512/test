package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import com.cannontech.common.constants.*;
import com.cannontech.common.util.CtiUtilities;
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
public class LiteLMHardwareBase extends LiteBase {

	private int accountID = com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT;
	private int categoryID = CtiUtilities.NONE_ID;
	private int installationCompanyID = com.cannontech.database.db.stars.report.ServiceCompany.NONE_INT;
	private long receiveDate = 0;
	private long installDate = 0;
	private long removeDate = 0;
	private String alternateTrackingNumber = null;
	private int voltageID = CtiUtilities.NONE_ID;
	private String notes = null;
	private String manufactureSerialNumber = null;
	private int lmHardwareTypeID = CtiUtilities.NONE_ID;
	private String deviceLabel = null;
	
	public LiteLMHardwareBase() {
		super();
		setLiteType( LiteTypes.STARS_LMHARDWARE );
	}
	
	public LiteLMHardwareBase(int invID) {
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
	 * Returns the alternateTrackingNumber.
	 * @return String
	 */
	public String getAlternateTrackingNumber() {
		return alternateTrackingNumber;
	}

	/**
	 * Returns the categoryID.
	 * @return int
	 */
	public int getCategoryID() {
		return categoryID;
	}

	/**
	 * Returns the installationCompanyID.
	 * @return int
	 */
	public int getInstallationCompanyID() {
		return installationCompanyID;
	}

	/**
	 * Returns the installDate.
	 * @return long
	 */
	public long getInstallDate() {
		return installDate;
	}

	/**
	 * Returns the lmHardwareTypeID.
	 * @return int
	 */
	public int getLmHardwareTypeID() {
		return lmHardwareTypeID;
	}

	/**
	 * Returns the manufactureSerialNumber.
	 * @return String
	 */
	public String getManufactureSerialNumber() {
		return manufactureSerialNumber;
	}

	/**
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Returns the receiveDate.
	 * @return long
	 */
	public long getReceiveDate() {
		return receiveDate;
	}

	/**
	 * Returns the removeDate.
	 * @return long
	 */
	public long getRemoveDate() {
		return removeDate;
	}

	/**
	 * Returns the voltageID.
	 * @return int
	 */
	public int getVoltageID() {
		return voltageID;
	}

	/**
	 * Sets the alternateTrackingNumber.
	 * @param alternateTrackingNumber The alternateTrackingNumber to set
	 */
	public void setAlternateTrackingNumber(String alternateTrackingNumber) {
		this.alternateTrackingNumber = alternateTrackingNumber;
	}

	/**
	 * Sets the categoryID.
	 * @param categoryID The categoryID to set
	 */
	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	/**
	 * Sets the installationCompanyID.
	 * @param installationCompanyID The installationCompanyID to set
	 */
	public void setInstallationCompanyID(int installationCompanyID) {
		this.installationCompanyID = installationCompanyID;
	}

	/**
	 * Sets the installDate.
	 * @param installDate The installDate to set
	 */
	public void setInstallDate(long installDate) {
		this.installDate = installDate;
	}

	/**
	 * Sets the lmHardwareTypeID.
	 * @param lmHardwareTypeID The lmHardwareTypeID to set
	 */
	public void setLmHardwareTypeID(int lmHardwareTypeID) {
		this.lmHardwareTypeID = lmHardwareTypeID;
	}

	/**
	 * Sets the manufactureSerialNumber.
	 * @param manufactureSerialNumber The manufactureSerialNumber to set
	 */
	public void setManufactureSerialNumber(String manufactureSerialNumber) {
		this.manufactureSerialNumber = manufactureSerialNumber;
	}

	/**
	 * Sets the notes.
	 * @param notes The notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Sets the receiveDate.
	 * @param receiveDate The receiveDate to set
	 */
	public void setReceiveDate(long receiveDate) {
		this.receiveDate = receiveDate;
	}

	/**
	 * Sets the removeDate.
	 * @param removeDate The removeDate to set
	 */
	public void setRemoveDate(long removeDate) {
		this.removeDate = removeDate;
	}

	/**
	 * Sets the voltageID.
	 * @param voltageID The voltageID to set
	 */
	public void setVoltageID(int voltageID) {
		this.voltageID = voltageID;
	}

	/**
	 * Returns the accountID.
	 * @return int
	 */
	public int getAccountID() {
		return accountID;
	}

	/**
	 * Sets the accountID.
	 * @param accountID The accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	/**
	 * Returns the deviceLabel.
	 * @return String
	 */
	public String getDeviceLabel() {
		return deviceLabel;
	}

	/**
	 * Sets the deviceLabel.
	 * @param deviceLabel The deviceLabel to set
	 */
	public void setDeviceLabel(String deviceLabel) {
		this.deviceLabel = deviceLabel;
	}

}

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
public class LiteStarsAppliance extends LiteBase {
	
	private int accountID = com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT;
	private int applianceCategoryID = com.cannontech.database.db.stars.appliance.ApplianceCategory.NONE_INT;
	private int lmProgramID = 0;
	private int yearManufactured = 0;
	private int manufacturerID = com.cannontech.common.util.CtiUtilities.NONE_ID;
	private int locationID = com.cannontech.common.util.CtiUtilities.NONE_ID;
	private String notes = null;
	private String modelNumber = null;
	
	private int inventoryID = com.cannontech.database.db.stars.hardware.InventoryBase.NONE_INT;
	private int addressingGroupID = 0;
	
	public LiteStarsAppliance() {
		super();
		setLiteType( LiteTypes.STARS_APPLIANCE );
	}
	
	public LiteStarsAppliance(int appID) {
		super();
		setApplianceID( appID );
		setLiteType( LiteTypes.STARS_APPLIANCE );
	}
	
	public int getApplianceID() {
		return getLiteID();
	}
	
	public void setApplianceID(int appID) {
		setLiteID( appID );
	}

	/**
	 * Returns the accountID.
	 * @return int
	 */
	public int getAccountID() {
		return accountID;
	}

	/**
	 * Returns the applianceCategoryID.
	 * @return int
	 */
	public int getApplianceCategoryID() {
		return applianceCategoryID;
	}

	/**
	 * Returns the lmProgramID.
	 * @return int
	 */
	public int getLmProgramID() {
		return lmProgramID;
	}

	/**
	 * Returns the locationID.
	 * @return int
	 */
	public int getLocationID() {
		return locationID;
	}

	/**
	 * Returns the manufacturerID.
	 * @return int
	 */
	public int getManufacturerID() {
		return manufacturerID;
	}

	/**
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Returns the yearManufactured.
	 * @return int
	 */
	public int getYearManufactured() {
		return yearManufactured;
	}

	/**
	 * Sets the accountID.
	 * @param accountID The accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	/**
	 * Sets the applianceCategoryID.
	 * @param applianceCategoryID The applianceCategoryID to set
	 */
	public void setApplianceCategoryID(int applianceCategoryID) {
		this.applianceCategoryID = applianceCategoryID;
	}

	/**
	 * Sets the lmProgramID.
	 * @param lmProgramID The lmProgramID to set
	 */
	public void setLmProgramID(int lmProgramID) {
		this.lmProgramID = lmProgramID;
	}

	/**
	 * Sets the locationID.
	 * @param locationID The locationID to set
	 */
	public void setLocationID(int locationID) {
		this.locationID = locationID;
	}

	/**
	 * Sets the manufacturerID.
	 * @param manufacturerID The manufacturerID to set
	 */
	public void setManufacturerID(int manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	/**
	 * Sets the notes.
	 * @param notes The notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Sets the yearManufactured.
	 * @param yearManufactured The yearManufactured to set
	 */
	public void setYearManufactured(int yearManufactured) {
		this.yearManufactured = yearManufactured;
	}

	/**
	 * Returns the addressingGroupID.
	 * @return int
	 */
	public int getAddressingGroupID() {
		return addressingGroupID;
	}

	/**
	 * Returns the inventoryID.
	 * @return int
	 */
	public int getInventoryID() {
		return inventoryID;
	}

	/**
	 * Sets the addressingGroupID.
	 * @param addressingGroupID The addressingGroupID to set
	 */
	public void setAddressingGroupID(int addressingGroupID) {
		this.addressingGroupID = addressingGroupID;
	}

	/**
	 * Sets the inventoryID.
	 * @param inventoryID The inventoryID to set
	 */
	public void setInventoryID(int inventoryID) {
		this.inventoryID = inventoryID;
	}

	/**
	 * Returns the modelNumber.
	 * @return String
	 */
	public String getModelNumber() {
		return modelNumber;
	}

	/**
	 * Sets the modelNumber.
	 * @param modelNumber The modelNumber to set
	 */
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

}

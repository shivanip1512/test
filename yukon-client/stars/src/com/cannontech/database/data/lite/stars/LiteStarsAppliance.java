package com.cannontech.database.data.lite.stars;

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
public class LiteStarsAppliance extends LiteBase {
	
	private int accountID = com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT;
	private int applianceCategoryID = com.cannontech.database.db.stars.appliance.ApplianceCategory.NONE_INT;
	private int programID = 0;
	private int yearManufactured = 0;
	private int manufacturerID = CtiUtilities.NONE_ID;
	private int locationID = CtiUtilities.NONE_ID;
	private String notes = null;
	private String modelNumber = null;
	private int kwCapacity = 0;
	private int efficiencyRating = 0;
	
	private int inventoryID = CtiUtilities.NONE_ID;
	private int addressingGroupID = 0;
	private int loadNumber = 0;
	private boolean extended = false;
	
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
	 * Returns the programID.
	 * @return int
	 */
	public int getProgramID() {
		return programID;
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
	 * Sets the programID.
	 * @param programID The programID to set
	 */
	public void setProgramID(int programID) {
		this.programID = programID;
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

	/**
	 * Returns the efficiencyRating.
	 * @return int
	 */
	public int getEfficiencyRating() {
		return efficiencyRating;
	}

	/**
	 * Returns the kwCapacity.
	 * @return int
	 */
	public int getKWCapacity() {
		return kwCapacity;
	}

	/**
	 * Sets the efficiencyRating.
	 * @param efficiencyRating The efficiencyRating to set
	 */
	public void setEfficiencyRating(int efficiencyRating) {
		this.efficiencyRating = efficiencyRating;
	}

	/**
	 * Sets the kwCapacity.
	 * @param kwCapacity The kwCapacity to set
	 */
	public void setKWCapacity(int kwCapacity) {
		this.kwCapacity = kwCapacity;
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
	 * @return
	 */
	public int getLoadNumber() {
		return loadNumber;
	}

	/**
	 * @param i
	 */
	public void setLoadNumber(int i) {
		loadNumber = i;
	}

}

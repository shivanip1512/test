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
public class LiteServiceCompany extends LiteBase {
	
	private String companyName = null;
	private int addressID = 0;
	private String mainPhoneNumber = null;
	private String mainFaxNumber = null;
	private int primaryContactID = 0;
	private String hiType = null;
	
	// Direct owner of this service company, used to tell if it is inherited from the parent company
	private LiteStarsEnergyCompany directOwner = null;
	
	public LiteServiceCompany() {
		super();
		setLiteType( LiteTypes.STARS_SERVICE_COMPANY );
	}
	
	public LiteServiceCompany(int companyID) {
		super();
		setCompanyID( companyID );
		setLiteType( LiteTypes.STARS_SERVICE_COMPANY );
	}
	
	public int getCompanyID() {
		return getLiteID();
	}
	
	public void setCompanyID(int companyID) {
		setLiteID( companyID );
	}

	/**
	 * Returns the addressID.
	 * @return int
	 */
	public int getAddressID() {
		return addressID;
	}

	/**
	 * Returns the companyName.
	 * @return String
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Returns the hiType.
	 * @return String
	 */
	public String getHiType() {
		return hiType;
	}

	/**
	 * Returns the mainFaxNumber.
	 * @return String
	 */
	public String getMainFaxNumber() {
		return mainFaxNumber;
	}

	/**
	 * Returns the mainPhoneNumber.
	 * @return String
	 */
	public String getMainPhoneNumber() {
		return mainPhoneNumber;
	}

	/**
	 * Returns the primaryContactID.
	 * @return int
	 */
	public int getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Sets the addressID.
	 * @param addressID The addressID to set
	 */
	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}

	/**
	 * Sets the companyName.
	 * @param companyName The companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Sets the hiType.
	 * @param hiType The hiType to set
	 */
	public void setHiType(String hiType) {
		this.hiType = hiType;
	}

	/**
	 * Sets the mainFaxNumber.
	 * @param mainFaxNumber The mainFaxNumber to set
	 */
	public void setMainFaxNumber(String mainFaxNumber) {
		this.mainFaxNumber = mainFaxNumber;
	}

	/**
	 * Sets the mainPhoneNumber.
	 * @param mainPhoneNumber The mainPhoneNumber to set
	 */
	public void setMainPhoneNumber(String mainPhoneNumber) {
		this.mainPhoneNumber = mainPhoneNumber;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(int primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * @return
	 */
	public LiteStarsEnergyCompany getDirectOwner() {
		return directOwner;
	}

	/**
	 * @param company
	 */
	public void setDirectOwner(LiteStarsEnergyCompany company) {
		directOwner = company;
	}

}

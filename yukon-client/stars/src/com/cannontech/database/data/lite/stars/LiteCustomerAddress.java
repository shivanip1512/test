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
public class LiteCustomerAddress extends LiteBase {

	private String locationAddress1 = null;
	private String locationAddress2 = null;
	private String cityName = null;
	private String stateCode = null;
	private String zipCode = null;
	
	public LiteCustomerAddress() {
		super();
	}
	
	public LiteCustomerAddress(int addressID) {
		super();
		setAddressID( addressID );
		setLiteType( LiteTypes.STARS_CUSTOMER_ADDRESS );
	}
	
	public LiteCustomerAddress(int addressID, String locationAddr1, String locationAddr2, String city, String state, String zip) {
		super();
		setAddressID( addressID );
		locationAddress1 = locationAddr1;
		locationAddress2 = locationAddr2;
		cityName = city;
		stateCode = state;
		zipCode = zip;
		setLiteType( LiteTypes.STARS_CUSTOMER_ADDRESS );
	}
	
	public int getAddressID() {
		return getLiteID();
	}
	
	public void setAddressID(int addrID) {
		setLiteID( addrID );
	}
	
	/**
	 * Returns the cityName.
	 * @return String
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * Returns the locationAddress1.
	 * @return String
	 */
	public String getLocationAddress1() {
		return locationAddress1;
	}

	/**
	 * Returns the locationAddress2.
	 * @return String
	 */
	public String getLocationAddress2() {
		return locationAddress2;
	}

	/**
	 * Returns the stateCode.
	 * @return String
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * Returns the zipCode.
	 * @return String
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Sets the cityName.
	 * @param cityName The cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * Sets the locationAddress1.
	 * @param locationAddress1 The locationAddress1 to set
	 */
	public void setLocationAddress1(String locationAddress1) {
		this.locationAddress1 = locationAddress1;
	}

	/**
	 * Sets the locationAddress2.
	 * @param locationAddress2 The locationAddress2 to set
	 */
	public void setLocationAddress2(String locationAddress2) {
		this.locationAddress2 = locationAddress2;
	}

	/**
	 * Sets the stateCode.
	 * @param stateCode The stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * Sets the zipCode.
	 * @param zipCode The zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}

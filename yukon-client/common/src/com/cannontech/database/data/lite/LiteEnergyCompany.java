package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteEnergyCompany extends LiteBase {
	private String name;
	private int primaryContactID;
	private int userID;
	
	public LiteEnergyCompany() {
		initialize(0,null,0,0);
	}
	
	public LiteEnergyCompany(int id) {
		initialize(id,null,0,0);
	}
	
	public LiteEnergyCompany(int id, String name, int primaryContactID, int userID) {
		initialize(id,name, primaryContactID, userID);		
	}	
	
	private void initialize(int id, String name, int primaryContactID, int userID) {
		setLiteType(LiteTypes.ENERGY_COMPANY);
		setLiteID(id);
		setName(name);		
		setPrimaryContactID(primaryContactID);
		setUserID(userID);
	}	
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the energyCompanyID.
	 * @return int
	 */
	public int getEnergyCompanyID() {
		return getLiteID();
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(int energyCompanyID) {
		setLiteID(energyCompanyID);
	}

	/**
	 * @return
	 */
	public int getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * @return
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param i
	 */
	public void setPrimaryContactID(int i) {
		primaryContactID = i;
	}

	/**
	 * @param i
	 */
	public void setUserID(int i) {
		userID = i;
	}

}

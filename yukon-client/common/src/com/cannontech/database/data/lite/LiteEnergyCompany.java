package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteEnergyCompany extends LiteBase {
	private String name;
	
	public LiteEnergyCompany() {
		initialize(0,null);
	}
	
	public LiteEnergyCompany(int id) {
		initialize(id,null);
	}
	
	public LiteEnergyCompany(int id, String name) {
		initialize(id,name);		
	}	
	
	private void initialize(int id, String name) {
		setLiteType(LiteTypes.ENERGY_COMPANY);
		setLiteID(id);
		setName(name);		
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

}

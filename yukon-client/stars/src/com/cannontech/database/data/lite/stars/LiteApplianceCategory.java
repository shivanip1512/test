package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

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
public class LiteApplianceCategory extends LiteBase {
	
	private String description = null;
	private int categoryID = com.cannontech.common.util.CtiUtilities.NONE_ID;
	private int webConfigurationID = CtiUtilities.NONE_ID;
	
	// Array of published programs
	private ArrayList publishedPrograms = null;
	
	// Direct owner of this appliance category, used to tell if it is inherited from the parent company
	private LiteStarsEnergyCompany directOwner = null;
	
	public LiteApplianceCategory() {
		super();
	}
	
	public LiteApplianceCategory(int appCatID) {
		super();
		setApplianceCategoryID( appCatID );
		setLiteType( LiteTypes.STARS_APPLIANCE_CATEGORY );
	}
	
	public LiteApplianceCategory(int appCatID, String desc, int catID, int webConfigID) {
		super();
		setApplianceCategoryID( appCatID );
		description = desc;
		categoryID = catID;
		webConfigurationID = webConfigID;
		setLiteType( LiteTypes.STARS_APPLIANCE_CATEGORY );
	}
	
	public int getApplianceCategoryID() {
		return getLiteID();
	}
	
	public void setApplianceCategoryID(int appCatID) {
		setLiteID( appCatID );
	}

	/**
	 * Returns the categoryID.
	 * @return int
	 */
	public int getCategoryID() {
		return categoryID;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the webConfigurationID.
	 * @return int
	 */
	public int getWebConfigurationID() {
		return webConfigurationID;
	}

	/**
	 * Sets the categoryID.
	 * @param categoryID The categoryID to set
	 */
	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the webConfigurationID.
	 * @param webConfigurationID The webConfigurationID to set
	 */
	public void setWebConfigurationID(int webConfigurationID) {
		this.webConfigurationID = webConfigurationID;
	}

	/**
	 * Returns the publishedPrograms.
	 * @return ArrayList
	 */
	public ArrayList getPublishedPrograms() {
		if (publishedPrograms == null)
			publishedPrograms = new ArrayList();
		return publishedPrograms;
	}

	/**
	 * Sets the publishedPrograms.
	 * @param publishedPrograms The publishedPrograms to set
	 */
	public void setPublishedPrograms(ArrayList publishedPrograms) {
		this.publishedPrograms = publishedPrograms;
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

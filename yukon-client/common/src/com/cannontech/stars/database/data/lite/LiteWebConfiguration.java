package com.cannontech.stars.database.data.lite;

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
public class LiteWebConfiguration extends LiteBase {

	private String logoLocation = null;
	private String description = null;
	private String alternateDisplayName = null;
	private String url = null;
	
	public LiteWebConfiguration() {
		super();
	}
	
	public LiteWebConfiguration(int configID) {
		super();
		setConfigID( configID );
		setLiteType( LiteTypes.STARS_CUSTOMER_WEB_CONFIGURATION );
	}
	
	public LiteWebConfiguration(int configID, String logo, String desc, String altName, String url) {
		super();
		setConfigID( configID );
		logoLocation = logo;
		description = desc;
		alternateDisplayName = altName;
		this.url = url;
		setLiteType( LiteTypes.STARS_CUSTOMER_WEB_CONFIGURATION );
	}
	
	public int getConfigID() {
		return getLiteID();
	}
	
	public void setConfigID(int configID) {
		setLiteID( configID );
	}
	
	/**
	 * Returns the alternateDisplayName.
	 * @return String
	 */
	public String getAlternateDisplayName() {
		return alternateDisplayName;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the logoLocation.
	 * @return String
	 */
	public String getLogoLocation() {
		return logoLocation;
	}

	/**
	 * Returns the url.
	 * @return String
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the alternateDisplayName.
	 * @param alternateDisplayName The alternateDisplayName to set
	 */
	public void setAlternateDisplayName(String alternateDisplayName) {
		this.alternateDisplayName = alternateDisplayName;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the logoLocation.
	 * @param logoLocation The logoLocation to set
	 */
	public void setLogoLocation(String logoLocation) {
		this.logoLocation = logoLocation;
	}

	/**
	 * Sets the url.
	 * @param url The url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}

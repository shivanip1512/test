package com.cannontech.database.data.lite.stars;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.db.stars.customer.SiteInformation;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteSiteInformation extends LiteBase {

	private String feeder = null;
	private String pole = null;
	private String transformerSize = null;
	private String serviceVoltage = null;
	private int substationID = com.cannontech.database.db.stars.Substation.NONE_INT;
	
	public LiteSiteInformation() {
		super();
		setLiteType( LiteTypes.STARS_SITE_INFORMATION );
	}
	
	public LiteSiteInformation(int siteID) {
		super();
		setSiteID( siteID );
		setLiteType( LiteTypes.STARS_SITE_INFORMATION );
	}
	
	public int getSiteID() {
		return getLiteID();
	}
	
	public void setSiteID(int siteID) {
		setLiteID( siteID );
	}
	
	public void retrieve() {
		SiteInformation site = new SiteInformation();
		site.setSiteID( new Integer(getSiteID()) );
		try {
			site = (SiteInformation) Transaction.createTransaction(Transaction.RETRIEVE, site).execute();
			StarsLiteFactory.setLiteSiteInformation( this, site );
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	
	/**
	 * Returns the feeder.
	 * @return String
	 */
	public String getFeeder() {
		return feeder;
	}

	/**
	 * Returns the pole.
	 * @return String
	 */
	public String getPole() {
		return pole;
	}

	/**
	 * Returns the serviceVoltage.
	 * @return String
	 */
	public String getServiceVoltage() {
		return serviceVoltage;
	}

	/**
	 * Returns the substationID.
	 * @return int
	 */
	public int getSubstationID() {
		return substationID;
	}

	/**
	 * Returns the transformerSize.
	 * @return String
	 */
	public String getTransformerSize() {
		return transformerSize;
	}

	/**
	 * Sets the feeder.
	 * @param feeder The feeder to set
	 */
	public void setFeeder(String feeder) {
		this.feeder = feeder;
	}

	/**
	 * Sets the pole.
	 * @param pole The pole to set
	 */
	public void setPole(String pole) {
		this.pole = pole;
	}

	/**
	 * Sets the serviceVoltage.
	 * @param serviceVoltage The serviceVoltage to set
	 */
	public void setServiceVoltage(String serviceVoltage) {
		this.serviceVoltage = serviceVoltage;
	}

	/**
	 * Sets the substationID.
	 * @param substationID The substationID to set
	 */
	public void setSubstationID(int substationID) {
		this.substationID = substationID;
	}

	/**
	 * Sets the transformerSize.
	 * @param transformerSize The transformerSize to set
	 */
	public void setTransformerSize(String transformerSize) {
		this.transformerSize = transformerSize;
	}

}

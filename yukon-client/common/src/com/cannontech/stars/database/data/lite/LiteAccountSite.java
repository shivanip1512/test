package com.cannontech.stars.database.data.lite;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.stars.database.db.customer.AccountSite;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteAccountSite extends LiteBase {
	
	private int siteInformationID = com.cannontech.stars.database.db.customer.SiteInformation.NONE_INT;
	private String siteNumber = null;
	private int streetAddressID = 0;
	private String propertyNotes = null;
    private String custAtHome = "N";
    private String custStatus = "A";
	
	public LiteAccountSite() {
		super();
		setLiteType( LiteTypes.STARS_ACCOUNT_SITE );
	}
	
	public LiteAccountSite(int accountSiteID) {
		super();
		setAccountSiteID( accountSiteID );
		setLiteType( LiteTypes.STARS_ACCOUNT_SITE );
	}

	public int getAccountSiteID() {
		return getLiteID();
	}
	
	public void setAccountSiteID(int accountSiteID) {
		setLiteID( accountSiteID );
	}
	
	public void retrieve() {
		AccountSite acctSite = new AccountSite();
		acctSite.setAccountSiteID( new Integer(getAccountSiteID()) );
		try {
			acctSite = (AccountSite) Transaction.createTransaction(Transaction.RETRIEVE, acctSite).execute();
			StarsLiteFactory.setLiteAccountSite( this, acctSite );
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	
	/**
	 * Returns the propertyNotes.
	 * @return String
	 */
	public String getPropertyNotes() {
		return propertyNotes;
	}

	/**
	 * Returns the siteInformationID.
	 * @return int
	 */
	public int getSiteInformationID() {
		return siteInformationID;
	}

	/**
	 * Returns the siteNumber.
	 * @return String
	 */
	public String getSiteNumber() {
		return siteNumber;
	}

	/**
	 * Returns the streetAddressID.
	 * @return int
	 */
	public int getStreetAddressID() {
		return streetAddressID;
	}

	/**
	 * Sets the propertyNotes.
	 * @param propertyNotes The propertyNotes to set
	 */
	public void setPropertyNotes(String propertyNotes) {
		this.propertyNotes = propertyNotes;
	}

	/**
	 * Sets the siteInformationID.
	 * @param siteInformationID The siteInformationID to set
	 */
	public void setSiteInformationID(int siteInformationID) {
		this.siteInformationID = siteInformationID;
	}

	/**
	 * Sets the siteNumber.
	 * @param siteNumber The siteNumber to set
	 */
	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	/**
	 * Sets the streetAddressID.
	 * @param streetAddressID The streetAddressID to set
	 */
	public void setStreetAddressID(int streetAddressID) {
		this.streetAddressID = streetAddressID;
	}

    public String getCustAtHome()
    {
        return custAtHome;
    }

    public void setCustAtHome(String custAtHome)
    {
        this.custAtHome = custAtHome;
    }

    public String getCustStatus() {
        return custStatus;
    }

    public void setCustStatus(String custStatus) {
        this.custStatus = custStatus;
    }

}

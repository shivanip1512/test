package com.cannontech.database.data.stars.customer;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AccountSite extends DBPersistent {

    private com.cannontech.database.db.stars.customer.AccountSite accountSite = null;
    private com.cannontech.database.db.customer.Address streetAddress = null;

    private com.cannontech.database.data.stars.customer.SiteInformation siteInformation = null;

    public AccountSite() {
        super();
    }

    public void setAccountSiteID(Integer newID) {
        getAccountSite().setAccountSiteID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getAccountSite().setDbConnection(conn);
        getStreetAddress().setDbConnection(conn);
        
        if (getSiteInformation() != null)
	        getSiteInformation().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
    	// Delete residence information
    	delete( "CustomerResidence", "AccountSiteID", getAccountSite().getAccountSiteID() );
    	
        getAccountSite().delete();
        getStreetAddress().delete();
        if (getSiteInformation() != null)
        	getSiteInformation().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getAccountSite().getSiteInformationID().intValue() == com.cannontech.database.db.stars.customer.SiteInformation.NONE_INT) {
		    getSiteInformation().add();
		    getAccountSite().setSiteInformationID( getSiteInformation().getSiteInformation().getSiteID() );
    	}
    	
        getStreetAddress().add();
        getAccountSite().setStreetAddressID( getStreetAddress().getAddressID() );
        
        getAccountSite().add();
    }

    public void update() throws java.sql.SQLException {
        getAccountSite().update();
        getStreetAddress().update();
        getSiteInformation().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getAccountSite().retrieve();

        getStreetAddress().setAddressID( getAccountSite().getStreetAddressID() );
        getStreetAddress().retrieve();

        getSiteInformation().setSiteID( getAccountSite().getSiteInformationID() );
        getSiteInformation().retrieve();
    }

    public com.cannontech.database.db.customer.Address getStreetAddress() {
        if (streetAddress == null)
            streetAddress = new com.cannontech.database.db.customer.Address();
        return streetAddress;
    }

    public void setStreetAddress(com.cannontech.database.db.customer.Address newStreetAddress) {
        streetAddress = newStreetAddress;
    }
	/**
	 * Returns the accountSite.
	 * @return com.cannontech.database.db.stars.customer.AccountSite
	 */
	public com.cannontech.database.db.stars.customer.AccountSite getAccountSite() {
		if (accountSite == null)
			accountSite = new com.cannontech.database.db.stars.customer.AccountSite();
		return accountSite;
	}

	/**
	 * Returns the siteInformation.
	 * @return com.cannontech.database.data.stars.customer.SiteInformation
	 */
	public com.cannontech.database.data.stars.customer.SiteInformation getSiteInformation() {
		if (siteInformation == null)
			siteInformation = new com.cannontech.database.data.stars.customer.SiteInformation();
		return siteInformation;
	}

	/**
	 * Sets the accountSite.
	 * @param accountSite The accountSite to set
	 */
	public void setAccountSite(
		com.cannontech.database.db.stars.customer.AccountSite accountSite) {
		this.accountSite = accountSite;
	}

	/**
	 * Sets the siteInformation.
	 * @param siteInformation The siteInformation to set
	 */
	public void setSiteInformation(
		com.cannontech.database.data.stars.customer.SiteInformation siteInformation) {
		this.siteInformation = siteInformation;
	}

}
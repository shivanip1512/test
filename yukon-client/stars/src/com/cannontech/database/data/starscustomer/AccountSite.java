package com.cannontech.database.data.starscustomer;

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

    private com.cannontech.database.db.starscustomer.AccountSite accountSite = null;
    private com.cannontech.database.db.customer.CustomerAddress streetAddress = null;

    private com.cannontech.database.data.starscustomer.SiteInformation siteInformation = null;

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
        getSiteInformation().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getStreetAddress().delete();
        getAccountSite().delete();
        getSiteInformation().delete();
    }

    public void add() throws java.sql.SQLException {
        getAccountSite().add();
        getStreetAddress().add();
        //getSiteInformation().add();
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

    public com.cannontech.database.db.starscustomer.AccountSite getAccountSite() {
        if (accountSite == null)
            accountSite = new com.cannontech.database.db.starscustomer.AccountSite();
        return accountSite;
    }

    public void setAccountSite(com.cannontech.database.db.starscustomer.AccountSite newAccountSite) {
        accountSite = newAccountSite;
    }

    public com.cannontech.database.db.customer.CustomerAddress getStreetAddress() {
        if (streetAddress == null)
            streetAddress = new com.cannontech.database.db.customer.CustomerAddress();
        return streetAddress;
    }

    public void setStreetAddress(com.cannontech.database.db.customer.CustomerAddress newStreetAddress) {
        streetAddress = newStreetAddress;
    }

    public com.cannontech.database.data.starscustomer.SiteInformation getSiteInformation() {
        if (siteInformation == null)
            siteInformation = new com.cannontech.database.data.starscustomer.SiteInformation();
        return siteInformation;
    }

    public void setSiteInformation(com.cannontech.database.data.starscustomer.SiteInformation newSiteInformation) {
        siteInformation = newSiteInformation;
    }
}
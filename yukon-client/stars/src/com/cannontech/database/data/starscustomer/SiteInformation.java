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

public class SiteInformation extends DBPersistent {

    private com.cannontech.database.db.starscustomer.SiteInformation siteInformation = null;
    private com.cannontech.database.db.starscustomer.Substation substation = null;

    public SiteInformation() {
        super();
    }

    public void setSiteID(Integer newID) {
        getSiteInformation().setSiteID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getSiteInformation().setDbConnection(conn);
        getSubstation().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        // Set the SiteInformationID to NONE_INT for all the account sites referencing this one
        com.cannontech.database.db.starscustomer.AccountSite.clearSiteInformation(
            getSiteInformation().getSiteID(), getDbConnection() );
        getSiteInformation().delete();
    }

    public void add() throws java.sql.SQLException {
        getSiteInformation().add();
    }

    public void update() throws java.sql.SQLException {
        getSiteInformation().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getSiteInformation().retrieve();

        getSubstation().setSubstationID( getSiteInformation().getSubstationID() );
        getSubstation().retrieve();
    }

    public com.cannontech.database.db.starscustomer.SiteInformation getSiteInformation() {
        if (siteInformation == null)
            siteInformation = new com.cannontech.database.db.starscustomer.SiteInformation();
        return siteInformation;
    }

    public void setSiteInformation(com.cannontech.database.db.starscustomer.SiteInformation newSiteInformation) {
        siteInformation = newSiteInformation;
    }

    public com.cannontech.database.db.starscustomer.Substation getSubstation() {
        if (substation == null)
            substation = new com.cannontech.database.db.starscustomer.Substation();
        return substation;
    }

    public void setSubstation(com.cannontech.database.db.starscustomer.Substation newSubstation) {
        substation = newSubstation;
    }
}
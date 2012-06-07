package com.cannontech.stars.database.db.customer;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AccountSite extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer accountSiteID = null;
    private Integer siteInformationID = new Integer( SiteInformation.NONE_INT );
    private String siteNumber = "";
    private Integer streetAddressID = new Integer( 0 );
    private String propertyNotes = "";
    private String customerStatus = "";
    private String custAtHome = "N";

    public static final String[] SETTER_COLUMNS = {
        "SiteInformationID", "SiteNumber", "StreetAddressID", "PropertyNotes", 
        "CustomerStatus", "CustAtHome"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "AccountSiteID" };

    public static final String TABLE_NAME = "AccountSite";

    public AccountSite() {
        super();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountSiteID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getAccountSiteID() == null)
    		setAccountSiteID( getNextAccountSiteID() );
    		
        Object[] addValues = {
            getAccountSiteID(), getSiteInformationID(), getSiteNumber(),
            getStreetAddressID(), getPropertyNotes(), getCustomerStatus(),
            getCustAtHome()
        };

        add( TABLE_NAME, addValues );
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getSiteInformationID(), getSiteNumber(), getStreetAddressID(), getPropertyNotes(),
            getCustomerStatus(), getCustAtHome()
        };

        Object[] constraintValues = { getAccountSiteID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountSiteID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setSiteInformationID( (Integer) results[0] );
            setSiteNumber( (String) results[1] );
            setStreetAddressID( (Integer) results[2] );
            setPropertyNotes( (String) results[3] );
            setCustomerStatus( (String) results[4] );
            setCustAtHome( (String) results[5] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    private Integer getNextAccountSiteID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }

    public Integer getAccountSiteID() {
        return accountSiteID;
    }

    public void setAccountSiteID(Integer newAccountSiteID) {
        accountSiteID = newAccountSiteID;
    }

    public Integer getSiteInformationID() {
        return siteInformationID;
    }

    public void setSiteInformationID(Integer newSiteInformationID) {
        siteInformationID = newSiteInformationID;
    }

    public String getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(String newSiteNumber) {
        siteNumber = newSiteNumber;
    }

    public Integer getStreetAddressID() {
        return streetAddressID;
    }

    public void setStreetAddressID(Integer newStreetAddressID) {
        streetAddressID = newStreetAddressID;
    }

    public String getPropertyNotes() {
        return propertyNotes;
    }

    public void setPropertyNotes(String newPropertyNotes) {
        propertyNotes = newPropertyNotes;
    }
    
    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String newCustomerStatus) {
        customerStatus = newCustomerStatus;
    }
    
    public String getCustAtHome() {
        return custAtHome;
    }

    public void setCustAtHome(String newCustAtHome) {
        custAtHome = newCustAtHome;
    }
}
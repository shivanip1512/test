package com.cannontech.database.db.stars.customer;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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

    public static final int NONE_INT = 0;

    private Integer accountSiteID = null;
    private Integer siteInformationID = new Integer( SiteInformation.NONE_INT );
    private String siteNumber = "";
    private Integer streetAddressID = new Integer( 0 );
    private String propertyNotes = "";

    public static final String[] SETTER_COLUMNS = {
        "SiteInformationID", "SiteNumber", "StreetAddressID", "PropertyNotes"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "AccountSiteID" };

    public static final String TABLE_NAME = "AccountSite";

    public static final String GET_NEXT_ACCOUNTSITE_ID_SQL =
        "SELECT MAX(AccountSiteID) FROM " + TABLE_NAME;

    public AccountSite() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountSiteID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getAccountSiteID() == null)
    		setAccountSiteID( getNextAccountSiteID() );
    		
        Object[] addValues = {
            getAccountSiteID(), getSiteInformationID(), getSiteNumber(),
            getStreetAddressID(), getPropertyNotes()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getSiteInformationID(), getSiteNumber(), getStreetAddressID(), getPropertyNotes()
        };

        Object[] constraintValues = { getAccountSiteID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getAccountSiteID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setSiteInformationID( (Integer) results[0] );
            setSiteNumber( (String) results[1] );
            setStreetAddressID( (Integer) results[2] );
            setPropertyNotes( (String) results[3] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextAccountSiteID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextAccountSiteID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_ACCOUNTSITE_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextAccountSiteID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
				if( rset != null ) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextAccountSiteID );
    }
    
    public static Integer getAccountIDBySiteNo(String siteNo, int energyCompanyID) {
    	java.sql.Connection conn = null;
    	String sql = "SELECT acct.AccountID FROM " +
    			"AccountSite site, CustomerAccount acct, ECToAccountMapping map " +
    			"WHERE UPPER(site.SiteNumber) LIKE UPPER(?) " +
    			"AND site.AccountSiteID = acct.AccountSiteID " +
    			"AND acct.AccountID = map.AccountID and map.EnergyCompanyID = ?";
    	
    	try {
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
    		java.sql.PreparedStatement stmt = conn.prepareStatement( sql );
    		stmt.setString( 1, siteNo );
    		stmt.setInt( 2, energyCompanyID );
    		
    		java.sql.ResultSet rset = stmt.executeQuery();
    		if (rset.next())
    			return new Integer( rset.getInt(1) );
    	}
    	catch (java.sql.SQLException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (java.sql.SQLException e) {}
    	}
    	
    	return null;
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
}
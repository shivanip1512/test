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

public class SiteInformation extends DBPersistent {

    private com.cannontech.database.db.stars.customer.SiteInformation siteInformation = null;
    private com.cannontech.database.db.stars.Substation substation = null;

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
	/**
	 * Returns the siteInformation.
	 * @return com.cannontech.database.db.stars.customer.SiteInformation
	 */
	public com.cannontech.database.db.stars.customer.SiteInformation getSiteInformation() {
		if (siteInformation == null)
			siteInformation = new com.cannontech.database.db.stars.customer.SiteInformation();
		return siteInformation;
	}

	/**
	 * Returns the substation.
	 * @return com.cannontech.database.db.stars.Substation
	 */
	public com.cannontech.database.db.stars.Substation getSubstation() {
		if (substation == null)
			substation = new com.cannontech.database.db.stars.Substation();
		return substation;
	}

	/**
	 * Sets the siteInformation.
	 * @param siteInformation The siteInformation to set
	 */
	public void setSiteInformation(
		com.cannontech.database.db.stars.customer.SiteInformation siteInformation) {
		this.siteInformation = siteInformation;
	}

	/**
	 * Sets the substation.
	 * @param substation The substation to set
	 */
	public void setSubstation(
		com.cannontech.database.db.stars.Substation substation) {
		this.substation = substation;
	}

}
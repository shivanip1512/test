package com.cannontech.database.data.stars;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Substation extends DBPersistent {

    private com.cannontech.database.db.stars.Substation substation = null;
    
    private com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase = null;

    public Substation() {
        super();
    }

    public void setSubstationID(Integer newID) {
        getSubstation().setSubstationID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getSubstation().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
    	// delete from mapping table
    	delete("ECToGenericMapping", "ItemID", getSubstation().getSubstationID());
    	
        getSubstation().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getEnergyCompanyBase() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyBase() must be called before this function");
    		
        getSubstation().add();
        
        if (getEnergyCompanyBase().getEnergyCompany() != null) {
        	// Add to mapping table
        	Object[] addValues = {
        		getEnergyCompanyBase().getEnergyCompany().getEnergyCompanyID(),
        		getSubstation().getSubstationID(),
        		getSubstation().TABLE_NAME
        	};
        	add("ECToGenericMapping", addValues);
        }
    }

    public void update() throws java.sql.SQLException {
        getSubstation().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getSubstation().retrieve();
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
	 * Sets the substation.
	 * @param substation The substation to set
	 */
	public void setSubstation(
		com.cannontech.database.db.stars.Substation substation) {
		this.substation = substation;
	}

	/**
	 * Returns the energyCompanyBase.
	 * @return com.cannontech.database.data.company.EnergyCompanyBase
	 */
	public com.cannontech.database.data.company.EnergyCompanyBase getEnergyCompanyBase() {
		return energyCompanyBase;
	}

	/**
	 * Sets the energyCompanyBase.
	 * @param energyCompanyBase The energyCompanyBase to set
	 */
	public void setEnergyCompanyBase(
		com.cannontech.database.data.company.EnergyCompanyBase energyCompanyBase) {
		this.energyCompanyBase = energyCompanyBase;
	}

}
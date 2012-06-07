package com.cannontech.stars.database.data;

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

    private com.cannontech.stars.database.db.Substation substation = null;
    
    private Integer energyCompanyID = null;

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
    	String[] constraintColumns = {
    		"ItemID", "MappingCategory"
    	};
    	Object[] constraintValues = {
    		getSubstation().getSubstationID(),
    		com.cannontech.stars.database.db.Substation.TABLE_NAME
    	};
    	delete("ECToGenericMapping", constraintColumns, constraintValues);
    	
        getSubstation().delete();
    }

    public void add() throws java.sql.SQLException {
    	if (getEnergyCompanyID() == null)
    		throw new java.sql.SQLException("Add: setEnergyCompanyID() must be called before this function");
    		
        getSubstation().add();
        
    	// Add to mapping table
    	Object[] addValues = {
    		getEnergyCompanyID(),
    		getSubstation().getSubstationID(),
    		com.cannontech.stars.database.db.Substation.TABLE_NAME
    	};
    	add("ECToGenericMapping", addValues);
    }

    public void update() throws java.sql.SQLException {
        getSubstation().update();
    }

    public void retrieve() throws java.sql.SQLException {
        getSubstation().retrieve();
    }
    
	/**
	 * Returns the substation.
	 * @return com.cannontech.stars.database.db.Substation
	 */
	public com.cannontech.stars.database.db.Substation getSubstation() {
		if (substation == null)
			substation = new com.cannontech.stars.database.db.Substation();
		return substation;
	}

	/**
	 * Sets the substation.
	 * @param substation The substation to set
	 */
	public void setSubstation(
		com.cannontech.stars.database.db.Substation substation) {
		this.substation = substation;
	}

	/**
	 * Returns the energyCompanyID.
	 * @return Integer
	 */
	public Integer getEnergyCompanyID() {
		return energyCompanyID;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(Integer energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

}
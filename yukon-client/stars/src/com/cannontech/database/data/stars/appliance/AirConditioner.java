package com.cannontech.database.data.stars.appliance;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AirConditioner extends ApplianceBase {

    private com.cannontech.database.db.stars.appliance.AirConditioner airConditioner = null;
    private com.cannontech.database.db.stars.CustomerListEntry tonage = null;
    private com.cannontech.database.db.stars.CustomerListEntry type = null;

    public AirConditioner() {
        super();
    }

    public void setApplianceID(Integer newID) {
        super.setApplianceID(newID);
        getAirConditioner().setApplianceID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getAirConditioner().setDbConnection(conn);
        getTonage().setDbConnection(conn);
        getType().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getAirConditioner().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
        super.add();
        getAirConditioner().add();
    }

    public void update() throws java.sql.SQLException {
        super.update();
        getAirConditioner().update();
    }

    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getAirConditioner().retrieve();
        
        getTonage().setEntryID( getAirConditioner().getTonageID() );
        getTonage().retrieve();
        
        getType().setEntryID( getAirConditioner().getTypeID() );
        getType().retrieve();
    }

    public com.cannontech.database.db.stars.appliance.AirConditioner getAirConditioner() {
        if (airConditioner == null)
            airConditioner = new com.cannontech.database.db.stars.appliance.AirConditioner();
        return airConditioner;
    }

    public void setAirConditioner(com.cannontech.database.db.stars.appliance.AirConditioner newAirConditioner) {
        airConditioner = newAirConditioner;
    }
	/**
	 * Returns the tonage.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getTonage() {
		if (tonage == null)
			tonage = new com.cannontech.database.db.stars.CustomerListEntry();
		return tonage;
	}

	/**
	 * Returns the type.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getType() {
		if (type == null)
			type = new com.cannontech.database.db.stars.CustomerListEntry();
		return type;
	}

	/**
	 * Sets the tonage.
	 * @param tonage The tonage to set
	 */
	public void setTonage(
		com.cannontech.database.db.stars.CustomerListEntry tonage) {
		this.tonage = tonage;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(
		com.cannontech.database.db.stars.CustomerListEntry type) {
		this.type = type;
	}

}
package com.cannontech.database.db.stars.appliance;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AirConditioner extends DBPersistent {

    private Integer applianceID = null;
    private Integer tonageID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );
    private Integer typeID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "TonageID", "TypeID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };

    public static final String TABLE_NAME = "AirConditioner";

    public AirConditioner() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getApplianceID(), getTonageID(), getTypeID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getTonageID(), getTypeID()
        };

        Object[] constraintValues = { getApplianceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setTonageID( (Integer) results[0] );
            setTypeID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public Integer getApplianceID() {
        return applianceID;
    }

    public void setApplianceID(Integer newApplianceID) {
        applianceID = newApplianceID;
    }
	/**
	 * Returns the tonageID.
	 * @return Integer
	 */
	public Integer getTonageID() {
		return tonageID;
	}

	/**
	 * Returns the typeID.
	 * @return Integer
	 */
	public Integer getTypeID() {
		return typeID;
	}

	/**
	 * Sets the tonageID.
	 * @param tonageID The tonageID to set
	 */
	public void setTonageID(Integer tonageID) {
		this.tonageID = tonageID;
	}

	/**
	 * Sets the typeID.
	 * @param typeID The typeID to set
	 */
	public void setTypeID(Integer typeID) {
		this.typeID = typeID;
	}

}
package com.cannontech.database.db.stars.hardware;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareBase extends DBPersistent {

    private Integer inventoryID = null;
    private String manufacturerSerialNumber = "";
    private Integer lmHardwareTypeID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "ManufacturerSerialNumber", "LMHardwareTypeID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };

    public static final String TABLE_NAME = "LMHardwareBase";

    public LMHardwareBase() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getInventoryID(), getManufacturerSerialNumber(), getLMHardwareTypeID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getManufacturerSerialNumber(), getLMHardwareTypeID()
        };

        Object[] constraintValues = { getInventoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setManufacturerSerialNumber( (String) results[0] );
            setLMHardwareTypeID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public Integer getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(Integer newInventoryID) {
        inventoryID = newInventoryID;
    }

    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }

    public void setManufacturerSerialNumber(String newManufacturerSerialNumber) {
        manufacturerSerialNumber = newManufacturerSerialNumber;
    }
	/**
	 * Returns the lmHardwareTypeID.
	 * @return Integer
	 */
	public Integer getLMHardwareTypeID() {
		return lmHardwareTypeID;
	}

	/**
	 * Sets the lmHardwareTypeID.
	 * @param lmHardwareTypeID The lmHardwareTypeID to set
	 */
	public void setLMHardwareTypeID(Integer lmHardwareTypeID) {
		this.lmHardwareTypeID = lmHardwareTypeID;
	}

}
package com.cannontech.database.db.stars.appliance;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ApplianceIrrigation extends DBPersistent {

    private Integer applianceID = null;
    private Integer irrigationTypeID = new Integer( CtiUtilities.NONE_ID );
    private Integer horsePowerID = new Integer( CtiUtilities.NONE_ID );
    private Integer energySourceID = new Integer( CtiUtilities.NONE_ID );
    private Integer soilTypeID = new Integer( CtiUtilities.NONE_ID );
    private Integer meterLocationID = new Integer( CtiUtilities.NONE_ID );
    private Integer meterVoltageID = new Integer( CtiUtilities.NONE_ID );

    public static final String[] SETTER_COLUMNS = {
        "IrrigationTypeID", "HorsePowerID", "EnergySourceID",
        "SoilTypeID", "MeterLocationID", "MeterVoltageID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };

    public static final String TABLE_NAME = "ApplianceIrrigation";

    public ApplianceIrrigation() {
        super();
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getApplianceID(), getIrrigationTypeID(), getHorsePowerID(), getEnergySourceID(),
            getSoilTypeID(), getMeterLocationID(), getMeterVoltageID()
        };

        add( TABLE_NAME, addValues );
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setIrrigationTypeID( (Integer) results[0] );
            setHorsePowerID( (Integer) results[1] );
            setEnergySourceID( (Integer) results[2] );
            setSoilTypeID( (Integer) results[3] );
            setMeterLocationID( (Integer) results[4] );
            setMeterVoltageID( (Integer) results[5] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getIrrigationTypeID(), getHorsePowerID(), getEnergySourceID(),
            getSoilTypeID(), getMeterLocationID(), getMeterVoltageID()
        };
        Object[] constraintValues = { getApplianceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

	/**
	 * Returns the applianceID.
	 * @return Integer
	 */
	public Integer getApplianceID() {
		return applianceID;
	}

	/**
	 * Returns the energySourceID.
	 * @return Integer
	 */
	public Integer getEnergySourceID() {
		return energySourceID;
	}

	/**
	 * Returns the horsePowerID.
	 * @return Integer
	 */
	public Integer getHorsePowerID() {
		return horsePowerID;
	}

	/**
	 * Returns the irrigationTypeID.
	 * @return Integer
	 */
	public Integer getIrrigationTypeID() {
		return irrigationTypeID;
	}

	/**
	 * Returns the meterLocationID.
	 * @return Integer
	 */
	public Integer getMeterLocationID() {
		return meterLocationID;
	}

	/**
	 * Returns the meterVoltageID.
	 * @return Integer
	 */
	public Integer getMeterVoltageID() {
		return meterVoltageID;
	}

	/**
	 * Returns the soilTypeID.
	 * @return Integer
	 */
	public Integer getSoilTypeID() {
		return soilTypeID;
	}

	/**
	 * Sets the applianceID.
	 * @param applianceID The applianceID to set
	 */
	public void setApplianceID(Integer applianceID) {
		this.applianceID = applianceID;
	}

	/**
	 * Sets the energySourceID.
	 * @param energySourceID The energySourceID to set
	 */
	public void setEnergySourceID(Integer energySourceID) {
		this.energySourceID = energySourceID;
	}

	/**
	 * Sets the horsePowerID.
	 * @param horsePowerID The horsePowerID to set
	 */
	public void setHorsePowerID(Integer horsePowerID) {
		this.horsePowerID = horsePowerID;
	}

	/**
	 * Sets the irrigationTypeID.
	 * @param irrigationTypeID The irrigationTypeID to set
	 */
	public void setIrrigationTypeID(Integer irrigationTypeID) {
		this.irrigationTypeID = irrigationTypeID;
	}

	/**
	 * Sets the meterLocationID.
	 * @param meterLocationID The meterLocationID to set
	 */
	public void setMeterLocationID(Integer meterLocationID) {
		this.meterLocationID = meterLocationID;
	}

	/**
	 * Sets the meterVoltageID.
	 * @param meterVoltageID The meterVoltageID to set
	 */
	public void setMeterVoltageID(Integer meterVoltageID) {
		this.meterVoltageID = meterVoltageID;
	}

	/**
	 * Sets the soilTypeID.
	 * @param soilTypeID The soilTypeID to set
	 */
	public void setSoilTypeID(Integer soilTypeID) {
		this.soilTypeID = soilTypeID;
	}

}
package com.cannontech.database.data.lite.stars;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsAppIrrigation extends LiteStarsAppliance {

	private int irrigationTypeID = CtiUtilities.NONE_ID;
	private int horsePowerID = CtiUtilities.NONE_ID;
	private int energySourceID = CtiUtilities.NONE_ID;
	private int soilTypeID = CtiUtilities.NONE_ID;
	private int meterLocationID = CtiUtilities.NONE_ID;
	private int meterVoltageID = CtiUtilities.NONE_ID;
	
	public LiteStarsAppIrrigation() {
		super();
	}
	
	public LiteStarsAppIrrigation(int appID) {
		super( appID );
	}

	/**
	 * Returns the energySourceID.
	 * @return int
	 */
	public int getEnergySourceID() {
		return energySourceID;
	}

	/**
	 * Returns the horsePowerID.
	 * @return int
	 */
	public int getHorsePowerID() {
		return horsePowerID;
	}

	/**
	 * Returns the irrigationTypeID.
	 * @return int
	 */
	public int getIrrigationTypeID() {
		return irrigationTypeID;
	}

	/**
	 * Returns the meterLocationID.
	 * @return int
	 */
	public int getMeterLocationID() {
		return meterLocationID;
	}

	/**
	 * Returns the meterVoltageID.
	 * @return int
	 */
	public int getMeterVoltageID() {
		return meterVoltageID;
	}

	/**
	 * Returns the soilTypeID.
	 * @return int
	 */
	public int getSoilTypeID() {
		return soilTypeID;
	}

	/**
	 * Sets the energySourceID.
	 * @param energySourceID The energySourceID to set
	 */
	public void setEnergySourceID(int energySourceID) {
		this.energySourceID = energySourceID;
	}

	/**
	 * Sets the horsePowerID.
	 * @param horsePowerID The horsePowerID to set
	 */
	public void setHorsePowerID(int horsePowerID) {
		this.horsePowerID = horsePowerID;
	}

	/**
	 * Sets the irrigationTypeID.
	 * @param irrigationTypeID The irrigationTypeID to set
	 */
	public void setIrrigationTypeID(int irrigationTypeID) {
		this.irrigationTypeID = irrigationTypeID;
	}

	/**
	 * Sets the meterLocationID.
	 * @param meterLocationID The meterLocationID to set
	 */
	public void setMeterLocationID(int meterLocationID) {
		this.meterLocationID = meterLocationID;
	}

	/**
	 * Sets the meterVoltageID.
	 * @param meterVoltageID The meterVoltageID to set
	 */
	public void setMeterVoltageID(int meterVoltageID) {
		this.meterVoltageID = meterVoltageID;
	}

	/**
	 * Sets the soilTypeID.
	 * @param soilTypeID The soilTypeID to set
	 */
	public void setSoilTypeID(int soilTypeID) {
		this.soilTypeID = soilTypeID;
	}

}

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
public class LiteStarsAppGrainDryer extends LiteStarsAppliance {

	private int dryerTypeID = CtiUtilities.NONE_ID;
	private int binSizeID = CtiUtilities.NONE_ID;
	private int blowerEnergySourceID = CtiUtilities.NONE_ID;
	private int blowerHorsePowerID = CtiUtilities.NONE_ID;
	private int blowerHeatSourceID = CtiUtilities.NONE_ID;
	
	public LiteStarsAppGrainDryer() {
		super();
	}
	
	public LiteStarsAppGrainDryer(int appID) {
		super( appID );
	}

	/**
	 * Returns the binSizeID.
	 * @return int
	 */
	public int getBinSizeID() {
		return binSizeID;
	}

	/**
	 * Returns the blowerEnergySourceID.
	 * @return int
	 */
	public int getBlowerEnergySourceID() {
		return blowerEnergySourceID;
	}

	/**
	 * Returns the blowerHeatSourceID.
	 * @return int
	 */
	public int getBlowerHeatSourceID() {
		return blowerHeatSourceID;
	}

	/**
	 * Returns the blowerHorsePowerID.
	 * @return int
	 */
	public int getBlowerHorsePowerID() {
		return blowerHorsePowerID;
	}

	/**
	 * Returns the dryerTypeID.
	 * @return int
	 */
	public int getDryerTypeID() {
		return dryerTypeID;
	}

	/**
	 * Sets the binSizeID.
	 * @param binSizeID The binSizeID to set
	 */
	public void setBinSizeID(int binSizeID) {
		this.binSizeID = binSizeID;
	}

	/**
	 * Sets the blowerEnergySourceID.
	 * @param blowerEnergySourceID The blowerEnergySourceID to set
	 */
	public void setBlowerEnergySourceID(int blowerEnergySourceID) {
		this.blowerEnergySourceID = blowerEnergySourceID;
	}

	/**
	 * Sets the blowerHeatSourceID.
	 * @param blowerHeatSourceID The blowerHeatSourceID to set
	 */
	public void setBlowerHeatSourceID(int blowerHeatSourceID) {
		this.blowerHeatSourceID = blowerHeatSourceID;
	}

	/**
	 * Sets the blowerHorsePowerID.
	 * @param blowerHorsePowerID The blowerHorsePowerID to set
	 */
	public void setBlowerHorsePowerID(int blowerHorsePowerID) {
		this.blowerHorsePowerID = blowerHorsePowerID;
	}

	/**
	 * Sets the dryerTypeID.
	 * @param dryerTypeID The dryerTypeID to set
	 */
	public void setDryerTypeID(int dryerTypeID) {
		this.dryerTypeID = dryerTypeID;
	}

}

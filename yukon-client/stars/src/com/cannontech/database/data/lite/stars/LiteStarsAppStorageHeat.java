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
public class LiteStarsAppStorageHeat extends LiteStarsAppliance {

	private int storageTypeID = CtiUtilities.NONE_ID;
	private int peakKWCapacity = 0;
	private int hoursToRecharge = 0;
	
	public LiteStarsAppStorageHeat() {
		super();
	}
	
	public LiteStarsAppStorageHeat(int appID) {
		super( appID );
	}

	/**
	 * Returns the hoursToRecharge.
	 * @return int
	 */
	public int getHoursToRecharge() {
		return hoursToRecharge;
	}

	/**
	 * Returns the peakKWCapacity.
	 * @return int
	 */
	public int getPeakKWCapacity() {
		return peakKWCapacity;
	}

	/**
	 * Returns the storageTypeID.
	 * @return int
	 */
	public int getStorageTypeID() {
		return storageTypeID;
	}

	/**
	 * Sets the hoursToRecharge.
	 * @param hoursToRecharge The hoursToRecharge to set
	 */
	public void setHoursToRecharge(int hoursToRecharge) {
		this.hoursToRecharge = hoursToRecharge;
	}

	/**
	 * Sets the peakKWCapacity.
	 * @param peakKWCapacity The peakKWCapacity to set
	 */
	public void setPeakKWCapacity(int peakKWCapacity) {
		this.peakKWCapacity = peakKWCapacity;
	}

	/**
	 * Sets the storageTypeID.
	 * @param storageTypeID The storageTypeID to set
	 */
	public void setStorageTypeID(int storageTypeID) {
		this.storageTypeID = storageTypeID;
	}

}

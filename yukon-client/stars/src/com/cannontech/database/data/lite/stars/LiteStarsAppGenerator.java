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
public class LiteStarsAppGenerator extends LiteStarsAppliance {

	private int transferSwitchTypeID = CtiUtilities.NONE_ID;
	private int transferSwitchMfgID = CtiUtilities.NONE_ID;
	private int peakKWCapacity = 0;
	private int fuelCapGallons = 0;
	private int startDelaySeconds = 0;
	
	public LiteStarsAppGenerator() {
		super();
	}
	
	public LiteStarsAppGenerator(int appID) {
		super( appID );
	}

	/**
	 * Returns the fuelCapGallons.
	 * @return int
	 */
	public int getFuelCapGallons() {
		return fuelCapGallons;
	}

	/**
	 * Returns the peakKWCapacity.
	 * @return int
	 */
	public int getPeakKWCapacity() {
		return peakKWCapacity;
	}

	/**
	 * Returns the startDelaySeconds.
	 * @return int
	 */
	public int getStartDelaySeconds() {
		return startDelaySeconds;
	}

	/**
	 * Returns the transferSwitchMfgID.
	 * @return int
	 */
	public int getTransferSwitchMfgID() {
		return transferSwitchMfgID;
	}

	/**
	 * Returns the transferSwitchTypeID.
	 * @return int
	 */
	public int getTransferSwitchTypeID() {
		return transferSwitchTypeID;
	}

	/**
	 * Sets the fuelCapGallons.
	 * @param fuelCapGallons The fuelCapGallons to set
	 */
	public void setFuelCapGallons(int fuelCapGallons) {
		this.fuelCapGallons = fuelCapGallons;
	}

	/**
	 * Sets the peakKWCapacity.
	 * @param peakKWCapacity The peakKWCapacity to set
	 */
	public void setPeakKWCapacity(int peakKWCapacity) {
		this.peakKWCapacity = peakKWCapacity;
	}

	/**
	 * Sets the startDelaySeconds.
	 * @param startDelaySeconds The startDelaySeconds to set
	 */
	public void setStartDelaySeconds(int startDelaySeconds) {
		this.startDelaySeconds = startDelaySeconds;
	}

	/**
	 * Sets the transferSwitchMfgID.
	 * @param transferSwitchMfgID The transferSwitchMfgID to set
	 */
	public void setTransferSwitchMfgID(int transferSwitchMfgID) {
		this.transferSwitchMfgID = transferSwitchMfgID;
	}

	/**
	 * Sets the transferSwitchTypeID.
	 * @param transferSwitchTypeID The transferSwitchTypeID to set
	 */
	public void setTransferSwitchTypeID(int transferSwitchTypeID) {
		this.transferSwitchTypeID = transferSwitchTypeID;
	}

}

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
public class LiteStarsAppDualFuel extends LiteStarsAppliance {

	private int switchOverTypeID = CtiUtilities.NONE_ID;
	private int secondaryKWCapacity = 0;
	private int secondaryEnergySourceID = CtiUtilities.NONE_ID;
	
	public LiteStarsAppDualFuel() {
		super();
	}
	
	public LiteStarsAppDualFuel(int appID) {
		super( appID );
	}

	/**
	 * Returns the secondaryEnergySourceID.
	 * @return int
	 */
	public int getSecondaryEnergySourceID() {
		return secondaryEnergySourceID;
	}

	/**
	 * Returns the secondaryKWCapacity.
	 * @return int
	 */
	public int getSecondaryKWCapacity() {
		return secondaryKWCapacity;
	}

	/**
	 * Returns the switchOverTypeID.
	 * @return int
	 */
	public int getSwitchOverTypeID() {
		return switchOverTypeID;
	}

	/**
	 * Sets the secondaryEnergySourceID.
	 * @param secondaryEnergySourceID The secondaryEnergySourceID to set
	 */
	public void setSecondaryEnergySourceID(int secondaryEnergySourceID) {
		this.secondaryEnergySourceID = secondaryEnergySourceID;
	}

	/**
	 * Sets the secondaryKWCapacity.
	 * @param secondaryKWCapacity The secondaryKWCapacity to set
	 */
	public void setSecondaryKWCapacity(int secondaryKWCapacity) {
		this.secondaryKWCapacity = secondaryKWCapacity;
	}

	/**
	 * Sets the switchOverTypeID.
	 * @param switchOverTypeID The switchOverTypeID to set
	 */
	public void setSwitchOverTypeID(int switchOverTypeID) {
		this.switchOverTypeID = switchOverTypeID;
	}

}

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
public class LiteStarsAppWaterHeater extends LiteStarsAppliance {

	private int numberOfGallonsID = CtiUtilities.NONE_ID;
	private int energySourceID = CtiUtilities.NONE_ID;
	private int numberOfElements = 0;
	
	public LiteStarsAppWaterHeater() {
		super();
	}
	
	public LiteStarsAppWaterHeater(int appID) {
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
	 * Returns the numberOfElements.
	 * @return int
	 */
	public int getNumberOfElements() {
		return numberOfElements;
	}

	/**
	 * Returns the numberOfGallonsID.
	 * @return int
	 */
	public int getNumberOfGallonsID() {
		return numberOfGallonsID;
	}

	/**
	 * Sets the energySourceID.
	 * @param energySourceID The energySourceID to set
	 */
	public void setEnergySourceID(int energySourceID) {
		this.energySourceID = energySourceID;
	}

	/**
	 * Sets the numberOfElements.
	 * @param numberOfElements The numberOfElements to set
	 */
	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	/**
	 * Sets the numberOfGallonsID.
	 * @param numberOfGallonsID The numberOfGallonsID to set
	 */
	public void setNumberOfGallonsID(int numberOfGallonsID) {
		this.numberOfGallonsID = numberOfGallonsID;
	}

}

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
public class LiteStarsAppHeatPump extends LiteStarsAppliance {

	private int pumpTypeID = CtiUtilities.NONE_ID;
	private int pumpSizeID = CtiUtilities.NONE_ID;
	private int standbySourceID = CtiUtilities.NONE_ID;
	private int secondsDelayToRestart = 0;
	
	public LiteStarsAppHeatPump() {
		super();
	}
	
	public LiteStarsAppHeatPump(int appID) {
		super( appID );
	}

	/**
	 * Returns the pumpTypeID.
	 * @return int
	 */
	public int getPumpTypeID() {
		return pumpTypeID;
	}

	/**
	 * Returns the secondsDelayToRestart.
	 * @return int
	 */
	public int getSecondsDelayToRestart() {
		return secondsDelayToRestart;
	}

	/**
	 * Returns the standbySourceID.
	 * @return int
	 */
	public int getStandbySourceID() {
		return standbySourceID;
	}

	/**
	 * Sets the pumpTypeID.
	 * @param pumpTypeID The pumpTypeID to set
	 */
	public void setPumpTypeID(int pumpTypeID) {
		this.pumpTypeID = pumpTypeID;
	}

	/**
	 * Sets the secondsDelayToRestart.
	 * @param secondsDelayToRestart The secondsDelayToRestart to set
	 */
	public void setSecondsDelayToRestart(int secondsDelayToRestart) {
		this.secondsDelayToRestart = secondsDelayToRestart;
	}

	/**
	 * Sets the standbySourceID.
	 * @param standbySourceID The standbySourceID to set
	 */
	public void setStandbySourceID(int standbySourceID) {
		this.standbySourceID = standbySourceID;
	}

	/**
	 * @return
	 */
	public int getPumpSizeID() {
		return pumpSizeID;
	}

	/**
	 * @param i
	 */
	public void setPumpSizeID(int i) {
		pumpSizeID = i;
	}

}

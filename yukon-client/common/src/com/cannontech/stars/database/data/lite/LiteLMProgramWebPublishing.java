package com.cannontech.stars.database.data.lite;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteLMProgramWebPublishing extends LiteBase {

	private int applianceCategoryID = CtiUtilities.NONE_ZERO_ID;
	private int deviceID = CtiUtilities.NONE_ZERO_ID;
	private int webSettingsID = CtiUtilities.NONE_ZERO_ID;
	private int chanceOfControlID = CtiUtilities.NONE_ZERO_ID;
	private int programOrder = 0;
	private int[] groupIDs = null;
	
	public LiteLMProgramWebPublishing() {
		super();
		setLiteType( LiteTypes.STARS_LMPROGRAM );
	}
	
	public LiteLMProgramWebPublishing(int programID) {
		super();
		setProgramID( programID );
		setLiteType( LiteTypes.STARS_LMPROGRAM );
	}
	
	public int getProgramID() {
		return getLiteID();
	}
	
	public void setProgramID(int programID) {
		setLiteID( programID );
	}

	/**
	 * Returns the webSettingsID.
	 * @return int
	 */
	public int getWebSettingsID() {
		return webSettingsID;
	}

	/**
	 * Sets the webSettingsID.
	 * @param webSettingsID The webSettingsID to set
	 */
	public void setWebSettingsID(int webSettingsID) {
		this.webSettingsID = webSettingsID;
	}

	/**
	 * Returns the groupIDs.
	 * @return int[]
	 */
	public int[] getGroupIDs() {
		return groupIDs;
	}

	/**
	 * Sets the groupIDs.
	 * @param groupIDs The groupIDs to set
	 */
	public void setGroupIDs(int[] groupIDs) {
		this.groupIDs = groupIDs;
	}

	/**
	 * Returns the default group id
	 * @return
	 */
	public int getDefaultGroupId() {
        int defaultGroupId = 0;
        if (groupIDs.length > 0) {
            defaultGroupId = groupIDs[0];
        }
        return defaultGroupId;
    }
	
	/**
     * Returns true, if this is a Virtual program, i.e., No Yukon load control; false otherwise.
     * @return
     */
	public boolean isVirtualProgram() {
        boolean virtualProgram = (getDeviceID() > 0) ? false : true;
        return virtualProgram;
    }
	
	/**
	 * Returns the chanceOfControlID.
	 * @return int
	 */
	public int getChanceOfControlID() {
		return chanceOfControlID;
	}

	/**
	 * Sets the chanceOfControlID.
	 * @param chanceOfControlID The chanceOfControlID to set
	 */
	public void setChanceOfControlID(int chanceOfControlID) {
		this.chanceOfControlID = chanceOfControlID;
	}

	/**
	 * @return
	 */
	public int getProgramOrder() {
		return programOrder;
	}

	/**
	 * @param i
	 */
	public void setProgramOrder(int i) {
		programOrder = i;
	}

	/**
	 * @return
	 */
	public int getApplianceCategoryID() {
		return applianceCategoryID;
	}

	/**
	 * @return
	 */
	public int getDeviceID() {
		return deviceID;
	}

	/**
	 * @param i
	 */
	public void setApplianceCategoryID(int i) {
		applianceCategoryID = i;
	}

	/**
	 * @param i
	 */
	public void setDeviceID(int i) {
		deviceID = i;
	}

}

package com.cannontech.database.data.lite.stars;

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
public class LiteLMProgram extends LiteBase {

	private String programName = null;
	private int webSettingsID = CtiUtilities.NONE_ID;
	private int chanceOfControlID = CtiUtilities.NONE_ID;
	private int programOrder = 0;
	private String programCategory = null;
	private int[] groupIDs = null;
	
	public LiteLMProgram() {
		super();
	}
	
	public LiteLMProgram(int progID) {
		super();
		setProgramID( progID );
		setLiteType( LiteTypes.STARS_LMPROGRAM );
	}
	
	public int getProgramID() {
		return getLiteID();
	}
	
	public void setProgramID(int progID) {
		setLiteID( progID );
	}
	
	/**
	 * Returns the programName.
	 * @return String
	 */
	public String getProgramName() {
		return programName;
	}

	/**
	 * Sets the programName.
	 * @param programName The programName to set
	 */
	public void setProgramName(String programName) {
		this.programName = programName;
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
	 * Returns the programCategory.
	 * @return String
	 */
	public String getProgramCategory() {
		return programCategory;
	}

	/**
	 * Sets the programCategory.
	 * @param programCategory The programCategory to set
	 */
	public void setProgramCategory(String programCategory) {
		this.programCategory = programCategory;
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

}

package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.database.data.lite.LiteBase;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsLMControlHistory extends LiteBase {
	
	private ArrayList lmControlHistory = null;
	
	// Last control history entry of the load group, used for getting control summary.
	// Notice: the ID of the lite object is not the ID of the corresponding control history entry,
	// rather it is the ID of the last searched entry in the database when getting control summary.
	private LiteLMControlHistory lastControlHistory = null;
	
	// ID of the last searched entry in the database when getting control history
	private int lastSearchedCtrlHistID = 0;
	
	// Start and stop time stamp of the last database search to get control history
	private long lastSearchedStartTime = 0;
	private long lastSearchedStopTime = 0;
	
	public LiteStarsLMControlHistory() {
		super();
	}
	
	public LiteStarsLMControlHistory(int groupID) {
		super();
		setGroupID( groupID );
	}

	public int getGroupID() {
		return getLiteID();
	}
	
	public void setGroupID(int groupID) {
		setLiteID( groupID );
	}
	
	public void clearLMControlHistory() {
		lmControlHistory = null;
		lastSearchedCtrlHistID = 0;
		lastSearchedStartTime = 0;
		lastSearchedStopTime = 0;
	}

	/**
	 * Returns the lmControlHistory.
	 * @return java.util.ArrayList
	 */
	public ArrayList getLmControlHistory() {
		return lmControlHistory;
	}

	/**
	 * Sets the lmControlHistory.
	 * @param lmControlHistory The lmControlHistory to set
	 */
	public void setLmControlHistory(ArrayList lmControlHistory) {
		this.lmControlHistory = lmControlHistory;
	}

	/**
	 * @return
	 */
	public LiteLMControlHistory getLastControlHistory() {
		return lastControlHistory;
	}

	/**
	 * @param history
	 */
	public void setLastControlHistory(LiteLMControlHistory history) {
		lastControlHistory = history;
	}

	/**
	 * @return
	 */
	public int getLastSearchedCtrlHistID() {
		return lastSearchedCtrlHistID;
	}

	/**
	 * @param i
	 */
	public void setLastSearchedCtrlHistID(int i) {
		lastSearchedCtrlHistID = i;
	}

	/**
	 * @return
	 */
	public long getLastSearchedStartTime() {
		return lastSearchedStartTime;
	}

	/**
	 * @return
	 */
	public long getLastSearchedStopTime() {
		return lastSearchedStopTime;
	}

	/**
	 * @param l
	 */
	public void setLastSearchedStartTime(long l) {
		lastSearchedStartTime = l;
	}

	/**
	 * @param l
	 */
	public void setLastSearchedStopTime(long l) {
		lastSearchedStopTime = l;
	}

}

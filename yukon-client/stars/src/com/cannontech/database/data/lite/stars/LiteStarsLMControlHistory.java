package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

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
	
	private long timeBase = 0;	// Time stamp of last index update
	private int currentYearStartIndex = 0;
	private int currentMonthStartIndex = 0;
	private int currentWeekStartIndex = 0;
	private int currentDayStartIndex = 0;
	
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
	
	public void updateStartIndices(TimeZone tz) {
		if (lmControlHistory == null) return;
		
		java.util.Date endOfDay = com.cannontech.util.ServletUtil.getTomorrow( tz );
		if (Math.abs(timeBase - endOfDay.getTime()) < 1000) return;	// The time stamp is on the same day
		timeBase = endOfDay.getTime();
		Calendar limitCal = Calendar.getInstance();
		
		// Set the time limit to the beginning of today
		limitCal.setTime( endOfDay );
		limitCal.add( Calendar.DAY_OF_YEAR, -1 );
			
		int curIndex = lmControlHistory.size() - 1;
		while (curIndex >= 0) {
			LiteLMControlHistory ctrlHist = (LiteLMControlHistory) lmControlHistory.get( curIndex );
			if (ctrlHist.getStartDateTime() < limitCal.getTime().getTime()) {
				currentDayStartIndex = curIndex + 1;
				break;
			}
			curIndex--;
		}
		if (curIndex < 0) currentDayStartIndex = 0;
		
		// Set the time limit to a week ago
		limitCal.setTime( endOfDay );
		limitCal.add( Calendar.WEEK_OF_YEAR, -1 );
		
		while (curIndex >= 0) {
			LiteLMControlHistory ctrlHist = (LiteLMControlHistory) lmControlHistory.get( curIndex );
			if (ctrlHist.getStartDateTime() < limitCal.getTime().getTime()) {
				currentWeekStartIndex = curIndex + 1;
				break;
			}
			curIndex--;
		}
		if (curIndex < 0) currentWeekStartIndex = 0;
		
		// Set the time limit to a month ago
		limitCal.setTime( endOfDay );
		limitCal.add( Calendar.MONTH, -1 );
		
		while (curIndex >= 0) {
			LiteLMControlHistory ctrlHist = (LiteLMControlHistory) lmControlHistory.get( curIndex );
			if (ctrlHist.getStartDateTime() < limitCal.getTime().getTime()) {
				currentMonthStartIndex = curIndex + 1;
				break;
			}
			curIndex--;
		}
		if (curIndex < 0) currentMonthStartIndex = 0;
		
		// Set the time limit to a year ago
		limitCal.setTime( endOfDay );
		limitCal.add( Calendar.YEAR, -1 );
		
		while (curIndex >= 0) {
			LiteLMControlHistory ctrlHist = (LiteLMControlHistory) lmControlHistory.get( curIndex );
			if (ctrlHist.getStartDateTime() < limitCal.getTime().getTime()) {
				currentYearStartIndex = curIndex + 1;
				break;
			}
			curIndex--;
		}
		if (curIndex < 0) currentYearStartIndex = 0;
	}
	
	/**
	 * Returns the currentDayStartIndex.
	 * @return int
	 */
	public int getCurrentDayStartIndex() {
		return currentDayStartIndex;
	}

	/**
	 * Returns the currentMonthStartIndex.
	 * @return int
	 */
	public int getCurrentMonthStartIndex() {
		return currentMonthStartIndex;
	}

	/**
	 * Returns the currentWeekStartIndex.
	 * @return int
	 */
	public int getCurrentWeekStartIndex() {
		return currentWeekStartIndex;
	}

	/**
	 * Returns the currentYearStartIndex.
	 * @return int
	 */
	public int getCurrentYearStartIndex() {
		return currentYearStartIndex;
	}

	/**
	 * Returns the lmControlHistory.
	 * @return java.util.ArrayList
	 */
	public ArrayList getLmControlHistory() {
		return lmControlHistory;
	}

	/**
	 * Returns the timeBase.
	 * @return long
	 */
	public long getTimeBase() {
		return timeBase;
	}

	/**
	 * Sets the lmControlHistory.
	 * @param lmControlHistory The lmControlHistory to set
	 */
	public void setLmControlHistory(ArrayList lmControlHistory) {
		this.lmControlHistory = lmControlHistory;
	}

	/**
	 * Sets the timeBase.
	 * @param timeBase The timeBase to set
	 */
	public void setTimeBase(long timeBase) {
		this.timeBase = timeBase;
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

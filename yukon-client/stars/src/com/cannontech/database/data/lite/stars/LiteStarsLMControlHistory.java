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
		if (lmControlHistory == null)
			lmControlHistory = new ArrayList();
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
	 * Sets the currentDayStartIndex.
	 * @param currentDayStartIndex The currentDayStartIndex to set
	 */
	public void setCurrentDayStartIndex(int currentDayStartIndex) {
		this.currentDayStartIndex = currentDayStartIndex;
	}

	/**
	 * Sets the currentMonthStartIndex.
	 * @param currentMonthStartIndex The currentMonthStartIndex to set
	 */
	public void setCurrentMonthStartIndex(int currentMonthStartIndex) {
		this.currentMonthStartIndex = currentMonthStartIndex;
	}

	/**
	 * Sets the currentWeekStartIndex.
	 * @param currentWeekStartIndex The currentWeekStartIndex to set
	 */
	public void setCurrentWeekStartIndex(int currentWeekStartIndex) {
		this.currentWeekStartIndex = currentWeekStartIndex;
	}

	/**
	 * Sets the currentYearStartIndex.
	 * @param currentYearStartIndex The currentYearStartIndex to set
	 */
	public void setCurrentYearStartIndex(int currentYearStartIndex) {
		this.currentYearStartIndex = currentYearStartIndex;
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

}

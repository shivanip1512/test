package com.cannontech.database.data.lite.stars;

import java.util.Calendar;
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
	
	private long timeBase = 0;
	private int currentYearStartIndex = 0;
	private int currentMonthStartIndex = 0;
	private int currentWeekStartIndex = 0;
	private int currentDayStartIndex = 0;
	private java.util.ArrayList lmControlHistory = null;
	
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
	
	public void updateStartIndices() {
		if (lmControlHistory == null) return;
		
		Calendar oldCal = Calendar.getInstance();
		oldCal.setTime( new java.util.Date(timeBase) );
		Calendar limitCal = Calendar.getInstance();
		timeBase = limitCal.getTime().getTime();
		
		// Set the time limit to the beginning of the day
		limitCal.set(Calendar.HOUR_OF_DAY, 0);
		limitCal.set(Calendar.MINUTE, 0);
		limitCal.set(Calendar.SECOND, 0);
		if (oldCal.after(limitCal)) return;
			
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
		
		// Set the time limit to the beggining of the week
		Calendar limitCal2 = Calendar.getInstance();
		limitCal2.setTime( limitCal.getTime() );
		limitCal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		
		if (oldCal.before(limitCal2)) {
			int curIndex2 = curIndex;
			while (curIndex2 >= 0) {
				LiteLMControlHistory ctrlHist = (LiteLMControlHistory) lmControlHistory.get( curIndex2 );
				if (ctrlHist.getStartDateTime() < limitCal2.getTime().getTime()) {
					currentWeekStartIndex = curIndex2 + 1;
					break;
				}
				curIndex2--;
			}
			if (curIndex2 < 0) currentWeekStartIndex = 0;
		}
		
		// Set the time limit to the beginning of the month
		limitCal.set(Calendar.DAY_OF_MONTH, 1);
		if (oldCal.after(limitCal)) return;
		
		while (curIndex >= 0) {
			LiteLMControlHistory ctrlHist = (LiteLMControlHistory) lmControlHistory.get( curIndex );
			if (ctrlHist.getStartDateTime() < limitCal.getTime().getTime()) {
				currentMonthStartIndex = curIndex + 1;
				break;
			}
			curIndex--;
		}
		if (curIndex < 0) currentMonthStartIndex = 0;
		
		// Set the time limit to the beginning of the year
		limitCal.set(Calendar.MONTH, Calendar.JANUARY);
		if (oldCal.after(limitCal)) return;
		
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
	public java.util.ArrayList getLmControlHistory() {
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
	public void setLmControlHistory(java.util.ArrayList lmControlHistory) {
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

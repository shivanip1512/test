package com.cannontech.database.data.lite.stars;

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
public class LiteLMControlHistory extends LiteBase {

	private long startDateTime = 0;
	private long controlDuration = 0;
	private String controlType = null;
	private long currentDailyTime = 0;
	private long currentMonthlyTime = 0;
	private long currentSeasonalTime = 0;
	private long currentAnnualTime = 0;
	
	public LiteLMControlHistory() {
		super();
	}
	
	public LiteLMControlHistory(int lmCtrlHistID) {
		super();
		setLmCtrlHistID( lmCtrlHistID );
		setLiteType( LiteTypes.STARS_CONTROL_HISTORY );
	}
	
	public int getLmCtrlHistID() {
		return getLiteID();
	}
	
	public void setLmCtrlHistID(int lmCtrlHistID) {
		setLiteID( lmCtrlHistID );
	}

	/**
	 * Returns the controlDuration.
	 * @return long
	 */
	public long getControlDuration() {
		return controlDuration;
	}

	/**
	 * Returns the currentAnnualTime.
	 * @return long
	 */
	public long getCurrentAnnualTime() {
		return currentAnnualTime;
	}

	/**
	 * Returns the currentDailyTime.
	 * @return long
	 */
	public long getCurrentDailyTime() {
		return currentDailyTime;
	}

	/**
	 * Returns the currentMonthlyTime.
	 * @return long
	 */
	public long getCurrentMonthlyTime() {
		return currentMonthlyTime;
	}

	/**
	 * Returns the currentSeasonalTime.
	 * @return long
	 */
	public long getCurrentSeasonalTime() {
		return currentSeasonalTime;
	}

	/**
	 * Returns the startDateTime.
	 * @return long
	 */
	public long getStartDateTime() {
		return startDateTime;
	}

	/**
	 * Sets the controlDuration.
	 * @param controlDuration The controlDuration to set
	 */
	public void setControlDuration(long controlDuration) {
		this.controlDuration = controlDuration;
	}

	/**
	 * Sets the currentAnnualTime.
	 * @param currentAnnualTime The currentAnnualTime to set
	 */
	public void setCurrentAnnualTime(long currentAnnualTime) {
		this.currentAnnualTime = currentAnnualTime;
	}

	/**
	 * Sets the currentDailyTime.
	 * @param currentDailyTime The currentDailyTime to set
	 */
	public void setCurrentDailyTime(long currentDailyTime) {
		this.currentDailyTime = currentDailyTime;
	}

	/**
	 * Sets the currentMonthlyTime.
	 * @param currentMonthlyTime The currentMonthlyTime to set
	 */
	public void setCurrentMonthlyTime(long currentMonthlyTime) {
		this.currentMonthlyTime = currentMonthlyTime;
	}

	/**
	 * Sets the currentSeasonalTime.
	 * @param currentSeasonalTime The currentSeasonalTime to set
	 */
	public void setCurrentSeasonalTime(long currentSeasonalTime) {
		this.currentSeasonalTime = currentSeasonalTime;
	}

	/**
	 * Sets the startDateTime.
	 * @param startDateTime The startDateTime to set
	 */
	public void setStartDateTime(long startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * Returns the controlType.
	 * @return String
	 */
	public String getControlType() {
		return controlType;
	}

	/**
	 * Sets the controlType.
	 * @param controlType The controlType to set
	 */
	public void setControlType(String controlType) {
		this.controlType = controlType;
	}

}

package com.cannontech.stars.util.timertask;

import java.util.TimerTask;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class StarsTimerTask extends TimerTask {
	
	/**
	 * Get the period of the timer task.
	 */
	public abstract long getTimerPeriod();
	
	/**
	 * Get the delay of executing the timer task after it's been initiated,
	 * negative value means no initial execution.
	 */
	public abstract long getInitialDelay();
	
	/**
	 * Get the next (from now) scheduled time the timer task is supposed to run
	 */
	public abstract java.util.Date getNextScheduledTime();

}

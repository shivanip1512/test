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
	
	private static long MIN_INIT_DELAY = 1000 * 60 * 1;	// 1 minutes
	private static long MAX_INIT_DELAY = 1000 * 60 * 5;	// 5 minutes
	
	/**
	 * Return whether the timer task is fixed-rate or fixed delay.
	 * If the function returns true, which means fixed-rate, the timer task
	 * will be scheduled relative to the initial execution time; otherwise,
	 * if it's fixed-delay, the timer task will be scheduled relative to the
	 * time of the previous execution.
	 */
	public abstract boolean isFixedRate();
	
	/**
	 * Return the period of the timer task.
	 */
	public abstract long getTimerPeriod();
	
	/**
	 * Return the delay of executing the timer task after it's been initiated,
	 * negative value means no initial execution.
	 */
	public long getInitialDelay() {
		return (long) (MIN_INIT_DELAY + Math.random() * (MAX_INIT_DELAY - MIN_INIT_DELAY));
	}
	
	/**
	 * Return the next scheduled time of the timer task.
	 * If the timer task is fixed-rate, then after the initial execution, the
	 * timer task will be scheduled to start running at the next scheduled time.
	 */
	public abstract java.util.Date getNextScheduledTime();

}

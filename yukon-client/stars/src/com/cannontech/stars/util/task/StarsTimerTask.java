package com.cannontech.stars.util.task;

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
	 * Return the next scheduled time of the timer task.
	 * If the timer task is fixed-rate, then after the initial execution, the
	 * timer task will be scheduled to start running at the next scheduled time.
	 */
	public abstract java.util.Date getNextScheduledTime();
    
    public long getNextTimeOffsetMillis() {
        return getNextScheduledTime().getTime() - System.currentTimeMillis();
    }

}

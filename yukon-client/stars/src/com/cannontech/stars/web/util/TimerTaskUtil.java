/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;

import java.util.Timer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.util.task.DailyTimerTask;
import com.cannontech.stars.util.task.HourlyTimerTask;
import com.cannontech.stars.util.task.RefreshTimerTask;
import com.cannontech.stars.util.task.StarsTimerTask;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TimerTaskUtil {

	// Timer object for less frequently happened tasks
	private static Timer timer1 = null;
	// Timer object for frequently happened tasks
	private static Timer timer2 = null;
	
	private static void runTimerTask(StarsTimerTask timerTask, Timer timer) {
		if (timerTask.isFixedRate()) {
			// Run the first time after the initial delay,
			// then run periodically at a fixed rate, e.g. at every midnight
			timer.scheduleAtFixedRate( timerTask, timerTask.getNextScheduledTime(), timerTask.getTimerPeriod() );
		}
		else if (timerTask.getTimerPeriod() == 0) {
			// Run just once after the initial delay,
			// If initial delay set to 0, has the same effect as creating a new thread
			timer.schedule( timerTask, timerTask.getInitialDelay() );
		}
		else {
			// Run the first time after the initial delay,
			// then run periodically at a fixed delay, e.g. every 5 minutes
			timer.schedule( timerTask, timerTask.getInitialDelay(), timerTask.getTimerPeriod() );
		}
	}
	
	public static void restartFrequentTimerTasks() {
		if (timer2 != null) timer2.cancel();
		timer2 = new Timer();
		runTimerTask( new HourlyTimerTask(), timer2 );
		runTimerTask( new RefreshTimerTask(), timer2 );
		
		CTILogger.info("All frequent timer tasks restarted");
	}
	
	public static void restartAllTimerTasks() {
		if (timer1 != null) timer1.cancel();
		timer1 = new Timer();
		runTimerTask( new DailyTimerTask(), timer1 );
		
		restartFrequentTimerTasks();
		
		CTILogger.info("All timer tasks restarted");
	}
}

/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.servlet;

import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

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
public class TimerTaskServlet extends HttpServlet {
    
	// Timer object for periodical tasks
	private static Timer timer = new Timer();
    
	private static StarsTimerTask[] timerTasks = {
		new DailyTimerTask(),
		new HourlyTimerTask(),
		new RefreshTimerTask()
	};

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();
		
		for (int i = 0; i < timerTasks.length; i++)
			runTimerTask( timerTasks[i] );
		
		CTILogger.info("All timer tasks started");
	}
    
	public static void runTimerTask(StarsTimerTask timerTask) {
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

}

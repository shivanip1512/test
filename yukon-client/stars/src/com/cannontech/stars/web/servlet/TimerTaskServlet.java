/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private Timer timer = null;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();
		
		timer = new Timer();
		runTimerTask( new DailyTimerTask() );
		runTimerTask( new HourlyTimerTask() );
		runTimerTask( new RefreshTimerTask() );
		
		CTILogger.info("All timer tasks started");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		if (req.getParameter("Restart") != null)
			restartTimerTasks();
	}
    
	private void runTimerTask(StarsTimerTask timerTask) {
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
	
	private void restartTimerTasks() {
		if (timer != null) timer.cancel();
		
		timer = new Timer();
		runTimerTask( new DailyTimerTask() );
		runTimerTask( new HourlyTimerTask() );
		runTimerTask( new RefreshTimerTask() );
		
		CTILogger.info("All timer tasks restarted");
	}

}

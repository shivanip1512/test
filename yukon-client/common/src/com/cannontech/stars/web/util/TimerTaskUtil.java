package com.cannontech.stars.web.util;

import java.util.concurrent.TimeUnit;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.stars.util.task.DailyTimerTask;
import com.cannontech.stars.util.task.LMCtrlHistTimerTask;
import com.cannontech.stars.util.task.StarsTimerTask;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TimerTaskUtil {

	// Timer object for less frequently happened tasks
	private ScheduledExecutor timer;
	
	private void runTimerTask(StarsTimerTask timerTask) {
        // This class used to go to great lengths to clear and reschedule all of the classes.
        // Because it no longer does this, make sure that the tasks don't throw any exceptions.
        
        Runnable safeRunner = createSafeRunnable(timerTask);
        long randomDelay = (long) (30000 * Math.random() + 10000);
		if (timerTask.isFixedRate()) {
			// Run the first time after the initial delay,
			// then run periodically at a fixed rate, e.g. at every midnight
			timer.scheduleAtFixedRate( safeRunner, timerTask.getNextTimeOffsetMillis(), 
                                       timerTask.getTimerPeriod(), TimeUnit.MILLISECONDS );
		}
		else if (timerTask.getTimerPeriod() == 0) {
			// Run just once after the initial delay,
			// If initial delay set to 0, has the same effect as creating a new thread
			timer.schedule( safeRunner, randomDelay, TimeUnit.MILLISECONDS );
		}
		else {
			// Run the first time after the initial delay,
			// then run periodically at a fixed delay, e.g. every 5 minutes
			timer.scheduleWithFixedDelay( safeRunner, randomDelay, 
                                          timerTask.getTimerPeriod(), TimeUnit.MILLISECONDS );
		}
	}
	
	public void startAllTimerTasks() {
		runTimerTask( new DailyTimerTask());
		
        runTimerTask( new LMCtrlHistTimerTask());
        
		CTILogger.info("Stars timer tasks started");
	}
    
    private Runnable createSafeRunnable(final StarsTimerTask stt) {
        return new Runnable() {
            public void run() {
                try {
                    stt.run();
                } catch (Exception e) {
                    CTILogger.error("a StarsTimerTask threw an exception", e);
                }
            }
        };
    }

    public ScheduledExecutor getTimer() {
        return timer;
    }

    public void setTimer(ScheduledExecutor timer) {
        this.timer = timer;
    }
}

/*
 * Created on Dec 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.util.LMControlHistoryUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMCtrlHistTimerTask extends StarsTimerTask {
	
	public static final long TIMER_PERIOD = 1000 * 60 * 5;	// 5 minutes

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.StarsTimerTask#isFixedRate()
	 */
	public boolean isFixedRate() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.StarsTimerTask#getTimerPeriod()
	 */
	public long getTimerPeriod() {
		return TIMER_PERIOD;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.StarsTimerTask#getNextScheduledTime()
	 */
	public Date getNextScheduledTime() {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.debug( "*** Start control history timer task ***" );
		
		LMControlHistoryUtil.updateActiveControlHistory();
		
		CTILogger.debug( "*** End control history timer task ***" );
	}

}

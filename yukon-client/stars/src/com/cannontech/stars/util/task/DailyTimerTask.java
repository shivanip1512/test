/*
 * Created on May 3, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.web.servlet.TimerTaskServlet;
import com.cannontech.util.ServletUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DailyTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 60 * 24;	// 1 day

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.StarsTimerTask#isFixedRate()
	 */
	public boolean isFixedRate() {
		return true;
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
		return ServletUtil.getTomorrow();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.debug( "*** Daily timer task start ***" );
		
		ArrayList companies = SOAPServer.getAllEnergyCompanies();
		if (companies == null) return;
		
		for (int i = 0; i < companies.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
			if (ECUtils.isDefaultEnergyCompany( company )) continue;
			
			try {
				InventoryManager.sendSwitchCommands( company, null );
			}
			catch (WebClientException e) {
				CTILogger.debug( e.getMessage() );
			}
			
			// Clear all the *active* information
			company.clearActiveAccounts();
		}
		
		// Restart *frequently happened* timer tasks
		TimerTaskServlet.restartFrequentTimerTasks();
		
		CTILogger.debug( "*** Daily timer task stop ***" );
	}

}

package com.cannontech.stars.util.timertask;

import java.util.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMControlHistoryTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 5;	// 5 minutes

	/**
	 * @see com.cannontech.stars.util.timertask.StarsTimerTask#isFixedRate()
	 */
	public boolean isFixedRate() {
		return false;
	}

	/**
	 * @see com.cannontech.stars.util.timertask.StarsTimerTask#getTimerPeriod()
	 */
	public long getTimerPeriod() {
		return TIMER_PERIOD;
	}

	/**
	 * @see com.cannontech.stars.util.timertask.StarsTimerTask#getNextScheduledTime()
	 */
	public Date getNextScheduledTime() {
		return null;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.info( "*** Start LMControlHistory timer task ***" );
		
		LiteStarsEnergyCompany[] companies = SOAPServer.getAllEnergyCompanies();
		for (int i = 0; i < companies.length; i++) {
			if (companies[i].getEnergyCompanyID().intValue() < 0) continue;
			
			ArrayList ctrlHistList = SOAPServer.getAllLMControlHistory( companies[i].getLiteID() );
			if (ctrlHistList.size() == 0) continue;

			for (int j = 0; j < ctrlHistList.size(); j++) {
				LiteStarsLMControlHistory liteCtrlHist = (LiteStarsLMControlHistory) ctrlHistList.get(j);
				SOAPServer.updateLMControlHistory( liteCtrlHist );
			}
		}
		
		CTILogger.info( "*** End LMControlHistory timer task ***" );
	}

}

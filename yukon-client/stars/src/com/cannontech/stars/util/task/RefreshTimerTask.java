package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RefreshTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 1;	// 5 minutes
	
	private static final long GED_EXPIRATION_TIME = 1000 * 60 * 60;	// Expiration time for updating a GatewayEndDevice

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
		CTILogger.info( "*** Start Refresh timer task ***" );
		
		LiteStarsEnergyCompany[] companies = SOAPServer.getAllEnergyCompanies();
		
		// Update LMControlHistory
		for (int i = 0; i < companies.length; i++) {
			if (companies[i].getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID) continue;
			
			ArrayList ctrlHistList = companies[i].getAllLMControlHistory();
			for (int j = 0; j < ctrlHistList.size(); j++) {
				LiteStarsLMControlHistory liteCtrlHist = (LiteStarsLMControlHistory) ctrlHistList.get(j);
				SOAPServer.updateLMControlHistory( liteCtrlHist );
				companies[i].updateStarsLMControlHistory( liteCtrlHist );
			}
		}
		
		// Update GatewayEndDevice
		long now = System.currentTimeMillis();
		for (int i = 0; i < companies.length; i++) {
			if (companies[i].getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID) continue;
			
			ArrayList accountList = companies[i].getAccountsWithGatewayEndDevice();
			
			// Remove all the "expired" customer accounts
			synchronized (accountList) {
				Iterator it = accountList.iterator();
				while (it.hasNext()) {
					LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) it.next();
					if (now - liteAcctInfo.getLastLoginTime() > GED_EXPIRATION_TIME)
						it.remove();
				}
			}
			
			for (int j = 0; j < accountList.size(); j++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) accountList.get(j);
				companies[i].updateThermostatSettings( liteAcctInfo );
			}
		}
		
		CTILogger.info( "*** End Refresh timer task ***" );
	}

}

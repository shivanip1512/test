package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMControlHistory;
import com.cannontech.stars.util.ECUtils;
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
		CTILogger.debug( "*** Start Refresh timer task ***" );
		
		ArrayList companies = SOAPServer.getAllEnergyCompanies();
		
		// Update from LMControlHistory
		for (int i = 0; i < companies.size(); i++) {
    		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
			if (ECUtils.isDefaultEnergyCompany( company )) continue;
			
			ArrayList ctrlHistList = company.getAllLMControlHistory();
			for (int j = 0; j < ctrlHistList.size(); j++) {
				LiteStarsLMControlHistory liteCtrlHist = (LiteStarsLMControlHistory) ctrlHistList.get(j);
				SOAPServer.updateLMControlHistory( liteCtrlHist );
				company.updateStarsLMControlHistory( liteCtrlHist );
			}
		}
		
		// Update from GatewayEndDevice
		long now = System.currentTimeMillis();
		for (int i = 0; i < companies.size(); i++) {
    		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
			if (ECUtils.isDefaultEnergyCompany( company )) continue;
			
			ArrayList accountList = company.getAccountsWithGatewayEndDevice();
			
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
				company.updateThermostatSettings( liteAcctInfo );
			}
		}
		
		CTILogger.debug( "*** End Refresh timer task ***" );
	}

}

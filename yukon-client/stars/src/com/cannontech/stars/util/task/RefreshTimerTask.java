package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RefreshTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 1;	// 1 minutes
	
	// Length of time without any action from the client to consider an account inactive
	private static final long INACTIVE_INTERVAL = 1000 * 60 * 60;

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
		
		ArrayList companies = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
		
		for (int i = 0; i < companies.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
			if (ECUtils.isDefaultEnergyCompany( company )) continue;
			
			// Update the real-time data for EnergyPro thermostats
			ArrayList activeAccounts = company.getActiveAccounts();
			for (int j = 0; j < activeAccounts.size(); j++) {
				StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) activeAccounts.get(j);
				
				// If account is no longer active, remove it from the active account list
				if (System.currentTimeMillis() - starsAcctInfo.getLastActiveTime().getTime() > INACTIVE_INTERVAL) {
					company.deleteStarsCustAccountInformation( starsAcctInfo.getStarsCustomerAccount().getAccountID() );
					continue;
				}
				
				LiteStarsCustAccountInformation liteAcctInfo = company.getCustAccountInformation(
						starsAcctInfo.getStarsCustomerAccount().getAccountID(), false );
				if (liteAcctInfo != null)
					company.updateThermostatSettings( liteAcctInfo );
			}
		}
		
		CTILogger.debug( "*** End Refresh timer task ***" );
	}

}

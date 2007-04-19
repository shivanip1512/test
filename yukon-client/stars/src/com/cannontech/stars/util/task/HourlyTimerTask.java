package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.action.ProgramOptOutAction;
import com.cannontech.stars.web.action.ProgramReenableAction;
import com.cannontech.user.UserUtils;

/**
 * @author yao
 * Run at midnight every day
 */
public class HourlyTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 60;	// 1 hour
	private static final long TIME_LIMIT = 1000 * 60 * 30;	// 30 minutes

	/**
	 * @see com.cannontech.stars.util.timertask.StarsTimerTask#isFixedRate()
	 */
	public boolean isFixedRate() {
		return true;
	}
	
	/**
	 * @see com.cannontech.stars.util.timer.StarsTimer#getTimerPeriod()
	 */
	public long getTimerPeriod() {
		return TIMER_PERIOD;
	}

	/**
	 * @see com.cannontech.stars.util.timer.StarsTimer#getNextScheduledTime()
	 */
	public Date getNextScheduledTime() {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.add( Calendar.HOUR_OF_DAY, 1 );
		return cal.getTime();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.debug( "*** Hourly timer task start ***" );
		
		/* Check for opted out programs that should be reactivated */
		ArrayList companies = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
		if (companies == null) return;
		
		Date now = new Date();
		for (int i = 0; i < companies.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
			if (ECUtils.isDefaultEnergyCompany( company )) continue;
			
			OptOutEventQueue.OptOutEvent[] dueEvents = OptOutEventQueue.getInstance().getDueEvents(company.getLiteID(), TIME_LIMIT);
			
			for (int j = 0; j < dueEvents.length; j++) {
				// If the opt out event has already expired, then do nothing
				Calendar reenableTime = null;
				if (dueEvents[j].getPeriod() >= 0) {
					reenableTime = Calendar.getInstance();
					reenableTime.setTime( new Date(dueEvents[j].getStartDateTime()) );
					reenableTime.add( Calendar.HOUR_OF_DAY, dueEvents[j].getPeriod() );
					if (reenableTime.getTimeInMillis() - now.getTime() < TIME_LIMIT) continue;
				}
				
				LiteStarsCustAccountInformation liteAcctInfo = company.getCustAccountInformation( dueEvents[j].getAccountID(), true );
				if (liteAcctInfo == null) continue;
				/*
                 * With permissions now necessary to send commands, we'll use the admin user
                 * for automated STARS sends.
				 */
                YukonUserDao yukonUserDao = DaoFactory.getYukonUserDao();
                LiteYukonUser user = yukonUserDao.getLiteYukonUser(UserUtils.USER_ADMIN_ID);
                
				try {
					if (dueEvents[j].getPeriod() == OptOutEventQueue.PERIOD_REENABLE) {	// This is a "reenable" event
						ArrayList hardwares = new ArrayList();
						if (dueEvents[j].getInventoryID() != 0)
							hardwares.add( company.getInventory(dueEvents[j].getInventoryID(), true) );
						else
							hardwares = ProgramOptOutAction.getAffectedHardwares( liteAcctInfo, company );
						
						for (int k = 0; k < hardwares.size(); k++) {
							LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(k);
							if (liteHw == null) continue;
							
							String cmd = ProgramReenableAction.getReenableCommand( liteHw, company );
							int routeID = liteHw.getRouteID();
							if (routeID == 0) routeID = company.getDefaultRouteID();
							ServerUtils.sendSerialCommand( cmd, routeID, user );
						}
						
						ProgramReenableAction.handleReenableEvent( dueEvents[j], company );
					}
					else {	// This is a "opt out" event
						ArrayList hardwares = new ArrayList();
						if (dueEvents[j].getInventoryID() != 0)
							hardwares.add( company.getInventory(dueEvents[j].getInventoryID(), true) );
						else
							hardwares = ProgramOptOutAction.getAffectedHardwares( liteAcctInfo, company );
						
						for (int k = 0; k < hardwares.size(); k++) {
							LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(k);
							
							String cmd = ProgramOptOutAction.getOptOutCommand( liteHw, company, dueEvents[j].getPeriod() );
							int routeID = liteHw.getRouteID();
							if (routeID == 0) routeID = company.getDefaultRouteID();
							ServerUtils.sendSerialCommand( cmd, routeID, user );
						}
						
						ProgramOptOutAction.handleOptOutEvent( dueEvents[j], company );
						
						// Insert a corresponding "reenable" event back into the queue
						OptOutEventQueue.OptOutEvent e = new OptOutEventQueue.OptOutEvent();
						e.setEnergyCompanyID( company.getLiteID() );
						e.setStartDateTime( reenableTime.getTimeInMillis() );
						e.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
						e.setAccountID( dueEvents[j].getAccountID() );
						e.setInventoryID( dueEvents[j].getInventoryID() );
						OptOutEventQueue.getInstance().addEvent( e, false );
					}
				}
				catch (Exception e) {
					com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
				}
			}
			
			// Synchronize the event queue to disk file
			OptOutEventQueue.getInstance().addEvent( null, true );
		}
		
		CTILogger.debug( "*** Hourly timer task stop ***" );
	}

}

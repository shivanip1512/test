package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.web.action.ProgramOptOutAction;
import com.cannontech.stars.web.action.ProgramReenableAction;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
import com.cannontech.stars.xml.serialize.StarsProgramReenable;

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
		CTILogger.info( "*** Hourly timer task start ***" );
		
		/* Check for opted out programs that should be reactivated */
		ArrayList companies = SOAPServer.getAllEnergyCompanies();
		if (companies == null) return;
		
		Date now = new Date();
		for (int i = 0; i < companies.size(); i++) {
    		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
			if (ECUtils.isDefaultEnergyCompany( company )) continue;
			
			OptOutEventQueue queue = company.getOptOutEventQueue();
			if (queue == null) {
				CTILogger.error("Cannot get the OptOutEventQueue for energy company #" + company.getLiteID());
				continue; 
			}
			
			OptOutEventQueue.OptOutEvent[] dueEvents = queue.getDueEvents(company.getLiteID(), TIME_LIMIT);
			
			for (int j = 0; j < dueEvents.length; j++) {
				// If the opt out event has already expired, then do nothing
				if (dueEvents[j].getPeriod() >= 0) {
					Calendar cal = Calendar.getInstance();
					cal.add( Calendar.DATE, dueEvents[j].getPeriod() );
					if (cal.getTime().getTime() - now.getTime() < TIME_LIMIT) continue;
				}
				
				LiteStarsCustAccountInformation liteAcctInfo = company.getCustAccountInformation( dueEvents[j].getAccountID(), true );
				if (liteAcctInfo == null) continue;
				
				try {
					// Send out "opt out"/"reenable" command
					String[] commands = dueEvents[j].getCommand().split(",");
					
					com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
					synchronized (yc) {
						yc.setRouteID( company.getDefaultRouteID() );
						for (int k = 0; k < commands.length; k++) {
							yc.setCommand( commands[k] );
							yc.handleSerialNumber();
						}
					}
					
					if (dueEvents[j].getPeriod() == OptOutEventQueue.PERIOD_REENABLE) {	// This is a "reenable" event
						StarsProgramReenable reEnable = new StarsProgramReenable();
						ProgramReenableAction.updateCustAccountInfo( liteAcctInfo, company );
					}
					else {	// This is a "opt out" event
						StarsProgramOptOut optOut = new StarsProgramOptOut();
						optOut.setStartDateTime( new Date(dueEvents[j].getStartDateTime()) );
						optOut.setPeriod( dueEvents[j].getPeriod() );
						ProgramOptOutAction.updateCustAccountInfo( liteAcctInfo, company, optOut );
						
						// Insert a corresponding "reenable" event back into the queue
						OptOutEventQueue.OptOutEvent e = new OptOutEventQueue.OptOutEvent();
						e.setEnergyCompanyID( company.getLiteID() );
						Calendar cal = Calendar.getInstance();
						cal.setTime( new Date(dueEvents[j].getStartDateTime()) );
						cal.add( Calendar.DATE, dueEvents[j].getPeriod() );
						e.setStartDateTime( cal.getTime().getTime() );
						e.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
						e.setAccountID( dueEvents[j].getAccountID() );
		            	
						commands = ProgramReenableAction.getReenableCommands( liteAcctInfo, company );
		            	StringBuffer cmd = new StringBuffer();
		            	if (commands.length > 0) {
		            		cmd.append( commands[0] );
		            		for (int k = 1; k < commands.length; k++)
		            			cmd.append( "," ).append( commands[k] );
		            	}
		            	e.setCommand( cmd.toString() );
		            	company.getOptOutEventQueue().addEvent( e, false );
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// Synchronize the event queue to disk file
			company.getOptOutEventQueue().addEvent( null, true );
		}
		
		CTILogger.info( "*** Hourly timer task stop ***" );
	}

}

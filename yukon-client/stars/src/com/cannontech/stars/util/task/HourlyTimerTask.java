package com.cannontech.stars.util.task;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
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
		LiteStarsEnergyCompany[] companies = SOAPServer.getAllEnergyCompanies();
		if (companies == null) return;
		
		Date now = new Date();
		for (int i = 0; i < companies.length; i++) {
			OptOutEventQueue.OptOutEvent[] dueEvents = companies[i].getOptOutEventQueue().getDueEvents(TIME_LIMIT);
			
			for (int j = 0; j < dueEvents.length; j++) {
				// If the opt out event has already expired, then do nothing
				if (dueEvents[j].getPeriod() >= 0) {
					Calendar cal = Calendar.getInstance();
					cal.add( Calendar.DATE, dueEvents[j].getPeriod() );
					if (cal.getTime().getTime() - now.getTime() < TIME_LIMIT) continue;
				}
				LiteStarsCustAccountInformation liteAcctInfo = companies[i].getCustAccountInformation( dueEvents[j].getAccountID(), true );
				
				try {
					// Execute "opt out"/"reenable" command
					StringTokenizer st = new StringTokenizer( dueEvents[j].getCommand(), "," );
					while (st.hasMoreTokens())
						ServerUtils.sendCommand( st.nextToken() );
					
					if (dueEvents[j].getPeriod() == OptOutEventQueue.PERIOD_REENABLE) {	// This is a "reenable" event
						StarsProgramReenable reEnable = new StarsProgramReenable();
						ProgramReenableAction.updateCustAccountInfo( liteAcctInfo, companies[i], reEnable );
					}
					else {	// This is a "opt out" event
						StarsProgramOptOut optOut = new StarsProgramOptOut();
						optOut.setStartDateTime( new Date(dueEvents[j].getStartDateTime()) );
						optOut.setPeriod( dueEvents[j].getPeriod() );
						ProgramOptOutAction.updateCustAccountInfo( liteAcctInfo, companies[i], optOut );
						
						// Insert a corresponding "reenable" event back into the queue
						OptOutEventQueue.OptOutEvent e = new OptOutEventQueue.OptOutEvent();
						Calendar cal = Calendar.getInstance();
						cal.setTime( new Date(dueEvents[j].getStartDateTime()) );
						cal.add( Calendar.DATE, dueEvents[j].getPeriod() );
						e.setStartDateTime( cal.getTime().getTime() );
						e.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
						e.setAccountID( dueEvents[j].getAccountID() );
		            	
						String[] commands = ProgramReenableAction.getReenableCommands( liteAcctInfo, companies[i] );
		            	StringBuffer cmd = new StringBuffer();
		            	if (commands.length > 0) {
		            		cmd.append( commands[0] );
		            		for (int k = 1; k < commands.length; k++)
		            			cmd.append( "," ).append( commands[k] );
		            	}
		            	e.setCommand( cmd.toString() );
		            	companies[i].getOptOutEventQueue().addEvent( e, false );
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// Synchronize the event queue to disk file
			companies[i].getOptOutEventQueue().addEvent( null, true );
		}
		
		CTILogger.info( "*** Hourly timer task stop ***" );
	}

}

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
public class DailyTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 60 * 24;	// 1 day

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
		return com.cannontech.util.ServletUtil.getTommorow();	// Run at every midnight
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.info( "*** Daily timer task start ***" );
		
		/* Check for opted out programs that should be reactivated */
		LiteStarsEnergyCompany[] companies = SOAPServer.getAllEnergyCompanies();
		if (companies == null) return;
		
		Date now = new Date();
		Calendar timeLimit = Calendar.getInstance();
		timeLimit.add(Calendar.MINUTE, 30);	// Give the time limit a 30 minutes margin
		
		for (int i = 0; i < companies.length; i++) {
			ArrayList dueEvents = companies[i].getOptOutEventQueue().consumeEvents( timeLimit.getTime().getTime() );
			for (int j = 0; j < dueEvents.size(); j++) {
				OptOutEventQueue.OptOutEvent event = (OptOutEventQueue.OptOutEvent) dueEvents.get(j);

				try {
					// Execute "opt out" and "reenable" commands
					StringTokenizer st = new StringTokenizer( event.getCommand(), "," );
					while (st.hasMoreTokens())
						ServerUtils.sendCommand( st.nextToken() );
					
					// Update the lite and stars objects
					LiteStarsCustAccountInformation liteAcctInfo = companies[i].getCustAccountInformation( event.getAccountID(), false );
					if (liteAcctInfo == null) break;
					if (event.getPeriod() == -1) {	// This is a "reenable" event
						StarsProgramReenable reEnable = new StarsProgramReenable();
						ProgramReenableAction.updateCustAccountInfo( liteAcctInfo, companies[i], reEnable );
					}
					else {	// This is a "opt out" event
						StarsProgramOptOut optOut = new StarsProgramOptOut();
						optOut.setStartDateTime( new Date(event.getStartDateTime()) );
						optOut.setPeriod( event.getPeriod() );
						ProgramOptOutAction.updateCustAccountInfo( liteAcctInfo, companies[i], optOut );
						
						// Insert a corresponding "reenable" event back into the queue
						OptOutEventQueue.OptOutEvent e = new OptOutEventQueue.OptOutEvent();
						Calendar cal = Calendar.getInstance();
						cal.setTime( new Date(event.getStartDateTime()) );
						cal.add( Calendar.DATE, event.getPeriod() );
						e.setStartDateTime( cal.getTime().getTime() );
						e.setPeriod( -1 );	// "Reenable" event
						e.setAccountID( event.getAccountID() );
		            	
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
		
		CTILogger.info( "*** Daily timer task stop ***" );
	}

}

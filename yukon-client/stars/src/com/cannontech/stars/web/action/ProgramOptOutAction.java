package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
import com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.util.ServletUtil;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProgramOptOutAction implements ActionBase {
	
	public static final int REPEAT_LAST = -1;
	public static final int OPTOUT_TODAY = -2;

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
            
            StarsProgramOptOut optOut = new StarsProgramOptOut();
            int period = 0;
            
            if (req.getParameter("OptOutPeriod") != null) {
	            String periodStr = req.getParameter("OptOutPeriod");
	            if (periodStr != null)
	                try {
	                    period = Integer.parseInt(periodStr);
	                }
	                catch (NumberFormatException e) {}
            }
            if (period == 0) return null;
            
            if (period < 0) {	// Negative period is the number of days to be opted out
            	optOut.setPeriod( Math.abs(period) * 24 );
            }
            else {	// This is a special entry, e.g. "Today"
            	int periodDefID = YukonListFuncs.getYukonListEntry( period ).getYukonDefID();
            	
            	if (periodDefID == YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TOMORROW) {
            		optOut.setStartDateTime( ServletUtil.getTomorrow(energyCompany.getDefaultTimeZone()) );
            		optOut.setPeriod( 24 );
            	}
            	else if (periodDefID == YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TODAY) {
            		optOut.setPeriod( OPTOUT_TODAY );
            	}
            	else if (periodDefID == YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_REPEAT_LAST) {
            		optOut.setPeriod( REPEAT_LAST );
            	}
            }

            StarsOperation operation = new StarsOperation();
            operation.setStarsProgramOptOut( optOut );
            
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        LiteStarsEnergyCompany energyCompany = null;
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
			
            int energyCompanyID = user.getEnergyCompanyID();
        	energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
			TimeZone tz = energyCompany.getDefaultTimeZone();
			String logMsg = null;
			
            StarsProgramOptOut optOut = reqOper.getStarsProgramOptOut();
            StarsProgramOptOutResponse resp = new StarsProgramOptOutResponse();
			
			try {
				checkOptOutRules( liteAcctInfo, user, optOut );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
            // Get all the hardwares to be opted out
            ArrayList hardwares = null;
            if (optOut.hasInventoryID()) {
            	hardwares = new ArrayList();
				hardwares.add( energyCompany.getInventory(optOut.getInventoryID(), true) );
            }
            else {
				hardwares = getAffectedHardwares( liteAcctInfo, energyCompany );
				if (hardwares.size() == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "There is no hardware assigned to any program") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
            }
            
            if (optOut.getStartDateTime() != null) {
            	// Schedule the opt out event
				OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
				event.setEnergyCompanyID( energyCompanyID );
				event.setStartDateTime( optOut.getStartDateTime().getTime() );
				event.setPeriod( optOut.getPeriod() );
				event.setAccountID( liteAcctInfo.getAccountID() );
				event.setInventoryID( optOut.getInventoryID() );
				energyCompany.getOptOutEventQueue().addEvent( event );
				
            	resp.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
            	resp.setDescription( "The " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event has been scheduled" );
            	
				logMsg = "Start Date/Time:" + ServerUtils.formatDate(optOut.getStartDateTime(), tz) +
            			", Duration:" + ServletUtils.getDurationFromHours( optOut.getPeriod() );
				logMsg += ", Serial #:" + ((LiteStarsLMHardware) hardwares.get(0)).getManufacturerSerialNumber();
				for (int i = 1; i < hardwares.size(); i++)
					logMsg += "," + ((LiteStarsLMHardware) hardwares.get(i)).getManufacturerSerialNumber();
            }
            else if (optOut.getPeriod() == OPTOUT_TODAY) {
            	int offHours = (int)((ServletUtil.getTomorrow(tz).getTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
            	
				for (int i = 0; i < hardwares.size(); i++) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
            		
					String cmd = getOptOutCommand( liteHw, energyCompany, offHours );
					int routeID = liteHw.getRouteID();
					if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
					ServerUtils.sendSerialCommand( cmd, routeID );
					
					OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
					event.setEnergyCompanyID( energyCompanyID );
					event.setStartDateTime( ServletUtil.getTomorrow(tz).getTime() );
					event.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
					event.setAccountID( liteAcctInfo.getAccountID() );
					event.setInventoryID( liteHw.getInventoryID() );
					energyCompany.getOptOutEventQueue().addEvent( event );
					
					StarsLMHardwareHistory hwHist = processOptOut( optOut, liteHw, liteAcctInfo, energyCompany );
					resp.addStarsLMHardwareHistory( hwHist );
				}
            	
            	StarsLMProgramHistory progHist = StarsLiteFactory.createStarsLMProgramHistory( liteAcctInfo, energyCompany );
            	resp.setStarsLMProgramHistory( progHist );
            	
		        if (ECUtils.isOperator( user ))
			        resp.setDescription( ServletUtil.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " command has been sent out successfully" );
			    else
			        resp.setDescription( "Your programs have been " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_PAST) );
			    
				logMsg = "Today, Serial #:" + ((LiteStarsLMHardware) hardwares.get(0)).getManufacturerSerialNumber();
				for (int i = 1; i < hardwares.size(); i++)
					logMsg += "," + ((LiteStarsLMHardware) hardwares.get(i)).getManufacturerSerialNumber();
            }
            else if (optOut.getPeriod() == REPEAT_LAST) {
				/* We will only resend the command if there is no opt out event in the event queue
				 * (otherwise the command hasn't been sent out yet), and there is a reenable event
				 * (the opt out event is still active)
				 */
				OptOutEventQueue queue = energyCompany.getOptOutEventQueue();
				OptOutEventQueue.OptOutEvent[] reenableEvents = queue.findReenableEvents( liteAcctInfo.getAccountID() );
				
				if (reenableEvents.length > 0) {
					OptOutEventQueue.OptOutEvent lastEvent = reenableEvents[reenableEvents.length - 1];
					int offHours = (int) ((lastEvent.getStartDateTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
					
					OptOutEventQueue.OptOutEvent optOutEvent = queue.findOptOutEvent( lastEvent.getInventoryID() );
					if (optOutEvent == null) {
						LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( lastEvent.getInventoryID(), true );
						String cmd = getOptOutCommand( liteHw, energyCompany, offHours );
						int routeID = liteHw.getRouteID();
						if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
						ServerUtils.sendSerialCommand( cmd, routeID );
						
						resp.setDescription( "The last " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " command has been resent" );
					}
					else {
						respOper.setStarsFailure( StarsFactory.newStarsFailure(
								StarsConstants.FAILURE_CODE_OPERATION_FAILED, "There is no active " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event") );
						return SOAPUtil.buildSOAPMessage( respOper );
					}
					
					// There may be other hardware involved if the last event is opting out all programs
					for (int i = reenableEvents.length - 2; i >= 0; i--) {
						if (reenableEvents[i].getStartDateTime() != lastEvent.getStartDateTime())
							break;
						
						OptOutEventQueue.OptOutEvent e = queue.findOptOutEvent( reenableEvents[i].getInventoryID() );
						if (e != null) break;
						
						LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( reenableEvents[i].getInventoryID(), true );
						String cmd = getOptOutCommand( liteHw, energyCompany, offHours );
						int routeID = liteHw.getRouteID();
						if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
						ServerUtils.sendSerialCommand( cmd, routeID );
					}
				}
            }
            else {
            	// Send opt out command immediately, and schedule the reenable event
				Calendar reenableTime = Calendar.getInstance();
				reenableTime.add( Calendar.HOUR_OF_DAY, optOut.getPeriod() );
				
				for (int i = 0; i < hardwares.size(); i++) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
					
					String cmd = getOptOutCommand( liteHw, energyCompany, optOut.getPeriod() );
					int routeID = liteHw.getRouteID();
					if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
					ServerUtils.sendSerialCommand( cmd, routeID );
	            	
					OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
					event.setEnergyCompanyID( energyCompanyID );
					event.setStartDateTime( reenableTime.getTimeInMillis() );
					event.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
					event.setAccountID( liteAcctInfo.getAccountID() );
					event.setInventoryID( liteHw.getInventoryID() );
					energyCompany.getOptOutEventQueue().addEvent( event );
					
					StarsLMHardwareHistory hwHist = processOptOut( optOut, liteHw, liteAcctInfo, energyCompany );
					resp.addStarsLMHardwareHistory( hwHist );
				}
            	
            	StarsLMProgramHistory progHist = StarsLiteFactory.createStarsLMProgramHistory( liteAcctInfo, energyCompany );
            	resp.setStarsLMProgramHistory( progHist );
            	
		        if (ECUtils.isOperator( user ))
			        resp.setDescription( ServletUtil.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " command has been sent out successfully" );
			    else
			        resp.setDescription( "Your programs have been " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_PAST) );
			    
				logMsg = "Start Date/Time:(Immediately), Duration:" + ServletUtils.getDurationFromHours( optOut.getPeriod() );
				logMsg += ", Serial #:" + ((LiteStarsLMHardware) hardwares.get(0)).getManufacturerSerialNumber();
				for (int i = 1; i < hardwares.size(); i++)
					logMsg += "," + ((LiteStarsLMHardware) hardwares.get(i)).getManufacturerSerialNumber();
            }
            
			// Log activity
			if (logMsg != null) {
				ActivityLogger.logEvent(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(), liteAcctInfo.getCustomer().getCustomerID(),
						ActivityLogActions.PROGRAM_OPT_OUT_ACTION, logMsg );
			}
			
        	respOper.setStarsProgramOptOutResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_VERB) + " the programs") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	CTILogger.error( e2.getMessage(), e2 );
            }
        }

        return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}

			StarsProgramOptOutResponse resp = operation.getStarsProgramOptOutResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			if (resp.getDescription() != null)
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
            
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
            
            parseResponse( resp, accountInfo, energyCompany );
            
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	static void removeProgramFutureActivationEvents(ArrayList progHist, int programID, LiteStarsEnergyCompany energyCompany) {
		int futureActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ).getEntryID();
		int termID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION ).getEntryID();
		
		try {
			for (int i = progHist.size() - 1; i >= 0; i--) {
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) progHist.get(i);
				if (liteEvent.getProgramID() != programID) continue;
				
				if (liteEvent.getActionID() == termID) break;
				
				if (liteEvent.getActionID() == futureActID) {
					com.cannontech.database.data.stars.event.LMCustomerEventBase event = (com.cannontech.database.data.stars.event.LMCustomerEventBase)
							StarsLiteFactory.createDBPersistent( liteEvent );
					Transaction.createTransaction( Transaction.DELETE, event ).execute();
					progHist.remove( i );
				}
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	static void removeFutureActivationEvents(LiteStarsLMHardware liteHw, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany) {
		int futureActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ).getEntryID();
		
		try {
			Iterator it = liteHw.getInventoryHistory().iterator();
			while (it.hasNext()) {
				LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) it.next();
				if (liteEvent.getActionID() == futureActID) {
					com.cannontech.database.data.stars.event.LMHardwareEvent event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
							StarsLiteFactory.createDBPersistent( liteEvent );
					Transaction.createTransaction( Transaction.DELETE, event ).execute();
					it.remove();
				}
			}
			
			for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
				if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getProgramID() > 0)
					removeProgramFutureActivationEvents( liteAcctInfo.getProgramHistory(), liteApp.getProgramID(), energyCompany );
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	/**
	 * Opt out rules format:
	 * (Max Times)/(Max Hours)/(Period)/(Start Date),(Due Time)
	 * 
	 * For example: "4/96/month/20,4:00PM"
	 * means for every month starting at the 20th, a customer
	 * is only allowed for opting out 4 times or up to 96 hours,
	 * and the request must be submitted by 4:00PM the day before.
	 * 
	 * If a constraint doesn't apply, set it to 0 (e.g. "4" and "96"
	 * in the example above), or ignore it (e.g. "20" and "4:00PM").
	 * So the first 3 fields are required (could be 0), the rest is optional.
	 * 
	 * Currently the only period types supported are "mo/month",
	 * "yr/year", or "none" (the first two fields are both ignored).
	 * If the (Start Date) is ignored, the period starts from the
	 * first day of the month(year).
	 * 
	 * Example of a simpler version: "0/24/mo"
	 * means a customer can only opt out up to 24 hours a month.
	 */
	private void checkOptOutRules(LiteStarsCustAccountInformation liteAcctInfo,
			StarsYukonUser user, StarsProgramOptOut optout) throws WebClientException
	{
		if (optout.getPeriod() == REPEAT_LAST) return;
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		String ruleStr = null;
		if (ECUtils.isOperator( user ))
			ruleStr = AuthFuncs.getRolePropertyValue(user.getYukonUser(), ConsumerInfoRole.OPT_OUT_RULES);
		else
			ruleStr = AuthFuncs.getRolePropertyValue(user.getYukonUser(), ResidentialCustomerRole.OPT_OUT_RULES);
		ruleStr = ServerUtils.forceNotNone( ruleStr );
		
		Date optOutDate = optout.getStartDateTime();
		if (optOutDate == null) optOutDate = new Date();
		
		int timesAllowed = 0;
		int hoursAllowed = 0;
		Calendar periodBegin = null;
		Calendar periodEnd = null;
		String periodStr = null;
		Date dueTime = null;
		String durTimeStr = null;
		
		if (ruleStr.length() > 0) {
			String[] rules = ruleStr.split(",");
			if (rules.length > 0) {
				String[] fields = rules[0].split("/");
				if (fields.length < 3)
					throw new WebClientException("Wrong format in the opt out rules '" + rules[0] + "'");
				
				timesAllowed = Integer.parseInt( fields[0] );
				hoursAllowed = Integer.parseInt( fields[1] );
				String period = fields[2];
				int initDay = (fields.length > 3)? Integer.parseInt(fields[3]) : 1;
				
				periodBegin = Calendar.getInstance();
				periodBegin.setTime( optOutDate );
				periodEnd = Calendar.getInstance();
				
				if (period.equalsIgnoreCase("mo") || period.equalsIgnoreCase("month")) {
					periodBegin.set(Calendar.DAY_OF_MONTH, initDay);
					if (periodBegin.getTime().after( optOutDate ))
						periodBegin.add(Calendar.MONTH, -1);
					
					periodEnd.setTime( periodBegin.getTime() );
					periodEnd.add(Calendar.MONTH, 1);
					
					periodStr = "month";
				}
				else if (period.equalsIgnoreCase("yr") || period.equalsIgnoreCase("year")) {
					periodBegin.set(Calendar.DAY_OF_YEAR, initDay);
					if (periodBegin.getTime().after( optOutDate ))
						periodBegin.add(Calendar.YEAR, -1);
					
					periodEnd.setTime( periodBegin.getTime() );
					periodEnd.add(Calendar.YEAR, 1);
					
					periodStr = "year";
				}
				else if (period.equalsIgnoreCase("none")) {
					periodBegin = periodEnd = null;
				}
				else
					throw new WebClientException("Wrong format in the opt out rules '" + rules[0] + "'");
			}
			
			if (rules.length > 1) {
				dueTime = ServletUtils.parseTime( rules[1], energyCompany.getDefaultTimeZone() );
				if (dueTime == null)
					throw new WebClientException("Wrong format in the opt out rules '" + rules[1] + "'");
				
				durTimeStr = rules[1];
			}
		}
		
		if (periodBegin != null) {
			StarsLMProgramHistory progHist = null;
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
			if (starsAcctInfo != null)
				progHist = starsAcctInfo.getStarsLMPrograms().getStarsLMProgramHistory();
			else
				progHist = StarsLiteFactory.createStarsLMProgramHistory( liteAcctInfo, energyCompany );
			
			if (timesAllowed > 0) {
				// Check for time limitation
				int timesOptedOut = 0;
				
				for (int i = progHist.getStarsLMProgramEventCount() - 1; i >= 0; i--) {
					StarsLMProgramEvent event = progHist.getStarsLMProgramEvent(i);
					if (event.getEventDateTime().before( periodBegin.getTime() ))
						break;
					
					if (event.hasDuration()) timesOptedOut++;
					if (timesOptedOut >= timesAllowed) {
						String timeUnit = (timesAllowed > 1)? "times" : "time";
						String errorMsg = energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " constraint violated!" +
								" You can only " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_VERB) +
								" the programs " + timesAllowed + " " + timeUnit + " each " + periodStr +
								", but you have already " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_PAST) +
								" " + timesOptedOut + " " + timeUnit + " this " + periodStr + ".";
						throw new WebClientException( errorMsg );
					}
				}
			}
			
			if (hoursAllowed > 0) {
				// Check for hour limitation
				Date lastEventDate = null;
				int hoursLastDuration =0;
				int hoursOptedOut = 0;
				
				for (int i = progHist.getStarsLMProgramEventCount() - 1; i >= 0; i--) {
					StarsLMProgramEvent event = progHist.getStarsLMProgramEvent(i);
					
					int hoursInPeriod = 0;
					if (event.getEventDateTime().before( periodBegin.getTime() )) {
						if (!event.hasDuration()) break;
						
						// The event could be partially in this period
						hoursInPeriod = (int)(event.getDuration() - (periodBegin.getTimeInMillis() - event.getEventDateTime().getTime()) * 0.001 / 3600 + 0.5);
						if (hoursInPeriod <= 0) break;
					}
					else {
						if (!event.hasDuration()) continue;
						
						hoursInPeriod = event.getDuration();
					}
					
					if (lastEventDate != null && lastEventDate.equals( event.getEventDateTime() )) {
						if (hoursInPeriod > hoursLastDuration) {
							hoursOptedOut += hoursInPeriod - hoursLastDuration;
							hoursLastDuration = hoursInPeriod;
						}
					}
					else {
						hoursOptedOut += hoursInPeriod;
						lastEventDate = event.getEventDateTime();
						hoursLastDuration = hoursInPeriod;
					}
				}
				
				int hoursInPeriod = 0;
				if (optout.getPeriod() == OPTOUT_TODAY) {
					TimeZone tz = energyCompany.getDefaultTimeZone();
					hoursInPeriod = (int)((ServletUtil.getTomorrow(tz).getTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
				}
				else {
					hoursInPeriod = (int) ((periodEnd.getTime().getTime() - optOutDate.getTime()) * 0.001 / 3600 + 0.5);
					if (hoursInPeriod > optout.getPeriod())
						hoursInPeriod = optout.getPeriod();
				}
				
				if (hoursOptedOut + hoursInPeriod > hoursAllowed) {
					String hourUnit = (hoursAllowed > 1)? "hours" : "hour";
					String errorMsg = energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " constraint violated!" +
							" You can only " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_VERB) +
							" the programs " + hoursAllowed + " " + hourUnit + " each " + periodStr +
							", but you have already " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_PAST) +
							" " + hoursOptedOut + " " + hourUnit + " this " + periodStr + ".";
					throw new WebClientException( errorMsg );
				}
			}
		}
		
		if (dueTime != null) {
			// Check for due time limitation
			Calendar dueCal = Calendar.getInstance();
			dueCal.setTime( optOutDate );
			Calendar timeCal = Calendar.getInstance();
			timeCal.setTime( dueTime );
			
			dueCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
			dueCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			dueCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
			if (dueCal.getTime().after( optOutDate ))
				dueCal.add(Calendar.DATE, -1);
			
			if (new Date().after( dueCal.getTime() )) {
				String errorMsg = energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " constraint violated!" +
						" You must submit your request by " + durTimeStr + " the day before.";
				throw new WebClientException( errorMsg );
			}
		}
	}
	
	/**
	 * Update the program and hardware history when an opt out event happens
	 */
	private static StarsLMHardwareHistory processOptOut(StarsProgramOptOut optOut, LiteStarsLMHardware liteHw,
		LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws com.cannontech.database.TransactionException
	{
		TimeZone tz = TimeZone.getTimeZone( energyCompany.getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
		
        Integer progEventEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID() );
		Integer hwEventEntryID = new Integer(
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
        Integer tempTermEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION).getEntryID() );
        Integer futureActEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).getEntryID() );
		
		Date optOutDate = optOut.getStartDateTime();
		if (optOutDate == null) optOutDate = new Date();
		
		Date reenableDate = null;
		if (optOut.getPeriod() > 0) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime( optOutDate );
	        cal.add( Calendar.HOUR_OF_DAY, optOut.getPeriod() );
	        reenableDate = cal.getTime();
		}
		else if (optOut.getPeriod() == OPTOUT_TODAY) {
			reenableDate = ServletUtil.getTomorrow(tz);
		}
		
		removeFutureActivationEvents( liteHw, liteAcctInfo, energyCompany );
		
		// Add "Temp Opt Out" and "Future Activation" to hardware events
		com.cannontech.database.data.multi.MultiDBPersistent multiDB =
				new com.cannontech.database.data.multi.MultiDBPersistent();
		
		com.cannontech.database.data.stars.event.LMHardwareEvent hwEvent =
				new com.cannontech.database.data.stars.event.LMHardwareEvent();
		hwEvent.getLMHardwareEvent().setInventoryID( new Integer(liteHw.getInventoryID()) );
		hwEvent.getLMCustomerEventBase().setEventTypeID( hwEventEntryID );
		hwEvent.getLMCustomerEventBase().setActionID( tempTermEntryID );
		hwEvent.getLMCustomerEventBase().setEventDateTime( optOutDate );
		hwEvent.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		multiDB.getDBPersistentVector().addElement( hwEvent );
		
		hwEvent = new com.cannontech.database.data.stars.event.LMHardwareEvent();
		hwEvent.getLMHardwareEvent().setInventoryID( new Integer(liteHw.getInventoryID()) );
		hwEvent.getLMCustomerEventBase().setEventTypeID( hwEventEntryID );
		hwEvent.getLMCustomerEventBase().setActionID( futureActEntryID );
		hwEvent.getLMCustomerEventBase().setEventDateTime( reenableDate );
		hwEvent.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		multiDB.getDBPersistentVector().addElement( hwEvent );
		
		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
		
		// Update lite objects and create response
		for (int j = 0; j < multiDB.getDBPersistentVector().size(); j++) {
			hwEvent = (com.cannontech.database.data.stars.event.LMHardwareEvent) multiDB.getDBPersistentVector().get(j);
			LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) StarsLiteFactory.createLite( hwEvent );
			liteHw.getInventoryHistory().add( liteEvent );
		}
		liteHw.updateDeviceStatus();
		
		StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
		hwHist.setInventoryID( liteHw.getInventoryID() );
		
		for (int j = 0; j < liteHw.getInventoryHistory().size(); j++) {
			LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) liteHw.getInventoryHistory().get(j);
			StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
			StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
			hwHist.addStarsLMHardwareEvent( starsEvent );
		}
		
		// Add "Temp Opt Out" and "Future Activation" to program events
		for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
			if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getProgramID() > 0) {
				LiteStarsLMProgram liteProg = ProgramSignUpAction.getLMProgram( liteAcctInfo, liteApp.getProgramID() );
				
				multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
	            
				com.cannontech.database.data.stars.event.LMProgramEvent event =
						new com.cannontech.database.data.stars.event.LMProgramEvent();
				event.getLMProgramEvent().setProgramID( new Integer(liteApp.getProgramID()) );
				event.getLMProgramEvent().setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
				event.getLMCustomerEventBase().setEventTypeID( progEventEntryID );
				event.getLMCustomerEventBase().setActionID( tempTermEntryID );
				event.getLMCustomerEventBase().setEventDateTime( optOutDate );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				multiDB.getDBPersistentVector().addElement( event );
	            
				event = new com.cannontech.database.data.stars.event.LMProgramEvent();
				event.getLMProgramEvent().setProgramID( new Integer(liteApp.getProgramID()) );
				event.getLMProgramEvent().setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
				event.getLMCustomerEventBase().setEventTypeID( progEventEntryID );
				event.getLMCustomerEventBase().setActionID( futureActEntryID );
				event.getLMCustomerEventBase().setEventDateTime( reenableDate );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				multiDB.getDBPersistentVector().addElement( event );
	            
				multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
						Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
				
				// Update lite objects and create response
				for (int j = 0; j < multiDB.getDBPersistentVector().size(); j++) {
					event = (com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(j);
					LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite( event );
					liteAcctInfo.getProgramHistory().add( liteEvent );
				}
				liteProg.updateProgramStatus( liteAcctInfo.getProgramHistory() );
			}
        }
        
        return hwHist;
	}
	
	private static void parseResponse(StarsProgramOptOutResponse resp,
		StarsCustAccountInformation starsAcctInfo, LiteStarsEnergyCompany energyCompany)
	{
		StarsLMPrograms programs = starsAcctInfo.getStarsLMPrograms();
		if (resp.getStarsLMProgramHistory() != null)
			programs.setStarsLMProgramHistory( resp.getStarsLMProgramHistory() );
        
        StarsInventories inventories = starsAcctInfo.getStarsInventories();
		DeviceStatus hwStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL),
				DeviceStatus.class );
		
		for (int i = 0; i < resp.getStarsLMHardwareHistoryCount(); i++) {
			StarsLMHardwareHistory hwHist = resp.getStarsLMHardwareHistory(i);
			
			// Update the hardware status
			for (int j = 0; j < inventories.getStarsInventoryCount(); j++) {
				StarsInventory inv = inventories.getStarsInventory(j);
				if (inv.getInventoryID() == hwHist.getInventoryID()) {
					inv.setStarsLMHardwareHistory( hwHist );
					inv.setDeviceStatus( hwStatus );
					break;
				}
			}
			
			// Update the corresponding program status
			for (int j = 0; j < starsAcctInfo.getStarsAppliances().getStarsApplianceCount(); j++) {
				StarsAppliance app = starsAcctInfo.getStarsAppliances().getStarsAppliance(j);
				if (app.getInventoryID() == hwHist.getInventoryID() && app.getProgramID() > 0) {
					for (int k = 0; k < programs.getStarsLMProgramCount(); k++) {
						if (programs.getStarsLMProgram(k).getProgramID() == app.getProgramID()) {
							programs.getStarsLMProgram(k).setStatus( ServletUtils.OUT_OF_SERVICE );
							break;
						}
					}
				}
			}
		}
	}
	
	public static ArrayList getAffectedHardwares(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany) {
		ArrayList hardwares = new ArrayList();
		
		for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
			if (liteApp.getProgramID() > 0 && liteApp.getInventoryID() > 0) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( liteApp.getInventoryID(), true );
				if (!hardwares.contains( liteHw )) hardwares.add( liteHw );
			}
		}
		
		return hardwares;
	}
	
	public static String getOptOutCommand(LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany, int offHours)
		throws WebClientException
	{
		if (liteHw.getManufacturerSerialNumber().trim().length() == 0)
			throw new WebClientException( "The serial # of the hardware cannot be empty" );
        
		String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() +
					" service out temp offhours " + String.valueOf(offHours);
		return cmd;
	}
	
	public static void handleOptOutEvent(OptOutEventQueue.OptOutEvent event, LiteStarsEnergyCompany energyCompany) {
		LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( event.getAccountID(), true );
		LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( event.getInventoryID(), true );
		
		StarsProgramOptOut optOut = new StarsProgramOptOut();
		optOut.setStartDateTime( new Date(event.getStartDateTime()) );
		optOut.setPeriod( event.getPeriod() );
		
		StarsProgramOptOutResponse resp = new StarsProgramOptOutResponse();
		
		try {
			resp.addStarsLMHardwareHistory( processOptOut(optOut, liteHw, liteAcctInfo, energyCompany) );
			resp.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
		}
		catch (com.cannontech.database.TransactionException e) {
			CTILogger.error( e.getMessage(), e );
			return;
		}
		
		StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
				energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
		if (starsAcctInfo == null) return;
		
		parseResponse( resp, starsAcctInfo, energyCompany );
	}

}

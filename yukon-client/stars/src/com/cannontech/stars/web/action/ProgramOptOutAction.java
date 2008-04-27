package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
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
import com.cannontech.roles.operator.InventoryRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.event.dao.LMHardwareEventDao;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
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
	private static final LMHardwareEventDao hardwareEventDao = YukonSpringHook.getBean("hardwareEventDao", LMHardwareEventDao.class);
	public static final int REPEAT_LAST = -1;

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			TimeZone tz = energyCompany.getDefaultTimeZone();
            
			StarsProgramOptOut optOut = new StarsProgramOptOut();
			
			String[] invIDs = req.getParameterValues("InvID");
			if (invIDs != null) {
				for (int i = 0; i < invIDs.length; i++)
					optOut.addInventoryID( Integer.parseInt(invIDs[i]) );
			}
			
			if (req.getParameter("action").equalsIgnoreCase("RepeatLastOptOut")) {
				optOut.setPeriod( REPEAT_LAST );
			}
			else {
				Date today = ServletUtil.getToday( tz );
				Date startDate = ServletUtil.parseDateStringLiberally( req.getParameter("StartDate"), tz );
				int duration = Integer.parseInt( req.getParameter("Duration") );
				
				if (startDate == null)
					throw new WebClientException( "Invalid start date '" + req.getParameter("StartDate") + "'" );
				if (today.getTime() - startDate.getTime() > 1000)
					throw new WebClientException( "The start date cannot be earlier than today" );
				
				if (Math.abs(startDate.getTime() - today.getTime()) < 1000) {
					// Start date is today
					Calendar cal = Calendar.getInstance();
					cal.setTime( today );
					cal.add( Calendar.HOUR, duration );
					optOut.setPeriod( (int)((cal.getTimeInMillis() - System.currentTimeMillis()) * 0.001 / 3600 + 0.5) );
				}
				else {
					optOut.setStartDateTime( startDate );
					optOut.setPeriod( duration );
				}
			}

            StarsOperation operation = new StarsOperation();
            operation.setStarsProgramOptOut( optOut );
            
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (WebClientException e) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
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
            
            LiteYukonUser theUsefulUser = (LiteYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
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
			
        	energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        	TimeZone tz = energyCompany.getDefaultTimeZone();
			
            StarsProgramOptOut optOut = reqOper.getStarsProgramOptOut();
            StarsProgramOptOutResponse resp = new StarsProgramOptOutResponse();
			String logMsg = null;
			
			try {
				checkOptOutRules( liteAcctInfo, user, optOut );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
            // Get all the hardware to be opted out
            ArrayList hardware = null;
            if (optOut.getInventoryIDCount() > 0) {
            	hardware = new ArrayList();
            	for (int i = 0; i < optOut.getInventoryIDCount(); i++)
					hardware.add( energyCompany.getInventory(optOut.getInventoryID(i), true) );
            }
            else {
				hardware = getAffectedHardwares( liteAcctInfo, energyCompany );
				if (hardware.size() == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "There is no hardware assigned to any program.") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
            }
            
            if (optOut.getStartDateTime() != null) {
            	// Schedule the opt out event
            	int[] invIDs = optOut.getInventoryID();
            	if (invIDs == null || invIDs.length == 0)
            		invIDs = new int[] {0};
            	
            	for (int i = 0; i < invIDs.length; i++) {
					OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
					event.setEnergyCompanyID( energyCompany.getLiteID() );
					event.setStartDateTime( optOut.getStartDateTime().getTime() );
					event.setPeriod( optOut.getPeriod() );
					event.setAccountID( liteAcctInfo.getAccountID() );
					event.setInventoryID( invIDs[i] );
					OptOutEventQueue.getInstance().addEvent( event );
            	}
				
            	resp.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
            	resp.setDescription( "The " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event has been scheduled." );
            	
				logMsg = "Start Date/Time:" + StarsUtils.formatDate(optOut.getStartDateTime(), tz) +
            			", Duration:" + ServletUtils.getDurationFromHours( optOut.getPeriod() );
				logMsg += ", Serial #:" + ((LiteStarsLMHardware) hardware.get(0)).getManufacturerSerialNumber();
				for (int i = 1; i < hardware.size(); i++)
					logMsg += "," + ((LiteStarsLMHardware) hardware.get(i)).getManufacturerSerialNumber();
            }
			else if (optOut.getPeriod() == REPEAT_LAST) {
				/* We will only resend the command if there is a reenable event in the queue
				 * (the opt out event is still active)
				 */
				OptOutEventQueue.OptOutEvent[] reenableEvents = OptOutEventQueue.getInstance().findReenableEvents( liteAcctInfo.getAccountID() );
				
				if (reenableEvents == null || reenableEvents.length == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "There is no active " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event.") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				for (int i = 0; i < reenableEvents.length; i++) {
					int offHours = (int) ((reenableEvents[i].getStartDateTime() - System.currentTimeMillis()) * 0.001 / 3600 + 0.5);
					
					LiteStarsLMHardware[] hw = null;
					if (reenableEvents[i].getInventoryID() > 0) {
						hw = new LiteStarsLMHardware[0];
						hw[0] = (LiteStarsLMHardware) energyCompany.getInventory(reenableEvents[i].getInventoryID(), true);
					}
					else {
						hw = new LiteStarsLMHardware[ hardware.size() ];
						hardware.toArray( hw );
					}
					
					for (int j = 0; j < hw.length; j++) {
						String cmd = getOptOutCommand( hw[j], energyCompany, offHours );
						int routeID = hw[j].getRouteID();
						if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
						ServerUtils.sendSerialCommand( cmd, routeID, user.getYukonUser() );
					}
					
					resp.setDescription( "The last " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " command has been resent." );
				}
			}
            else {
            	// Send opt out command immediately, and schedule the reenable event
				Calendar reenableTime = Calendar.getInstance();
				reenableTime.add( Calendar.HOUR_OF_DAY, optOut.getPeriod() );
				
				for (int i = 0; i < hardware.size(); i++) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardware.get(i);
					
					String cmd = getOptOutCommand( liteHw, energyCompany, optOut.getPeriod() );
					int routeID = liteHw.getRouteID();
					if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
					ServerUtils.sendSerialCommand( cmd, routeID, user.getYukonUser() );
					
					StarsLMHardwareHistory hwHist = processOptOut( optOut, liteHw, liteAcctInfo, energyCompany, theUsefulUser );
					resp.addStarsLMHardwareHistory( hwHist );
				}
	            
	            int[] invIDs = optOut.getInventoryID();
	            if (invIDs == null || invIDs.length == 0)
	            	invIDs = new int[] {0};
				
				for (int i = 0; i < invIDs.length; i++) {
					OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
					event.setEnergyCompanyID( energyCompany.getLiteID() );
					event.setStartDateTime( reenableTime.getTimeInMillis() );
					event.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
					event.setAccountID( liteAcctInfo.getAccountID() );
					event.setInventoryID( invIDs[i] );
					OptOutEventQueue.getInstance().addEvent( event );
				}
            	
            	StarsLMProgramHistory progHist = StarsLiteFactory.createStarsLMProgramHistory( liteAcctInfo, energyCompany );
            	resp.setStarsLMProgramHistory( progHist );
            	
		        if (StarsUtils.isOperator( user.getYukonUser() ))
			        resp.setDescription( ServletUtil.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " command has been sent out successfully." );
			    else
			        resp.setDescription( "Your programs have been " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_PAST) + "." );
			    
				logMsg = "Start Date/Time:(Immediately), Duration:" + ServletUtils.getDurationFromHours( optOut.getPeriod() );
				logMsg += ", Serial #:" + ((LiteStarsLMHardware) hardware.get(0)).getManufacturerSerialNumber();
				for (int i = 1; i < hardware.size(); i++)
					logMsg += "," + ((LiteStarsLMHardware) hardware.get(i)).getManufacturerSerialNumber();
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
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_VERB) + " the programs.") );
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

			StarsProgramOptOutResponse resp = operation.getStarsProgramOptOutResponse();
            if (resp == null) {
				StarsFailure failure = operation.getStarsFailure();
				if (failure != null) {
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
					return failure.getStatusCode();
				}
				else
					return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            }
			
			if (resp.getDescription() != null)
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
            
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
            
            parseResponse( resp, accountInfo, energyCompany );
            
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	static void removeProgramFutureActivationEvents(List<LiteLMProgramEvent> progHist, int programID, LiteStarsEnergyCompany energyCompany) {
		int futureActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ).getEntryID();
		int termID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION ).getEntryID();
		
		try {
			for (int i = progHist.size() - 1; i >= 0; i--) {
				LiteLMProgramEvent liteEvent = progHist.get(i);
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
		    List<LiteLMHardwareEvent> list = hardwareEventDao.getByInventoryId(liteHw.getLiteID());
			Iterator<LiteLMHardwareEvent> it = list.iterator();
			while (it.hasNext()) {
				LiteLMHardwareEvent liteEvent = it.next();
				if (liteEvent.getActionID() == futureActID) {
					com.cannontech.database.data.stars.event.LMHardwareEvent event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
							StarsLiteFactory.createDBPersistent( liteEvent );
					Transaction.createTransaction( Transaction.DELETE, event ).execute();
					it.remove();
				}
			}
			
			for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
				LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(i);
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
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		String ruleStr = null;
		if (StarsUtils.isOperator( user.getYukonUser() ))
			ruleStr = DaoFactory.getAuthDao().getRolePropertyValue(user.getYukonUser(), ConsumerInfoRole.OPT_OUT_RULES);
		else
			ruleStr = DaoFactory.getAuthDao().getRolePropertyValue(user.getYukonUser(), ResidentialCustomerRole.OPT_OUT_RULES);
		ruleStr = StarsUtils.forceNotNone( ruleStr );
		
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
				
				int hoursInPeriod = (int) ((periodEnd.getTime().getTime() - optOutDate.getTime()) * 0.001 / 3600 + 0.5);
				if (hoursInPeriod > optout.getPeriod())
					hoursInPeriod = optout.getPeriod();
				
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
	 * @param currentUser TODO
	 */
	private static StarsLMHardwareHistory processOptOut(StarsProgramOptOut optOut, LiteStarsLMHardware liteHw,
		LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, LiteYukonUser currentUser)
		throws com.cannontech.database.TransactionException
	{
		if (optOut.getPeriod() <= 0) return null;
		
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
		
        Calendar cal = Calendar.getInstance();
        cal.setTime( optOutDate );
        cal.add( Calendar.HOUR_OF_DAY, optOut.getPeriod() );
        Date reenableDate = cal.getTime();
		
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
		
		liteHw.updateDeviceStatus();
		
		StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
		hwHist.setInventoryID( liteHw.getInventoryID() );
		
		List<LiteLMHardwareEvent> list = hardwareEventDao.getByInventoryId(liteHw.getLiteID());
		for (int j = 0; j < list.size(); j++) {
			LiteLMHardwareEvent liteEvent = list.get(j);
			StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
			StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
			hwHist.addStarsLMHardwareEvent( starsEvent );
		}
		
		// Add "Temp Opt Out" and "Future Activation" to program events
		for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
			LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(i);
			if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getProgramID() > 0) {
				LiteStarsLMProgram liteProg = ProgramSignUpAction.getLMProgram( liteAcctInfo, liteApp.getProgramID() );
				
                /*
                 * New enrollment, opt out, and control history tracking
                 *-------------------------------------------------------------------------------
                 */
                LMHardwareControlInformationService lmHardwareControlInformationService = (LMHardwareControlInformationService) YukonSpringHook.getBean("lmHardwareControlInformationService");
                lmHardwareControlInformationService.startOptOut(liteHw.getInventoryID(), liteApp.getAddressingGroupID(), liteHw.getAccountID(), currentUser);
                /*-------------------------------------------------------------------------------
                 * */
                
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
        
		String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber();
		int hwConfigType = InventoryUtils.getHardwareConfigType( liteHw.getLmHardwareTypeID() );
		if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
			cmd += " vcom service out temp offhours " + String.valueOf(offHours);
		}
		else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
			cmd += " xcom service out temp offhours " + String.valueOf(offHours);
			//if true, the opt out also includes a restore command so the switch gets both at once
			String restoreFirst = energyCompany.getEnergyCompanySetting( InventoryRole.EXPRESSCOM_TOOS_RESTORE_FIRST );
			if (restoreFirst != null && Boolean.valueOf(restoreFirst).booleanValue())
				cmd += " control restore load 0";
		}
		else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205)
		{
			cmd += " sa205 service out temp offhours " + String.valueOf(offHours);
		}
		else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305)
		{
			String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
			if (trackHwAddr == null || !Boolean.valueOf(trackHwAddr).booleanValue())
				throw new WebClientException("The utility ID of the SA305 switch is unknown");
			
			cmd += " sa305 utility " + liteHw.getLMConfiguration().getSA305().getUtility() + " override " + String.valueOf( offHours );
		}
		
		return cmd;
	}
	
	public static void handleOptOutEvent(OptOutEventQueue.OptOutEvent event, LiteStarsEnergyCompany energyCompany, LiteYukonUser currentUser) {
		LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( event.getAccountID(), true );
		
		ArrayList hardwares = new ArrayList();
		if (event.getInventoryID() != 0)
			hardwares.add( energyCompany.getInventory(event.getInventoryID(), true) );
		else
			hardwares = getAffectedHardwares( liteAcctInfo, energyCompany );
		
		StarsProgramOptOut optOut = new StarsProgramOptOut();
		optOut.setStartDateTime( new Date(event.getStartDateTime()) );
		optOut.setPeriod( event.getPeriod() );
		
		StarsProgramOptOutResponse resp = new StarsProgramOptOutResponse();
		
		for (int i = 0; i < hardwares.size(); i++) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
			try {
				resp.addStarsLMHardwareHistory( processOptOut(optOut, liteHw, liteAcctInfo, energyCompany, currentUser) );
			}
			catch (com.cannontech.database.TransactionException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		
		resp.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
		
		StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
				energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
		if (starsAcctInfo != null)
			parseResponse( resp, starsAcctInfo, energyCompany );
	}

}

package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
import com.cannontech.stars.xml.serialize.StarsProgramReenable;
import com.cannontech.stars.xml.serialize.StarsSendOptOutNotification;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.util.ServletUtil;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SendOptOutNotificationAction implements ActionBase {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
            
			StarsSendOptOutNotification sendNotif = new StarsSendOptOutNotification();
			
			if (req.getParameter("action").equalsIgnoreCase("SendOptOutNotification")) {
				StarsEnergyCompanySettings ecSettings = (StarsEnergyCompanySettings)
						session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
				StarsExitInterviewQuestions questions = ecSettings.getStarsExitInterviewQuestions();
	            
				if (questions != null && questions.getStarsExitInterviewQuestionCount() > 0) {
					String[] qIDStrs = req.getParameterValues( "QID" );
					String[] answers = req.getParameterValues( "Answer" );
	            	
					for (int i = 0; i < qIDStrs.length; i++) {
						StarsExitInterviewQuestion answer = new StarsExitInterviewQuestion();
						answer.setQuestionID( Integer.parseInt(qIDStrs[i]) );
						answer.setAnswer( answers[i] );
						sendNotif.addStarsExitInterviewQuestion( answer );
						int qID = Integer.parseInt( qIDStrs[i] );
					}
				}
			}
            
			StarsOperation operation = new StarsOperation();
			operation.setStarsSendOptOutNotification( sendNotif );
			
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
            
			LiteStarsCustAccountInformation  liteAcctInfo =
					(LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
            
			String notifRecip = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
			if (notifRecip != null && notifRecip.trim().length() > 0) {
				String notifMsg = getOptOutNotifMessage( energyCompany, liteAcctInfo, reqOper );
				if (notifMsg != null) sendNotification( notifMsg, energyCompany );
			}
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( ServletUtil.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " notification has been sent successfully." );
            
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot send out " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " notification.") );
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

			StarsSuccess success = operation.getStarsSuccess();
			if (success == null) {
				StarsFailure failure = operation.getStarsFailure();
				if (failure != null) {
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
					return failure.getStatusCode();
				}
				else
					return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
            
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private static String getAccountInformation(LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo) {
		StringBuffer text = new StringBuffer();
		text.append("Account #").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append(LINE_SEPARATOR);
        
		LiteContact cont = ContactFuncs.getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		String name = ECUtils.formatName( cont );
		if (name.length() > 0)
			text.append( name ).append(LINE_SEPARATOR);
        
		LiteAddress addr = energyCompany.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
		if (addr.getLocationAddress1().trim().length() > 0) {
			text.append(addr.getLocationAddress1());
			if (addr.getLocationAddress2().trim().length() > 0)
				text.append(", ").append(addr.getLocationAddress2());
			text.append(LINE_SEPARATOR);
			if (addr.getCityName().trim().length() > 0)
				text.append(addr.getCityName()).append(", ");
			if (addr.getStateCode().trim().length() > 0)
				text.append(addr.getStateCode()).append(" ");
			if (addr.getZipCode().trim().length() > 0)
				text.append(addr.getZipCode());
			text.append(LINE_SEPARATOR);
		}
		
		String homePhone = ECUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE) );
		String workPhone = ECUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE) );
		String email = ECUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
        
		if (homePhone.length() > 0)
			text.append(homePhone).append(LINE_SEPARATOR);
		else if (workPhone.length() > 0)
			text.append(workPhone).append(LINE_SEPARATOR);
		else if (email.length() > 0)
			text.append(email).append(LINE_SEPARATOR);
        
		return text.toString();
	}
	
	private static String getProgramInformation(LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo, ArrayList hardwares) {
		StringBuffer text = new StringBuffer();
		
		for (int i = 0; i < hardwares.size(); i++) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
			text.append("Serial #: ").append(liteHw.getManufacturerSerialNumber()).append(LINE_SEPARATOR);
			
			boolean hasAssignedProg = false;
			for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
				if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getProgramID() > 0) {
					LiteStarsLMProgram liteProg = ProgramSignUpAction.getLMProgram( liteAcctInfo, liteApp.getProgramID() );
					
					String progName = ECUtils.getPublishedProgramName( liteProg.getPublishedProgram() );
					text.append("    Program: ").append( progName );
					
					String groupName = "(none)";
					if (liteApp.getAddressingGroupID() > 0)
						groupName = PAOFuncs.getYukonPAOName( liteApp.getAddressingGroupID() );
					text.append(", Group: ").append(groupName).append(LINE_SEPARATOR);
					
					hasAssignedProg = true;
				}
			}
			
			if (!hasAssignedProg)
				text.append("    (No Assigned Program)").append(LINE_SEPARATOR);
		}
        
		return text.toString();
	}
	
	private static LiteLMHardwareEvent findLastOptOutEvent(LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany) {
		int tempTermID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION ).getEntryID();
		
		try {
			for (int i = liteHw.getInventoryHistory().size() - 1; i >= 0; i--) {
				LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) liteHw.getInventoryHistory().get(i);
				if (liteEvent.getActionID() == tempTermID)
					return liteEvent;
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public static String getOptOutNotifMessage(
			LiteStarsEnergyCompany energyCompany,
			LiteStarsCustAccountInformation liteAcctInfo,
			StarsOperation operation)
	{
		StarsProgramOptOut optout = operation.getStarsProgramOptOut();
		if (optout.getPeriod() <= 0) return null;
		
		String optOutTxt = energyCompany.getEnergyCompanySetting( ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN );
		String reenableTxt = energyCompany.getEnergyCompanySetting( ConsumerInfoRole.WEB_TEXT_REENABLE );
		
		Date optOutDate = optout.getStartDateTime();
		if (optOutDate == null) optOutDate = new Date();
        
		Calendar cal = Calendar.getInstance();
		cal.setTime( optOutDate );
		cal.add( Calendar.HOUR_OF_DAY, optout.getPeriod() );
		Date reenableDate = cal.getTime();
        
		StringBuffer text = new StringBuffer();
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append( getAccountInformation(energyCompany, liteAcctInfo) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append(optOutTxt.toUpperCase()).append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		
		ArrayList hardwares = null;
		if (optout.getInventoryIDCount() > 0) {
			hardwares = new ArrayList();
			for (int i = 0; i < optout.getInventoryIDCount(); i++)
				hardwares.add( energyCompany.getInventory(optout.getInventoryID(i), true) );
		}
		else
			hardwares = ProgramOptOutAction.getAffectedHardwares( liteAcctInfo, energyCompany );
        
		text.append(ServletUtil.capitalizeAll( optOutTxt )).append(" Time: ")
			.append(ServerUtils.formatDate( optOutDate, energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
		text.append(ServletUtil.capitalize( reenableTxt )).append(" Time: ")
			.append(ServerUtils.formatDate( reenableDate, energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append( getProgramInformation(energyCompany, liteAcctInfo, hardwares) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		
		int exitQType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT).getEntryID();
		LiteInterviewQuestion[] liteQuestions = energyCompany.getInterviewQuestions( exitQType );
        
		StarsSendOptOutNotification sendNotif = operation.getStarsSendOptOutNotification();
		for (int i = 0; i < sendNotif.getStarsExitInterviewQuestionCount(); i++) {
			StarsExitInterviewQuestion answer = sendNotif.getStarsExitInterviewQuestion(i);
			
			for (int j = 0; j < liteQuestions.length; j++) {
				if (liteQuestions[j].getQuestionID() == answer.getQuestionID()) {
					text.append("Q: ").append(liteQuestions[j].getQuestion()).append(LINE_SEPARATOR);
					text.append("A: ").append(answer.getAnswer()).append(LINE_SEPARATOR);
					text.append(LINE_SEPARATOR);
					break;
				}
			}
		}
		
		return text.toString();
	}
	
	public static String getReenableNotifMessage(
			LiteStarsEnergyCompany energyCompany,
			LiteStarsCustAccountInformation liteAcctInfo,
			StarsOperation operation)
	{
		StarsProgramReenable reenable = operation.getStarsProgramReenable();
		Date now = new Date();
		
		String optOutTxt = energyCompany.getEnergyCompanySetting( ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN );
		String reenableTxt = energyCompany.getEnergyCompanySetting( ConsumerInfoRole.WEB_TEXT_REENABLE );
		
		StringBuffer text = new StringBuffer();
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append( getAccountInformation(energyCompany, liteAcctInfo) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		
		if (reenable.getCancelScheduledOptOut()) {
			OptOutEventQueue.OptOutEvent[] events = OptOutEventQueue.getInstance().findOptOutEvents( liteAcctInfo.getAccountID() );
			if (events.length == 0) return null;
			
			text.append("SCHEDULED ").append(optOutTxt.toUpperCase()).append(" CANCELED").append(LINE_SEPARATOR);
			
			for (int i = 0; i < events.length; i++) {
				Calendar reenableTime = Calendar.getInstance();
				reenableTime.setTime( new Date(events[i].getStartDateTime()) );
				reenableTime.add( Calendar.HOUR_OF_DAY, events[i].getPeriod() );
				
				text.append(LINE_SEPARATOR);
				text.append("Scheduled ").append(ServletUtil.capitalizeAll( optOutTxt )).append(" Time: ")
					.append(ServerUtils.formatDate( new Date(events[i].getStartDateTime()), energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
				text.append("Scheduled ").append(ServletUtil.capitalizeAll( reenableTxt )).append(" Time: ")
					.append(ServerUtils.formatDate( reenableTime.getTime(), energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
			}
		}
		else {
			text.append(reenableTxt.toUpperCase()).append(LINE_SEPARATOR);
			text.append(LINE_SEPARATOR);
			
			StarsLMProgramHistory progHist = StarsLiteFactory.createStarsLMProgramHistory( liteAcctInfo, energyCompany );
			boolean foundLastOptOut = false;
			
			for (int i = progHist.getStarsLMProgramEventCount() - 1; i >= 0; i--) {
				StarsLMProgramEvent event = progHist.getStarsLMProgramEvent(i);
				if (event.hasDuration() && event.getEventDateTime().before( now )) {
					Calendar reenableTime = Calendar.getInstance();
					reenableTime.setTime( event.getEventDateTime() );
					reenableTime.add( Calendar.HOUR_OF_DAY, event.getDuration() );
					
					text.append("Last ").append(ServletUtil.capitalizeAll( optOutTxt )).append(" Time: ")
						.append(ServerUtils.formatDate( event.getEventDateTime(), energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
					text.append("Scheduled ").append(ServletUtil.capitalizeAll( reenableTxt )).append(" Time: ")
						.append(ServerUtils.formatDate( reenableTime.getTime(), energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
					
					foundLastOptOut = true;
					break;
				}
			}
			
			if (!foundLastOptOut)
				text.append("Last ").append(ServletUtil.capitalizeAll( optOutTxt )).append(" Time: (none)").append(LINE_SEPARATOR);
			
			text.append(ServletUtil.capitalize( reenableTxt )).append(" Time: ")
				.append(ServerUtils.formatDate( now, energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
		}
		
		ArrayList hardwares = null;
		if (reenable.hasInventoryID()) {
			hardwares = new ArrayList();
			hardwares.add( energyCompany.getInventory(reenable.getInventoryID(), true) );
		}
		else
			hardwares = ProgramOptOutAction.getAffectedHardwares( liteAcctInfo, energyCompany );
		
		text.append(LINE_SEPARATOR);
		text.append( getProgramInformation(energyCompany, liteAcctInfo, hardwares) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		
		return text.toString();
	}
	
	public static void sendNotification(String notifMessage, LiteStarsEnergyCompany energyCompany) throws Exception {
		String toStr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
		if (toStr == null || toStr.trim().length() == 0)
			throw new Exception( "Property \"optout_notification_recipients\" is not set" );
		
		StringTokenizer st = new StringTokenizer( toStr, "," );
		ArrayList toList = new ArrayList();
		while (st.hasMoreTokens())
			toList.add( st.nextToken() );
		String[] to = new String[ toList.size() ];
		toList.toArray( to );
        
		String subject = ServletUtil.capitalizeAll( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) )
				+ "/" + ServletUtil.capitalizeAll( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) ) + " Notification";
		
		EmailMessage emailMsg = new EmailMessage( to, subject, notifMessage );
		emailMsg.setFrom( energyCompany.getAdminEmailAddress() );
		emailMsg.send();
	}

}

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
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
import com.cannontech.stars.xml.serialize.StarsProgramReenable;
import com.cannontech.stars.xml.serialize.StarsSendExitInterviewAnswers;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.tools.email.EmailMessage;

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
            
			StarsEnergyCompanySettings ecSettings = (StarsEnergyCompanySettings)
					session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsExitInterviewQuestions questions = ecSettings.getStarsExitInterviewQuestions();
			StarsSendExitInterviewAnswers sendAnswers = new StarsSendExitInterviewAnswers();
            
			if (questions != null && questions.getStarsExitInterviewQuestionCount() > 0) {
				String[] qIDStrs = req.getParameterValues( "QID" );
				String[] answers = req.getParameterValues( "Answer" );
            	
				for (int i = 0; i < qIDStrs.length; i++) {
					StarsExitInterviewQuestion answer = new StarsExitInterviewQuestion();
					answer.setQuestionID( Integer.parseInt(qIDStrs[i]) );
					answer.setAnswer( answers[i] );
					sendAnswers.addStarsExitInterviewQuestion( answer );
					int qID = Integer.parseInt( qIDStrs[i] );
				}
			}
            
			StarsOperation operation = new StarsOperation();
			operation.setStarsSendExitInterviewAnswers( sendAnswers );
			
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
            
			int energyCompanyID = user.getEnergyCompanyID();
			energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
			sendOptOutNotification( energyCompany, liteAcctInfo, reqOper );
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " notification has been sent successfully" );
            
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot send out " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " notification") );
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
			
			StarsSuccess success = operation.getStarsSuccess();
			if (success == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
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
        
		LiteContact cont = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
		String name = ServerUtils.formatName( cont );
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
		
		String homePhone = ServerUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE) );
		String workPhone = ServerUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE) );
		String email = ServerUtils.getNotification(
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
			text.append("Serial #: ").append(liteHw.getManufacturerSerialNumber());
			
			boolean hasAssignedProg = false;
			for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
				if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getLmProgramID() > 0) {
					LiteStarsLMProgram liteProg = ProgramSignUpAction.getLMProgram( liteAcctInfo, liteApp.getLmProgramID() );
					
					String progName = ECUtils.getPublishedProgramName( liteProg.getLmProgram(), energyCompany );
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
	
	public static void sendOptOutNotification(
			LiteStarsEnergyCompany energyCompany,
			LiteStarsCustAccountInformation liteAcctInfo,
			StarsOperation operation) throws Exception
	{
		StarsProgramOptOut optout = operation.getStarsProgramOptOut();
		Date optOutDate = optout.getStartDateTime();
		if (optOutDate == null) optOutDate = new Date();
        
		Date reenableDate = null;
		if (optout.getPeriod() == ProgramOptOutAction.REPEAT_LAST)
			return;
		if (optout.getPeriod() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime( optOutDate );
			cal.add( Calendar.HOUR_OF_DAY, optout.getPeriod() );
			reenableDate = cal.getTime();
		}
		else if (optout.getPeriod() == ProgramOptOutAction.OPTOUT_TODAY)
			reenableDate = com.cannontech.util.ServletUtil.getTomorrow( energyCompany.getDefaultTimeZone() );
        
		StringBuffer text = new StringBuffer();
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append( getAccountInformation(energyCompany, liteAcctInfo) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		
		ArrayList hardwares = null;
		if (optout.hasInventoryID()) {
			hardwares = new ArrayList();
			hardwares.add( energyCompany.getInventory(optout.getInventoryID(), true) );
		}
		else
			hardwares = ProgramOptOutAction.getAffectedHardwares( liteAcctInfo, energyCompany );
        
		text.append(ServletUtils.capitalize2( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
			.append(" Time: ").append(ServerUtils.formatDate( optOutDate, energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
		text.append(ServletUtils.capitalize( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) ))
			.append(" Time: ").append(ServerUtils.formatDate( reenableDate, energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append( getProgramInformation(energyCompany, liteAcctInfo, hardwares) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		
		int exitQType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT).getEntryID();
		LiteInterviewQuestion[] liteQuestions = energyCompany.getInterviewQuestions( exitQType );
        
		StarsSendExitInterviewAnswers sendAnswers = operation.getStarsSendExitInterviewAnswers();
		for (int i = 0; i < sendAnswers.getStarsExitInterviewQuestionCount(); i++) {
			StarsExitInterviewQuestion answer = sendAnswers.getStarsExitInterviewQuestion(i);
			LiteInterviewQuestion liteQuestion = null;
			for (int j = 0; j < liteQuestions.length; j++)
				if (liteQuestions[j].getQuestionID() == answer.getQuestionID()) {
					liteQuestion = liteQuestions[j];
					break;
				}
			if (liteQuestion == null)
				throw new Exception("Cannot find exit interview question with id = " + answer.getQuestionID());
    		
			text.append("Q: ").append(liteQuestion.getQuestion()).append(LINE_SEPARATOR);
			text.append("A: ").append(answer.getAnswer()).append(LINE_SEPARATOR);
			text.append(LINE_SEPARATOR);
		}
		
		String toStr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
		if (toStr == null)
			throw new Exception( "Property \"optout_notification_recipients\" not found" );
		
		StringTokenizer st = new StringTokenizer( toStr, "," );
		ArrayList toList = new ArrayList();
		while (st.hasMoreTokens())
			toList.add( st.nextToken() );
		String[] to = new String[ toList.size() ];
		toList.toArray( to );
        
		String from = null;
		if (energyCompany.getPrimaryContactID() > 0) {
			String[] emails = ContactFuncs.getAllEmailAddresses( energyCompany.getPrimaryContactID() );
			if (emails.length > 0)
				from = emails[0];
		}
		if (from == null)
			from = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
        
		String subject = ServletUtils.capitalize2(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " Notification";
		
		EmailMessage emailMsg = new EmailMessage( to, subject, text.toString() );
		emailMsg.setFrom( from );
		emailMsg.send();
	}
	
	public static void sendReenableNotification(
			LiteStarsEnergyCompany energyCompany,
			LiteStarsCustAccountInformation liteAcctInfo,
			StarsOperation operation) throws Exception
	{
		StarsProgramReenable reenable = operation.getStarsProgramReenable();
		
		StringBuffer text = new StringBuffer();
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append( getAccountInformation(energyCompany, liteAcctInfo) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		
		ArrayList hardwares = null;
		if (reenable.hasInventoryID()) {
			hardwares = new ArrayList();
			hardwares.add( energyCompany.getInventory(reenable.getInventoryID(), true) );
		}
		else
			hardwares = ProgramReenableAction.getAffectedHardwares( liteAcctInfo, energyCompany );
        
		OptOutEventQueue queue = energyCompany.getOptOutEventQueue();
		ArrayList events = new ArrayList();
		for (int i = 0; i < hardwares.size(); i++) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
			OptOutEventQueue.OptOutEvent e = queue.findOptOutEvent( liteHw.getInventoryID() );
			if (e != null) events.add(e);
		}
		
		if (events.size() == 1) {
			// e.g. "Scheduled Opt Out Time: 04/25/03	(Canceled)"
			OptOutEventQueue.OptOutEvent e = (OptOutEventQueue.OptOutEvent) events.get(0);
			text.append("Scheduled ").append(ServletUtils.capitalize2( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
				.append(" Time: ").append(ServerUtils.formatDate( new Date(e.getStartDateTime()), energyCompany.getDefaultTimeZone() ))
				.append("\t(Canceled)").append(LINE_SEPARATOR);
		}
		else if (events.size() > 1){
			text.append(events.size()).append(" Scheduled ").append(ServletUtils.capitalize2( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
				.append(" Events Canceled").append(LINE_SEPARATOR);
		}
		
		boolean foundLastOptOutEvent = false;
		if (hardwares.size() > 0) {
			LiteLMHardwareEvent event = findLastOptOutEvent( (LiteStarsLMHardware)hardwares.get(0), energyCompany );
			
			if (event != null) {
				text.append("Last ").append(ServletUtils.capitalize2(energyCompany.getEnergyCompanySetting( ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
					.append(" Time: ").append(ServerUtils.formatDate( new Date(event.getEventDateTime()), energyCompany.getDefaultTimeZone() ))
					.append(LINE_SEPARATOR);
				foundLastOptOutEvent = true;
			}
		}
		
		if (!foundLastOptOutEvent) {
			text.append("Last ").append(ServletUtils.capitalize2( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
				.append(" Time: (none)").append(LINE_SEPARATOR);
		}
		
		text.append(ServletUtils.capitalize( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) ))
			.append(" Time: ").append(ServerUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() )).append(LINE_SEPARATOR);
		text.append(LINE_SEPARATOR);
		text.append( getProgramInformation(energyCompany, liteAcctInfo, hardwares) );
		text.append(LINE_SEPARATOR);
		text.append("======================================================").append(LINE_SEPARATOR);
		
		String toStr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
		if (toStr == null)
			throw new Exception( "Property \"optout_notification_recipients\" not found" );
		
		StringTokenizer st = new StringTokenizer( toStr, "," );
		ArrayList toList = new ArrayList();
		while (st.hasMoreTokens())
			toList.add( st.nextToken() );
		String[] to = new String[ toList.size() ];
		toList.toArray( to );
        
		String from = null;
		if (energyCompany.getPrimaryContactID() > 0) {
			String[] emails = ContactFuncs.getAllEmailAddresses( energyCompany.getPrimaryContactID() );
			if (emails.length > 0)
				from = emails[0];
		}
		if (from == null)
			from = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
        
		String subject = ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE)) + " Notification";
		
		EmailMessage emailMsg = new EmailMessage( to, subject, text.toString() );
		emailMsg.setFrom( from );
		emailMsg.send();
	}

}

package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
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

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
            
			StarsGetEnergyCompanySettingsResponse ecSettings = (StarsGetEnergyCompanySettingsResponse)
					user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
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
			e.printStackTrace();
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
            
            LiteStarsCustAccountInformation  liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            
            int energyCompanyID = user.getEnergyCompanyID();
            energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            sendOptOutNotification( energyCompany, liteAcctInfo, reqOper );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " notification has been sent successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot send out " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " notification") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private static String getAccountInformation(LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo) {
		StringBuffer text = new StringBuffer();
        text.append("Account #").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("\r\n");
        
        LiteContact cont = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
        text.append( ServerUtils.getFormattedName(cont) ).append("\r\n");
        
        LiteAddress addr = energyCompany.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
        if (addr.getLocationAddress1().trim().length() > 0) {
            text.append(addr.getLocationAddress1());
            if (addr.getLocationAddress2().trim().length() > 0)
            	text.append(", ").append(addr.getLocationAddress2());
            text.append("\r\n");
            if (addr.getCityName().trim().length() > 0)
            	text.append(addr.getCityName()).append(", ");
            if (addr.getStateCode().trim().length() > 0)
            	text.append(addr.getStateCode()).append(" ");
            if (addr.getZipCode().trim().length() > 0)
            	text.append(addr.getZipCode());
            text.append("\r\n");
        }
		
		String homePhone = ServerUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE) );
		String workPhone = ServerUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE) );
		String email = ServerUtils.getNotification(
				ContactFuncs.getContactNotification(cont, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
        
        if (homePhone.length() > 0)
            text.append(homePhone).append("\r\n");
        else if (workPhone.length() > 0)
            text.append(workPhone).append("\r\n");
        else if (email.length() > 0)
        	text.append(email).append("\r\n");
        
        return text.toString();
	}
	
	private static String getProgramInformation(LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo) {
        StringBuffer text = new StringBuffer("Program/Group/Serial #: \r\n");
        
        for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
        	LiteStarsLMProgram program = (LiteStarsLMProgram)liteAcctInfo.getLmPrograms().get(i);
    		text.append("    ").append(program.getLmProgram().getProgramName()).append(" / ");
        	
        	String groupName = "(none)";
        	com.cannontech.database.data.lite.LiteYukonPAObject device =
        			com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO( program.getGroupID() );
        	if (device != null)
        		groupName = device.getPaoName();
    		text.append(groupName).append(" / ");
        	
        	String serialNo = "(none)";
        	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
        		LiteStarsAppliance app = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
        		
        		if (app.getLmProgramID() == program.getLmProgram().getProgramID()) {
        			for (int k = 0; k < liteAcctInfo.getInventories().size(); k++) {
        				int invID = ((Integer) liteAcctInfo.getInventories().get(k)).intValue();
        				
        				if (invID == app.getInventoryID()) {
							LiteStarsLMHardware hw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
							serialNo = hw.getManufactureSerialNumber();
							break;
        				}
        			}
        			
        			break;
        		}
        	}
        	text.append(serialNo).append("\r\n");
        }
        
        return text.toString();
	}
	
	private static LiteLMProgramEvent findLastOptOutEvent(ArrayList custEventHist, int programID, LiteStarsEnergyCompany energyCompany) {
		int tempTermID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION ).getEntryID();
		int termID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION ).getEntryID();
		
		try {
			for (int i = custEventHist.size() - 1; i >= 0; i--) {
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) custEventHist.get(i);
				if (liteEvent.getProgramID() != programID) continue;
				
				if (liteEvent.getActionID() == termID) break;
				
				if (liteEvent.getActionID() == tempTermID)
					return liteEvent;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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
	        cal.add( Calendar.DATE, optout.getPeriod() );
	        reenableDate = cal.getTime();
        }
        else if (optout.getPeriod() == ProgramOptOutAction.OPTOUT_TODAY)
        	reenableDate = com.cannontech.util.ServletUtil.getTomorrow( energyCompany.getDefaultTimeZone() );
        
        StringBuffer text = new StringBuffer();
        text.append("======================================================\r\n");
        text.append("\r\n");
        text.append( getAccountInformation(energyCompany, liteAcctInfo) );
        text.append("\r\n");
        text.append("======================================================\r\n");
        text.append("\r\n");
        
        text.append(ServletUtils.capitalize2( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
        	.append(" Time: ").append(ServerUtils.formatDate( optOutDate, energyCompany.getDefaultTimeZone() )).append("\r\n");
        text.append(ServletUtils.capitalize( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) ))
        	.append(" Time: ").append(ServerUtils.formatDate( reenableDate, energyCompany.getDefaultTimeZone() )).append("\r\n");
        text.append("\r\n");
        text.append( getProgramInformation(energyCompany, liteAcctInfo) );
        text.append("\r\n");
        text.append("======================================================\r\n");
        text.append("\r\n");
        text.append("\r\n");

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
    		
        	text.append("Q: ").append(liteQuestion.getQuestion()).append("\r\n");
        	text.append("A: ").append(answer.getAnswer()).append("\r\n");
        	text.append("\r\n");
        }
		
        String toStr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
        if (toStr == null)
			throw new Exception( "Property \"optout_notification_recipients\" not found, opt out notification email isn't sent" );
		
		StringTokenizer st = new StringTokenizer( toStr, "," );
		ArrayList toList = new ArrayList();
		while (st.hasMoreTokens())
			toList.add( st.nextToken() );
		String[] to = new String[ toList.size() ];
		toList.toArray( to );
        
        String from = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
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
        StringBuffer text = new StringBuffer();
        text.append("======================================================\r\n");
        text.append("\r\n");
        text.append( getAccountInformation(energyCompany, liteAcctInfo) );
        text.append("\r\n");
        text.append("======================================================\r\n");
        text.append("\r\n");
        
        OptOutEventQueue queue = energyCompany.getOptOutEventQueue();
		OptOutEventQueue.OptOutEvent e1 = queue.findOptOutEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
		OptOutEventQueue.OptOutEvent e2 = queue.findReenableEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
		
		if (e1 != null)
			text.append("Scheduled ").append(ServletUtils.capitalize2( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
				.append(" Time: ").append(ServerUtils.formatDate( new Date(e1.getStartDateTime()), energyCompany.getDefaultTimeZone() )).append("\t(Canceled)\r\n");

		boolean foundLastOptOutEvent = false;
		for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
			LiteStarsLMProgram program = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
			LiteLMProgramEvent event = findLastOptOutEvent(
					liteAcctInfo.getProgramHistory(), program.getLmProgram().getProgramID(), energyCompany );
			
			if (event != null) {
				text.append("Last ").append(ServletUtils.capitalize2(energyCompany.getEnergyCompanySetting( ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) ))
					.append(" Time: ").append(ServerUtils.formatDate( new Date(event.getEventDateTime()), energyCompany.getDefaultTimeZone() )).append("\r\n");
				foundLastOptOutEvent = true;
				break;
			}
		}
		
		if (!foundLastOptOutEvent)
			text.append("Last ").append(ServletUtils.capitalize2( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) )).append(" Time: (none)\r\n");
		
        text.append(ServletUtils.capitalize( energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) ))
        	.append(" Time: ").append(ServerUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() )).append("\r\n");
        text.append("\r\n");
        text.append( getProgramInformation(energyCompany, liteAcctInfo) );
        text.append("\r\n");
        text.append("======================================================\r\n");
		
        String toStr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
        if (toStr == null)
			throw new Exception( "Property \"optout_notification_recipients\" not found, opt out notification email isn't sent" );
		
		StringTokenizer st = new StringTokenizer( toStr, "," );
		ArrayList toList = new ArrayList();
		while (st.hasMoreTokens())
			toList.add( st.nextToken() );
		String[] to = new String[ toList.size() ];
		toList.toArray( to );
        
        String from = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
        String subject = ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE)) + " Notification";
		
		EmailMessage emailMsg = new EmailMessage( to, subject, text.toString() );
		emailMsg.setFrom( from );
		emailMsg.send();
	}

}

package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsGetExitInterviewQuestionsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
import com.cannontech.stars.xml.serialize.StarsSendExitInterviewAnswers;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.util.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SendInterviewAnswersAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
            
			StarsGetExitInterviewQuestionsResponse questions = (StarsGetExitInterviewQuestionsResponse)
					user.getAttribute( ServletUtils.ATT_EXIT_INTERVIEW_QUESTIONS );
			StarsSendExitInterviewAnswers sendAnswers = new StarsSendExitInterviewAnswers();
            
            if (questions != null && questions.getStarsExitInterviewQuestionCount() > 0) {
            	String[] qIDStrs = req.getParameterValues( "QID" );
            	String[] answers = req.getParameterValues( "Answer" );
            	
            	for (int i = 0; i < qIDStrs.length; i++) {
            		int qID = Integer.parseInt( qIDStrs[i] );
            		for (int j = 0; j < questions.getStarsExitInterviewQuestionCount(); j++) {
            			StarsExitInterviewQuestion question = questions.getStarsExitInterviewQuestion(j);
            			if (question.getQuestionID() == qID) {
            				question.setAnswer( answers[i] );
            				sendAnswers.addStarsExitInterviewQuestion( question );
            				break;
            			}
            		}
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
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
            LiteStarsCustAccountInformation liteAcctInfo = null;
            int energyCompanyID = 0;
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user != null) {
            	liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            	energyCompanyID = user.getEnergyCompanyID();
            }
            else {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            StringBuffer text = new StringBuffer("======================================================\r\n");
            text.append("Account #").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("\r\n");
            LiteCustomerContact cont = SOAPServer.getCustomerContact( energyCompanyID, liteAcctInfo.getCustomerBase().getPrimaryContactID() );
            text.append(cont.getFirstName()).append(" ").append(cont.getLastName()).append("\r\n");
            LiteCustomerAddress addr = SOAPServer.getCustomerAddress( energyCompanyID, liteAcctInfo.getAccountSite().getStreetAddressID() );
            text.append(addr.getLocationAddress1()).append(", ").append(addr.getLocationAddress2()).append("\r\n");
            text.append(addr.getCityName()).append(", ").append(addr.getStateCode()).append(" ").append(addr.getZipCode()).append("\r\n");
            text.append(cont.getHomePhone()).append("\r\n");
            text.append("======================================================\r\n");
            
            StarsProgramOptOut optout = reqOper.getStarsProgramOptOut();
            if (optout != null) {
	            text.append("Override Time: ").append(ServerUtils.formatDate( new Date() )).append("\r\n");
	            text.append("Reenable Time: ").append(ServerUtils.formatDate( optout.getReenableDateTime() )).append("\r\n\r\n");
	            text.append("Program/Group/Serial #: \r\n");
	            
	            List devices = com.cannontech.database.cache.DefaultDatabaseCache.getInstance().getAllDevices();
	            for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
	            	LiteStarsLMProgram program = (LiteStarsLMProgram)liteAcctInfo.getLmPrograms().get(i);
            		text.append("    ").append(program.getLmProgram().getProgramName()).append(" / ");
	            	
	            	String groupName = "(N/A)";
		            for (int j = 0; j < devices.size(); j++) {
		            	com.cannontech.database.data.lite.LiteYukonPAObject device = (com.cannontech.database.data.lite.LiteYukonPAObject) devices.get(j);
		            	if (device.getLiteID() == program.getGroupID() && device.getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.GROUP) {
		            		groupName = device.getPaoName();
		            		break;
		            	}
		            }
            		text.append(groupName).append(" / ");
	            	
	            	String serialNo = "(N/A)";
	            	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
	            		LiteStarsAppliance app = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
	            		if (app.getLmProgramID() == program.getLmProgram().getProgramID()) {
	            			for (int k = 0; k < liteAcctInfo.getInventories().size(); k++) {
	            				LiteLMHardwareBase hw = SOAPServer.getLMHardware( energyCompanyID, ((Integer) liteAcctInfo.getInventories().get(k)).intValue(), true );
	            				if (hw.getInventoryID() == app.getInventoryID()) {
	            					serialNo = hw.getManufactureSerialNumber();
	            					break;
	            				}
	            			}
	            			break;
	            		}
	            	}
	            	text.append(serialNo).append("\r\n");
	            }
            }
            text.append("======================================================\r\n\r\n\r\n");
            
            StarsSendExitInterviewAnswers sendAnswers = reqOper.getStarsSendExitInterviewAnswers();
            for (int i = 0; i < sendAnswers.getStarsExitInterviewQuestionCount(); i++) {
            	StarsExitInterviewQuestion answer = sendAnswers.getStarsExitInterviewQuestion(i);
            	text.append("Q: ").append(answer.getQuestion()).append("\r\n");
            	text.append("A: ").append(answer.getAnswer()).append("\r\n\r\n");
            }
        	
			ResourceBundle bundle = ResourceBundle.getBundle( "config" );
			
			String toStr = null;
			try {
				toStr = bundle.getString( "optout_notification_recipients" );
				StringTokenizer st = new StringTokenizer( toStr, "," );
				ArrayList toList = new ArrayList();
				while (st.hasMoreTokens())
					toList.add( st.nextToken() );
				String[] to = new String[ toList.size() ];
				toList.toArray( to );
				
				String from = null;
				try {
					from = bundle.getString( "optout_notification_sender" );
				}
				catch (java.util.MissingResourceException mre) {
					from = "";
				}
				String subject = "Opt Out Notification";
				
				ServerUtils.sendEmailMsg( from, to, null, subject, text.toString() );
			}
			catch (java.util.MissingResourceException mre) {
				CTILogger.info( "Property \"optout_notification_recipients\" not found, opt out notification email isn't sent" );
			}
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Interview answers have been sent" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot send out interview answers") );
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
	
	private String getNotificationReceiver() {
		String email = null;
		try {
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle( "config" );
			email = bundle.getString( "optout_notification_recipients" );
		}
		catch (java.util.MissingResourceException e) {
			email = "yzhihong@cannontech.com";
		}
		
		return email;
	}

}

package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteCustomerContact;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.roles.yukon.EnergyCompanyRole;
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
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            StringBuffer text = new StringBuffer("======================================================\r\n");
            text.append("Account #").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("\r\n");
            LiteCustomerContact cont = energyCompany.getCustomerContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
            text.append(cont.getFirstName()).append(" ").append(cont.getLastName()).append("\r\n");
            LiteAddress addr = energyCompany.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
            text.append(addr.getLocationAddress1()).append(", ").append(addr.getLocationAddress2()).append("\r\n");
            text.append(addr.getCityName()).append(", ").append(addr.getStateCode()).append(" ").append(addr.getZipCode()).append("\r\n");
            text.append(cont.getHomePhone()).append("\r\n");
            text.append("======================================================\r\n");
            
            StarsProgramOptOut optout = reqOper.getStarsProgramOptOut();
            Date optOutDate = optout.getStartDateTime();
            if (optOutDate == null) optOutDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime( optOutDate );
            cal.add( Calendar.DATE, optout.getPeriod() );
            Date reenableDate = cal.getTime();
            
            if (optout != null) {
	            text.append("Opt Out Time : ").append(ServerUtils.formatDate( optOutDate )).append("\r\n");
	            text.append("Reenable Time: ").append(ServerUtils.formatDate( reenableDate )).append("\r\n\r\n");
	            text.append("Program/Group/Serial #: \r\n");
	            
	            for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
	            	LiteStarsLMProgram program = (LiteStarsLMProgram)liteAcctInfo.getLmPrograms().get(i);
            		text.append("    ").append(program.getLmProgram().getProgramName()).append(" / ");
	            	
	            	String groupName = "(N/A)";
	            	com.cannontech.database.data.lite.LiteYukonPAObject device =
	            			com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO( program.getGroupID() );
	            	if (device != null)
	            		groupName = device.getPaoName();
            		text.append(groupName).append(" / ");
	            	
	            	String serialNo = "(N/A)";
	            	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
	            		LiteStarsAppliance app = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
	            		if (app.getLmProgramID() == program.getLmProgram().getProgramID()) {
	            			for (int k = 0; k < liteAcctInfo.getInventories().size(); k++) {
	            				LiteLMHardwareBase hw = energyCompany.getLMHardware( ((Integer) liteAcctInfo.getInventories().get(k)).intValue(), true );
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
            
            int exitQType = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT).getEntryID();
            LiteInterviewQuestion[] liteQuestions = energyCompany.getInterviewQuestions( exitQType );
            
            StarsSendExitInterviewAnswers sendAnswers = reqOper.getStarsSendExitInterviewAnswers();
            for (int i = 0; i < sendAnswers.getStarsExitInterviewQuestionCount(); i++) {
            	StarsExitInterviewQuestion answer = sendAnswers.getStarsExitInterviewQuestion(i);
            	LiteInterviewQuestion liteQuestion = null;
            	for (int j = 0; j < liteQuestions.length; j++)
            		if (liteQuestions[j].getQuestionID() == answer.getQuestionID()) {
            			liteQuestion = liteQuestions[j];
	            		break;
            		}
            		
            	if (liteQuestion != null) {
	            	text.append("Q: ").append(liteQuestion.getQuestion()).append("\r\n");
	            	text.append("A: ").append(answer.getAnswer()).append("\r\n\r\n");
            	}
            	else
            		throw new Exception("Cannot find exit interview question with id = " + answer.getQuestionID());
            }
			
            String toStr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
            if (toStr != null) {
				StringTokenizer st = new StringTokenizer( toStr, "," );
				ArrayList toList = new ArrayList();
				while (st.hasMoreTokens())
					toList.add( st.nextToken() );
				String[] to = new String[ toList.size() ];
				toList.toArray( to );
	            
	            String from = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
				
				String subject = "Opt Out Notification";
				
				ServerUtils.sendEmailMsg( from, to, null, subject, text.toString() );
            }
			else
				CTILogger.info( "Property \"optout_notification_recipients\" not found, opt out notification email isn't sent" );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Interview answers have been sent" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
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

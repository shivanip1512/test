package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.data.lite.stars.LiteCustomerSelectionList;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsGetExitInterviewQuestionsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GetInterviewQuestionsAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetExitInterviewQuestions getQuestions = new StarsGetExitInterviewQuestions();
			
			int energyCompanyID = 0;
			String companyIDStr = req.getParameter("CompanyID");
			if (companyIDStr != null && companyIDStr.length() > 0)
				try {
					energyCompanyID = Integer.parseInt( companyIDStr );
				}
				catch (NumberFormatException e) {}
			if (energyCompanyID > 0)
				getQuestions.setEnergyCompanyID( energyCompanyID );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetExitInterviewQuestions( getQuestions );
			
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
            
            StarsGetExitInterviewQuestions getQuestions = reqOper.getStarsGetExitInterviewQuestions();
            int energyCompanyID = getQuestions.getEnergyCompanyID();
            
            if (energyCompanyID <= 0) {
				StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
	            if (user != null)
	            	energyCompanyID = user.getEnergyCompanyID();
	            else {
	            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	            }
            }
            
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            Hashtable selectionLists = energyCompany.getAllSelectionLists();
            
            int exitQType = StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_QUESTIONTYPE),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_QUESTIONTYPE_EXIT ).getEntryID();
            LiteInterviewQuestion[] liteQuestions = energyCompany.getInterviewQuestions( exitQType );
            		
            StarsGetExitInterviewQuestionsResponse resp = new StarsGetExitInterviewQuestionsResponse();
            for (int i = 0; i < liteQuestions.length; i++) {
            	StarsExitInterviewQuestion starsQuestion = new StarsExitInterviewQuestion();
            	StarsLiteFactory.setStarsInterviewQuestion( starsQuestion, liteQuestions[i], selectionLists );
            	resp.addStarsExitInterviewQuestion( starsQuestion );
            }
            
            respOper.setStarsGetExitInterviewQuestionsResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get exit interview questions") );
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
			
            StarsGetExitInterviewQuestionsResponse questions = operation.getStarsGetExitInterviewQuestionsResponse();
            if (questions == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            if (questions.getStarsExitInterviewQuestionCount() == 0) {
            	String redir = "/servlet/SOAPClient?action=SendExitAnswers"
            				 + "&REDIRECT=" + session.getAttribute( ServletUtils.ATT_REDIRECT2 )
            				 + "&REFERRER=" + session.getAttribute( ServletUtils.ATT_REFERRER );
            	session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
            }

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        	user.setAttribute(ServletUtils.ATT_EXIT_INTERVIEW_QUESTIONS, questions);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}

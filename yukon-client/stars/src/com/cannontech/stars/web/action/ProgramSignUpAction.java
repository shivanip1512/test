package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.stars.web.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.StarsCustomerContactFactory;
import com.cannontech.stars.xml.StarsGetEnrollmentProgramsResponseFactory;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProgramSignUpAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsProgramSignUp progSignUp = new StarsProgramSignUp();
			progSignUp.setEnergyCompanyID( Integer.parseInt(req.getParameter("CompanyID")) );
			progSignUp.setAccountNumber( req.getParameter("AcctNo") );
			
			String[] catIDs = req.getParameterValues( "CatID" );
			String[] progIDs = req.getParameterValues( "ProgID" );
			StarsLMProgramSignUps programs = new StarsLMProgramSignUps();
			if (progIDs != null)
				for (int i = 0; i < progIDs.length; i++) {
					if (progIDs[i].length() == 0) continue;
					
					LMProgram program = new LMProgram();
					program.setProgramID( Integer.parseInt(progIDs[i]) );
					program.setApplianceCategoryID( Integer.parseInt(catIDs[i]) );
					programs.addLMProgram( program );
				}
			progSignUp.setStarsLMProgramSignUps( programs );
			
			StarsLogin login = new StarsLogin();
			login.setUsername( req.getParameter("UserName") );
			login.setPassword( req.getParameter("Password") );
			progSignUp.setStarsLogin( login );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsProgramSignUp( progSignUp );
			
            return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			
            StarsProgramSignUp progSignUp = reqOper.getStarsProgramSignUp();
            int energyCompanyID = progSignUp.getEnergyCompanyID();
            
            if (energyCompanyID <= 0) {
	            if (operator == null && user == null) {
	            	StarsFailure failure = new StarsFailure();
	            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
	            	failure.setDescription( "Session invalidated, please login again" );
	            	respOper.setStarsFailure( failure );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	            }
	            
	            if (operator != null)
	            	energyCompanyID = (int) operator.getEnergyCompanyID();
	            else
	            	energyCompanyID = user.getEnergyCompanyID();
            }
            
            com.cannontech.database.data.stars.customer.CustomerAccount account =
		            com.cannontech.database.data.stars.customer.CustomerAccount.searchByAccountNumber(
        	    		new Integer(energyCompanyID), progSignUp.getAccountNumber() );
        	    		
            StarsLMProgramSignUps programs = progSignUp.getStarsLMProgramSignUps();
        	for (int i = 0; i < programs.getLMProgramCount(); i++) {
        		LMProgram program = programs.getLMProgram(i);
        		com.cannontech.database.db.stars.appliance.ApplianceBase appDB = new com.cannontech.database.db.stars.appliance.ApplianceBase();
        		appDB.setAccountID( account.getCustomerAccount().getAccountID() );
        		appDB.setApplianceCategoryID( new Integer(program.getApplianceCategoryID()) );
        		appDB.setLMProgramID( new Integer(program.getProgramID()) );
        		
        		Transaction.createTransaction( Transaction.INSERT, appDB ).execute();
        	}
            
            StarsLogin starsLogin = progSignUp.getStarsLogin();
            com.cannontech.database.db.customer.CustomerLogin login = new com.cannontech.database.db.customer.CustomerLogin();
            login.setUserName( starsLogin.getUsername() );
            login.setUserPassword( starsLogin.getPassword() );
            login.setLoginType( "STARS" );
            
            Transaction.createTransaction( Transaction.INSERT, login ).execute();
            
            com.cannontech.database.db.customer.CustomerContact primContact = account.getCustomerBase().getPrimaryContact();
            primContact.setLogInID( login.getLoginID() );
            
            com.cannontech.database.db.stars.CustomerContact contact = new com.cannontech.database.db.stars.CustomerContact();
            contact.setCustomerContact( primContact );
            Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
            
            StarsSuccess success = new StarsSuccess();
            respOper.setStarsSuccess( success );

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
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
			if (failure != null) return failure.getStatusCode();
			
            if (operation.getStarsSuccess() == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}

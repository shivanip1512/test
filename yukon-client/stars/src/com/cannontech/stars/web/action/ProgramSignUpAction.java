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
			StarsSULMPrograms programs = new StarsSULMPrograms();
			if (progIDs != null)
				for (int i = 0; i < progIDs.length; i++) {
					if (progIDs[i].length() == 0) continue;
					
					SULMProgram program = new SULMProgram();
					program.setProgramID( Integer.parseInt(progIDs[i]) );
					program.setApplianceCategoryID( Integer.parseInt(catIDs[i]) );
					programs.addSULMProgram( program );
				}
			progSignUp.setStarsSULMPrograms( programs );
			
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
        	    		
            StarsSULMPrograms programs = progSignUp.getStarsSULMPrograms();
            if (programs.getSULMProgramCount() > 0) {
	        	com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
	        	
	        	// Get the primary IDs for table insert
	        	int nextAppID = 0;
	        	int nextEventID = 0;
	        	
	        	java.sql.Connection conn = null;
	        	try {
	        		conn = com.cannontech.database.PoolManager.getInstance().getConnection(
	        				com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	        				
	        		com.cannontech.database.db.stars.appliance.ApplianceBase appDB = new com.cannontech.database.db.stars.appliance.ApplianceBase();
	        		appDB.setDbConnection( conn );
	        		nextAppID = appDB.getNextApplianceID().intValue();
	        		
	        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = new com.cannontech.database.db.stars.event.LMCustomerEventBase();
	        		eventDB.setDbConnection( conn );
	        		nextEventID = eventDB.getNextEventID().intValue();
	        	}
	        	catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        	finally {
	        		try {
	        			conn.close();
	        		}
	        		catch (Exception e) {}
	        	}
	        	
	        	// Get "Signup" action & event type ID
            	java.util.Hashtable selectionLists = com.cannontech.stars.util.CommonUtils.getSelectionListTable(
            			new Integer(energyCompanyID) );
            			
            	Integer progEventEntryID = null;
	        	Integer signUpEntryID = null;
	        	
	            StarsCustSelectionList custEventList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT );
	            for (int i = 0; i < custEventList.getStarsSelectionListEntryCount(); i++) {
	            	StarsSelectionListEntry entry = custEventList.getStarsSelectionListEntry(i);
	            	if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMPROGRAMEVENT )) {
	            		progEventEntryID = new Integer( entry.getEntryID() );
	            		break;
	            	}
	            }
	        	
	            StarsCustSelectionList actionList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION );
	            for (int i = 0; i < actionList.getStarsSelectionListEntryCount(); i++) {
	            	StarsSelectionListEntry entry = actionList.getStarsSelectionListEntry(i);
	            	if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_SIGNUP )) {
	            		signUpEntryID = new Integer( entry.getEntryID() );
	            		break;
	            	}
	            }
	            
	            java.util.Date now = new java.util.Date();
	        	
	        	for (int i = 0; i < programs.getSULMProgramCount(); i++) {
	        		SULMProgram program = programs.getSULMProgram(i);
	        		
	        		com.cannontech.database.db.stars.appliance.ApplianceBase appDB = new com.cannontech.database.db.stars.appliance.ApplianceBase();
	        		appDB.setApplianceID( new Integer(nextAppID++) );
	        		appDB.setAccountID( account.getCustomerAccount().getAccountID() );
	        		appDB.setApplianceCategoryID( new Integer(program.getApplianceCategoryID()) );
	        		appDB.setLMProgramID( new Integer(program.getProgramID()) );
	        		
	        		multiDB.getDBPersistentVector().addElement( appDB );
	        		
	        		com.cannontech.database.data.stars.event.LMProgramEvent event =
	        				new com.cannontech.database.data.stars.event.LMProgramEvent();
	        		com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
	        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
	        		
	        		event.setEventID( new Integer(nextEventID++) );
	        		eventDB.setAccountID( account.getCustomerAccount().getAccountID() );
	        		eventDB.setLMProgramID( new Integer(program.getProgramID()) );
	        		eventBase.setEventTypeID( progEventEntryID );
	        		eventBase.setActionID( signUpEntryID );
	        		eventBase.setEventDateTime( now );
	        		
					com.cannontech.database.data.company.EnergyCompanyBase energyCompany = new com.cannontech.database.data.company.EnergyCompanyBase();
					energyCompany.getEnergyCompany().setEnergyCompanyID( new Integer(energyCompanyID) );
	        		event.setEnergyCompanyBase( energyCompany );
	        		
	        		multiDB.getDBPersistentVector().addElement( event );
	        	}
	            
	            Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
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

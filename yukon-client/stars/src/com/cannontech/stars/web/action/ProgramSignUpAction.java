package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import java.util.*;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.StarsCustomerContactFactory;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
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
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
			
            StarsProgramSignUp progSignUp = reqOper.getStarsProgramSignUp();
            int energyCompanyID = progSignUp.getEnergyCompanyID();
            if (energyCompanyID <= 0)
            	energyCompanyID = user.getEnergyCompanyID();
            
            LiteStarsCustAccountInformation liteAcctInfo = SOAPServer.searchByAccountNumber( energyCompanyID, progSignUp.getAccountNumber() );
            com.cannontech.database.data.stars.customer.CustomerAccount account =
		            com.cannontech.database.data.stars.customer.CustomerAccount.searchByAccountNumber(
        	    		new Integer(energyCompanyID), progSignUp.getAccountNumber() );
        	    		
            StarsSULMPrograms programs = progSignUp.getStarsSULMPrograms();
            if (programs.getSULMProgramCount() > 0) {
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
            	Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            	
            	Integer progEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            			(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            			com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMPROGRAMEVENT)
            			.getEntryID() );
	        	Integer signUpEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            			(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            			com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_SIGNUP)
            			.getEntryID() );
	            
	            Date now = new Date();
	            if (liteAcctInfo.getAppliances() == null)
	            	liteAcctInfo.setAppliances( new ArrayList() );
	            
	        	for (int i = 0; i < programs.getSULMProgramCount(); i++) {
	        		SULMProgram program = programs.getSULMProgram(i);
		        	com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
	        		
	        		com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
	        		com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
	        		
	        		app.setApplianceID( new Integer(nextAppID++) );
	        		appDB.setAccountID( account.getCustomerAccount().getAccountID() );
	        		appDB.setApplianceCategoryID( new Integer(program.getApplianceCategoryID()) );
	        		appDB.setLMProgramID( new Integer(program.getProgramID()) );
	        		
	        		multiDB.getDBPersistentVector().addElement( app );
	        		
	        		com.cannontech.database.data.stars.event.LMProgramEvent event =
	        				new com.cannontech.database.data.stars.event.LMProgramEvent();
	        		com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
	        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
	        		
	        		event.setEventID( new Integer(nextEventID++) );
	        		event.setEnergyCompanyID( new Integer(energyCompanyID) );
	        		eventDB.setAccountID( account.getCustomerAccount().getAccountID() );
	        		eventDB.setLMProgramID( new Integer(program.getProgramID()) );
	        		eventBase.setEventTypeID( progEventEntryID );
	        		eventBase.setActionID( signUpEntryID );
	        		eventBase.setEventDateTime( now );
	        		
	        		multiDB.getDBPersistentVector().addElement( event );
	            
		            multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
		            		Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
		            app = (com.cannontech.database.data.stars.appliance.ApplianceBase) multiDB.getDBPersistentVector().get(0);
		            event = (com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(1);
		            
		            liteAcctInfo.getAppliances().add( StarsLiteFactory.createStarsAppliance(app, energyCompanyID) );
		            
		            LiteStarsLMProgram liteProg = new LiteStarsLMProgram();
		            liteProg.setProgramID( app.getLMProgram().getPAObjectID().intValue() );
		            
		            liteProg.setProgramHistory( new ArrayList() );
		            LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
		            liteProg.getProgramHistory().add( liteEvent );
		            
		            liteAcctInfo.getLmPrograms().add( liteProg );
	        	}
            }
            
            StarsLogin starsLogin = progSignUp.getStarsLogin();
            com.cannontech.database.db.customer.CustomerLogin login = new com.cannontech.database.db.customer.CustomerLogin();
            login.setUserName( starsLogin.getUsername() );
            login.setUserPassword( starsLogin.getPassword() );
            login.setLoginType( "STARS" );
            
            Transaction.createTransaction( Transaction.INSERT, login ).execute();
            
            com.cannontech.database.db.customer.CustomerContact primContact = account.getCustomerBase().getPrimaryContact();
            primContact.setLogInID( login.getLoginID() );
            
            com.cannontech.database.db.stars.CustomerContact contact = new com.cannontech.database.db.stars.CustomerContact( primContact );
    		Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
            
            StarsSuccess success = new StarsSuccess();
            respOper.setStarsSuccess( success );

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot complete the program signup") );
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

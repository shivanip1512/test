package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsLogin;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsProgramSignUpResponse;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
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
public class ProgramSignUpAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsProgramSignUp progSignUp = new StarsProgramSignUp();
			if (req.getParameter("CompanyID") != null)
				progSignUp.setEnergyCompanyID( Integer.parseInt(req.getParameter("CompanyID")) );
			if (req.getParameter("AcctNo") != null)
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
			
			if (req.getParameter("UserName") != null && req.getParameter("Password") != null) {
				StarsLogin login = new StarsLogin();
				login.setUsername( req.getParameter("UserName") );
				login.setPassword( req.getParameter("Password") );
				progSignUp.setStarsLogin( login );
			}
			
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
            StarsProgramSignUp progSignUp = reqOper.getStarsProgramSignUp();
            
            int energyCompanyID = progSignUp.getEnergyCompanyID();
            if (energyCompanyID <= 0) {
            	if (user == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
            	}
            	energyCompanyID = user.getEnergyCompanyID();
            }
            
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            LiteStarsCustAccountInformation liteAcctInfo = null;
            if (user != null)
            	liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            else
            	liteAcctInfo = energyCompany.searchByAccountNumber( progSignUp.getAccountNumber() );
	        	
        	// Get action & event type IDs
        	Integer progEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID() );
        	Integer signUpEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP).getEntryID() );
        	Integer termEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID() );
        	Integer dftLocationID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_LOC_UNKNOW).getEntryID() );
        	Integer dftManufacturerID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_MANU_UNKNOWN).getEntryID() );
        	
        	ArrayList progList = liteAcctInfo.getLmPrograms();
        	ArrayList appList = liteAcctInfo.getAppliances();
        	ArrayList newAppList = new ArrayList();
        	ArrayList newProgList = new ArrayList();
        	
			com.cannontech.database.data.stars.event.LMProgramEvent event =
					new com.cannontech.database.data.stars.event.LMProgramEvent();
			com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
			
			Integer accountID = new Integer( liteAcctInfo.getCustomerAccount().getAccountID() );
            Date now = new Date();
        	
    		/* Assumption: there is only one appliance in each category, otherwise this won't work!!! */
            /* Only assign appliance to the first group of the program now!!! */
            StarsSULMPrograms programs = progSignUp.getStarsSULMPrograms();
            for (int i = 0; i < programs.getSULMProgramCount(); i++) {
        		SULMProgram program = programs.getSULMProgram(i);
        		
        		LiteStarsAppliance liteApp = null;
        		for (int j = 0; j < appList.size(); j++) {
        			LiteStarsAppliance lApp = (LiteStarsAppliance) appList.get(j);
        			if (lApp.getApplianceCategoryID() == program.getApplianceCategoryID()) {
        				liteApp = lApp;
        				
        				if (liteApp.getLmProgramID() == 0) {
        				/* Assign the program to an unenrolled appliance */
        					// Add "sign up" event to the new program
        					event.setEventID( null );
							event.setEnergyCompanyID( new Integer(energyCompanyID) );
							eventDB.setAccountID( accountID );
							eventDB.setLMProgramID( new Integer(program.getProgramID()) );
							eventBase.setEventTypeID( progEventEntryID );
							eventBase.setActionID( signUpEntryID );
							eventBase.setEventDateTime( now );
							Transaction.createTransaction(Transaction.INSERT, event).execute();
							
							// What's the initial status of the program?
							
							// Add the program to the program list of the account
			                LiteLMProgram liteProg = energyCompany.getLMProgram( program.getProgramID() );
			                LiteStarsLMProgram liteStarsProg = new LiteStarsLMProgram( liteProg );
			                if (liteApp.getInventoryID() > 0) {
			                	if (liteProg.getGroupIDs() != null || liteProg.getGroupIDs().length > 0)
			                		liteStarsProg.setGroupID( liteProg.getGroupIDs()[0] );
			                }
			                com.cannontech.database.data.stars.event.LMProgramEvent[] events =
			                		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents(
			                			new Integer(liteAcctInfo.getCustomerAccount().getAccountID()), new Integer(program.getProgramID()) );
			                if (events != null) {
			                	liteStarsProg.setProgramHistory( new ArrayList() );
			                	for (int k = 0; k < events.length; k++) {
			                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[k] );
			                		liteStarsProg.getProgramHistory().add( liteEvent );
			                	}
			                }
			                newProgList.add( liteStarsProg );
							
        					liteApp.setLmProgramID( program.getProgramID() );
        					com.cannontech.database.data.stars.appliance.ApplianceBase app =
        							(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
        					Transaction.createTransaction(Transaction.UPDATE, app).execute();
        				}
        				else if (liteApp.getLmProgramID() != program.getProgramID()) {
						/* Update the program enrollment for an appliance */
        					// Add "termination" event to the old program
        					event.setEventID( null );
							event.setEnergyCompanyID( new Integer(energyCompanyID) );
							eventDB.setAccountID( accountID );
							eventDB.setLMProgramID( new Integer(liteApp.getLmProgramID()) );
							eventBase.setEventTypeID( progEventEntryID );
							eventBase.setActionID( termEntryID );
							eventBase.setEventDateTime( now );
							Transaction.createTransaction(Transaction.INSERT, event).execute();
							
							// Keep the current program status?
							
							// Add "sign up" event to the new program
        					event.setEventID( null );
							eventDB.setLMProgramID( new Integer(program.getProgramID()) );
							eventBase.setActionID( signUpEntryID );
							Transaction.createTransaction(Transaction.INSERT, event).execute();
							
							// Update the program list of the account
			                LiteLMProgram liteProg = energyCompany.getLMProgram( program.getProgramID() );
			                LiteStarsLMProgram liteStarsProg = new LiteStarsLMProgram( liteProg );
			                if (liteApp.getInventoryID() > 0) {
			                	if (liteProg.getGroupIDs() != null || liteProg.getGroupIDs().length > 0)
			                		liteStarsProg.setGroupID( liteProg.getGroupIDs()[0] );
			                }
			                com.cannontech.database.data.stars.event.LMProgramEvent[] events =
			                		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents(
			                			new Integer(liteAcctInfo.getCustomerAccount().getAccountID()), new Integer(program.getProgramID()) );
			                if (events != null) {
			                	liteStarsProg.setProgramHistory( new ArrayList() );
			                	for (int k = 0; k < events.length; k++) {
			                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[k] );
			                		liteStarsProg.getProgramHistory().add( liteEvent );
			                	}
			                }
			                newProgList.add( liteStarsProg );
							
        					liteApp.setLmProgramID( program.getProgramID() );
        					com.cannontech.database.data.stars.appliance.ApplianceBase app =
        							(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
        					Transaction.createTransaction(Transaction.UPDATE, app).execute();
        				}
        				else {
        					for (int k = 0; k < progList.size(); k++) {
        						LiteStarsLMProgram liteStarsProg = (LiteStarsLMProgram) progList.get(k);
        						if (liteStarsProg.getLmProgram().getProgramID() == program.getProgramID()) {
        							newProgList.add( liteStarsProg );
        							break;
        						}
        					}
        				}
    					
    					appList.remove( liteApp );
    					newAppList.add( liteApp );
        				break;
        			}
        		}
        		
        		if (liteApp == null) {
        		/* Create a new appliance for the program */
					// Add "sign up" event to the new program
					event.setEventID( null );
					event.setEnergyCompanyID( new Integer(energyCompanyID) );
					eventDB.setAccountID( accountID );
					eventDB.setLMProgramID( new Integer(program.getProgramID()) );
					eventBase.setEventTypeID( progEventEntryID );
					eventBase.setActionID( signUpEntryID );
					eventBase.setEventDateTime( now );
					Transaction.createTransaction(Transaction.INSERT, event).execute();
					
					// What's the initial status of the program
					
					// Add the program to the program list of the account
	                LiteLMProgram liteProg = energyCompany.getLMProgram( program.getProgramID() );
	                LiteStarsLMProgram liteStarsProg = new LiteStarsLMProgram( liteProg );
	                if (liteApp.getInventoryID() > 0) {
	                	if (liteProg.getGroupIDs() != null || liteProg.getGroupIDs().length > 0)
	                		liteStarsProg.setGroupID( liteProg.getGroupIDs()[0] );
	                }
	                com.cannontech.database.data.stars.event.LMProgramEvent[] events =
	                		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents(
	                			new Integer(liteAcctInfo.getCustomerAccount().getAccountID()), new Integer(program.getProgramID()) );
	                if (events != null) {
	                	liteStarsProg.setProgramHistory( new ArrayList() );
	                	for (int k = 0; k < events.length; k++) {
	                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[k] );
	                		liteStarsProg.getProgramHistory().add( liteEvent );
	                	}
	                }
	                newProgList.add( liteStarsProg );
	        		
	        		com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
	        		com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
	        		
	        		appDB.setAccountID( accountID );
	        		appDB.setApplianceCategoryID( new Integer(program.getApplianceCategoryID()) );
	        		appDB.setLMProgramID( new Integer(program.getProgramID()) );
	        		appDB.setLocationID( dftLocationID );
	        		appDB.setManufacturerID( dftManufacturerID );
	        		app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
	        				Transaction.createTransaction(Transaction.INSERT, app).execute();
	        		
	        		liteApp = (LiteStarsAppliance) StarsLiteFactory.createLite( app );
	        		newAppList.add( liteApp );
        		}
            }
    		
    		/* Remove enrolled program for the rest of the appliances */
    		for (int i = 0; i < appList.size(); i++) {
    			LiteStarsAppliance liteApp = (LiteStarsAppliance) appList.get(i);
    			
    			if (liteApp.getLmProgramID() != 0) {
					// Add "termination" event to the old program
					event.setEventID( null );
					event.setEnergyCompanyID( new Integer(energyCompanyID) );
					eventDB.setAccountID( accountID );
					eventDB.setLMProgramID( new Integer(liteApp.getLmProgramID()) );
					eventBase.setEventTypeID( progEventEntryID );
					eventBase.setActionID( termEntryID );
					eventBase.setEventDateTime( now );
					Transaction.createTransaction(Transaction.INSERT, event).execute();
					
	    			liteApp.setLmProgramID( 0 );
	    			liteApp.setAddressingGroupID( 0 );
					com.cannontech.database.data.stars.appliance.ApplianceBase app =
							(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
					Transaction.createTransaction(Transaction.UPDATE, app).execute();
    			}
    			
    			newAppList.add( liteApp );
    		}
    		
    		liteAcctInfo.setAppliances( newAppList );
    		liteAcctInfo.setLmPrograms( newProgList );
            
            StarsLogin starsLogin = progSignUp.getStarsLogin();
            if (starsLogin != null)
            	UpdateLoginAction.createLogin( liteAcctInfo.getCustomerAccount(), energyCompanyID, starsLogin.getUsername(), starsLogin.getPassword() );
            
            if (user != null) {
				StarsProgramSignUpResponse resp = new StarsProgramSignUpResponse();
				
				ArrayList liteProgs = liteAcctInfo.getLmPrograms();
				StarsLMPrograms starsProgs = new StarsLMPrograms();
				resp.setStarsLMPrograms( starsProgs );
				
				for (int i = 0; i < liteProgs.size(); i++) {
					LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteProgs.get(i);
					LiteStarsAppliance liteApp = null;
					
					ArrayList liteApps = liteAcctInfo.getAppliances();
					for (int k = 0; k < liteApps.size(); k++) {
						LiteStarsAppliance lApp = (LiteStarsAppliance) liteApps.get(k);
						if (lApp.getLmProgramID() == liteProg.getLmProgram().getProgramID()) {
							liteApp = lApp;
							break;
						}
					}
					
					starsProgs.addStarsLMProgram( StarsLiteFactory.createStarsLMProgram(liteProg, liteApp, energyCompanyID) );
				}
				
				if (SOAPServer.isClientLocal() || ServerUtils.isOperator( user )) {
					ArrayList liteApps = liteAcctInfo.getAppliances();
					StarsAppliances starsApps = new StarsAppliances();
					resp.setStarsAppliances( starsApps );
					
					TreeMap tmap = new TreeMap();
					for (int i = 0; i < liteApps.size(); i++) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
						StarsAppliance starsApp = (StarsAppliance) StarsLiteFactory.createStarsAppliance(liteApp, energyCompanyID);
						
						ArrayList list = (ArrayList) tmap.get( starsApp.getCategoryName() );
						if (list == null) {
							list = new ArrayList();
							tmap.put( starsApp.getCategoryName(), list );
						}
						list.add( starsApp );
					}
					
					Iterator it = tmap.values().iterator();
					while (it.hasNext()) {
						ArrayList list = (ArrayList) it.next();
						for (int i = 0; i < list.size(); i++)
							starsApps.addStarsAppliance( (StarsAppliance) list.get(i) );
					}
				}
				
				respOper.setStarsProgramSignUpResponse( resp );
            }
            else {
	            StarsSuccess success = new StarsSuccess();
	            respOper.setStarsSuccess( success );
            }

            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user != null) {
	            user.removeAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_LM_PROGRAM_HISTORY );
	            
				StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				StarsProgramSignUpResponse resp = operation.getStarsProgramSignUpResponse();
				accountInfo.setStarsLMPrograms( resp.getStarsLMPrograms() );
				accountInfo.setStarsAppliances( resp.getStarsAppliances() );
			}
			else {
	            if (operation.getStarsSuccess() == null)
	            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}

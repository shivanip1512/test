package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.LMHardwareConfiguration;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ECUtils;
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
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
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

	private static ArrayList hwIDsToConfig = new ArrayList();
	private static ArrayList hwIDsToDisable = new ArrayList();
	
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
			
			String notEnrolled = req.getParameter( "NotEnrolled" );
			if (notEnrolled == null) {
				StarsSULMPrograms programs = new StarsSULMPrograms();
				progSignUp.setStarsSULMPrograms( programs );
				
				String[] catIDs = req.getParameterValues( "CatID" );
				String[] progIDs = req.getParameterValues( "ProgID" );
				if (progIDs != null)
					for (int i = 0; i < progIDs.length; i++) {
						if (progIDs[i].length() == 0) continue;
						
						SULMProgram program = new SULMProgram();
						program.setProgramID( Integer.parseInt(progIDs[i]) );
						program.setApplianceCategoryID( Integer.parseInt(catIDs[i]) );
						programs.addSULMProgram( program );
					}
			}
			else if (Boolean.valueOf( notEnrolled ).booleanValue()) {
				StarsSULMPrograms programs = new StarsSULMPrograms();
				progSignUp.setStarsSULMPrograms( programs );
			}
			// else if (notEnrolled.equalsIgnoreCase( "Resend" ))
				// Resend the not enrolled command
			
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
        java.sql.Connection conn = null;
        
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
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
            		com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            
            LiteStarsCustAccountInformation liteAcctInfo = null;
            if (progSignUp.getAccountNumber() != null)
            	liteAcctInfo = energyCompany.searchByAccountNumber( progSignUp.getAccountNumber() );
            else
            	liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
            if (progSignUp.getStarsSULMPrograms() == null) {
            	// Resend the not enrolled command
            	respOper.setStarsProgramSignUpResponse( resendNotEnrolled(energyCompany, liteAcctInfo, conn) );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            String progEnrBefore = null;
            for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
            	LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
            	if (progEnrBefore == null)
            		progEnrBefore = liteProg.getLmProgram().getProgramName();
            	else
					progEnrBefore += ", " + liteProg.getLmProgram().getProgramName();
            }
            
			// If there is only one hardware in this account, use it as the default hardware, assign all programs to it
			Integer dftInvID = null;
			if (liteAcctInfo.getInventories().size() == 1)
				dftInvID = (Integer) liteAcctInfo.getInventories().get(0);
	        
	        updateProgramEnrollment( progSignUp, liteAcctInfo, dftInvID, energyCompany, conn );
	        
	        String progEnrNow = null;
			for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
				if (progEnrNow == null)
					progEnrNow = liteProg.getLmProgram().getProgramName();
				else
					progEnrNow += ", " + liteProg.getLmProgram().getProgramName();
			}
	        
    		// Go through the list of hardware "to be disabled", and move the fake ones to the "to be configured" list
    		for (int i = 0; i < hwIDsToDisable.size(); i++) {
    			int invID = ((Integer) hwIDsToDisable.get(i)).intValue();
    			
    			for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
    				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
    				if (liteApp.getInventoryID() == invID) {
    					Integer id = (Integer) hwIDsToDisable.remove(i);
    					if (!hwIDsToConfig.contains( id )) hwIDsToConfig.add( id );
    					break;
    				}
    			}
    		}
			
			// Send out the config/disable command
			StarsInventories starsInvs = new StarsInventories();
			
			for (int i = 0; i < hwIDsToConfig.size(); i++) {
				int invID = ((Integer) hwIDsToConfig.get(i)).intValue();
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
				
				// Send the reenable command if hardware status is unavailable,
				// whether to send the config command is controlled by the AUTOMATIC_CONFIGURATION role property
				if (ServerUtils.isOperator(user) && AuthFuncs.checkRoleProperty( user.getYukonUser(), ConsumerInfoRole.AUTOMATIC_CONFIGURATION )
					|| ServerUtils.isResidentialCustomer(user) && AuthFuncs.checkRoleProperty(user.getYukonUser(), ResidentialCustomerRole.AUTOMATIC_CONFIGURATION))
					YukonSwitchCommandAction.sendConfigCommand(energyCompany, liteHw, false, conn);
				else if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL)
					YukonSwitchCommandAction.sendEnableCommand(energyCompany, liteHw, conn);
				
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
				starsInvs.addStarsInventory( starsInv );
			}
			
			for (int i = 0; i < hwIDsToDisable.size(); i++) {
				int invID = ((Integer) hwIDsToDisable.get(i)).intValue();
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
				
				YukonSwitchCommandAction.sendDisableCommand(energyCompany, liteHw, conn);
				
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
				starsInvs.addStarsInventory( starsInv );
			}
			
			// Log activity
			String logMsg = "Program Enrolled Before:" + ((progEnrBefore != null)? progEnrBefore : "(None)") +
					"; Now:" + ((progEnrNow != null)? progEnrNow : "(Not Enrolled)");
			ActivityLogger.log(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(), liteAcctInfo.getCustomer().getCustomerID(),
					"Program Enrollment", logMsg );
            
            if (user == null) {	// Probably from the sign up wizard?
	            StarsSuccess success = new StarsSuccess();
	            respOper.setStarsSuccess( success );
	            return SOAPUtil.buildSOAPMessage( respOper );
            }
            
			StarsProgramSignUpResponse resp = new StarsProgramSignUpResponse();
			resp.setStarsInventories( starsInvs );
			
			String desc = "Program enrollment updated successfully";
			if (ServerUtils.isOperator( user ))
				desc += ", please check the hardware configuration";
			resp.setDescription( desc );
			
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
				
				starsProgs.addStarsLMProgram( StarsLiteFactory.createStarsLMProgram(liteProg, liteApp, energyCompany) );
				starsProgs.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo.getProgramHistory()) );
			}
			
			ArrayList liteApps = liteAcctInfo.getAppliances();
			StarsAppliances starsApps = new StarsAppliances();
			resp.setStarsAppliances( starsApps );
			
			TreeMap tmap = new TreeMap();
			for (int i = 0; i < liteApps.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
				StarsAppliance starsApp = (StarsAppliance) StarsLiteFactory.createStarsAppliance(liteApp, energyCompany);
				
				ArrayList list = (ArrayList) tmap.get( starsApp.getDescription() );
				if (list == null) {
					list = new ArrayList();
					tmap.put( starsApp.getDescription(), list );
				}
				list.add( starsApp );
			}
			
			Iterator it = tmap.values().iterator();
			while (it.hasNext()) {
				ArrayList list = (ArrayList) it.next();
				for (int i = 0; i < list.size(); i++)
					starsApps.addStarsAppliance( (StarsAppliance) list.get(i) );
			}
			
			respOper.setStarsProgramSignUpResponse( resp );
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
        finally {
        	try {
        		if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
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
				StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
						user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				
				StarsProgramSignUpResponse resp = operation.getStarsProgramSignUpResponse();
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
				
				if (resp.getStarsLMPrograms() != null)
					accountInfo.setStarsLMPrograms( resp.getStarsLMPrograms() );
				
				if (resp.getStarsAppliances() != null && accountInfo.getStarsAppliances() != null)
					accountInfo.setStarsAppliances( resp.getStarsAppliances() );
				
				if (resp.getStarsInventories() != null && accountInfo.getStarsInventories() != null) {
					for (int i = 0; i < resp.getStarsInventories().getStarsInventoryCount(); i++) {
						StarsInventory starsInv = resp.getStarsInventories().getStarsInventory(i);
						
						StarsInventories inventories = accountInfo.getStarsInventories();
						for (int j = 0; j < inventories.getStarsInventoryCount(); j++) {
							StarsInventory inv = inventories.getStarsInventory(j);
							if (inv.getInventoryID() == starsInv.getInventoryID()) {
								inventories.setStarsInventory(j, starsInv);
								break;
							}
						}
					}
				}
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
	
	public static void updateProgramEnrollment(StarsProgramSignUp progSignUp, LiteStarsCustAccountInformation liteAcctInfo,
		Integer invID, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn) throws java.sql.SQLException
	{
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
		
		// Set the termination time a little bit earlier than the signup date
		Date signupDate = new Date();
		Date termDate = new Date( signupDate.getTime() - 1000 );
        
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
					break;
				}
			}
        	
			if (liteApp != null) {
			/* There is an appliance in the same category as the program.
			 * If the appliance isn't enrolled in any program now, assign the program to it
			 * If the appliance is enrolled in some other program, update its program enrollment
			 * If the appliance is enrolled in the same program -- nothing has been changed
			 */
				if (liteApp.getLmProgramID() == 0) {
					// Add "sign up" event to the new program
					event.setEventID( null );
					event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					eventDB.setAccountID( accountID );
					eventDB.setLMProgramID( new Integer(program.getProgramID()) );
					eventBase.setEventTypeID( progEventEntryID );
					eventBase.setActionID( signUpEntryID );
					eventBase.setEventDateTime( signupDate );
					event.setDbConnection( conn );
					event.add();
		            
					LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
					liteAcctInfo.getProgramHistory().add( liteEvent );
					
					// Add the program to the program list of the account
					LiteLMProgram liteProg = energyCompany.getLMProgram( program.getProgramID() );
					LiteStarsLMProgram liteStarsProg = new LiteStarsLMProgram( liteProg );
					liteStarsProg.updateProgramStatus( liteAcctInfo.getProgramHistory() );
					newProgList.add( liteStarsProg );
		            
					if (liteApp.getInventoryID() == 0 && invID != null)
						liteApp.setInventoryID( invID.intValue() );
					if (liteApp.getInventoryID() > 0) {
						int groupID = program.getAddressingGroupID();
						if (groupID == 0 && liteProg.getGroupIDs() != null && liteProg.getGroupIDs().length > 0)
							groupID =  liteProg.getGroupIDs()[0];
						liteApp.setAddressingGroupID( groupID );
						liteStarsProg.setGroupID( groupID );
	                		
						Integer hwID = new Integer( liteApp.getInventoryID() );
						if (!hwIDsToConfig.contains( hwID )) hwIDsToConfig.add( hwID );
					}
    				
					liteApp.setLmProgramID( program.getProgramID() );
					
					com.cannontech.database.data.stars.appliance.ApplianceBase app =
							(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
					app.setDbConnection( conn );
					app.update();
				}
				else if (liteApp.getLmProgramID() != program.getProgramID()) {
					// Add "termination" event to the old program
					ECUtils.removeFutureActivationEvents( liteAcctInfo.getProgramHistory(), liteApp.getLmProgramID(), energyCompany );
    				
					event.setEventID( null );
					event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					eventDB.setAccountID( accountID );
					eventDB.setLMProgramID( new Integer(liteApp.getLmProgramID()) );
					eventBase.setEventTypeID( progEventEntryID );
					eventBase.setActionID( termEntryID );
					eventBase.setEventDateTime( termDate );
					event.setDbConnection( conn );
					event.add();
					
					LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
					liteAcctInfo.getProgramHistory().add( liteEvent );
					
					// Add "sign up" event to the new program
					event.setEventID( null );
					eventDB.setLMProgramID( new Integer(program.getProgramID()) );
					eventBase.setActionID( signUpEntryID );
					eventBase.setEventDateTime( signupDate );
					event.setDbConnection( conn );
					event.add();
					
					liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
					liteAcctInfo.getProgramHistory().add( liteEvent );
					
					// Update the program list of the account
					LiteLMProgram liteProg = energyCompany.getLMProgram( program.getProgramID() );
					LiteStarsLMProgram liteStarsProg = new LiteStarsLMProgram( liteProg );
					liteStarsProg.updateProgramStatus( liteAcctInfo.getProgramHistory() );
					newProgList.add( liteStarsProg );
		            
					if (liteApp.getInventoryID() == 0 && invID != null)
						liteApp.setInventoryID( invID.intValue() );
					if (liteApp.getInventoryID() > 0) {
						int groupID = program.getAddressingGroupID();
						if (groupID == 0 && liteProg.getGroupIDs() != null && liteProg.getGroupIDs().length > 0)
							groupID = liteProg.getGroupIDs()[0];
						liteApp.setAddressingGroupID( groupID );
						liteStarsProg.setGroupID( groupID );
		                
						Integer hwID = new Integer( liteApp.getInventoryID() );
						if (!hwIDsToConfig.contains( hwID )) hwIDsToConfig.add( hwID );
					}
    				
					liteApp.setLmProgramID( program.getProgramID() );
					
					com.cannontech.database.data.stars.appliance.ApplianceBase app =
							(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
					app.setDbConnection( conn );
					app.update();
				}
				else {
					// Just copy the program to the new program list of the account
					LiteStarsLMProgram liteStarsProg = liteAcctInfo.getLMProgram( program.getProgramID() );
					if (liteStarsProg != null)
						newProgList.add( liteStarsProg );
		            
					if (liteApp.getInventoryID() == 0 && invID != null)
						liteApp.setInventoryID( invID.intValue() );
					if (liteApp.getInventoryID() > 0) {
						int groupID = program.getAddressingGroupID();
						if (groupID != 0 && liteStarsProg.getGroupID() != groupID) {
							liteApp.setAddressingGroupID( groupID );
							liteStarsProg.setGroupID( groupID );
			                	
							Integer hwID = new Integer( liteApp.getInventoryID() );
							if (!hwIDsToConfig.contains( hwID )) hwIDsToConfig.add( hwID );
						}
					}
				}
				
				appList.remove( liteApp );
				newAppList.add( liteApp );
			}
			else {
			/* There is no appliance in the same category as the program,
			 * so create a new appliance for the program
			 */
				// Add "sign up" event to the new program
				event.setEventID( null );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				eventDB.setAccountID( accountID );
				eventDB.setLMProgramID( new Integer(program.getProgramID()) );
				eventBase.setEventTypeID( progEventEntryID );
				eventBase.setActionID( signUpEntryID );
				eventBase.setEventDateTime( signupDate );
				event.setDbConnection( conn );
				event.add();
				
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
				liteAcctInfo.getProgramHistory().add( liteEvent );
				
				// Add the program to the program list of the account
				LiteLMProgram liteProg = energyCompany.getLMProgram( program.getProgramID() );
				LiteStarsLMProgram liteStarsProg = new LiteStarsLMProgram( liteProg );
				liteStarsProg.updateProgramStatus( liteAcctInfo.getProgramHistory() );
				newProgList.add( liteStarsProg );
	        	
				com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
				com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
	        	
				appDB.setAccountID( accountID );
				appDB.setApplianceCategoryID( new Integer(program.getApplianceCategoryID()) );
				appDB.setLMProgramID( new Integer(program.getProgramID()) );
				appDB.setLocationID( dftLocationID );
				appDB.setManufacturerID( dftManufacturerID );
	        	
				if (invID != null) {
					if (liteProg.getGroupIDs() != null && liteProg.getGroupIDs().length > 0) {
						int groupID = liteProg.getGroupIDs()[0];
						liteStarsProg.setGroupID( groupID );
		        		
						LMHardwareConfiguration hwConfig = new LMHardwareConfiguration();
						hwConfig.setInventoryID( invID );
						hwConfig.setAddressingGroupID( new Integer(groupID) );
						app.setLMHardwareConfig( hwConfig );
		        		
						if (!hwIDsToConfig.contains( invID )) hwIDsToConfig.add( invID );
					}
				}
	        	
				app.setDbConnection( conn );
				app.add();
	        	
				liteApp = StarsLiteFactory.createLiteStarsAppliance( app, energyCompany );
				newAppList.add( liteApp );
			}
		}
		
		// Remove enrolled program for all the remaining appliances
		for (int i = 0; i < appList.size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) appList.get(i);
    		
			if (liteApp.getLmProgramID() != 0) {
				// Add "termination" event to the old program
				ECUtils.removeFutureActivationEvents( liteAcctInfo.getProgramHistory(), liteApp.getLmProgramID(), energyCompany );
				
				event.setEventID( null );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				eventDB.setAccountID( accountID );
				eventDB.setLMProgramID( new Integer(liteApp.getLmProgramID()) );
				eventBase.setEventTypeID( progEventEntryID );
				eventBase.setActionID( termEntryID );
				eventBase.setEventDateTime( termDate );
				event.setDbConnection( conn );
				event.add();
				
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
				liteAcctInfo.getProgramHistory().add( liteEvent );
				
				if (liteApp.getInventoryID() > 0) {
					Integer hwID = new Integer( liteApp.getInventoryID() );
					if (!hwIDsToDisable.contains( hwID )) hwIDsToDisable.add( hwID );
				}
				
				liteApp.setInventoryID( 0 );
				liteApp.setLmProgramID( 0 );
				liteApp.setAddressingGroupID( 0 );
	    		
				com.cannontech.database.data.stars.appliance.ApplianceBase app =
						(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
				app.setDbConnection( conn );
				app.update();
				
				com.cannontech.database.data.stars.hardware.LMHardwareConfiguration.deleteLMHardwareConfiguration( app.getApplianceBase().getApplianceID() );
			}
    		
			newAppList.add( liteApp );
		}
    	
		liteAcctInfo.setAppliances( newAppList );
		liteAcctInfo.setLmPrograms( newProgList );
	}
	
	/* For every hardware that's out of service, resend a disable command
	 */
	private StarsProgramSignUpResponse resendNotEnrolled(LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo, java.sql.Connection conn)
	throws java.sql.SQLException {
		StarsProgramSignUpResponse resp = new StarsProgramSignUpResponse();
		StarsInventories starsInvs = new StarsInventories();
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			
			try {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
				if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
					YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw, conn );
					starsInvs.addStarsInventory( StarsLiteFactory.createStarsInventory(liteHw, energyCompany) );
				}
			}
			catch (ClassCastException e) {}
		}
		
		resp.setStarsInventories( starsInvs );
    	resp.setDescription( "Not enrolled command has been resent successfully" );
		return resp;
	}

}

package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
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
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
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
			
			String notEnrolled = req.getParameter( "NotEnrolled" );
			if (notEnrolled == null) {
				StarsSULMPrograms programs = new StarsSULMPrograms();
				progSignUp.setStarsSULMPrograms( programs );
				
				String[] catIDs = req.getParameterValues( "CatID" );
				String[] progIDs = req.getParameterValues( "ProgID" );
				if (progIDs != null) {
					for (int i = 0; i < progIDs.length; i++) {
						if (progIDs[i].length() == 0) continue;
						
						SULMProgram program = new SULMProgram();
						program.setProgramID( Integer.parseInt(progIDs[i]) );
						program.setApplianceCategoryID( Integer.parseInt(catIDs[i]) );
						programs.addSULMProgram( program );
					}
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
			if (progSignUp.getAccountNumber() != null) {
				liteAcctInfo = energyCompany.searchAccountByAccountNo( progSignUp.getAccountNumber() );
			}
			else
				liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
			if (progSignUp.getStarsSULMPrograms() == null) {
				// Resend the not enrolled command
				respOper.setStarsProgramSignUpResponse( resendNotEnrolled(energyCompany, liteAcctInfo) );
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
	        
			StarsInventories starsInvs = new StarsInventories();
			
			try {
				ArrayList hwsToConfig = updateProgramEnrollment( progSignUp, liteAcctInfo, null, energyCompany );
				
				// Send out the config/disable command
				for (int i = 0; i < hwsToConfig.size(); i++) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hwsToConfig.get(i);
					boolean toConfig = false;
					
					for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
						if (liteApp.getInventoryID() == liteHw.getInventoryID()) {
							toConfig = true;
							break;
						}
					}
					
					if (toConfig) {
						// Send the reenable command if hardware status is unavailable,
						// whether to send the config command is controlled by the AUTOMATIC_CONFIGURATION role property
						if (ServerUtils.isOperator(user) && AuthFuncs.checkRoleProperty( user.getYukonUser(), ConsumerInfoRole.AUTOMATIC_CONFIGURATION )
							|| ServerUtils.isResidentialCustomer(user) && AuthFuncs.checkRoleProperty(user.getYukonUser(), ResidentialCustomerRole.AUTOMATIC_CONFIGURATION))
							YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, false );
						else if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL)
							YukonSwitchCommandAction.sendEnableCommand( energyCompany, liteHw );
					}
					else {
						// Send disable command to hardware
						YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw );
					}
					
					StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
					starsInvs.addStarsInventory( starsInv );
				}
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
	        
			// Log activity
			String progEnrNow = null;
			for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
				if (progEnrNow == null)
					progEnrNow = liteProg.getLmProgram().getProgramName();
				else
					progEnrNow += ", " + liteProg.getLmProgram().getProgramName();
			}
			
			String logMsg = "Program Enrolled Before:" + ((progEnrBefore != null)? progEnrBefore : "(None)") +
					"; Now:" + ((progEnrNow != null)? progEnrNow : "(Not Enrolled)");
			ActivityLogger.logEvent(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(), liteAcctInfo.getCustomer().getCustomerID(),
					ActivityLogActions.PROGRAM_ENROLLMENT_ACTION, logMsg );
            
			if (user == null) {	// Probably from the sign up wizard?
				StarsSuccess success = new StarsSuccess();
				respOper.setStarsSuccess( success );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			StarsProgramSignUpResponse resp = new StarsProgramSignUpResponse();
			resp.setStarsInventories( starsInvs );
			resp.setStarsLMPrograms( StarsLiteFactory.createStarsLMPrograms(liteAcctInfo, energyCompany) );
			resp.setStarsAppliances( StarsLiteFactory.createStarsAppliances(liteAcctInfo.getAppliances(), energyCompany) );
			resp.setDescription( "Program enrollment updated successfully" );
			
			respOper.setStarsProgramSignUpResponse( resp );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to update the program enrollment") );
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user != null) {
				StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
						session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				
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
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static ArrayList updateProgramEnrollment(StarsProgramSignUp progSignUp, LiteStarsCustAccountInformation liteAcctInfo,
		LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) throws WebClientException
	{
		StarsSULMPrograms programs = progSignUp.getStarsSULMPrograms();
		ArrayList hwsToConfig = new ArrayList();
		
		try {
			ArrayList appCats = energyCompany.getAllApplianceCategories();
			Integer accountID = new Integer( liteAcctInfo.getCustomerAccount().getAccountID() );
			
			Integer dftLocationID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_LOC_UNKNOW).getEntryID() );
			Integer dftManufacturerID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_MANU_UNKNOWN).getEntryID() );
			
			// Set the termination time a little bit earlier than the signup date
			Date signupDate = new Date();
			Date termDate = new Date( signupDate.getTime() - 1000 );
        	
			ArrayList progList = liteAcctInfo.getLmPrograms();	// List of old programs
			ArrayList appList = liteAcctInfo.getAppliances();	// List of old appliances
			ArrayList newAppList = new ArrayList();		// List of new appliances
			ArrayList newProgList = new ArrayList();	// List of new programs
			ArrayList progEnrollList = new ArrayList();	// List of program IDs to be enrolled in
			ArrayList progUnenrollList = new ArrayList();	// List of program IDs to be unenrolled
        	
			for (int i = 0; i < programs.getSULMProgramCount(); i++) {
				SULMProgram program = programs.getSULMProgram(i);
				
				if (!program.hasApplianceCategoryID()) {
					// If ApplianceCategoryID is not provided, set it here
					synchronized (appCats) {
						for (int j = 0; j < appCats.size(); j++) {
							LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
							for (int k = 0; k < liteAppCat.getPublishedPrograms().length; k++) {
								if (liteAppCat.getPublishedPrograms()[k].getProgramID() == program.getProgramID()) {
									program.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
									break;
								}
							}
							if (program.hasApplianceCategoryID()) break;
						}
					}
				}
				
				// Find which hardware(s) is assigned to this program
				int[] invIDs = new int[1];
				invIDs[0] = 0;
        		
				if (liteInv != null) {
					// Update program enrollment for the specifed hardware only
					invIDs[0] = liteInv.getInventoryID();
				}
				else if (program.getInventoryID() != null) {
					// Inventory ID(s) is specified in the request
					String[] invIDStr = program.getInventoryID().split(",");
					invIDs = new int[ invIDStr.length ];
					for (int j = 0; j < invIDStr.length; j++)
						invIDs[j] = Integer.parseInt( invIDStr[j] );
				}
				else {
					// Find hardwares already assigned to this program or another program in the same category
					ArrayList hwsAssigned = new ArrayList();
					for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
						LiteStarsAppliance lApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
						if (lApp.getInventoryID() > 0 &&
							(lApp.getLmProgramID() == program.getProgramID() || lApp.getApplianceCategoryID() == program.getApplianceCategoryID()))
						{
							hwsAssigned.add( new Integer(lApp.getInventoryID()) );
						}
					}
        			
					if (hwsAssigned.size() > 0) {
						invIDs = new int[ hwsAssigned.size() ];
						for (int j = 0; j < hwsAssigned.size(); j++)
							invIDs[j] = ((Integer) hwsAssigned.get(j)).intValue();
					}
        			
					// If all of the above failed, then use the first hardware, if any
					if (invIDs[0] == 0 && liteAcctInfo.getInventories().size() > 0)
						invIDs[0] = ((Integer) liteAcctInfo.getInventories().get(0)).intValue();
				}
        		
				// Add the program to the new program list
				LiteLMProgram liteProg = energyCompany.getLMProgram( program.getProgramID() );
				LiteStarsLMProgram liteStarsProg = new LiteStarsLMProgram( liteProg );
				newProgList.add( liteStarsProg );
			    
				int groupID = program.getAddressingGroupID();
				if (!program.hasAddressingGroupID() && liteProg.getGroupIDs() != null && liteProg.getGroupIDs().length > 0)
					groupID = liteProg.getGroupIDs()[0];
				liteStarsProg.setGroupID( groupID );
        		
				for (int idx = 0; idx < invIDs.length; idx++) {
					LiteStarsLMHardware liteHw = null;
					if (invIDs[idx] > 0)
						liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invIDs[idx], true );
        			
					// Find appliance attached to this hardware or not attached to any hardware, and assigned to this program or another program of the same category.
					// Priority: same hardware & same program > same hardware & same category > no hardware & same program > no hardware & same category
					LiteStarsAppliance liteApp = null;
					for (int j = 0; j < appList.size(); j++) {
						LiteStarsAppliance lApp = (LiteStarsAppliance) appList.get(j);
						
						if ((lApp.getInventoryID() == invIDs[idx] || lApp.getInventoryID() == 0) && 
							(lApp.getLmProgramID() == program.getProgramID() || lApp.getApplianceCategoryID() == program.getApplianceCategoryID()))
						{
							if (liteApp == null) {
								liteApp = lApp;
							}
							else if (liteApp.getInventoryID() == 0) {
								if (lApp.getInventoryID() > 0 || lApp.getLmProgramID() == program.getProgramID())
									liteApp = lApp;
							}
							else {
								if (lApp.getInventoryID() > 0 && lApp.getLmProgramID() == program.getProgramID())
									liteApp = lApp;
							}
						}
					}
	        		
					if (liteApp != null) {
						liteApp.setInventoryID( invIDs[idx] );
						
						// If the appliance is enrolled in some other program, update its program enrollment
						if (liteApp.getLmProgramID() != program.getProgramID()) {
							// The appliance isn't enrolled in any program now, assign the program to it
							Integer newProgID = new Integer( program.getProgramID() );
							if (!progEnrollList.contains(newProgID)) progEnrollList.add( newProgID );
							
							if (liteApp.getLmProgramID() != 0) {
								Integer oldProgID = new Integer( liteApp.getLmProgramID() );
								if (!progUnenrollList.contains(oldProgID)) progUnenrollList.add( oldProgID );
							}
			            	
							if (invIDs[idx] > 0) {
								if (liteApp.getAddressingGroupID() != groupID && !hwsToConfig.contains( liteHw ))
									hwsToConfig.add( liteHw );
								liteApp.setAddressingGroupID( groupID );
							}
							
							liteApp.setLmProgramID( program.getProgramID() );
							
							com.cannontech.database.data.stars.appliance.ApplianceBase app =
									(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
							app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
									Transaction.createTransaction( Transaction.UPDATE, app ).execute();
						}
						else {
							// The appliance is enrolled in the same program, update the addressing group if necessary.
							// If liteInv is not null, it's from the import program or hardware configuration page;
							// in the later case, update the group of all loads assigned to this program if necessary.
							if (invIDs[idx] > 0 && program.hasAddressingGroupID() && liteApp.getAddressingGroupID() != groupID) {
								liteApp.setAddressingGroupID( groupID );
								if (!hwsToConfig.contains( liteHw ))
									hwsToConfig.add( liteHw );
								
								if (liteInv != null) {
									for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
										LiteStarsAppliance lApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
										if (lApp.getLmProgramID() == program.getProgramID() && lApp.getInventoryID() > 0 && !lApp.equals(liteApp)) {
											lApp.setAddressingGroupID( groupID );
											
											LiteStarsLMHardware lHw = (LiteStarsLMHardware) energyCompany.getInventory( lApp.getInventoryID(), true );
											if (!hwsToConfig.contains( lHw ))
												hwsToConfig.add( lHw );
										}
									}
								}
							}
						}
						
						appList.remove( liteApp );
						newAppList.add( liteApp );
					}
					else {
						Integer progID = new Integer( program.getProgramID() );
						if (!progEnrollList.contains(progID)) progEnrollList.add( progID );
		        		
						// Create a new appliance for the program
						com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
						com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
		        		
						appDB.setAccountID( accountID );
						appDB.setApplianceCategoryID( new Integer(program.getApplianceCategoryID()) );
						appDB.setLMProgramID( progID );
						appDB.setLocationID( dftLocationID );
						appDB.setManufacturerID( dftManufacturerID );
		        		
						if (invIDs[idx] > 0 && groupID > 0) {
							LMHardwareConfiguration hwConfig = new LMHardwareConfiguration();
							hwConfig.setInventoryID( new Integer(invIDs[idx]) );
							hwConfig.setAddressingGroupID( new Integer(groupID) );
							app.setLMHardwareConfig( hwConfig );
		        			
							if (!hwsToConfig.contains( liteHw ))
								hwsToConfig.add( liteHw );
						}
		        		
						app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
								Transaction.createTransaction( Transaction.INSERT, app ).execute();
		        		
						liteApp = StarsLiteFactory.createLiteStarsAppliance( app, energyCompany );
						newAppList.add( liteApp );
					}
				}
			}
			
			// Remove enrolled programs for all the remaining appliances
			// (if liteInv is not null, only remove programs assigned to the specified hardware)
			for (int i = 0; i < appList.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) appList.get(i);
    			
				if (liteApp.getLmProgramID() != 0) {
					Integer progID = new Integer( liteApp.getLmProgramID() );
					
					if (liteInv == null || liteApp.getInventoryID() == liteInv.getInventoryID()) {
						if (!progUnenrollList.contains(progID)) progUnenrollList.add( progID );
						
						if (liteApp.getInventoryID() > 0) {
							LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( liteApp.getInventoryID(), true );
							if (!hwsToConfig.contains( liteHw ))
								hwsToConfig.add( liteHw );
						}
						
						liteApp.setInventoryID( 0 );
						liteApp.setLmProgramID( 0 );
						liteApp.setAddressingGroupID( 0 );
		    			
						com.cannontech.database.data.stars.appliance.ApplianceBase app =
								(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
						app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
								Transaction.createTransaction( Transaction.UPDATE, app ).execute();
						
						com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.deleteLMHardwareConfiguration( app.getApplianceBase().getApplianceID() );
					}
					else {
						boolean progInNewList = false;
						for (int j = 0; j < newProgList.size(); j++) {
							LiteStarsLMProgram liteStarsProg = (LiteStarsLMProgram) newProgList.get(j);
							if (liteStarsProg.getLmProgram().getProgramID() == progID.intValue()) {
								progInNewList = true;
								break;
							}
						}
						
						if (!progInNewList) {
							for (int j = 0; j < progList.size(); j++) {
								LiteStarsLMProgram liteStarsProg = (LiteStarsLMProgram) progList.get(j);
								if (liteStarsProg.getLmProgram().getProgramID() == progID.intValue()) {
									newProgList.add( liteStarsProg );
									break;
								}
							}
						}
					}
				}
    			
				newAppList.add( liteApp );
			}
			
			// Remove redundant program IDs in the enroll and unenroll list
			Iterator it = progEnrollList.iterator();
			while (it.hasNext()) {
				int progID = ((Integer) it.next()).intValue();
				for (int i = 0; i < progList.size(); i++) {
					if (((LiteStarsLMProgram) progList.get(i)).getLmProgram().getProgramID() == progID) {
						it.remove();
						break;
					}
				}
			}
			
			it = progUnenrollList.iterator();
			while (it.hasNext()) {
				int progID = ((Integer) it.next()).intValue();
				for (int i = 0; i < newProgList.size(); i++) {
					if (((LiteStarsLMProgram) newProgList.get(i)).getLmProgram().getProgramID() == progID) {
						it.remove();
						break;
					}
				}
			}
			
			// Get action & event type IDs
			Integer progEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID() );
			Integer signUpEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP).getEntryID() );
			Integer termEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID() );
			
			com.cannontech.database.data.stars.event.LMProgramEvent event =
					new com.cannontech.database.data.stars.event.LMProgramEvent();
			com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
			
			// Add "sign up" event to the programs to be enrolled in
			for (int i = 0; i < progEnrollList.size(); i++) {
				event.setEventID( null );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				eventDB.setAccountID( accountID );
				eventDB.setLMProgramID( (Integer)progEnrollList.get(i) );
				eventBase.setEventTypeID( progEventEntryID );
				eventBase.setActionID( signUpEntryID );
				eventBase.setEventDateTime( signupDate );
				
				event = (com.cannontech.database.data.stars.event.LMProgramEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
				liteAcctInfo.getProgramHistory().add( liteEvent );
			}
			
			// Add "termination" event to the old program
			for (int i = 0; i < progUnenrollList.size(); i++) {
				Integer progID = (Integer) progUnenrollList.get(i);
				ECUtils.removeFutureActivationEvents( liteAcctInfo.getProgramHistory(), progID.intValue(), energyCompany );
	    		
				event.setEventID( null );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				eventDB.setAccountID( accountID );
				eventDB.setLMProgramID( progID );
				eventBase.setEventTypeID( progEventEntryID );
				eventBase.setActionID( termEntryID );
				eventBase.setEventDateTime( termDate );
				
				event = (com.cannontech.database.data.stars.event.LMProgramEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
				liteAcctInfo.getProgramHistory().add( liteEvent );
			}
			
			// Update program status
			for (int i = 0; i < newProgList.size(); i++) {
				LiteStarsLMProgram liteStarsProg = (LiteStarsLMProgram) newProgList.get(i);
				liteStarsProg.updateProgramStatus( liteAcctInfo.getProgramHistory() );
			}
    		
			liteAcctInfo.setAppliances( newAppList );
			liteAcctInfo.setLmPrograms( newProgList );
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to update the program enrollment" );
		}
		
		return hwsToConfig;
	}
	
	public static SOAPMessage setAdditionalEnrollmentInfo(SOAPMessage msg, HttpServletRequest req) throws Exception {
		StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( msg );
		StarsSULMPrograms programs = operation.getStarsProgramSignUp().getStarsSULMPrograms();
		
		String[] progIDs = req.getParameterValues( "ProgID" );
		String[] groupIDs = req.getParameterValues( "GroupID" );
		String[] invIDs = req.getParameterValues( "InvIDs" );
		
		for (int i = 0; i < progIDs.length; i++) {
			int progID = Integer.parseInt( progIDs[i] );
			int groupID = Integer.parseInt( groupIDs[i] );
			
			for (int j = 0; j < programs.getSULMProgramCount(); j++) {
				if (programs.getSULMProgram(j).getProgramID() == progID) {
					programs.getSULMProgram(j).setAddressingGroupID( groupID );
					programs.getSULMProgram(j).setInventoryID( invIDs[i] );
					break;
				}
			}
		}
		
		return SOAPUtil.buildSOAPMessage( operation );
	}
	
	/* For every hardware that's out of service, resend a disable command
	 */
	private StarsProgramSignUpResponse resendNotEnrolled(LiteStarsEnergyCompany energyCompany, LiteStarsCustAccountInformation liteAcctInfo)
		throws WebClientException
	{
		StarsProgramSignUpResponse resp = new StarsProgramSignUpResponse();
		StarsInventories starsInvs = new StarsInventories();
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			
			try {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
				if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
					YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw );
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

package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.LMHardwareConfiguration;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
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
import com.cannontech.stars.util.InventoryUtils;

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
                LiteYukonUser liteUser = (LiteYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
                boolean multiProgramSelect = StarsUtils.isResidentialCustomer(liteUser) && DaoFactory.getAuthDao().checkRoleProperty(liteUser, ResidentialCustomerRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY) 
                            || StarsUtils.isOperator(liteUser) && true; //TODO DaoFactory.getAuthDao().checkRoleProperty( liteUser, ConsumerInfoRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY )
                
                StarsSULMPrograms programs = new StarsSULMPrograms();
				progSignUp.setStarsSULMPrograms( programs );
				
				String[] catIDs = req.getParameterValues( "CatID" );
				String[] progIDs = req.getParameterValues( "ProgID" );
                String[] allProgIDs = req.getParameterValues( "AllProgID" );
				if (progIDs != null) {
					for (int i = 0; i < progIDs.length; i++) 
                    {
						if (progIDs[i].length() == 0) continue;
						
						SULMProgram program = new SULMProgram();
						program.setProgramID( Integer.parseInt(progIDs[i]) );
                        if(multiProgramSelect && allProgIDs != null && catIDs.length != progIDs.length) {
                            for (int j = 0; j < allProgIDs.length; j++) {
                                if(allProgIDs[j].compareTo(progIDs[i]) == 0) {
                                    program.setApplianceCategoryID( Integer.parseInt(catIDs[j]) );
                                    break;
                                }
                            }
                        }
                        else
                            program.setApplianceCategoryID( Integer.parseInt(catIDs[i]) );
                        String notOperator = req.getParameter("notOperator");
                        if(notOperator != null && notOperator.compareTo("true") == 0)
                        {
                            /*Going to need to do some guesswork since consumers aren't allowed
                             * to choose load groups.  At this point, we will need to require
                             * that the switch or stat has been configured or enrolled previously from the
                             * operator side.  If it has not, there may not be an entry in the configuration tables
                             */
                            program.setAddressingGroupID(ServerUtils.ADDRESSING_GROUP_NOT_FOUND);
                               
                        }
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
            LiteYukonUser liteUser = (LiteYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
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
            
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
            
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
			for (int i = 0; i < liteAcctInfo.getPrograms().size(); i++) 
            {
				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getPrograms().get(i);
				String progName = StarsUtils.getPublishedProgramName( liteProg.getPublishedProgram() );
				if (progEnrBefore == null)
					progEnrBefore = progName;
				else
					progEnrBefore += ", " + progName;
			}
			
            /*Going to need to do some guesswork since consumers aren't allowed
             * to choose load groups. 
             * --If the program has more than one group, we will take the first one in the list.  Could be
             * A DANGEROUS ASSUMPTION.  TODO: Track groups better.
             * --At this point, we will need to require that the switch or stat has been configured or enrolled 
             * previously from the operator side.  If it has not, there may not be a groupID set.
             */
            
            for(int j = 0; j < progSignUp.getStarsSULMPrograms().getSULMProgramCount(); j++)
            {
                if(progSignUp.getStarsSULMPrograms().getSULMProgram(j).getAddressingGroupID() == ServerUtils.ADDRESSING_GROUP_NOT_FOUND)
                {
                    int progID = progSignUp.getStarsSULMPrograms().getSULMProgram(j).getProgramID();
                    LiteLMProgramWebPublishing webProg = energyCompany.getProgram(progID);
                    int grpID = webProg.getGroupIDs()[0];
                    if(grpID > 0)
                    {
                        progSignUp.getStarsSULMPrograms().getSULMProgram(j).setAddressingGroupID(grpID);
                    }
                    else
                    {
                        throw new WebClientException("Program not defined correctly.  Contact your administrator.");
                    }
                }
            }
            
			String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
			boolean useHardwareAddressing = (trackHwAddr != null) && Boolean.valueOf(trackHwAddr).booleanValue();
	        
			StarsInventories starsInvs = new StarsInventories();
			
			try {
				List<LiteStarsLMHardware> hwsToConfig = updateProgramEnrollment( progSignUp, liteAcctInfo, null, energyCompany, liteUser );
				
				// Send out the config/disable command
				for (int i = 0; i < hwsToConfig.size(); i++) {
					LiteStarsLMHardware liteHw = hwsToConfig.get(i);
					boolean toConfig = UpdateLMHardwareConfigAction.isToConfig( liteHw, liteAcctInfo );
					
					if (toConfig) {
						// Send the reenable command if hardware status is unavailable,
						// whether to send the config command is controlled by the AUTOMATIC_CONFIGURATION role property
						if (!useHardwareAddressing
							&& (StarsUtils.isOperator(user.getYukonUser()) && DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), ConsumerInfoRole.AUTOMATIC_CONFIGURATION )
								|| StarsUtils.isResidentialCustomer(user.getYukonUser()) && DaoFactory.getAuthDao().checkRoleProperty(user.getYukonUser(), ResidentialCustomerRole.AUTOMATIC_CONFIGURATION))) {
							YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, false, null );
                        }
						else if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
							YukonSwitchCommandAction.sendEnableCommand( energyCompany, liteHw, null );
                        }
					}
					else {
						// Send disable command to hardware
						YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw, null );
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
			for (int i = 0; i < liteAcctInfo.getPrograms().size(); i++) {
				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getPrograms().get(i);
				String progName = StarsUtils.getPublishedProgramName( liteProg.getPublishedProgram() );
				if (progEnrNow == null)
					progEnrNow = progName;
				else
					progEnrNow += ", " + progName;
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
			
			String desc = "Program enrollment updated successfully.";
			if (useHardwareAddressing)
				desc += " Please go to the hardware configuration page and update the addressing information.";
			resp.setDescription( desc );
			
			respOper.setStarsProgramSignUpResponse( resp );
            
            EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, liteAcctInfo.getAccountID(), session);
            
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to update the program enrollment.") );
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

			StarsProgramSignUpResponse resp = operation.getStarsProgramSignUpResponse();
			if (resp == null) {
				StarsFailure failure = operation.getStarsFailure();
				if (failure != null) {
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
					return failure.getStatusCode();
				}
				else
					return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user != null) {
				StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
						session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				
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
	
	public static List<LiteStarsLMHardware> updateProgramEnrollment(StarsProgramSignUp progSignUp, LiteStarsCustAccountInformation liteAcctInfo,
		LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany, LiteYukonUser currentUser) throws WebClientException
	{
		StarsSULMPrograms programs = progSignUp.getStarsSULMPrograms();
		ArrayList hwsToConfig = new ArrayList();
		
        /*
         * New enrollment, opt out, and control history tracking
         *-------------------------------------------------------------------------------
         */
        LMHardwareControlInformationService lmHardwareControlInformationService = (LMHardwareControlInformationService) YukonSpringHook.getBean("lmHardwareControlInformationService");
        List<int[]> hwInfoToEnroll = new ArrayList<int[]>(3);
        List<int[]> hwInfoToUnenroll = new ArrayList<int[]>(3);
        final int INV = 0;
        final int ACCT = 1;
        final int GROUP = 2;
        final int RELAY = 3;
        //final int APPCAT = 4;
        //final int PROG = 5;
        /*-------------------------------------------------------------------------------
         * */
        
		try {
            List<LiteApplianceCategory> appCats = energyCompany.getAllApplianceCategories();
			Integer accountID = new Integer( liteAcctInfo.getCustomerAccount().getAccountID() );
			
			Integer dftLocationID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_LOC_UNKNOWN).getEntryID() );
			Integer dftManufacturerID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_MANU_UNKNOWN).getEntryID() );
			
			// Set the termination time a little bit earlier than the signup date
			Date signupDate = new Date();
			Date termDate = new Date( signupDate.getTime() - 1000 );
        	
            List<LiteStarsLMProgram> progList = liteAcctInfo.getPrograms();	// List of old programs
			ArrayList appList = new ArrayList( liteAcctInfo.getAppliances() );	// List of old appliances
			ArrayList newAppList = new ArrayList();		// List of new appliances
			ArrayList newProgList = new ArrayList();	// List of new programs
			ArrayList progNewEnrollList = new ArrayList();	// List of program IDs newly enrolled in
			ArrayList progUnenrollList = new ArrayList();	// List of program IDs to be unenrolled
			
			// The StarsSULMPrograms object after the omitted fields have been filled in
			StarsSULMPrograms processedPrograms = new StarsSULMPrograms();
			
			// Map from Integer(programID) to Hashtable(Map from Integer(inventoryID) to LiteStarsAppliance)
			Hashtable progHwAppMap = new Hashtable();
			
			for (int i = 0; i < programs.getSULMProgramCount(); i++) {
				SULMProgram program = programs.getSULMProgram(i);
				processedPrograms.addSULMProgram( program );
				
				// If ApplianceCategoryID is not provided, set it here
				if (!program.hasApplianceCategoryID()) {
					LiteLMProgramWebPublishing liteProg = energyCompany.getProgram( program.getProgramID() );
					program.setApplianceCategoryID( liteProg.getApplianceCategoryID() );
				}
				
				Hashtable hwAppMap = (Hashtable) progHwAppMap.get( new Integer(program.getProgramID()) );
				if (hwAppMap == null) {
					hwAppMap = new Hashtable();
					progHwAppMap.put( new Integer(program.getProgramID()), hwAppMap );
				}
        		
				if (liteInv != null) {
					// Update program enrollment for the specifed hardware only
					program.setInventoryID( liteInv.getInventoryID() );
				}
				else if (!program.hasInventoryID()) {
					// Find hardwares already assigned to this program or another program in the same category
					Iterator it = appList.iterator();
					while (it.hasNext()) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) it.next();
						
						if (liteApp.getInventoryID() > 0 &&
							(liteApp.getProgramID() == program.getProgramID() || liteApp.getApplianceCategoryID() == program.getApplianceCategoryID()))
						{
							if (!program.hasInventoryID()) {
								if (!program.hasAddressingGroupID() && liteApp.getProgramID() == program.getProgramID())
									program.setAddressingGroupID( liteApp.getAddressingGroupID() );
								program.setInventoryID( liteApp.getInventoryID() );
								program.setLoadNumber( liteApp.getLoadNumber() );
							}
							else {
								SULMProgram prog = new SULMProgram();
								prog.setProgramID( program.getProgramID() );
								prog.setApplianceCategoryID( program.getApplianceCategoryID() );
								prog.setInventoryID( liteApp.getInventoryID() );
								prog.setLoadNumber( liteApp.getLoadNumber() );
								if (program.hasAddressingGroupID())
									prog.setAddressingGroupID( program.getAddressingGroupID() );
								processedPrograms.addSULMProgram( prog );
							}
							
							hwAppMap.put( new Integer(liteApp.getInventoryID()), liteApp );
							newAppList.add( liteApp );
							it.remove();
						}
					}
        			
					// If no hardware found above, then assign all hardwares
					if (!program.hasInventoryID()) {
						for (int j = 0; j < liteAcctInfo.getInventories().size(); j++) {
							int invID = ((Integer) liteAcctInfo.getInventories().get(j)).intValue();
							if (energyCompany.getInventory(invID, true) instanceof LiteStarsLMHardware) {
								if (!program.hasInventoryID()) {
									program.setInventoryID( invID );
								}
								else {
									SULMProgram prog = new SULMProgram();
									prog.setProgramID( program.getProgramID() );
									prog.setApplianceCategoryID( program.getApplianceCategoryID() );
									prog.setInventoryID( invID );
									if (program.hasAddressingGroupID())
										prog.setAddressingGroupID( program.getAddressingGroupID() );
									if (program.hasLoadNumber())
										prog.setLoadNumber( program.getLoadNumber() );
									processedPrograms.addSULMProgram( prog );
								}
							}
						}
					}
				}
				
				// Try to find the appliance controlled by this program and complete the request
				for (int j = 0; j < appList.size(); j++) {
					LiteStarsAppliance liteApp = (LiteStarsAppliance) appList.get(j);
					if (liteApp.getProgramID() == program.getProgramID() && liteApp.getInventoryID() == program.getInventoryID()) {
						if (!program.hasAddressingGroupID())
							program.setAddressingGroupID( liteApp.getAddressingGroupID() );
						if (!program.hasLoadNumber())
							program.setLoadNumber( liteApp.getLoadNumber() );
						
						hwAppMap.put( new Integer(liteApp.getInventoryID()), liteApp );
						newAppList.add( liteApp );
						appList.remove( liteApp );
						break;
					}
				}
			}
			
			/* Remove unenrolled programs and free up appliances attached to them first,
			 * so they can be reused by the new enrolled programs.
			 * If liteInv is not null, only remove programs assigned to the specified hardware.
			 */
			ArrayList appsToUpdate = new ArrayList();
			
			for (int i = 0; i < appList.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) appList.get(i);
				newAppList.add( liteApp );
    			
				if (liteApp.getProgramID() == 0) continue;
				
				if (liteInv == null || liteApp.getInventoryID() == liteInv.getInventoryID()) {
					Integer progID = new Integer( liteApp.getProgramID() );
					if (!progUnenrollList.contains( progID ))
						progUnenrollList.add( progID );
					
					if (liteApp.getInventoryID() > 0) {
						LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( liteApp.getInventoryID(), true );
						if (!hwsToConfig.contains( liteHw )) 
							hwsToConfig.add( liteHw );
                            
                        /* New enrollment, opt out, and control history tracking
                         *-------------------------------------------------------------------------------
                         * here we catch hardware that are ONLY be unenrolled.  If they are simply being enrolled in a different
                         * program, then the service's startEnrollment will handle the appropriate un-enrollments.
                         */
                        int[] currentUnenrollmentInfo = new int[6];
                        currentUnenrollmentInfo[INV] = liteHw.getInventoryID();
                        currentUnenrollmentInfo[ACCT] = liteHw.getAccountID();
                        currentUnenrollmentInfo[GROUP] = liteApp.getAddressingGroupID();
                        if(currentUnenrollmentInfo[GROUP] == 0)
                            currentUnenrollmentInfo[GROUP] = InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(liteApp.getProgramID());
                        currentUnenrollmentInfo[RELAY] = liteApp.getLoadNumber();
                        //currentUnenrollmentInfo[APPCAT] = liteApp.getApplianceCategoryID();
                        //currentUnenrollmentInfo[PROG] = liteApp.getProgramID();
                        hwInfoToUnenroll.add(currentUnenrollmentInfo);
                        /*-------------------------------------------------------------------------------*/
					}
					
					liteApp.setInventoryID( 0 );
					liteApp.setProgramID( 0 );
					liteApp.setAddressingGroupID( 0 );
					liteApp.setLoadNumber( 0 );
					
					// Save the appliance to update it in the database later
					appsToUpdate.add( liteApp );
				}
				else {
					LiteStarsLMProgram liteStarsProg = getLMProgram( liteAcctInfo, liteApp.getProgramID() );
					if (!newProgList.contains( liteStarsProg ))
						newProgList.add( liteStarsProg );
				}
			}
			
			String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
			boolean useHardwareAddressing = (trackHwAddr != null) && Boolean.valueOf(trackHwAddr).booleanValue();
        	
			for (int i = 0; i < processedPrograms.getSULMProgramCount(); i++) {
				SULMProgram program = processedPrograms.getSULMProgram(i);
				
				LiteLMProgramWebPublishing liteProg = energyCompany.getProgram( program.getProgramID() );
				StarsEnrLMProgram starsProg = ServletUtils.getEnrollmentProgram(
						energyCompany.getStarsEnrollmentPrograms(), program.getProgramID() );
				
				// Add the program to the new program list
				LiteStarsLMProgram liteStarsProg = getLMProgram( liteAcctInfo, program.getProgramID() );
				if (liteStarsProg == null) {
					for (int j = 0; j < newProgList.size(); j++) {
						LiteStarsLMProgram prog = (LiteStarsLMProgram) newProgList.get(j);
						if (prog.getProgramID() == program.getProgramID()) {
							liteStarsProg = prog;
							break;
						}
					}
					
					if (liteStarsProg == null)
						liteStarsProg = new LiteStarsLMProgram( liteProg );
				}
				if (!newProgList.contains( liteStarsProg ))
					newProgList.add( liteStarsProg );
			    
				int groupID = program.getAddressingGroupID();
                int relay = program.getLoadNumber();
				if (!program.hasAddressingGroupID() && !useHardwareAddressing && starsProg.getAddressingGroupCount() > 1)
					groupID = starsProg.getAddressingGroup(1).getEntryID();
                if(groupID == 0) {
                    groupID = InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(program.getProgramID());
                }
				liteStarsProg.setGroupID( groupID );
        		
				LiteStarsLMHardware liteHw = null;
				if (program.getInventoryID() > 0)
					liteHw = (LiteStarsLMHardware) energyCompany.getInventory( program.getInventoryID(), true );
    			
    			LiteStarsAppliance liteApp = (LiteStarsAppliance)
    					(((Hashtable) progHwAppMap.get(new Integer(program.getProgramID()))).get( new Integer(program.getInventoryID()) ));
    			
    			if (liteApp == null) {
					// Find existing appliance that could be attached to this hardware controlling this program
					// Priority: same hardware & same program > same hardware & same category > no hardware & same program > no hardware & same category
					for (int j = 0; j < appList.size(); j++) {
						LiteStarsAppliance lApp = (LiteStarsAppliance) appList.get(j);
						
						if ((lApp.getInventoryID() == program.getInventoryID() || lApp.getInventoryID() == 0) && 
							(lApp.getProgramID() == program.getProgramID() || lApp.getApplianceCategoryID() == program.getApplianceCategoryID()))
						{
							if (liteApp == null) {
								liteApp = lApp;
							}
							else if (liteApp.getInventoryID() == 0) {
								if (lApp.getInventoryID() > 0 || lApp.getProgramID() == program.getProgramID())
									liteApp = lApp;
							}
							else {
								if (lApp.getInventoryID() > 0 && lApp.getProgramID() == program.getProgramID())
									liteApp = lApp;
							}
						}
					}
					
					// We only need to update the database once for this appliance
					if (liteApp != null && appsToUpdate.contains( liteApp ))
						appsToUpdate.remove( liteApp );
    			}
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app = null;
				
				if (liteApp != null) {
					liteApp.setInventoryID( program.getInventoryID() );
					int oldApplianceRelay = liteApp.getLoadNumber();
                    int oldLoadGroupId = liteApp.getAddressingGroupID();
                    liteApp.setLoadNumber( relay );
					
                    //the appliance is on a different program then the current, this is an enrollment switch
					if (liteApp.getProgramID() != program.getProgramID()) {
						// If the appliance is enrolled in another program, update its program enrollment
						Integer newProgID = new Integer( program.getProgramID() );
						if (!progNewEnrollList.contains( newProgID ))
							progNewEnrollList.add( newProgID );
                        
						if (liteApp.getProgramID() != 0) {
							Integer oldProgID = new Integer( liteApp.getProgramID() );
                            
							if (!progUnenrollList.contains( oldProgID ))
								progUnenrollList.add( oldProgID );
                            
						}
		            	
						if (liteHw != null) {
							if ((liteApp.getAddressingGroupID() != groupID || groupID == 0) && !hwsToConfig.contains( liteHw )) 
								hwsToConfig.add( liteHw );
                                
                            /* New enrollment, opt out, and control history tracking
                             * TODO Refactor this
                             *-------------------------------------------------------------------------------
                             */
                            int[] currentEnrollmentInformation = new int[4];
                            currentEnrollmentInformation[INV] = liteHw.getInventoryID();
                            currentEnrollmentInformation[ACCT] = liteHw.getAccountID();
                            currentEnrollmentInformation[GROUP] = groupID;
                            currentEnrollmentInformation[RELAY] = relay;
                            hwInfoToEnroll.add(currentEnrollmentInformation);
                            /*
                             * TODO: What about different relays?  Are we handling this correctly?
                             */
                            if(program.getApplianceCategoryID() == liteApp.getApplianceCategoryID() && 
                                    program.getLoadNumber() == oldApplianceRelay &&
                                    oldLoadGroupId != 0) {
                                int[] currentUnenrollmentInformation = new int[4];
                                currentUnenrollmentInformation[INV] = liteHw.getInventoryID();
                                currentUnenrollmentInformation[ACCT] = liteHw.getAccountID();
                                currentUnenrollmentInformation[GROUP] = oldLoadGroupId;
                                currentUnenrollmentInformation[RELAY] = oldApplianceRelay;
                                hwInfoToUnenroll.add(currentUnenrollmentInformation);
                            }
                            /*
                             * here we catch hardware that are ONLY being unenrolled.  If they are simply being enrolled in a different
                             * program, then the service's startEnrollment will handle the appropriate un-enrollments and we don't need
                             * to specifically do a stopEnrollment.
                             */
                            /*for(int[] potentialUnenroll : hwInfoToUnenroll) {
                                if(currentEnrollmentInformation[INV] == potentialUnenroll[INV] &&
                                        currentEnrollmentInformation[ACCT] == potentialUnenroll[ACCT] &&
                                        currentEnrollmentInformation[GROUP] == potentialUnenroll[GROUP] &&
                                        currentEnrollmentInformation[RELAY] == potentialUnenroll[RELAY]) {
                                    hwInfoToUnenroll.remove(potentialUnenroll);
                                }
                            }*/
                            /*-------------------------------------------------------------------------------*/
                            
                            liteApp.setAddressingGroupID( groupID );
						}
						else
							liteApp.setAddressingGroupID( 0 );
						
						liteApp.setProgramID( program.getProgramID() );
						
						app = (com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
						app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
								Transaction.createTransaction( Transaction.UPDATE, app ).execute();
					}
					else {
						/* The appliance is enrolled in the same program, update the load group if necessary.
						 * If liteInv is not null, it's from the import program or hardware configuration page;
						 * in the later case, update the group of all loads assigned to this program if necessary.
						 */
						if (liteHw != null && program.hasAddressingGroupID() && liteApp.getAddressingGroupID() != groupID) {
							liteApp.setAddressingGroupID( groupID );
							if (!hwsToConfig.contains( liteHw )) 
								hwsToConfig.add( liteHw );
                            
                            /* New enrollment, opt out, and control history tracking
                             * TODO Refactor this
                             *-------------------------------------------------------------------------------
                             */
                            int[] currentEnrollmentInformation = new int[6];
                            currentEnrollmentInformation[INV] = liteHw.getInventoryID();
                            currentEnrollmentInformation[ACCT] = liteHw.getAccountID();
                            currentEnrollmentInformation[GROUP] = groupID;
                            currentEnrollmentInformation[RELAY] = relay;
                            hwInfoToEnroll.add(currentEnrollmentInformation);
                            /*
                             * here we catch hardware that are ONLY being unenrolled.  If they are simply being enrolled in a different
                             * program, then the service's startEnrollment will handle the appropriate un-enrollments and we don't need
                             * to specifically do a stopEnrollment.
                             */
                            /*for(int[] potentialUnenroll : hwInfoToUnenroll) {
                                if(currentEnrollmentInformation[INV] == potentialUnenroll[INV] &&
                                        currentEnrollmentInformation[ACCT] == potentialUnenroll[ACCT] &&
                                        currentEnrollmentInformation[GROUP] == potentialUnenroll[GROUP] &&
                                        currentEnrollmentInformation[RELAY] == potentialUnenroll[RELAY]) {
                                    hwInfoToUnenroll.remove(potentialUnenroll);
                                }
                                    
                            }*/
                            /*-------------------------------------------------------------------------------*/
                            
							app = (com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
							app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
									Transaction.createTransaction( Transaction.UPDATE, app ).execute();
							
							if (liteInv != null) {
								for (int j = 0; j < appList.size(); j++) {
									LiteStarsAppliance lApp = (LiteStarsAppliance) appList.get(j);
									if (lApp.getProgramID() == program.getProgramID() && lApp.getInventoryID() != program.getInventoryID()) {
										lApp.setAddressingGroupID( groupID );
										
										LiteStarsLMHardware lHw = (LiteStarsLMHardware) energyCompany.getInventory( lApp.getInventoryID(), true );
										if (!hwsToConfig.contains( lHw ))
											hwsToConfig.add( lHw );
										
										app = (com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( lApp );
										app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
												Transaction.createTransaction( Transaction.UPDATE, app ).execute();
									}
								}
							}
						}
					}
				}
				else {
					Integer progID = new Integer( program.getProgramID() );
					if (!progNewEnrollList.contains(progID))
						progNewEnrollList.add( progID );
	        		
					// Create a new appliance for the program
					app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
					com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
	        		
					appDB.setAccountID( accountID );
					appDB.setApplianceCategoryID( new Integer(program.getApplianceCategoryID()) );
					appDB.setProgramID( progID );
					appDB.setLocationID( dftLocationID );
					appDB.setManufacturerID( dftManufacturerID );
	        		
					if (liteHw != null) {
						LMHardwareConfiguration hwConfig = new LMHardwareConfiguration();
						hwConfig.setInventoryID( new Integer(program.getInventoryID()) );
                        hwConfig.setAddressingGroupID( groupID );
						hwConfig.setLoadNumber( new Integer(program.getLoadNumber()) );
						app.setLMHardwareConfig( hwConfig );
	        			
						if (!hwsToConfig.contains( liteHw ))
							hwsToConfig.add( liteHw );
                        
                        /* New enrollment, opt out, and control history tracking
                         * TODO Refactor this
                         *-------------------------------------------------------------------------------
                         */
                        int[] currentEnrollmentInformation = new int[6];
                        currentEnrollmentInformation[INV] = liteHw.getInventoryID();
                        currentEnrollmentInformation[ACCT] = liteHw.getAccountID();
                        currentEnrollmentInformation[GROUP] = groupID;
                        currentEnrollmentInformation[RELAY] = relay;
                        hwInfoToEnroll.add(currentEnrollmentInformation);
                        /*
                         * here we catch hardware that are ONLY being unenrolled.  If they are simply being enrolled in a different
                         * program, then the service's startEnrollment will handle the appropriate un-enrollments and we don't need
                         * to specifically do a stopEnrollment.
                         */
                       /* for(int[] potentialUnenroll : hwInfoToUnenroll) {
                            if(currentEnrollmentInformation[INV] == potentialUnenroll[INV] &&
                                    currentEnrollmentInformation[ACCT] == potentialUnenroll[ACCT] &&
                                    currentEnrollmentInformation[GROUP] == potentialUnenroll[GROUP] &&
                                    currentEnrollmentInformation[RELAY] == potentialUnenroll[RELAY]) {
                                hwInfoToUnenroll.remove(potentialUnenroll);
                            }
                        }*/
                        /*-------------------------------------------------------------------------------*/
					}
	        		
					app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
							Transaction.createTransaction( Transaction.INSERT, app ).execute();
	        		
					liteApp = StarsLiteFactory.createLiteStarsAppliance( app, energyCompany );
					newAppList.add( liteApp );
				}
			}
            
			// Update appliances saved earlier
			for (int i = 0; i < appsToUpdate.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) appsToUpdate.get(i);
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app =
						(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
				app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
						Transaction.createTransaction( Transaction.UPDATE, app ).execute();
			}
			
			// Remove redundant program IDs in the enroll and unenroll list
			Iterator it = progNewEnrollList.iterator();
			while (it.hasNext()) {
				int progID = ((Integer) it.next()).intValue();
				for (int i = 0; i < progList.size(); i++) {
					if (((LiteStarsLMProgram) progList.get(i)).getProgramID() == progID) {
						it.remove();
						break;
					}
				}
			}
			
			it = progUnenrollList.iterator();
			while (it.hasNext()) {
				int progID = ((Integer) it.next()).intValue();
				for (int i = 0; i < newProgList.size(); i++) {
					if (((LiteStarsLMProgram) newProgList.get(i)).getProgramID() == progID) {
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
			for (int i = 0; i < progNewEnrollList.size(); i++) {
				event.setEventID( null );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				eventDB.setAccountID( accountID );
				eventDB.setProgramID( (Integer)progNewEnrollList.get(i) );
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
				
				ProgramOptOutAction.removeProgramFutureActivationEvents(
						liteAcctInfo.getProgramHistory(), progID.intValue(), energyCompany );
	    		
				event.setEventID( null );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				eventDB.setAccountID( accountID );
				eventDB.setProgramID( progID );
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
			liteAcctInfo.setPrograms( newProgList );
            
            /* New enrollment, opt out, and control history tracking
             *-------------------------------------------------------------------------------
             */
                            
            //unenroll
            for(int[] currentUnenrollmentInfo : hwInfoToUnenroll) {
                boolean success = lmHardwareControlInformationService.stopEnrollment(currentUnenrollmentInfo[INV], 
                                                                                     currentUnenrollmentInfo[GROUP], 
                                                                                     currentUnenrollmentInfo[ACCT], 
                                                                                     currentUnenrollmentInfo[RELAY], 
                                                                                     currentUser);
                if(!success) {
                    CTILogger.error( "Enrollment STOP occurred for InventoryId: " + currentUnenrollmentInfo[INV] + 
                                     " LMGroupId: " + currentUnenrollmentInfo[GROUP] + 
                                     " on relay " + currentUnenrollmentInfo[RELAY] +
                                     " AccountId: " + currentUnenrollmentInfo[ACCT] + " done by user: " + 
                                     currentUser.getUsername() + " but could NOT be logged to LMHardwareControlGroup table." );
                }
            }
            
            //enroll
            for(int[] currentEnrollmentInfo : hwInfoToEnroll) {
                boolean success = lmHardwareControlInformationService.startEnrollment(currentEnrollmentInfo[INV], 
                                                                                     currentEnrollmentInfo[GROUP], 
                                                                                     currentEnrollmentInfo[ACCT], 
                                                                                     currentEnrollmentInfo[RELAY], 
                                                                                     currentUser);
                if(!success) {
                    CTILogger.error( "Enrollment START occurred for InventoryId: " + currentEnrollmentInfo[INV] + 
                                     " LMGroupId: " + currentEnrollmentInfo[GROUP] + 
                                     " on relay " + currentEnrollmentInfo[RELAY] +
                                     " AccountId: " + currentEnrollmentInfo[ACCT] + " done by user: " + 
                                     currentUser.getUsername() + " but could NOT be logged to LMHardwareControlGroup table." );
                }
            }
            /*-------------------------------------------------------------------------------*/
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to update the program enrollment", e );
		}
		
		return hwsToConfig;
	}
	
	public static SOAPMessage setAdditionalEnrollmentInfo(HttpServletRequest req, HttpSession session) throws Exception {
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		StarsOperation operation = new StarsOperation();
		StarsProgramSignUp progSignUp = new StarsProgramSignUp();
		StarsSULMPrograms programs = new StarsSULMPrograms();
		
		String[] progIDs = req.getParameterValues( "ProgID" );
		String[] appCatIDs = req.getParameterValues( "AppCatID" );
		String[] groupIDs = req.getParameterValues( "GroupID" );
		String[] invIDList = req.getParameterValues( "InvIDs" );
		String[] loadNoList = req.getParameterValues( "LoadNos" );
		
		for (int i = 0; i < progIDs.length; i++) {
			int progID = Integer.parseInt( progIDs[i] );
			int appCatID = Integer.parseInt( appCatIDs[i] );
			int groupID = Integer.parseInt( groupIDs[i] );
			
			String[] invIDs = invIDList[i].split( "," );
			String[] loadNos = loadNoList[i].split( "," );
			if (invIDs.length == 0) {
				String progName = StarsUtils.getPublishedProgramName( energyCompany.getProgram(progID) );
				throw new WebClientException( "No hardware is assigned to program \"" + progName + "\"" );
			}
			
			for (int j = 0; j < invIDs.length; j++) {
				int invID = Integer.parseInt( invIDs[j] );
				int loadNo = Integer.parseInt( loadNos[j] );
				/*if (invID > 0 && loadNo > 0) {
					for (int k = 0; k < programs.getSULMProgramCount(); k++) {
						if (programs.getSULMProgram(k).getInventoryID() == invID
							&& programs.getSULMProgram(k).getLoadNumber() == loadNo)
						{
							LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
							String label = StarsUtils.forceNotNone( liteHw.getDeviceLabel() );
							if (label.equals("")) label = liteHw.getManufacturerSerialNumber();
							throw new WebClientException( "Relay #" + loadNo + " has been selected for hardware \"" + label + "\" more than once" );
						}
					}
				}*/
				
				SULMProgram program = new SULMProgram();
				program.setProgramID( progID );
				program.setApplianceCategoryID( appCatID );
				program.setAddressingGroupID( groupID );
				if (invID > 0) program.setInventoryID( invID );
				program.setLoadNumber( loadNo );
				programs.addSULMProgram( program );
			}
		}
		
		progSignUp.setStarsSULMPrograms( programs );
		operation.setStarsProgramSignUp( progSignUp );
		return SOAPUtil.buildSOAPMessage( operation );
	}
	
	static LiteStarsLMProgram getLMProgram(LiteStarsCustAccountInformation liteAcctInfo, int programID) {
		for (int i = 0; i < liteAcctInfo.getPrograms().size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getPrograms().get(i);
			if (liteProg.getProgramID() == programID)
				return liteProg;
		}
		
		return null;
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
					YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw, null );
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

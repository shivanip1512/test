package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.hardware.LMHardwareConfiguration;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
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

                        programs.addSULMProgram( program );
					}
				}
			} else if (notEnrolled.equalsIgnoreCase( "Resend" )){
				// Resend the not enrolled command
				progSignUp.setStarsSULMPrograms(null);
			} else if (Boolean.valueOf( notEnrolled ).booleanValue()) {
				StarsSULMPrograms programs = new StarsSULMPrograms();
				progSignUp.setStarsSULMPrograms( programs );
			}
			
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
			boolean accountInSession = false;
			if (progSignUp.getAccountNumber() != null) {
			    liteAcctInfo = energyCompany.searchAccountByAccountNo( progSignUp.getAccountNumber() );
			} else {
			    LiteStarsCustAccountInformation tempAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			    if (tempAcctInfo == null || tempAcctInfo.getAccountID() <= 0) {
                    respOper.setStarsFailure(StarsFactory.newStarsFailure(StarsConstants.FAILURE_CODE_SESSION_INVALID,
                                                                          "Session invalidated, please login again"));
                    return SOAPUtil.buildSOAPMessage(respOper);
			    }
                StarsCustAccountInformationDao starsCustAccountInformationDao = 
                    YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
                liteAcctInfo = starsCustAccountInformationDao.getById(tempAcctInfo.getAccountID(), energyCompanyID);			    
			    session.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo);
			    accountInSession = true;
			}
			
			if (progSignUp.getStarsSULMPrograms() == null) {
				// Resend the not enrolled command
				respOper.setStarsProgramSignUpResponse( resendNotEnrolled(energyCompany, liteAcctInfo) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			
            CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
            CustomerAccount customerAccount = customerAccountDao.getById(liteAcctInfo.getAccountID());
            
            List<ProgramEnrollment> requests = new ArrayList<ProgramEnrollment>();
            for(int j = 0; j < progSignUp.getStarsSULMPrograms().getSULMProgramCount(); j++)
            {
                SULMProgram program = progSignUp.getStarsSULMPrograms().getSULMProgram(j);
                ProgramEnrollment enrollment = new ProgramEnrollment();
                enrollment.setAssignedProgramId(program.getProgramID());
                enrollment.setApplianceCategoryId(program.getApplianceCategoryID());
                enrollment.setInventoryId(program.getInventoryID());
                enrollment.setLmGroupId(program.getAddressingGroupID());
                enrollment.setRelay(program.getLoadNumber());
                
                requests.add(enrollment);
            }
            
            // Update program enrollments here
            ProgramEnrollmentService programEnrollmentService = YukonSpringHook.getBean("starsProgramEnrollmentService", ProgramEnrollmentService.class);            
            ProgramEnrollmentResultEnum enrollmentResult = programEnrollmentService.applyEnrollmentRequests(customerAccount, requests, liteUser);            
            if (enrollmentResult.equals(ProgramEnrollmentResultEnum.FAILURE) || 
                    enrollmentResult.equals(ProgramEnrollmentResultEnum.NOT_CONFIGURED_CORRECTLY)) {
                respOper.setStarsFailure(StarsFactory.newStarsFailure(StarsConstants.FAILURE_CODE_OPERATION_FAILED,
                                                                      "Program enrollment update failed"));
                return SOAPUtil.buildSOAPMessage(respOper);
            }
            
            // refresh account info, after update program enrollment
            if (accountInSession) {
                StarsCustAccountInformationDao starsCustAccountInformationDao = 
                    YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
                liteAcctInfo = starsCustAccountInformationDao.getById(liteAcctInfo.getAccountID(), energyCompanyID);
                session.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo);                    
            }
			if (user == null) {	// Probably from the sign up wizard?
				StarsSuccess success = new StarsSuccess();
				respOper.setStarsSuccess( success );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			StarsProgramSignUpResponse resp = new StarsProgramSignUpResponse();
			
			String desc = "Program enrollment updated successfully.";
			if (enrollmentResult.equals(ProgramEnrollmentResultEnum.SUCCESS_HARDWARE_CONFIG))
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
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
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
		List<LiteStarsLMHardware> hwsToConfig = new ArrayList<LiteStarsLMHardware>();
		
		StarsInventoryBaseDao starsInventoryBaseDao = 
			YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
		
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
        final int PROG = 4;
        //final int APPCAT = 5;        
        /*-------------------------------------------------------------------------------
         * */
        
		try {
			Integer accountID = new Integer( liteAcctInfo.getCustomerAccount().getAccountID() );
			
			Integer dftLocationID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_LOC_UNKNOWN).getEntryID() );
			Integer dftManufacturerID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_MANU_UNKNOWN).getEntryID() );
			
			// Set the termination time a little bit earlier than the signup date
			Date signupDate = new Date();
			Date termDate = new Date( signupDate.getTime() - 1000 );
        	
            List<LiteStarsLMProgram> progList = liteAcctInfo.getPrograms();	// List of old programs
			List<LiteStarsAppliance> appList = new ArrayList<LiteStarsAppliance>( liteAcctInfo.getAppliances() );	// List of old appliances
			appList.addAll(liteAcctInfo.getUnassignedAppliances());
			List<LiteStarsAppliance> newAppList = new ArrayList<LiteStarsAppliance>();		// List of new appliances
			List<LiteStarsLMProgram> newProgList = new ArrayList<LiteStarsLMProgram>();	// List of new programs
			List<Integer> progNewEnrollList = new ArrayList<Integer>();	// List of program IDs newly enrolled in
			List<Integer> progUnenrollList = new ArrayList<Integer>();	// List of program IDs to be unenrolled
			
			// The StarsSULMPrograms object after the omitted fields have been filled in
			StarsSULMPrograms processedPrograms = new StarsSULMPrograms();
			
			// Map from Integer(programID) to Hashtable(Map from Integer(inventoryID) to LiteStarsAppliance)
			Map<Integer, Map<Integer, LiteStarsAppliance>> progHwAppMap = new Hashtable<Integer, Map<Integer, LiteStarsAppliance>>();
			
			for (int i = 0; i < programs.getSULMProgramCount(); i++) {
				SULMProgram program = programs.getSULMProgram(i);
				processedPrograms.addSULMProgram( program );
				
				// If ApplianceCategoryID is not provided, set it here
				if (!program.hasApplianceCategoryID()) {
					LiteLMProgramWebPublishing liteProg = energyCompany.getProgram( program.getProgramID() );
					program.setApplianceCategoryID( liteProg.getApplianceCategoryID() );
				}
				
				Map<Integer, LiteStarsAppliance> hwAppMap = progHwAppMap.get( new Integer(program.getProgramID()) );
				if (hwAppMap == null) {
					hwAppMap = new Hashtable<Integer, LiteStarsAppliance>();
					progHwAppMap.put( new Integer(program.getProgramID()), hwAppMap );
				}
        		
				if (liteInv != null) {
					// Update program enrollment for the specifed hardware only
					program.setInventoryID( liteInv.getInventoryID() );
				}
				else if (!program.hasInventoryID()) {
					// Find hardwares already assigned to this program or another program in the same category
					Iterator<LiteStarsAppliance> it = appList.iterator();
					while (it.hasNext()) {
						LiteStarsAppliance liteApp = it.next();
						
						if (liteApp.getInventoryID() > 0 &&
							(liteApp.getProgramID() == program.getProgramID() || 
							 liteApp.getApplianceCategory().getApplianceCategoryId() == program.getApplianceCategoryID()))
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
							int invID = liteAcctInfo.getInventories().get(j).intValue();
							if (starsInventoryBaseDao.getByInventoryId(invID) instanceof LiteStarsLMHardware) {
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
					LiteStarsAppliance liteApp = appList.get(j);
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
			List<LiteStarsAppliance> appsToUpdate = new ArrayList<LiteStarsAppliance>();
			
			for (int i = 0; i < appList.size(); i++) {
				LiteStarsAppliance liteApp = appList.get(i);
				newAppList.add( liteApp );
    			
				if (liteApp.getProgramID() == 0) continue;
				
				if (liteInv == null || liteApp.getInventoryID() == liteInv.getInventoryID()) {
					Integer progID = new Integer( liteApp.getProgramID() );
					if (!progUnenrollList.contains( progID ))
						progUnenrollList.add( progID );
					
					if (liteApp.getInventoryID() > 0) {
						
						LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(liteApp.getInventoryID());
						if (!hwsToConfig.contains( liteHw )) 
							hwsToConfig.add( liteHw );
                            
                        /* New enrollment, opt out, and control history tracking
                         *-------------------------------------------------------------------------------
                         * here we catch hardware that are ONLY be unenrolled.  If they are simply being enrolled in a different
                         * program, then the service's startEnrollment will handle the appropriate un-enrollments.
                         */
                        int[] currentUnenrollmentInfo = new int[5];
                        currentUnenrollmentInfo[INV] = liteHw.getInventoryID();
                        currentUnenrollmentInfo[ACCT] = liteHw.getAccountID();
                        currentUnenrollmentInfo[GROUP] = liteApp.getAddressingGroupID();
                        if(currentUnenrollmentInfo[GROUP] == 0)
                            currentUnenrollmentInfo[GROUP] = InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(liteApp.getProgramID());
                        currentUnenrollmentInfo[RELAY] = liteApp.getLoadNumber();
                        currentUnenrollmentInfo[PROG] = liteApp.getProgramID();
                        //currentUnenrollmentInfo[APPCAT] = liteApp.getApplianceCategoryID();
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
						LiteStarsLMProgram prog = newProgList.get(j);
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
			    
                LiteStarsLMHardware liteHw = null;
                if (program.getInventoryID() > 0) {
                    try {
                        liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(program.getInventoryID());
                    }
                    catch (NotFoundException e) {
                        //not found ok, leave as null and continue
                    }
                }
				int groupID = program.getAddressingGroupID();
                int relay = program.getLoadNumber();
                if (useHardwareAddressing) {
                    if (liteHw != null && liteHw.getLMConfiguration() != null) {
                        int[] grpIDs = LMControlHistoryUtil.getControllableGroupIDs( liteHw.getLMConfiguration(), relay);
                        if (grpIDs != null && grpIDs.length >= 1){
                            groupID = grpIDs[0];
                        }
                    }
                } else if (groupID == 0 && starsProg.getAddressingGroupCount() >= 1) {
					groupID = starsProg.getAddressingGroup(0).getEntryID();
                }
				liteStarsProg.setGroupID( groupID );
    			
    			LiteStarsAppliance liteApp = 
    					progHwAppMap.get(new Integer(program.getProgramID())).get(program.getInventoryID());
    			
    			if (liteApp == null) {
					// Find existing appliance that could be attached to this hardware controlling this program
					// Priority: same hardware & same program > same hardware & same category > no hardware & same program > no hardware & same category
					for (int j = 0; j < appList.size(); j++) {
						LiteStarsAppliance lApp = appList.get(j);
						
						if ((lApp.getInventoryID() == program.getInventoryID() || lApp.getInventoryID() == 0) && 
							(lApp.getProgramID() == program.getProgramID() || 
							 lApp.getApplianceCategory().getApplianceCategoryId() == program.getApplianceCategoryID()))
						{
							if (liteApp == null) {
								liteApp = lApp;
							} else if (liteApp.getInventoryID() == 0) {
								if (lApp.getInventoryID() > 0 || lApp.getProgramID() == program.getProgramID())
									liteApp = lApp;
							} else {
								if (lApp.getInventoryID() > 0 && lApp.getProgramID() == program.getProgramID())
									liteApp = lApp;
							}
						}
					}
					
					// We found an existing appliance.  Remove it from the appLists so it can't be used by another enrollment.
					if (liteApp != null) {
						appList.remove(liteApp);
						
						// We only need to update the database once for this appliance
						if (appsToUpdate.contains( liteApp )) {
							appsToUpdate.remove( liteApp );
						}
					}
    			}
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app = null;
				
				if (liteApp != null) {
					liteApp.setInventoryID( program.getInventoryID() );
					int oldApplianceRelay = liteApp.getLoadNumber();
                    int oldLoadGroupId = liteApp.getAddressingGroupID();
                    int oldProgramId = liteApp.getProgramID();
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
                            int[] currentEnrollmentInformation = new int[5];
                            currentEnrollmentInformation[INV] = liteHw.getInventoryID();
                            currentEnrollmentInformation[ACCT] = liteHw.getAccountID();
                            currentEnrollmentInformation[GROUP] = groupID;
                            currentEnrollmentInformation[RELAY] = relay;
                            currentEnrollmentInformation[PROG] = program.getProgramID();
                            hwInfoToEnroll.add(currentEnrollmentInformation);
                            /*
                             * TODO: What about different relays?  Are we handling this correctly?
                             */
                            if(program.getApplianceCategoryID() == liteApp.getApplianceCategory().getApplianceCategoryId() && 
                                    program.getLoadNumber() == oldApplianceRelay &&
                                    oldLoadGroupId != 0) {
                                int[] currentUnenrollmentInformation = new int[5];
                                currentUnenrollmentInformation[INV] = liteHw.getInventoryID();
                                currentUnenrollmentInformation[ACCT] = liteHw.getAccountID();
                                currentUnenrollmentInformation[GROUP] = oldLoadGroupId;
                                currentUnenrollmentInformation[RELAY] = oldApplianceRelay;
                                currentUnenrollmentInformation[PROG] = oldProgramId;
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
						
						app = (ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
						app = Transaction.createTransaction( Transaction.UPDATE, app ).execute();
					}
					else {
						/* The appliance is enrolled in the same program, update the load group if necessary.
						 * If liteInv is not null, it's from the hardware configuration page;
						 * in the later case, update the group of all loads assigned to this program if necessary.
						 */
						if (liteHw != null && 
						    ((program.hasAddressingGroupID() && liteApp.getAddressingGroupID() != groupID) ||
						     (program.hasLoadNumber() && liteApp.getLoadNumber() != oldApplianceRelay))) {
							liteApp.setAddressingGroupID( groupID );
							if (!hwsToConfig.contains( liteHw )) 
								hwsToConfig.add( liteHw );
                            
                            /* New enrollment, opt out, and control history tracking
                             * TODO Refactor this
                             *-------------------------------------------------------------------------------
                             */
                            int[] currentEnrollmentInformation = new int[5];
                            currentEnrollmentInformation[INV] = liteHw.getInventoryID();
                            currentEnrollmentInformation[ACCT] = liteHw.getAccountID();
                            currentEnrollmentInformation[GROUP] = groupID;
                            currentEnrollmentInformation[RELAY] = relay;
                            currentEnrollmentInformation[PROG] = program.getProgramID();
                            hwInfoToEnroll.add(currentEnrollmentInformation);
                            
                            //  We need remove the old enrollment entry to eliminate duplicate entries.
                            int[] pastEnrollmentInformation = new int [5];
                            pastEnrollmentInformation[INV] = liteHw.getInventoryID();
                            pastEnrollmentInformation[ACCT] = liteHw.getAccountID();
                            pastEnrollmentInformation[GROUP] = oldLoadGroupId;
                            pastEnrollmentInformation[RELAY] = oldApplianceRelay;
                            pastEnrollmentInformation[PROG] = oldProgramId;
                            hwInfoToUnenroll.add(pastEnrollmentInformation);
                            
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
                            
							app = (ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
							app = Transaction.createTransaction( Transaction.UPDATE, app ).execute();
							
							
							// Checks to see if there are any hardware that are enrolled in this program already and updated their 
							// addressing group to the new supplied address group.
							if (liteInv != null && !useHardwareAddressing) {
								for (int j = 0; j < appList.size(); j++) {
									LiteStarsAppliance lApp = appList.get(j);
									if (lApp.getProgramID() == program.getProgramID() && lApp.getInventoryID() != program.getInventoryID()) {
										lApp.setAddressingGroupID( groupID );
										
										LiteStarsLMHardware lHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(lApp.getInventoryID());
										if (!hwsToConfig.contains( lHw ))
											hwsToConfig.add( lHw );
										
										app = (ApplianceBase) StarsLiteFactory.createDBPersistent( lApp );
										app = Transaction.createTransaction( Transaction.UPDATE, app ).execute();
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
					app = new ApplianceBase();
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
                        int[] currentEnrollmentInformation = new int[5];
                        currentEnrollmentInformation[INV] = liteHw.getInventoryID();
                        currentEnrollmentInformation[ACCT] = liteHw.getAccountID();
                        currentEnrollmentInformation[GROUP] = groupID;
                        currentEnrollmentInformation[RELAY] = relay;
                        currentEnrollmentInformation[PROG] = program.getProgramID();
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
	        		
					app = Transaction.createTransaction( Transaction.INSERT, app ).execute();
	        		
					liteApp = StarsLiteFactory.createLiteStarsAppliance( app, energyCompany );
					newAppList.add( liteApp );
				}
			}
            
			// Update appliances saved earlier
			for (int i = 0; i < appsToUpdate.size(); i++) {
				LiteStarsAppliance liteApp = appsToUpdate.get(i);
				
				ApplianceBase app = (ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
				app = Transaction.createTransaction( Transaction.UPDATE, app ).execute();
			}
			
			// Remove redundant program IDs in the enroll and unenroll list
			Iterator<Integer> it = progNewEnrollList.iterator();
			while (it.hasNext()) {
				int progID = it.next();
				for (int i = 0; i < progList.size(); i++) {
					if (progList.get(i).getProgramID() == progID) {
						it.remove();
						break;
					}
				}
			}
			
			it = progUnenrollList.iterator();
			while (it.hasNext()) {
				int progID = it.next();
				for (int i = 0; i < newProgList.size(); i++) {
					if (newProgList.get(i).getProgramID() == progID) {
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
				eventDB.setProgramID( progNewEnrollList.get(i) );
				eventBase.setEventTypeID( progEventEntryID );
				eventBase.setActionID( signUpEntryID );
				eventBase.setEventDateTime( signupDate );
				
				event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
				liteAcctInfo.getProgramHistory().add( liteEvent );
			}
			
			// Add "termination" event to the old program
			for (int i = 0; i < progUnenrollList.size(); i++) {
				Integer progID = progUnenrollList.get(i);
				
				event.setEventID( null );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				eventDB.setAccountID( accountID );
				eventDB.setProgramID( progID );
				eventBase.setEventTypeID( progEventEntryID );
				eventBase.setActionID( termEntryID );
				eventBase.setEventDateTime( termDate );
				
				event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
				liteAcctInfo.getProgramHistory().add( liteEvent );
			}
			
			// Update program status
			for (int i = 0; i < newProgList.size(); i++) {
				LiteStarsLMProgram liteStarsProg = newProgList.get(i);
				liteStarsProg.updateProgramStatus( liteAcctInfo.getProgramHistory() );
			}
    		
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
                                                                                     currentUnenrollmentInfo[PROG],
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
                                                                                     currentEnrollmentInfo[PROG],
                                                                                     currentUser,
                                                                                     useHardwareAddressing);
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
			LiteStarsLMProgram liteProg = liteAcctInfo.getPrograms().get(i);
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
		
		StarsInventoryBaseDao starsInventoryBaseDao = 
			YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = liteAcctInfo.getInventories().get(i);
			
			try {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(invID);
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

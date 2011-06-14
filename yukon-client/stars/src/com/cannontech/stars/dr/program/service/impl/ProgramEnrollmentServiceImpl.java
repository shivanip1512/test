package com.cannontech.stars.dr.program.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.LMGroupDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
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
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.HardwareAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.exception.DigiWebServiceException;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class ProgramEnrollmentServiceImpl implements ProgramEnrollmentService {
    private static final Logger log = YukonLogManager.getLogger(ProgramEnrollmentServiceImpl.class);

    private AssignedProgramDao assignedProgramDao;
    private ControlHistoryService controlHistoryService;
    private DBPersistentDao dbPersistentDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    private EnrollmentDao enrollmentDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private LoadGroupDao loadGroupDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private ZigbeeWebService zigbeeWebService;
    private YukonListDao yukonListDao;
    private LMGroupDao lmGroupDao;
    private GatewayDeviceDao gatewayDeviceDao;
    
    @Override
    @Transactional
    public ProgramEnrollmentResultEnum applyEnrollmentRequests(final CustomerAccount customerAccount,
                                                               final List<ProgramEnrollment> requests,
                                                               final LiteYukonUser user) {

        final int accountId = customerAccount.getAccountId();
        YukonEnergyCompany yukonEnergyCompany = 
            yukonEnergyCompanyService.getEnergyCompanyByAccountId(accountId);

        boolean trackHardwareAddressingEnabled = 
            energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, yukonEnergyCompany);
        boolean automaticConfigurationEnabled = 
            energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.AUTOMATIC_CONFIGURATION, yukonEnergyCompany);

        LiteStarsCustAccountInformation liteCustomerAccount = starsCustAccountInformationDao.getByAccountId(accountId);
        List<LiteStarsLMProgram> previouslyEnrolledPrograms = liteCustomerAccount.getPrograms();

        try {
            StarsOperation operation = createStarsOperation(customerAccount, requests, yukonEnergyCompany, liteCustomerAccount, trackHardwareAddressingEnabled);
            StarsProgramSignUp programSignUp = operation.getStarsProgramSignUp();
            Instant now  = new Instant();

            // Clean up an opt outs that are active or scheduled.
            for (ProgramEnrollment programEnrollment : requests) {
                if (programEnrollment.isEnroll() == false) {
                    
                    List<LMHardwareControlGroup> lmHardwareControlGroups = 
                        lmHardwareControlGroupDao
                            .getByInventoryIdAndGroupIdAndAccountIdAndType(programEnrollment.getInventoryId(), 
                                                                           programEnrollment.getLmGroupId(), 
                                                                           accountId, 
                                                                           LMHardwareControlGroup.OPT_OUT_ENTRY);
                    
                    for (LMHardwareControlGroup lmHardwareControlGroup : lmHardwareControlGroups) {
                        lmHardwareControlGroupDao.stopOptOut(lmHardwareControlGroup.getInventoryId(),
                                                             lmHardwareControlGroup.getAccountId(),
                                                             lmHardwareControlGroup.getProgramId(),
                                                             user, now);
                    }
                }
            }
            
            // Process Enrollments
            List<LiteStarsLMHardware> hwsToConfig = 
                updateProgramEnrollment(programSignUp, liteCustomerAccount, null, yukonEnergyCompany, user);

            // Send out the config/disable command
            for (final LiteStarsLMHardware liteHw : hwsToConfig) {
                boolean toConfig = HardwareAction.isToConfig(liteHw, liteCustomerAccount);
                
                if (toConfig) {
                    //ZigBee related configs
                    YukonListEntry yukonListEntry = yukonListDao.getYukonListEntry(liteHw.getLmHardwareTypeID());
                    HardwareType hardwareType = HardwareType.valueOf(yukonListEntry.getYukonDefID());
                    
                    // If send config commands to device.
                    if (hardwareType.isZigbee()) {
                        //Determine Gateway and Device Id.
                        int deviceId = liteHw.getDeviceID();
                        int lmGroupId = gatewayDeviceDao.getLMGroupIdByDeviceId(deviceId);
                        
                        //Build Attributes to send
                        Map<DRLCClusterAttribute,Integer> attributes = Maps.newHashMap();
                        
                        //Utility Enrollment group
                        int utilEnrollGroup = lmGroupDao.getUtilityEnrollmentGroupForSepGroup(lmGroupId);
                        if (utilEnrollGroup == 0) {
                            log.warn("Not sending Utility Enrollment Group to device because it is '0'. ");
                        } else {
                            attributes.put(DRLCClusterAttribute.UTILITY_ENROLLMENT_GROUP, utilEnrollGroup);    
                            zigbeeWebService.sendLoadGroupAddressing(deviceId, attributes);
                        }
                    }
                
                    // Send the reenable command if hardware status is unavailable,
                    // whether to send the config command is controlled by the AUTOMATIC_CONFIGURATION role property
                    if (!trackHardwareAddressingEnabled && automaticConfigurationEnabled) {
                        YukonSwitchCommandAction.sendConfigCommand( yukonEnergyCompany, liteHw, false, null );
                    } else if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
                        YukonSwitchCommandAction.sendEnableCommand( yukonEnergyCompany, liteHw, null );
                    }
                } else {
                    // Send disable command to hardware
                    YukonSwitchCommandAction.sendDisableCommand(yukonEnergyCompany, liteHw, null );
                }
            }
        } catch (InvalidParameterException e) {
            log.error(e);
            return ProgramEnrollmentResultEnum.NOT_CONFIGURED_CORRECTLY;
        } catch (WebClientException e2) {
            log.error(e2);
            return ProgramEnrollmentResultEnum.FAILURE;
        } catch (DigiWebServiceException e3) {
            log.error(e3);
            return ProgramEnrollmentResultEnum.FAILURE;
        }

        // Log activity
        String progEnrBefore = toProgramNameString(previouslyEnrolledPrograms, "(None)");
        String progEnrNow = toProgramNameString(liteCustomerAccount.getPrograms(), "(Not Enrolled)");

        final StringBuilder sb = new StringBuilder();
        sb.append("Program Enrolled Before: ");
        sb.append(progEnrBefore);
        sb.append("; Now: ");
        sb.append(progEnrNow);
        String logMessage = sb.toString();

        ActivityLogger.logEvent(user.getUserID(),
                                accountId,
                                yukonEnergyCompany.getEnergyCompanyId(),
                                customerAccount.getCustomerId(),
                                ActivityLogActions.PROGRAM_ENROLLMENT_ACTION,
                                logMessage);
        
        ProgramEnrollmentResultEnum result = (trackHardwareAddressingEnabled) ?
                ProgramEnrollmentResultEnum.SUCCESS_HARDWARE_CONFIG : ProgramEnrollmentResultEnum.SUCCESS;
        return result;
    }
    
    private StarsOperation createStarsOperation(CustomerAccount customerAccount,
                                                List<ProgramEnrollment> programEnrollmentList,
                                                YukonEnergyCompany energyCompany,
                                                LiteStarsCustAccountInformation liteCustomerAccount,
                                                boolean useHardwareAddressing) {

        final StarsProgramSignUp programSignUp = new StarsProgramSignUp();
        programSignUp.setEnergyCompanyID(energyCompany.getEnergyCompanyId());
        programSignUp.setAccountNumber(customerAccount.getAccountNumber());

        // Converting programEnrollmentList to a more friendly programSignUp object.
        StarsSULMPrograms starsSULMPrograms = new StarsSULMPrograms();
        for (final ProgramEnrollment programEnrollment : programEnrollmentList) {
            SULMProgram sulmProgram = new SULMProgram(programEnrollment);
            starsSULMPrograms.addSULMProgram(sulmProgram);
        }
        programSignUp.setStarsSULMPrograms(starsSULMPrograms);

        /*Going to need to do some guesswork since consumers aren't allowed  to choose load groups. 
         * --If the program has more than one group, we will take the first one in the list.  Could be
         * A DANGEROUS ASSUMPTION.  TODO: Track groups better.
         * --At this point, we will need to require that the switch or stat has been configured or enrolled 
         * previously from the operator side.  If it has not, there may not be a groupID set.
         */
        for (int j = 0; j < programSignUp.getStarsSULMPrograms().getSULMProgramCount(); j++) {
            SULMProgram sulmProgram = programSignUp.getStarsSULMPrograms().getSULMProgram(j);
            int groupId = sulmProgram.getAddressingGroupID();
            if (groupId == ServerUtils.ADDRESSING_GROUP_NOT_FOUND) {
                
                AssignedProgram assignedProgram = assignedProgramDao.getById(sulmProgram.getProgramID());
                LoadGroup defaultLoadGroup = getDefaultLoadGroupForProgram(assignedProgram);
                int loadGroupId =  0;
                if (defaultLoadGroup != null) {
                    loadGroupId = defaultLoadGroup.getLoadGroupId();
                }
                
                // Checking to see if there are any active enrollments that we can get the load 
                // group id from.  If we can use that load group id.
                if (!useHardwareAddressing) {
                    List<ProgramEnrollment> activeProgramEnrollments = enrollmentDao.getActiveEnrollmentsByAccountId(customerAccount.getAccountId());
                    for (ProgramEnrollment programEnrollment : activeProgramEnrollments) {
                        if (programEnrollment.getAssignedProgramId() == assignedProgram.getProgramId() && 
                                programEnrollment.getApplianceCategoryId() == assignedProgram.getApplianceCategoryId()) {
                            loadGroupId = programEnrollment.getLmGroupId();
                        }
                    }
                }
                if (!assignedProgram.isVirtualProgram() && loadGroupId <= 0) {
                    throw new InvalidParameterException("Program not defined correctly");
                }
                programSignUp.getStarsSULMPrograms().getSULMProgram(j).setAddressingGroupID(loadGroupId);
            }
        }

        StarsOperation operation = new StarsOperation();
        operation.setStarsProgramSignUp(programSignUp);
        return operation;
    }
    
    /**
     *  This method gets the first load group that is associated with the program.
     *  If there are no load groups attached to this assigned program or if the assigned program is a 
     *  virtual program this method will return null.
     */
    private LoadGroup getDefaultLoadGroupForProgram(AssignedProgram assignedProgram) {
        if (!assignedProgram.isVirtualProgram()) {
            List<LoadGroup> loadGroups = loadGroupDao.getByProgramId(assignedProgram.getProgramId());
            if (loadGroups.size() > 0) {
                return loadGroups.get(0);
            }
        }
        return null;
    }
    
    @Override
    public List<LiteStarsLMHardware> applyEnrollmentRequests(CustomerAccount customerAccount, 
                                                             List<ProgramEnrollment> programEnrollmentList,
                                                             LiteInventoryBase liteInv, LiteYukonUser user)
    throws WebClientException {

        final int customerAccountId = customerAccount.getAccountId();
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByAccountId(customerAccount.getAccountId());
        LiteStarsCustAccountInformation liteCustomerAccount = 
            starsCustAccountInformationDao.getByAccountId(customerAccountId);
        boolean trackHardwareAddressingEnabled = 
            energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, yukonEnergyCompany);
        
        StarsOperation operation = 
            createStarsOperation(customerAccount, programEnrollmentList, yukonEnergyCompany, liteCustomerAccount, trackHardwareAddressingEnabled);
        StarsProgramSignUp programSignUp = operation.getStarsProgramSignUp();

        List<LiteStarsLMHardware> hwsToConfig = 
            updateProgramEnrollment(programSignUp, liteCustomerAccount, liteInv, yukonEnergyCompany, user);

        return hwsToConfig;
    }    
    
    private String toProgramNameString(List<LiteStarsLMProgram> programs, String defaultValue) {
        if (programs == null || programs.isEmpty()) return defaultValue;
        final StringBuilder sb = new StringBuilder();
        for (final LiteStarsLMProgram program : programs) {
            String programName = StarsUtils.getPublishedProgramName(program.getPublishedProgram());
            sb.append(programName);
            sb.append(",");
        }
        String result = StringUtils.chop(sb.toString());
        return result;
    }

    @Override
    public void removeNonEnrolledPrograms(final List<Program> programs, final ListMultimap<Integer, ControlHistory> controlHistoryMap) {

        Validate.notNull(programs, "programs parameter cannot be null");
        Validate.notNull(controlHistoryMap, "controlHistoryMap parameter cannot be null");

        final List<Program> removeList = new ArrayList<Program>(0);

        for (final Program program : programs) {
            List<ControlHistory> controlHistoryList = controlHistoryMap.get(program.getProgramId());
            boolean containsOnlyNotEnrolledHistory = controlHistoryService.containsOnlyNotEnrolledHistory(controlHistoryList);
            if (containsOnlyNotEnrolledHistory) removeList.add(program);
        }

        programs.removeAll(removeList);
    }
    
    /**
     * This is a transplant from the ProgramSignUpAction class which has now been deleted:
     */
    private List<LiteStarsLMHardware> updateProgramEnrollment(StarsProgramSignUp progSignUp, 
                                                              LiteStarsCustAccountInformation liteAcctInfo,
                                                              LiteInventoryBase liteInv, 
                                                              YukonEnergyCompany yukonEnergyCompany, 
                                                              LiteYukonUser currentUser) throws WebClientException {
        
        LiteStarsEnergyCompany energyCompany = 
            StarsDatabaseCache.getInstance().getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        
        StarsSULMPrograms programs = progSignUp.getStarsSULMPrograms();
        List<LiteStarsLMHardware> hwsToConfig = new ArrayList<LiteStarsLMHardware>();
        
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
            
            List<LiteStarsLMProgram> progList = liteAcctInfo.getPrograms(); // List of old programs
            List<LiteStarsAppliance> appList = new ArrayList<LiteStarsAppliance>( liteAcctInfo.getAppliances() );   // List of old appliances
            appList.addAll(liteAcctInfo.getUnassignedAppliances());
            List<LiteStarsAppliance> newAppList = new ArrayList<LiteStarsAppliance>();      // List of new appliances
            List<LiteStarsLMProgram> newProgList = new ArrayList<LiteStarsLMProgram>(); // List of new programs
            List<Integer> progNewEnrollList = new ArrayList<Integer>(); // List of program IDs newly enrolled in
            List<Integer> progUnenrollList = new ArrayList<Integer>();  // List of program IDs to be unenrolled
            
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
                    	for (Integer inventoryId : liteAcctInfo.getInventories()) {
                            if (starsInventoryBaseDao.getByInventoryId(inventoryId) instanceof LiteStarsLMHardware) {
                                if (!program.hasInventoryID()) {
                                    program.setInventoryID(inventoryId);
                                }
                                else {
                                    SULMProgram prog = new SULMProgram();
                                    prog.setProgramID( program.getProgramID() );
                                    prog.setApplianceCategoryID( program.getApplianceCategoryID() );
                                    prog.setInventoryID(inventoryId);
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
            
            boolean hardwareAddressingEnabled =
                energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, energyCompany);
            
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
                if (hardwareAddressingEnabled) {
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
                        dbPersistentDao.performDBChange(app, TransactionType.UPDATE);
                    }
                    else {
                        /* The appliance is enrolled in the same program, update the load group if necessary.
                         * If liteInv is not null, it's from the hardware configuration page;
                         * in the later case, update the group of all loads assigned to this program if necessary.
                         */
                        if (liteHw != null && 
                            ((program.hasAddressingGroupID() && liteApp.getAddressingGroupID() != groupID) ||
                             liteApp.getLoadNumber() != oldApplianceRelay)) {
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
                            dbPersistentDao.performDBChange(app, TransactionType.UPDATE);
                            
                            // Checks to see if there are any hardware that are enrolled in this program already and updated their 
                            // addressing group to the new supplied address group.
                            if (liteInv != null && !hardwareAddressingEnabled) {
                                for (int j = 0; j < appList.size(); j++) {
                                    LiteStarsAppliance lApp = appList.get(j);
                                    if (lApp.getProgramID() == program.getProgramID() && lApp.getInventoryID() != program.getInventoryID()) {
                                        lApp.setAddressingGroupID( groupID );
                                        
                                        LiteStarsLMHardware lHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(lApp.getInventoryID());
                                        if (!hwsToConfig.contains( lHw ))
                                            hwsToConfig.add( lHw );
                                        
                                        app = (ApplianceBase) StarsLiteFactory.createDBPersistent( lApp );
                                        dbPersistentDao.performDBChange(app, TransactionType.UPDATE);
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
                    
                    dbPersistentDao.performDBChange(app, TransactionType.INSERT);
                    
                    liteApp = StarsLiteFactory.createLiteStarsAppliance( app, energyCompany );
                    newAppList.add( liteApp );
                }
            }
            
            // Update appliances saved earlier
            for (int i = 0; i < appsToUpdate.size(); i++) {
                LiteStarsAppliance liteApp = appsToUpdate.get(i);
                
                ApplianceBase app = (ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
                dbPersistentDao.performDBChange(app, TransactionType.UPDATE);
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
                event.setEnergyCompanyID( yukonEnergyCompany.getEnergyCompanyId() );
                eventDB.setAccountID( accountID );
                eventDB.setProgramID( progNewEnrollList.get(i) );
                eventBase.setEventTypeID( progEventEntryID );
                eventBase.setActionID( signUpEntryID );
                eventBase.setEventDateTime( signupDate );

                dbPersistentDao.performDBChange(event, TransactionType.INSERT);
                
                LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
                liteAcctInfo.getProgramHistory().add( liteEvent );
            }
            
            // Add "termination" event to the old program
            for (int i = 0; i < progUnenrollList.size(); i++) {
                Integer progID = progUnenrollList.get(i);
                
                event.setEventID( null );
                event.setEnergyCompanyID( yukonEnergyCompany.getEnergyCompanyId() );
                eventDB.setAccountID( accountID );
                eventDB.setProgramID( progID );
                eventBase.setEventTypeID( progEventEntryID );
                eventBase.setActionID( termEntryID );
                eventBase.setEventDateTime( termDate );

                dbPersistentDao.performDBChange(event, TransactionType.INSERT);

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
                                                                                     hardwareAddressingEnabled);
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
        catch (PersistenceException e) {
            CTILogger.error( e.getMessage(), e );
            throw new WebClientException( "Failed to update the program enrollment", e );
        }
        
        return hwsToConfig;
    }
    
    /**
     * This is a transplant from ProgramSignUpAciton: returns the program
     * in the program list for this account with the given program id.
     */
    private static LiteStarsLMProgram getLMProgram(LiteStarsCustAccountInformation liteAcctInfo, int programID) {
        for (LiteStarsLMProgram program : liteAcctInfo.getPrograms()) {
            if (program.getProgramID() == programID) {
                return program;
            }
        }
        
        return null;
    }

    @Override
    public boolean isProgramEnrolled(final int customerAccountId, final int inventoryId, final int programId) {
        final LMHardwareControlGroup currentEnrollment = 
                    lmHardwareControlGroupDao.findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(inventoryId, 
                                                                                                         programId,
                                                                                                         customerAccountId);
        
        if (currentEnrollment != null) {
            return currentEnrollment.isActiveEnrollment();
        }
        return false;
    }
    
    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    // DI Setter
    @Autowired
    public void setAssignedProgramDao(AssignedProgramDao assignedProgramDao) {
        this.assignedProgramDao = assignedProgramDao;
    }

    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setControlHistoryService(ControlHistoryService controlHistoryService) {
        this.controlHistoryService = controlHistoryService;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }

    @Autowired
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
    
    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

    @Autowired
    public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
    
    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }
    
    @Autowired
    public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setLmGroupDao(LMGroupDao lmGroupDao) {
        this.lmGroupDao = lmGroupDao;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
}

package com.cannontech.stars.dr.program.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.commands.exception.SystemConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.appliance.ApplianceBase;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLMProgramEvent;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMProgram;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.hardware.LMHardwareConfiguration;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.cannontech.stars.dr.controlHistory.service.StarsControlHistoryService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.exception.EnrollmentException;
import com.cannontech.stars.dr.enrollment.exception.EnrollmentSystemConfigurationException;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LMHardwareControlInformationService;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.HardwareEnrollmentInfo;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.action.HardwareAction;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.google.common.collect.ListMultimap;

public class ProgramEnrollmentServiceImpl implements ProgramEnrollmentService {
    private final static Logger log = YukonLogManager.getLogger(ProgramEnrollmentServiceImpl.class);

    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmHardwareCommandService lmHardwareCommandService;
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    @Autowired private LMHardwareControlInformationService lmHardwareControlInformationService;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private ProgramDao programDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private StarsControlHistoryService controlHistoryService;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private YukonListDao listDao;

    private final Map<Integer, Object> accountIdMutex = Collections.synchronizedMap(new HashMap<Integer, Object>());
    
    @Override
    @Transactional
    public ProgramEnrollmentResultEnum applyEnrollmentRequests(final CustomerAccount account,
                                                               final List<ProgramEnrollment> requests,
                                                               final LiteYukonUser user) {

        final int accountId = account.getAccountId();
        // Don't allow concurrent threads to handle requests for the same account to avoid
        // duplicate simultaneous requests which create duplicate entries in the database.
        synchronized (accountIdMutex) {
            if (!accountIdMutex.containsKey(accountId)) {
                accountIdMutex.put(accountId, new Object());
            }
        }
        synchronized (accountIdMutex.get(accountId)) {
            EnergyCompany ec = ecDao.getEnergyCompanyByAccountId(accountId);

            boolean trackAddressing = ecSettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, ec.getId());
            boolean autoConfig = ecSettingDao.getBoolean(EnergyCompanySettingType.AUTOMATIC_CONFIGURATION, ec.getId());
            boolean suppressMessages = ecSettingDao.getBoolean(EnergyCompanySettingType.SUPPRESS_IN_OUT_SERVICE_MESSAGES, ec.getId());

            LiteAccountInfo liteAccount = starsCustAccountInformationDao.getByAccountId(accountId);
            List<LiteStarsLMProgram> previouslyEnrolledPrograms = liteAccount.getPrograms();

            try {
                StarsOperation operation = createStarsOperation(account, requests, ec, trackAddressing);
                StarsProgramSignUp programSignUp = operation.getStarsProgramSignUp();
                Instant now  = new Instant();

                // Clean up any opt outs that are active or scheduled.
                for (ProgramEnrollment programEnrollment : requests) {
                    if (programEnrollment.isEnroll() == false) {

                        List<LMHardwareControlGroup> lmHardwareControlGroups =
                            lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndType(programEnrollment.getInventoryId(),
                                                                                        programEnrollment.getLmGroupId(),
                                                                                        LMHardwareControlGroup.OPT_OUT_ENTRY);

                        for (LMHardwareControlGroup lmHardwareControlGroup : lmHardwareControlGroups) {
                            lmHardwareControlGroupDao.stopOptOut(lmHardwareControlGroup.getInventoryId(),
                                                                 lmHardwareControlGroup.getProgramId(),
                                                                 user,
                                                                 now);
                        }
                    }
                }
                
                // Process Enrollments
                List<LiteLmHardwareBase> hwsToConfig = updateProgramEnrollment(programSignUp, liteAccount, null, ec, user);

                // Send out the config/disable command
                for (final LiteLmHardwareBase liteHw : hwsToConfig) {

                    boolean toConfig = HardwareAction.isToConfig(liteHw, liteAccount);
                    YukonListEntry typeEntry = listDao.getYukonListEntry(liteHw.getLmHardwareTypeID());
                    HardwareType hardwareType = HardwareType.valueOf(typeEntry.getYukonDefID());
                    
                    if(hardwareType.isNest()) {
                        //we do not send any messages to Nest on enrollment
                        continue;
                    }

                    if (toConfig) {
                        // Send the re-enable command if hardware status is unavailable.
                        // Whether to send the config command is controlled by the AUTOMATIC_CONFIGURATION energy
                        // company setting.
                        if (autoConfig) {
                            if (!trackAddressing || hardwareType.isZigbee() || hardwareType.isEcobee()
                                || hardwareType.isHoneywell() || hardwareType.isItron()) {
     
                                LmHardwareCommand command = new LmHardwareCommand();
                                command.setDevice(liteHw);
                                command.setType(LmHardwareCommandType.CONFIG);
                                command.setUser(user);
                                
                                lmHardwareCommandService.sendConfigCommand(command);
                            }
                        } else if (inventoryBaseDao.getDeviceStatus(liteHw.getInventoryID())
                                == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL && !suppressMessages) {
                            LmHardwareCommand command = new LmHardwareCommand();
                            command.setDevice(liteHw);
                            command.setType(LmHardwareCommandType.IN_SERVICE);
                            command.setUser(user);

                            lmHardwareCommandService.sendInServiceCommand(command);
                        }
                    } else if (!suppressMessages) {
                        LmHardwareCommand command = new LmHardwareCommand();
                        command.setDevice(liteHw);
                        command.setType(LmHardwareCommandType.OUT_OF_SERVICE);
                        command.setUser(user);
                        
                        lmHardwareCommandService.sendOutOfServiceCommand(command);
                    }
                }
            } catch(SystemConfigurationException e) {
                log.error(e);
                throw new EnrollmentSystemConfigurationException("System configuration error in enrollment.", e);
            } catch (ItronCommunicationException e) {
                log.error(e);
                throw new EnrollmentException("There was a communication error trying to connect with Itron. Please view the logs for more details.", e);
            } catch (CommandCompletionException e) {
                log.error(e);
                throw new EnrollmentException("Error sending enrollment/unenrollment command.", e);
            }

            // Log activity
            String progEnrBefore = toProgramNameString(previouslyEnrolledPrograms, "(None)");
            String progEnrNow = toProgramNameString(liteAccount.getPrograms(), "(Not Enrolled)");

            final StringBuilder sb = new StringBuilder();
            sb.append("Program Enrolled Before: ");
            sb.append(progEnrBefore);
            sb.append("; Now: ");
            sb.append(progEnrNow);
            String logMessage = sb.toString();

            ActivityLogger.logEvent(user.getUserID(), accountId, ec.getId(), account.getCustomerId(),
                ActivityLogActions.PROGRAM_ENROLLMENT_ACTION, logMessage);

            ProgramEnrollmentResultEnum result = trackAddressing ?
                    ProgramEnrollmentResultEnum.SUCCESS_HARDWARE_CONFIG : ProgramEnrollmentResultEnum.SUCCESS;
            return result;
        }
    }

    private StarsOperation createStarsOperation(CustomerAccount customerAccount,
            List<ProgramEnrollment> programEnrollmentList, EnergyCompany energyCompany, boolean useHardwareAddressing) {
        StarsProgramSignUp programSignUp = new StarsProgramSignUp();
        programSignUp.setEnergyCompanyID(energyCompany.getId());
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
            if (groupId == SULMProgram.ADDRESSING_GROUP_NOT_FOUND) {

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
    public List<LiteLmHardwareBase> applyEnrollmentRequests(CustomerAccount customerAccount,
                                                             List<ProgramEnrollment> programEnrollmentList,
                                                             LiteInventoryBase liteInv, LiteYukonUser user) {
        int customerAccountId = customerAccount.getAccountId();
        EnergyCompany ec = ecDao.getEnergyCompanyByAccountId(customerAccount.getAccountId());
        LiteAccountInfo liteCustomerAccount = starsCustAccountInformationDao.getByAccountId(customerAccountId);
        // Don't allow concurrent threads to handle requests for the same account to avoid
        // duplicate simultaneous requests which create duplicate entries in the database.
        synchronized (accountIdMutex) {
            if (!accountIdMutex.containsKey(liteCustomerAccount.getAccountID())) {
                accountIdMutex.put(liteCustomerAccount.getAccountID(), new Object());
            }
        }
        synchronized (accountIdMutex.get(liteCustomerAccount.getAccountID())) {
            boolean trackHardwareAddressingEnabled =
                    ecSettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, ec.getId());

            StarsOperation operation = createStarsOperation(customerAccount, programEnrollmentList, ec,
                trackHardwareAddressingEnabled);
            StarsProgramSignUp programSignUp = operation.getStarsProgramSignUp();

            List<LiteLmHardwareBase> hwsToConfig =
                updateProgramEnrollment(programSignUp, liteCustomerAccount, liteInv, ec, user);

            return hwsToConfig;
        }
    }

    private String toProgramNameString(List<LiteStarsLMProgram> programs, String defaultValue) {
        if (programs == null || programs.isEmpty()) {
            return defaultValue;
        }
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

        final List<Program> removeList = new ArrayList<>(0);

        for (final Program program : programs) {
            List<ControlHistory> controlHistoryList = controlHistoryMap.get(program.getProgramId());
            boolean containsOnlyNotEnrolledHistory = controlHistoryService.containsOnlyNotEnrolledHistory(controlHistoryList);
            if (containsOnlyNotEnrolledHistory) {
                removeList.add(program);
            }
        }

        programs.removeAll(removeList);
    }

    /**
     * This is a transplant from the ProgramSignUpAction class which has now been deleted:
     */
    private List<LiteLmHardwareBase> updateProgramEnrollment(StarsProgramSignUp progSignUp,
            LiteAccountInfo liteAcctInfo, LiteInventoryBase liteInv, EnergyCompany ec, LiteYukonUser currentUser) {
        LiteStarsEnergyCompany lsec = StarsDatabaseCache.getInstance().getEnergyCompany(ec.getId());
        StarsSULMPrograms programs = progSignUp.getStarsSULMPrograms();
        List<LiteLmHardwareBase> hwsToConfig = new ArrayList<>();

        int accountID = liteAcctInfo.getCustomerAccount().getAccountID();

        int dftLocationID = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_LOC_UNKNOWN).getEntryID();
        int dftManufacturerID = selectionListService.getListEntry(ec, YukonListEntryTypes.YUK_DEF_ID_MANU_UNKNOWN).getEntryID();

        // Set the termination time a little bit earlier than the signup date
        Date signupDate = new Date();
        Date termDate = new Date(signupDate.getTime() - 1000);

        List<LiteStarsLMProgram> progList = liteAcctInfo.getPrograms(); // List of old programs
        List<LiteStarsAppliance> appList = new ArrayList<>(liteAcctInfo.getAppliances());   // List of old appliances
        appList.addAll(liteAcctInfo.getUnassignedAppliances());
        List<LiteStarsAppliance> newAppList = new ArrayList<>();      // List of new appliances
        List<LiteStarsLMProgram> newProgList = new ArrayList<>(); // List of new programs
        List<Integer> progNewEnrollList = new ArrayList<>(); // List of program IDs newly enrolled in
        List<Integer> progUnenrollList = new ArrayList<>();  // List of program IDs to be unenrolled

        // The StarsSULMPrograms object after the omitted fields have been filled in
        StarsSULMPrograms processedPrograms = new StarsSULMPrograms();

        // Map from Integer(programID) to Hashtable(Map from Integer(inventoryID) to LiteStarsAppliance)
        Map<Integer, Map<Integer, LiteStarsAppliance>> progHwAppMap = new Hashtable<>();

        for (int i = 0; i < programs.getSULMProgramCount(); i++) {
            SULMProgram program = programs.getSULMProgram(i);
            processedPrograms.addSULMProgram(program);

            // If ApplianceCategoryID is not provided, set it here
            if (!program.hasApplianceCategoryID()) {
                LiteLMProgramWebPublishing liteProg = lsec.getProgram(program.getProgramID());
                program.setApplianceCategoryID(liteProg.getApplianceCategoryID());
            }

            Map<Integer, LiteStarsAppliance> hwAppMap = progHwAppMap.get(program.getProgramID());
            if (hwAppMap == null) {
                hwAppMap = new Hashtable<>();
                progHwAppMap.put(program.getProgramID(), hwAppMap);
            }

            if (liteInv != null) {
                // Update program enrollment for the specifed hardware only
                program.setInventoryID(liteInv.getInventoryID());
            } else if (!program.hasInventoryID()) {
                // Find hardwares already assigned to this program or another program in the same category
                Iterator<LiteStarsAppliance> it = appList.iterator();
                while (it.hasNext()) {
                    LiteStarsAppliance liteApp = it.next();

                    if (liteApp.getInventoryID() > 0 &&
                        (liteApp.getProgramID() == program.getProgramID() ||
                         liteApp.getApplianceCategory().getApplianceCategoryId()== program.getApplianceCategoryID())) {
                        if (!program.hasInventoryID()) {
                            if (!program.hasAddressingGroupID()&& liteApp.getProgramID()== program.getProgramID()) {
                                program.setAddressingGroupID(liteApp.getAddressingGroupID());
                            }
                            program.setInventoryID(liteApp.getInventoryID());
                            program.setLoadNumber(liteApp.getLoadNumber());
                        } else {
                            SULMProgram prog = new SULMProgram();
                            prog.setProgramID(program.getProgramID());
                            prog.setApplianceCategoryID(program.getApplianceCategoryID());
                            prog.setInventoryID(liteApp.getInventoryID());
                            prog.setLoadNumber(liteApp.getLoadNumber());
                            if (program.hasAddressingGroupID()) {
                                prog.setAddressingGroupID(program.getAddressingGroupID());
                            }
                            processedPrograms.addSULMProgram(prog);
                        }

                        hwAppMap.put(liteApp.getInventoryID(), liteApp);
                        newAppList.add(liteApp);
                        it.remove();
                    }
                }

                // If no hardware found above, then assign all hardwares
                if (!program.hasInventoryID()) {
                    for (Integer inventoryId : liteAcctInfo.getInventories()) {
                        if (inventoryBaseDao.getByInventoryId(inventoryId) instanceof LiteLmHardwareBase) {
                            if (!program.hasInventoryID()) {
                                program.setInventoryID(inventoryId);
                            } else {
                                SULMProgram prog = new SULMProgram();
                                prog.setProgramID(program.getProgramID());
                                prog.setApplianceCategoryID(program.getApplianceCategoryID());
                                prog.setInventoryID(inventoryId);
                                if (program.hasAddressingGroupID()) {
                                    prog.setAddressingGroupID(program.getAddressingGroupID());
                                }
                                if (program.hasLoadNumber()) {
                                    prog.setLoadNumber(program.getLoadNumber());
                                }
                                processedPrograms.addSULMProgram(prog);
                            }
                        }
                    }
                }
            }

            // Try to find the appliance controlled by this program and complete the request
            for (int j = 0; j < appList.size(); j++) {
                LiteStarsAppliance liteApp = appList.get(j);
                if (liteApp.getProgramID() == program.getProgramID() && liteApp.getInventoryID() == program.getInventoryID()) {
                    if (!program.hasAddressingGroupID()) {
                        program.setAddressingGroupID(liteApp.getAddressingGroupID());
                    }

                    hwAppMap.put(liteApp.getInventoryID(), liteApp);
                    newAppList.add(liteApp);
                    appList.remove(liteApp);
                    break;
                }
            }
        }

        /* Remove unenrolled programs and free up appliances attached to them first,
         * so they can be reused by the new enrolled programs.
         * If liteInv is not null, only remove programs assigned to the specified hardware.
         */
        /*
         * New enrollment, opt out, and control history tracking
         */
        List<HardwareEnrollmentInfo> hwInfoToEnroll = new ArrayList<>(3);
        List<HardwareEnrollmentInfo> hwInfoToUnenroll = new ArrayList<>(3);

        List<LiteStarsAppliance> appsToUpdate = new ArrayList<>();
        for (int i = 0; i < appList.size(); i++) {
            LiteStarsAppliance liteApp = appList.get(i);
            newAppList.add(liteApp);

            if (liteApp.getProgramID() == 0) {
                continue;
            }

            if (liteInv == null || liteApp.getInventoryID() == liteInv.getInventoryID()) {
                if (!progUnenrollList.contains(liteApp.getProgramID())) {
                    progUnenrollList.add(liteApp.getProgramID());
                }

                if (liteApp.getInventoryID() > 0) {
                    LiteLmHardwareBase liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(liteApp.getInventoryID());
                    if (!hwsToConfig.contains(liteHw)) {
                        hwsToConfig.add(liteHw);
                    }

                    /* New enrollment, opt out, and control history tracking
                     *-------------------------------------------------------------------------------
                     * here we catch hardware that are ONLY be unenrolled.  If they are simply being enrolled in a different
                     * program, then the service's startEnrollment will handle the appropriate un-enrollments.
                     */
                    int loadGroupId = liteApp.getAddressingGroupID();
                    if(loadGroupId == 0) {
                        loadGroupId = programDao.getLoadGroupIdForProgramId(liteApp.getProgramID());
                    }
                    hwInfoToUnenroll.add(new HardwareEnrollmentInfo(liteHw.getInventoryID(),
                        liteHw.getAccountID(), loadGroupId, liteApp.getLoadNumber(), liteApp.getProgramID()));
                    /*-------------------------------------------------------------------------------*/
                }

                liteApp.setInventoryID(0);
                liteApp.setProgramID(0);
                liteApp.setAddressingGroupID(0);
                liteApp.setLoadNumber(0);

                // Save the appliance to update it in the database later
                appsToUpdate.add(liteApp );
            } else {
                LiteStarsLMProgram liteStarsProg = getLMProgram(liteAcctInfo, liteApp.getProgramID());
                if (!newProgList.contains(liteStarsProg)) {
                    newProgList.add(liteStarsProg );
                }
            }
        }

        boolean hardwareAddressingEnabled = ecSettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, lsec.getEnergyCompanyId());

        for (int i = 0; i < processedPrograms.getSULMProgramCount(); i++) {
            SULMProgram program = processedPrograms.getSULMProgram(i);

            LiteLMProgramWebPublishing liteProg = lsec.getProgram(program.getProgramID());
            StarsEnrLMProgram starsProg =
                ServletUtils.getEnrollmentProgram(lsec.getStarsEnrollmentPrograms(), program.getProgramID());

            // Add the program to the new program list
            LiteStarsLMProgram liteStarsProg = getLMProgram(liteAcctInfo, program.getProgramID());
            if (liteStarsProg == null) {
                for (int j = 0; j < newProgList.size(); j++) {
                    LiteStarsLMProgram prog = newProgList.get(j);
                    if (prog.getProgramID() == program.getProgramID()) {
                        liteStarsProg = prog;
                        break;
                    }
                }

                if (liteStarsProg == null) {
                    liteStarsProg = new LiteStarsLMProgram(liteProg);
                }
            }
            if (!newProgList.contains(liteStarsProg)) {
                newProgList.add(liteStarsProg );
            }

            LiteLmHardwareBase liteHw = null;
            if (program.getInventoryID() > 0) {
                try {
                    liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(program.getInventoryID());
                } catch (NotFoundException e) {
                    //not found ok, leave as null and continue
                }
            }
            int groupID = program.getAddressingGroupID();
            int relay = program.getLoadNumber();
            if (hardwareAddressingEnabled) {
                if (liteHw != null && liteHw.getLMConfiguration() != null) {
                    int[] grpIDs = LMControlHistoryUtil.getControllableGroupIDs(liteHw.getLMConfiguration(), relay);
                    if (grpIDs != null && grpIDs.length >= 1) {
                        groupID = grpIDs[0];
                    }
                }
            } else if (groupID == 0 && starsProg.getAddressingGroupCount() >= 1) {
                groupID = starsProg.getAddressingGroup(0).getEntryID();
            }
            liteStarsProg.setGroupID(groupID );

            LiteStarsAppliance liteApp =
                    progHwAppMap.get(program.getProgramID()).get(program.getInventoryID());

            if (liteApp == null) {
                // Find existing appliance that could be attached to this hardware controlling this program
                // Priority: same hardware & same program > same hardware & same category > no hardware & same program > no hardware & same category
                for (int j = 0; j < appList.size(); j++) {
                    LiteStarsAppliance lApp = appList.get(j);

                    if ((lApp.getInventoryID()== program.getInventoryID() || lApp.getInventoryID() == 0)
                            && (lApp.getProgramID()== program.getProgramID()
                            ||  lApp.getApplianceCategory().getApplianceCategoryId() == program.getApplianceCategoryID())) {
                        if (liteApp == null) {
                            liteApp = lApp;
                        } else if (liteApp.getInventoryID() == 0) {
                            if (lApp.getInventoryID()> 0 || lApp.getProgramID() == program.getProgramID()) {
                                liteApp = lApp;
                            }
                        } else {
                            if (lApp.getInventoryID()> 0 && lApp.getProgramID() == program.getProgramID()) {
                                liteApp = lApp;
                            }
                        }
                    }
                }

                // We found an existing appliance.  Remove it from the appLists so it can't be used by another enrollment.
                if (liteApp != null) {
                    appList.remove(liteApp);

                    // We only need to update the database once for this appliance
                    if (appsToUpdate.contains(liteApp)) {
                        appsToUpdate.remove(liteApp);
                    }
                }
            }

            com.cannontech.stars.database.data.appliance.ApplianceBase app = null;

            if (liteApp != null) {
                liteApp.setInventoryID(program.getInventoryID());
                int oldApplianceRelay = liteApp.getLoadNumber();
                int oldLoadGroupId = liteApp.getAddressingGroupID();
                int oldProgramId = liteApp.getProgramID();
                liteApp.setLoadNumber(relay);

                //the appliance is on a different program then the current, this is an enrollment switch
                if (liteApp.getProgramID()!= program.getProgramID()) {
                    // If the appliance is enrolled in another program, update its program enrollment
                    if (!progNewEnrollList.contains(program.getProgramID())) {
                        progNewEnrollList.add(program.getProgramID());
                    }

                    if (liteApp.getProgramID() != 0) {
                        if (!progUnenrollList.contains(liteApp.getProgramID())) {
                            progUnenrollList.add(liteApp.getProgramID());
                        }
                    }

                    if (liteHw != null) {
                        if ((liteApp.getAddressingGroupID() != groupID || groupID == 0) && !hwsToConfig.contains(liteHw)) {
                            hwsToConfig.add(liteHw);
                        }

                        /* New enrollment, opt out, and control history tracking
                         */
                        hwInfoToEnroll.add(new HardwareEnrollmentInfo(liteHw.getInventoryID(), liteHw.getAccountID(),
                            groupID, relay, program.getProgramID()));
                        /*
                         * TODO: What about different relays?  Are we handling this correctly?
                         */
                        if(program.getApplianceCategoryID() == liteApp.getApplianceCategory().getApplianceCategoryId()
                                && program.getLoadNumber() == oldApplianceRelay
                                && oldLoadGroupId != 0) {
                            hwInfoToUnenroll.add(new HardwareEnrollmentInfo(liteHw.getInventoryID(),
                                liteHw.getAccountID(), oldLoadGroupId, oldApplianceRelay, oldProgramId));
                        }
                        liteApp.setAddressingGroupID(groupID);
                    } else {
                        liteApp.setAddressingGroupID(0);
                    }

                    liteApp.setProgramID(program.getProgramID());

                    app = (ApplianceBase) StarsLiteFactory.createDBPersistent(liteApp);
                    dbPersistentDao.performDBChange(app, TransactionType.UPDATE);
                } else {
                    /* The appliance is enrolled in the same program, update the load group if necessary.
                     * If liteInv is not null, it's from the hardware configuration page;
                     * in the later case, update the group of all loads assigned to this program if necessary.
                     */
                    if (liteHw != null
                            && ((program.hasAddressingGroupID() && liteApp.getAddressingGroupID() != groupID)
                                    || liteApp.getLoadNumber() != oldApplianceRelay)) {
                        liteApp.setAddressingGroupID(groupID);
                        if (!hwsToConfig.contains(liteHw)) {
                            hwsToConfig.add(liteHw);
                        }

                        /* New enrollment, opt out, and control history tracking
                         * TODO Refactor this
                         *-------------------------------------------------------------------------------
                         */
                        hwInfoToEnroll.add(new HardwareEnrollmentInfo(liteHw.getInventoryID(),
                            liteHw.getAccountID(), groupID, relay, program.getProgramID()));
                        //  We need remove the old enrollment entry to eliminate duplicate entries.
                        hwInfoToUnenroll.add(new HardwareEnrollmentInfo(liteHw.getInventoryID(),
                            liteHw.getAccountID(), oldLoadGroupId, oldApplianceRelay, oldProgramId));
                        app = (ApplianceBase) StarsLiteFactory.createDBPersistent(liteApp);
                        dbPersistentDao.performDBChange(app, TransactionType.UPDATE);

                        // Checks to see if there are any hardware that are enrolled in this program already and updated their
                        // addressing group to the new supplied address group.
                        if (liteInv != null && !hardwareAddressingEnabled) {
                            for (int j = 0; j < appList.size(); j++) {
                                LiteStarsAppliance lApp = appList.get(j);
                                if (lApp.getProgramID() == program.getProgramID()&& lApp.getInventoryID() != program.getInventoryID()) {
                                    lApp.setAddressingGroupID(groupID);

                                    LiteLmHardwareBase lHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(lApp.getInventoryID());
                                    if (!hwsToConfig.contains(lHw)) {
                                        hwsToConfig.add(lHw);
                                    }

                                    app = (ApplianceBase)StarsLiteFactory.createDBPersistent(lApp);
                                    dbPersistentDao.performDBChange(app, TransactionType.UPDATE);
                                }
                            }
                        }
                    }
                }
            } else {
                if (!progNewEnrollList.contains(program.getProgramID())) {
                    progNewEnrollList.add(program.getProgramID());
                }

                // Create a new appliance for the program
                app = new ApplianceBase();
                com.cannontech.stars.database.db.appliance.ApplianceBase appDB = app.getApplianceBase();

                appDB.setAccountID(accountID);
                appDB.setApplianceCategoryID(program.getApplianceCategoryID());
                appDB.setProgramID(program.getProgramID());
                appDB.setLocationID(dftLocationID );
                appDB.setManufacturerID(dftManufacturerID);

                if (liteHw != null) {
                    LMHardwareConfiguration hwConfig = new LMHardwareConfiguration();
                    hwConfig.setInventoryID(program.getInventoryID());
                    hwConfig.setAddressingGroupID(groupID);
                    hwConfig.setLoadNumber(program.getLoadNumber());
                    app.setLMHardwareConfig(hwConfig);

                    if (!hwsToConfig.contains(liteHw)) {
                        hwsToConfig.add(liteHw);
                    }

                    /* New enrollment, opt out, and control history tracking
                     */
                    hwInfoToEnroll.add(new HardwareEnrollmentInfo(liteHw.getInventoryID(),
                        liteHw.getAccountID(), groupID, relay, program.getProgramID()));
                }

                dbPersistentDao.performDBChange(app, TransactionType.INSERT);

                liteApp = StarsLiteFactory.createLiteStarsAppliance(app, lsec);
                newAppList.add(liteApp);
            }
        }

        // Update appliances saved earlier
        for (int i = 0; i < appsToUpdate.size(); i++) {
            LiteStarsAppliance liteApp = appsToUpdate.get(i);

            ApplianceBase app = (ApplianceBase) StarsLiteFactory.createDBPersistent(liteApp);
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
        int progEventEntryID = selectionListService.getListEntry(lsec, YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID();
        int signUpEntryID = selectionListService.getListEntry(lsec, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP).getEntryID();
        int termEntryID = selectionListService.getListEntry(lsec, YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID();

        com.cannontech.stars.database.data.event.LMProgramEvent event =
                new com.cannontech.stars.database.data.event.LMProgramEvent();
        com.cannontech.stars.database.db.event.LMProgramEvent eventDB = event.getLMProgramEvent();
        com.cannontech.stars.database.db.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();

        // Add "sign up" event to the programs to be enrolled in
        for (int i = 0; i < progNewEnrollList.size(); i++) {
            event.setEventID(null);
            event.setEnergyCompanyID(ec.getId());
            eventDB.setAccountID(accountID);
            eventDB.setProgramID(progNewEnrollList.get(i));
            eventBase.setEventTypeID(progEventEntryID);
            eventBase.setActionID(signUpEntryID);
            eventBase.setEventDateTime(signupDate);

            dbPersistentDao.performDBChange(event, TransactionType.INSERT);

            LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
            liteAcctInfo.getProgramHistory().add(liteEvent );
        }

        // Add "termination" event to the old program
        for (int i = 0; i < progUnenrollList.size(); i++) {
            event.setEventID(null);
            event.setEnergyCompanyID(ec.getId());
            eventDB.setAccountID(accountID);
            eventDB.setProgramID(progUnenrollList.get(i));
            eventBase.setEventTypeID(progEventEntryID);
            eventBase.setActionID(termEntryID);
            eventBase.setEventDateTime(termDate);

            dbPersistentDao.performDBChange(event, TransactionType.INSERT);

            LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
            liteAcctInfo.getProgramHistory().add(liteEvent );
        }

        // Update program status
        for (int i = 0; i < newProgList.size(); i++) {
            LiteStarsLMProgram liteStarsProg = newProgList.get(i);
            liteStarsProg.updateProgramStatus(liteAcctInfo.getProgramHistory());
        }

        liteAcctInfo.setPrograms(newProgList);

        /* New enrollment, opt out, and control history tracking
         */
        //unenroll
        for (HardwareEnrollmentInfo enrollmentInfo : hwInfoToUnenroll) {
            boolean success = lmHardwareControlInformationService.stopEnrollment(enrollmentInfo, currentUser);
            if (!success) {
                CTILogger.error("Enrollment STOP occurred for InventoryId: " + enrollmentInfo.getInventoryId() +
                                 " LMGroupId: " + enrollmentInfo.getLoadGroupId() +
                                 " on relay " + enrollmentInfo.getRelayNumber() +
                                 " AccountId: " + enrollmentInfo.getAccountId() + " done by user: " +
                                 currentUser.getUsername()+ " but could NOT be logged to LMHardwareControlGroup table.");
            }
        }

        //enroll
        for (HardwareEnrollmentInfo enrollmentInfo : hwInfoToEnroll) {
            boolean success =
                lmHardwareControlInformationService.startEnrollment(enrollmentInfo, currentUser, hardwareAddressingEnabled);
            if (!success) {
                CTILogger.error("Enrollment START occurred for InventoryId: " + enrollmentInfo.getInventoryId() +
                                 " LMGroupId: " + enrollmentInfo.getLoadGroupId() +
                                 " on relay " + enrollmentInfo.getRelayNumber() +
                                 " AccountId: " + enrollmentInfo.getAccountId() + " done by user: " +
                                 currentUser.getUsername() + " but could NOT be logged to LMHardwareControlGroup table." );
            }
        }

        return hwsToConfig;
    }

    /**
     * This is a transplant from ProgramSignUpAciton: returns the program
     * in the program list for this account with the given program id.
     */
    private static LiteStarsLMProgram getLMProgram(LiteAccountInfo liteAcctInfo, int programID) {
        for (LiteStarsLMProgram program : liteAcctInfo.getPrograms()) {
            if (program.getProgramID() == programID) {
                return program;
            }
        }

        return null;
    }
}
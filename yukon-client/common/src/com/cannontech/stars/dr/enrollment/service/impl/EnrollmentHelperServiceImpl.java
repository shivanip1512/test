package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.dao.ItronDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.exception.EnrollmentException;
import com.cannontech.stars.dr.enrollment.model.EnrolledDevicePrograms;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEventLoggingData;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelperHolder;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EnrollmentHelperServiceImpl implements EnrollmentHelperService {
    private final static Logger log = YukonLogManager.getLogger(EnrollmentHelperServiceImpl.class);
    
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private ApplianceDao applianceDao;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private ProgramDao programDao;
    @Autowired private ProgramService programService;
    @Autowired private ProgramEnrollmentService programEnrollmentService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private PaoDao paoDao;
    @Autowired private ItronDao itronDao;
    @Autowired private DisconnectService disconnectService;
    @Autowired private DeviceDao deviceDao;

    @Override
    public void updateProgramEnrollments(List<ProgramEnrollment> programEnrollments, 
                                         int accountId,
                                         YukonUserContext userContext) {

        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        // Get the current enrollments.  This list will be updated to reflect
        // the desired enrollment data then passed to applyEnrollments which
        // will make it so.
        List<ProgramEnrollment> enrollments = 
            enrollmentDao.getActiveEnrollmentsByAccountId(customerAccount.getAccountId());

        // Index current enrollments by program id and inventory id.
        // Map<assignedProgramId, Map<inventoryId, ProgramEnrollment>>
        Map<Integer, Map<Integer, ProgramEnrollment>> currentEnrollments = Maps.newHashMap();
        for (ProgramEnrollment enrollment : enrollments) {
            Map<Integer, ProgramEnrollment> enrollmentsByInventory = currentEnrollments.get(enrollment.getAssignedProgramId());
            if (enrollmentsByInventory == null) {
                enrollmentsByInventory = Maps.newHashMap();
                currentEnrollments.put(enrollment.getAssignedProgramId(), enrollmentsByInventory);
            }
            enrollmentsByInventory.put(enrollment.getInventoryId(), enrollment);
        }

        // Go through the list of enrollments we want to update and make
        // sure they are in or out of the enrollmentData list as appropriate.
        List<ProgramEnrollment> removedEnrollments = Lists.newArrayList();
        List<ProgramEnrollment> addedEnrollments = Lists.newArrayList();
        for (ProgramEnrollment enrollment : programEnrollments) {
            int assignedProgramId = enrollment.getAssignedProgramId();
            int inventoryId = enrollment.getInventoryId();
            ProgramEnrollment currentEnrollment = currentEnrollments.containsKey(assignedProgramId) ? currentEnrollments.get(assignedProgramId).get(inventoryId) : null;
            if ((currentEnrollment == null || !currentEnrollment.isEnroll()) && !enrollment.isEnroll()) {
                // we weren't enrolled and we're not going to be; nothing to change
                continue;
            }

            if (enrollment.equals(currentEnrollment)) {
                // the enrollment hasn't changed
                continue;
            }

            if (currentEnrollment != null) {
                enrollments.remove(currentEnrollment);
                removedEnrollments.add(currentEnrollment);
            }
            if (enrollment.isEnroll()) {
                enrollments.add(enrollment);
                addedEnrollments.add(enrollment);
            }

            LoadGroup loadGroup = loadGroupDao.getById(enrollment.getLmGroupId());
            verifyRelay(loadGroup, enrollment);
        }
        programEnrollmentService.applyEnrollmentRequests(customerAccount, enrollments, userContext.getYukonUser());
        
        // Log the new enrollments.
        for (ProgramEnrollment programEnrollment : addedEnrollments) {
            EnrollmentEventLoggingData eventLoggingData = getEventLoggingData(programEnrollment);

            accountEventLogService.deviceEnrolled(userContext.getYukonUser(), 
                                                  customerAccount.getAccountNumber(),
                                                  eventLoggingData.getManufacturerSerialNumber(),
                                                  eventLoggingData.getProgramName(),
                                                  eventLoggingData.getLoadGroupName());
        }
        for (ProgramEnrollment programEnrollment : removedEnrollments) {
            EnrollmentEventLoggingData eventLoggingData = getEventLoggingData(programEnrollment);

            accountEventLogService.deviceUnenrolled(userContext.getYukonUser(),
                                                    customerAccount.getAccountNumber(),
                                                    eventLoggingData.getManufacturerSerialNumber(),
                                                    eventLoggingData.getProgramName(),
                                                    eventLoggingData.getLoadGroupName());
        }
        
    }

    @Override
    public EnrollmentEventLoggingData getEventLoggingData(ProgramEnrollment programEnrollment){
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(programEnrollment.getInventoryId());

        LoadGroup loadGroup = null; 
        if (programEnrollment.getLmGroupId() != 0) {    //need check for != 0 for when track_hardware_addressing is true, or virtual program
            loadGroup = loadGroupDao.getById(programEnrollment.getLmGroupId());
        }
        Program program = programDao.getByProgramId(programEnrollment.getAssignedProgramId());
        
        return new EnrollmentEventLoggingData(lmHardwareBase, loadGroup, program);
    }

    @Override
    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user){
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        CustomerAccount customerAccount =
            customerAccountDao.getByAccountNumber(enrollmentHelper.getAccountNumber(), ec.getId());
        doEnrollment(enrollmentHelper, enrollmentEnum, user, customerAccount);
    }

    @Override
    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user,
            CustomerAccount customerAccount) {
        EnrollmentHelperHolder enrollmentHelperHolder =
            buildEnrollmentHelperHolder(enrollmentHelper, enrollmentEnum, user, customerAccount);
        doEnrollment(enrollmentHelperHolder, enrollmentEnum, user);
    }
    
    @Override
    public void makeDisconnectMetersOnAccountEnrollable(int accountId) {
        // Find meter hardware (inventory) for this account
        List<HardwareSummary> allMeterHardwareSummary = inventoryDao.getMeterHardwareSummaryForAccount(accountId);
        
        if(!allMeterHardwareSummary.isEmpty()) {
            // Get the PAOs for these meters, so we can do the disconnect checks on the paos
            List<Integer> meterPaoIds = allMeterHardwareSummary.stream()
                .map(HardwareSummary::getDeviceId)
                .collect(Collectors.toList());
            
            // Find disconnect meters
            List<SimpleDevice> allMeters = deviceDao.getYukonDeviceObjectByIds(meterPaoIds);
            List<Integer> disconnectMeters = disconnectService.filter(allMeters).stream()
                .map(SimpleDevice::getDeviceId)
                .collect(Collectors.toList());
            
            // Now that we know which PAOS are disconnect paos, filter out the inventory that does not support disconnect
            List<HardwareSummary> disconnectMeterHardware = allMeterHardwareSummary.stream()
                .filter(hardware -> disconnectMeters.contains(hardware.getDeviceId()))
                .collect(Collectors.toList());
                      
            EnergyCompany energyCompany = ecDao.getEnergyCompanyByAccountId(accountId);
            
            for (HardwareSummary hardwareSummary : disconnectMeterHardware) {
                inventoryBaseDao.addLmHardwareToMeterIfMissing(hardwareSummary.getSerialNumber(), hardwareSummary.getInventoryId(), energyCompany);
            }

        }
        
    }

    @Override
    public void doEnrollment(EnrollmentHelperHolder enrollmentHelperHolder, EnrollmentEnum enrollmentEnum, LiteYukonUser user){

        CustomerAccount customerAccount = enrollmentHelperHolder.getCustomerAccount();
        EnrollmentHelper enrollmentHelper = enrollmentHelperHolder.getEnrollmentHelper();
        LMHardwareBase lmHardwareBase = enrollmentHelperHolder.getLmHardwareBase();
        
        // Fix this account so disconnect meter enrollments are possible.
        makeDisconnectMetersOnAccountEnrollable(customerAccount.getAccountId());
        
        // Get the current enrollments.  This list will be updated to reflect the desired enrollment
        // data then passed to applyEnrollments which will make it so.
        List<ProgramEnrollment> enrollmentData = enrollmentDao.getActiveEnrollmentsByAccountId(customerAccount.getAccountId());
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByAccountId(customerAccount.getAccountId());
        boolean isMultipleProgramsPerCategoryAllowed =
                energyCompanySettingDao.getBoolean(EnergyCompanySettingType.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY, energyCompany.getId());
        // This handles an unenrollment with no program given.  In this case we we just want to unenroll
        // the device from every program it is enrolled.
        ProgramEnrollment programEnrollment;
        if(enrollmentEnum.equals(EnrollmentEnum.UNENROLL) && StringUtils.isEmpty(enrollmentHelper.getProgramName())) {
            programEnrollment = new ProgramEnrollment();
            programEnrollment.setInventoryId(lmHardwareBase.getInventoryId());
            programEnrollment.setAssignedProgramId(0);
            for(ProgramEnrollment enroll : enrollmentData) {
                if (enroll.getInventoryId() == programEnrollment.getInventoryId()) {
                    AssignedProgram assignedProgram = assignedProgramDao.getById(enroll.getAssignedProgramId());
                    enrollmentHelper.setProgramName(assignedProgram.getProgramName());
                    LiteYukonPAObject pao = paoDao.getLiteYukonPAO(enroll.getLmGroupId());
                    enrollmentHelper.setLoadGroupName(pao.getPaoName());
                }
            }
        } else {

            /**
             * This method also take care of the validation for the applianceCategory, program, and loadGroup
             * and includes checking to see if they belong to one another.
             */
            Program program = programService.getByProgramName(enrollmentHelper.getProgramName(), energyCompany); 

            Set<Integer> appCatEnergyCompanyIds = applianceCategoryDao.getAppCatEnergyCompanyIds(energyCompany);
            ApplianceCategory applianceCategory = 
                getApplianceCategoryByName(enrollmentHelper.getApplianceCategoryName(), program, appCatEnergyCompanyIds);

            LoadGroup loadGroup = getLoadGroupByName(enrollmentHelper.getLoadGroupName(), program);

            programEnrollment = new ProgramEnrollment(enrollmentHelper, lmHardwareBase, applianceCategory, program, loadGroup);
            
            verifyRelay(loadGroup, programEnrollment);
        }
        
        // Adding or Removing the new program enrollment to the active enrollment list, which is needed
        // in legacy code to process enrollment
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            addProgramEnrollment(enrollmentData, programEnrollment, enrollmentHelper.isSeasonalLoad(), isMultipleProgramsPerCategoryAllowed);
        } else if (enrollmentEnum == EnrollmentEnum.UNENROLL) {
            removeProgramEnrollment(enrollmentData, programEnrollment);
        }
        
        // Create/Remove the supplied enrollments.  This includes adding them to the database and
        // also sending out the commands to the device.
        programEnrollmentService.applyEnrollmentRequests(customerAccount, enrollmentData, user);
        
        // Updates the applianceKW if the process was an enrollment.
        updateApplianceKW(enrollmentEnum, customerAccount, enrollmentHelper, programEnrollment);
        
        // Logging the new enrollment changes.
        logEnrollmentChange(user, enrollmentEnum, enrollmentHelper);
    }
    
    /**
     * This method will verify the relay selected for enrollment is valid
     * @param loadGroup the load group for enrollment
     * @param enrollment the Program Enrollment 
     */
    private void verifyRelay(LoadGroup loadGroup, ProgramEnrollment enrollment) {
        if (loadGroup != null) {
            int enrollmentRelay = enrollment.getRelay();
            //For Itron, the relay should match the relay specified in the Group
            if (loadGroup.getPaoIdentifier().getPaoType() == PaoType.LM_GROUP_ITRON) {
                int relayFromGroup = itronDao.getVirtualRelayId(loadGroup.getLoadGroupId());
                //if no relay was provided, set it to the relay specified in the Itron group
                if (enrollmentRelay == 0) {
                    enrollment.setRelay(relayFromGroup);
                    return;
                }
                //error if relay does not match group
                if (enrollmentRelay != relayFromGroup) {
                    EnrollmentException e = new EnrollmentException("Relay selected for enrollment does not match the Load Group: " + loadGroup.getName() + ".");
                    YukonMessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.operator.enrollmentError.relay", loadGroup.getName());
                    e.setDetailMessage(message);
                    log.error(e.getMessage());
                    throw e;
                }
            }
        }
    }

    /**
     * This method updates the applianceKW of a newly enrolled inventory.
     */
    private void updateApplianceKW(EnrollmentEnum enrollmentEnum, CustomerAccount customerAccount,
                                         EnrollmentHelper enrollmentHelper, ProgramEnrollment programEnrollment) {
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            Appliance appliance = 
                applianceDao.getByAccountIdAndProgramIdAndInventoryId(customerAccount.getAccountId(),
                                                                      programEnrollment.getAssignedProgramId(),
                                                                      programEnrollment.getInventoryId());
            if (enrollmentHelper.getApplianceKW() != null) {
                applianceDao.updateApplianceKW(appliance.getApplianceId(), enrollmentHelper.getApplianceKW());
            }
        }
    }

    
    /**
     * This method write the new enrollment information to the event log table.
     */
    private void logEnrollmentChange(LiteYukonUser user, EnrollmentEnum enrollmentEnum, EnrollmentHelper enrollmentHelper) {
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            accountEventLogService.deviceEnrolled(user, enrollmentHelper.getAccountNumber(),
                                                  enrollmentHelper.getSerialNumber(),
                                                  enrollmentHelper.getProgramName(),
                                                  enrollmentHelper.getLoadGroupName());
        }
        if (enrollmentEnum == EnrollmentEnum.UNENROLL) {
            accountEventLogService.deviceUnenrolled(user, enrollmentHelper.getAccountNumber(),
                                                    enrollmentHelper.getSerialNumber(),
                                                    enrollmentHelper.getProgramName(),
                                                    enrollmentHelper.getLoadGroupName());
        }
    }

    /*
     * Note : If we send an EIM request for enrollment with same account, load group, load program as one of the already enrolled programs 
     * but different serial number, the matching program enrollment is updated to ADD the device with serial number received in the request.
     * Whereas when we try to do the same from web server, we ask for user confirmation before performing this enrollment
     * update. 
     */
    protected void addProgramEnrollment(List<ProgramEnrollment> programEnrollments,
                                         ProgramEnrollment newProgramEnrollment, boolean seasonalLoad,
                                         boolean isMultipleProgramsPerCategoryAllowed) {
        boolean isProgramEnrollmentEnrolled = false;
        List<ProgramEnrollment> removedEnrollments = Lists.newArrayList();
        for (ProgramEnrollment programEnrollment : programEnrollments) {

            if (programEnrollment.getApplianceCategoryId() == newProgramEnrollment.getApplianceCategoryId()) {
                if (programEnrollment.getInventoryId() == newProgramEnrollment.getInventoryId()) {
                    if (programEnrollment.getRelay() == newProgramEnrollment.getRelay()) {
                        // The below change is intentional. Even if isMultipleProgramsPerCategoryAllowed = true, it would 
                        // un-enroll the matching program and enroll the new program when ApplianceCategoryId, InventoryId
                        // relay are same and seasonalLoad = false.
                        // This behavior is different from that in Web.
                        if (!seasonalLoad || programEnrollment.getAssignedProgramId() == newProgramEnrollment.getAssignedProgramId()) {
                            programEnrollment.update(newProgramEnrollment);
                            programEnrollment.setEnroll(true);
                            isProgramEnrollmentEnrolled = true;
                        }
                    } else {
                        if (programEnrollment.getAssignedProgramId() == newProgramEnrollment.getAssignedProgramId()) {
                            programEnrollment.update(newProgramEnrollment);
                            programEnrollment.setEnroll(true);
                            isProgramEnrollmentEnrolled = true;
                        } else if (!isMultipleProgramsPerCategoryAllowed) {
                            removedEnrollments.add(programEnrollment);
                        }
                    }
                }
                /* 
                 * The EIM does not remove enrollments from other devices on the same account (even if 
                 * isMultipleProgramsPerCategoryAllowed=false), while the web does. 
                 */
            }
        }

        if (CollectionUtils.isNotEmpty(removedEnrollments)) {
            programEnrollments.removeAll(removedEnrollments);
        }

        if (!isProgramEnrollmentEnrolled) {
            programEnrollments.add(newProgramEnrollment);
        }
    }
    
    protected void removeProgramEnrollment(List<ProgramEnrollment> programEnrollments,
            ProgramEnrollment removedProgramEnrollment) {

        removedProgramEnrollment.setEnroll(true);

        List<ProgramEnrollment> programEnrollmentResults = new ArrayList<>();
        programEnrollmentResults.addAll(programEnrollments);

        boolean found = false;
        for (int i = 0; i < programEnrollments.size(); i++) {
            ProgramEnrollment programEnrollment = programEnrollments.get(i);
            if (removedProgramEnrollment.getAssignedProgramId() == 0) {
                if (removedProgramEnrollment.getInventoryId() == programEnrollment.getInventoryId()) {
                    programEnrollmentResults.remove(programEnrollment);
                    found = true;
                }
            }

            if (removedProgramEnrollment.equivalent(programEnrollment)) {
                programEnrollmentResults.remove(programEnrollment);
                found = true;
            }
        }
        if (!found && removedProgramEnrollment.getAssignedProgramId() > 0) {
            throw new NotFoundException(
                "Enrollment not found for program [" + removedProgramEnrollment.getAssignedProgramId() + "]");
        }
        programEnrollments.retainAll(programEnrollmentResults);
    }

    private ApplianceCategory getApplianceCategoryByName(String applianceCategoryName, Program program, Set<Integer> energyCompanyIds){
        
        if (!StringUtils.isBlank(applianceCategoryName)){
            List<ApplianceCategory> applianceCategoryList = applianceCategoryDao.getByApplianceCategoryName(applianceCategoryName,
                                                                                                            energyCompanyIds);

            for (ApplianceCategory applianceCategory : applianceCategoryList) {
                if(program.getApplianceCategoryId() == applianceCategory.getApplianceCategoryId()) {
                        return applianceCategory;
                }
            }
                
            throw new IllegalArgumentException("The supplied program does not belong to the supplied appliance category.");
        }
        
        return null;
    }

    private LoadGroup getLoadGroupByName(String loadGroupName, Program program){
        
        LoadGroup loadGroup = null; 
        if (!StringUtils.isBlank(loadGroupName)){
            loadGroup = loadGroupDao.getByLoadGroupName(loadGroupName);
            if (!loadGroup.getProgramIds().contains(program.getProgramId())) {
                throw new IllegalArgumentException("The supplied load group does not belong to the supplied program.");
            }
        }
        return loadGroup;
    }
    
    
    @Override
    public List<EnrolledDevicePrograms> getEnrolledDeviceProgramsByAccountNumber(String accountNumber, Date startDate,
            Date stopDate, LiteYukonUser user) throws AccountNotFoundException {

        // account
        CustomerAccount account = null;
        try {
            account = customerAccountDao.getByAccountNumber(accountNumber, user);
        } catch (NotFoundException e) {
            throw new AccountNotFoundException("Account not found: " + accountNumber, e);
        }

        // inventory
        List<Integer> inventoryIdList = inventoryDao.getInventoryIdsByAccount(account.getAccountId());
        List<LiteLmHardwareBase> inventoryList = inventoryBaseDao.getLMHardwareForIds(inventoryIdList);

        // enrolledDeviceProgramsList
        List<EnrolledDevicePrograms> enrolledDeviceProgramsList =
            Lists.newArrayListWithExpectedSize(inventoryIdList.size());
        for (LiteLmHardwareBase lmHardware : inventoryList) {

            String serialNumber = lmHardware.getManufacturerSerialNumber();

            List<Program> programs =
                enrollmentDao.getEnrolledProgramIdsByInventory(lmHardware.getInventoryID(), startDate, stopDate);
            Map<String, Integer> enrolledPrograms = new HashMap<>();
            for (Program program : programs) {
                enrolledPrograms.put(program.getProgramPaoName(), program.getRelay());
            }

            if (enrolledPrograms.keySet().size() > 0) {
                EnrolledDevicePrograms enrolledDevicePrograms =
                    new EnrolledDevicePrograms(serialNumber, enrolledPrograms);
                enrolledDeviceProgramsList.add(enrolledDevicePrograms);
            }
        }

        return enrolledDeviceProgramsList;
    }

    private EnrollmentHelperHolder buildEnrollmentHelperHolder(EnrollmentHelper enrollmentHelper,
            EnrollmentEnum enrollmentEnum, LiteYukonUser user, CustomerAccount customerAccount) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        LiteInventoryBase liteInventoryBase;
        try {
            liteInventoryBase =
                starsSearchDao.searchLmHardwareBySerialNumber(enrollmentHelper.getSerialNumber(), energyCompany);
        } catch (ObjectInOtherEnergyCompanyException e) {
            if (enrollmentEnum.equals(EnrollmentEnum.UNENROLL)) {
                liteInventoryBase = (LiteInventoryBase) e.getObject();
            } else {
                throw new RuntimeException(e);
            }
        }
        if (liteInventoryBase == null) {
            throw new IllegalArgumentException(
                "The supplied piece of hardware was not found: " + enrollmentHelper.getSerialNumber());
        }
        if (liteInventoryBase.getAccountID() != customerAccount.getAccountId()) {
            throw new IllegalArgumentException("The supplied piece of hardware: " + enrollmentHelper.getSerialNumber()
                + " does not belong to the supplied account: " + enrollmentHelper.getAccountNumber());
        }

        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(liteInventoryBase.getInventoryID());
        EnrollmentHelperHolder enrollmentHelperHolder =
            new EnrollmentHelperHolder(enrollmentHelper, customerAccount, lmHardwareBase);
        return enrollmentHelperHolder;
    }
}

package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.dr.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.user.YukonUserContext;

public class EnrollmentHelperServiceImpl implements EnrollmentHelperService {
    
    private ApplianceDao applianceDao;
    private ApplianceCategoryDao applianceCategoryDao;
    private CustomerAccountDao customerAccountDao;
    private EnrollmentDao enrollmentDao;
    private LoadGroupDao loadGroupDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ProgramDao programDao;
    private ProgramEnrollmentService programEnrollmentService;
    
    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, YukonUserContext yukonUserContext){
        CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(enrollmentHelper.getAccountNumber(), yukonUserContext.getYukonUser());
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getBySerialNumber(enrollmentHelper.getSerialNumber());
        List<ProgramEnrollment> enrollmentData = 
            enrollmentDao.getActiveEnrollmentByAccountId(customerAccount.getAccountId());

        /*
         * Gets the program, appliance category, and load group information.  These 
         * methods also take care of the validation for these objects as well, which
         * includes checking to see if they are joined.
         */
        Program program = getProgramByName(enrollmentHelper.getProgramName());
        ApplianceCategory applianceCategory = getApplianceCategoryByName(enrollmentHelper.getApplianceCategoryName(), program);
        LoadGroup loadGroup = getLoadGroupByName(enrollmentHelper.getLoadGroupName(), program);
        
        /* Builds up the program enrollment object that will be 
         * used later on to enroll or unenroll.
         */
        ProgramEnrollment programEnrollment = new ProgramEnrollment();
        programEnrollment.setInventoryId(lmHardwareBase.getInventoryId());
        programEnrollment.setProgramId(program.getProgramId());
        programEnrollment.setApplianceKW(enrollmentHelper.getApplianceKW());

        if (applianceCategory != null)
            programEnrollment.setApplianceCategoryId(applianceCategory.getApplianceCategoryId());
        else
            programEnrollment.setApplianceCategoryId(program.getApplianceCategoryId());

        if (loadGroup != null) 
            programEnrollment.setLmGroupId(loadGroup.getLoadGroupId());

        if (!StringUtils.isBlank(enrollmentHelper.getRelay()))
            programEnrollment.setRelay(enrollmentHelper.getRelay());
        
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            addProgramEnrollment(enrollmentData, programEnrollment);
        }
        if (enrollmentEnum == EnrollmentEnum.UNENROLL) {
            removeProgramEnrollment(enrollmentData, programEnrollment);
        }
        
        // Processes the enrollment requests
        programEnrollmentService.applyEnrollmentRequests(customerAccount,
                                                         enrollmentData,
                                                         yukonUserContext);
        
        // Updates the applianceKW if the process was an enrollment.
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            List<LMHardwareConfiguration> lmHardwareConfigurations = 
                lmHardwareControlGroupDao.getOldConfigDataByInventoryIdAndGroupId(programEnrollment.getInventoryId(),
                                                                                  programEnrollment.getLmGroupId());
            if (lmHardwareConfigurations.size() == 1)
                applianceDao.updateApplianceKW(lmHardwareConfigurations.get(0).getApplianceId(),
                                               enrollmentHelper.getApplianceKW());
            else
                throw new DuplicateEnrollmentException("A duplicate enrollment entry was found in your database.  Please fix as soon as possible.");
        }
    }

    
    private void addProgramEnrollment(List<ProgramEnrollment> programEnrollments,
                                     ProgramEnrollment newProgramEnrollment){
        boolean isProgramEnrollmentEnrolled = false;
       
        /* Loop through all the hardware that is enrolled and check to see if there is hardware
         * enrolled in the same program and appliance category.  If it qualifies the inventory 
         * should be updated to the new group
         */
        for (ProgramEnrollment programEnrollment : programEnrollments) {
            if ((programEnrollment.getApplianceCategoryId() == 
                    newProgramEnrollment.getApplianceCategoryId()) &&
                 programEnrollment.getProgramId() == newProgramEnrollment.getProgramId()) {

                /* Check to see if the inventory is being re-enrolled.  If this is the case
                 * we don't want to enroll the hardware twice.
                 */
                if (programEnrollment.getInventoryId() == newProgramEnrollment.getInventoryId()) {
                    isProgramEnrollmentEnrolled = true;
                    if (newProgramEnrollment.getLmGroupId() != 0){
                        programEnrollment.setLmGroupId(newProgramEnrollment.getLmGroupId());
                    }
                    programEnrollment.setRelay(newProgramEnrollment.getRelay());
                }

                if (newProgramEnrollment.getLmGroupId() != 0){
                    programEnrollment.setLmGroupId(newProgramEnrollment.getLmGroupId());
                }
            }
        }
        
        if (!isProgramEnrollmentEnrolled) {
            programEnrollments.add(newProgramEnrollment);
        }
    }           
    
    private void removeProgramEnrollment(List<ProgramEnrollment> programEnrollments,
                                        ProgramEnrollment removedProgramEnrollment){
        
        /* Loop through all the hardware that is enrolled and check to see if there is hardware
         * enrolled in the same program and appliance category.  If it qualifies the inventory 
         * should be updated to the new group
         */
        int i;
        removedProgramEnrollment.setEnroll(true);
        for (i = 0;i < programEnrollments.size();i++) {
            ProgramEnrollment programEnrollment = programEnrollments.get(i);
            
            if (removedProgramEnrollment.getInventoryId() != 
                programEnrollment.getInventoryId())
                continue;

            if (removedProgramEnrollment.getProgramId() != 
                programEnrollment.getProgramId())
                continue;

            if (removedProgramEnrollment.getApplianceCategoryId() != 0 &&
                removedProgramEnrollment.getApplianceCategoryId() != 
                   programEnrollment.getApplianceCategoryId())
                continue;
            
            if (removedProgramEnrollment.getApplianceKW() != 0 &&
                removedProgramEnrollment.getApplianceKW() != 
                    programEnrollment.getApplianceKW())
                continue;
                 
            if (removedProgramEnrollment.getLmGroupId() != 0 &&
                removedProgramEnrollment.getLmGroupId() != 
                    programEnrollment.getLmGroupId())
                continue;
                 
            if(removedProgramEnrollment.getRelay() != 0 &&
                    removedProgramEnrollment.getRelay() != 
                        programEnrollment.getRelay())
                continue;
                 
            break;
        }
        
        if (i < programEnrollments.size())
            programEnrollments.remove(i);
        
    }           

    private Program getProgramByName(String programName) {
        List<Program> programList = programDao.getByProgramName(programName);
        return programList.get(0);
    }
    
    private ApplianceCategory getApplianceCategoryByName(String applianceCategoryName, Program program){
        ApplianceCategory applianceCategory = null;
        if (!StringUtils.isBlank(applianceCategoryName)){
            List<ApplianceCategory> applianceCategoryList = applianceCategoryDao.getByApplianceCategoryName(applianceCategoryName);
            applianceCategory = applianceCategoryList.get(0);
        }
        
        if (program.getApplianceCategoryId() != applianceCategory.getApplianceCategoryId())
            throw new IllegalArgumentException("The supplied program does not belong to the supplied appliance category.");

        return applianceCategory;
    }

    private LoadGroup getLoadGroupByName(String loadGroupName, Program program){
        
        LoadGroup loadGroup = null; 
        if (!StringUtils.isBlank(loadGroupName)){
            loadGroup = loadGroupDao.getByLoadGroupName(loadGroupName);
            if (!loadGroup.getProgramIds().contains(program.getProgramId()))
                throw new IllegalArgumentException("The supplied load group does not belong to the supplied program.");
        }
        return loadGroup;
    }

    
    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

    @Autowired
    public void setProgramEnrollmentService(
            ProgramEnrollmentService programEnrollmentService) {
        this.programEnrollmentService = programEnrollmentService;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Required
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

}

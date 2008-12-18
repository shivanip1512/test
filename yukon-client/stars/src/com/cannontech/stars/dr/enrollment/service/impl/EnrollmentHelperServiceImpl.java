package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.yukon.EnergyCompanyRole;
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
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.util.ECUtils;

public class EnrollmentHelperServiceImpl implements EnrollmentHelperService {
    
    private ApplianceDao applianceDao;
    private ApplianceCategoryDao applianceCategoryDao;
    private AuthDao authDao;
    private CustomerAccountDao customerAccountDao;
    private EnrollmentDao enrollmentDao;
    private LoadGroupDao loadGroupDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ProgramDao programDao;
    private ProgramEnrollmentService programEnrollmentService;
    private StarsDatabaseCache starsDatabaseCache;
    
    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user){
        
        CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(enrollmentHelper.getAccountNumber(), user);
        List<ProgramEnrollment> enrollmentData = 
            enrollmentDao.getActiveEnrollmentsByAccountId(customerAccount.getAccountId());

        ProgramEnrollment programEnrollment = buildProgrameEnrollment(enrollmentHelper, user);
        
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            addProgramEnrollment(enrollmentData, programEnrollment, enrollmentHelper.isSeasonalLoad());
        }
        if (enrollmentEnum == EnrollmentEnum.UNENROLL) {
            removeProgramEnrollment(enrollmentData, programEnrollment);
        }
        
        // Processes the enrollment requests
        ProgramEnrollmentResultEnum applyEnrollmentRequests = 
            programEnrollmentService.applyEnrollmentRequests(customerAccount,
                                                             enrollmentData,
                                                             user);
        
        if (applyEnrollmentRequests.getFormatKey().equals(ProgramEnrollmentResultEnum.FAILURE)){
            throw new IllegalArgumentException("Program Enrollment Failed.");
        }
        if (applyEnrollmentRequests.getFormatKey().equals(ProgramEnrollmentResultEnum.NOT_CONFIGURED_CORRECTLY)){
            throw new IllegalArgumentException("Incorrect Configuration.");
        }
        
        // Updates the applianceKW if the process was an enrollment.
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            List<LMHardwareConfiguration> lmHardwareConfigurations = 
                lmHardwareControlGroupDao.getOldConfigDataByInventoryIdAndGroupId(programEnrollment.getInventoryId(),
                                                                                  programEnrollment.getLmGroupId());
            if (lmHardwareConfigurations.size() == 1) {
                applianceDao.updateApplianceKW(lmHardwareConfigurations.get(0).getApplianceId(),
                                               enrollmentHelper.getApplianceKW());
            } else {
                throw new DuplicateEnrollmentException("A duplicate enrollment entry was found in your database.  Please fix as soon as possible.");
            }
        }
    }

    private ProgramEnrollment buildProgrameEnrollment(EnrollmentHelper enrollmentHelper, LiteYukonUser user){
        
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getBySerialNumber(enrollmentHelper.getSerialNumber());
        /* This part of the method will get all the energy company ids that can have 
         * an appliance category this energy company can use.
         */
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        List<Integer> energyCompanyIds = new ArrayList<Integer>();
        if(authDao.checkRoleProperty(energyCompany.getUserID(), EnergyCompanyRole.INHERIT_PARENT_APP_CATS )) {
            List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
            
            for (LiteStarsEnergyCompany ec : allAscendants) {
                energyCompanyIds.add(ec.getEnergyCompanyID());
            }
        } else {
            energyCompanyIds.add(energyCompany.getEnergyCompanyID());
        }
        
        /*
         * Gets the program, appliance category, and load group information.  These 
         * methods also take care of the validation for these three types and
         * includes checking to see if they belong to one another.
         */
        Program program = programDao.getByProgramName(enrollmentHelper.getProgramName(),
                                                      energyCompanyIds);
        ApplianceCategory applianceCategory = getApplianceCategoryByName(enrollmentHelper.getApplianceCategoryName(), 
                                                                         program,
                                                                         energyCompanyIds);
        LoadGroup loadGroup = getLoadGroupByName(enrollmentHelper.getLoadGroupName(), program);
        
        /* Builds up the program enrollment object that will be 
         * used later on to enroll or unenroll.
         */
        ProgramEnrollment programEnrollment = new ProgramEnrollment();
        programEnrollment.setInventoryId(lmHardwareBase.getInventoryId());
        programEnrollment.setProgramId(program.getProgramId());
        programEnrollment.setApplianceKW(enrollmentHelper.getApplianceKW());
        
        if (applianceCategory != null) {
            programEnrollment.setApplianceCategoryId(applianceCategory.getApplianceCategoryId());
        } else {
            programEnrollment.setApplianceCategoryId(program.getApplianceCategoryId());
        }
        if (loadGroup != null) {
            programEnrollment.setLmGroupId(loadGroup.getLoadGroupId());
        }
        if (!StringUtils.isBlank(enrollmentHelper.getRelay())) {
            programEnrollment.setRelay(enrollmentHelper.getRelay());
        }
        
        return programEnrollment;
    }
    
    protected void addProgramEnrollment(List<ProgramEnrollment> programEnrollments,
                                        ProgramEnrollment newProgramEnrollment,
                                        boolean seasonalLoad){
        boolean isProgramEnrollmentEnrolled = false;
       
        for (ProgramEnrollment programEnrollment : programEnrollments) {
            
            if (programEnrollment.getApplianceCategoryId() == newProgramEnrollment.getApplianceCategoryId()){
                if (programEnrollment.getInventoryId() == newProgramEnrollment.getInventoryId()) {
                    if (programEnrollment.getRelay() == newProgramEnrollment.getRelay()) {
                        if (seasonalLoad){
                            if (programEnrollment.getProgramId() == newProgramEnrollment.getProgramId()) {
                                programEnrollment.update(newProgramEnrollment);
                                programEnrollment.setEnroll(true);
                                isProgramEnrollmentEnrolled = true;
                            } else {
                                continue;
                            }
                        } else {
                            programEnrollment.update(newProgramEnrollment);
                            programEnrollment.setEnroll(true);
                            isProgramEnrollmentEnrolled = true;
                        }
                    } else {
                        if (programEnrollment.getProgramId() == newProgramEnrollment.getProgramId()){
                            programEnrollment.update(newProgramEnrollment);
                            programEnrollment.setEnroll(true);
                            isProgramEnrollmentEnrolled = true;
                        }
                    }
                } else {
                    if(programEnrollment.getProgramId() == newProgramEnrollment.getProgramId()){
                        programEnrollment.setLmGroupId(newProgramEnrollment.getLmGroupId());
                        programEnrollment.setEnroll(true);
                    }
                }
            }
        }    
        
        if (!isProgramEnrollmentEnrolled) {
            programEnrollments.add(newProgramEnrollment);
        }
    }
    
    protected void removeProgramEnrollment(List<ProgramEnrollment> programEnrollments,
                                           ProgramEnrollment removedProgramEnrollment){
        
        int i;
        removedProgramEnrollment.setEnroll(true);
        for (i = 0;i < programEnrollments.size();i++) {
            ProgramEnrollment programEnrollment = programEnrollments.get(i);
            
            if (removedProgramEnrollment.equivalent(programEnrollment)) {
                break;
            }
        }
        
        if (i < programEnrollments.size()){
            programEnrollments.remove(i);
        } else {
            throw new NotFoundException("The given program enrollment was not found in the current system.");
        }
        
    }           

    private ApplianceCategory getApplianceCategoryByName(String applianceCategoryName, Program program, List<Integer> energyCompanyIds){
        
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
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
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

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

}

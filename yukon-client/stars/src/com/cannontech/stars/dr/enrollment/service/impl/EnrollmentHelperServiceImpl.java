package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.model.EnrolledDevicePrograms;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.google.common.collect.Lists;

public class EnrollmentHelperServiceImpl implements EnrollmentHelperService {
    
    private ApplianceDao applianceDao;
    private ApplianceCategoryDao applianceCategoryDao;
    private RolePropertyDao rolePropertyDao;
    private CustomerAccountDao customerAccountDao;
    private EnrollmentDao enrollmentDao;
    private LoadGroupDao loadGroupDao;
    private ProgramService programService;
    private ProgramEnrollmentService programEnrollmentService;
    private StarsDatabaseCache starsDatabaseCache;
    private StarsSearchDao starsSearchDao;    
	private InventoryDao inventoryDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    
    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user){

        CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(enrollmentHelper.getAccountNumber(),
                                                                                user);
        List<ProgramEnrollment> enrollmentData = 
            enrollmentDao.getActiveEnrollmentsByAccountId(customerAccount.getAccountId());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);        
        String trackHwAddr = energyCompany.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
        boolean useHardwareAddressing = Boolean.parseBoolean(trackHwAddr);
        
        ProgramEnrollment programEnrollment = buildProgrameEnrollment(enrollmentHelper, customerAccount, user, enrollmentEnum);
        
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            addProgramEnrollment(enrollmentData, programEnrollment, enrollmentHelper.isSeasonalLoad(), useHardwareAddressing);
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
            List<Appliance> appliances = 
                applianceDao.getByAccountIdAndProgramIdAndInventoryId(customerAccount.getAccountId(),
                                                                      programEnrollment.getProgramId(),
                                                                      programEnrollment.getInventoryId());
            if (appliances.size() == 1) {
                if (enrollmentHelper.getApplianceKW() != null){
                    applianceDao.updateApplianceKW(appliances.get(0).getApplianceId(),
                                                   enrollmentHelper.getApplianceKW());
                }
            } else {
                throw new DuplicateEnrollmentException("A duplicate enrollment entry was found in your database.  Please fix as soon as possible.");
            }
        }
    }

    private ProgramEnrollment buildProgrameEnrollment(EnrollmentHelper enrollmentHelper, CustomerAccount account, LiteYukonUser user, EnrollmentEnum enrollmentEnum){

        //get the energyCompany for the user
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        
        LiteInventoryBase liteInv = null;
        try {
            liteInv = starsSearchDao.searchLMHardwareBySerialNumber(enrollmentHelper.getSerialNumber(), energyCompany);
        } catch (ObjectInOtherEnergyCompanyException e) {
            if(enrollmentEnum.equals(EnrollmentEnum.UNENROLL)) {
                liteInv = (LiteInventoryBase) e.getObject();
            } else {
                throw new RuntimeException(e);
            }
        }
        if (liteInv == null) {
            throw new IllegalArgumentException("The supplied piece of hardware was not found: " + enrollmentHelper.getSerialNumber());
        }        
		if (liteInv.getAccountID() != account.getAccountId()) {
            throw new IllegalArgumentException("The supplied piece of hardware: " + enrollmentHelper.getSerialNumber() + 
            		" does not belong to the supplied account: " + enrollmentHelper.getAccountNumber());
        }

        /* This part of the method will get all the energy company ids that can have 
         * an appliance category this energy company can use.
         */
        List<Integer> energyCompanyIds = new ArrayList<Integer>();
        if (rolePropertyDao.checkProperty(YukonRoleProperty.INHERIT_PARENT_APP_CATS,
                                          energyCompany.getUser())) {
            List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
            
            for (LiteStarsEnergyCompany ec : allAscendants) {
                energyCompanyIds.add(ec.getEnergyCompanyID());
            }
        } else {
            energyCompanyIds.add(energyCompany.getEnergyCompanyID());
        }
        
        /*
         *  This handles an unenrollment with no program given.  In this case we
         *  we just want to unenroll the device from every program it is enrolled.
         */
        if(enrollmentEnum.equals(EnrollmentEnum.UNENROLL) &&
           StringUtils.isEmpty(enrollmentHelper.getProgramName())) {
        	ProgramEnrollment programEnrollment = new ProgramEnrollment();
	        programEnrollment.setInventoryId(liteInv.getInventoryID());
	        programEnrollment.setProgramId(0);
	        return programEnrollment;
        } else {
	        /*
	         * Gets the program, appliance category, and load group information.  These 
	         * methods also take care of the validation for these three types and
	         * includes checking to see if they belong to one another.
	         */
        	Program program = programService.getByProgramName(enrollmentHelper.getProgramName(), energyCompany); 

	        ApplianceCategory applianceCategory = getApplianceCategoryByName(enrollmentHelper.getApplianceCategoryName(), 
	                                                                         program,
	                                                                         energyCompanyIds);
	        LoadGroup loadGroup = getLoadGroupByName(enrollmentHelper.getLoadGroupName(), program);

	        /* Builds up the program enrollment object that will be 
	         * used later on to enroll or unenroll.
	         */
	        ProgramEnrollment programEnrollment = new ProgramEnrollment();
	        programEnrollment.setInventoryId(liteInv.getInventoryID());
	        programEnrollment.setProgramId(program.getProgramId());
	        if (enrollmentHelper.getApplianceKW() != null) {
	        	programEnrollment.setApplianceKW(enrollmentHelper.getApplianceKW());
	        }
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
        
    }
    
    protected void addProgramEnrollment(List<ProgramEnrollment> programEnrollments,
                                        ProgramEnrollment newProgramEnrollment,
                                        boolean seasonalLoad, boolean useHardwareAddressing){
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
                } else if (!useHardwareAddressing) {
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
        
    	removedProgramEnrollment.setEnroll(true);

    	List<ProgramEnrollment> programEnrollmentResults = new ArrayList<ProgramEnrollment>();
    	programEnrollmentResults.addAll(programEnrollments);
    	
    	boolean found = false;
    	for (int i = 0;i < programEnrollments.size();i++) {
    		ProgramEnrollment programEnrollment = programEnrollments.get(i);
    		if (removedProgramEnrollment.getProgramId() == 0){
    			if(removedProgramEnrollment.getInventoryId() == programEnrollment.getInventoryId()){
    				programEnrollmentResults.remove(programEnrollment);
    				found = true;
    			}
    		}
    			
    		if (removedProgramEnrollment.equivalent(programEnrollment)) {
    			programEnrollmentResults.remove(programEnrollment);
    			found = true;
    		}
    	}
        if (!found && removedProgramEnrollment.getProgramId() > 0) {
            throw new NotFoundException("Enrollment not found for program [" + removedProgramEnrollment.getProgramId() + "]");            
        }
    	programEnrollments.retainAll(programEnrollmentResults);
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
    
    
	
	@Override
	public List<EnrolledDevicePrograms> getEnrolledDeviceProgramsByAccountNumber(String accountNumber, Date startDate, Date stopDate, LiteYukonUser user) throws AccountNotFoundException {
		
		// account
		CustomerAccount account = null;
		try {
			account = customerAccountDao.getByAccountNumber(accountNumber, user);
		} catch (NotFoundException e) {
			throw new AccountNotFoundException("Account not found: " + accountNumber, e);
		}
		
		// inventory
		List<Integer> inventoryIdList = inventoryDao.getInventoryIdsByAccount(account.getAccountId());
		List<LiteStarsLMHardware> inventoryList = starsInventoryBaseDao.getLMHardwareForIds(inventoryIdList);
		
		// enrolledDeviceProgramsList
		List<EnrolledDevicePrograms> enrolledDeviceProgramsList = Lists.newArrayListWithExpectedSize(inventoryIdList.size());
		for (LiteStarsLMHardware lmHardware : inventoryList) {
			
			String serialNumber = lmHardware.getManufacturerSerialNumber();
			
			List<Program> programs = enrollmentDao.getAllEnrolledProgramsByInventoryId(lmHardware.getInventoryID(), startDate, stopDate);
			MappingList<Program, String> programNames = new MappingList<Program, String>(programs, new ObjectMapper<Program, String>() {
	            public String map(Program from) {
	                return from.getProgramPaoName();
	            }
	        });
			
			if (programNames.size() > 0) {
				EnrolledDevicePrograms enrolledDevicePrograms = new EnrolledDevicePrograms(serialNumber, programNames);
				enrolledDeviceProgramsList.add(enrolledDevicePrograms);
			}
		}
		
		return enrolledDeviceProgramsList;
	}

    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
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
    public void setProgramEnrollmentService(
            ProgramEnrollmentService programEnrollmentService) {
        this.programEnrollmentService = programEnrollmentService;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }
    
    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }
    
    @Autowired
	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
}

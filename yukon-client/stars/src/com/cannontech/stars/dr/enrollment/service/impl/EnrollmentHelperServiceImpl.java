package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.ECMappingDao;
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
import com.cannontech.stars.dr.enrollment.model.EnrollmentEventLoggingData;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelperAdapter;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EnrollmentHelperServiceImpl implements EnrollmentHelperService {
    
    private AccountEventLogService accountEventLogService;
    private ApplianceDao applianceDao;
    private ApplianceCategoryDao applianceCategoryDao;
    private CustomerAccountDao customerAccountDao;
    private EnrollmentDao enrollmentDao;
    private LoadGroupDao loadGroupDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private ProgramDao programDao;
    private ProgramService programService;
    private ProgramEnrollmentService programEnrollmentService;
    private StarsDatabaseCache starsDatabaseCache;
    private StarsSearchDao starsSearchDao;    
	private InventoryDao inventoryDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private ECMappingDao ecMappingDao;

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
        }

        applyEnrollments(enrollments, customerAccount, userContext.getYukonUser());
        
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

    public EnrollmentEventLoggingData getEventLoggingData(ProgramEnrollment programEnrollment){
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(programEnrollment.getInventoryId());

        LoadGroup loadGroup = null; 
        if (programEnrollment.getLmGroupId() != 0) {    //need check for != 0 for when track_hardware_addressing is true, or virtual program
            loadGroup = loadGroupDao.getById(programEnrollment.getLmGroupId());
        }
        Program program = programDao.getByProgramId(programEnrollment.getAssignedProgramId());
        
        return new EnrollmentEventLoggingData(lmHardwareBase, loadGroup, program);
    }

    public void doEnrollment(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user){
    	EnrollmentHelperAdapter enrollmentHelperAdapter = buildEnrollmentHelperAdapter(enrollmentHelper, enrollmentEnum, user);
    	doEnrollment(enrollmentHelperAdapter, enrollmentEnum, user);    	
    }
    
    public void doEnrollment(EnrollmentHelperAdapter enrollmentHelperAdapter, EnrollmentEnum enrollmentEnum, LiteYukonUser user){

        CustomerAccount customerAccount = enrollmentHelperAdapter.getCustomerAccount();
        EnrollmentHelper enrollmentHelper = enrollmentHelperAdapter.getEnrollmentHelper();
        
        // Get the current enrollments.  This list will be updated to reflect
        // the desired enrollment data then passed to applyEnrollments which
        // will make it so.
        List<ProgramEnrollment> enrollmentData = 
            enrollmentDao.getActiveEnrollmentsByAccountId(customerAccount.getAccountId());
        
        LiteStarsEnergyCompany energyCompany = enrollmentHelperAdapter.getLiteStarsEnergyCompany();        
        String trackHwAddr = energyCompany.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
        boolean useHardwareAddressing = Boolean.parseBoolean(trackHwAddr);
        
        ProgramEnrollment programEnrollment = buildProgrameEnrollment(enrollmentHelperAdapter, enrollmentEnum);
        
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            addProgramEnrollment(enrollmentData, programEnrollment, enrollmentHelper.isSeasonalLoad(), useHardwareAddressing);
        }
        if (enrollmentEnum == EnrollmentEnum.UNENROLL) {
            removeProgramEnrollment(enrollmentData, programEnrollment);
        }
        applyEnrollments(enrollmentData, customerAccount, user);
        
        // Updates the applianceKW if the process was an enrollment.
        if (enrollmentEnum == EnrollmentEnum.ENROLL) {
            List<Appliance> appliances = 
                applianceDao.getByAccountIdAndProgramIdAndInventoryId(customerAccount.getAccountId(),
                                                                      programEnrollment.getAssignedProgramId(),
                                                                      programEnrollment.getInventoryId());
            if (appliances.size() == 1) {
                if (enrollmentHelper.getApplianceKW() != null){
                    applianceDao.updateApplianceKW(appliances.get(0).getApplianceId(),
                                                   enrollmentHelper.getApplianceKW());
                }
            } else if (appliances.size() > 1) {
                throw new DuplicateEnrollmentException("duplicate appliance for account " +
                                                       customerAccount.getAccountId() +
                                                       ", assigned program " +
                                                       programEnrollment.getAssignedProgramId() +
                                                       ", inventory " + programEnrollment.getInventoryId() +
                                                       ".  Please fix as soon as possible.");
            } else {
                throw new RuntimeException("no appliance for account " +
                                           customerAccount.getAccountId() +
                                           ", assigned program " +
                                           programEnrollment.getAssignedProgramId() +
                                           ", inventory " + programEnrollment.getInventoryId() +
                                           ".  Please fix as soon as possible.");
            }
        }
        
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

    /**
     * This method takes a list of enrollment data and makes that list the exact
     * list of enrollments for the customer. If they are enrolled in something
     * not in the list, they will be unenrolled. If they are not enrolled in
     * something in the list, they will be enrolled in it.
     */
    private void applyEnrollments(List<ProgramEnrollment> enrollmentData,
            CustomerAccount customerAccount, LiteYukonUser user) {
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
    }

    private ProgramEnrollment buildProgrameEnrollment(EnrollmentHelperAdapter enrollmentHelperAdapter, EnrollmentEnum enrollmentEnum){

    	LMHardwareBase lmHardwareBase = enrollmentHelperAdapter.getLmHardwareBase();
    	EnrollmentHelper enrollmentHelper = enrollmentHelperAdapter.getEnrollmentHelper();
        LiteStarsEnergyCompany energyCompany = enrollmentHelperAdapter.getLiteStarsEnergyCompany();

        /* This part of the method will get all the energy company ids that can have 
         * an appliance category this energy company can use.
         */
		Set<Integer> energyCompanyIds = ecMappingDao.getInheritedEnergyCompanyIds(energyCompany);
        
        /*
         *  This handles an unenrollment with no program given.  In this case we
         *  we just want to unenroll the device from every program it is enrolled.
         */
        if(enrollmentEnum.equals(EnrollmentEnum.UNENROLL) &&
           StringUtils.isEmpty(enrollmentHelper.getProgramName())) {
        	ProgramEnrollment programEnrollment = new ProgramEnrollment();
	        programEnrollment.setInventoryId(lmHardwareBase.getInventoryId());
	        programEnrollment.setAssignedProgramId(0);
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
	        programEnrollment.setInventoryId(lmHardwareBase.getInventoryId());
	        programEnrollment.setAssignedProgramId(program.getProgramId());
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
                            if (programEnrollment.getAssignedProgramId() == newProgramEnrollment.getAssignedProgramId()) {
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
                        if (programEnrollment.getAssignedProgramId() == newProgramEnrollment.getAssignedProgramId()){
                            programEnrollment.update(newProgramEnrollment);
                            programEnrollment.setEnroll(true);
                            isProgramEnrollmentEnrolled = true;
                        }
                    }
                } else if (!useHardwareAddressing) {
                    if(programEnrollment.getAssignedProgramId() == newProgramEnrollment.getAssignedProgramId()){
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
    		if (removedProgramEnrollment.getAssignedProgramId() == 0){
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
        if (!found && removedProgramEnrollment.getAssignedProgramId() > 0) {
            throw new NotFoundException("Enrollment not found for program [" + removedProgramEnrollment.getAssignedProgramId() + "]");            
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
			
			List<Program> programs = enrollmentDao.getEnrolledProgramIdsByInventory(lmHardware.getInventoryID(), startDate, stopDate);
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
	
    private EnrollmentHelperAdapter buildEnrollmentHelperAdapter(EnrollmentHelper enrollmentHelper, EnrollmentEnum enrollmentEnum, LiteYukonUser user) {
    	CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(enrollmentHelper.getAccountNumber(), user);
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	LiteInventoryBase liteInventoryBase;
        try {
        	liteInventoryBase = starsSearchDao.searchLMHardwareBySerialNumber(enrollmentHelper.getSerialNumber(), energyCompany);
        } catch (ObjectInOtherEnergyCompanyException e) {
            if(enrollmentEnum.equals(EnrollmentEnum.UNENROLL)) {
                liteInventoryBase = (LiteInventoryBase) e.getObject();
            } else {
                throw new RuntimeException(e);
            }
        }
        if (liteInventoryBase == null) {
            throw new IllegalArgumentException("The supplied piece of hardware was not found: " + enrollmentHelper.getSerialNumber());
        }        
		if (liteInventoryBase.getAccountID() != customerAccount.getAccountId()) {
            throw new IllegalArgumentException("The supplied piece of hardware: " + enrollmentHelper.getSerialNumber() + 
            		" does not belong to the supplied account: " + enrollmentHelper.getAccountNumber());
        }
		
		LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(liteInventoryBase.getInventoryID());
    	EnrollmentHelperAdapter enrollmentHelperAdapter = new EnrollmentHelperAdapter(enrollmentHelper, customerAccount, lmHardwareBase, energyCompany);
    	return enrollmentHelperAdapter;
    }

	@Autowired
	public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
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
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
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
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
}

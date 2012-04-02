package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.OpenInterval;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class OptOutLimitModel extends BareDatedReportModelBase<OptOutLimitModel.ModelRow> implements EnergyCompanyModelAttributes, UserContextModelAttributes {

    private Logger log = YukonLogManager.getLogger(OptOutLimitModel.class);

    private CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
    private DateFormattingService dateFormattingService = YukonSpringHook.getBean("dateFormattingService", DateFormattingService.class);
    private ECMappingDao ecMappingDao = YukonSpringHook.getBean("ecMappingDao", ECMappingDao.class);
    private YukonEnergyCompanyService yukonEnergyCompanyService = YukonSpringHook.getBean("yukonEnergyCompanyService", YukonEnergyCompanyService.class);
    private EnrollmentDao enrollmentDao =  YukonSpringHook.getBean("enrollmentDao", EnrollmentDao.class);
    private InventoryBaseDao inventoryBaseDao = YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
    private LMHardwareControlGroupDao lmHardwareControlGroupDao = YukonSpringHook.getBean("lmHardwareControlGroupDao", LMHardwareControlGroupDao.class);
    private ProgramDao programDao =  YukonSpringHook.getBean("starsProgramDao", ProgramDao.class);
    private OptOutEventDao optOutEventDao = YukonSpringHook.getBean("optOutEventDao", OptOutEventDao.class);
    private OptOutService optOutService = YukonSpringHook.getBean("optOutService", OptOutService.class);
    
    private Function<LMHardwareControlGroup, Integer> lmHardwareControlGroupToAccountIdFunction =
            new Function<LMHardwareControlGroup, Integer>() {
                @Override
                public Integer apply(LMHardwareControlGroup lmHardwareControlGroup) {
                    return lmHardwareControlGroup.getAccountId();
                }};
        
        private Function<LMHardwareControlGroup, Integer> lmHardwareControlGroupToInventoryIdFunction = 
            new Function<LMHardwareControlGroup, Integer>() {
                @Override
                public Integer apply(LMHardwareControlGroup lmHardwareControlGroup) {
                    return lmHardwareControlGroup.getInventoryId();
                }};
    
    private YukonUserContext userContext;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private int energyCompanyId;
    private boolean showOverridesThatDoNotCount;
    private Set<Integer> accountIds;
    private Set<Integer> inventoryIds;
    private Set<Integer> programIds;
    private Set<Integer> userIds;
    
    static public class ModelRow {
        public String accountNumberAndName;
        public String accountNumber;	// accountNumber and accountName also exist as their separate fields to  
        public String accountName;		//  allow for cvs export of the non combined data.
        public String serialNumber;
        public String alternateTrackingNumber;
        public String enrolledProgram;
        public Integer numberOfOverridesUsed;
        public String issuingUser;
        public String optOutStart;
        public String optOutStop;
    }

    private Comparator<OptOutLimitModel.ModelRow> optOutLimitModelRowComparator = 
        new Comparator<OptOutLimitModel.ModelRow>(){

            @Override
            public int compare(OptOutLimitModel.ModelRow modelOne, OptOutLimitModel.ModelRow modelTwo) {
                
                int compareToIgnoreCase = modelOne.accountNumber.compareToIgnoreCase(modelTwo.accountNumber);
                
                if (compareToIgnoreCase == 0) {
                	if (modelOne.serialNumber == null) {
                		if (modelTwo.serialNumber == null) {
                			return 0;
                		} else {
                			return -1;	//modelTwo
                		}
                	} else if (modelTwo.serialNumber == null) {
            			return 1;	//modelOne
            		} else {
            			return modelOne.serialNumber.compareTo(modelTwo.serialNumber);
            		}
                }

                return compareToIgnoreCase;
            }
        };
    
    public String getTitle() {
        return "Opt Out Limit Report";
    }

    public int getRowCount() {
        return data.size();
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    @Override
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public boolean getShowOverridesThatDoNotCount(){
        return showOverridesThatDoNotCount;
    }
    public void setShowOverridesThatDoNotCount(boolean showOverridesThatDoNotCount) {
        this.showOverridesThatDoNotCount = showOverridesThatDoNotCount;
    }
    
    public Set<Integer> getAccountIds() {
        return accountIds;
    }
    public void setAccountIds(Set<Integer> accountIds) {
        this.accountIds = accountIds;
    }
    
    public Set<Integer> getInventoryIds() {
        return inventoryIds;
    }
    public void setInventoryIds(Set<Integer> inventoryIds) {
        this.inventoryIds = inventoryIds;
    }
    
    public Set<Integer> getProgramIds() {
        return programIds;
    }
    public void setProgramIds(Set<Integer> programIds) {
        this.programIds = programIds;
    }
    
    public Set<Integer> getUserIds() {
        return userIds;
    }
    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }
    
    public void doLoadData() {

        // get all of the customers
        Validate.notNull(getStopDateAsInstant(), "End date must not be null");

        ReadableInstant optOutEndDate = getStopDateAsInstant();
        YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(userContext.getYukonUser());
        
        // Find the limits for the given opt out end date
        List<LiteYukonGroup> residentialGroups = ecMappingDao.getResidentialGroups(energyCompany.getEnergyCompanyId());
        for (LiteYukonGroup residentialGroup : residentialGroups) {
            OptOutLimit residentialGroupOptOutLimit = findReportOptOutLimit(optOutEndDate,  residentialGroup);

            // Check to see if an opt out limit exists, and use the opt out limit object to build up the opt out limit for a group and
            // also setup the time period for the report. 
            if (residentialGroupOptOutLimit == null) continue;
            Integer optOutLimit = residentialGroupOptOutLimit.getLimit();
            DateTime reportStartDate = getReportStartDate(optOutEndDate, energyCompany, residentialGroupOptOutLimit);
            OpenInterval reportInterval = OpenInterval.createClosed(reportStartDate, optOutEndDate);
            
            // Check to see if users where selected and use them to find the report data.
            if (userIds != null) {
                for (Integer userId : userIds) {
                    List<OverrideHistory> overrideHistoryList = 
                        optOutEventDao.getOptOutHistoryByLogUserId(userId, reportStartDate, optOutEndDate, residentialGroup);
                    
                    addOverrideHistoryToModel(overrideHistoryList, optOutLimit);
                }
            }
            
            // Check to see if inventories where selected and uses them to find the report data.
            if (inventoryIds != null) {
                for (Integer inventoryId : inventoryIds){
                    List<OverrideHistory> overrideHistoryList =
                        optOutEventDao.getOptOutHistoryForInventory(inventoryId, reportStartDate, optOutEndDate, residentialGroup);
                    addOverrideHistoryToModel(overrideHistoryList, optOutLimit);
                }
            }
            
            // Check to see if accounts where selected and uses them to find the report data.
            if (accountIds != null) {
                for (Integer accountId : accountIds) {
                    List<OverrideHistory> overrideHistoryList =
                        optOutEventDao.getOptOutHistoryForAccount(accountId, reportStartDate, optOutEndDate, residentialGroup);
                    addOverrideHistoryToModel(overrideHistoryList, optOutLimit);
                }
            }
            
            // Check to see if programs where selected and uses them to find the report data.
            if (programIds != null) {
                // Get the enrollments for the programIds supplied
                List<Integer> groupIdsFromSQL = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
                Set<Integer> usableEnergyCompanies = ecMappingDao.getChildEnergyCompanyIds(energyCompanyId);
                List<LMHardwareControlGroup> enrollments = lmHardwareControlGroupDao.getIntersectingEnrollments(usableEnergyCompanies, groupIdsFromSQL, reportInterval);
                
                // Build up a multimap of account ids to enrolled inventory ids.
                ImmutableListMultimap<Integer, LMHardwareControlGroup> accountIdToLMHardwareControlGroups = 
                    Multimaps.index(enrollments, lmHardwareControlGroupToAccountIdFunction);
                ListMultimap<Integer, Integer> accountIdToInventoryIds = 
                    Multimaps.transformValues(accountIdToLMHardwareControlGroups, lmHardwareControlGroupToInventoryIdFunction);
                
                // Create the model rows of the enrolled inventories found on the account.  We're also using the set to remove any duplicate inventory
                // entries that may occur from enrolling and unenrolling a device mulitple times.  Records need to be grouped by account for the final report
                for (Integer accountId : accountIdToInventoryIds.keySet()) {
                    Set<Integer> enrolledInventoryIds = Sets.newHashSet(accountIdToInventoryIds.get(accountId));
                    for (int enrolledInventoryId : enrolledInventoryIds) {
                        List<OverrideHistory> overrideHistoryList =
                                optOutEventDao.getOptOutHistoryForInventory(enrolledInventoryId, reportStartDate, optOutEndDate, residentialGroup);
                        addOverrideHistoryToModel(overrideHistoryList, optOutLimit);
                    }
                }
            }
        }

        Collections.sort(data, optOutLimitModelRowComparator);        
        log.info("Report Records Collected from Database: " + data.size());
    }

    /**
     * This method retrieves the opt out limit for the supplied stop date and login group.  This is important because opt out limits are
     * based off of login groups not devices nor accounts.
     */
    private OptOutLimit findReportOptOutLimit(ReadableInstant optOutStopDate, LiteYukonGroup residentialGroup) {
        List<OptOutLimit> residentialGroupOptOutLimits = optOutService.findCurrentOptOutLimit(residentialGroup);

        for (OptOutLimit optOutLimit : residentialGroupOptOutLimits) {
            int stopDateMonth = optOutStopDate.get(DateTimeFieldType.monthOfYear());
            if  (optOutLimit.isMonthUnderLimit(stopDateMonth)){
                return optOutLimit;
            }
        }
        
        return null;
    }

    /**
     * This method returns the start date for the reports.  This is generated by selecting the first day at midnight of the opt out limit start month
     */
    private DateTime getReportStartDate(ReadableInstant stopDate, YukonEnergyCompany energyCompany, OptOutLimit residentialGroupOptOutLimit) {
        DateTime reportStopDate = new DateTime(stopDate, energyCompany.getDefaultDateTimeZone());
        DateTime reportStartDate = reportStopDate.withMonthOfYear(residentialGroupOptOutLimit.getStartMonth()).withDayOfMonth(1).toDateMidnight().toDateTime(energyCompany.getDefaultDateTimeZone());

        // Adjust the start date to the year before if needed.
        if (residentialGroupOptOutLimit.getStartMonth() > residentialGroupOptOutLimit.getStopMonth()) {
            return reportStartDate.minusYears(1);
        }

        return reportStartDate;
    }

    /**
     * This method takes the override history objects and generates all the date
     * needed to make a report row entry.
     */
    private void addOverrideHistoryToModel(List<OverrideHistory> overrideHistoryList, int optOutLimitCount) {

        final Set<Integer> reportableInventory = getReportableInventory(overrideHistoryList, optOutLimitCount);
        
        Iterable<OverrideHistory> overrideHistoryMeetingTheLimit = Iterables.filter(overrideHistoryList, new Predicate<OverrideHistory>() {
            @Override
            public boolean apply(OverrideHistory overrideHistory) {
                return reportableInventory.contains(overrideHistory.getInventoryId());
            }});

        
        for (OverrideHistory overrideHistory : overrideHistoryMeetingTheLimit) {
            
            // If the user doesn't want to see opt outs that don't count then lets not add them.
            if (!showOverridesThatDoNotCount && !overrideHistory.isCountedAgainstLimit()) {
                continue;
            }
            
            CustomerAccountWithNames customerAccountWithName =
                customerAccountDao.getAcountWithNamesByAccountNumber(overrideHistory.getAccountNumber(), energyCompanyId);
            
            InventoryBase inventory = null; 
            List<Program> programList = null;
            try {
                programList = enrollmentDao.getEnrolledProgramIdsByInventory(overrideHistory.getInventoryId(), overrideHistory.getStartDate(), overrideHistory.getStopDate());
                inventory = inventoryBaseDao.getById(overrideHistory.getInventoryId());

            } catch(EmptyResultDataAccessException e) {/* Inventory no longer exists. */}
                    
            if (programIds != null) {
                if (programList == null) {
                    continue;
                }
                    
                boolean enrolledProgramIdInProgramIdList = isProgramInList(programList);
                if (!enrolledProgramIdInProgramIdList) {
                    continue;
                }
            }

            // Adding the gathered information to the model
            addOptOutLimitRowToModel(customerAccountWithName, inventory, programList, overrideHistory);
        }
    }

    /**
     * This method returns a set of inventoryIds that will be included in the report. 
     */
    private Set<Integer> getReportableInventory(List<OverrideHistory> overrideHistoryList, final int optOutLimitCount) {

        // Get back a list of overrideHistory that only counts toward the opt out limit
        Iterable<OverrideHistory> overrideHistoryThatCounts = Iterables.filter(overrideHistoryList, new Predicate<OverrideHistory>() {
            @Override
            public boolean apply(OverrideHistory overrideHistory) {
                return overrideHistory.isCountedAgainstLimit();
            }
        });
        
        // This list will contain duplicates of a device that has been opted out multiple times in a time period. 
        // This is intentional so that we can check if an inventory is over it's opt out limit.
        final Iterable<Integer> countableOverrideHistoryInventories = 
                Iterables.transform(overrideHistoryThatCounts, new Function<OverrideHistory, Integer>() {
                    @Override
                    public Integer apply(OverrideHistory overrideHistory) {
                        return overrideHistory.getInventoryId();
                    }});
        
        // Creates a list of inventory that have meet their limits for their opt out period. 
        Iterable<Integer> inventoryMeetingTheOptOutLimit = 
                Iterables.filter(countableOverrideHistoryInventories, new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer inventoryId) {
                        return Collections.frequency(Lists.newArrayList(countableOverrideHistoryInventories), inventoryId) >= optOutLimitCount;
                    }});
        
        return Sets.newHashSet(inventoryMeetingTheOptOutLimit);
    }
    
    /**
     * This method checks to see if a list of enrolled programs contains one of the user
     * selected programs.
     */
    private boolean isProgramInList(List<Program> enrolledProgramList) {
        List<Program> selectedPrograms = 
            programDao.getByProgramIds(Lists.newArrayList(programIds));
        
        boolean enrolledProgramIdInProgramIdList = false;
        for (Program enrolledProgram : enrolledProgramList) {
            if (selectedPrograms.contains(enrolledProgram)) {
                enrolledProgramIdInProgramIdList = true;
            }
        }
        
        return enrolledProgramIdInProgramIdList;
    }
    
    /**
     * This method adds a row to the report.
     */
    private void addOptOutLimitRowToModel(CustomerAccountWithNames customerAccountWithName,
                                           InventoryBase inventory, 
                                           List<Program> programList,
                                           OverrideHistory overrideHistory) {
    
            OptOutLimitModel.ModelRow row = new OptOutLimitModel.ModelRow();
            
            row.accountNumber = customerAccountWithName.getAccountNumber();
            row.accountName = customerAccountWithName.getFirstName() + " " + customerAccountWithName.getLastName();
            row.accountNumberAndName = "#" + row.accountNumber + " - " + row.accountName;
            row.serialNumber = overrideHistory.getSerialNumber();
            row.alternateTrackingNumber = customerAccountWithName.getAlternateTrackingNumber();
            row.enrolledProgram = getProgramNames(programList);
            row.numberOfOverridesUsed = overrideHistory.isCountedAgainstLimit() ? 1 : 0;
            row.issuingUser = overrideHistory.getUserName();
            row.optOutStart = dateFormattingService.format(overrideHistory.getStartDate(), DateFormatEnum.BOTH, userContext);
            row.optOutStop = dateFormattingService.format(overrideHistory.getStopDate(), DateFormatEnum.BOTH, userContext);
            
            data.add(row);
    }

    /**
     * This method returns a string representation of a list of programs
     */
    private String getProgramNames(List<Program> programs) {
        String programNamesStr = "";
        
        List<String> programNames =
            Lists.transform(programs, new Function<Program, String>() {
   
                @Override
                public String apply(Program program) {
                    return program.getProgramName();
                }
            });
        
        if (programNames != null && programNames.size() > 0) {
            programNamesStr = programNames.get(0);
            for (int i = 1; i < programNames.size(); i++) {
                programNamesStr += ", " + programNames.get(i);
            }
        }
        
        return programNamesStr;
    }
    
    @Override
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }

    public YukonUserContext getUserContext() {
        return userContext;
    }
    
    @Override
    public boolean useStartDate() {
        return false;
    }
}
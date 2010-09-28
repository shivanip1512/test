package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.controlhistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class OptOutInfoModel extends BareDatedReportModelBase<OptOutInfoModel.ModelRow> implements EnergyCompanyModelAttributes, UserContextModelAttributes {

    private Logger log = YukonLogManager.getLogger(OptOutInfoModel.class);

    private double oneHour = Duration.standardHours(1).getMillis();
    
    private CustomerAccountDao customerAccountDao = 
        (CustomerAccountDao) YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
    private LmControlHistoryUtilService lmControlHistoryUtilService =
        (LmControlHistoryUtilService) YukonSpringHook.getBean("lmControlHistoryUtilService", LmControlHistoryUtilService.class);
    private ProgramDao programDao = 
        (ProgramDao) YukonSpringHook.getBean("starsProgramDao", ProgramDao.class);
    private OptOutService optOutService =
        (OptOutService) YukonSpringHook.getBean("optOutService", OptOutService.class);
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private int energyCompanyId;
    private YukonUserContext userContext;
    private String accountNumbers;
    private Set<Integer> programIds;

    static public class ModelRow {
        public String accountNumberAndName;
        public String serialNumber;
        public String enrolledProgram;
        public Date startTimeOfControl;
        public Date stopTimeOfControl;
        public Double totalProjectedControlHoursForPeriod;
        public Double totalActualControlHoursForPeriod;
        public Date dateOverrideWasScheduled;
        public Date dateOverrideWasActive;
        public Date startTimeOfOverride;
        public Date stopTimeOfOverride;
        public Double totalOverrideHours;
        public Boolean countedTowardOptOutLimit;
    }

    public String getTitle() {
        return "Opt Out Info Report";
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
    public void setUserContext(YukonUserContext userContext){
        this.userContext = userContext;
    }
    
    @Override
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public void setAccountNumbers(String accountNumbers) {
        this.accountNumbers = accountNumbers;
    }
    
    public Set<Integer> getProgramIds() {
        return programIds;
    }

    public void setProgramIds(Set<Integer> programIds) {
        this.programIds = programIds;
    }
    
    public void doLoadData() {

        // get all of the customers
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");

        Instant startInstant = new Instant(getStartDate());
        Instant stopInstant = new Instant(getStopDate());
        
        HashMap<Integer, StarsLMControlHistory> starsControlHistoryCache = Maps.newHashMap();
        
        List<CustomerAccountWithNames> accounts = 
            getCustomerAccountWithNamesFromAccountNumbers(accountNumbers, programIds, energyCompanyId, userContext);

        // Check and see if any programs were supplied by the user.  If so, only use those programs.
        List<Program> userSuppliedReportablePrograms = getUserSuppliedReportablePrograms(programIds);
        
        for (CustomerAccountWithNames account : accounts) {

            List<OverrideHistory> overrideHistoryList = 
                optOutService.getOptOutHistoryForAccount(account.getAccountNumber(), startInstant.toDate(), 
                                                         stopInstant.toDate(), userContext.getYukonUser(), 
                                                         null);

            removeNonReportableOverrideHistoryEntries(userSuppliedReportablePrograms, overrideHistoryList);
            
            for (OverrideHistory overrideHistory : overrideHistoryList) {
                List<Integer> reportableLoadGroupIds = getReportableLoadGroupIdsFromOverrideHistory(overrideHistory);
                
                for (Integer loadGroupId : reportableLoadGroupIds) {
                    
                    // Cache the control history for this group.
                    StarsLMControlHistory allControlEventsForAGroup = starsControlHistoryCache.get(loadGroupId);
                    if(allControlEventsForAGroup == null) {
                        allControlEventsForAGroup = 
                            LMControlHistoryUtil.getSTARSFormattedLMControlHistory(loadGroupId,
                                                                                   startInstant,
                                                                                   stopInstant,
                                                                                   energyCompanyId);
                        
                        starsControlHistoryCache.put(loadGroupId, allControlEventsForAGroup);
                    }

                    for (ControlHistory controlHistory : allControlEventsForAGroup.getControlHistory()) {
                        List<Interval> enrolledControlHistoryList = 
                            lmControlHistoryUtilService.getControHistoryEnrollmentIntervals(controlHistory, 
                                                                                            account.getAccountId(), 
                                                                                            overrideHistory.getInventoryId(), 
                                                                                            loadGroupId);
                        
                        for (Interval enrollmentControlHistory : enrolledControlHistoryList) {
                            if (enrollmentControlHistory.overlaps(overrideHistory.getInterval())) {
                                addOverrideHistoryToModel(account, overrideHistory, enrollmentControlHistory, loadGroupId);
                            }
                        }
                    }
                }
            }
        }

        log.info("Report Records Collected from Database: " + data.size());
        
    }

    private List<Integer> getReportableLoadGroupIdsFromOverrideHistory(OverrideHistory overrideHistory) {

        List<Program> programs = overrideHistory.getPrograms();
        List<Integer> programIds = 
            Lists.transform(programs, new Function<Program, Integer>() {

                @Override
                public Integer apply(Program program) {
                    return program.getProgramId();
                }
            
            });
        List<Integer> reportableLoadGroupIds = 
            programDao.getDistinctGroupIdsByProgramIds(Sets.newHashSet(programIds));
        return reportableLoadGroupIds;
    }

    private List<Program> getUserSuppliedReportablePrograms(Set<Integer> programIds) {
        if (programIds == null || programIds.size() <= 0) {
            return null;
        }
        
        List<Integer> programIdsList = Lists.newArrayList(programIds);
        return programDao.getByProgramIds(programIdsList);
    }

    /**
     * This method goes through and removes any program that is not in the override history list.  
     * This will help us to cut down the amount of control history data we need to generate this report.
     */
    private void removeNonReportableOverrideHistoryEntries(List<Program> userSuppliedReportablePrograms,
                                                           List<OverrideHistory> overrideHistoryList) {
        
        List<OverrideHistory> removableOverrideHistory = Lists.newArrayList();

        if (userSuppliedReportablePrograms != null && 
            userSuppliedReportablePrograms.size() > 0) {
            for (OverrideHistory overrideHistory : overrideHistoryList) {
                List<Program> programs = overrideHistory.getPrograms();
            
                for (Program program : programs) {
                    if (!userSuppliedReportablePrograms.contains(program)) {
                        removableOverrideHistory.add(overrideHistory);
                    }
                }
            }
        }

        overrideHistoryList.removeAll(removableOverrideHistory);
    }

    /**
     * This method takes in a string of accountNumbers and a list of programIds and returns a 
     * list of customerAccountWithNames.  If account numbers are supplied programIds will be ignored.
     */
    public List<CustomerAccountWithNames> 
                getCustomerAccountWithNamesFromAccountNumbers(String accountNumbersStr,
                                                              Set<Integer> programIds,
                                                              int energyCompanyId,
                                                              YukonUserContext userContext) {
        if(!StringUtils.isBlank(accountNumbersStr)) {
            List<CustomerAccountWithNames> accounts = Lists.newArrayList();
            String[] accountNumbersArr = StringUtils.split(accountNumbersStr, ';');
            for (String accountNumber : accountNumbersArr) {
                try {
                    CustomerAccount custAccount = 
                        customerAccountDao.getByAccountNumber(accountNumber, 
                                                              userContext.getYukonUser());
                    CustomerAccountWithNames customerAccountWithNames = 
                        customerAccountDao.getAccountWithNamesByCustomerId(custAccount.getCustomerId(), 
                                                                           energyCompanyId);
                    accounts.add(customerAccountWithNames);

                // No results from the given accountNumber. Ignore this supplied account number. 
                } catch(NotFoundException e) {}
            }
            
            return accounts;
        } else {
            List<Integer> groupIdsFromSQL = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            return customerAccountDao.getAllAccountsWithNamesByGroupIds(energyCompanyId, 
                                                                        groupIdsFromSQL,
                                                                        getStartDate(),
                                                                        getStopDate());
        }
    }
    
    private void addOverrideHistoryToModel(CustomerAccountWithNames account, OverrideHistory overrideHistory, 
                                           Interval enrolledControlHistory, int loadGroupId) {
    
            OptOutInfoModel.ModelRow row = new OptOutInfoModel.ModelRow();
            
            Duration overrideDuration =
                new Duration(new Instant(overrideHistory.getStartDate()), 
                             new Instant(overrideHistory.getStopDate()));
            
            row.accountNumberAndName = "#" + account.getAccountNumber() + " ---- " + account.getLastName() + ", " + account.getFirstName();
            row.serialNumber = overrideHistory.getSerialNumber();
            row.startTimeOfControl = enrolledControlHistory.getStart().toDate();
            row.stopTimeOfControl = enrolledControlHistory.getEnd().toDate();
            row.totalProjectedControlHoursForPeriod = enrolledControlHistory.toDuration().getMillis()/oneHour;
            row.dateOverrideWasScheduled = overrideHistory.getScheduledDate();
            row.startTimeOfOverride = overrideHistory.getStartDate();
            row.stopTimeOfOverride = overrideHistory.getStopDate();
            row.totalOverrideHours = overrideDuration.getMillis()/oneHour;
            row.countedTowardOptOutLimit = overrideHistory.isCountedAgainstLimit();

            String programNames = programDao.getProgramNames(loadGroupId);
            row.enrolledProgram = programNames;
            
            Duration actualControlDuration = 
                enrolledControlHistory.toDuration().minus(getOptOutControlDuration(enrolledControlHistory, 
                                                                                   overrideHistory));
            row.totalActualControlHoursForPeriod = actualControlDuration.getMillis()/oneHour;

            data.add(row);
    }

    
    
    private Duration getOptOutControlDuration(Interval enrolledControlHistory,
                                              OverrideHistory overrideHistory) {
        Interval overlappingInterval = enrolledControlHistory.overlap(overrideHistory.getInterval());
        
        if (overlappingInterval == null) {
            return Duration.ZERO;
        }
        
        return overlappingInterval.toDuration();
    }
}
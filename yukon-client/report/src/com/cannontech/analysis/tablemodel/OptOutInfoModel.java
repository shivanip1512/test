package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.OpenInterval;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.controlHistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class OptOutInfoModel extends BareDatedReportModelBase<OptOutInfoModel.ModelRow> implements EnergyCompanyModelAttributes {

    private Logger log = YukonLogManager.getLogger(OptOutInfoModel.class);

    private double oneHour = Duration.standardHours(1).getMillis();
    
    private CustomerAccountDao customerAccountDao = 
        (CustomerAccountDao) YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
    private LmControlHistoryUtilService lmControlHistoryUtilService =
        (LmControlHistoryUtilService) YukonSpringHook.getBean("lmControlHistoryUtilService", LmControlHistoryUtilService.class);
    private ProgramDao programDao = 
        (ProgramDao) YukonSpringHook.getBean("starsProgramDao", ProgramDao.class);
    private OptOutEventDao optOutEventDao =
        (OptOutEventDao) YukonSpringHook.getBean("optOutEventDao", OptOutEventDao.class);
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private int energyCompanyId;
    private Set<Integer> accountIds;
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
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public Set<Integer> getAccountIds() {
        return accountIds;
    }
    
    public void setAccountIds(Set<Integer> accountIds) {
        this.accountIds = accountIds;
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
        
        List<OverrideHistory> overrideHistoryList = Lists.newArrayList();            
        
        // Check to see if accounts where selected and uses them to find the report data.
        if (accountIds != null) {
            for (Integer accountId : accountIds) {
                overrideHistoryList.addAll(optOutEventDao.getOptOutHistoryForAccount(accountId, getStartDate(), getStopDate()));
            }
        }
        
        // Check to see if programs where selected and uses them to find the report data.
        if (programIds != null) {
            List<Program> suppliedProgramIds = programDao.getByProgramIds(programIds);

            List<Integer> groupIdsFromSQL = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            List<CustomerAccountWithNames> accounts = 
                customerAccountDao.getAllAccountsWithNamesByGroupIds(energyCompanyId, groupIdsFromSQL, getStartDate(), getStopDate());

            for (CustomerAccountWithNames account : accounts) {
                List<OverrideHistory> overrideHistories = 
                    optOutEventDao.getOptOutHistoryForAccount(account.getAccountId(), getStartDate(), getStopDate());
                
                for (OverrideHistory overrideHistory : overrideHistories) {

                    // Retrieve the involved program ids from override history entry  
                    for (Program overrideHistoryProgram : overrideHistory.getPrograms()) {
                        if (suppliedProgramIds.contains(overrideHistoryProgram)) {
                            overrideHistoryList.add(overrideHistory);
                            break;
                        }
                    }
                }
            }
        }
        
        // Iterate through the override history list and build up the data for the report.
        for (OverrideHistory overrideHistory : overrideHistoryList) {
            List<Integer> reportableLoadGroupIds = getReportableLoadGroupIdsFromOverrideHistory(overrideHistory);
            CustomerAccountWithNames customerAccountWithName =
                customerAccountDao.getAcountWithNamesByAccountNumber(overrideHistory.getAccountNumber(), energyCompanyId);
            
            for (Integer loadGroupId : reportableLoadGroupIds) {
                
                // Cache the control history for this group.
                StarsLMControlHistory allControlEventsForAGroup = starsControlHistoryCache.get(loadGroupId);
                if(allControlEventsForAGroup == null) {
                    allControlEventsForAGroup = 
                        LMControlHistoryUtil.getStarsFormattedLMControlHistory(loadGroupId,
                                                                               startInstant,
                                                                               stopInstant,
                                                                               energyCompanyId);
                    
                    starsControlHistoryCache.put(loadGroupId, allControlEventsForAGroup);
                }

                for (ControlHistoryEntry controlHistoryEntry : allControlEventsForAGroup.getControlHistory()) {
                    List<OpenInterval> enrolledControlHistoryList = 
                        lmControlHistoryUtilService.getControHistoryEnrollmentIntervals(controlHistoryEntry, 
                                                                                        customerAccountWithName.getAccountId(), 
                                                                                        overrideHistory.getInventoryId(), 
                                                                                        loadGroupId);
                    
                    for (OpenInterval enrollmentControlHistory : enrolledControlHistoryList) {
                        if (enrollmentControlHistory.overlaps(overrideHistory.getOpenInterval())) {
                            addOverrideHistoryToModel(customerAccountWithName, overrideHistory, enrollmentControlHistory, loadGroupId);
                        }
                    }
                }
            }
        }

        log.info("Report Records Collected from Database: " + data.size());
        
    }

    /**
     * This method retrieves all the load group ids for the programs supplied in the override history object.
     */
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

    /**
     * This method takes the calculated information and generates a row of user friendly data that
     * is added to the report..
     */
    private void addOverrideHistoryToModel(CustomerAccountWithNames account, OverrideHistory overrideHistory, 
                                           OpenInterval enrolledControlHistory, int loadGroupId) {
    
            OptOutInfoModel.ModelRow row = new OptOutInfoModel.ModelRow();
            
            Duration overrideDuration =
                new Duration(new Instant(overrideHistory.getStartDate()), 
                             new Instant(overrideHistory.getStopDate()));
            
            Duration enrollmentControlDuration = Duration.ZERO;
            if (enrolledControlHistory.isOpenEnd()) {
                enrollmentControlDuration = enrolledControlHistory.withCurrentEnd().toClosedInterval().toDuration();
            } else {
                enrollmentControlDuration = enrolledControlHistory.toClosedInterval().toDuration();
            }
            
            row.accountNumberAndName = "#" + account.getAccountNumber() + " ---- " + account.getLastName() + ", " + account.getFirstName();
            row.serialNumber = overrideHistory.getSerialNumber();
            row.startTimeOfControl = enrolledControlHistory.getStart().toDate();
            row.stopTimeOfControl = enrolledControlHistory.getEnd() == null
                ? null : enrolledControlHistory.getEnd().toDate();
            row.totalProjectedControlHoursForPeriod = enrollmentControlDuration.getMillis()/oneHour;
            row.dateOverrideWasScheduled = overrideHistory.getScheduledDate();
            row.startTimeOfOverride = overrideHistory.getStartDate();
            row.stopTimeOfOverride = overrideHistory.getStopDate();
            row.totalOverrideHours = overrideDuration.getMillis()/oneHour;
            row.countedTowardOptOutLimit = overrideHistory.isCountedAgainstLimit();

            String programNames = programDao.getProgramNames(loadGroupId);
            row.enrolledProgram = programNames;
            
            Duration actualControlDuration = 
                getActualControlDuration(enrolledControlHistory, overrideHistory, enrollmentControlDuration);
            row.totalActualControlHoursForPeriod = actualControlDuration.getMillis()/oneHour;

            data.add(row);
    }
    
    private Duration getActualControlDuration(OpenInterval enrolledControlHistory,
                                              OverrideHistory overrideHistory, 
                                              Duration enrollmentControlDuration) {
        OpenInterval overlappingInterval = enrolledControlHistory.overlap(overrideHistory.getOpenInterval());
        
        if (overlappingInterval == null) {
            return Duration.ZERO;
        }
        
        Duration optOutControlDuration;
        if (overlappingInterval.isOpenEnd()) {
            optOutControlDuration = overlappingInterval.withCurrentEnd().toClosedInterval().toDuration();
        } else if (enrolledControlHistory.isOpenEnd()) {
            OpenInterval optOutControlDurationOpenInterval = 
                enrolledControlHistory.withCurrentEnd().overlap(overrideHistory.getOpenInterval());
            optOutControlDuration = optOutControlDurationOpenInterval.toClosedInterval().toDuration();
        } else {
            optOutControlDuration = overlappingInterval.toClosedInterval().toDuration();
        }
        
        return enrollmentControlDuration.minus(optOutControlDuration);
    }
}
package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.model.CustomerControlTotals;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class LMControlDetailModel extends BareDatedReportModelBase<LMControlDetailModel.ModelRow> implements EnergyCompanyModelAttributes, UserContextModelAttributes {
    private int energyCompanyId;
    private Set<Integer> programIds;
    private String accountNumbers;
    private YukonUserContext userContext;
    
    private CustomerAccountDao customerAccountDao = (CustomerAccountDao) YukonSpringHook.getBean("customerAccountDao");
    private LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
    private ApplianceAndProgramDao applianceAndProgramDao = (ApplianceAndProgramDao) YukonSpringHook.getBean("applianceAndProgramDao");
    private static DecimalFormat decFormat = new java.text.DecimalFormat("0.#");
    private ProgramDao programDao = (ProgramDao)YukonSpringHook.getBean("starsProgramDao");
    private List<ModelRow> data = Collections.emptyList();
    
    public LMControlDetailModel() {
    }
    
    static public class ModelRow {
        public String accountNumberAndName;
        public String program;
        public Date enrollmentStart;
        public Date enrollmentStop;
        public Double controlHours;
        public Double totalOptOutHoursDuringControl;
        public Double totalOptOutHours;
        public Integer optOutEvents;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    public String getTitle() {
        return "Customer Control Detail";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        // get all of the customers
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");

        Instant startDateTime = new Instant(getStartDate());
        Instant stopDateTime = new Instant(getStopDate());
        
        /*Normal LMControlHistory data is useless to me without the active restore being considered and actual control
         *ranges being assembled out of individual events.  May have to do something unpleasant.
         */
        HashMap<Integer, List<ProgramLoadGroup>> groupIdToProgram = new HashMap<Integer, List<ProgramLoadGroup>>(10);
        HashMap<Integer, StarsLMControlHistory> groupIdToSTARSControlHistory = new HashMap<Integer, StarsLMControlHistory>(10);
        List<CustomerAccountWithNames> accounts = null;
        if(accountNumbers.trim() != "") {
            accounts = new ArrayList<CustomerAccountWithNames>();
            String[] accountNumbersArr = accountNumbers.split(";");
            for (String accountNumber : accountNumbersArr) {
                try {
                    CustomerAccount custAccount = 
                        customerAccountDao.getByAccountNumber(accountNumber, 
                                                              userContext.getYukonUser());
                    CustomerAccountWithNames customerAccountWithNames = 
                        customerAccountDao.getAccountWithNamesByCustomerId(custAccount.getCustomerId(), 
                                                                           energyCompanyId);
                    accounts.add(customerAccountWithNames);
                } catch(NotFoundException ex) {
                    // No results from the given accountNumber.  
                }
            }
        } else {
            List<Integer> groupIdsFromSQL = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            accounts = customerAccountDao.getAllAccountsWithNamesByGroupIds(energyCompanyId, 
                                                                            groupIdsFromSQL,
                                                                            getStartDate(),
                                                                            getStopDate());
        }
        List<LiteYukonPAObject> restrictedPrograms = 
            ReportFuncs.getRestrictedPrograms(userContext.getYukonUser());
        data = new ArrayList<ModelRow>(accounts.size());
        for (CustomerAccountWithNames account : accounts) {
            try {
                List<Integer> groupIds = lmHardwareControlGroupDao.getDistinctGroupIdsByAccountId(account.getAccountId());
                for (Integer groupId : groupIds) {
                    
                    List<ProgramLoadGroup> groupPrograms = groupIdToProgram.get(groupId);
                    if(groupPrograms == null) {
                        groupPrograms = applianceAndProgramDao.getProgramsByLMGroupId(groupId);
                        groupIdToProgram.put(groupId, groupPrograms);
                    }
                    
                    groupPrograms = ReportFuncs.filterProgramsByPermission(groupPrograms, restrictedPrograms);
                    
                    /*lots of for loops, but this one will not normally be more than one iteration*/
                    for(final ProgramLoadGroup currentGroupProgram : groupPrograms) {
                        //Check filter: program
                        if(programIds != null && programIds.size() > 0 && ! programIds.contains(currentGroupProgram.getPaobjectId())) {
                            continue;
                        }else {
                            StarsLMControlHistory allControlEventsForAGroup = groupIdToSTARSControlHistory.get(groupId);
                            if(allControlEventsForAGroup == null) {
                                allControlEventsForAGroup = 
                                    LMControlHistoryUtil.getSTARSFormattedLMControlHistory(groupId, 
                                                                                           startDateTime,
                                                                                           energyCompanyId);
                                groupIdToSTARSControlHistory.put(groupId, allControlEventsForAGroup);
                            }
                            
                            List<LMHardwareControlGroup> enrollments = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupId, account.getAccountId(), LMHardwareControlGroup.ENROLLMENT_ENTRY);
                            List<LMHardwareControlGroup> optOuts = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupId, account.getAccountId(), LMHardwareControlGroup.OPT_OUT_ENTRY);
                            
                            // Check to see if the currentGroupProgram is enrolled.  If not skip it.
                            boolean isCurrentProgramEnrolled = 
                                Iterables.any(enrollments, new Predicate<LMHardwareControlGroup>() {
                                    @Override
                                    public boolean apply(LMHardwareControlGroup lmHardwareControlGroup) {
                                        return lmHardwareControlGroup.getProgramId() == currentGroupProgram.getProgramId();
                                    }
                                    
                                });
                            if (!isCurrentProgramEnrolled) {
                                continue;
                            }
                            
                            CustomerControlTotals controlTotals = 
                                LMControlHistoryUtil
                                    .calculateCumulativeCustomerControlValues(allControlEventsForAGroup,
                                                                              startDateTime,
                                                                              stopDateTime,
                                                                              enrollments,
                                                                              optOuts);

                            ModelRow row = new ModelRow();
                            row.accountNumberAndName = "#" + account.getAccountNumber() + " ---- " + account.getLastName() + ", " + account.getFirstName();
                            row.program = currentGroupProgram.getProgramName();

                            double oneHour = Duration.standardHours(1).getMillis();
                            double totalControlHours =
                                controlTotals.getTotalControlTime().getMillis()/oneHour;
                            row.controlHours = 
                                new Double(decFormat.format(1.0 * totalControlHours));

                            double totalOptOutHours = 
                                controlTotals.getTotalOptOutTime().getMillis()/oneHour;
                            row.totalOptOutHours = 
                                new Double(decFormat.format(1.0 * totalOptOutHours));

                            double totalOptOutHoursDuringControl = 
                                controlTotals.getTotalControlDuringOptOutTime().getMillis()/oneHour;
                            row.totalOptOutHoursDuringControl = 
                                new Double(decFormat.format(1.0 * totalOptOutHoursDuringControl));

                            row.optOutEvents = controlTotals.getTotalOptOutEvents();
                            
                            /*These are sorted by date.  For reporting purposes, we'll take the first enrollment start date we can find for
                             * this group, and the last enrollment stop we can find for this group.
                             */
                            if (enrollments.size() > 0) {
                                if (enrollments.get(0).getGroupEnrollStart() != null) {
                                    row.enrollmentStart = enrollments.get(0).getGroupEnrollStart()
                                                                            .toDate();
                                }
                                if (enrollments.get(enrollments.size() - 1).getGroupEnrollStop() != null) {
                                    row.enrollmentStop = enrollments.get(enrollments.size() - 1)
                                                                    .getGroupEnrollStop()
                                                                    .toDate();
                                }
                            }
                            
                            data.add(row);
                        }
                    }
                }
            } catch (Exception e) {
                // not sure what to do here???
                CTILogger.error("Unable to generate row of report for account " + account.getAccountNumber(), e);
            }
        }
    }
    
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }

    public Set<Integer> getProgramIds() {
        return programIds;
    }
    public void setProgramIds(Set<Integer> programIds) {
        this.programIds = programIds;
    }

    public String getAccountNumbers() {
        return accountNumbers;
    }
    public void setAccountNumbers(String accountNumbers) {
        this.accountNumbers = accountNumbers;
    }
}

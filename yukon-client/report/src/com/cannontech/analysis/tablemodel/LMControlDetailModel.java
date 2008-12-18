package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
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
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;

public class LMControlDetailModel extends BareDatedReportModelBase<LMControlDetailModel.ModelRow> {
    private int energyCompanyId;
    private Set<Integer> programIds;
    private String accountNumbers;
    private LiteYukonUser liteUser;
    
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
        return "Detailed Customer Control Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        // get all of the customers
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");

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
                    CustomerAccount custAccount = customerAccountDao.getByAccountNumber(accountNumber, liteUser);
                    CustomerAccountWithNames customerAccountWithNames = customerAccountDao.getAccountWithNamesByCustomerId(custAccount.getCustomerId(), energyCompanyId);
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

        data = new ArrayList<ModelRow>(accounts.size());
        for (CustomerAccountWithNames account : accounts) {
            try {
                List<Integer> groupIds = lmHardwareControlGroupDao.getDistinctGroupIdsByAccountId(account.getAccountId());
                for (Integer groupId : groupIds) {
                    ModelRow row = new ModelRow();
                    row.accountNumberAndName = "#" + account.getAccountNumber() + " ---- " + account.getLastName() + ", " + account.getFirstName();
                    
                    List<ProgramLoadGroup> groupPrograms = groupIdToProgram.get(groupId);
                    if(groupPrograms == null) {
                        groupPrograms = applianceAndProgramDao.getProgramsByLMGroupId(groupId);
                        groupIdToProgram.put(groupId, groupPrograms);
                    }
                    
                    /*lots of for loops, but this one will not normally be more than one iteration*/
                    for(ProgramLoadGroup currentGroupProgram : groupPrograms) {
                        //Check filter: program
                        if(programIds != null && programIds.size() > 0 && ! programIds.contains(currentGroupProgram.getPaobjectId())) 
                            continue;
                        else {
                            row.program = currentGroupProgram.getProgramName();
                            
                            StarsLMControlHistory allControlEventsForAGroup = groupIdToSTARSControlHistory.get(groupId);
                            if(allControlEventsForAGroup == null) {
                                allControlEventsForAGroup = LMControlHistoryUtil.getSTARSFormattedLMControlHistory(groupId, getStartDate(), energyCompanyId);
                                groupIdToSTARSControlHistory.put(groupId, allControlEventsForAGroup);
                            }
                            
                            List<LMHardwareControlGroup> enrollments = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupId, account.getAccountId(), LMHardwareControlGroup.ENROLLMENT_ENTRY);
                            List<LMHardwareControlGroup> optOuts = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupId, account.getAccountId(), LMHardwareControlGroup.OPT_OUT_ENTRY);
                            
                            long[] controlTotals = LMControlHistoryUtil.calculateCumulativeCustomerControlValues(allControlEventsForAGroup, getStartDate(), getStopDate(), enrollments, optOuts);
                            row.controlHours = new Double(decFormat.format(1.0 * controlTotals[LMControlHistoryUtil.TOTAL_CONTROL_TIME] / 3600));
                            row.totalOptOutHours = new Double(decFormat.format(1.0 * controlTotals[LMControlHistoryUtil.TOTAL_OPTOUT_TIME] / 3600));
                            row.totalOptOutHoursDuringControl = new Double(decFormat.format(1.0 * controlTotals[LMControlHistoryUtil.TOTAL_CONTROL_DURING_OPTOUT_TIME] / 3600));
                            row.optOutEvents = new Integer(Long.valueOf(controlTotals[LMControlHistoryUtil.TOTAL_OPTOUT_EVENTS]).toString());
                            
                            /*These are sorted by date.  For reporting purposes, we'll take the first enrollment start date we can find for
                             * this group, and the last enrollment stop we can find for this group.
                             */
                            if(enrollments.size() > 0) {
                                if(enrollments.get(0).getGroupEnrollStart() != null)
                                    row.enrollmentStart = enrollments.get(0).getGroupEnrollStart();
                                if(enrollments.get(enrollments.size() - 1).getGroupEnrollStop() != null)
                                row.enrollmentStop = enrollments.get(enrollments.size() - 1).getGroupEnrollStop();
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
        //----------------------------------------------------------------------------------
    }

    
    
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
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
   
    public LiteYukonUser getLiteUser() {
        return liteUser;
    }
    public void setLiteUser(LiteYukonUser liteUser) {
        this.liteUser = liteUser;
    }
}

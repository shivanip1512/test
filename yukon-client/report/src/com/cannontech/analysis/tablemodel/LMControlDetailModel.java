package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;

public class LMControlDetailModel extends BareDatedReportModelBase<LMControlDetailModel.ModelRow> {
    private int energyCompanyId;
    
    private CustomerAccountDao customerAccountDao = (CustomerAccountDao) YukonSpringHook.getBean("customerAccountDao");
    private LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
    private ApplianceAndProgramDao applianceAndProgramDao = (ApplianceAndProgramDao) YukonSpringHook.getBean("applianceAndProgramDao");
    private static DecimalFormat decFormat = new java.text.DecimalFormat("0.#");
    
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
        
        List<CustomerAccountWithNames> accounts = customerAccountDao.getAllAccountsWithNamesByEC(energyCompanyId);
        /*These are sorted by paobjectId (groupId)*/
        //List<LMControlHistory> controlHistory = lmControlHistoryDao.getByStartDateRange(getStartDate(), getLoadDate());
        
        /*Normal LMControlHistory data is useless to me without the active restore being considered and actual control
         *ranges being assembled out of individual events.  May have to do something unpleasant.
         */
        /*-create data structure to track program name by groupId, and to track control history (STARS formatted) by groupId
        -for each account, get all control info from the table by account, look up program name and control history from that groupId, store if not already stored
        -for each group under an account, calculate true control hours
        -also for each group under an account, calculate total number of opt out hours and events*/
        //SUPERHACK-------------------------------------------------------------------------
        HashMap<Integer, ProgramLoadGroup> groupIdToProgram = new HashMap<Integer, ProgramLoadGroup>(10);
        HashMap<Integer, StarsLMControlHistory> groupIdToSTARSControlHistory = new HashMap<Integer, StarsLMControlHistory>(10);
        
        data = new ArrayList<ModelRow>(accounts.size());
        
        for (CustomerAccountWithNames account : accounts) {
            try {
                List<Integer> groupIds = lmHardwareControlGroupDao.getDistinctGroupIdsByAccountId(account.getAccountId());
                for (Integer groupId : groupIds) {
                    ModelRow row = new ModelRow();
                    row.accountNumberAndName = "#" + account.getAccountNumber() + " ---- " + account.getLastName() + ", " + account.getFirstName();
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
                    row.optOutEvents = new Integer(Long.valueOf(controlTotals[LMControlHistoryUtil.TOTAL_OPTOUT_TIME]).toString());
                    
                    /*These are sorted by date.  For reporting purposes, we'll take the first enrollment start date we can find for
                     * this group, and the last enrollment stop we can find for this group.
                     */
                    if(enrollments.size() > 0) {
                        if(enrollments.get(0).getGroupEnrollStart() != null)
                            row.enrollmentStart = enrollments.get(0).getGroupEnrollStart();
                        if(enrollments.get(enrollments.size() - 1).getGroupEnrollStop() != null)
                        row.enrollmentStop = enrollments.get(enrollments.size() - 1).getGroupEnrollStop();
                    }
                    
                    ProgramLoadGroup program = groupIdToProgram.get(groupId);
                    if(program == null) {
                        program = applianceAndProgramDao.getProgramByLMGroupId(groupId);
                        groupIdToProgram.put(groupId, program);
                    }
                    row.program = program.getProgramName();
                    
                    data.add(row);
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

}

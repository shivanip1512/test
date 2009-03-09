package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;

public class LMControlSummaryModel extends BareDatedReportModelBase<LMControlSummaryModel.ModelRow> implements EnergyCompanyModelAttributes {
    private int energyCompanyId;
    private Set<Integer> programIds;
    private LiteYukonUser liteUser;
    
    private final int ENROLLED_CUSTOMERS = 0;
    private final int TOTAL_CONTROL_HOURS = 1;
    private final int TOTAL_OPT_OUT_HOURS_DURING_CONTROL = 2;
    private final int TOTAL_OPT_OUT_HOURS = 3;
    private final int TOTAL_OPT_OUT_EVENTS = 4;
    
    private CustomerAccountDao customerAccountDao = (CustomerAccountDao) YukonSpringHook.getBean("customerAccountDao");
    private LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
    private ApplianceAndProgramDao applianceAndProgramDao = (ApplianceAndProgramDao) YukonSpringHook.getBean("applianceAndProgramDao");
    private static DecimalFormat decFormat = new java.text.DecimalFormat("0.#");
    private ProgramDao programDao = (ProgramDao)YukonSpringHook.getBean("starsProgramDao");
    
    private List<ModelRow> data = Collections.emptyList();
    
    public LMControlSummaryModel() {
    }
    
    static public class ModelRow {
        public String program;
        public Integer enrolledCustomers = 0;
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
        return "Customer Control Summary";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        // get all of the customers
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");
        
        List<Integer> groupIdsFromSQL = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
        List<CustomerAccountWithNames> accountsFromSQL = customerAccountDao.getAllAccountsWithNamesByGroupIds(energyCompanyId, 
                                                                                                              groupIdsFromSQL,
                                                                                                              getStartDate(),
                                                                                                              getStopDate());
        
        EnergyCompanyDao energyCompanyDao = YukonSpringHook.getBean("energyCompanyDao", EnergyCompanyDao.class);
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        LiteYukonUser ecAdminUser = energyCompanyDao.getEnergyCompanyUser(energyCompanyId);
        boolean inheritCategories = rolePropertyDao.checkProperty(YukonRoleProperty.INHERIT_PARENT_APP_CATS, ecAdminUser);
        List<ProgramLoadGroup> ecPrograms;
        if(inheritCategories) {
            ecPrograms = applianceAndProgramDao.getAllProgramsForAnECAndParentEC(energyCompanyId);
        } else {
            ecPrograms = applianceAndProgramDao.getAllProgramsForAnEC(energyCompanyId);
        }

        /*Normal LMControlHistory data is useless to me without the active restore being considered and actual control
         *ranges being assembled out of individual events.  May have to do something unpleasant.
         */
        HashMap<Integer, List<ProgramLoadGroup>> groupIdToProgram = new HashMap<Integer, List<ProgramLoadGroup>>(10);
        HashMap<Integer, StarsLMControlHistory> groupIdToSTARSControlHistory = new HashMap<Integer, StarsLMControlHistory>(10);
        
        data = new ArrayList<ModelRow>(ecPrograms.size());
        HashMap<Integer, Double[]> programTotals = new HashMap<Integer, Double[]>();
        List<LiteYukonPAObject> restrictedPrograms = ReportFuncs.getRestrictedPrograms(liteUser);
        for (CustomerAccountWithNames account : accountsFromSQL) {
            try{
                List<Integer> groupIds = lmHardwareControlGroupDao.getDistinctGroupIdsByAccountId(account.getAccountId());
                for (Integer groupId : groupIds) {
                    if(!groupIdsFromSQL.contains(groupId)) {
                        continue;
                    }
                    
                    List<ProgramLoadGroup> groupPrograms = groupIdToProgram.get(groupId);
                    if(groupPrograms == null) {
                        groupPrograms = applianceAndProgramDao.getProgramsByLMGroupId(groupId);
                        groupIdToProgram.put(groupId, groupPrograms);
                    }
                    
                    groupPrograms = ReportFuncs.filterProgramsByPermission(groupPrograms, restrictedPrograms);
                    
                    /*lots of for loops, but this one will not normally be more than one iteration*/
                    for(ProgramLoadGroup currentGroupProgram : groupPrograms) {
                        //Check filter: program
                        if(programIds != null && programIds.size() > 0 && ! programIds.contains(currentGroupProgram.getPaobjectId())) { 
                            continue;
                        } else {
                            Double[] totals = programTotals.get(currentGroupProgram.getPaobjectId());
                            if(totals == null) {
                                totals = new Double[5];
                                totals[ENROLLED_CUSTOMERS] = 0.0;
                                totals[TOTAL_CONTROL_HOURS] = 0.0;
                                totals[TOTAL_OPT_OUT_HOURS_DURING_CONTROL] = 0.0;
                                totals[TOTAL_OPT_OUT_HOURS] = 0.0;
                                totals[TOTAL_OPT_OUT_EVENTS] = 0.0;
                                programTotals.put(currentGroupProgram.getPaobjectId(), totals);
                            }
                            
                            StarsLMControlHistory allControlEventsForAGroup = groupIdToSTARSControlHistory.get(groupId);
                            if(allControlEventsForAGroup == null) {
                                allControlEventsForAGroup = LMControlHistoryUtil.getSTARSFormattedLMControlHistory(groupId, getStartDate(), energyCompanyId);
                                groupIdToSTARSControlHistory.put(groupId, allControlEventsForAGroup);
                            }


                            List<LMHardwareControlGroup> enrollments = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupId, account.getAccountId(), LMHardwareControlGroup.ENROLLMENT_ENTRY);
                            List<LMHardwareControlGroup> optOuts = lmHardwareControlGroupDao.getByLMGroupIdAndAccountIdAndType(groupId, account.getAccountId(), LMHardwareControlGroup.OPT_OUT_ENTRY);
                            
                            totals = programTotals.get(currentGroupProgram.getPaobjectId());
                            long[] controlTotals = LMControlHistoryUtil.calculateCumulativeCustomerControlValues(allControlEventsForAGroup, getStartDate(), getStopDate(), enrollments, optOuts);
                            totals[TOTAL_CONTROL_HOURS] = totals[TOTAL_CONTROL_HOURS] + new Double(decFormat.format(1.0 * controlTotals[LMControlHistoryUtil.TOTAL_CONTROL_TIME] / 3600));
                            totals[TOTAL_OPT_OUT_HOURS] =  totals[TOTAL_OPT_OUT_HOURS] + new Double(decFormat.format(1.0 * controlTotals[LMControlHistoryUtil.TOTAL_OPTOUT_TIME] / 3600));
                            totals[TOTAL_OPT_OUT_HOURS_DURING_CONTROL] = totals[TOTAL_OPT_OUT_HOURS_DURING_CONTROL] + new Double(decFormat.format(1.0 * controlTotals[LMControlHistoryUtil.TOTAL_CONTROL_DURING_OPTOUT_TIME] / 3600));
                            totals[TOTAL_OPT_OUT_EVENTS] = totals[TOTAL_OPT_OUT_EVENTS] + new Integer(Long.valueOf(controlTotals[LMControlHistoryUtil.TOTAL_OPTOUT_EVENTS]).toString());
                            
                            for(LMHardwareControlGroup enrollment : enrollments) {
                                if(enrollment.getGroupEnrollStart() != null && (enrollment.getGroupEnrollStop() == null ||
                                        enrollment.getGroupEnrollStop().getTime() > getStartDate().getTime())) {
                                    totals[ENROLLED_CUSTOMERS] = totals[ENROLLED_CUSTOMERS] + 1;
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // not sure what to do here???
                CTILogger.error("Unable to generate row of report for account " + account.getAccountNumber(), e);
            }
        }
            
        //----------------------------------------------------------------------------------
        
        if(accountsFromSQL.size() > 0) {
            for(ProgramLoadGroup prog : ecPrograms) {
                if(programIds != null && programIds.size() > 0 && ! programIds.contains(prog.getPaobjectId())) 
                    continue;
                else {
                    ModelRow row = new ModelRow();
                    row.program = prog.getProgramName();
                    Double[] totals = programTotals.get(prog.getPaobjectId());
                    if(totals == null) {
                        row.controlHours = 0.0;
                        row.enrolledCustomers = 0;
                        row.optOutEvents = 0;
                        row.totalOptOutHours = 0.0;
                        row.totalOptOutHoursDuringControl = 0.0;
                    } else {
                        row.controlHours = totals[TOTAL_CONTROL_HOURS];
                        row.enrolledCustomers = (int)totals[ENROLLED_CUSTOMERS].doubleValue();
                        row.optOutEvents = (int)totals[TOTAL_OPT_OUT_EVENTS].doubleValue();
                        row.totalOptOutHours = totals[TOTAL_OPT_OUT_HOURS];
                        row.totalOptOutHoursDuringControl = totals[TOTAL_OPT_OUT_HOURS_DURING_CONTROL];
                    }
                    data.add(row);
                }
            }
        }
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
    
    public LiteYukonUser getLiteUser() {
        return liteUser;
    }
    public void setLiteUser(LiteYukonUser liteUser) {
        this.liteUser = liteUser;
    }
    
}

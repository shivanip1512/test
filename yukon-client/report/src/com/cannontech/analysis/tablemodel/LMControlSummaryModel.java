package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.OpenInterval;
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
import com.cannontech.stars.dr.controlhistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.model.CustomerControlTotals;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.user.YukonUserContext;

public class LMControlSummaryModel extends BareDatedReportModelBase<LMControlSummaryModel.ModelRow> 
                                     implements EnergyCompanyModelAttributes, UserContextModelAttributes{
    private int energyCompanyId;
    private Set<Integer> programIds;
    private YukonUserContext userContext;
    
    private final int ENROLLED_CUSTOMERS = 0;
    private final int ENROLLED_INVENTORY = 1;
    private final int TOTAL_CONTROL_HOURS = 2;
    private final int TOTAL_OPT_OUT_HOURS_DURING_CONTROL = 3;
    private final int TOTAL_OPT_OUT_HOURS = 4;
    private final int TOTAL_OPT_OUT_EVENTS = 5;
    
    private CustomerAccountDao customerAccountDao = (CustomerAccountDao) YukonSpringHook.getBean("customerAccountDao");
    private InventoryBaseDao inventoryBaseDao = (InventoryBaseDao) YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
    private LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
    private LmControlHistoryUtilService lmControlHistoryUtilService = (LmControlHistoryUtilService) YukonSpringHook.getBean("lmControlHistoryUtilService", LmControlHistoryUtilService.class); 
    private ApplianceAndProgramDao applianceAndProgramDao = (ApplianceAndProgramDao) YukonSpringHook.getBean("applianceAndProgramDao");
    private static DecimalFormat decFormat = new java.text.DecimalFormat("0.#");
    private ProgramDao programDao = (ProgramDao)YukonSpringHook.getBean("starsProgramDao");
    
    private List<ModelRow> data = Collections.emptyList();
    
    public LMControlSummaryModel() {}
    
    static public class ModelRow {
    	public String total = "";
        public String program;
        public Integer enrolledCustomers = 0;
        public Integer enrolledInventory = 0;
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
        // Validate that neither getStartDate and getStopDate are null and then create
        // a closed openInterval with those dates.
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");

        Instant startDateTime = new Instant(getStartDate());
        Instant stopDateTime = new Instant(getStopDate());
        OpenInterval reportInterval = OpenInterval.createClosed(startDateTime, stopDateTime);
        

        // get all of the customers
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
        List<LiteYukonPAObject> restrictedPrograms = 
            ReportFuncs.getRestrictedPrograms(userContext.getYukonUser());
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
                                totals = new Double[6];
                                totals[ENROLLED_CUSTOMERS] = 0.0;
                                totals[ENROLLED_INVENTORY] = 0.0;
                                totals[TOTAL_CONTROL_HOURS] = 0.0;
                                totals[TOTAL_OPT_OUT_HOURS_DURING_CONTROL] = 0.0;
                                totals[TOTAL_OPT_OUT_HOURS] = 0.0;
                                totals[TOTAL_OPT_OUT_EVENTS] = 0.0;
                                programTotals.put(currentGroupProgram.getPaobjectId(), totals);
                            }
                            
                            StarsLMControlHistory controlEventsForLoadGroup = groupIdToSTARSControlHistory.get(groupId);
                            if(controlEventsForLoadGroup == null) {
                                controlEventsForLoadGroup = 
                                    LMControlHistoryUtil.getStarsFormattedLMControlHistory(groupId, 
                                                                                           startDateTime,
                                                                                           stopDateTime,
                                                                                           energyCompanyId);
                                groupIdToSTARSControlHistory.put(groupId, controlEventsForLoadGroup);
                            }

                            boolean accountEnrollmentCounted = false;
                            List<Integer> inventoryIds = inventoryBaseDao.getInventoryIdsByAccountId(account.getAccountId());
                            for (int inventoryId : inventoryIds) {
                                List<LMHardwareControlGroup> enrollments = 
                                    lmHardwareControlGroupDao.getIntersectingEnrollments(account.getAccountId(),
                                                                                         inventoryId,
                                                                                         groupId,
                                                                                         reportInterval);
                                List<LMHardwareControlGroup> optOuts = 
                                    lmHardwareControlGroupDao.getIntersectingOptOuts(account.getAccountId(),
                                                                                     inventoryId,
                                                                                     groupId,
                                                                                     reportInterval);
                                
                                totals = programTotals.get(currentGroupProgram.getPaobjectId());
                                CustomerControlTotals CustomerControlTotals = 
                                    lmControlHistoryUtilService.calculateCumulativeCustomerControlValues(controlEventsForLoadGroup,
                                                                                                         startDateTime,
                                                                                                         stopDateTime,
                                                                                                         enrollments,
                                                                                                         optOuts);
                                
                                // Check and see if the account is enrolled in the program for the
                                // given time period.  If it is add it to the enrolled customer count;
                                if (!accountEnrollmentCounted && enrollments.size() > 0) {
                                    accountEnrollmentCounted = true;
                                    totals[ENROLLED_CUSTOMERS]++;
                                }
                                
                                double oneHour = Duration.standardHours(1).getMillis();
                                double totalControlHours =
                                    CustomerControlTotals.getTotalControlTime().getMillis()/oneHour;
                                totals[TOTAL_CONTROL_HOURS] += new Double(totalControlHours);
    
                                double totalOptOutHours = 
                                    CustomerControlTotals.getTotalOptOutTime().getMillis()/oneHour;
                                totals[TOTAL_OPT_OUT_HOURS] += new Double(totalOptOutHours);
    
                                double totalOptOutHoursDuringControl = 
                                    CustomerControlTotals.getTotalControlDuringOptOutTime().getMillis()/oneHour;
                                totals[TOTAL_OPT_OUT_HOURS_DURING_CONTROL] += new Double(totalOptOutHoursDuringControl);
    
                                totals[TOTAL_OPT_OUT_EVENTS] += CustomerControlTotals.getTotalOptOutEvents();
                                
                                for(LMHardwareControlGroup enrollment : enrollments) {
                                    if(enrollment.isActiveEnrollment() ||
                                       enrollment.getGroupEnrollStop().isAfter(new Instant(getStartDate()))) {
                                        totals[ENROLLED_INVENTORY] = totals[ENROLLED_INVENTORY] + 1;
                                        break;
                                    }
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
                        row.enrolledInventory = 0;
                        row.optOutEvents = 0;
                        row.totalOptOutHours = 0.0;
                        row.totalOptOutHoursDuringControl = 0.0;
                    } else {
                        row.controlHours = new Double(decFormat.format(1.0 * totals[TOTAL_CONTROL_HOURS]));
                        row.enrolledCustomers = (int)totals[ENROLLED_CUSTOMERS].doubleValue();
                        row.enrolledInventory = (int)totals[ENROLLED_INVENTORY].doubleValue();
                        row.optOutEvents = (int)totals[TOTAL_OPT_OUT_EVENTS].doubleValue();
                        row.totalOptOutHours = new Double(decFormat.format(1.0 * totals[TOTAL_OPT_OUT_HOURS]));
                        row.totalOptOutHoursDuringControl = new Double(decFormat.format(1.0 * totals[TOTAL_OPT_OUT_HOURS_DURING_CONTROL]));
                    }
                    data.add(row);
                }
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

    
}

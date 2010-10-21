package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.model.OverrideStatus;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class OptOutLimitModel extends BareDatedReportModelBase<OptOutLimitModel.ModelRow> implements EnergyCompanyModelAttributes {

    private Logger log = YukonLogManager.getLogger(OptOutLimitModel.class);

    private CustomerAccountDao customerAccountDao = 
        (CustomerAccountDao) YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
    private EnrollmentDao enrollmentDao = 
        (EnrollmentDao) YukonSpringHook.getBean("enrollmentDao", EnrollmentDao.class);
    private InventoryBaseDao inventoryBaseDao =
        (InventoryBaseDao) YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
    private ProgramDao programDao = 
        (ProgramDao) YukonSpringHook.getBean("starsProgramDao", ProgramDao.class);
    private OptOutEventDao optOutEventDao = 
        (OptOutEventDao) YukonSpringHook.getBean("optOutEventDao", OptOutEventDao.class);
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private int energyCompanyId;
    private Set<Integer> accountIds;
    private Set<Integer> inventoryIds;
    private Set<Integer> programIds;
    private Set<Integer> userIds;
    
    static public class ModelRow {
        public String accountNumberAndName;
        public String serialNumber;
        public String alternateTrackingNumber;
        public String enrolledProgram;
        public Integer numberOfOverridesUsed;
        public String issuingUser;
        public Date optOutStart;
        public Date optOutStop;
    }

    private Comparator<OverrideHistory> overrideHistoryAccountIdInventoryIdComparator = 
        new Comparator<OverrideHistory>(){

            @Override
            public int compare(OverrideHistory overrideHistoryOne, OverrideHistory overrideHistoryTwo) {
                
                int compareToIgnoreCase = overrideHistoryOne.getAccountNumber().compareToIgnoreCase(overrideHistoryTwo.getAccountNumber());
                
                if (compareToIgnoreCase == 0) {
                    return overrideHistoryOne.getInventoryId().compareTo(overrideHistoryTwo.getInventoryId());
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
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");

        // Check to see if users where selected and use them to find the report data.
        if (userIds != null) {
            for (Integer userId : userIds) {
                List<OverrideHistory> overrideHistoryList = 
                    optOutEventDao.getOptOutHistoryByLogUserId(userId, 
                                                               getStartDate(), 
                                                               getStopDate());
                
                addOverrideHistoryToModel(overrideHistoryList);
            }
        }
        
        // Check to see if inventories where selected and uses them to find the report data.
        if (inventoryIds != null) {
            for (Integer inventoryId : inventoryIds){
                List<OverrideHistory> overrideHistoryList =
                    optOutEventDao.getOptOutHistoryForInventory(inventoryId, 
                                                                getStartDate(), 
                                                                getStopDate());
                addOverrideHistoryToModel(overrideHistoryList);
            }
        }
        
        // Check to see if accounts where selected and uses them to find the report data.
        if (accountIds != null) {
            for (Integer accountId : accountIds) {
                List<OverrideHistory> overrideHistoryList =
                    optOutEventDao.getOptOutHistoryForAccount(accountId, 
                                                              getStartDate(), 
                                                              getStopDate());
                addOverrideHistoryToModel(overrideHistoryList);
            }
        }
        
        // Check to see if programs where selected and uses them to find the report data.
        if (programIds != null) {
            List<Program> suppliedProgramIds = programDao.getByProgramIds(programIds);

            List<Integer> groupIdsFromSQL = programDao.getDistinctGroupIdsByYukonProgramIds(programIds);
            List<CustomerAccountWithNames> accounts = 
                customerAccountDao.getAllAccountsWithNamesByGroupIds(energyCompanyId, groupIdsFromSQL,
                                                                     getStartDate(), getStopDate());

            for (CustomerAccountWithNames account : accounts) {
                List<OverrideHistory> overrideHistories = 
                    optOutEventDao.getOptOutHistoryForAccount(account.getAccountId(), getStartDate(), getStopDate());
                
                for (OverrideHistory overrideHistory : overrideHistories) {

                    // Retrieve the involved program ids from override history entry  
                    for (Program overrideHistoryProgram : overrideHistory.getPrograms()) {
                        if (suppliedProgramIds.contains(overrideHistoryProgram)) {
                            addOverrideHistoryToModel(Collections.singletonList(overrideHistory));
                            break;
                        }
                    }
                }
            }
        }
        
        log.info("Report Records Collected from Database: " + data.size());
    }

    /**
     * This method takes the override history objects and generates all the date
     * needed to make a report row entry.
     */
    private void addOverrideHistoryToModel(List<OverrideHistory> overrideHistoryList) {
        
        Collections.sort(overrideHistoryList, overrideHistoryAccountIdInventoryIdComparator);
        
        for (OverrideHistory overrideHistory : overrideHistoryList) {
            
            CustomerAccountWithNames customerAccountWithName =
                customerAccountDao.getAcountWithNamesByAccountNumber(overrideHistory.getAccountNumber(),
                                                                             energyCompanyId);
            
            if (overrideHistory.getStatus().equals(OverrideStatus.Active)) {
   
                InventoryBase inventory = null; 
                List<Program> programList = null;
                try {
                    programList = enrollmentDao.getEnrolledProgramIdsByInventory(overrideHistory.getInventoryId(), 
                                                                                 overrideHistory.getStartDate(), 
                                                                                 overrideHistory.getStopDate());
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
            
            row.accountNumberAndName = "#" + customerAccountWithName.getAccountNumber() + " ---- " +
                                       customerAccountWithName.getLastName() + ", " + 
                                       customerAccountWithName.getFirstName();
            row.serialNumber = overrideHistory.getSerialNumber();
            row.alternateTrackingNumber = customerAccountWithName.getAlternateTrackingNumber();
            row.enrolledProgram = getProgramNames(programList);
            row.numberOfOverridesUsed = overrideHistory.isCountedAgainstLimit() ? 1 : 0;
            row.issuingUser = overrideHistory.getUserName();

            row.optOutStart = overrideHistory.getStartDate();
            row.optOutStop = overrideHistory.getStopDate();
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
                if (programNames.size() - 1 < i) {
                    programNamesStr += ", ";
                }
                
                String programName = programNames.get(i);
                programNamesStr += programName;
            }
        }
        
        return programNamesStr;
    }
}
package com.cannontech.stars.dr.displayable.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.displayable.dao.AbstractDisplayableDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.displayable.model.DisplayableControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.displayable.model.DisplayableControlHistory.DisplayableControlHistoryType;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;

@Repository
public class DisplayableProgramDaoImpl extends AbstractDisplayableDao implements DisplayableProgramDao {
    
    @Override
    public List<DisplayableProgram> getDisplayablePrograms(final CustomerAccount customerAccount, 
                                                           final YukonUserContext yukonUserContext,
                                                           final ControlPeriod controlPeriod) {
        return doAction(customerAccount, yukonUserContext, controlPeriod, true);
    }

    @Override
    public List<DisplayableProgram> getAllDisplayablePrograms(final CustomerAccount customerAccount, 
                                                              final YukonUserContext yukonUserContext,
                                                              final ControlPeriod controlPeriod) {
        return doAction(customerAccount, yukonUserContext, controlPeriod, false);
    }

    /**
     * Filter Rule #1 - DisplayableProgram are not created for programs that have no control history: 
     * Filter Rule #2 - DisplayableProgram are not created for programs that only have control history of NOT_ENROLLED
     *      
     * If only one device exists on a program then show only the single status with 
     * no device information displayed.
     * @param program
     * @param controlHistoryList
     * @return - a DisplayableProgram used while rendering the view.
     */
    
    
    @Override
    public DisplayableProgram getDisplayableProgram(Program program,
                                                    final List<ControlHistory> controlHistoryList,
                                                    ControlPeriod controlPeriod,
                                                    final boolean applyFilters) {
        // Filter Rule #1
        if (applyFilters && controlHistoryList.isEmpty()) return null;
        
        // Filter Rule #2 - This Filter will always be applied.
        boolean containsOnlyNotEnrolledHistory = controlHistoryService.containsOnlyNotEnrolledHistory(controlHistoryList);
        if (containsOnlyNotEnrolledHistory) return null;
        
        List<DisplayableControlHistory> displayableControlHistoryList = new ArrayList<DisplayableControlHistory>(controlHistoryList.size());

        for (final ControlHistory controlHistory : controlHistoryList) {
            DisplayableControlHistoryType displayableType = null;
            
            if (controlHistoryList.size() == 1) {
                displayableType = DisplayableControlHistoryType.CONTROLSTATUS;
            }else {
                displayableType = DisplayableControlHistoryType.DEVICELABEL_CONTROLSTATUS;
            }
            
            DisplayableControlHistory displayableControlHistory = 
                new DisplayableControlHistory(displayableType, controlHistory);
            displayableControlHistoryList.add(displayableControlHistory);
        }
        
        Collections.sort(displayableControlHistoryList,DEVICE_LABLE_COMPARATOR);
        DisplayableProgram displayableProgram = new DisplayableProgram(program, displayableControlHistoryList);
        return displayableProgram;
    }
    
    private static Comparator<DisplayableControlHistory> DEVICE_LABLE_COMPARATOR = new Comparator<DisplayableControlHistory>() {
        public int compare(DisplayableControlHistory o1, DisplayableControlHistory o2) {
            try {
                String strA = o1.getControlHistory().getInventory().getDeviceLabel();
                String strB = o2.getControlHistory().getInventory().getDeviceLabel();

                return strA.compareToIgnoreCase(strB);
            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules", e);
                return 0;
            }

        }
    };
    
    private List<DisplayableProgram> doAction(CustomerAccount customerAccount, 
                                              YukonUserContext yukonUserContext,
                                              ControlPeriod controlPeriod,
                                              boolean applyFilters) {
                                         
        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccount.getAccountId());
        List<Program> programList = programDao.getByAppliances(applianceList);

        return doAction(customerAccount, yukonUserContext, controlPeriod, applyFilters, applianceList, programList);
    }

    private List<DisplayableProgram> doAction(CustomerAccount customerAccount,
                                              YukonUserContext yukonUserContext,
                                              ControlPeriod controlPeriod,
                                              boolean applyFilters,
                                              List<Appliance> applianceList,
                                              List<Program> programList) {
        ListMultimap<Integer,ControlHistory> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, applianceList, yukonUserContext, controlPeriod);

        final List<DisplayableProgram> displayableProgramList = new ArrayList<DisplayableProgram>(programList.size());

        for (final Program program : programList) {
            Integer programId = program.getProgramId();

            List<ControlHistory> controlHistoryList = new ArrayList<ControlHistory>(controlHistoryMap.get(programId));
            if (controlHistoryList == null) controlHistoryList = Collections.emptyList();

            DisplayableProgram displayableProgram = getDisplayableProgram(program, controlHistoryList, controlPeriod, applyFilters);
            if (displayableProgram != null) displayableProgramList.add(displayableProgram);
        }

        return displayableProgramList;
    }
    
    public DisplayableProgram getDisplayableProgram(CustomerAccount customerAccount, 
                                                    YukonUserContext yukonUserContext,
                                                    Program program,
                                                    ControlPeriod controlPeriod){

        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccount.getAccountId());
        List<Program> programList = Collections.singletonList(program);

        List<DisplayableProgram> displayableProgramList = 
            doAction(customerAccount, yukonUserContext, controlPeriod, true, applianceList, programList);
        
        return displayableProgramList.get(0);
    }
    
}

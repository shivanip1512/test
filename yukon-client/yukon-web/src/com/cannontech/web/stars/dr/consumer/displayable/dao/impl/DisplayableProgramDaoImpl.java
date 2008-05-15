package com.cannontech.web.stars.dr.consumer.displayable.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.dao.AbstractDisplayableDao;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableProgramDao;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableControlHistory;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableProgram;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableControlHistory.DisplayableControlHistoryType;

@Repository
public class DisplayableProgramDaoImpl extends AbstractDisplayableDao implements DisplayableProgramDao {
    
    @Override
    public List<DisplayableProgram> getDisplayablePrograms(
            final CustomerAccount customerAccount, final YukonUserContext yukonUserContext) {
        return doAction(customerAccount, yukonUserContext, true);
    }

    @Override
    public List<DisplayableProgram> getAllDisplayablePrograms(
            final CustomerAccount customerAccount, final YukonUserContext yukonUserContext) {
        return doAction(customerAccount, yukonUserContext, false);
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
            final List<ControlHistory> controlHistoryList, final boolean applyFilters) {
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
        
        DisplayableProgram displayableProgram = new DisplayableProgram(program, displayableControlHistoryList);
        return displayableProgram;
    }
    
    private List<DisplayableProgram> doAction(CustomerAccount customerAccount, 
             YukonUserContext yukonUserContext, boolean applyFilters) {
                                         
        List<Appliance> appliances = applianceDao.getByAccountId(customerAccount.getAccountId());
        List<Program> programs = programDao.getByAppliances(appliances);

        Map<Integer,List<ControlHistory>> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, appliances, yukonUserContext);

        final List<DisplayableProgram> displayableProgramList = new ArrayList<DisplayableProgram>(programs.size());

        for (final Program program : programs) {
            Integer programId = program.getProgramId();

            List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
            if (controlHistoryList == null) controlHistoryList = Collections.emptyList();

            DisplayableProgram displayableProgram = getDisplayableProgram(program, controlHistoryList, applyFilters);
            if (displayableProgram != null) displayableProgramList.add(displayableProgram);
        }

        return displayableProgramList;
    }
    
}

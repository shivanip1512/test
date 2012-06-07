package com.cannontech.stars.dr.displayable.dao.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.Duration;
import org.springframework.stereotype.Repository;

import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlHistory.model.ControlHistory;
import com.cannontech.stars.dr.controlHistory.model.ControlHistoryStatus;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.dr.displayable.dao.AbstractDisplayableDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableProgramDao;
import com.cannontech.stars.dr.displayable.model.DisplayableControlHistory;
import com.cannontech.stars.dr.displayable.model.DisplayableControlHistory.DisplayableControlHistoryType;
import com.cannontech.stars.dr.displayable.model.DisplayableProgram;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Repository
public class DisplayableProgramDaoImpl extends AbstractDisplayableDao implements DisplayableProgramDao {
    
    @Override
    public List<DisplayableProgram> getControlHistorySummary(final int accountId, final YukonUserContext userContext, final ControlPeriod controlPeriod) {
        List<DisplayableProgram> displayableProgramList = getForAccount(accountId, userContext, controlPeriod, true, false);
        List<DisplayableProgram> filteredList = filterEntriesForControlHistorySummary(displayableProgramList, false);
        
        return filteredList;
    }
    
    @Override
    public List<DisplayableProgram> getAllControlHistorySummary(int accountId, YukonUserContext userContext, ControlPeriod controlPeriod, boolean past) {
        List<DisplayableProgram> displayableProgramList = getForAccount(accountId, userContext, controlPeriod, false, past);
        List<DisplayableProgram> filteredList = filterEntriesForControlHistorySummary(displayableProgramList, past);
        
        return filteredList;
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
                                                    final boolean applyFilters,
                                                    boolean past) {
        // Filter Rule #1
        if (applyFilters && controlHistoryList.isEmpty()) return null;
        
        // Filter Rule #2
        boolean containsOnlyNotEnrolledHistory = controlHistoryService.containsOnlyNotEnrolledHistory(controlHistoryList);
        if (!past && applyFilters && containsOnlyNotEnrolledHistory) return null;
        
        List<DisplayableControlHistory> displayableControlHistoryList = Lists.newArrayListWithExpectedSize(controlHistoryList.size());

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
        
        Collections.sort(displayableControlHistoryList, DEVICE_LABEL_COMPARATOR);
        DisplayableProgram displayableProgram = new DisplayableProgram(program, displayableControlHistoryList);
        return displayableProgram;
    }
    
    private static Comparator<DisplayableControlHistory> DEVICE_LABEL_COMPARATOR = 
        Ordering.from(String.CASE_INSENSITIVE_ORDER)
        .nullsLast()
        .onResultOf(new Function<DisplayableControlHistory, String> () {
            @Override
            public String apply(DisplayableControlHistory from) {
                return from.getControlHistory().getDisplayName();
            }
        });

    
    private List<DisplayableProgram> getForAccount(int accountId, YukonUserContext userContext, ControlPeriod controlPeriod, boolean applyFilters, boolean past) {
        List<Program> programList = Lists.newArrayList();
        
        if(!past) {
            List<Appliance> applianceList = applianceDao.getAssignedAppliancesByAccountId(accountId);
            programList = programDao.getByAppliances(applianceList);
        } else {
            List<Integer> assignedProgramIds = lmHardwareControlGroupDao.getPastEnrollmentProgramIds(accountId);
            programList = programDao.getByAssignedProgramIds(assignedProgramIds);
        }

        return getForAccount(accountId, userContext, controlPeriod, applyFilters, programList, past);
    }

    private List<DisplayableProgram> getForAccount(int accountId, YukonUserContext userContext,
                                                   ControlPeriod controlPeriod, boolean applyFilters,
                                                   List<Program> programList, boolean past) {
        
        ListMultimap<Integer,ControlHistory> controlHistoryMap = controlHistoryDao.getControlHistory(accountId, userContext, controlPeriod, past);

        final List<DisplayableProgram> displayableProgramList = Lists.newArrayListWithExpectedSize(programList.size());

        for (final Program program : programList) {
            Integer programId = program.getProgramId();

            List<ControlHistory> controlHistoryList = Lists.newArrayList(controlHistoryMap.get(programId));
            DisplayableProgram displayableProgram = getDisplayableProgram(program, controlHistoryList, controlPeriod, applyFilters, past);
            if (displayableProgram != null) { 
                displayableProgramList.add(displayableProgram);
            }
        }

        return displayableProgramList;
    }
    
    @Override
    public DisplayableProgram getDisplayableProgram(int accountId, YukonUserContext userContext, Program program, ControlPeriod controlPeriod, boolean past){
        List<Program> programList = Collections.singletonList(program);
        List<DisplayableProgram> displayableProgramList = getForAccount(accountId, userContext, controlPeriod, true, programList, past);
        
        return displayableProgramList.get(0);
    }
    
    
    /**
     * This method goes through the list of displayablePrograms and keeps the first 
     * control history event for each device.  This method should only be used when
     * you want to display the control history summaries and should not be used 
     * for displaying actual control history events.
     * 
     * For past enrollment's control history, don't show programs that have no control history.
     * Also fix the status for these history to reflect that they are past control history.
     */
    private List<DisplayableProgram> filterEntriesForControlHistorySummary(List<DisplayableProgram> displayablePrograms, boolean past) {
        List<DisplayableProgram> results = Lists.newArrayList();
        
        for (DisplayableProgram displayableProgram : displayablePrograms) {
            
            List<DisplayableControlHistory> reducedControlHistory = Lists.newArrayList();
            for (DisplayableControlHistory displayableControlHistory : displayableProgram.getDisplayableControlHistoryList()) {
                
                if(past) {
                    /* For past history if there was no control in the last year, skip it. */
                    ControlHistory history = displayableControlHistory.getControlHistory();
                    if(history.getControlHistorySummary().getYearlySummary().isEqual(Duration.ZERO)){
                        continue;
                    } else {
                        history.setCurrentStatus(ControlHistoryStatus.CONTROLLED_PREVIOUSLY);
                    }
                }
                
                reducedControlHistory.add(displayableControlHistory);
            }
            
            if(past && reducedControlHistory.isEmpty()) {
                continue;
            }
            
            DisplayableProgram reducedDisplayableProgram = new DisplayableProgram(displayableProgram.getProgram(), reducedControlHistory);
            results.add(reducedDisplayableProgram);
            
        }
        
        return results;
    }

}
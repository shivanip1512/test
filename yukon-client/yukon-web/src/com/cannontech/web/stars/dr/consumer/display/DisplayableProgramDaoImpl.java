package com.cannontech.web.stars.dr.consumer.display;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryStatus;
import com.cannontech.stars.dr.optout.dao.ScheduledOptOutDao;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.display.DisplayableControlHistory.DisplayableControlHistoryType;
import com.cannontech.web.stars.dr.consumer.display.DisplayableOptOut.DisplayableOptOutType;

public class DisplayableProgramDaoImpl implements DisplayableProgramDao {
    private ScheduledOptOutDao scheduledOptOutDao;
    private ControlHistoryDao controlHistoryDao;
    private ApplianceDao applianceDao;
    private ProgramDao programDao;

    @Override
    public List<DisplayableProgram> getDisplayablePrograms(final CustomerAccount customerAccount, 
            final YukonUserContext yukonUserContext) {

        List<Appliance> appliances = applianceDao.getByAccountId(customerAccount.getAccountId());
        List<Program> programs = programDao.getByAppliances(appliances);
        
        Map<Integer,List<ControlHistory>> controlHistoryMap = 
            controlHistoryDao.getControlHistory(customerAccount, appliances, yukonUserContext);
        
        final List<DisplayableProgram> displayableProgramList = new ArrayList<DisplayableProgram>(programs.size());
        
        for (final Program program : programs) {
            Integer programId = program.getProgramId();
            
            List<ControlHistory> controlHistoryList = controlHistoryMap.get(programId);
            
            DisplayableProgram displayableProgram = createDisplayableProgram(program, controlHistoryList);
            if (displayableProgram != null) displayableProgramList.add(displayableProgram);
        }
        
        return displayableProgramList;
        
    }

    @Override
    public DisplayableOptOut getDisplayableOptOut(final CustomerAccount customerAccount, 
            final YukonUserContext yukonUserContext) {
        try {
            ScheduledOptOut scheduledOptOut = scheduledOptOutDao.getByCustomerAccount(customerAccount, yukonUserContext);
            if (scheduledOptOut == null) return null;

            Date startDate = scheduledOptOut.getStartDate();
            Date endDate = scheduledOptOut.getEndDate();

            int duration = scheduledOptOut.getDuration();
            DisplayableOptOutType type = (duration > 24) ? 
                    DisplayableOptOutType.FROM : DisplayableOptOutType.FOR;

            DisplayableOptOut displayableOptOut = new DisplayableOptOut(startDate, endDate, type);
            return displayableOptOut;
        } catch (NotFoundException e) {
            return null;
        }
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
    private DisplayableProgram createDisplayableProgram(final Program program, final List<ControlHistory> controlHistoryList) {
        
        // Filter Rule #1
        if (controlHistoryList == null || controlHistoryList.isEmpty()) return null;
        
        // Filter Rule #2
        boolean containsOnlyNotEnrolledHistory = containsOnlyNotEnrolledHistory(controlHistoryList);
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
    
    private boolean containsOnlyNotEnrolledHistory(List<ControlHistory> controlHistoryList) {
        for (final ControlHistory controlHistory : controlHistoryList) {
            ControlHistoryStatus status = controlHistory.getCurrentStatus();
            if (!status.equals(ControlHistoryStatus.NOT_ENROLLED)) return false;
        }
        return true;
    }

    @Autowired
    public void setScheduledOptOutDao(ScheduledOptOutDao scheduledOptOutDao) {
        this.scheduledOptOutDao = scheduledOptOutDao;
    }

    @Autowired
    public void setControlHistoryDao(ControlHistoryDao controlHistoryDao) {
        this.controlHistoryDao = controlHistoryDao;
    }
    
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
}

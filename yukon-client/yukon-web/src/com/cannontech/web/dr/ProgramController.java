package com.cannontech.web.dr;


import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.user.YukonUserContext;

@Controller
public class ProgramController {
    private ControlAreaService controlAreaService = null;
    private ScenarioDao scenarioDao = null;
    private ProgramService programService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;
    private LoadGroupControllerHelper loadGroupControllerHelper;
    private RolePropertyDao rolePropertyDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;

    public static class GearAdjustmentTimeSlot {
        private Date startTime;
        private Date endTime;
        private GearAdjustmentTimeSlot(Date startTime) {
            this.startTime = startTime;
        }

        public Date getStartTime() {
            return startTime;
        }

        public Date getEndTime() {
            return endTime;
        }
    }

    public static class StartProgramBackingBean {
        private int programId;
        private int gearNumber;
        // only used for target cycle gears
        private boolean addAdjustments;
        private int numAdjustments;
        private List<Double> gearAdjustments = new ArrayList<Double>();
        private boolean startNow;
        private Date startDate;
        private boolean scheduleStop;
        private Date stopDate;
        private boolean autoObserveConstraints;

        public int getProgramId() {
            return programId;
        }

        public void setProgramId(int programId) {
            this.programId = programId;
        }

        public int getGearNumber() {
            return gearNumber;
        }

        public void setGearNumber(int gearNumber) {
            this.gearNumber = gearNumber;
        }

        public boolean isAddAdjustments() {
            return addAdjustments;
        }

        public void setAddAdjustments(boolean addAdjustments) {
            this.addAdjustments = addAdjustments;
        }

        public int getNumAdjustments() {
            return numAdjustments;
        }

        public void setNumAdjustments(int numAdjustments) {
            this.numAdjustments = numAdjustments;
        }

        public List<Double> getGearAdjustments() {
            return gearAdjustments;
        }

        public void setGearAdjustments(List<Double> gearAdjustments) {
            this.gearAdjustments = gearAdjustments;
        }

        public boolean isStartNow() {
            return startNow;
        }

        public void setStartNow(boolean startNow) {
            this.startNow = startNow;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public boolean isScheduleStop() {
            return scheduleStop;
        }

        public void setScheduleStop(boolean scheduleStop) {
            this.scheduleStop = scheduleStop;
        }

        public Date getStopDate() {
            return stopDate;
        }

        public void setStopDate(Date stopDate) {
            this.stopDate = stopDate;
        }

        public boolean isAutoObserveConstraints() {
            return autoObserveConstraints;
        }

        public void setAutoObserveConstraints(boolean autoObserveConstraints) {
            this.autoObserveConstraints = autoObserveConstraints;
        }
    }

    public static class StopProgramBackingBean {
        private int programId;
        private boolean stopNow;
        private Date stopDate;
        private boolean useStopGear;
        private int gearNumber;

        public int getProgramId() {
            return programId;
        }

        public void setProgramId(int programId) {
            this.programId = programId;
        }

        public boolean isStopNow() {
            return stopNow;
        }

        public void setStopNow(boolean stopNow) {
            this.stopNow = stopNow;
        }

        public Date getStopDate() {
            return stopDate;
        }

        public void setStopDate(Date stopDate) {
            this.stopDate = stopDate;
        }

        public boolean isUseStopGear() {
            return useStopGear;
        }

        public void setUseStopGear(boolean useStopGear) {
            this.useStopGear = useStopGear;
        }

        public int getGearNumber() {
            return gearNumber;
        }

        public void setGearNumber(int gearNumber) {
            this.gearNumber = gearNumber;
        }
    }

    @RequestMapping("/program/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {

        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, null);

        return "dr/program/list.jsp";
    }

    @RequestMapping("/program/detail")
    public String detail(int programId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE);
        
        modelMap.addAttribute("program", program);

        UiFilter<DisplayablePao> detailFilter = new LoadGroupsForProgramFilter(programId);
        loadGroupControllerHelper.filterGroups(modelMap, userContext, backingBean,
                                               result, status, detailFilter);

        DisplayablePao parentControlArea =
            controlAreaService.findControlAreaForProgram(userContext, programId);
        modelMap.addAttribute("parentControlArea", parentControlArea);
        List<DisplayablePao> parentScenarios = scenarioDao.findScenariosForProgram(programId);
        modelMap.addAttribute("parentScenarios", parentScenarios);
        return "dr/program/detail.jsp";
    }

    /**
     * Page one of the "start program" saga.
     */
    @RequestMapping("/program/startProgramDetails")
    public String startProgramDetails(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        // TODO:  need to not do this if they came via the "back" button...
        backingBean.setGearNumber(1);
        backingBean.setStartNow(true);
        backingBean.setStartDate(new Date());
        backingBean.setScheduleStop(true);
        backingBean.setStopDate(new DateTime(DateTimeZone.forTimeZone(userContext.getTimeZone())).plusHours(4).toDate());

        modelMap.addAttribute("program", program);

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);
        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);

        modelMap.addAttribute("autoObserveConstraintsAllowed", autoObserveConstraintsAllowed);
        modelMap.addAttribute("checkConstraintsAllowed", checkConstraintsAllowed);

        if (checkConstraintsAllowed && autoObserveConstraintsAllowed) {
            // It might be more sane to change the "DEFAULT_CONSTRAINT_SELECTION"
            // role property to something more like "AUTO_OBSERVE_CONSTRAINTS_BY_DEFAULT".
            String defaultConstraint =
                rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_CONSTRAINT_SELECTION, user);
            backingBean.setAutoObserveConstraints(defaultConstraint.equalsIgnoreCase(LMManualControlRequest.CONSTRAINT_FLAG_STRS[LMManualControlRequest.CONSTRAINTS_FLAG_USE]));
        }

        addGearsToModel(program, modelMap);

        return "dr/program/startProgramDetails.jsp";
    }

    @RequestMapping("/program/startProgramGearAdjustments")
    public String startProgramGearAdjustments(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        // TODO:  validate

        modelMap.addAttribute("program", program);

        LMProgramBase programBase = programService.map(program);
        LMProgramDirectGear gear =
            ((IGearProgram) programBase).getDirectGearVector().get(backingBean.getGearNumber() - 1);

        if (!gear.isTargetCycle()) {
            // they really can't adjust gears; got here via bug or hack
            throw new RuntimeException("startProgramGearAdjustments called for non-target cycle gear");
        }

        // TODO:  when coming at this page from "back", don't reinitialize adjustments
        modelMap.addAttribute("gear", gear);
        Calendar timeSlotStartCal = Calendar.getInstance(userContext.getLocale());
        timeSlotStartCal.setTime(backingBean.startDate);
        timeSlotStartCal.set(Calendar.MINUTE, 0);
        timeSlotStartCal.set(Calendar.SECOND, 0);
        timeSlotStartCal.set(Calendar.MILLISECOND, 0);
        int numTimeSlots = LCUtils.getTimeSlotsForTargetCycle(backingBean.stopDate, backingBean.startDate, gear.getMethodPeriod());
        backingBean.numAdjustments = numTimeSlots;
        backingBean.gearAdjustments.clear();
        GearAdjustmentTimeSlot[] timeSlots = new GearAdjustmentTimeSlot[numTimeSlots];
        for (int index = 0; index < numTimeSlots; index++) {
            backingBean.gearAdjustments.add(100.0);
            timeSlots[index] = new GearAdjustmentTimeSlot(timeSlotStartCal.getTime());
            timeSlotStartCal.add(Calendar.HOUR_OF_DAY, 1);
            timeSlots[index].endTime = timeSlotStartCal.getTime();
        }
        modelMap.addAttribute("timeSlots", timeSlots);
        return "dr/program/startProgramGearAdjustments.jsp";
    }

    @RequestMapping("/program/startProgramConstraints")
    public String startProgramConstraints(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        // With start and stop, it's important that we get the values
        // squared away up front and don't change them after checking
        // constraints so when we actually schedule the program, we are
        // scheduling exactly what we checked for constraints.
        // We keep the "startNow" and "scheduleStop" booleans around so we
        // always know if the user chose those options.
        if (backingBean.startNow) {
            backingBean.startDate = new Date();
        }
        if (!backingBean.scheduleStop) {
            backingBean.stopDate =
                new DateTime(DateTimeZone.forTimeZone(userContext.getTimeZone())).plusYears(1).toDate();
        }

        // TODO:  validate

        modelMap.addAttribute("program", program);

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);
        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);
        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);

        if (autoObserveConstraintsAllowed && backingBean.isAutoObserveConstraints()) {
            return startProgram(backingBean, false, modelMap, userContext);
        }

        if (!checkConstraintsAllowed) {
            // they're not allowed to do anything...they got here by hacking
            // (or a bug)
            return closeDialog(modelMap);
        }

        modelMap.addAttribute("overrideAllowed", overrideAllowed);
        ConstraintViolations violations =
            programService.getConstraintViolationForStartProgram(userContext,
                                                                 backingBean.getProgramId(),
                                                                 backingBean.getGearNumber(),
                                                                 backingBean.getStartDate(),
                                                                 backingBean.getStopDate());
        modelMap.addAttribute("violations", violations);

        return "dr/program/startProgramConstraints.jsp";
    }

    @RequestMapping("/program/startProgram")
    public String startProgram(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            Boolean overrideConstraints,
            ModelMap modelMap, YukonUserContext userContext) {

        // TODO:  validate

        int programId = backingBean.getProgramId();
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        assertOverrideAllowed(userContext, overrideConstraints);

        String additionalInfo = null;
        if (backingBean.numAdjustments > 0) {
            additionalInfo = "adjustments " +
            		StringUtils.join(backingBean.gearAdjustments, ' ');
        }
        programService.startProgram(userContext, programId,
                                    backingBean.getGearNumber(),
                                    backingBean.getStartDate(),
                                    backingBean.isScheduleStop(),
                                    backingBean.getStopDate(),
                                    overrideConstraints != null && overrideConstraints,
                                    additionalInfo);

        return closeDialog(modelMap);
    }

    @RequestMapping("/program/stopProgramDetails")
    public String stopProgramDetails(
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        // TODO:  don't do this if we're coming here from the back button
        backingBean.stopNow = true;
        backingBean.gearNumber = 1;
        backingBean.stopDate = new Date();

        DisplayablePao program = programService.getProgram(backingBean.programId);
        modelMap.addAttribute("program", program);
        boolean stopGearAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                                                userContext.getYukonUser());
        modelMap.addAttribute("stopGearAllowed", stopGearAllowed);
        if (stopGearAllowed) {
            addGearsToModel(program, modelMap);
        }

        return "dr/program/stopProgramDetails.jsp";
    }

    @RequestMapping("/program/stopProgramConstraints")
    public String stopProgramConstraints(
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        // TODO:  validate

        modelMap.addAttribute("program", program);

        assertStopGearAllowed(userContext);

        ConstraintViolations violations =
            programService.getConstraintViolationsForStopProgram(userContext,
                                                                 backingBean.getProgramId(),
                                                                 backingBean.getGearNumber(),
                                                                 backingBean.getStopDate());
        modelMap.addAttribute("violations", violations);

        return "dr/program/stopProgramConstraints.jsp";
    }

    @RequestMapping("/program/stopProgram")
    public String stopProgram(
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            Boolean overrideConstraints,
            ModelMap modelMap, YukonUserContext userContext) {

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        if (backingBean.useStopGear) {
            assertStopGearAllowed(userContext);
            assertOverrideAllowed(userContext, overrideConstraints);
            programService.stopProgramWithGear(userContext,
                                               backingBean.programId,
                                               backingBean.gearNumber,
                                               backingBean.stopDate,
                                               overrideConstraints != null && overrideConstraints);
        } else if (backingBean.stopNow) {
            programService.stopProgram(userContext, backingBean.programId);
        } else {
            programService.scheduleProgramStop(userContext,
                                       backingBean.programId,
                                       backingBean.stopDate);
        }

        return closeDialog(modelMap);
    }

    @RequestMapping("/program/getChangeGearValue")
    public String getGearChangeValue(ModelMap modelMap, int programId, YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        this.addGearsToModel(program, modelMap);
        modelMap.addAttribute("program", program);
        
        return "dr/program/getChangeGearValue.jsp";
    }
    
    @RequestMapping("/program/changeGear")
    public String changeGear(ModelMap modelMap, int programId, int gearNumber, 
                             YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.changeGear(programId, gearNumber, userContext);
        
        return closeDialog(modelMap);
    }
    
    @RequestMapping("/program/sendEnableConfirm")
    public String sendEnableConfirm(ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("program", program);
        modelMap.addAttribute("isEnabled", isEnabled);
        return "dr/program/sendEnableConfirm.jsp";
    }
    
    @RequestMapping("/program/setEnabled")
    public String setEnabled(ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.setEnabled(programId, isEnabled, userContext);
        
        return closeDialog(modelMap);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor fullDateTimeEditor =
            datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);
        programControllerHelper.initBinder(binder, userContext);
        loadGroupControllerHelper.initBinder(binder, userContext);
    }

    private void addGearsToModel(DisplayablePao program, ModelMap modelMap) {
        // TODO:  I don't know how to create programs that aren't LMProgramDirect
        // programs so I don't know how to test them.
        List<LMProgramDirectGear> gears = Collections.emptyList();
        LMProgramBase programBase = programService.map(program);
        if (programBase instanceof IGearProgram) {
            gears = ((IGearProgram) programBase).getDirectGearVector();
        }
        modelMap.addAttribute("gears", gears);
    }

    private void assertStopGearAllowed(YukonUserContext userContext) {
        boolean stopGearAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                          userContext.getYukonUser());

        if (!stopGearAllowed) {
            throw new NotAuthorizedException("not allowed stop gear access");
        }
    }

    private void assertOverrideAllowed(YukonUserContext userContext, Boolean overrideConstraints) {
        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT,
                                          userContext.getYukonUser());
        if (!overrideAllowed && overrideConstraints != null && overrideConstraints) {
            throw new NotAuthorizedException("override not allowed");
        }
    }
    
    private String closeDialog(ModelMap modelMap) {
        modelMap.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
    }

    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }

    @Autowired
    public void setScenarioDao(ScenarioDao scenarioDao) {
        this.scenarioDao = scenarioDao;
    }

    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setProgramControllerHelper(
            ProgramControllerHelper programControllerHelper) {
        this.programControllerHelper = programControllerHelper;
    }

    @Autowired
    public void setLoadGroupControllerHelper(
            LoadGroupControllerHelper loadGroupControllerHelper) {
        this.loadGroupControllerHelper = loadGroupControllerHelper;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setDatePropertyEditorFactory(
            DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
}

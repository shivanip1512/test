package com.cannontech.web.dr;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
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
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.service.DemandResponseService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/program/*")
public class ProgramController {
    private ControlAreaService controlAreaService = null;
    private ScenarioDao scenarioDao = null;
    private ProgramService programService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;
    private LoadGroupControllerHelper loadGroupControllerHelper;
    private RolePropertyDao rolePropertyDao;
    private DemandResponseEventLogService demandResponseEventLogService;
    private FavoritesDao favoritesDao;
    private DemandResponseService drService;

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

    public abstract static class StartProgramBackingBeanBase {
        private boolean startNow;
        private Date startDate;
        private boolean scheduleStop;
        private Date stopDate;
        private boolean autoObserveConstraints;

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

    public static class StartProgramBackingBean extends StartProgramBackingBeanBase {
        private int programId;
        private int gearNumber;
        // only used for target cycle gears
        private boolean addAdjustments;
        private int numAdjustments;
        private List<Double> gearAdjustments = new ArrayList<Double>();

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
    }

    public static class ProgramStartInfo {
        private int programId;
        private int gearNumber;
        private boolean startProgram;
        private boolean overrideConstraints;

        public ProgramStartInfo() {
        }

        public ProgramStartInfo(int programId, int gearNumber,
                boolean startProgram) {
            this.programId = programId;
            this.gearNumber = gearNumber;
            this.startProgram = startProgram;
        }

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

        public boolean isStartProgram() {
            return startProgram;
        }

        public void setStartProgram(boolean startProgram) {
            this.startProgram = startProgram;
        }

        public boolean isOverrideConstraints() {
            return overrideConstraints;
        }

        public void setOverrideConstraints(boolean overrideConstraints) {
            this.overrideConstraints = overrideConstraints;
        }
    }

    @SuppressWarnings("unchecked")
    public static class StartMultipleProgramsBackingBean extends StartProgramBackingBeanBase {
        private Integer controlAreaId;
        private Integer scenarioId;
        private List<ProgramStartInfo> programStartInfo =
            LazyList.decorate(Lists.newArrayList(), FactoryUtils.instantiateFactory(ProgramStartInfo.class));

        public Integer getControlAreaId() {
            return controlAreaId;
        }

        public void setControlAreaId(Integer controlAreaId) {
            this.controlAreaId = controlAreaId;
        }

        public Integer getScenarioId() {
            return scenarioId;
        }

        public void setScenarioId(Integer scenarioId) {
            this.scenarioId = scenarioId;
        }

        public List<ProgramStartInfo> getProgramStartInfo() {
            return programStartInfo;
        }

        public void setProgramStartInfo(List<ProgramStartInfo> programStartInfo) {
            this.programStartInfo = programStartInfo;
        }
    }

    public static class StopProgramBackingBeanBase {
        private boolean stopNow;
        private Date stopDate;

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
    }

    public static class StopProgramBackingBean extends StopProgramBackingBeanBase {
        private int programId;
        private boolean useStopGear;
        private int gearNumber;

        public int getProgramId() {
            return programId;
        }

        public void setProgramId(int programId) {
            this.programId = programId;
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

    public static class ProgramStopInfo {
        private int programId;
        private boolean stopProgram;
        private boolean overrideConstraints;

        public ProgramStopInfo() {
        }

        public ProgramStopInfo(int programId, boolean stopProgram) {
            this.programId = programId;
            this.stopProgram = stopProgram;
        }

        public int getProgramId() {
            return programId;
        }

        public void setProgramId(int programId) {
            this.programId = programId;
        }

        public boolean isStopProgram() {
            return stopProgram;
        }

        public void setStopProgram(boolean stopProgram) {
            this.stopProgram = stopProgram;
        }

        public boolean isOverrideConstraints() {
            return overrideConstraints;
        }

        public void setOverrideConstraints(boolean overrideConstraints) {
            this.overrideConstraints = overrideConstraints;
        }
    }

    @SuppressWarnings("unchecked")
    public static class StopMultipleProgramsBackingBean extends StopProgramBackingBeanBase {
        private Integer controlAreaId;
        private Integer scenarioId;
        private List<ProgramStopInfo> programStopInfo =
            LazyList.decorate(Lists.newArrayList(), FactoryUtils.instantiateFactory(ProgramStopInfo.class));

        public Integer getControlAreaId() {
            return controlAreaId;
        }

        public void setControlAreaId(Integer controlAreaId) {
            this.controlAreaId = controlAreaId;
        }

        public Integer getScenarioId() {
            return scenarioId;
        }

        public void setScenarioId(Integer scenarioId) {
            this.scenarioId = scenarioId;
        }

        public List<ProgramStopInfo> getProgramStopInfo() {
            return programStopInfo;
        }

        public void setProgramStopInfo(List<ProgramStopInfo> programStopInfo) {
            this.programStopInfo = programStopInfo;
        }
    }

    @RequestMapping
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {

        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, null);

        return "dr/program/list.jsp";
    }

    @RequestMapping
    public String detail(int programId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE);

        favoritesDao.detailPageViewed(programId);
        modelMap.addAttribute("program", program);
        boolean isFavorite =
            favoritesDao.isFavorite(programId, userContext.getYukonUser());
        modelMap.addAttribute("isFavorite", isFavorite);

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
    @RequestMapping
    public String startProgramDetails(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

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

        addConstraintsInfoToModel(modelMap, userContext, backingBean);
        addGearsToModel(program, modelMap);

        return "dr/program/startProgramDetails.jsp";
    }

    @RequestMapping
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

        LMProgramBase programBase = programService.getProgramForPao(program);
        LMProgramDirectGear gear =
            ((IGearProgram) programBase).getDirectGearVector().get(backingBean.getGearNumber() - 1);

        if (!gear.isTargetCycle()) {
            // they really can't adjust gears; got here via bug or hack
            throw new RuntimeException("startProgramGearAdjustments called for non-target cycle gear");
        }

        // TODO:  when coming at this page from "back", don't reinitialize adjustments
        modelMap.addAttribute("gear", gear);
        Calendar timeSlotStartCal = Calendar.getInstance(userContext.getLocale());
        timeSlotStartCal.setTime(backingBean.getStartDate());
        timeSlotStartCal.set(Calendar.MINUTE, 0);
        timeSlotStartCal.set(Calendar.SECOND, 0);
        timeSlotStartCal.set(Calendar.MILLISECOND, 0);
        int numTimeSlots = drService.getTimeSlotsForTargetCycle(backingBean.getStopDate(),
                                                                backingBean.getStartDate(),
                                                                gear.getMethodPeriod());
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

    @RequestMapping
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
        if (backingBean.isStartNow()) {
            backingBean.setStartDate(new Date());
        }
        if (!backingBean.isScheduleStop()) {
            backingBean.setStopDate(
                new DateTime(DateTimeZone.forTimeZone(userContext.getTimeZone())).plusYears(1).toDate());
        }

        // TODO:  validate

        modelMap.addAttribute("program", program);

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);

        if (autoObserveConstraintsAllowed && backingBean.isAutoObserveConstraints()) {
            return startProgram(backingBean, false, modelMap, userContext);
        }

        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);
        if (!checkConstraintsAllowed) {
            // they're not allowed to do anything...they got here by hacking
            // (or a bug)
            return closeDialog(modelMap);
        }

        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);
        modelMap.addAttribute("overrideAllowed", overrideAllowed);

        ConstraintViolations violations =
            programService.getConstraintViolationForStartProgram(backingBean.getProgramId(),
                                                                 backingBean.getGearNumber(),
                                                                 backingBean.getStartDate(),
                                                                 backingBean.getStopDate());
        modelMap.addAttribute("violations", violations);

        return "dr/program/startProgramConstraints.jsp";
    }

    @RequestMapping
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
        int gearNumber = backingBean.getGearNumber();
        Date startDate = backingBean.getStartDate();
        boolean scheduleStop = backingBean.isScheduleStop();
        Date stopDate = backingBean.getStopDate();
        programService.startProgram(programId,
                                    gearNumber,
                                    startDate,
                                    scheduleStop,
                                    stopDate,
                                    overrideConstraints != null && overrideConstraints,
                                    additionalInfo);

        demandResponseEventLogService.threeTierProgramScheduled(userContext.getYukonUser(),
                                                                program.getName(),
                                                                startDate);
        
        return closeDialog(modelMap);
    }

    @RequestMapping
    public String startMultipleProgramsDetails(
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        UiFilter<DisplayablePao> filter = null;

        String paoName = null;
        if (backingBean.controlAreaId != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            paoName = controlArea.getName();
            filter = new ForControlAreaFilter(backingBean.controlAreaId);
        }
        if (backingBean.scenarioId != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            paoName = scenario.getName();
            filter = new ForScenarioFilter(backingBean.scenarioId);
        }

        if (filter == null) {
            throw new IllegalArgumentException();
        }

        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(filter, null, 0, Integer.MAX_VALUE,
                                          userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.startMultiplePrograms.noPrograms." +
                                                 (backingBean.controlAreaId != null ? "controlArea" : "scenario"),
                                                 paoName);
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("programs", programs);

        // TODO:  need to not do this if they came via the "back" button...
        backingBean.setStartNow(true);
        backingBean.setStartDate(new Date());
        backingBean.setScheduleStop(true);
        backingBean.setStopDate(new DateTime(DateTimeZone.forTimeZone(userContext.getTimeZone())).plusHours(4).toDate());
        List<ProgramStartInfo> programStartInfo = new ArrayList<ProgramStartInfo>(programs.size());
        for (DisplayablePao program : programs) {
            programStartInfo.add(new ProgramStartInfo(program.getPaoIdentifier().getPaoId(),
                                                      1, true));
        }
        backingBean.setProgramStartInfo(programStartInfo);

        addConstraintsInfoToModel(modelMap, userContext, backingBean);
        addGearsToModel(searchResult.getResultList(), modelMap);

        return "dr/program/startMultipleProgramsDetails.jsp";
    }

    @RequestMapping
    public String startMultipleProgramsConstraints(
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        if (backingBean.controlAreaId != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
        }
        if (backingBean.scenarioId != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
        }

        // With start and stop, it's important that we get the values
        // squared away up front and don't change them after checking
        // constraints so when we actually schedule the controlArea, we are
        // scheduling exactly what we checked for constraints.
        // We keep the "startNow" and "scheduleStop" booleans around so we
        // always know if the user chose those options.
        if (backingBean.isStartNow()) {
            backingBean.setStartDate(new Date());
        }
        if (!backingBean.isScheduleStop()) {
            backingBean.setStopDate(
                new DateTime(DateTimeZone.forTimeZone(userContext.getTimeZone())).plusYears(1).toDate());
        }

        // TODO:  validate

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);

        if (autoObserveConstraintsAllowed && backingBean.isAutoObserveConstraints()) {
            return startMultiplePrograms(backingBean, modelMap, userContext);
        }

        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);
        if (!checkConstraintsAllowed) {
            // they're not allowed to do anything...they got here by hacking
            // (or a bug)
            return closeDialog(modelMap);
        }

        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);
        modelMap.addAttribute("overrideAllowed", overrideAllowed);

        Map<Integer, ConstraintViolations> violationsByProgramId = Maps.newHashMap();
        Map<Integer, DisplayablePao> programsByProgramId = Maps.newHashMap();
        boolean constraintsViolated = false;
        int numProgramsToStart = 0;
        for (ProgramStartInfo programStartInfo : backingBean.getProgramStartInfo()) {
            if (!programStartInfo.startProgram) {
                continue;
            }

            numProgramsToStart++;
            int programId = programStartInfo.getProgramId();
            DisplayablePao program = programService.getProgram(programId);
            programsByProgramId.put(programId, program);

            ConstraintViolations violations =
                programService.getConstraintViolationForStartProgram(programId,
                                                                     programStartInfo.gearNumber,
                                                                     backingBean.getStartDate(),
                                                                     backingBean.getStopDate());
            if (violations != null && violations.getViolations().size() > 0) {
                violationsByProgramId.put(programId, violations);
                constraintsViolated = true;
            }
        }

        if (numProgramsToStart == 0) {
            // nothing was selected to start...
            // TODO:  error to user that nothing was selected
            return "dr/program/startMultipleProgramsDetails.jsp";
        }

        modelMap.addAttribute("numProgramsToStart", numProgramsToStart);
        modelMap.addAttribute("programsByProgramId", programsByProgramId);
        modelMap.addAttribute("violationsByProgramId", violationsByProgramId);
        modelMap.addAttribute("constraintsViolated", constraintsViolated);

        return "dr/program/startMultipleProgramsConstraints.jsp";
    }

    @RequestMapping
    public String startMultiplePrograms(
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        // TODO:  validate

        if (backingBean.controlAreaId != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            demandResponseEventLogService.threeTierControlAreaStarted(userContext.getYukonUser(),
                                                                      controlArea.getName());
        }
        if (backingBean.scenarioId != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            demandResponseEventLogService.threeTierScenarioStarted(userContext.getYukonUser(),
                                                                   scenario.getName());
        }

        // TODO:  validate permissions on programs too...or at least check to
        // make sure the programs here are actually part of the control area
        // or scenario that we've checked permissions on.

        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT,
                                          userContext.getYukonUser());

        for (ProgramStartInfo programStartInfo : backingBean.getProgramStartInfo()) {
            if (!programStartInfo.startProgram) {
                continue;
            }

            if (programStartInfo.overrideConstraints && !overrideAllowed) {
                throw new NotAuthorizedException("not authorized to override constraints");
            }

            programService.startProgram(programStartInfo.programId,
                                        programStartInfo.gearNumber,
                                        backingBean.getStartDate(),
                                        backingBean.isScheduleStop(),
                                        backingBean.getStopDate(),
                                        programStartInfo.overrideConstraints,
                                        null);
        }

        return closeDialog(modelMap);
    }

    @RequestMapping
    public String stopProgramDetails(
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        // TODO:  don't do this if we're coming here from the back button
        backingBean.setStopNow(true);
        backingBean.setGearNumber(1);
        backingBean.setStopDate(new Date());

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

    @RequestMapping
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
            programService.getConstraintViolationsForStopProgram(backingBean.getProgramId(),
                                                                 backingBean.getGearNumber(),
                                                                 backingBean.getStopDate());
        modelMap.addAttribute("violations", violations);

        return "dr/program/stopProgramConstraints.jsp";
    }

    @RequestMapping
    public String stopProgram(
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            Boolean overrideConstraints,
            ModelMap modelMap, YukonUserContext userContext) {

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        Date stopDate = backingBean.getStopDate();
        int gearNumber = backingBean.gearNumber;
        if (backingBean.useStopGear) {
            assertStopGearAllowed(userContext);
            assertOverrideAllowed(userContext, overrideConstraints);
            programService.stopProgramWithGear(backingBean.programId,
                                               gearNumber,
                                               stopDate,
                                               overrideConstraints != null && overrideConstraints);
            
            demandResponseEventLogService.threeTierProgramStopped(yukonUser,
                                                                  program.getName(), 
                                                                  stopDate);
        } else if (backingBean.isStopNow()) {
            programService.stopProgram(backingBean.programId);
            demandResponseEventLogService.threeTierProgramStopped(yukonUser,
                                                                  program.getName(), 
                                                                  stopDate);
        } else {
            programService.scheduleProgramStop(backingBean.programId, stopDate);
            demandResponseEventLogService.threeTierProgramStopScheduled(yukonUser,
                                                                        program.getName(), 
                                                                        stopDate);
        }

        return closeDialog(modelMap);
    }

    @RequestMapping
    public String stopMultipleProgramsDetails(
            @ModelAttribute("backingBean") StopMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        UiFilter<DisplayablePao> filter = null;

        if (backingBean.controlAreaId != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            filter = new ForControlAreaFilter(backingBean.controlAreaId);
        }
        if (backingBean.scenarioId != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            filter = new ForScenarioFilter(backingBean.scenarioId);
        }

        if (filter == null) {
            throw new IllegalArgumentException();
        }

        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(filter, null, 0, Integer.MAX_VALUE,
                                          userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        modelMap.addAttribute("programs", programs);

        // TODO:  don't do this if we're coming here from the back button
        backingBean.setStopNow(true);
        backingBean.setStopDate(new Date());
        List<ProgramStopInfo> programStopInfo = new ArrayList<ProgramStopInfo>(programs.size());
        for (DisplayablePao program : programs) {
            programStopInfo.add(new ProgramStopInfo(program.getPaoIdentifier().getPaoId(),
                                                    true));
        }
        backingBean.setProgramStopInfo(programStopInfo);

        return "dr/program/stopMultipleProgramsDetails.jsp";
    }

    @RequestMapping
    public String stopMultiplePrograms(
            @ModelAttribute("backingBean") StopMultipleProgramsBackingBean backingBean,
            Boolean overrideConstraints,
            ModelMap modelMap, YukonUserContext userContext) {

        Date stopDate = backingBean.getStopDate();
        if (backingBean.controlAreaId != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            demandResponseEventLogService.threeTierControlAreaStopped(userContext.getYukonUser(),
                                                                      controlArea.getName());
        }
        if (backingBean.scenarioId != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            demandResponseEventLogService.threeTierScenarioStopped(userContext.getYukonUser(),
                                                                   scenario.getName());
        }

        // TODO:  validate permissions on programs too...or at least check to
        // make sure the programs here are actually part of the control area
        // or scenario that we've checked permissions on.

        for (ProgramStopInfo programStopInfo : backingBean.getProgramStopInfo()) {
            if (!programStopInfo.stopProgram) {
                continue;
            }

            if (backingBean.isStopNow()) {
                programService.stopProgram(programStopInfo.programId);
            } else {
                programService.scheduleProgramStop(programStopInfo.programId, stopDate);
            }
        }

        return closeDialog(modelMap);
    }

    @RequestMapping
    public String getChangeGearValue(ModelMap modelMap, int programId, YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        addGearsToModel(program, modelMap);
        modelMap.addAttribute("program", program);

        return "dr/program/getChangeGearValue.jsp";
    }
    
    @RequestMapping
    public String changeGear(ModelMap modelMap, int programId, int gearNumber, 
                             YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.changeGear(programId, gearNumber);
        
        demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());
        
        return closeDialog(modelMap);
    }
    
    @RequestMapping
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
    
    @RequestMapping
    public String setEnabled(ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.setEnabled(programId, isEnabled);

        if(isEnabled) {
            demandResponseEventLogService.threeTierProgramEnabled(yukonUser, program.getName());
        } else {
            demandResponseEventLogService.threeTierProgramDisabled(yukonUser, program.getName());
        }
        
        return closeDialog(modelMap);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext);
        loadGroupControllerHelper.initBinder(binder, userContext);
    }

    private void addConstraintsInfoToModel(ModelMap modelMap,
            YukonUserContext userContext, StartProgramBackingBeanBase backingBean) {
        LiteYukonUser user = userContext.getYukonUser();
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

    }

    private void addGearsToModel(List<DisplayablePao> programs, ModelMap modelMap) {
        Map<Integer, List<LMProgramDirectGear>> gearsByProgramId = Maps.newHashMap();
        for (DisplayablePao program : programs) {
            List<LMProgramDirectGear> gears = Collections.emptyList();
            LMProgramBase programBase = programService.getProgramForPao(program);
            if (programBase instanceof IGearProgram) {
                gears = ((IGearProgram) programBase).getDirectGearVector();
            }
            gearsByProgramId.put(program.getPaoIdentifier().getPaoId(), gears);
        }
        modelMap.addAttribute("gearsByProgramId", gearsByProgramId);
    }

    private void addGearsToModel(DisplayablePao program, ModelMap modelMap) {
        // TODO:  I don't know how to create programs that aren't LMProgramDirect
        // programs so I don't know how to test them.
        List<LMProgramDirectGear> gears = Collections.emptyList();
        LMProgramDirectGear currentGear = null;
        LMProgramBase programBase = programService.getProgramForPao(program);
        if (programBase instanceof IGearProgram) {
            gears = ((IGearProgram) programBase).getDirectGearVector();
            currentGear = ((IGearProgram) programBase).getCurrentGear();
        }
        modelMap.addAttribute("gears", gears);
        modelMap.addAttribute("currentGear", currentGear);
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
    public void setDemandResponseEventLogService(DemandResponseEventLogService demandResponseEventLogService) {
        this.demandResponseEventLogService = demandResponseEventLogService;
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }

    @Autowired
    public void setDrService(DemandResponseService drService) {
        this.drService = drService;
    }
}

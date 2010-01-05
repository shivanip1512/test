package com.cannontech.web.dr;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
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
import com.cannontech.common.pao.DisplayablePaoComparator;
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
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
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
        backingBean.initDefaults(userContext);

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
        List<GearAdjustment> gearAdjustments =
            programService.getDefaultAdjustments(backingBean.getStartDate(),
                                                 backingBean.getActualStopDate(),
                                                 null, userContext);
        backingBean.setGearAdjustments(gearAdjustments);

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

        List<GearAdjustment> gearAdjustments = null;
        if (backingBean.isAddAdjustments()) {
            gearAdjustments = backingBean.getGearAdjustments();
        }
        ConstraintViolations violations =
            programService.getConstraintViolationForStartProgram(backingBean.getProgramId(),
                                                                 backingBean.getGearNumber(),
                                                                 backingBean.getStartDate(), null,
                                                                 backingBean.getActualStopDate(), null,
                                                                 gearAdjustments);
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

        List<GearAdjustment> gearAdjustments = null;
        if (backingBean.isAddAdjustments()) {
            gearAdjustments = backingBean.getGearAdjustments();
        }
        int gearNumber = backingBean.getGearNumber();
        Date startDate = backingBean.getStartDate();
        boolean scheduleStop = backingBean.isScheduleStop();
        programService.startProgram(programId, gearNumber, startDate, null,
                                    scheduleStop, backingBean.getActualStopDate(), null,
                                    overrideConstraints != null && overrideConstraints,
                                    gearAdjustments);

        demandResponseEventLogService.threeTierProgramScheduled(userContext.getYukonUser(),
                                                                program.getName(),
                                                                startDate);

        return closeDialog(modelMap);
    }

    @RequestMapping
    public String startMultipleProgramsDetails(
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {
        return startMultipleProgramsDetails(backingBean, modelMap, null, userContext);
    }

    public String startMultipleProgramsDetails(
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, List<MessageSourceResolvable> errors,
            YukonUserContext userContext) {

        UiFilter<DisplayablePao> filter = null;

        String paoName = null;
        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            paoName = controlArea.getName();
            filter = new ForControlAreaFilter(backingBean.getControlAreaId());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            paoName = scenario.getName();
            filter = new ForScenarioFilter(backingBean.getScenarioId());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
            modelMap.addAttribute("scenarioPrograms", scenarioPrograms);
        }

        if (filter == null) {
            throw new IllegalArgumentException();
        }

        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(filter, new DisplayablePaoComparator(),
                                          0, Integer.MAX_VALUE, userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.startMultiplePrograms.noPrograms." +
                                                 (backingBean.getControlAreaId() != null ? "controlArea" : "scenario"),
                                                 paoName);
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("programs", programs);

        if (errors != null && errors.size() > 0) {
            modelMap.addAttribute("errors", errors);
        } else {
            // TODO:  need to not do this if they came via the "back" button...
            backingBean.initDefaults(userContext, programs, scenarioPrograms);
        }

        addConstraintsInfoToModel(modelMap, userContext, backingBean);
        addGearsToModel(searchResult.getResultList(), modelMap);

        Map<Integer, Map<Integer, Boolean>> programIndexTargetGearMap = getIndexBasedIsTargetGearMap(programs);
        
        modelMap.addAttribute("targetGearMap",programIndexTargetGearMap);
        return "dr/program/startMultipleProgramsDetails.jsp";
    }

    @RequestMapping
    public String startMultipleProgramsGearAdjustments(
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {

        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
        }
        
        List<ProgramStartInfo> programStartInfos = backingBean.getProgramStartInfo();
        List<Integer> programsWithTargetCycleGears = Lists.newArrayList();
        for (ProgramStartInfo programStartInfo : programStartInfos) {
            if (!programStartInfo.isStartProgram()) {
                continue;
            }

            DisplayablePao program = programService.getProgram(programStartInfo.getProgramId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         program, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);

            // TODO:  validate

            LMProgramBase programBase = programService.getProgramForPao(program);

            LMProgramDirectGear gear =
                ((IGearProgram) programBase).getDirectGearVector().get(programStartInfo.getGearNumber() - 1);

            if (gear.isTargetCycle()) {
                programsWithTargetCycleGears.add(programStartInfo.getProgramId());
            }
        }

        if (programsWithTargetCycleGears.size() == 0) {
            // they really can't adjust gears; got here via bug or hack
            throw new RuntimeException("startMultipleProgramsGearAdjustments called for non-target cycle gear");
        }

        // TODO:  when coming at this page from "back", don't reinitialize adjustments
        List<GearAdjustment> gearAdjustments =
            programService.getDefaultAdjustments(backingBean.getStartDate(),
                                                 backingBean.getActualStopDate(),
                                                 scenarioPrograms.values(),
                                                 userContext);
        backingBean.setGearAdjustments(gearAdjustments);

        return "dr/program/startProgramGearAdjustments.jsp";
    }

    @RequestMapping
    public String startMultipleProgramsConstraints(
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
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
            if (!programStartInfo.isStartProgram()) {
                continue;
            }

            numProgramsToStart++;
            int programId = programStartInfo.getProgramId();
            DisplayablePao program = programService.getProgram(programId);
            programsByProgramId.put(programId, program);

            LMProgramBase programBase = programService.getProgramForPao(program);
            LMProgramDirectGear gear =
                ((IGearProgram) programBase).getDirectGearVector().get(programStartInfo.getGearNumber() - 1);

            List<GearAdjustment> gearAdjustments = null;
            if (gear.isTargetCycle() &&
                backingBean.isAddAdjustments()) {
                gearAdjustments = backingBean.getGearAdjustments();
            }

            Duration startOffset = null;
            Duration stopOffset = null;
            if (scenarioPrograms != null) {
                ScenarioProgram scenarioProgram = scenarioPrograms.get(programStartInfo.getProgramId());
                startOffset = scenarioProgram.getStartOffset();
                stopOffset = scenarioProgram.getStopOffset();
            }
            ConstraintViolations violations =
                programService.getConstraintViolationForStartProgram(programId,
                                                                     programStartInfo.getGearNumber(),
                                                                     backingBean.getStartDate(),
                                                                     startOffset,
                                                                     backingBean.getActualStopDate(),
                                                                     stopOffset,
                                                                     gearAdjustments);
            if (violations != null && violations.getViolations().size() > 0) {
                violationsByProgramId.put(programId, violations);
                constraintsViolated = true;
            }
        }

        if (numProgramsToStart == 0) {
            // nothing was selected to start...
            // TODO:  error to user that nothing was selected
            List<MessageSourceResolvable> errors = Lists.newArrayList();
            MessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.startMultiplePrograms.noProgramsSelected");
            errors.add(error);
            return startMultipleProgramsDetails(backingBean, modelMap, errors,
                                                userContext);
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

        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            demandResponseEventLogService.threeTierControlAreaStarted(userContext.getYukonUser(),
                                                                      controlArea.getName());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            demandResponseEventLogService.threeTierScenarioStarted(userContext.getYukonUser(),
                                                                   scenario.getName());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
        }

        // TODO:  validate permissions on programs too...or at least check to
        // make sure the programs here are actually part of the control area
        // or scenario that we've checked permissions on.

        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT,
                                          userContext.getYukonUser());

        for (ProgramStartInfo programStartInfo : backingBean.getProgramStartInfo()) {
            if (!programStartInfo.isStartProgram()) {
                continue;
            }

            if (programStartInfo.isOverrideConstraints() && !overrideAllowed) {
                throw new NotAuthorizedException("not authorized to override constraints");
            }

            DisplayablePao program = programService.getProgram(programStartInfo.getProgramId());
            LMProgramBase programBase = programService.getProgramForPao(program);
            LMProgramDirectGear gear =
                ((IGearProgram) programBase).getDirectGearVector().get(programStartInfo.getGearNumber() - 1);

            List<GearAdjustment> gearAdjustments = null;
            if (gear.isTargetCycle() &&
                backingBean.isAddAdjustments()) {
                gearAdjustments = backingBean.getGearAdjustments();
            }

            Duration startOffset = null;
            Duration stopOffset = null;
            if (scenarioPrograms != null) {
                ScenarioProgram scenarioProgram = scenarioPrograms.get(programStartInfo.getProgramId());
                startOffset = scenarioProgram.getStartOffset();
                stopOffset = scenarioProgram.getStopOffset();
            }

            programService.startProgram(programStartInfo.getProgramId(),
                                        programStartInfo.getGearNumber(),
                                        backingBean.getStartDate(), startOffset,
                                        backingBean.isScheduleStop(),
                                        backingBean.getActualStopDate(), stopOffset,
                                        programStartInfo.isOverrideConstraints(),
                                        gearAdjustments);
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

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
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
        int gearNumber = backingBean.getGearNumber();
        if (backingBean.isUseStopGear()) {
            assertStopGearAllowed(userContext);
            assertOverrideAllowed(userContext, overrideConstraints);
            programService.stopProgramWithGear(backingBean.getProgramId(),
                                               gearNumber,
                                               stopDate,
                                               overrideConstraints != null && overrideConstraints);
            
            demandResponseEventLogService.threeTierProgramStopped(yukonUser,
                                                                  program.getName(), 
                                                                  stopDate);
        } else if (backingBean.isStopNow()) {
            programService.stopProgram(backingBean.getProgramId());
            demandResponseEventLogService.threeTierProgramStopped(yukonUser,
                                                                  program.getName(), 
                                                                  stopDate);
        } else {
            programService.scheduleProgramStop(backingBean.getProgramId(),
                                               stopDate, null);
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

        String paoName = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            paoName = controlArea.getName();
            filter = new ForControlAreaFilter(backingBean.getControlAreaId());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            paoName = scenario.getName();
            filter = new ForScenarioFilter(backingBean.getScenarioId());
            Map<Integer, ScenarioProgram> scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
            modelMap.addAttribute("scenarioPrograms", scenarioPrograms);
        }

        if (filter == null) {
            throw new IllegalArgumentException();
        }

        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(filter, new DisplayablePaoComparator(),
                                          0, Integer.MAX_VALUE, userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopMultiplePrograms.noPrograms." +
                                                 (backingBean.getControlAreaId() != null ? "controlArea" : "scenario"),
                                                 paoName);
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
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
        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         controlArea, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("controlArea", controlArea);
            demandResponseEventLogService.threeTierControlAreaStopped(userContext.getYukonUser(),
                                                                      controlArea.getName());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                         scenario, 
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            modelMap.addAttribute("scenario", scenario);
            demandResponseEventLogService.threeTierScenarioStopped(userContext.getYukonUser(),
                                                                   scenario.getName());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
        }

        // TODO:  validate permissions on programs too...or at least check to
        // make sure the programs here are actually part of the control area
        // or scenario that we've checked permissions on.

        if (backingBean.isStopNow()) {
            // If we're starting a scenario, we need a common base "now" time
            // for offsets.
            stopDate = new Date();
        }
        for (ProgramStopInfo programStopInfo : backingBean.getProgramStopInfo()) {
            if (!programStopInfo.isStopProgram()) {
                continue;
            }

            Duration stopOffset = null;
            if (scenarioPrograms != null) {
                ScenarioProgram scenarioProgram = scenarioPrograms.get(programStopInfo.getProgramId());
                stopOffset = scenarioProgram.getStopOffset();
            }

            if (backingBean.isStopNow() && stopOffset == null) {
                programService.stopProgram(programStopInfo.getProgramId());
            } else {
                programService.scheduleProgramStop(programStopInfo.getProgramId(),
                                                   stopDate, stopOffset);
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
    
    private Map<Integer, Map<Integer, Boolean>> getIndexBasedIsTargetGearMap(List<DisplayablePao> programs) {
        Map<Integer, Map<Integer, Boolean>> programIndexTargetGearMap = new HashMap<Integer, Map<Integer, Boolean>>();
        for (int i = 0; i < programs.size(); i++){
            DisplayablePao displayablePao = programs.get(i);
            LMProgramBase programBase = programService.getProgramForPao(displayablePao);
            List<LMProgramDirectGear> gears;
            if (programBase instanceof IGearProgram) {
                gears = ((IGearProgram) programBase).getDirectGearVector();
                Map<Integer, Boolean> gearIndexIsTrueCycleMap = new HashMap<Integer, Boolean>();
                programIndexTargetGearMap.put(i, gearIndexIsTrueCycleMap);
                for (int j = 0; j < gears.size(); j++){
                    LMProgramDirectGear lmProgramDirectGear = gears.get(j);
                    gearIndexIsTrueCycleMap.put(j+1, lmProgramDirectGear.isTargetCycle());
                }
            }
        }
        return programIndexTargetGearMap;
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
}

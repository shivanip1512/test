package com.cannontech.web.dr;


import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.taglib.StandardPageTag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/program/start/*")
public class StartProgramController extends ProgramControllerBase {
    private StartProgramDatesValidator datesValidator =
        new StartProgramDatesValidator();
    private StartProgramAdjustmentsValidator gearAdjustmentsValidator =
        new StartProgramAdjustmentsValidator();

    /**
     * Page one of the "start program" saga.
     */
    @RequestMapping
    public String details(ModelMap model, Boolean fromBack,
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext) {
    	
    	model.addAttribute(StandardPageTag.PAGE_EDIT_MODE_ATTR, PageEditMode.EDIT);
    	
        if (fromBack == null || !fromBack) {
            backingBean.initDefaults(userContext);
        }

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     program,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        model.addAttribute("program", program);

        addConstraintsInfoToModel(model, fromBack, userContext, backingBean);
        addGearsToModel(program, model);

        return "dr/program/startProgramDetails.jsp";
    }

    @RequestMapping
    public String gearAdjustments(ModelMap model, Boolean fromBack,
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext) {
        if (fromBack == null || !fromBack) {
            if (backingBean.isStartNow()) {
                backingBean.setStartDate(backingBean.getNow());
            }
            validate(datesValidator, model, backingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                return details(model, true, backingBean, bindingResult,
                                userContext);
            }
        }

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     program,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        model.addAttribute("program", program);

        LMProgramBase programBase = programService.getProgramForPao(program);
        LMProgramDirectGear gear =
            ((IGearProgram) programBase).getDirectGearVector().get(backingBean.getGearNumber() - 1);

        if (!gear.isTargetCycle()) {
            // they really can't adjust gears; got here via bug or hack
            throw new RuntimeException("gearAdjustments called for non-target cycle gear");
        }

        model.addAttribute("gear", gear);
        if (fromBack == null || !fromBack) {
            List<GearAdjustment> gearAdjustments =
                programService.getDefaultAdjustments(backingBean.getStartDate(),
                                                     backingBean.getActualStopDate(),
                                                     null, userContext);
            backingBean.setGearAdjustments(gearAdjustments);
        }

        return "dr/program/startProgramGearAdjustments.jsp";
    }

    @RequestMapping
    public String constraints(ModelMap model, String from,
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext) {
        if (backingBean.isStartNow()) {
            backingBean.setStartDate(backingBean.getNow());
        }
        validate(datesValidator, model, backingBean, bindingResult);
        if (backingBean.isAddAdjustments()) {
            validate(gearAdjustmentsValidator, model, backingBean,
                     bindingResult);
        }
        if (bindingResult.hasErrors()) {
            if ("gear_adjustments".equals(from)) {
                return gearAdjustments(model, true, backingBean,
                                              bindingResult, userContext);
            }
            return details(model, true, backingBean, bindingResult, userContext);
        }

        LiteYukonUser user = userContext.getYukonUser();

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(user,
                                                     program,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        model.addAttribute("program", program);

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);

        if (autoObserveConstraintsAllowed && backingBean.isAutoObserveConstraints()) {
            return start(model, from, backingBean, bindingResult, false, userContext);
        }

        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);
        if (!checkConstraintsAllowed) {
            // they're not allowed to do anything...they got here by hacking
            // (or a bug)
            return closeDialog(model);
        }

        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);
        model.addAttribute("overrideAllowed", overrideAllowed);

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
        model.addAttribute("violations", violations);

        return "dr/program/startProgramConstraints.jsp";
    }

    @RequestMapping
    public String start(ModelMap model, String from,
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            BindingResult bindingResult, Boolean overrideConstraints,
            YukonUserContext userContext) {
        validate(datesValidator, model, backingBean, bindingResult);
        if (backingBean.isAddAdjustments()) {
            validate(gearAdjustmentsValidator, model, backingBean,
                     bindingResult);
        }
        if (bindingResult.hasErrors()) {
            if ("gear_adjustments".equals(from)) {
                return gearAdjustments(model, true, backingBean,
                                        bindingResult, userContext);
            }
            return details(model, true, backingBean, bindingResult, userContext);
        }

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

        return closeDialog(model);
    }

    @RequestMapping
    public String multipleDetails(ModelMap model,Boolean fromBack,
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext) {

    	model.addAttribute(StandardPageTag.PAGE_EDIT_MODE_ATTR, PageEditMode.EDIT);
    	
        UiFilter<DisplayablePao> filter = null;

        String paoName = null;
        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
            paoName = controlArea.getName();
            filter = new ForControlAreaFilter(backingBean.getControlAreaId());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
            paoName = scenario.getName();
            filter = new ForScenarioFilter(backingBean.getScenarioId());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
            model.addAttribute("scenarioPrograms", scenarioPrograms);
        }

        if (filter == null) {
            throw new IllegalArgumentException();
        }

        SearchResult<DisplayablePao> searchResult =
            programService.filterPrograms(filter, new DisplayablePaoComparator(),
                                          0, Integer.MAX_VALUE, userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {

            model.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.startMultiplePrograms.noPrograms." +
                                                 (backingBean.getControlAreaId() != null ? "controlArea" : "scenario"),
                                                 paoName);
            model.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        model.addAttribute("programs", programs);

        if (fromBack == null || !fromBack) {
            backingBean.initDefaults(userContext, programs, scenarioPrograms);
        }

        addConstraintsInfoToModel(model, fromBack, userContext, backingBean);
        addGearsToModel(searchResult.getResultList(), model);

        Map<Integer, Map<Integer, Boolean>> programIndexTargetGearMap = getIndexBasedIsTargetGearMap(programs);

        model.addAttribute("targetGearMap",programIndexTargetGearMap);
        return "dr/program/startMultipleProgramsDetails.jsp";
    }

    @RequestMapping
    public String multipleGearAdjustments(ModelMap model, Boolean fromBack,
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext) {
        if (fromBack == null || !fromBack) {
            if (backingBean.isStartNow()) {
                backingBean.setStartDate(backingBean.getNow());
            }
            validate(datesValidator, model, backingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                return multipleDetails(model, true, backingBean,
                                        bindingResult, userContext);
            }
        }

        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
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

        if (fromBack == null || !fromBack) {
            List<GearAdjustment> gearAdjustments =
                programService.getDefaultAdjustments(backingBean.getStartDate(),
                                                     backingBean.getActualStopDate(),
                                                     scenarioPrograms.values(),
                                                     userContext);
            backingBean.setGearAdjustments(gearAdjustments);
        }

        return "dr/program/startProgramGearAdjustments.jsp";
    }

    @RequestMapping
    public String multipleConstraints(ModelMap model, String from,
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext) {
        if (backingBean.isStartNow()) {
            backingBean.setStartDate(backingBean.getNow());
        }
        validate(datesValidator, model, backingBean, bindingResult);
        if (backingBean.isAddAdjustments()) {
            validate(gearAdjustmentsValidator, model, backingBean, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            if ("gear_adjustments".equals(from)) {
                return multipleGearAdjustments(model, true, backingBean,
                                                bindingResult, userContext);
            }
            return multipleDetails(model, true, backingBean,
                                    bindingResult, userContext);
        }

        LiteYukonUser user = userContext.getYukonUser();

        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(user,
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
        }

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);

        if (autoObserveConstraintsAllowed && backingBean.isAutoObserveConstraints()) {
            return multipleStart(model, from, backingBean, bindingResult, userContext);
        }

        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);
        if (!checkConstraintsAllowed) {
            // they're not allowed to do anything...they got here by hacking
            // (or a bug)
            return closeDialog(model);
        }

        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);
        model.addAttribute("overrideAllowed", overrideAllowed);

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
            bindingResult.reject("yukon.web.modules.dr.program.startMultiplePrograms.noProgramsSelected");
            return multipleDetails(model, true, backingBean,
                                    bindingResult, userContext);
        }

        model.addAttribute("numProgramsToStart", numProgramsToStart);
        model.addAttribute("programsByProgramId", programsByProgramId);
        model.addAttribute("violationsByProgramId", violationsByProgramId);
        model.addAttribute("constraintsViolated", constraintsViolated);

        return "dr/program/startMultipleProgramsConstraints.jsp";
    }

    @RequestMapping
    public String multipleStart(ModelMap model, String from,
            @ModelAttribute("backingBean") StartMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext) {
        validate(datesValidator, model, backingBean, bindingResult);
        if (backingBean.isAddAdjustments()) {
            validate(gearAdjustmentsValidator, model, backingBean, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            if ("gear_adjustments".equals(from)) {
                return multipleGearAdjustments(model, true, backingBean,
                                                bindingResult, userContext);
            }
            return multipleDetails(model, true, backingBean,
                                    bindingResult, userContext);
        }

        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
            demandResponseEventLogService.threeTierControlAreaStarted(userContext.getYukonUser(),
                                                                      controlArea.getName());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
            demandResponseEventLogService.threeTierScenarioStarted(userContext.getYukonUser(),
                                                                   scenario.getName());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
        }

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

        return closeDialog(model);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            DefaultMessageCodesResolver msgCodesResolver = new DefaultMessageCodesResolver();
            msgCodesResolver.setPrefix("yukon.web.modules.dr.program.startProgram.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
        programControllerHelper.initBinder(binder, userContext);
    }

    private void addConstraintsInfoToModel(ModelMap model, Boolean fromBack,
            YukonUserContext userContext, StartProgramBackingBeanBase backingBean) {
        LiteYukonUser user = userContext.getYukonUser();
        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);
        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);

        model.addAttribute("autoObserveConstraintsAllowed", autoObserveConstraintsAllowed);
        model.addAttribute("checkConstraintsAllowed", checkConstraintsAllowed);

        if (checkConstraintsAllowed && autoObserveConstraintsAllowed) {
            // It might be more sane to change the "DEFAULT_CONSTRAINT_SELECTION"
            // role property to something more like "AUTO_OBSERVE_CONSTRAINTS_BY_DEFAULT".
            String defaultConstraint =
                rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_CONSTRAINT_SELECTION, user);
            if (fromBack == null || !fromBack) {
                backingBean.setAutoObserveConstraints(defaultConstraint.equalsIgnoreCase(LMManualControlRequest.CONSTRAINT_FLAG_STRS[LMManualControlRequest.CONSTRAINTS_FLAG_USE]));
            }
        }
    }

    private void addGearsToModel(List<DisplayablePao> programs, ModelMap model) {
        Map<Integer, List<LMProgramDirectGear>> gearsByProgramId = Maps.newHashMap();
        for (DisplayablePao program : programs) {
            List<LMProgramDirectGear> gears = Collections.emptyList();
            LMProgramBase programBase = programService.getProgramForPao(program);
            if (programBase instanceof IGearProgram) {
                gears = ((IGearProgram) programBase).getDirectGearVector();
            }
            gearsByProgramId.put(program.getPaoIdentifier().getPaoId(), gears);
        }
        model.addAttribute("gearsByProgramId", gearsByProgramId);
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
}

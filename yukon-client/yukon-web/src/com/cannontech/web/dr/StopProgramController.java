package com.cannontech.web.dr;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Maps;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/program/stop/*")
public class StopProgramController extends ProgramControllerBase {
	
    @Autowired private NestService nestService;
    private static class StopProgramValidator extends SimpleValidator<StopProgramBackingBeanBase> {
        
        public StopProgramValidator() {
            super(StopProgramBackingBeanBase.class);
        }

        @Override
        protected void doValidation(StopProgramBackingBeanBase stopProgram,
                Errors errors) {
            if (!stopProgram.isStopNow()) {
                ValidationUtils.rejectIfEmpty(errors, "stopDate", "required");
                Date now = new Date();
                if (now.after(stopProgram.getStopDate())) {
                    errors.reject("stopTimeBeforeNow");
                }
            }
        }
    }
    private final StopProgramValidator validator = new StopProgramValidator();

    @RequestMapping("details")
    public String details(ModelMap model, Boolean fromBack,
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
    	
        if (fromBack == null || !fromBack) {
            backingBean.setStopNow(true);
            backingBean.setGearNumber(1);
            backingBean.setStopDate(new Date());
        }

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        model.addAttribute("program", program);
        
        addConstraintsInfoToModel(model, fromBack, userContext, backingBean);
        boolean stopGearAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                                                userContext.getYukonUser());
        model.addAttribute("stopGearAllowed", stopGearAllowed);
        if (stopGearAllowed) {
            addGearsToModel(program, model);
        }
        
        addErrorsToFlashScopeIfNecessary(bindingResult, flashScope);

        return "dr/program/stopProgramDetails.jsp";
    }

    @RequestMapping("constraints")
    public String constraints(ModelMap model,
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
    	
        validate(validator, model, backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            return details(model, true, backingBean, bindingResult, userContext, flashScope);
        }

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     program,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        model.addAttribute("program", program);

        assertStopGearAllowed(userContext);

        ConstraintViolations violations =
            programService.getConstraintViolationsForStopProgram(backingBean.getProgramId(),
                                                                 backingBean.getGearNumber(),
                                                                 backingBean.getStopDate());
        model.addAttribute("violations", violations);

        return "dr/program/stopProgramConstraints.jsp";
    }

    @RequestMapping("stop")
    public String stop(HttpServletResponse resp, ModelMap model, Boolean overrideConstraints,
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) throws IOException {
    	
        validate(validator, model, backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            return details(model, true, backingBean, bindingResult, userContext, flashScope);
        }

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser,
                                                     program,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        if(nestService.isNestProgram(backingBean.getProgramId())) {
            String error = nestService.stopControl(backingBean.getProgramId());
            System.out.println("----------------------" + error);
        }

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
                                                                  new Date());
        } else {
            programService.stopProgram(backingBean.getProgramId(),
                                               stopDate, null);
            demandResponseEventLogService.threeTierProgramStopScheduled(yukonUser,
                                                                        program.getName(),
                                                                        stopDate);
        }
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopProgram.programStopRequested"));

        resp.setContentType("application/json");
        resp.getWriter().write(JsonUtils.toJson(Collections.singletonMap("action", "reload")));
        return null;
    }

    @RequestMapping("multipleDetails")
    public String multipleDetails(ModelMap model, Boolean fromBack,
            @ModelAttribute("backingBean") StopMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
        
        UiFilter<DisplayablePao> filter = null;
        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        String paoName = null;
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

        SearchResults<DisplayablePao> searchResult =
            programService.filterPrograms(filter, new DisplayablePaoComparator(),
                                          0, Integer.MAX_VALUE, userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {
            model.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopMultiplePrograms.noPrograms." +
                                                 (backingBean.getControlAreaId() != null ? "controlArea" : "scenario"),
                                                 paoName);
            model.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        model.addAttribute("programs", programs);

        if (fromBack == null || !fromBack) {
            backingBean.setStopNow(true);
            backingBean.setStopDate(new Date());
            List<ProgramStopInfo> programStopInfo = new ArrayList<>(programs.size());
            for (DisplayablePao program : programs) {
                int programId = program.getPaoIdentifier().getPaoId();
                int gearNumber = 1;
                if (scenarioPrograms != null) {
                    ScenarioProgram scenarioProgram = scenarioPrograms.get(programId);
                    if (scenarioProgram != null) {
                        gearNumber = scenarioProgram.getStartGear();
                    }
                }
                programStopInfo.add(new ProgramStopInfo(programId, gearNumber,true));
            }
            backingBean.setProgramStopInfo(programStopInfo);
        }
        
        addConstraintsInfoToModel(model, fromBack, userContext, backingBean);
        
        boolean stopGearAllowed =
                rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                              userContext.getYukonUser());
        model.addAttribute("stopGearAllowed",stopGearAllowed);
        
        addGearsToModel(searchResult.getResultList(), model);
        addErrorsToFlashScopeIfNecessary(bindingResult, flashScope);

        return "dr/program/stopMultipleProgramsDetails.jsp";
    }

    @RequestMapping("stopMultiple")
    public String stopMultiple(HttpServletResponse resp, ModelMap model, Boolean overrideConstraints,
            @ModelAttribute("backingBean") StopMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) throws IOException {
    	
        validate(validator, model, backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            return multipleDetails(model, true, backingBean, bindingResult, userContext, flashScope);
        }

        Map<Integer, ScenarioProgram> scenarioPrograms = null;
        if (backingBean.getControlAreaId() != null) {
            DisplayablePao controlArea = controlAreaService.getControlArea(backingBean.getControlAreaId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         controlArea,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("controlArea", controlArea);
            demandResponseEventLogService.threeTierControlAreaStopped(userContext.getYukonUser(),
                                                                      controlArea.getName());
        }
        if (backingBean.getScenarioId() != null) {
            DisplayablePao scenario = scenarioDao.getScenario(backingBean.getScenarioId());
            paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                         scenario,
                                                         Permission.LM_VISIBLE,
                                                         Permission.CONTROL_COMMAND);
            model.addAttribute("scenario", scenario);
            demandResponseEventLogService.threeTierScenarioStopped(userContext.getYukonUser(),
                                                                   scenario.getName());
            scenarioPrograms =
                scenarioDao.findScenarioProgramsForScenario(backingBean.getScenarioId());
        }

        Date stopDate = backingBean.getStopDate();
        if (backingBean.isStopNow()) {
            // If we're starting a scenario, we need a common base "now" time
            // for offsets.
            stopDate = new Date();
        }
        for (ProgramStopInfo programStopInfo : backingBean.getProgramStopInfo()) {
            if (programStopInfo.isStopProgram()) {
                Duration stopOffset = null;
                if (scenarioPrograms != null) {
                    ScenarioProgram scenarioProgram = scenarioPrograms.get(programStopInfo.getProgramId());
                    stopOffset = scenarioProgram.getStopOffset();
                }
    
                boolean stopGearAllowed =
                        rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                                      userContext.getYukonUser());
                if (nestService.isNestProgram(programStopInfo.getProgramId())) {
                    String error = nestService.stopControl(programStopInfo.getProgramId());
                    // Nest program returned an error, we are going to skip this program
                    // Do we need to write this to some event log? If so which one?
                    if(error != null) {
                        continue;
                    }
                }
                
            
                if (backingBean.isStopNow() && stopOffset == null) {
                    programService.stopProgram(programStopInfo.getProgramId());
                } else if (stopGearAllowed && programStopInfo.isUseStopGear()) {
                    programService.stopProgramWithGear(programStopInfo.getProgramId(), 
                                                       programStopInfo.getGearNumber(), 
                                                       stopDate, 
                                                       programStopInfo.isOverrideConstraints());
                    
                    DisplayablePao program = programService.getProgram(programStopInfo.getProgramId());
                    LiteYukonUser yukonUser = userContext.getYukonUser();
                    demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());

                } else {
                    programService.stopProgram(programStopInfo.getProgramId(),
                                                       stopDate, stopOffset);
                }
            }
        }
        
        if(backingBean.getControlAreaId() != null){
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopMultiplePrograms.controlAreaStopRequested"));
        }
        if(backingBean.getScenarioId() != null){
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopMultiplePrograms.scenarioStopRequested"));
        }

        resp.setContentType("application/json");
        resp.getWriter().write(JsonUtils.toJson(Collections.singletonMap("action", "reload")));
        return null;
    }

    @RequestMapping("stopMultipleConstraints")
    public String stopMultipleConstraints(HttpServletResponse resp, ModelMap model,
            @ModelAttribute("backingBean") StopMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) throws IOException {

        LiteYukonUser user = userContext.getYukonUser();

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
        }

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);
        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);

        if (autoObserveConstraintsAllowed && (!checkConstraintsAllowed || backingBean.isAutoObserveConstraints())) {
            return stopMultiple(resp, model, checkConstraintsAllowed, backingBean, bindingResult, userContext, flashScope);
        }

        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);
        model.addAttribute("overrideAllowed", overrideAllowed);

        Map<Integer, ConstraintViolations> violationsByProgramId = Maps.newHashMap();
        Map<Integer, DisplayablePao> programsByProgramId = Maps.newHashMap();
        boolean constraintsViolated = false;
        int numProgramsToStop = 0;
        for (ProgramStopInfo programStopInfo : backingBean.getProgramStopInfo()) {
            if (!programStopInfo.isStopProgram()) {
                continue;
            }

            numProgramsToStop++;
            int programId = programStopInfo.getProgramId();
            DisplayablePao program = programService.getProgram(programId);
            programsByProgramId.put(programId, program);

            if (programStopInfo.isUseStopGear()) {
                ConstraintViolations violations =
                    programService.getConstraintViolationsForStopProgram(programId,
                                                                         programStopInfo.getGearNumber(),
                                                                         backingBean.getStopDate());
                if (violations != null && violations.isViolated() ) {
                    violationsByProgramId.put(programId, violations);
                    constraintsViolated = true;
                }
            }
        }

        if (numProgramsToStop == 0) {
            bindingResult.reject("noProgramsSelected");
            return multipleDetails(model, true, backingBean, bindingResult, userContext, flashScope);
        }

        model.addAttribute("numProgramsToStop", numProgramsToStop);
        model.addAttribute("programsByProgramId", programsByProgramId);
        model.addAttribute("violationsByProgramId", violationsByProgramId);
        model.addAttribute("constraintsViolated", constraintsViolated);

        return "dr/program/stopMultipleProgramsConstraints.jsp";
    }
    
    private void addConstraintsInfoToModel(ModelMap model, Boolean fromBack,
                                           YukonUserContext userContext, StopProgramBackingBeanBase backingBean) {
        LiteYukonUser user = userContext.getYukonUser();
        boolean autoObserveConstraintsAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);
        boolean checkConstraintsAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);

        model.addAttribute("autoObserveConstraintsAllowed", autoObserveConstraintsAllowed);
        model.addAttribute("checkConstraintsAllowed", checkConstraintsAllowed);

        if (checkConstraintsAllowed && autoObserveConstraintsAllowed) {
            // It might be more sane to change the "DEFAULT_CONSTRAINT_SELECTION"
            // role property to something more like "AUTO_OBSERVE_CONSTRAINTS_BY_DEFAULT".
            String defaultConstraint = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_CONSTRAINT_SELECTION, user);
            if (fromBack == null || !fromBack) {
                backingBean.setAutoObserveConstraints(defaultConstraint.equalsIgnoreCase(LMManualControlRequest.CONSTRAINT_FLAG_STRS[LMManualControlRequest.CONSTRAINTS_FLAG_USE]));
            }
        }
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programsHelper.initBinder(binder, userContext, "program.stopProgram");
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
}

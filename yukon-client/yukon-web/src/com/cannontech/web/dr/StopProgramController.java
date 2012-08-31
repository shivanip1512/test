package com.cannontech.web.dr;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
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
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
@RequestMapping("/program/stop/*")
public class StopProgramController extends ProgramControllerBase {
	
    private static class StopProgramValidator
        extends SimpleValidator<StopProgramBackingBeanBase> {

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
    private StopProgramValidator validator = new StopProgramValidator();

    @RequestMapping
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
        boolean stopGearAllowed = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                                                userContext.getYukonUser());
        model.addAttribute("stopGearAllowed", stopGearAllowed);
        if (stopGearAllowed) {
            addGearsToModel(program, model);
        }
        
        addErrorsToFlashScopeIfNecessary(bindingResult, flashScope);

        return "dr/program/stopProgramDetails.jsp";
    }

    @RequestMapping
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

    @RequestMapping
    public String stop(ModelMap model, Boolean overrideConstraints,
            @ModelAttribute("backingBean") StopProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
    	
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
            programService.scheduleProgramStop(backingBean.getProgramId(),
                                               stopDate, null);
            demandResponseEventLogService.threeTierProgramStopScheduled(yukonUser,
                                                                        program.getName(),
                                                                        stopDate);
        }
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopProgram.programStopRequested"));
        
        return closeDialog(model);
    }

    @RequestMapping
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

        SearchResult<DisplayablePao> searchResult =
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
            List<ProgramStopInfo> programStopInfo = new ArrayList<ProgramStopInfo>(programs.size());
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
        
        boolean stopGearAllowed =
                rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                              userContext.getYukonUser());
        model.addAttribute("stopGearAllowed",stopGearAllowed);
        
        addGearsToModel(searchResult.getResultList(), model);
        addErrorsToFlashScopeIfNecessary(bindingResult, flashScope);

        return "dr/program/stopMultipleProgramsDetails.jsp";
    }

    @RequestMapping
    public String stopMultiple(ModelMap model, Boolean overrideConstraints,
            @ModelAttribute("backingBean") StopMultipleProgramsBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
    	
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
    
                if (backingBean.isStopNow() && stopOffset == null) {
                    programService.stopProgram(programStopInfo.getProgramId());
                } else {
                    programService.scheduleProgramStop(programStopInfo.getProgramId(),
                                                       stopDate, stopOffset);
                }
                boolean stopGearAllowed =
                        rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,
                                                      userContext.getYukonUser());
                
                if (stopGearAllowed && programStopInfo.isUseStopGear()) {
                    DisplayablePao program = programService.getProgram(programStopInfo.getProgramId());
        
                    LiteYukonUser yukonUser = userContext.getYukonUser();
                    programService.changeGear(programStopInfo.getProgramId(), programStopInfo.getGearNumber());
                    demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());
    
                }
            }
        }
        
        if(backingBean.getControlAreaId() != null){
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopMultiplePrograms.controlAreaStopRequested"));
        }
        if(backingBean.getScenarioId() != null){
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.stopMultiplePrograms.scenarioStopRequested"));
        }
        
        return closeDialog(model);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext, "program.stopProgram");
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

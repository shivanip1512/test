package com.cannontech.web.dr;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.google.common.collect.Maps;

public class ProgramControllerBase {
    protected ControlAreaService controlAreaService = null;
    protected ScenarioDao scenarioDao = null;
    protected ProgramService programService = null;
    protected PaoAuthorizationService paoAuthorizationService;
    protected ProgramControllerHelper programControllerHelper;
    protected RolePropertyDao rolePropertyDao;
    protected DemandResponseEventLogService demandResponseEventLogService;

    protected void addGearsToModel(DisplayablePao program, ModelMap modelMap) {
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
    
    protected String closeDialog(ModelMap model) {
        model.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
    }

    protected static void validate(Validator validator,
            ModelMap model, Object backingBean, BindingResult bindingResult) {
        validator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> errors =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            model.addAttribute("errors", errors);
        }
    }

    protected void addErrorsToFlashScopeIfNecessary(
            BindingResult bindingResult, FlashScope flashScope) {
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
    }
    
    protected void addGearsToModel(List<DisplayablePao> programs, ModelMap model) {
        Map<Integer, List<LMProgramDirectGear>> gearsByProgramId = Maps.newHashMap();
        Map<Integer, LMProgramDirectGear> currentGearByProgramId = Maps.newHashMap();
        LMProgramDirectGear currentGear = null;
        for (DisplayablePao program : programs) {
            List<LMProgramDirectGear> gears = Collections.emptyList();
            LMProgramBase programBase = programService.getProgramForPao(program);
            if (programBase instanceof IGearProgram) {
                gears = ((IGearProgram) programBase).getDirectGearVector();
                currentGear = ((IGearProgram) programBase).getCurrentGear();
            }
            currentGearByProgramId.put(program.getPaoIdentifier().getPaoId(), currentGear);
            gearsByProgramId.put(program.getPaoIdentifier().getPaoId(), gears);
        }
        model.addAttribute("gearsByProgramId", gearsByProgramId);
        model.addAttribute("currentGearByProgramId", currentGearByProgramId);

    }
    
    protected Map<Integer, Map<Integer, Boolean>> getIndexBasedIsTargetGearMap(List<DisplayablePao> programs) {
        Map<Integer, Map<Integer, Boolean>> programIndexTargetGearMap = new HashMap<Integer, Map<Integer, Boolean>>();
        for (int i = 0; i < programs.size(); i++){
            DisplayablePao program = programs.get(i);
            LMProgramBase programBase = programService.getProgramForPao(program);
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setDemandResponseEventLogService(DemandResponseEventLogService demandResponseEventLogService) {
        this.demandResponseEventLogService = demandResponseEventLogService;
    }
}

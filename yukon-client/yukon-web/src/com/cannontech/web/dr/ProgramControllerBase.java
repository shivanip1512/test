package com.cannontech.web.dr;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;

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

    protected <T> void validate(SimpleValidator<? super T> validator,
            ModelMap model, T backingBean, BindingResult bindingResult) {
        validator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> errors =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            model.addAttribute("errors", errors);
        }
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

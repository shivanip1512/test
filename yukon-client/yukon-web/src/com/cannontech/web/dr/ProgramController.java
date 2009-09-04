package com.cannontech.web.dr;


import java.util.List;

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
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.user.YukonUserContext;

@Controller
public class ProgramController {
    private ControlAreaService controlAreaService = null;
    private ScenarioDao scenarioDao = null;
    private ProgramService programService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;
    private LoadGroupControllerHelper loadGroupControllerHelper;    

    @RequestMapping("/program/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("filter") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {

        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, null);

        return "dr/program/list.jsp";
    }

    @RequestMapping("/program/detail")
    public String detail(int programId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("filter") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        DisplayablePao program = programService.getProgram(programId);
        if (false && !paoAuthorizationService.isAuthorized(userContext.getYukonUser(),
                                                  Permission.LM_VISIBLE, program)) {
             throw new NotAuthorizedException("Program " + programId
                                              + " is not visible to user.");
        }
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

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext);
    }

    @InitBinder
    public void initLoadGroupBinder(WebDataBinder binder, YukonUserContext userContext) {
        loadGroupControllerHelper.initBinder(binder, userContext);
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
}

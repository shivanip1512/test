package com.cannontech.web.dr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.dr.scenario.dao.ScenarioDao;

@Controller
public class ScenarioController {
    private ScenarioDao scenarioDao = null;
    private ProgramDao programDao = null;
    private PaoAuthorizationService paoAuthorizationService;

    @RequestMapping("/scenario/list")
    public String list(ModelMap modelMap) {
        List<DisplayablePao> scenarios = scenarioDao.getScenarios();
        modelMap.addAttribute("scenarios", scenarios);
        return "/dr/scenario/list.jsp";
    }

    @RequestMapping("/scenario/detail")
    public String detail(int scenarioId, ModelMap modelMap) {
        DisplayablePao scenario = scenarioDao.getScenario(scenarioId);
        // TODO:  check permissions of scenario
        modelMap.addAttribute("scenario", scenario);
        List<DisplayablePao> programs = programDao.getProgramsForScenario(scenarioId);
        modelMap.addAttribute("programs", programs);
        return "/dr/scenario/detail.jsp";
    }

    @Autowired
    public void setScenarioDao(ScenarioDao scenarioDao) {
        this.scenarioDao = scenarioDao;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
}

package com.cannontech.web.dr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.dr.scenario.dao.ScenarioDao;

@Controller
public class ProgramController {
    private ControlAreaDao controlAreaDao = null;
    private ScenarioDao scenarioDao = null;
    private ProgramDao programDao = null;
    private LoadGroupDao loadGroupDao = null;
    private PaoAuthorizationService paoAuthorizationService;

    @RequestMapping("/program/list")
    public String list(ModelMap modelMap) {
        List<DisplayableDevice> programs = programDao.getPrograms();
        modelMap.addAttribute("programs", programs);
        return "/dr/program/list.jsp";
    }

    @RequestMapping("/program/detail")
    public String detail(int programId, ModelMap modelMap) {
        DisplayableDevice program = programDao.getProgram(programId);
        // TODO:  check permissions of program
        modelMap.addAttribute("program", program);
        List<DisplayableDevice> loadGroups = loadGroupDao.getLoadGroupsForProgram(programId);
        modelMap.addAttribute("loadGroups", loadGroups);
        DisplayableDevice parentControlArea = controlAreaDao.getControlAreaForProgram(programId);
        modelMap.addAttribute("parentControlArea", parentControlArea);
        List<DisplayableDevice> parentScenarios = scenarioDao.getScenariosForProgram(programId);
        modelMap.addAttribute("parentScenarios", parentScenarios);
        return "/dr/program/detail.jsp";
    }

    @Autowired
    public void setControlAreaDao(ControlAreaDao controlAreaDao) {
        this.controlAreaDao = controlAreaDao;
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
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
}

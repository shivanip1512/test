package com.cannontech.web.dr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.program.dao.ProgramDao;

@Controller
public class ControlAreaController {
    private ControlAreaDao controlAreaDao = null;
    private ProgramDao programDao = null;
    private PaoAuthorizationService paoAuthorizationService;

    @RequestMapping("/controlArea/list")
    public String list(ModelMap modelMap) {
        List<DisplayablePao> controlAreas = controlAreaDao.getControlAreas();
        modelMap.addAttribute("controlAreas", controlAreas);
        return "/dr/controlArea/list.jsp";
    }

    @RequestMapping("/controlArea/detail")
    public String detail(int controlAreaId, ModelMap modelMap) {
        DisplayablePao controlArea = controlAreaDao.getControlArea(controlAreaId);
        // TODO:  check permissions of control area
        modelMap.addAttribute("controlArea", controlArea);
        List<DisplayablePao> programs = programDao.getProgramsForControlArea(controlAreaId);
        modelMap.addAttribute("programs", programs);
        return "/dr/controlArea/detail.jsp";
    }

    @Autowired
    public void setControlAreaDao(ControlAreaDao controlAreaDao) {
        this.controlAreaDao = controlAreaDao;
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

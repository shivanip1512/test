package com.cannontech.web.dr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.program.dao.ProgramDao;

@Controller
public class LoadGroupController {
    private LoadGroupDao loadGroupDao = null;
    private ProgramDao programDao = null;
    private PaoAuthorizationService paoAuthorizationService;

    @RequestMapping("/loadGroup/list")
    public String list(ModelMap modelMap) {
        List<DisplayableDevice> loadGroups = loadGroupDao.getLoadGroups();
        modelMap.addAttribute("loadGroups", loadGroups);
        return "/dr/loadGroup/list.jsp";
    }

    @RequestMapping("/loadGroup/detail")
    public String detail(int loadGroupId, ModelMap modelMap) {
        DisplayableDevice loadGroup = loadGroupDao.getLoadGroup(loadGroupId);
        // TODO:  check permissions of control area
        modelMap.addAttribute("loadGroup", loadGroup);
        List<DisplayableDevice> parentPrograms = programDao.getProgramsForLoadGroup(loadGroupId);
        modelMap.addAttribute("parentPrograms", parentPrograms);
        return "/dr/loadGroup/detail.jsp";
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
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

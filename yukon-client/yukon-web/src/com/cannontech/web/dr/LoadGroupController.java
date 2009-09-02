package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.program.filter.ForLoadGroupFilter;
import com.cannontech.user.YukonUserContext;

@Controller
public class LoadGroupController {
    private LoadGroupDao loadGroupDao = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;

    @RequestMapping("/loadGroup/list")
    public String list(ModelMap modelMap) {
        List<DisplayablePao> loadGroups = loadGroupDao.getLoadGroups();
        modelMap.addAttribute("loadGroups", loadGroups);
        return "/dr/loadGroup/list.jsp";
    }

    @RequestMapping("/loadGroup/detail")
    public String detail(int loadGroupId, ModelMap modelMap,
            YukonUserContext userContext) {
        DisplayablePao loadGroup = loadGroupDao.getLoadGroup(loadGroupId);
        // TODO:  check permissions of control area
        modelMap.addAttribute("loadGroup", loadGroup);
        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        filters.add(new ForLoadGroupFilter(loadGroupId));
        List<DisplayablePao> parentPrograms =
            programControllerHelper.getFilteredPrograms(userContext, filters);
        modelMap.addAttribute("parentPrograms", parentPrograms);
        return "/dr/loadGroup/detail.jsp";
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
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
}

package com.cannontech.web.dr;

import java.util.ArrayList;
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
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.program.filter.ForLoadGroupFilter;
import com.cannontech.user.YukonUserContext;

@Controller
public class LoadGroupController {
    private LoadGroupService loadGroupService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private LoadGroupControllerHelper loadGroupControllerHelper;
    private ProgramControllerHelper programControllerHelper;

    @RequestMapping("/loadGroup/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("filter") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        
        loadGroupControllerHelper.filterGroups(modelMap, userContext, backingBean,
                                               result, status, null);
        return "/dr/loadGroup/list.jsp";
    }    

    @RequestMapping("/loadGroup/detail")
    public String detail(int loadGroupId, ModelMap modelMap,
            YukonUserContext userContext) {
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        // TODO:  check permissions of control area
        if (false && !paoAuthorizationService.isAuthorized(userContext.getYukonUser(),
                                                           Permission.LM_VISIBLE,
                                                           loadGroup)) {
            throw new NotAuthorizedException("LoadGroup " + loadGroupId + " is not visible to user.");
        }
        modelMap.addAttribute("loadGroup", loadGroup);
        
        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        filters.add(new ForLoadGroupFilter(loadGroupId));
        List<DisplayablePao> parentPrograms =
            programControllerHelper.getFilteredPrograms(userContext, filters);
        modelMap.addAttribute("parentPrograms", parentPrograms);
        return "/dr/loadGroup/detail.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        loadGroupControllerHelper.initBinder(binder, userContext);
    }
    
    @Autowired
    public void setLoadGroupService(LoadGroupService loadGroupService) {
        this.loadGroupService = loadGroupService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setLoadGroupControllerHelper(
            LoadGroupControllerHelper loadGroupControllerHelper) {
        this.loadGroupControllerHelper = loadGroupControllerHelper;
    }

    @Autowired
    public void setProgramControllerHelper(
            ProgramControllerHelper programControllerHelper) {
        this.programControllerHelper = programControllerHelper;
    }
}

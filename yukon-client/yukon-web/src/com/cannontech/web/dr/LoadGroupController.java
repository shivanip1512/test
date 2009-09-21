package com.cannontech.web.dr;


import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.user.YukonUserContext;

@Controller
public class LoadGroupController {
    private LoadGroupService loadGroupService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramService programService;
    private LoadGroupControllerHelper loadGroupControllerHelper;
    private final static Map<Integer, String> shedTimeOptions;
    static {
        // TODO:  make this immutable...can we update google collections so
        // we can use ImmutableSortedMap.Builder?
        shedTimeOptions = new TreeMap<Integer, String>();
        // TODO:  localize
        shedTimeOptions.put(60 * 5, "5 minutes");
        shedTimeOptions.put(60 * 7, "7 minutes");
        shedTimeOptions.put(60 * 10, "10 minutes");
        shedTimeOptions.put(60 * 15, "15 minutes");
        shedTimeOptions.put(60 * 20, "20 minutes");
        shedTimeOptions.put(60 * 30, "30 minutes");
        shedTimeOptions.put(60 * 45, "45 minutes");
        shedTimeOptions.put(60 * 60 * 1, "1 hour");
        shedTimeOptions.put(60 * 60 * 2, "2 hours");
        shedTimeOptions.put(60 * 60 * 3, "3 hours");
        shedTimeOptions.put(60 * 60 * 4, "4 hours");
        shedTimeOptions.put(60 * 60 * 6, "6 hours");
        shedTimeOptions.put(60 * 60 * 8, "8 hours");
    }

    @RequestMapping("/loadGroup/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult result, SessionStatus status) {

        loadGroupControllerHelper.filterGroups(modelMap, userContext, backingBean,
                                               result, status, null);
        return "dr/loadGroup/list.jsp";
    }    

    @RequestMapping("/loadGroup/detail")
    public String detail(int loadGroupId, ModelMap modelMap,
            YukonUserContext userContext) {
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        if (false && !paoAuthorizationService.isAuthorized(userContext.getYukonUser(),
                                                           Permission.LM_VISIBLE,
                                                           loadGroup)) {
            throw new NotAuthorizedException("LoadGroup " + loadGroupId + " is not visible to user.");
        }

        modelMap.addAttribute("loadGroup", loadGroup);
        modelMap.addAttribute("parentPrograms",
                              programService.findProgramsForLoadGroup(userContext,
                                                                      loadGroupId));

        return "dr/loadGroup/detail.jsp";
    }

    @RequestMapping("/loadGroup/sendShedConfirm")
    public String sendShedConfirm(ModelMap modelMap, int loadGroupId,
            YukonUserContext userContext) {
        // TODO:  check permissions

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        modelMap.addAttribute("loadGroup", loadGroup);
        modelMap.addAttribute("shedTimeOptions", shedTimeOptions);

        return "dr/loadGroup/sendShedConfirm.jsp";
    }

    @RequestMapping("/loadGroup/sendShed")
    public String sendShed(ModelMap modelMap, int loadGroupId,
            int durationInSeconds, YukonUserContext userContext) {
        // TODO:  check permissions
        loadGroupService.sendShed(loadGroupId, durationInSeconds);
        modelMap.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
    }

    @RequestMapping("/loadGroup/sendRestore")
    public void sendRestore(int loadGroupId, YukonUserContext userContext) {
        // TODO:  check permissions
        loadGroupService.sendRestore(loadGroupId);
    }

    @RequestMapping("/loadGroup/setEnabled")
    public void setEnabled(int loadGroupId, boolean isEnabled,
            YukonUserContext userContext) {
        // TODO:  check permissions
        loadGroupService.setEnabled(loadGroupId, isEnabled);
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
    public void setProgramService(
            ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setLoadGroupControllerHelper(
            LoadGroupControllerHelper loadGroupControllerHelper) {
        this.loadGroupControllerHelper = loadGroupControllerHelper;
    }
}

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

import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.user.YukonUserContext;

@Controller
public class LoadGroupController {
    private LoadGroupService loadGroupService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramService programService;
    private LoadGroupControllerHelper loadGroupControllerHelper;
    private DemandResponseEventLogService demandResponseEventLogService;
    private FavoritesDao favoritesDao;

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
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE);

        favoritesDao.detailPageViewed(loadGroupId);
        modelMap.addAttribute("loadGroup", loadGroup);
        boolean isFavorite =
            favoritesDao.isFavorite(loadGroupId, userContext.getYukonUser());
        modelMap.addAttribute("isFavorite", isFavorite);
        modelMap.addAttribute("parentPrograms",
                              programService.findProgramsForLoadGroup(loadGroupId, userContext));

        return "dr/loadGroup/detail.jsp";
    }

    @RequestMapping("/loadGroup/sendShedConfirm")
    public String sendShedConfirm(ModelMap modelMap, int loadGroupId,
            YukonUserContext userContext) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        modelMap.addAttribute("loadGroup", loadGroup);
        modelMap.addAttribute("shedTimeOptions", shedTimeOptions);
        return "dr/loadGroup/sendShedConfirm.jsp";
    }

    @RequestMapping("/loadGroup/sendShed")
    public String sendShed(ModelMap modelMap, int loadGroupId,
            int durationInSeconds, YukonUserContext userContext) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        loadGroupService.sendShed(loadGroupId, durationInSeconds);
        
        demandResponseEventLogService.threeTierLoadGroupShed(yukonUser, 
                                                             loadGroup.getName(), 
                                                             durationInSeconds);
        
        modelMap.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
    }
    
    @RequestMapping("/loadGroup/sendRestoreConfirm")
    public String sendRestoreConfirm(ModelMap modelMap, int loadGroupId,
            YukonUserContext userContext) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        modelMap.addAttribute("loadGroup", loadGroup);
        return "dr/loadGroup/sendRestoreConfirm.jsp";
    }

    @RequestMapping("/loadGroup/sendRestore")
    public String sendRestore(ModelMap modelMap, int loadGroupId, YukonUserContext userContext) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        loadGroupService.sendRestore(loadGroupId);
        
        demandResponseEventLogService.threeTierLoadGroupRestore(yukonUser, loadGroup.getName());
        
        modelMap.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
    }

    @RequestMapping("/loadGroup/sendEnableConfirm")
    public String sendEnableConfirm(ModelMap modelMap, int loadGroupId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("loadGroup", loadGroup);
        modelMap.addAttribute("isEnabled", isEnabled);
        return "dr/loadGroup/sendEnableConfirm.jsp";
    }
    
    @RequestMapping("/loadGroup/setEnabled")
    public String setEnabled(ModelMap modelMap, int loadGroupId, boolean isEnabled,
            YukonUserContext userContext) {

        DisplayablePao loadGroup = loadGroupService.getLoadGroup(loadGroupId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     loadGroup, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        loadGroupService.setEnabled(loadGroupId, isEnabled);

        if (isEnabled) {
            demandResponseEventLogService.threeTierLoadGroupEnabled(yukonUser, loadGroup.getName());
        } else {
            demandResponseEventLogService.threeTierLoadGroupDisabled(yukonUser, loadGroup.getName());
        }

        modelMap.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
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
    
    @Autowired
    public void setDemandResponseEventLogService(DemandResponseEventLogService demandResponseEventLogService) {
        this.demandResponseEventLogService = demandResponseEventLogService;
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }
}

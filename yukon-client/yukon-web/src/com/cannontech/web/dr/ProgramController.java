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
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.favorites.dao.FavoritesDao;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/program/*")
public class ProgramController extends ProgramControllerBase {
    private LoadGroupControllerHelper loadGroupControllerHelper;
    private FavoritesDao favoritesDao;

    @RequestMapping
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status, FlashScope flashScope) {

        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, null);
        
        addErrorsToFlashScopeIfNecessary(result, flashScope);

        return "dr/program/list.jsp";
    }

    @RequestMapping
    public String detail(int programId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult result, SessionStatus status, FlashScope flashScope) {
        
        ControllablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE);

        favoritesDao.detailPageViewed(programId);
        modelMap.addAttribute("program", program);
        boolean isFavorite =
            favoritesDao.isFavorite(programId, userContext.getYukonUser());
        modelMap.addAttribute("isFavorite", isFavorite);

        UiFilter<ControllablePao> detailFilter = new LoadGroupsForProgramFilter(programId);
        loadGroupControllerHelper.filterGroups(modelMap, userContext, backingBean,
                                               result, status, detailFilter);

        ControllablePao parentControlArea =
            controlAreaService.findControlAreaForProgram(userContext, programId);
        modelMap.addAttribute("parentControlArea", parentControlArea);
        List<Scenario> parentScenarios = scenarioDao.findScenariosForProgram(programId);
        modelMap.addAttribute("parentScenarios", parentScenarios);
        
        addErrorsToFlashScopeIfNecessary(result, flashScope);
        
        return "dr/program/detail.jsp";
    }

    @RequestMapping
    public String getChangeGearValue(ModelMap modelMap, int programId, YukonUserContext userContext) {
        
        ControllablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);

        addGearsToModel(program, modelMap);
        modelMap.addAttribute("program", program);

        return "dr/program/getChangeGearValue.jsp";
    }
    
    @RequestMapping
    public String changeGear(ModelMap modelMap, int programId, int gearNumber, 
                             YukonUserContext userContext, FlashScope flashScope) {
        
        ControllablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.changeGear(programId, gearNumber);
        
        demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, program.getName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.getChangeGearValue.gearChanged"));
        return closeDialog(modelMap);
    }
    
    @RequestMapping
    public String sendEnableConfirm(ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext) {
        
        ControllablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("program", program);
        modelMap.addAttribute("isEnabled", isEnabled);
        return "dr/program/sendEnableConfirm.jsp";
    }
    
    @RequestMapping
    public String setEnabled(ModelMap modelMap, int programId, boolean isEnabled,
            YukonUserContext userContext, FlashScope flashScope) {
        
        ControllablePao program = programService.getProgram(programId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser, 
                                                     program, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        programService.setEnabled(programId, isEnabled);

        if(isEnabled) {
            demandResponseEventLogService.threeTierProgramEnabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.enabled"));
        } else {
            demandResponseEventLogService.threeTierProgramDisabled(yukonUser, program.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableConfirm.disabled"));
        }
        
        return closeDialog(modelMap);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext);
        loadGroupControllerHelper.initBinder(binder, userContext);
    }

    @Autowired
    public void setLoadGroupControllerHelper(
            LoadGroupControllerHelper loadGroupControllerHelper) {
        this.loadGroupControllerHelper = loadGroupControllerHelper;
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }
}

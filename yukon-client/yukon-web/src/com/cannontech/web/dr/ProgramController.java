package com.cannontech.web.dr;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.program.model.ProgramState;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dr.LoadGroupControllerHelper.LoadGroupListBackingBean;
import com.cannontech.web.dr.ProgramControllerHelper.ProgramListBackingBean;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/program/*")
public class ProgramController extends ProgramControllerBase {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ScenarioService scenarioService;
    @Autowired private LoadGroupControllerHelper loadGroupControllerHelper;
    @Autowired private FavoritesDao favoritesDao;

    @RequestMapping
    public String list(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramListBackingBean backingBean,
            BindingResult bindingResult, FlashScope flashScope) {
        programControllerHelper.filterPrograms(model, userContext, backingBean,
                                               bindingResult, null);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/program/list.jsp";
    }

    @RequestMapping
    public String detail(int programId, ModelMap model,
            @ModelAttribute("backingBean") LoadGroupListBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE);

        favoritesDao.detailPageViewed(programId);
        model.addAttribute("program", program);
        boolean isFavorite =
            favoritesDao.isFavorite(programId, userContext.getYukonUser());
        model.addAttribute("isFavorite", isFavorite);

        UiFilter<DisplayablePao> detailFilter = new LoadGroupsForProgramFilter(programId);
        loadGroupControllerHelper.filterGroups(model, userContext, backingBean,
                                               bindingResult, detailFilter, flashScope);

        DisplayablePao parentControlArea =
            controlAreaService.findControlAreaForProgram(userContext, programId);
        model.addAttribute("parentControlArea", parentControlArea);
        List<Scenario> parentScenarios = scenarioDao.findScenariosForProgram(programId);
        model.addAttribute("parentScenarios", parentScenarios);

        return "dr/program/detail.jsp";
    }

    @RequestMapping
    public String getChangeGearValue(ModelMap modelMap, int programId, YukonUserContext userContext) {
        
        DisplayablePao program = programService.getProgram(programId);
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
        
        DisplayablePao program = programService.getProgram(programId);
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
        
        DisplayablePao program = programService.getProgram(programId);
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
        
        DisplayablePao program = programService.getProgram(programId);
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
    
    @RequestMapping
    public String sendEnableDisableProgramsConfirm(ModelMap modelMap, YukonUserContext userContext,
                                             Integer controlAreaId, Integer scenarioId, boolean enable) {
        
        modelMap.addAttribute("enable", enable);
        
        //Determine if parent object is a control area or scenario
        DisplayablePao parent = null;
        UiFilter<DisplayablePao> filter = null;
        if(controlAreaId != null) {
            parent = controlAreaService.getControlArea(controlAreaId);
            filter = new ForControlAreaFilter(controlAreaId);
        } else if(scenarioId != null){
            parent = scenarioService.getScenario(scenarioId);
            filter = new ForScenarioFilter(scenarioId);
        } else {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.invalidParent");
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("parent", parent);
        
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     parent,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);
        //Get programs
        SearchResult<DisplayablePao> searchResult = programService.filterPrograms(filter, 
                                                                                  new DisplayablePaoComparator(),
                                                                                  0, 
                                                                                  Integer.MAX_VALUE, 
                                                                                  userContext);
        List<DisplayablePao> programs = searchResult.getResultList();
        if (programs == null || programs.size() == 0) {
            modelMap.addAttribute("popupId", "drDialog");
            YukonMessageSourceResolvable error =
                new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.noPrograms." +
                                                (controlAreaId != null ? "controlArea" : "scenario"));
            modelMap.addAttribute("userMessage", error);
            return "common/userMessage.jsp";
        }
        modelMap.addAttribute("programs", programs);
        
        //get current gears and statuses
        List<String> gears = Lists.newArrayListWithCapacity(programs.size());
        List<ProgramState> states = Lists.newArrayListWithCapacity(programs.size());
        for(DisplayablePao program : programs) {
            DatedObject<LMProgramBase> datedObject = programService.findDatedProgram(program.getPaoIdentifier().getPaoId());
            LMProgramBase programBase = datedObject.getObject();
            LMProgramDirectGear gear = null;
            
            ProgramState state = programBase.getProgramState();
            states.add(state);
            
            if (programBase instanceof IGearProgram) {
                gear = ((IGearProgram) programBase).getCurrentGear();
            }
            if (gear != null) {
                gears.add(gear.getGearName());
            } else {
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String dashes = messageSourceAccessor.getMessage("");
                gears.add(dashes);
            }
        }
        modelMap.addAttribute("states", states);
        modelMap.addAttribute("gears", gears);
        
        return "dr/program/sendEnableDisableProgramsConfirm.jsp";
    }
    
    @RequestMapping
    public String enableDisablePrograms(ModelMap modelMap, HttpServletRequest request, FlashScope flashScope,
                      Boolean supressRestoration, boolean enable) {
        
        String[] programIds = request.getParameterValues("disableProgram");
        
        if(programIds == null) {
            YukonMessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.noProgramsSelected");
            flashScope.setError(message);
            return closeDialog(modelMap);
        }
        
        for(String programIdString : programIds) {
            int programId = Integer.parseInt(programIdString);
            
            if(!enable && (supressRestoration != null && supressRestoration)) {
                programService.disableAndSupressRestoration(programId);
            } else {
                programService.setEnabled(programId, enable);
            }
        }
        
        YukonMessageSourceResolvable message;
        if(enable) {
            message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendEnableProgramsConfirm.programsEnabledConfirmation", programIds.length);
        } else {
            message = new YukonMessageSourceResolvable("yukon.web.modules.dr.program.sendDisableProgramsConfirm.programsDisabledConfirmation", programIds.length);
        }
        flashScope.setConfirm(message);
        
        return closeDialog(modelMap);
    }
    
    private void addFilterErrorsToFlashScopeIfNecessary(ModelMap model,
            BindingResult bindingResult, FlashScope flashScope) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hasFilterErrors", true);
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext, "programList");
        loadGroupControllerHelper.initBinder(binder, userContext);
    }
}

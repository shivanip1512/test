package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CapControlStrategy.DayOfWeek;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.database.db.capcontrol.VoltViolationType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.service.StrategyService;
import com.cannontech.web.capcontrol.validators.StrategyValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class StrategyController {
    
    private static final String baseKey = "yukon.web.modules.capcontrol.strategies";
    
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private StrategyService strategyService;
    @Autowired private StrategyDao strategyDao;
    @Autowired private StrategyValidator validator;
    
    @RequestMapping("strategies")
    public String strategies(ModelMap model) {
        
        List<CapControlStrategy> strategies = strategyDao.getAllStrategies();
        
        model.addAttribute("strategies", strategies);
        
        model.put("settingArgs", StrategyPeakSettingsHelper.getDisplayArguments());

        return "strategy/strategies.jsp";
    }
    
    @RequestMapping(value="strategies/{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        
        CapControlStrategy strategy = strategyDao.getForId(id); 
        
        model.addAttribute("mode", PageEditMode.VIEW);
        
        return setUpModel(model, strategy);
    }
    
    @RequestMapping(value="strategies/{id}/edit", method=RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id) {
        
        CapControlStrategy strategy = strategyDao.getForId(id); 
        
        model.addAttribute("mode", PageEditMode.EDIT);
        
        return setUpModel(model, strategy);
    }
    
    @RequestMapping("strategies/create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model) {
        
        CapControlStrategy strategy = new CapControlStrategy();
        model.addAttribute("mode",  PageEditMode.CREATE);
        
        return setUpModel(model, strategy);
    }
    
    private String setUpModel(ModelMap model, CapControlStrategy strategy) {
        
        Object modelStrategy = model.get("strategy");
        if (modelStrategy instanceof CapControlStrategy) {
            strategy = (CapControlStrategy) modelStrategy;
        }
        
        model.addAttribute("strategy", strategy);
        
        model.addAttribute("ControlMethods",  ControlMethod.values());
        model.addAttribute("ControlAlgorithms",  ControlAlgorithm.values());
        model.addAttribute("EndDaySettings",  CapControlStrategy.EndDaySetting.values());
        model.addAttribute("VoltViolationTypes",  VoltViolationType.values());
        model.addAttribute("TargetSettingTypes",  TargetSettingType.values());
        model.addAttribute("algorithmToSettings",  StrategyPeakSettingsHelper.getAlgorithmToSettings());
        model.addAttribute("methodToAlgorithms",  ControlMethod.getMethodToAlgorithms());
        model.addAttribute("DaysOfWeek",  CapControlStrategy.DayOfWeek.values());
        
        return "strategy/strategy.jsp";
    }
    
    @RequestMapping(value={"strategies"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("strategy") CapControlStrategy strategy,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        /*
         * https://jira.spring.io/browse/SPR-9606 
         * Spring does not bind false values correctly as values of a Map.
         * Here we take all null entries and set them to false.
         */
        
        Map<DayOfWeek, Boolean> peakDays = strategy.getPeakDays();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (peakDays.get(day) == null) {
                peakDays.put(day, false);
            }
        }
        
        validator.validate(strategy, result);
        
        if (result.hasErrors()) {
            return bindAndForward(strategy, result, redirectAttributes);
        }
        
        int id;
        try {
            id = strategyService.save(strategy);
        } catch (Exception e) {
            //Something happened to make the config invalid since validation.
            result.rejectValue("name", "Something Broke");
            
            return bindAndForward(strategy, result, redirectAttributes);
        }
        
        return "redirect:strategies/" + id;
    }
    
    private String bindAndForward(CapControlStrategy strategy, BindingResult result, RedirectAttributes attrs) {
        
        attrs.addFlashAttribute("strategy", strategy);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.strategy", result);
        
        if (strategy.getId() == null) {
            return "redirect:strategies/create";
        }
        
        return "redirect:strategies/" + strategy.getId() + "/edit";
    }
    

    @RequestMapping(value="strategies/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(@PathVariable int id, FlashScope flash) {

        String name = strategyDao.getForId(id).getName();
        List<String> paosUsingStrategy = strategyDao.getAllPaoNamesUsingStrategyAssignment(id);
        if (paosUsingStrategy.isEmpty()) {
            strategyService.delete(id);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".deleteSuccess", name));
        } else {
            List<Object> params = new ArrayList<>();
            params.add(name);
            params.add(paosUsingStrategy.size());
            params.addAll(paosUsingStrategy);

            flash.setError(new YukonMessageSourceResolvable(baseKey + ".deleteFailed", 
                                                            params.toArray()));
        }

        return "redirect:strategies";
    }
    
    /*
     * This is used by the faces editor (cbcBase.jsp).
     * Get rid of this endpoint as soon as that can be removed.
     */
    @Deprecated
    @RequestMapping(value="/strategy/deleteStrategy")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String deleteStrategy(int strategyId, FlashScope flash) {
        
        String name = strategyDao.getForId(strategyId).getName();
        List<String> otherPaosUsingStrategy = strategyService.getAllPaoNamesUsingStrategyAssignment(strategyId);
        if (otherPaosUsingStrategy.isEmpty()) {
            strategyService.delete(strategyId);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".deleteSuccess", name));
        } else {
            List<Object> params = new ArrayList<>();
            params.add(name);
            params.add(otherPaosUsingStrategy.size());
            params.addAll(otherPaosUsingStrategy);

            flash.setError(new YukonMessageSourceResolvable(baseKey + ".deleteFailed", 
                                                            params.toArray()));
        }

        return "redirect:strategies";
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder binder, final YukonUserContext userContext) {
        
        binder.registerCustomEditor(LocalTime.class, 
            datePropertyEditorFactory.getLocalTimePropertyEditor(DateFormatEnum.TIME24H, userContext));
    }
}
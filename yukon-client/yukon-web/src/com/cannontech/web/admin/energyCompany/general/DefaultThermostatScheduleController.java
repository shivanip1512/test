package com.cannontech.web.admin.energyCompany.general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.context.Theme;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;
import com.cannontech.web.admin.energyCompany.general.service.GeneralInfoService;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.cannontech.web.stars.dr.operator.validator.AccountThermostatScheduleValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@Controller
@RequestMapping("/energyCompany/schedules/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
public class DefaultThermostatScheduleController { 

    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private GeneralInfoService generalInfoService;
    @Autowired private OperatorThermostatHelper operatorThermostatHelper;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private ThermostatService thermostatService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private EnergyCompanyDao ecDao;

    @RequestMapping("view")
    public String view(YukonUserContext userContext, ModelMap modelMap, int ecId, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        List<HardwareType> availableThermostatTypes = energyCompany.getAvailableThermostatTypes();
        Iterable<HardwareType> availableThermostats = Iterables.filter(availableThermostatTypes, new Predicate<HardwareType>() {
            @Override
            public boolean apply(HardwareType input) {
                return input.isSupportsSchedules();
            }
        });
        availableThermostatTypes = Lists.newArrayList(availableThermostats);
        
        List<SchedulableThermostatType> schedulableThermostatTypes =
            Lists.transform(availableThermostatTypes, new Function<HardwareType, SchedulableThermostatType>() {
                @Override
                public SchedulableThermostatType apply(HardwareType hardwareType) {
                    return SchedulableThermostatType.getByHardwareType(hardwareType);
                }
            });
        
        Set<SchedulableThermostatType> distinctScheduleThermostatTypes = Sets.newLinkedHashSet(schedulableThermostatTypes);
        modelMap.addAttribute("schedulableThermostatTypes", distinctScheduleThermostatTypes);
        modelMap.addAttribute("availableThermostatTypes", availableThermostatTypes);        
        
        return "energyCompany/defaultThermostatScheduleList.jsp";
    }

    @RequestMapping(value = "editDefaultThermostatSchedule", method = RequestMethod.GET)
    public String editDefaultThermostatSchedule(ModelMap modelMap,
                                                EnergyCompanyInfoFragment energyCompanyInfoFragment,
                                                HttpServletRequest request,
                                                String type,
                                                FlashScope flashScope,
                                                YukonUserContext yukonUserContext) {

        setupModelMap(modelMap, energyCompanyInfoFragment, yukonUserContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        // AccountThermostatSchedule
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.valueOf(type);
        AccountThermostatSchedule schedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByType(energyCompany.getEnergyCompanyId(), schedulableThermostatType);
        modelMap.addAttribute("schedule", schedule);
        
        // temperatureUnit
        String temperatureUnit = energyCompanySettingDao.getString(EnergyCompanySettingType.DEFAULT_TEMPERATURE_UNIT, energyCompany.getEnergyCompanyId());
        TemperatureUnit temp = TemperatureUnit.valueOf(temperatureUnit);
        modelMap.addAttribute("temperatureUnit", temp.getLetter());

        modelMap.addAttribute("thermostatType", schedulableThermostatType);
        modelMap.addAttribute("type", type);

        Set<ThermostatScheduleMode> modes = schedulableThermostatType.getAllowedModes(thermostatService.getAllowedThermostatScheduleModes(energyCompany));
        modelMap.addAttribute("allowedModes", modes);
        
        // Set the displable form of type to the model map
        String typeStr = getMessage(request).getMessage(schedulableThermostatType.getHardwareType());
        modelMap.addAttribute("typeStr", typeStr);
        
        //check for invalid schedule bits
        DataBinder binder = new DataBinder(schedule);
        
        AccountThermostatScheduleValidator accountThermostatScheduleValidator =
            new AccountThermostatScheduleValidator(accountThermostatScheduleDao, messageSourceResolver.getMessageSourceAccessor(yukonUserContext));
        
        binder.setValidator(accountThermostatScheduleValidator);
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();
        
        //show the errors in the flash scope
        if(bindingResult.hasErrors()){
            //we want to show the field errors in the flash scope since the UI does not have room for showing the errors
            //individually
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult, true);
            messages.add(0, new YukonMessageSourceResolvable("yukon.web.components.thermostat.schedule.error.invalidScheduleEntry"));
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
        
        List<AccountThermostatSchedule> defaultSchedules = new ArrayList<AccountThermostatSchedule>();
        for(ThermostatScheduleMode mode : modes){
            if(schedule.getThermostatScheduleMode() == mode){
                defaultSchedules.add(schedule);
            }else{
                //create a schedule that looks similar to the default
                AccountThermostatSchedule defaultAts = new AccountThermostatSchedule();
                defaultAts.setAccountId(schedule.getAccountId());
                defaultAts.setAccountThermostatScheduleId(schedule.getAccountThermostatScheduleId());
                defaultAts.setScheduleName(schedule.getScheduleName());
                defaultAts.setThermostatScheduleMode(mode);
                defaultAts.setThermostatType(schedulableThermostatType);
                thermostatService.addMissingScheduleEntriesForDefaultSchedules(defaultAts);
                
                defaultSchedules.add(defaultAts);
            }
        }        
        modelMap.addAttribute("defaultSchedules", defaultSchedules);
        
        return "energyCompany/defaultThermostatScheduleEdit.jsp";
    }

    @RequestMapping(value="save", method = RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
    public String save(@ModelAttribute("temperatureUnit") String temperatureUnit,
                       @RequestParam(value="schedules", required=true) String schedulesJson,
                       EnergyCompanyInfoFragment energyCompanyInfoFragment,
                       FlashScope flashScope,
                       YukonUserContext userContext,
                       ModelMap modelMap) throws JsonProcessingException, IOException {

        AccountThermostatSchedule ats = JsonUtils.fromJson(schedulesJson, AccountThermostatSchedule.class);
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);

        // determine if legacy schedule name should be changed 
        String useScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(ats, null, userContext);
        ats.setScheduleName(useScheduleName);

        // Save changes to schedule
        accountThermostatScheduleDao.save(ats);

        modelMap.addAttribute("type", ats.getThermostatType());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.schedules.scheduleSaved"));
        return "redirect:editDefaultThermostatSchedule";
    }
    
    private void setupModelMap(ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment, YukonUserContext userContext) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);

        EnergyCompany energyCompany = ecDao.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        GeneralInfo generalInfo = generalInfoService.getGeneralInfo(energyCompany);
        modelMap.addAttribute("generalInfo", generalInfo);
        
    }
    
    private MessageSourceAccessor getMessage(HttpServletRequest request) {
        Theme theme = RequestContextUtils.getTheme(request);
        MessageSource messageSource = theme.getMessageSource();
        Locale locale = RequestContextUtils.getLocale(request);
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource, locale);
        
        return messageSourceAccessor;
    }
}
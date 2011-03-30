package com.cannontech.web.admin.energyCompany.general;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.context.Theme;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;
import com.cannontech.web.admin.energyCompany.general.service.GeneralInfoService;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;


@Controller
@RequestMapping("/energyCompany/schedules/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
public class DefaultThermostatScheduleController { 

    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    private GeneralInfoService generalInfoService;
    private OperatorThermostatHelper operatorThermostatHelper;
    private RolePropertyDao rolePropertyDao;   
    private StarsDatabaseCache starsDatabaseCache;
    
    @RequestMapping
    public String view(YukonUserContext userContext, ModelMap modelMap, int ecId, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        List<HardwareType> availableThermostatTypes = energyCompany.getAvailableThermostatTypes();
        List<SchedulableThermostatType> schedulableThermostatTypes =
            Lists.transform(availableThermostatTypes, new Function<HardwareType, SchedulableThermostatType>() {

                @Override
                public SchedulableThermostatType apply(HardwareType hardwareType) {
                    return SchedulableThermostatType.getByHardwareType(hardwareType);
                }
                
            });
        
        modelMap.addAttribute("schedulableThermostatTypes", schedulableThermostatTypes);
        modelMap.addAttribute("availableThermostatTypes", availableThermostatTypes);        
        
        
        return "energyCompany/defaultThermostatScheduleList.jsp";
    }

    @RequestMapping(value = "editDefaultThermostatSchedule", method = RequestMethod.GET)
    public String editDefaultThermostatSchedule(ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment,
                                                HttpServletRequest request, String type, YukonUserContext userContext) {

        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        // AccountThermostatSchedule
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.valueOf(type);
        AccountThermostatSchedule schedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByType(energyCompany.getEnergyCompanyId(), schedulableThermostatType);
        modelMap.addAttribute("schedule", schedule);
        
        // schedule52Enabled
        boolean schedule52Enabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2, userContext.getYukonUser());
        modelMap.addAttribute("schedule52Enabled", schedule52Enabled);
        
        // adjusted scheduleMode
        ThermostatScheduleMode scheduleMode = schedule.getThermostatScheduleMode();
        if (scheduleMode == ThermostatScheduleMode.WEEKDAY_WEEKEND && (schedule.getThermostatType() != SchedulableThermostatType.UTILITY_PRO || !schedule52Enabled)) {
            scheduleMode = ThermostatScheduleMode.WEEKDAY_SAT_SUN;
        }
        modelMap.addAttribute("scheduleMode", scheduleMode);
        
        // temperatureUnit
        String temperatureUnit = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_TEMPERATURE_UNIT, energyCompany.getUser());
        modelMap.addAttribute("temperatureUnit", temperatureUnit);
        
        // scheduleJSON
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = operatorThermostatHelper.getJSONForSchedule(schedule, isFahrenheit);
        modelMap.addAttribute("scheduleJSONString", scheduleJSON.toString());
        modelMap.addAttribute("schedule", schedule);
        
        modelMap.addAttribute("type", type);
        
        // Set the displable form of type to the model map
        YukonMessageSourceResolvable messageSourceResolvable = 
            new YukonMessageSourceResolvable(schedulableThermostatType.getHardwareType().getDisplayKey());
        String typeStr = getMessage(request).getMessage(messageSourceResolvable);
        modelMap.addAttribute("typeStr", typeStr);
        
        return "energyCompany/defaultThermostatScheduleEdit.jsp";
    }

    @RequestMapping(value = "saveDefaultThermostatSchedule", method = RequestMethod.POST)
    public String save(String type,
                       String timeOfWeek,
                       String scheduleMode,
                       String temperatureUnit,
                       Integer scheduleId, 
                       String scheduleName,
                       EnergyCompanyInfoFragment energyCompanyInfoFragment,
                       @RequestParam(value="schedules", required=true) String scheduleString,
                       FlashScope flashScope,
                       YukonUserContext userContext,
                       ModelMap modelMap) throws ServletRequestBindingException {

        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
        ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);

        // id
        AccountThermostatSchedule ats = new AccountThermostatSchedule();
        ats.setAccountThermostatScheduleId(scheduleId);

        // schedulableThermostatType
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.valueOf(type);
        ats.setThermostatType(schedulableThermostatType);
        
        // Create schedule from submitted JSON string
        List<AccountThermostatScheduleEntry> atsEntries = 
            operatorThermostatHelper.getScheduleEntriesForJSON(scheduleString, scheduleId, schedulableThermostatType, thermostatScheduleMode, isFahrenheit);
        ats.setScheduleEntries(atsEntries);
        
        
        // thermostatScheduleMode
        ats.setThermostatScheduleMode(thermostatScheduleMode);
        
        // COMMERCIAL_EXPRESSSTAT setToTwoTimeTemps
        if(schedulableThermostatType.getPeriodStyle() == ThermostatSchedulePeriodStyle.TWO_TIMES){
            operatorThermostatHelper.setToTwoTimeTemps(ats);
        }
        
        // determine if legacy schedule name should be changed 
        String useScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(ats, null, userContext);
        ats.setScheduleName(useScheduleName);

        // Save changes to schedule
        accountThermostatScheduleDao.save(ats);

        modelMap.addAttribute("type", type);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.schedules.scheduleSaved"));
        return "redirect:editDefaultThermostatSchedule";
    }
    
    private void setupModelMap(ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment, YukonUserContext userContext) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
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

    // DI Setters
    @Autowired
    public void setGeneralInfoService(GeneralInfoService generalInfoService) {
        this.generalInfoService = generalInfoService;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setOperatorThermostatHelper(OperatorThermostatHelper operatorThermostatHelper) {
        this.operatorThermostatHelper = operatorThermostatHelper;
    }
    
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
        this.accountThermostatScheduleDao = accountThermostatScheduleDao;
    }

}
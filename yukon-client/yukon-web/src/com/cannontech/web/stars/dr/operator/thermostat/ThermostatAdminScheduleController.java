package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.List;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

/**
 * Controller for Operator-side Thermostat schedule operations
 */
@CheckRole(YukonRole.CONSUMER_INFO)
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatAdminScheduleController {

    private StarsDatabaseCache starsDatabaseCache;
    private RolePropertyDao rolePropertyDao;   
    private OperatorThermostatHelper operatorThermostatHelper;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    
    @RequestMapping(value = "/admin/thermostat/schedule/view", method = RequestMethod.GET)
    public String view(String type, YukonUserContext yukonUserContext, ModelMap map) {

    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(yukonUserContext.getYukonUser());
        
    	// AccountThermostatSchedule
    	SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.valueOf(type);
    	AccountThermostatSchedule schedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByType(energyCompany.getEnergyCompanyId(), schedulableThermostatType);
    	map.addAttribute("schedule", schedule);
    	
    	// schedule52Enabled
    	boolean schedule52Enabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2, yukonUserContext.getYukonUser());
    	map.addAttribute("schedule52Enabled", schedule52Enabled);
    	
    	// adjusted scheduleMode
    	ThermostatScheduleMode scheduleMode = schedule.getThermostatScheduleMode();
    	if (scheduleMode == ThermostatScheduleMode.WEEKDAY_WEEKEND && (schedule.getThermostatType() != SchedulableThermostatType.UTILITY_PRO || !schedule52Enabled)) {
    		scheduleMode = ThermostatScheduleMode.WEEKDAY_SAT_SUN;
    	}
    	map.addAttribute("scheduleMode", scheduleMode);
    	
    	// temperatureUnit
    	String temperatureUnit = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_TEMPERATURE_UNIT, energyCompany.getUser());
    	map.addAttribute("temperatureUnit", temperatureUnit);
    	
    	// scheduleJSON
    	boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = operatorThermostatHelper.getJSONForSchedule(schedule, isFahrenheit);
        map.addAttribute("scheduleJSONString", scheduleJSON.toString());
        map.addAttribute("schedule", schedule);
    	
        return "operator/thermostat/adminThermostatSchedule.jsp";
    }

    @RequestMapping(value = "/admin/thermostat/schedule/save", method = RequestMethod.POST)
    public String save(String type,
    				   String timeOfWeek,
    				   String scheduleMode,
    				   String temperatureUnit,
    				   Integer scheduleId, 
    				   String scheduleName,
			    	   @RequestParam(value="schedules", required=true) String scheduleString,
			    	   YukonUserContext yukonUserContext,
			    	   ModelMap map) throws ServletRequestBindingException {

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
        String useScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(ats, null, yukonUserContext);
        ats.setScheduleName(useScheduleName);

        // Save changes to schedule
        accountThermostatScheduleDao.save(ats);

        map.addAttribute("type", type);
        
        map.addAttribute("saveSuccessful", true);
        
        return "redirect:/operator/Admin/ThermSchedule.jsp";
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
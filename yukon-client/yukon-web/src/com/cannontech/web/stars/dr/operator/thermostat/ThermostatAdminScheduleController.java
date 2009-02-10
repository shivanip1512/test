package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Controller for Operator-side Thermostat schedule operations
 */
@CheckRole(YukonRole.CONSUMER_INFO)
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatAdminScheduleController extends AbtractThermostatOperatorScheduleController {

    private ThermostatScheduleDao thermostatScheduleDao;
    private StarsDatabaseCache starsDatabaseCache;

    @RequestMapping(value = "/admin/thermostat/schedule/view", method = RequestMethod.GET)
    public String view(String type, YukonUserContext yukonUserContext, ModelMap map, 
            HttpServletRequest request) {

    	LiteStarsEnergyCompany energyCompany = 
    		starsDatabaseCache.getEnergyCompanyByUser(yukonUserContext.getYukonUser());
        
    	HardwareType thermostatType = HardwareType.valueOf(type);
        ThermostatSchedule schedule = 
        	thermostatScheduleDao.getEnergyCompanyDefaultSchedule(energyCompany, thermostatType);

        // Add the temperature unit to model
        String temperatureUnit = CtiUtilities.FAHRENHEIT_CHARACTER;
        map.addAttribute("temperatureUnit", temperatureUnit);

        // Get json string for schedule and add schedule and string to model
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = this.getJSONForSchedule(schedule, isFahrenheit);
        map.addAttribute("scheduleJSONString", scheduleJSON.toString());
        map.addAttribute("schedule", schedule);

        Locale locale = yukonUserContext.getLocale();
        map.addAttribute("localeString", locale.toString());

        map.addAttribute("thermostatType", thermostatType.toString());
        
        return "operator/thermostat/adminThermostatSchedule.jsp";
    }

    @RequestMapping(value = "/admin/thermostat/schedule/save", method = RequestMethod.POST)
    public String save(String type, String timeOfWeek, String scheduleMode, String temperatureUnit, 
    		Integer scheduleId, YukonUserContext yukonUserContext, HttpServletRequest request, 
    		ModelMap map) 
    	throws ServletRequestBindingException {

    	LiteStarsEnergyCompany energyCompany = 
    		starsDatabaseCache.getEnergyCompanyByUser(yukonUserContext.getYukonUser());
        
        String scheduleString = ServletRequestUtils.getRequiredStringParameter(request, "schedules");

        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);

        // Create schedule from submitted JSON string
        ThermostatSchedule newSchedule = getScheduleForJSON(scheduleString,
                                                         isFahrenheit);
        newSchedule.setId(scheduleId);
        
        HardwareType hardwareType = HardwareType.valueOf(type);
        newSchedule.setThermostatType(hardwareType);
        
        if (type.equals(HardwareType.COMMERCIAL_EXPRESSSTAT)) {
            this.setToTwoTimeTemps(newSchedule);
        }

        // Save changes to schedule
        thermostatScheduleDao.saveDefaultSchedule(newSchedule, energyCompany);

        return "redirect:/operator/Admin/ThermSchedule.jsp?type=" + type;
    }
    
    @Autowired
    public void setThermostatScheduleDao(
            ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
    
}

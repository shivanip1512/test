package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleDisplay;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriodStyle;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

/**
 * Controller for Consumer-side Thermostat schedule operations
 */
@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatScheduleController extends AbstractThermostatController {
    private AccountEventLogService accountEventLogService;
    private InventoryDao inventoryDao;
    private CustomerDao customerDao;
    private ThermostatService thermostatService;
    private OperatorThermostatHelper operatorThermostatHelper;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    private ThermostatEventHistoryDao thermostatEventHistoryDao;

    @RequestMapping(value = "/consumer/thermostat/schedule/view", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount account, 
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            Integer scheduleId, Boolean createAsNew, YukonUserContext yukonUserContext, ModelMap map) {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        accountCheckerService.checkThermostatSchedule(user, scheduleId);
        accountCheckerService.checkInventory(user, thermostatIds);

        int thermostatId = thermostatIds.get(0);
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        
        AccountThermostatSchedule schedule = null;
        AccountThermostatSchedule defaultSchedule = null;
        int accountId = account.getAccountId();
        if (scheduleId != null) {
        	schedule = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId, accountId);
        	if (!schedulableThermostatType.getSupportedScheduleModes().contains(schedule.getThermostatScheduleMode())) {
        		throw new IllegalStateException("Thermostat type " + schedulableThermostatType + " does not support schedule mode " + schedule.getThermostatScheduleMode());
        	}
        }

        // first try to get specific schedule asked for
        if (scheduleId != null) {
        	schedule = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId, accountId);
        	if (schedule != null && !schedulableThermostatType.getSupportedScheduleModes().contains(schedule.getThermostatScheduleMode())) {
        		throw new IllegalStateException("Thermostat type " + schedulableThermostatType + " does not support schedule mode " + schedule.getThermostatScheduleMode());
        	}
        }

        // next try to get the schedule the thermostat is currently associated with (or the default if none)
        // or use the default schedule as a template if "createAsNew"
        if (schedule == null) {
        	if (BooleanUtils.isTrue(createAsNew)) {
        		schedule = thermostatService.getAccountThermostatScheduleTemplate(accountId, schedulableThermostatType);
        	} else {
        		schedule = accountThermostatScheduleDao.findByInventoryId(thermostat.getId());
        		if (schedule == null) {
        			schedule = thermostatService.getAccountThermostatScheduleTemplate(accountId, schedulableThermostatType);
        		}
        	}
        }
        
        //make sure the schedule has entries for each time of week associated with its current mode
        thermostatService.addMissingScheduleEntries(schedule);
        
        String useScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(schedule, thermostatIds.size() == 1 ? thermostat.getLabel() : null, yukonUserContext);
        schedule.setScheduleName(useScheduleName);
        
        defaultSchedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByAccountAndType(accountId, schedulableThermostatType);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, yukonUserContext);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        
        // schedule52Enabled
    	boolean schedule52Enabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.RESIDENTIAL_THERMOSTAT_SCHEDULE_5_2, yukonUserContext.getYukonUser());
    	map.addAttribute("schedule52Enabled", schedule52Enabled);
    	
    	// adjusted scheduleMode
    	ThermostatScheduleMode scheduleMode = schedule.getThermostatScheduleMode();
    	if (scheduleMode == ThermostatScheduleMode.WEEKDAY_WEEKEND && (schedule.getThermostatType() != SchedulableThermostatType.UTILITY_PRO || !schedule52Enabled)) {
    		scheduleMode = ThermostatScheduleMode.WEEKDAY_SAT_SUN;
    	}
    	map.addAttribute("scheduleMode", scheduleMode);
    	
        // thermostatLabel
        if (thermostatIds.size() == 1) {
	        YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.label", thermostat.getLabel());
	        map.addAttribute("thermostatLabel", resolvable);
        } else {
        	YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.multipleLabel");
            map.addAttribute("thermostatLabel", resolvable);
        }
        
        // temperatureUnit
        LiteCustomer customer = customerDao.getCustomerForUser(user.getUserID());
        String temperatureUnit = customer.getTemperatureUnit();
        map.addAttribute("temperatureUnit", temperatureUnit);
        
        // scheduleJSON
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = operatorThermostatHelper.getJSONForSchedule(schedule, isFahrenheit);
        map.addAttribute("scheduleJSONString", scheduleJSON.toString());
        map.addAttribute("schedule", schedule);
        
        JSONObject defaultFahrenheitScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, true);
        map.addAttribute("defaultFahrenheitScheduleJSON", defaultFahrenheitScheduleJSON.toString());
        JSONObject defaultCelsiusScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, false);
        map.addAttribute("defaultCelsiusScheduleJSON", defaultCelsiusScheduleJSON.toString());
        
        return "consumer/thermostatSchedule.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/confirm", method = RequestMethod.POST)
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String type, String scheduleMode, String temperatureUnit, Integer scheduleId,
            String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
            @RequestParam(value="schedules", required=true) String scheduleString,
            ModelMap map) throws ServletRequestBindingException {
    	
    	// thermostatLabel
    	Thermostat thermostat = null;
        int thermostatId = thermostatIds.get(0);
		thermostat = inventoryDao.getThermostatById(thermostatId);
        YukonMessageSourceResolvable resolvable = 
        	new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.label",
        									 thermostat.getLabel());
        map.addAttribute("thermostatLabel", resolvable);
    	
    	// Create the confirm schedule text
    	boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
    	ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);
    	
    	// id
        AccountThermostatSchedule ats = new AccountThermostatSchedule();
        ats.setAccountThermostatScheduleId(scheduleId);
        ats.setThermostatType(SchedulableThermostatType.valueOf(type));
        
        // Create schedule from submitted JSON string
        List<AccountThermostatScheduleEntry> atsEntries = operatorThermostatHelper.getScheduleEntriesForJSON(scheduleString, scheduleId, thermostatScheduleMode, isFahrenheit);
        ats.setScheduleEntries(atsEntries);
        
        // Build up schedule display object, containing printable representations of the thermostat schedule entries
        String i18nKey = (isFahrenheit) ? "yukon.dr.consumer.thermostatScheduleConfirm.scheduleText.timeCoolHeatFahrenheit" : 
			                              "yukon.dr.consumer.thermostatScheduleConfirm.scheduleText.timeCoolHeatCelsius";
        List<ThermostatScheduleDisplay> scheduleDisplays = operatorThermostatHelper.getScheduleDisplays(yukonUserContext, type, thermostatScheduleMode, ats, isFahrenheit, i18nKey);

    	// Pass all of the parameters through to confirm page
        map.addAttribute("confirmationDisplays", scheduleDisplays);
    	map.addAttribute("schedules", scheduleString);
    	map.addAttribute("type", type);
    	map.addAttribute("scheduleMode", scheduleMode);
    	map.addAttribute("temperatureUnit", temperatureUnit);
    	map.addAttribute("scheduleId", scheduleId);
    	map.addAttribute("scheduleName", scheduleName);
    	map.addAttribute("saveAction", saveAction);
        
    	return "consumer/thermostatScheduleConfirm.jsp";
    }    	

    @RequestMapping(value = "/consumer/thermostat/schedule/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("customerAccount") CustomerAccount account,
			@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
			String type, String scheduleMode, String temperatureUnit, Integer scheduleId,
			String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
			@RequestParam(value="schedules", required=true) String scheduleString,
			ModelMap map) throws ServletRequestBindingException {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        AccountThermostatSchedule oldAts = accountThermostatScheduleDao.getById(scheduleId);
        
        // Log thermostat schedule save attempt
        for (int thermostatId : thermostatIds) {
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            
            accountEventLogService.thermostatScheduleSavingAttemptedByConsumer(user,
                                                                               account.getAccountNumber(),
                                                                               thermostat.getSerialNumber(),
                                                                               scheduleName);
        }

        accountCheckerService.checkThermostatSchedule(user, scheduleId);
        accountCheckerService.checkInventory(user, thermostatIds);
        
        boolean sendAndSave = "saveApply".equals(saveAction);

        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(account.getCustomerId(), escapedTempUnit);
        }
        
        ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);
        
        // id
        AccountThermostatSchedule ats = new AccountThermostatSchedule();
        ats.setAccountThermostatScheduleId(scheduleId);
        ats.setAccountId(account.getAccountId());
        
        // Create schedule from submitted JSON string
        List<AccountThermostatScheduleEntry> atsEntries = operatorThermostatHelper.getScheduleEntriesForJSON(scheduleString, scheduleId, thermostatScheduleMode, isFahrenheit);
        ats.setScheduleEntries(atsEntries);
        
        // schedulableThermostatType
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.valueOf(type);
        ats.setThermostatType(schedulableThermostatType);
        
        // thermostatScheduleMode
        ats.setThermostatScheduleMode(thermostatScheduleMode);
        
        // COMMERCIAL_EXPRESSSTAT setToTwoTimeTemps
        if (schedulableThermostatType.getPeriodStyle() == ThermostatSchedulePeriodStyle.TWO_TIMES) {
        	operatorThermostatHelper.setToTwoTimeTemps(ats);
        }
        
        // scheduleName
    	ats.setScheduleName(scheduleName);
        
    	// SAVE
        accountThermostatScheduleDao.saveAndMapToThermostats(ats, thermostatIds);
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.CONSUMER_SAVE_SCHEDULE_SUCCESS;

        // Log schedule name change
        if (!oldAts.getScheduleName().equalsIgnoreCase(ats.getScheduleName())) {
            accountEventLogService.thermostatScheduleNameChanged(user,
                                                                 oldAts.getScheduleName(),
                                                                 ats.getScheduleName());
        }

        
        // SEND
        if (sendAndSave) {
        	
        	boolean failed = false;
        	for (int thermostatId : thermostatIds) {
        		
        		for (TimeOfWeek timeOfWeek : thermostatScheduleMode.getAssociatedTimeOfWeeks()) {
        			
        			message = thermostatService.sendSchedule(account, ats, thermostatId, timeOfWeek, thermostatScheduleMode, yukonUserContext);
                	
            		if (message.isFailed()) {
                        failed = true;
                    }
        		}
        		
        		if(!failed) {
                    //Log schedule send to thermostat history
                    thermostatEventHistoryDao.logScheduleEvent(yukonUserContext.getYukonUser(), thermostatId, ats.getAccountThermostatScheduleId(), thermostatScheduleMode);
                }
        	}
        	
        	if (failed && thermostatIds.size() > 1) {
                message = ThermostatScheduleUpdateResult.CONSUMER_MULTIPLE_ERROR;
            }
        }
        
        map.addAttribute("message", message.toString());
        map.addAttribute("thermostatIds", thermostatIds.toString());

        return "redirect:/spring/stars/consumer/thermostat/schedule/complete";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/complete", method = RequestMethod.GET)
    public String updateComplete(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String message, LiteYukonUser user, ModelMap map) throws Exception {

        accountCheckerService.checkInventory(user, thermostatIds);
        
        ThermostatScheduleUpdateResult resultMessage = ThermostatScheduleUpdateResult.valueOf(message);
        String key = resultMessage.getDisplayKey();

        YukonMessageSourceResolvable resolvable;

        List<String> thermostatLabels = new ArrayList<String>();
        for(Integer thermostatId : thermostatIds) {
        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        	thermostatLabels.add(thermostat.getLabel());
        }
        
        String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
        resolvable = new YukonMessageSourceResolvable(key, thermostatLabelString);

        map.addAttribute("message", resolvable);

        map.addAttribute("thermostatIds", thermostatIds);

        map.addAttribute("viewUrl",
                         "/spring/stars/consumer/thermostat/schedule/view");

        return "consumer/actionComplete.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/hints", method = RequestMethod.GET)
    public String hints(ModelMap map) {
        return "consumer/scheduleHints.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/view/saved", method = RequestMethod.GET)
    public String viewSaved(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds, YukonUserContext yukonUserContext,
            ModelMap map) throws Exception {

        accountCheckerService.checkInventory(yukonUserContext.getYukonUser(), thermostatIds);

        int thermostatId = thermostatIds.get(0);
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        
        AccountThermostatSchedule thermostatCurrentSchedule = accountThermostatScheduleDao.findByInventoryId(thermostatId);
		if (thermostatCurrentSchedule != null) {
			map.addAttribute("currentScheduleId", thermostatCurrentSchedule.getAccountThermostatScheduleId());
		}
		
		List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllSchedulesForAccountByType(account.getAccountId(), type);
		map.addAttribute("schedules", schedules);

        return "consumer/savedSchedules.jsp";
    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/saved", method = RequestMethod.POST, params = "delete")
    public String delete(HttpServletRequest request, @ModelAttribute("customerAccount") CustomerAccount account,
    		String thermostatIds, Integer scheduleId, LiteYukonUser user, ModelMap map) throws Exception {
    	
    	List<Integer> thermostatIdsList = getThermostatIds(request);
    	
    	accountCheckerService.checkInventory(user, thermostatIdsList);
    	
    	accountThermostatScheduleDao.deleteById(scheduleId);
    	
    	map.addAttribute("thermostatIds", thermostatIds);
    	
    	return "redirect:/spring/stars/consumer/thermostat/schedule/view/saved";
    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/saved", method = RequestMethod.POST, params = "view")
    public String viewSchedule(String thermostatIds, Integer scheduleId, ModelMap map) throws Exception {
    	map.addAttribute("scheduleId", scheduleId);
    	map.addAttribute("thermostatIds", thermostatIds);
    	return "redirect:/spring/stars/consumer/thermostat/schedule/view";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/saved", method = RequestMethod.POST, params = "createNew")
    public String createNewSchedule(String thermostatIds, Integer scheduleId, ModelMap map) throws Exception {
    	
    	map.addAttribute("createAsNew", true);
    	map.addAttribute("thermostatIds", thermostatIds);
    	return "redirect:/spring/stars/consumer/thermostat/schedule/view";
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Autowired
    public void setThermostatService(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }
    
    @Autowired
    public void setOperatorThermostatHelper(OperatorThermostatHelper operatorThermostatHelper) {
		this.operatorThermostatHelper = operatorThermostatHelper;
	}
    
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
		this.accountThermostatScheduleDao = accountThermostatScheduleDao;
	}
    
    @Autowired
    public void setThermostatEventHistoryDao(ThermostatEventHistoryDao thermostatEventHistoryDao) {
        this.thermostatEventHistoryDao = thermostatEventHistoryDao;
    }
}

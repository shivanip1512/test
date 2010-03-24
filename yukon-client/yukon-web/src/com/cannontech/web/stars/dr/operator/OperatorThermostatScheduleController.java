package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ScheduleDropDownItem;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

@CheckRole(YukonRole.CONSUMER_INFO)
@Controller
@RequestMapping(value = "/operator/thermostatSchedule/*")
public class OperatorThermostatScheduleController {
	
	private InventoryDao inventoryDao;
	private CustomerDao customerDao;
	private CustomerAccountDao customerAccountDao;
	private ThermostatService thermostatService;
	private ThermostatScheduleDao thermostatScheduleDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private DateFormattingService dateFormattingService;
	private OperatorThermostatHelper operatorThermostatHelper;
	private AccountCheckerService accountCheckerService;
	
	// VIEW
	@RequestMapping
    public String view(String thermostatIds,
					            Integer scheduleId, 
					            YukonUserContext yukonUserContext, 
					            ModelMap modelMap, 
					            AccountInfoFragment accountInfoFragment) {

		
		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        
        ThermostatSchedule schedule = null;
        ThermostatSchedule defaultSchedule = null;
        int accountId = customerAccount.getAccountId();
        if (scheduleId != null) {
            schedule = thermostatScheduleDao.getThermostatScheduleById(scheduleId, accountId); // Get the schedule that the user selected
        }

        Integer thermostatId = null;
        HardwareType thermostatType = null;
        
        // single thermostat selected
        if (thermostatIdsList.size() == 1) {

            thermostatId = thermostatIdsList.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

            // Get the current (or default if no current) schedule
            if (schedule == null) {
                
                schedule = thermostatService.getThermostatSchedule(thermostat, customerAccount);
                if (StringUtils.isBlank(schedule.getName())) {
                    schedule.setName(thermostat.getLabel());
                }
            }

            defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId, thermostat.getType());
            thermostatType = thermostat.getType();
            
         // multiple thermostats selected
        } else {
            
            // Get type of the first thermostat in the list
			thermostatId = thermostatIdsList.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
			thermostatType = thermostat.getType();

            if (schedule == null) {
                schedule = thermostatService.getThermostatSchedule(thermostat, customerAccount);
            }

            defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId, thermostatType);
        }

        // Add the temperature unit to model
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        modelMap.addAttribute("temperatureUnit", temperatureUnit);

        // Get json string for schedule and add schedule and string to model
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = operatorThermostatHelper.getJSONForSchedule(schedule, isFahrenheit);
        modelMap.addAttribute("scheduleJSONString", scheduleJSON.toString());
        modelMap.addAttribute("schedule", schedule);

        // Get json string for the default schedule and add to model
        JSONObject defaultFahrenheitScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, true);
        modelMap.addAttribute("defaultFahrenheitScheduleJSON", defaultFahrenheitScheduleJSON.toString());

        JSONObject defaultCelsiusScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, false);
        modelMap.addAttribute("defaultCelsiusScheduleJSON", defaultCelsiusScheduleJSON.toString());


        Locale locale = yukonUserContext.getLocale();
        modelMap.addAttribute("localeString", locale.toString());

        modelMap.addAttribute("thermostatType", thermostatType.toString());

        return "operator/operatorThermostat/schedule/view.jsp";
    }
	
	// CONFIRM OR SAVE 
	@RequestMapping
    public String confirmOrSave(String thermostatIds,
					    		String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
					    		String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
					    		String schedules,
					    		HttpServletRequest request, ModelMap modelMap,
					    		AccountInfoFragment accountInfoFragment) {
		
		operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		
    	modelMap.addAttribute("saveAction", saveAction);
    	modelMap.addAttribute("scheduleId", scheduleId);
    	modelMap.addAttribute("schedules", schedules);
    	modelMap.addAttribute("timeOfWeek", timeOfWeek);
    	modelMap.addAttribute("scheduleMode", scheduleMode);
    	modelMap.addAttribute("temperatureUnit", temperatureUnit);
    	modelMap.addAttribute("scheduleId", scheduleId);
    	modelMap.addAttribute("scheduleName", scheduleName);
    	
    	boolean sendAndSave = "saveApply".equals(saveAction);
    	if(!sendAndSave) {
    		return "redirect:save";
    	}
    	
    	return "redirect:confirm";
    }
	
	// CONFIRM
	@RequestMapping
    public String confirm(String thermostatIds,
					            String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
					            String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
					            String schedules,
					            HttpServletRequest request, ModelMap modelMap,
					            AccountInfoFragment accountInfoFragment) {
    	
		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		
    	// Create the confirm schedule text
    	boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
    	ThermostatSchedule schedule = operatorThermostatHelper.getScheduleForJSON(schedules, isFahrenheit);
    	
    	MessageSource messageSource = messageSourceResolver.getMessageSource(yukonUserContext);
    	TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);
        TimeOfWeek displayTimeOfWeek = scheduleTimeOfWeek;
        if (ThermostatScheduleMode.valueOf(scheduleMode) == ThermostatScheduleMode.ALL) {
            displayTimeOfWeek = TimeOfWeek.EVERYDAY;
        }    	
    	YukonMessageSourceResolvable timeOfWeekString = new YukonMessageSourceResolvable(displayTimeOfWeek.getDisplayKey());
    	
    	List<Object> argumentList = new ArrayList<Object>();
    	argumentList.add(timeOfWeekString);
    	
    	int thermostatId = thermostatIdsList.get(0);
    	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
    	
        String scheduleConfirmKey = "yukon.dr.operator.thermostatScheduleConfirm.scheduleText";
    	if (HardwareType.COMMERCIAL_EXPRESSSTAT.equals(thermostat.getType())) {
    		operatorThermostatHelper.setToTwoTimeTemps(schedule);
    		scheduleConfirmKey =  "yukon.dr.operator.thermostatScheduleConfirm.scheduleTextTwoTimeTemp";
    	}

    	List<ThermostatSeasonEntry> seasonEntries = schedule.getSeason().getSeasonEntries(scheduleTimeOfWeek);
    	
    	for(ThermostatSeasonEntry entry : seasonEntries) {

    	    LocalTime startTime = entry.getStartTime();
    	    String startDateString = dateFormattingService.format(startTime, DateFormatEnum.TIME, yukonUserContext);

    	    Integer coolTemperature = entry.getCoolTemperature();
    	    Integer heatTemperature = entry.getHeatTemperature();

    	    // Temperatures are only -1 if this is a 2 time temp thermostat type - ignore if -1
    	    if(coolTemperature != -1 && heatTemperature != -1) {
    	    	
    	        String tempUnit = (isFahrenheit) ? CtiUtilities.FAHRENHEIT_CHARACTER : CtiUtilities.CELSIUS_CHARACTER;
    	        coolTemperature = (int) CtiUtilities.convertTemperature(coolTemperature, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
    	        heatTemperature = (int) CtiUtilities.convertTemperature(heatTemperature, CtiUtilities.FAHRENHEIT_CHARACTER, tempUnit);
    	        argumentList.add(startDateString);
    	        argumentList.add(coolTemperature);
    	        argumentList.add(heatTemperature);
    	    }
    	}
    	
    	String scheduleConfirm = messageSource.getMessage(
						    			scheduleConfirmKey, 
						    			argumentList.toArray(), 
						    			yukonUserContext.getLocale());
    	
    	modelMap.addAttribute("scheduleConfirm", scheduleConfirm);
    	
    	// Pass all of the parameters through to confirm page
    	modelMap.addAttribute("schedules", schedules);
    	modelMap.addAttribute("timeOfWeek", timeOfWeek);
    	modelMap.addAttribute("scheduleMode", scheduleMode);
    	modelMap.addAttribute("temperatureUnit", temperatureUnit);
    	modelMap.addAttribute("scheduleId", scheduleId);
    	modelMap.addAttribute("scheduleName", scheduleName);
    	modelMap.addAttribute("saveAction", saveAction);
    	
    	return "operator/operatorThermostat/schedule/confirm.jsp";
    }
	
	// SAVE
	@RequestMapping
    public String save(String thermostatIds,
					            String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
					            String scheduleName, String saveAction, YukonUserContext yukonUserContext,
					            String schedules,
					            HttpServletRequest request, ModelMap modelMap,
					            FlashScope flashScope,
					            AccountInfoFragment accountInfoFragment) {

		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
		
        boolean sendAndSave = "saveApply".equals(saveAction);

        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(customerAccount.getCustomerId(), escapedTempUnit);
        }
        
        if (scheduleName.length() > 60) {
        	flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatSchedule.scheduleNameTooLong"));
        	return "redirect:view";
        }

        // Create schedule from submitted JSON string
        ThermostatSchedule schedule = operatorThermostatHelper.getScheduleForJSON(schedules, isFahrenheit);
        schedule.setName(scheduleName);
        schedule.setAccountId(customerAccount.getAccountId());

        TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);
        ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);

        ThermostatScheduleUpdateResult message = null;

        boolean failed = false;
        for (Integer thermostatId : thermostatIdsList) {

        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

            // Default the schedule name if none provided
            if (StringUtils.isBlank(schedule.getName())) {
            	schedule.setName(thermostat.getLabel());
            }
            
            HardwareType type = thermostat.getType();
            schedule.setThermostatType(type);
            if (type.equals(HardwareType.COMMERCIAL_EXPRESSSTAT)) {
            	operatorThermostatHelper.setToTwoTimeTemps(schedule);
            }

			if (sendAndSave) {
                // Send the schedule to the thermostat(s) and then update the
                // existing thermostat(s) schedule or create a new schedule for
                // the thermostat(s) if it has none

                schedule.setInventoryId(thermostatId);

                // Send schedule to thermostat
                message = thermostatService.sendSchedule(customerAccount,
                                                         schedule,
                                                         scheduleTimeOfWeek,
                                                         thermostatScheduleMode,
                                                         yukonUserContext);

                // Save changes to schedule
                thermostatService.updateSchedule(customerAccount,
                                                 schedule,
                                                 scheduleTimeOfWeek,
                                                 thermostatScheduleMode,
                                                 yukonUserContext);
                // If there are multiple thermostats to send schedule to and any
                // of the save/sends fail, the whole thing is failed
                if (message.isFailed()) {
                    failed = true;
                }
            } else {
                // Update the schedule if it exists already or create a new
                // schedule

                schedule.setInventoryId(thermostatId);

                // Save changes to schedule
                thermostatService.updateSchedule(customerAccount,
                                                 schedule,
                                                 scheduleTimeOfWeek,
                                                 thermostatScheduleMode,
                                                 yukonUserContext);

                message = ThermostatScheduleUpdateResult.CONSUMER_SAVE_SCHEDULE_SUCCESS;
            }
        }

        // If there was a failure and we are processing multiple
        // thermostats, set error to generic multiple error
        if (failed && thermostatIdsList.size() > 1) {
            message = ThermostatScheduleUpdateResult.CONSUMER_MULTIPLE_ERROR;
        }
        
        // message
        List<String> thermostatLabels = new ArrayList<String>();
        for(Integer thermostatId : thermostatIdsList) {
        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        	thermostatLabels.add(thermostat.getLabel());
        }
        String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
        MessageSourceResolvable messageResolvable = new YukonMessageSourceResolvable(message.getDisplayKey(), thermostatLabelString);
        flashScope.setMessage(messageResolvable, message.isFailed() ? FlashScopeMessageType.ERROR : FlashScopeMessageType.CONFIRM);

        return "redirect:view";
    }
	
	// HINTS
	@RequestMapping
    public String hints(String thermostatIds, 
    						   AccountInfoFragment accountInfoFragment,
    						   ModelMap modelMap) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		modelMap.addAttribute("thermostatIds", thermostatIds);
		
		return "operator/operatorThermostat/schedule/hints.jsp";
	}
	
	// SAVED SCHEDULES
	@RequestMapping
    public String savedSchedules(int thermostatId,
					            LiteYukonUser user, 
					            ModelMap modelMap,
					            AccountInfoFragment accountInfoFragment) {

        accountCheckerService.checkInventory(user, thermostatId);
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		modelMap.addAttribute("thermostatId", thermostatId);

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        HardwareType type = thermostat.getType();
        List<ScheduleDropDownItem> schedules = thermostatScheduleDao.getSavedThermostatSchedulesByAccountId(accountInfoFragment.getAccountId(), type);
        modelMap.addAttribute("schedules", schedules);

        return "operator/operatorThermostat/schedule/savedSchedules.jsp";
    }
	
	// VIEW SAVED SCHEDULED
	@RequestMapping(params="view")
	public String viewSavedSchedule(int thermostatId, Integer scheduleId, ModelMap modelMap, AccountInfoFragment accountInfoFragment) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		modelMap.addAttribute("scheduleId", scheduleId);
		modelMap.addAttribute("thermostatIds", thermostatId);
		return "redirect:view";
	}

	// DELETE SAVED SCHEDULE
	@RequestMapping(params="delete")
    public String viewSavedSchedule(int thermostatId, 
    								int scheduleId, 
    								LiteYukonUser user, 
    								ModelMap modelMap, 
    								AccountInfoFragment accountInfoFragment,
    								FlashScope flashScope) {
    	
		accountCheckerService.checkInventory(user, thermostatId);
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		modelMap.addAttribute("thermostatId", thermostatId);
		
		ThermostatSchedule schedule = thermostatScheduleDao.getThermostatScheduleById(scheduleId, accountInfoFragment.getAccountId());
    	thermostatScheduleDao.delete(scheduleId);
    	
    	MessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatSavedSchedules.scheduleDeleted", schedule.getName());
    	flashScope.setConfirm(Collections.singletonList(message));
    	
    	return "redirect:savedSchedules";
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
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
	
	@Autowired
	public void setThermostatService(ThermostatService thermostatService) {
		this.thermostatService = thermostatService;
	}
	
	@Autowired
	public void setThermostatScheduleDao(ThermostatScheduleDao thermostatScheduleDao) {
		this.thermostatScheduleDao = thermostatScheduleDao;
	}
	
	@Autowired
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
	
	@Autowired
	public void setOperatorThermostatHelper(OperatorThermostatHelper operatorThermostatHelper) {
		this.operatorThermostatHelper = operatorThermostatHelper;
	}
	
	@Autowired
	public void setAccountCheckerService(AccountCheckerService accountCheckerService) {
		this.accountCheckerService = accountCheckerService;
	}
}

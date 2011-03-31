package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
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
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
@RequestMapping(value = "/operator/thermostatSchedule/*")
public class OperatorThermostatScheduleController {
    private AccountEventLogService accountEventLogService;
	private InventoryDao inventoryDao;
	private CustomerDao customerDao;
	private CustomerAccountDao customerAccountDao;
	private ThermostatService thermostatService;
	private OperatorThermostatHelper operatorThermostatHelper;
	private AccountCheckerService accountCheckerService;
	private AccountThermostatScheduleDao accountThermostatScheduleDao;
	private RolePropertyDao rolePropertyDao;
	private ThermostatEventHistoryDao thermostatEventHistoryDao;
	
	// VIEW
	@RequestMapping
    public String view(String thermostatIds,
					            Integer scheduleId, 
					            String canceledAction,
					            Boolean createAsNew,
					            YukonUserContext yukonUserContext, 
					            ModelMap modelMap, 
					            AccountInfoFragment accountInfoFragment,
					            FlashScope flashScope) {

		// thermostatIdsList
		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());

		int thermostatId = thermostatIdsList.get(0);
		Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
		SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.getByHardwareType(thermostat.getType());
		
		// AccountThermostatSchedule and EC default AccountThermostatSchedule
        AccountThermostatSchedule schedule = null;
        AccountThermostatSchedule defaultSchedule = null;
        int accountId = customerAccount.getAccountId();
        
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
        
        String useScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(schedule, thermostatIdsList.size() == 1 ? thermostat.getLabel() : null, yukonUserContext);
        schedule.setScheduleName(useScheduleName);
        
        defaultSchedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByAccountAndType(accountId, schedulableThermostatType);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, yukonUserContext);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        
        // numberOfThermostatsUsingSchedule
        List<Integer> thermostatIdsUsingSchedule = accountThermostatScheduleDao.getThermostatIdsUsingSchedule(schedule.getAccountThermostatScheduleId());
        modelMap.addAttribute("totalNumberOfThermostatsUsingSchedule", thermostatIdsUsingSchedule.size());
        int selectedNumberOfThermostatsUsingSchedule = 0;
        for (int id : thermostatIdsList) {
        	if (thermostatIdsUsingSchedule.contains(id)) {
        		selectedNumberOfThermostatsUsingSchedule++;
        	}
        }
        modelMap.addAttribute("selectedNumberOfThermostatsUsingSchedule", selectedNumberOfThermostatsUsingSchedule);
        
        // schedule52Enabled
    	boolean schedule52Enabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2, yukonUserContext.getYukonUser());
    	modelMap.addAttribute("schedule52Enabled", schedule52Enabled);
    	
    	// adjusted scheduleMode
    	ThermostatScheduleMode scheduleMode = operatorThermostatHelper.getAdjustedScheduleMode(schedule, yukonUserContext.getYukonUser());
    	modelMap.addAttribute("scheduleMode", scheduleMode);
    	
        // temperatureUnit
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        modelMap.addAttribute("temperatureUnit", temperatureUnit);

        // scheduleJSON
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = operatorThermostatHelper.getJSONForSchedule(schedule, isFahrenheit);
        modelMap.addAttribute("scheduleJSONString", scheduleJSON.toString());
        modelMap.addAttribute("schedule", schedule);

        JSONObject defaultFahrenheitScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, true);
        modelMap.addAttribute("defaultFahrenheitScheduleJSON", defaultFahrenheitScheduleJSON.toString());
        JSONObject defaultCelsiusScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, false);
        modelMap.addAttribute("defaultCelsiusScheduleJSON", defaultCelsiusScheduleJSON.toString());
        
        ThermostatScheduleMode defaultScheduleMode = operatorThermostatHelper.getAdjustedScheduleMode(defaultSchedule, yukonUserContext.getYukonUser());
        modelMap.addAttribute("defaultScheduleMode", defaultScheduleMode);
        
        // if arriving at the view page due to a canceled send/save operation
        if (StringUtils.isNotBlank(canceledAction)) {
        	MessageSourceResolvable cancelMessage = new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatScheduleConfirm.cancelText." + canceledAction);
        	flashScope.setWarning(cancelMessage);
        }

        return "operator/operatorThermostat/schedule/view.jsp";
    }
	
	// CONFIRM OR SAVE 
	@RequestMapping
    public String confirmOrSave(String thermostatIds,
					    		String type, String scheduleMode, String temperatureUnit, Integer scheduleId,
					    		String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
					    		String schedules,
					    		HttpServletRequest request, ModelMap modelMap,
					    		AccountInfoFragment accountInfoFragment) {
	    
		operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		
		if(StringUtils.isBlank(scheduleName)){
            modelMap.addAttribute("canceledAction", "emptyName");
            return "redirect:view";
        }
		
    	modelMap.addAttribute("saveAction", saveAction);
    	modelMap.addAttribute("scheduleId", scheduleId);
    	modelMap.addAttribute("type", type);
    	modelMap.addAttribute("schedules", schedules);
    	modelMap.addAttribute("scheduleMode", scheduleMode);
    	modelMap.addAttribute("temperatureUnit", temperatureUnit);
    	modelMap.addAttribute("scheduleId", scheduleId);
    	modelMap.addAttribute("scheduleName", scheduleName);
    	
    	return "redirect:confirm";
    }
	
	// CONFIRM
	@RequestMapping
    public String confirm(String thermostatIds, String type, String scheduleMode, String temperatureUnit, 
                          Integer scheduleId, String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
					      String schedules, HttpServletRequest request, ModelMap modelMap, AccountInfoFragment accountInfoFragment) {
		
		operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
        
    	// Create the confirm schedule text
    	boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
    	ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);
    	
    	// id
        AccountThermostatSchedule ats = new AccountThermostatSchedule();
        ats.setAccountThermostatScheduleId(scheduleId);
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.valueOf(type);
        ats.setThermostatType(schedulableThermostatType);
        
        // Create schedule from submitted JSON string
        List<AccountThermostatScheduleEntry> atsEntries = 
            operatorThermostatHelper.getScheduleEntriesForJSON(schedules, scheduleId, schedulableThermostatType, thermostatScheduleMode, isFahrenheit);
        ats.setScheduleEntries(atsEntries);

        // Build up confirmation display object, containing printable representations of the thermostat schedule entries
        String i18nKey = (isFahrenheit) ? "yukon.dr.operator.thermostatScheduleConfirm.scheduleText.timeCoolHeatFahrenheit" : 
        								  "yukon.dr.operator.thermostatScheduleConfirm.scheduleText.timeCoolHeatCelsius";
        List<ThermostatScheduleDisplay> scheduleDisplays = operatorThermostatHelper.getScheduleDisplays(yukonUserContext, type, thermostatScheduleMode, ats, isFahrenheit, i18nKey);

    	// Pass all of the parameters through to confirm page
        modelMap.addAttribute("confirmationDisplays", scheduleDisplays);
        modelMap.addAttribute("schedules", schedules);
    	modelMap.addAttribute("type", type);
    	modelMap.addAttribute("scheduleMode", scheduleMode);
    	modelMap.addAttribute("temperatureUnit", temperatureUnit);
    	modelMap.addAttribute("scheduleId", scheduleId);
    	modelMap.addAttribute("scheduleName", scheduleName);
    	modelMap.addAttribute("saveAction", saveAction);
    	
    	return "operator/operatorThermostat/schedule/confirm.jsp";
    }
	
	// SAVE
	@RequestMapping
    public String save(String thermostatIds, String type, String scheduleMode, String temperatureUnit, 
                       Integer scheduleId,String scheduleName, String saveAction, YukonUserContext userContext,
					   String schedules, HttpServletRequest request, ModelMap modelMap, FlashScope flashScope,
					   AccountInfoFragment accountInfoFragment) {

	    List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
	    CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());

	    String oldScheduleName = "";
	    if(scheduleId >= 0) {
	        AccountThermostatSchedule oldAts = accountThermostatScheduleDao.getById(scheduleId);
	        oldScheduleName = oldAts.getScheduleName();
	    }
	    
	    // Log thermostat schedule save attempt
	    for (int thermostatId : thermostatIdsList) {
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
	        
            accountEventLogService.thermostatScheduleSavingAttemptedByOperator(userContext.getYukonUser(),
                                                                               accountInfoFragment.getAccountNumber(),
                                                                               thermostat.getSerialNumber(),
                                                                               oldScheduleName);
        }
	    
		ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);
		
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
        
        // id
        AccountThermostatSchedule ats = new AccountThermostatSchedule();
        ats.setAccountThermostatScheduleId(scheduleId);
        ats.setAccountId(customerAccount.getAccountId());
        
        // schedulableThermostatType
        SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.valueOf(type);
        ats.setThermostatType(schedulableThermostatType);
        
        // Create schedule from submitted JSON string
        List<AccountThermostatScheduleEntry> atsEntries = 
            operatorThermostatHelper.getScheduleEntriesForJSON(schedules, scheduleId, schedulableThermostatType, thermostatScheduleMode, isFahrenheit);
        ats.setScheduleEntries(atsEntries);
        
        // thermostatScheduleMode
        ats.setThermostatScheduleMode(thermostatScheduleMode);
        
        // COMMERCIAL_EXPRESSSTAT setToTwoTimeTemps
        if (schedulableThermostatType.getPeriodStyle() == ThermostatSchedulePeriodStyle.TWO_TIMES) {
        	operatorThermostatHelper.setToTwoTimeTemps(ats);
        }
    	
    	// scheduleName
    	ats.setScheduleName(scheduleName);

        // SAVE
        accountThermostatScheduleDao.save(ats);
        accountThermostatScheduleDao.mapThermostatsToSchedule(thermostatIdsList, ats.getAccountThermostatScheduleId());
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.CONSUMER_SAVE_SCHEDULE_SUCCESS;

        // Log schedule name change
        if (!oldScheduleName.equalsIgnoreCase(ats.getScheduleName())) {
            accountEventLogService.thermostatScheduleNameChanged(userContext.getYukonUser(),
                                                                 oldScheduleName,
                                                                 ats.getScheduleName());
        }
        
        // SEND
        if (sendAndSave) {
        	
        	boolean failed = false;
        	for (int thermostatId : thermostatIdsList) {
        		
        		for (TimeOfWeek timeOfWeek : thermostatScheduleMode.getAssociatedTimeOfWeeks()) {
        			
        			message = thermostatService.sendSchedule(customerAccount, ats, thermostatId, timeOfWeek, thermostatScheduleMode, userContext);
                	
            		if (message.isFailed()) {
                        failed = true;
                    }
        		}
        		
        		if(!failed) {
        		    //Log schedule send to thermostat history
        		    thermostatEventHistoryDao.logScheduleEvent(userContext.getYukonUser(), thermostatId, ats.getAccountThermostatScheduleId(), thermostatScheduleMode);
        		}
        	}
        	
        	if (failed && thermostatIdsList.size() > 1) {
                message = ThermostatScheduleUpdateResult.CONSUMER_MULTIPLE_ERROR;
            }
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
					            AccountInfoFragment accountInfoFragment,
					            YukonUserContext yukonUserContext) {

        accountCheckerService.checkInventory(user, thermostatId);
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);

		Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
		SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
		modelMap.addAttribute("thermostatId", thermostatId);
		
		AccountThermostatSchedule thermostatCurrentSchedule = accountThermostatScheduleDao.findByInventoryId(thermostatId);
		if (thermostatCurrentSchedule != null) {
			modelMap.addAttribute("currentScheduleId", thermostatCurrentSchedule.getAccountThermostatScheduleId());
		}
		
		List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllSchedulesForAccountByType(accountInfoFragment.getAccountId(), type);
        modelMap.addAttribute("schedules", schedules);

        return "operator/operatorThermostat/schedule/savedSchedules.jsp";
    }
	
	// VIEW SAVED SCHEDULED
	@RequestMapping(params="view", value="viewSavedSchedule")
	public String viewSavedSchedule(int thermostatId, Integer scheduleId, ModelMap modelMap, AccountInfoFragment accountInfoFragment) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		modelMap.addAttribute("scheduleId", scheduleId);
		modelMap.addAttribute("thermostatIds", thermostatId);
		return "redirect:view";
	}
	
	// CREATE NEW SAVED SCHEDULE
	@RequestMapping(params="createNew", value="viewSavedSchedule")
    public String createNewSchedule(int thermostatId, 
    								LiteYukonUser user, 
    								ModelMap modelMap, 
    								AccountInfoFragment accountInfoFragment,
    								FlashScope flashScope) {
    	
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		modelMap.addAttribute("createAsNew", true);
		modelMap.addAttribute("thermostatIds", thermostatId);
		return "redirect:view";
    }

	// DELETE SAVED SCHEDULE
	@RequestMapping(params="delete", value="viewSavedSchedule")
    public String viewSavedSchedule(int thermostatId, 
    								int scheduleId, 
    								LiteYukonUser user, 
    								ModelMap modelMap, 
    								AccountInfoFragment accountInfoFragment,
    								FlashScope flashScope) {
    	
		accountCheckerService.checkInventory(user, thermostatId);
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		modelMap.addAttribute("thermostatId", thermostatId);
		
		AccountThermostatSchedule schedule = accountThermostatScheduleDao.getById(scheduleId);
		accountThermostatScheduleDao.deleteById(scheduleId);
		
		MessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatSavedSchedules.scheduleDeleted", schedule.getScheduleName());
		flashScope.setConfirm(Collections.singletonList(message));
    	
    	return "redirect:savedSchedules";
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
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
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
	public void setAccountCheckerService(AccountCheckerService accountCheckerService) {
		this.accountCheckerService = accountCheckerService;
	}
	
	@Autowired
	public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
		this.accountThermostatScheduleDao = accountThermostatScheduleDao;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setThermostatEventHistoryDao(ThermostatEventHistoryDao thermostatEventHistoryDao) {
	    this.thermostatEventHistoryDao = thermostatEventHistoryDao;
	}
}

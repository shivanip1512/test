package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleDisplay;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
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
	
	// VIEW
	@RequestMapping
    public String view(String thermostatIds,
					            Integer scheduleId, 
					            String canceledAction,
					            Boolean createAsNew,
					            YukonUserContext context, 
					            ModelMap model, 
					            AccountInfoFragment fragment,
					            FlashScope flash) {

		// thermostatIdsList
		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
		CustomerAccount customerAccount = customerAccountDao.getById(fragment.getAccountId());

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
        
        String useScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(schedule, thermostatIdsList.size() == 1 ? thermostat.getLabel() : null, context);
        schedule.setScheduleName(useScheduleName);
        
        defaultSchedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByAccountAndType(accountId, schedulableThermostatType);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, context);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        
        // numberOfThermostatsUsingSchedule
        List<Integer> thermostatIdsUsingSchedule = accountThermostatScheduleDao.getThermostatIdsUsingSchedule(schedule.getAccountThermostatScheduleId());
        model.addAttribute("totalNumberOfThermostatsUsingSchedule", thermostatIdsUsingSchedule.size());
        int selectedNumberOfThermostatsUsingSchedule = 0;
        for (int id : thermostatIdsList) {
        	if (thermostatIdsUsingSchedule.contains(id)) {
        		selectedNumberOfThermostatsUsingSchedule++;
        	}
        }
        model.addAttribute("selectedNumberOfThermostatsUsingSchedule", selectedNumberOfThermostatsUsingSchedule);
        
        // schedule52Enabled
    	boolean schedule52Enabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_THERMOSTAT_SCHEDULE_5_2, context.getYukonUser());
    	model.addAttribute("schedule52Enabled", schedule52Enabled);
    	
    	// adjusted scheduleMode
    	ThermostatScheduleMode scheduleMode = operatorThermostatHelper.getAdjustedScheduleMode(schedule, context.getYukonUser());
    	model.addAttribute("scheduleMode", scheduleMode);
    	
        // temperatureUnit
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        model.addAttribute("temperatureUnit", temperatureUnit);

        // scheduleJSON
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = operatorThermostatHelper.getJSONForSchedule(schedule, isFahrenheit);
        model.addAttribute("scheduleJSONString", scheduleJSON.toString());
        model.addAttribute("schedule", schedule);

        JSONObject defaultFahrenheitScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, true);
        model.addAttribute("defaultFahrenheitScheduleJSON", defaultFahrenheitScheduleJSON.toString());
        JSONObject defaultCelsiusScheduleJSON = operatorThermostatHelper.getJSONForSchedule(defaultSchedule, false);
        model.addAttribute("defaultCelsiusScheduleJSON", defaultCelsiusScheduleJSON.toString());
        
        ThermostatScheduleMode defaultScheduleMode = operatorThermostatHelper.getAdjustedScheduleMode(defaultSchedule, context.getYukonUser());
        model.addAttribute("defaultScheduleMode", defaultScheduleMode);
        
        // if arriving at the view page due to a canceled send/save operation
        if (StringUtils.isNotBlank(canceledAction)) {
        	MessageSourceResolvable cancelMessage = new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatScheduleConfirm.cancelText." + canceledAction);
        	flash.setWarning(cancelMessage);
        }

        return "operator/operatorThermostat/schedule/view.jsp";
    }
	
	// CONFIRM OR SAVE 
	@RequestMapping
    public String confirmOrSave(String thermostatIds,
					    		String type, 
					    		String scheduleMode, 
					    		String temperatureUnit, 
					    		Integer scheduleId,
					    		String scheduleName, 
					    		String saveAction, 
					    		String schedules,
					    		ModelMap model,
					    		AccountInfoFragment fragment) {
	    
		operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
		
		if(StringUtils.isBlank(scheduleName)){
            model.addAttribute("canceledAction", "emptyName");
            return "redirect:view";
        }
		
    	model.addAttribute("saveAction", saveAction);
    	model.addAttribute("scheduleId", scheduleId);
    	model.addAttribute("type", type);
    	model.addAttribute("schedules", schedules);
    	model.addAttribute("scheduleMode", scheduleMode);
    	model.addAttribute("temperatureUnit", temperatureUnit);
    	model.addAttribute("scheduleId", scheduleId);
    	model.addAttribute("scheduleName", scheduleName);
    	
    	return "redirect:confirm";
    }
	
	// CONFIRM
	@RequestMapping
    public String confirm(String thermostatIds, 
                          String type, 
                          String scheduleMode, 
                          String temperatureUnit, 
                          Integer scheduleId, 
                          String scheduleName, 
                          String saveAction, 
                          YukonUserContext context, 
					      String schedules, ModelMap model, 
					      AccountInfoFragment fragment) {
		
		operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
        
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
        List<ThermostatScheduleDisplay> scheduleDisplays = operatorThermostatHelper.getScheduleDisplays(context, type, thermostatScheduleMode, ats, isFahrenheit, i18nKey);

    	// Pass all of the parameters through to confirm page
        model.addAttribute("confirmationDisplays", scheduleDisplays);
        model.addAttribute("schedules", schedules);
    	model.addAttribute("type", type);
    	model.addAttribute("scheduleMode", scheduleMode);
    	model.addAttribute("temperatureUnit", temperatureUnit);
    	model.addAttribute("scheduleId", scheduleId);
    	model.addAttribute("scheduleName", scheduleName);
    	model.addAttribute("saveAction", saveAction);
    	
    	return "operator/operatorThermostat/schedule/confirm.jsp";
    }
	
	// SAVE
	@RequestMapping
    public String save(@RequestParam(value="thermostatIds", required=true) String thermostatIds,
                       @RequestParam(value="temperatureUnit", required=true) String temperatureUnit,
                       @RequestParam(value="schedules", required=true) String scheduleString,
                       YukonUserContext yukonUserContext,  
					   ModelMap model, 
					   FlashScope flash, 
					   AccountInfoFragment fragment) {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        List<Integer> thermostatIdList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
        CustomerAccount account = customerAccountDao.getById(fragment.getAccountId());
        
        JSONObject scheduleJSON = JSONObject.fromObject(scheduleString);
        
        AccountThermostatSchedule ats = operatorThermostatHelper.JSONtoAccountThermostatSchedule(scheduleJSON);
        ats.setAccountId(account.getAccountId());
        
        String oldScheduleName = "";
        if(ats.getAccountThermostatScheduleId() >= 0) {
            AccountThermostatSchedule oldAts = accountThermostatScheduleDao.getById(ats.getAccountThermostatScheduleId());
            oldScheduleName = oldAts.getScheduleName();
        }
        
        // Log thermostat schedule save attempt
        for (int thermostatId : thermostatIdList) {
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            accountEventLogService.thermostatScheduleSavingAttemptedByOperator(user, fragment.getAccountNumber(), thermostat.getSerialNumber(), oldScheduleName);
        }
        
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if (StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(account.getCustomerId(), escapedTempUnit);
        }

        //ensure this user can work with this schedule and thermostat
        accountCheckerService.checkThermostatSchedule(user, ats.getAccountThermostatScheduleId());
        accountCheckerService.checkInventory(user, thermostatIdList);
        
        // Save the Schedule
        accountThermostatScheduleDao.save(ats);
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;

        // Log schedule name change
        if (oldScheduleName.equalsIgnoreCase(ats.getScheduleName())) {
            accountEventLogService.thermostatScheduleNameChanged(user, oldScheduleName, ats.getScheduleName());
        }

        //flash messages
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.dr.consumer.scheduleUpdate.result.CONSUMER_" + message, ats.getScheduleName()));

        model.addAttribute("thermostatId", thermostatIds);
        return "redirect:savedSchedules";
    }
	
	
	//SEND
	@RequestMapping(value = "send", method = RequestMethod.POST)
    public String sendSchedule(HttpServletRequest request,
                               @RequestParam(value="thermostatIds", required=true) String thermostatIds,
                               @RequestParam(value="scheduleId", required=true) Integer scheduleId,
                               @RequestParam("temperatureUnit") String temperatureUnit,
                               YukonUserContext yukonUserContext,
                               FlashScope flashScope,
                               AccountInfoFragment fragment,
                               ModelMap map) throws Exception {
	    
	    List<Integer> thermostatIdList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, map);
        CustomerAccount account = customerAccountDao.getById(fragment.getAccountId());
        
        // retrieve the schedule
        AccountThermostatSchedule ats = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId, account.getAccountId());
        ThermostatScheduleMode thermostatScheduleMode = ats.getThermostatScheduleMode();
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;
        
        //associate the thermostats with this schedule
        accountThermostatScheduleDao.mapThermostatsToSchedule(thermostatIdList, ats.getAccountThermostatScheduleId());
        
        message = thermostatService.sendSchedule(account, ats, thermostatIdList, thermostatScheduleMode, yukonUserContext.getYukonUser());
        
        if (message.isFailed()) {
            //Log schedule send to thermostat history
            if(thermostatIdList.size() > 1) {
                message = ThermostatScheduleUpdateResult.MULTIPLE_ERROR;
            }else{
                message = ThermostatScheduleUpdateResult.SEND_SCHEDULE_ERROR;
            }
            flashScope.setError(new YukonMessageSourceResolvable("yukon.dr.consumer.scheduleUpdate.result.CONSUMER_" + message));
        }else{
            message = ThermostatScheduleUpdateResult.SEND_SCHEDULE_SUCCESS;
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.dr.consumer.scheduleUpdate.result.CONSUMER_" + message, ats.getScheduleName()));
        }
        
        //change the default temperature unit for the user based on what the interface was showing
        //when they submitted the form.  hmm... seems like an odd thing to do
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(account.getCustomerId(), escapedTempUnit);
        }
        
        map.addAttribute("thermostatId", thermostatIdList);
        return "redirect:savedSchedules";
    }
	
	// HINTS
	@RequestMapping
    public String hints(String thermostatIds, AccountInfoFragment fragment, ModelMap model) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
		model.addAttribute("thermostatIds", thermostatIds);
		
		return "operator/operatorThermostat/schedule/hints.jsp";
	}
	
	// SAVED SCHEDULES
	@RequestMapping
    public String savedSchedules(@RequestParam(value="thermostatIds", required=true) List<Integer> thermostatIds,
                                 YukonUserContext userContext,
                                 ModelMap model,
                                 AccountInfoFragment fragment) {

        accountCheckerService.checkInventory(userContext.getYukonUser(), thermostatIds);
		AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIds.get(0));
        List<SchedulableThermostatType> types = operatorThermostatHelper.getCompatibleSchedulableThermostatTypes(thermostat);
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllAllowedSchedulesAndEntriesForAccountByTypes(fragment.getAccountId(), types, userContext.getYukonUser());
        
        CustomerAccount customerAccount = customerAccountDao.getById(fragment.getAccountId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        model.addAttribute("temperatureUnit", temperatureUnit);
        
        // current schedule is only relevant when operating in the context of 1 Thermostat
        AccountThermostatSchedule thermostatCurrentSchedule = accountThermostatScheduleDao.findByInventoryId(thermostat.getId());
        if (thermostatCurrentSchedule != null) {
            model.addAttribute("currentScheduleId", thermostatCurrentSchedule.getAccountThermostatScheduleId());

            //put the current schedule first
            int index = -1;
            for(AccountThermostatSchedule ats : schedules){
                if (ats.getAccountThermostatScheduleId() == thermostatCurrentSchedule.getAccountThermostatScheduleId()){
                    index = schedules.indexOf(ats);
                    break;
                }
            }
            
            if(index > -1){
                schedules.add(0, schedules.remove(index));
            }
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("thermostat", thermostat);
        model.addAttribute("thermostatType", type);
        model.addAttribute("type", type);
        model.addAttribute("temperatureUnit", temperatureUnit);
        model.addAttribute("celcius_char", CtiUtilities.CELSIUS_CHARACTER);
        model.addAttribute("fahrenheit_char", CtiUtilities.FAHRENHEIT_CHARACTER);
        model.addAttribute("thermostatIds", thermostatIds);
        model.addAttribute("thermostatId", thermostatIds.get(0));
        
        //default schedule(s)
        AccountThermostatSchedule defaultSchedule  = thermostatService.getAccountThermostatScheduleTemplate(customerAccount.getAccountId(), type);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, userContext);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        
        List<ThermostatScheduleMode> modes = operatorThermostatHelper.getAllowedModesForUserAndType(userContext.getYukonUser(), type);
        List<AccountThermostatSchedule> defaultSchedules = new ArrayList<AccountThermostatSchedule>();
        for(ThermostatScheduleMode mode : modes){
            if(defaultSchedule.getThermostatScheduleMode() == mode){
                defaultSchedules.add(defaultSchedule);
            }else{
                //create a schedule that looks similar to the default
                AccountThermostatSchedule defaultAts = new AccountThermostatSchedule();
                defaultAts.setAccountId(customerAccount.getAccountId());
                defaultAts.setAccountThermostatScheduleId(-1);
                defaultAts.setScheduleName(useDefaultScheduleName);
                defaultAts.setThermostatScheduleMode(mode);
                defaultAts.setThermostatType(SchedulableThermostatType.getByHardwareType(thermostat.getType()));
                thermostatService.addMissingScheduleEntries(defaultAts);
                
                defaultSchedules.add(defaultAts);
            }
        }        
        model.addAttribute("defaultSchedules", defaultSchedules);
        model.addAttribute("allowedModes", modes);
        
        return "operator/operatorThermostat/schedule/savedSchedules.jsp";
    }
	
	// VIEW SAVED SCHEDULED
	@RequestMapping(params="view", value="viewSavedSchedule")
	public String viewSavedSchedule(int thermostatId, Integer scheduleId, ModelMap model, AccountInfoFragment fragemtn) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(fragemtn, model);
		
		model.addAttribute("scheduleId", scheduleId);
		model.addAttribute("thermostatIds", thermostatId);
		return "redirect:view";
	}
	
	// CREATE NEW SAVED SCHEDULE
	@RequestMapping(params="createNew", value="viewSavedSchedule")
    public String createNewSchedule(int thermostatId, ModelMap model, AccountInfoFragment fragment) {
    	
		AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
		
		model.addAttribute("createAsNew", true);
		model.addAttribute("thermostatIds", thermostatId);
		return "redirect:view";
    }

	// DELETE SAVED SCHEDULE
	@RequestMapping(value = "delete", method = RequestMethod.POST)
    public String viewSavedSchedule(@RequestParam(value="thermostatIds", required=true) String thermostatIds,
                                    @RequestParam(value="scheduleId", required=true) Integer scheduleId,
                                    LiteYukonUser user,
                                    ModelMap model,
                                    AccountInfoFragment fragment,
                                    FlashScope flash) {
    	
	    List<Integer> thermostatIdList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
	    
		accountCheckerService.checkInventory(user, thermostatIdList);
		AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
		
		AccountThermostatSchedule schedule = accountThermostatScheduleDao.getById(scheduleId);
		accountThermostatScheduleDao.deleteById(scheduleId);
		
		MessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatSavedSchedules.scheduleDeleted", schedule.getScheduleName());
		flash.setConfirm(Collections.singletonList(message));
    	
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
}
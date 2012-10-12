package com.cannontech.web.stars.dr.operator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.cannontech.web.stars.dr.operator.validator.AccountThermostatScheduleValidator;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
@RequestMapping(value = "/operator/thermostatSchedule/*")
public class OperatorThermostatScheduleController {
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ThermostatService thermostatService;
    @Autowired private OperatorThermostatHelper operatorThermostatHelper;
    @Autowired private AccountCheckerService accountCheckerService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	// SAVE - ajaxy
	//Try to save the schedule
    //@returns JSONObject containing success or error messages
    @RequestMapping(value = "save", method = {RequestMethod.POST, RequestMethod.HEAD}, headers = "x-requested-with=XMLHttpRequest")
    public void save(HttpServletResponse response,
                       @RequestParam(value="thermostatIds", required=true) String thermostatIds,
                       @RequestParam(value="schedules", required=true) String scheduleString,
                       YukonUserContext yukonUserContext,  
					   ModelMap model, 
					   FlashScope flash, 
					   AccountInfoFragment fragment) throws IOException {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        List<Integer> thermostatIdList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
        CustomerAccount account = customerAccountDao.getById(fragment.getAccountId());
        
        JSONObject returnJSON = new JSONObject();
        JSONObject scheduleJSON = JSONObject.fromObject(scheduleString);
        
        AccountThermostatSchedule ats = operatorThermostatHelper.JSONtoAccountThermostatSchedule(scheduleJSON);
        ats.setAccountId(account.getAccountId());
        
        //validate the schedule as posted
        DataBinder binder = new DataBinder(ats);
        AccountThermostatScheduleValidator accountThermostatScheduleValidator =
            new AccountThermostatScheduleValidator(accountThermostatScheduleDao, messageSourceResolver.getMessageSourceAccessor(yukonUserContext));
        binder.setValidator(accountThermostatScheduleValidator);
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();
        
        if(bindingResult.hasErrors()){
            JSONObject errorJSON = new JSONObject();
            for (Object object : bindingResult.getAllErrors()) {
                if(object instanceof FieldError) {
                    FieldError fieldError = (FieldError) object;
                    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
                    errorJSON.put(fieldError.getField(), messageSourceAccessor.getMessage(fieldError.getCode(), fieldError.getArguments()));
                }
            }
            
            returnJSON.put("errors", errorJSON);
            response.setStatus(HttpServletResponse.SC_CONFLICT);    //what is a 409 you ask? -> http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
        }else{
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
            
            //ensure this user can work with this schedule and thermostat
            accountCheckerService.checkThermostatSchedule(user, ats.getAccountThermostatScheduleId());
            accountCheckerService.checkInventory(user, thermostatIdList);
            
            // Save the Schedule
            accountThermostatScheduleDao.save(ats);
            ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;
            
            // Log schedule name change
            if (!oldScheduleName.equals(ats.getScheduleName())) {
                accountEventLogService.thermostatScheduleNameChanged(user, oldScheduleName, ats.getScheduleName());
            }
            
            //flash messages
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostat.single.send." + message, ats.getScheduleName()));
        }
        
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(returnJSON.toString());
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
        
        
        ThermostatScheduleUpdateResult message = thermostatService.sendSchedule(account, ats, thermostatIdList, thermostatScheduleMode, yukonUserContext.getYukonUser());
        
        String pageName = "single.send.";
        if(thermostatIdList.size() > 1){
            pageName = "multiple.send.";
        }
        
        if (message.isFailed()) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostat." + pageName + message, ats.getScheduleName()));
        }else{
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostat." + pageName + message, ats.getScheduleName()));
            //associate the thermostats with this schedule
            accountThermostatScheduleDao.mapThermostatsToSchedule(thermostatIdList, ats.getAccountThermostatScheduleId());
        }
        
        map.addAttribute("thermostatId", thermostatIdList);
        return "redirect:savedSchedules";
    }
	
	// SAVED SCHEDULES
	@RequestMapping
    public String savedSchedules(@RequestParam(value="thermostatIds", required=true) String thermostatIds,
                                 YukonUserContext userContext,
                                 ModelMap model,
                                 AccountInfoFragment fragment) throws IllegalArgumentException {

		AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
		List<Integer> thermostatIdList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
		accountCheckerService.checkInventory(userContext.getYukonUser(), thermostatIdList);

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIdList.get(0));
        List<SchedulableThermostatType> compatibleTypes = new ArrayList<SchedulableThermostatType>(thermostat.getCompatibleSchedulableThermostatTypes());
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllAllowedSchedulesAndEntriesForAccountByTypes(fragment.getAccountId(), compatibleTypes);
        
        CustomerAccount customerAccount = customerAccountDao.getById(fragment.getAccountId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        model.addAttribute("temperatureUnit", temperatureUnit);
        
        // current schedule is only relevant when operating in the context of 1 Thermostat
        if(thermostatIdList.size() == 1){
            AccountThermostatSchedule thermostatCurrentSchedule = accountThermostatScheduleDao.findByInventoryId(thermostat.getId());
            if (thermostatCurrentSchedule != null) {
                for(AccountThermostatSchedule ats : schedules){
                    if (ats.getAccountThermostatScheduleId() == thermostatCurrentSchedule.getAccountThermostatScheduleId()){
                        //only add the 'currentSchedule' if it is included in the allowed schedule
                        model.addAttribute("currentSchedule", schedules.remove(schedules.indexOf(ats)));
                        break;
                    }
                }
            }
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("thermostat", thermostat);
        model.addAttribute("thermostatType", type);
        model.addAttribute("type", type);
        model.addAttribute("temperatureUnit", temperatureUnit);
        model.addAttribute("thermostatIds", thermostatIds);
        model.addAttribute("thermostatId", thermostatIdList.get(0));
        
        //default schedule(s)
        AccountThermostatSchedule defaultSchedule  = thermostatService.getAccountThermostatScheduleTemplate(customerAccount.getAccountId(), type);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, userContext);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        
        Set<ThermostatScheduleMode> modes = type.getAllowedModes(thermostatService.getAllowedThermostatScheduleModesByAccountId(fragment.getAccountId()));
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
                thermostatService.addMissingScheduleEntries(defaultAts, defaultSchedule);
                
                defaultSchedules.add(defaultAts);
            }
        }        
        model.addAttribute("defaultSchedules", defaultSchedules);
        model.addAttribute("allowedModes", modes);
        
        if(thermostatIdList.size() > 1){
            model.addAttribute("pageName", "multiple");
        }else{
            //the serial number is the same as the hardwareDto.getDisplayName() that appears on the parent page.
            model.addAttribute("pageName", "single");
            model.addAttribute("inventoryId", thermostat.getId());
            model.addAttribute("displayName", thermostat.getSerialNumber());
        }
        
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
    public String deleteSchedule(@RequestParam(value="thermostatIds", required=true) String thermostatIds,
                                 @RequestParam(value="scheduleId", required=true) Integer scheduleId,
                                 LiteYukonUser user, ModelMap model, AccountInfoFragment fragment,
                                 FlashScope flash) {
    	
	    List<Integer> thermostatIdList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, fragment, model);
	    
		accountCheckerService.checkInventory(user, thermostatIdList);
		AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);

		AccountThermostatSchedule schedule = accountThermostatScheduleDao.getById(scheduleId);
        accountEventLogService.thermostatScheduleDeleteAttemptedByOperator(user,
            fragment.getAccountNumber(), schedule.getScheduleName());
		accountThermostatScheduleDao.deleteById(scheduleId);
		
		MessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostat.schedules.scheduleDeleted", schedule.getScheduleName());
		flash.setConfirm(Collections.singletonList(message));
    	
    	return "redirect:savedSchedules";
    }
	
	//ajax method to update the temperature preferences of the associated consumer
    @RequestMapping(value = "updateTemperaturePreference",
                    method = {RequestMethod.POST, RequestMethod.HEAD},     
                    headers = "x-requested-with=XMLHttpRequest")
    public void updateTemperaturePreference(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestParam("temperatureUnit") String temperatureUnit,
                                            LiteYukonUser user,
                                            FlashScope flashScope,
                                            AccountInfoFragment fragment,
                                            ModelMap map) throws NotAuthorizedException, WebClientException, TransactionException, IOException, IllegalArgumentException {
        
        JSONObject returnJSON = new JSONObject();
        
        CustomerAccount customerAccount = customerAccountDao.getById(fragment.getAccountId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        
        customerDao.setTemperatureUnit(customerAccount.getCustomerId(), temperatureUnit);
        
        returnJSON.put("temperatureUnit", customer.getTemperatureUnit());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(returnJSON.toString());
    }
}
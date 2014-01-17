package com.cannontech.web.stars.dr.consumer.thermostat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.cannontech.web.stars.dr.operator.validator.AccountThermostatScheduleValidator;

/**
 * Controller for Consumer-side Thermostat schedule operations
 */
@RequestMapping("/consumer/thermostat/schedule/*")
@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatScheduleController extends AbstractThermostatController {
    
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private OperatorThermostatHelper operatorThermostatHelper;
    @Autowired private ThermostatEventHistoryDao thermostatEventHistoryDao;
    @Autowired private ThermostatService thermostatService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    @RequestMapping(value = "send", method = RequestMethod.POST)
    public String sendSchedule(HttpServletRequest request,
                               @ModelAttribute("customerAccount") CustomerAccount account,
                               @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                               @RequestParam("temperatureUnit") String temperatureUnit,
                               Integer scheduleId,
                               YukonUserContext yukonUserContext,
                               FlashScope flashScope,
                               ModelMap map) throws NotAuthorizedException, ServletRequestBindingException, IllegalArgumentException {
        
        if(isCommunicationDisabled(yukonUserContext.getYukonUser())){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        
        // retrieve the schedule
        AccountThermostatSchedule ats = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId, account.getAccountId());
        ThermostatScheduleMode thermostatScheduleMode = ats.getThermostatScheduleMode();
                
        int yukonEnergyCompanyId = yukonEnergyCompanyService.getEnergyCompanyByAccountId(account.getAccountId()).getEnergyCompanyId();

        ThermostatScheduleUpdateResult message = thermostatService.sendSchedule(account, ats, thermostatIds, thermostatScheduleMode, yukonEnergyCompanyId, yukonUserContext.getYukonUser());
    
        String pageName = "single.send.";
        if(thermostatIds.size() > 1){
            pageName = "multiple.send.";
        }
        
        if (message.isFailed()) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.consumer.thermostat." + pageName + message, ats.getScheduleName()));
        }else{
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.consumer.thermostat." + pageName + message, ats.getScheduleName()));
            //associate the thermostats with this schedule
            accountThermostatScheduleDao.mapThermostatsToSchedule(thermostatIds, ats.getAccountThermostatScheduleId());
        }
        
        map.addAttribute("thermostatIds", StringUtils.join(thermostatIds, ","));
        return "redirect:view/saved";
    }

    @RequestMapping(value = "view/saved", 
                    method = RequestMethod.GET)
    public String viewSaved(@ModelAttribute("customerAccount") CustomerAccount account,
                            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                            YukonUserContext yukonUserContext,
                            ModelMap map) throws NotAuthorizedException, ServletRequestBindingException {
        
        if(isCommunicationDisabled(yukonUserContext.getYukonUser())){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }

        accountCheckerService.checkInventory(yukonUserContext.getYukonUser(), thermostatIds);

        //even if there are multiple thermostat ids, they are supposed to be of the same type
        int thermostatId = thermostatIds.get(0);
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        List<Thermostat> thermostats = new ArrayList<Thermostat>();
        for(int statId : thermostatIds) {
            thermostats.add(inventoryDao.getThermostatById(statId));
        }

        List<SchedulableThermostatType> compatibleTypes = new ArrayList<SchedulableThermostatType>(thermostat.getCompatibleSchedulableThermostatTypes());
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
		List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllAllowedSchedulesAndEntriesForAccountByTypes(account.getAccountId(), compatibleTypes);
		String temperatureUnit = customerDao.getCustomerForUser(yukonUserContext.getYukonUser().getUserID()).getTemperatureUnit();
		
		// current schedule is only relevant when operating in the context of 1 Thermostat
		if(thermostatIds.size() == 1){
    		AccountThermostatSchedule thermostatCurrentSchedule = accountThermostatScheduleDao.findByInventoryId(thermostat.getId());
            if (thermostatCurrentSchedule != null) {
                for(AccountThermostatSchedule ats : schedules){
                    if (ats.getAccountThermostatScheduleId() == thermostatCurrentSchedule.getAccountThermostatScheduleId()){
                        //only add the 'currentSchedule' if it is included in the allowed schedule
                        map.addAttribute("currentSchedule", schedules.remove(schedules.indexOf(ats)));
                        break;
                    }
                }
            }
		}

		map.addAttribute("schedules", schedules);
        map.addAttribute("thermostat", thermostat);
        map.addAttribute("thermostats", thermostats);
        map.addAttribute("thermostatType", type);
        map.addAttribute("type", type);
        map.addAttribute("temperatureUnit", temperatureUnit);
        map.addAttribute("celsius_char", TemperatureUnit.CELSIUS.getLetter());
        map.addAttribute("fahrenheit_char", TemperatureUnit.FAHRENHEIT.getLetter());
        
        //default schedule(s)
        AccountThermostatSchedule defaultSchedule  = thermostatService.getAccountThermostatScheduleTemplate(account.getAccountId(), type);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, yukonUserContext);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        
        Set<ThermostatScheduleMode> modes = type.getAllowedModes(thermostatService.getAllowedThermostatScheduleModesByAccountId(account.getAccountId()));
        List<AccountThermostatSchedule> defaultSchedules = new ArrayList<AccountThermostatSchedule>();
        for(ThermostatScheduleMode mode : modes){
            if(defaultSchedule.getThermostatScheduleMode() == mode){
                defaultSchedules.add(defaultSchedule);
            }else{
                //create a schedule that looks similar to the default
                AccountThermostatSchedule defaultAts = new AccountThermostatSchedule();
                defaultAts.setAccountId(account.getAccountId());
                defaultAts.setAccountThermostatScheduleId(-1);
                defaultAts.setScheduleName(useDefaultScheduleName);
                defaultAts.setThermostatScheduleMode(mode);
                defaultAts.setThermostatType(SchedulableThermostatType.getByHardwareType(thermostat.getType()));
                thermostatService.addMissingScheduleEntries(defaultAts, defaultSchedule);
                
                defaultSchedules.add(defaultAts);
            }
        }        
        map.addAttribute("defaultSchedules", defaultSchedules);
        map.addAttribute("allowedModes", modes);

        return "consumer/savedSchedules.jsp";
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(HttpServletRequest request,
                         @ModelAttribute("customerAccount") CustomerAccount account,
                         @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                         Integer scheduleId,
                         LiteYukonUser user,
                         FlashScope flashScope,
                         ModelMap map) throws NotAuthorizedException, ServletRequestBindingException {
    	
        if(isCommunicationDisabled(user)){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        
    	List<Integer> thermostatIdsList = getThermostatIds(request);
    	
    	AccountThermostatSchedule oldAts = accountThermostatScheduleDao.getById(scheduleId);
        String oldScheduleName = oldAts.getScheduleName();

        accountEventLogService.thermostatScheduleDeleteAttempted(user,
                                                                 account.getAccountNumber(),
                                                                 oldAts.getScheduleName(),
                                                                 EventSource.CONSUMER);

        accountCheckerService.checkInventory(user, thermostatIdsList);
    	accountThermostatScheduleDao.deleteById(scheduleId);
    	
    	//flash message
    	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostat.schedules.scheduleDeleted", oldScheduleName));
    	
    	map.addAttribute("thermostatIds", StringUtils.join(thermostatIds, ","));
    	return "redirect:view/saved";

    }
    
    @RequestMapping(value = "history", 
                    method = RequestMethod.GET)
    public String history(HttpServletRequest request,
                         @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                         @RequestParam(defaultValue = "10") int itemsPerPage,
                         @RequestParam(defaultValue = "1", value="page") int currentPage,
                         LiteYukonUser user,
                         ModelMap map) throws NotAuthorizedException, ServletRequestBindingException {

        if(isCommunicationDisabled(user)){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        
        List<Integer> thermostatIdsList = getThermostatIds(request);
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getEventsByThermostatIds(thermostatIdsList);

        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE) {
            // Limit the maximum items per page
            itemsPerPage = CtiUtilities.MAX_ITEMS_PER_PAGE;
        }
        SearchResults<ThermostatEvent> result = SearchResults.pageBasedForWholeList(currentPage, itemsPerPage, eventHistoryList);
        map.addAttribute("searchResult", result);
        map.addAttribute("eventHistoryList", result.getResultList());
        
        List<Thermostat> thermostats = new ArrayList<Thermostat>();
        for(int statId : thermostatIds) {
            thermostats.add(inventoryDao.getThermostatById(statId));
        }
        
        String temperatureUnit = customerDao.getCustomerForUser(user.getUserID()).getTemperatureUnit();
        
        map.addAttribute("thermostats", thermostats);
        map.addAttribute("temperatureUnit", temperatureUnit);
        map.addAttribute("thermostatIds", thermostatIds);
        
        return "consumer/history.jsp";
    }
    
    //------AJAX METHODS -> we should switch these to use the Spring 3 json conventions
    
    //ajax method to update the temperature preferences of the associated consumer
    @RequestMapping(value = "updateTemperaturePreference",
                    method = {RequestMethod.POST, RequestMethod.HEAD},     
                    headers = "x-requested-with=XMLHttpRequest")
    public void updateTemperaturePreference(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @ModelAttribute("customerAccount") CustomerAccount account,
                                            @RequestParam("temperatureUnit") String temperatureUnit,
                                            LiteYukonUser user,
                                            FlashScope flashScope,
                                            ModelMap map) throws NotAuthorizedException, WebClientException, TransactionException, IOException, IllegalArgumentException {
        
        if(isCommunicationDisabled(user)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write("");
        }
        
        JSONObject returnJSON = new JSONObject();
        customerDao.setTemperatureUnit(account.getCustomerId(), temperatureUnit);
        
        returnJSON.put("temperatureUnit", customerDao.getCustomerForUser(user.getUserID()).getTemperatureUnit());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(returnJSON.toString());
    }
    
    
    //Try to save the schedule
    //@returns JSONObject containing success or error messages
    @RequestMapping(value = "save", 
                    method = {RequestMethod.POST, RequestMethod.HEAD},
                    headers = "x-requested-with=XMLHttpRequest")
    public void saveJSON(HttpServletResponse response,
                           @ModelAttribute("customerAccount") CustomerAccount account,
                           @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                           @RequestParam(value="schedules", required=true) String scheduleString,
                           YukonUserContext yukonUserContext, 
                           FlashScope flashScope,
                           HttpServletRequest request) throws NotAuthorizedException, ServletRequestBindingException, IOException {

        if(isCommunicationDisabled(yukonUserContext.getYukonUser())){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write("");
        }
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        JSONObject returnJSON = new JSONObject();
        JSONObject scheduleJSON = JSONObject.fromObject(scheduleString);
        
        AccountThermostatSchedule ats = operatorThermostatHelper.JSONtoAccountThermostatSchedule(scheduleJSON);
        ats.setAccountId(account.getAccountId());

        //ensure this user can work with this schedule and thermostat
        accountCheckerService.checkThermostatSchedule(user, ats.getAccountThermostatScheduleId());
        accountCheckerService.checkInventory(user, thermostatIds);

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
            for (int thermostatId : thermostatIds) {
                Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
                accountEventLogService.thermostatScheduleSavingAttempted(user, account.getAccountNumber(), thermostat.getSerialNumber(), ats.getScheduleName(), EventSource.CONSUMER);
            }
        
            // Save the Schedule
            accountThermostatScheduleDao.save(ats);
            ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;
    
            // Log schedule name change
            if (!oldScheduleName.equalsIgnoreCase(ats.getScheduleName())) {
                accountEventLogService.thermostatScheduleNameChanged(user, oldScheduleName, ats.getScheduleName());
            }
    
            //flash messages - we will be navigating as a result of this response
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.consumer.thermostat.single.send." + message, ats.getScheduleName()));
        }
        
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(returnJSON.toString());
    }
    
    private boolean isCommunicationDisabled(LiteYukonUser user){
        return !optOutStatusService.getOptOutEnabled(user).isCommunicationEnabled();
    }
    
    @RequestMapping("viewArchivedSchedule")
    public String viewArchivedSchedule(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
                                       String thermostatIds,
                                       Integer scheduleId,
                                       YukonUserContext userContext,
                                       ModelMap model,
                                       HttpServletRequest request) throws IllegalArgumentException, ServletRequestBindingException {
        AccountThermostatSchedule schedule = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId,  customerAccount.getAccountId(), true);
        model.addAttribute("schedule", schedule);
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        model.addAttribute("temperatureUnit", temperatureUnit);
        List<Integer> thermostatIdsList = getThermostatIds(request);
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIdsList.get(0));
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
        model.addAttribute("thermostatType", type);
        model.addAttribute("thermostatIds", thermostatIds);
        model.addAttribute("thermostatId", thermostatIdsList.get(0));
        return "operator/operatorThermostat/history/commandDetail.jsp";
    }
}
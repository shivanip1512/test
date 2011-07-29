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
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.model.ThermostatScheduleCompatibility;
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

/**
 * Controller for Consumer-side Thermostat schedule operations
 */
@RequestMapping("/consumer/thermostat/schedule/*")
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

    
    @RequestMapping(value = "send", method = RequestMethod.POST)
    public String sendSchedule(HttpServletRequest request,
                               @ModelAttribute("customerAccount") CustomerAccount account,
                               @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                               @RequestParam("temperatureUnit") String temperatureUnit,
                               Integer scheduleId,
                               YukonUserContext yukonUserContext,
                               FlashScope flashScope,
                               ModelMap map) throws NotAuthorizedException, ServletRequestBindingException, IllegalArgumentException {
        // retrieve the schedule
        AccountThermostatSchedule ats = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId, account.getAccountId());
        ThermostatScheduleMode thermostatScheduleMode = ats.getThermostatScheduleMode();
        
        
        ThermostatScheduleUpdateResult message = thermostatService.sendSchedule(account, ats, thermostatIds, thermostatScheduleMode, yukonUserContext.getYukonUser());
    
        String pageName = "savedSchedules.";
        if(thermostatIds.size() > 1){
            pageName = "savedSchedulesMultiple.";
        }
        
        if (message.isFailed()) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.consumer." + pageName + message, ats.getScheduleName()));
        }else{
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.consumer." + pageName + message, ats.getScheduleName()));
            //associate the thermostats with this schedule
            accountThermostatScheduleDao.mapThermostatsToSchedule(thermostatIds, ats.getAccountThermostatScheduleId());
        }
        
        map.addAttribute("thermostatIds", StringUtils.join(thermostatIds, ","));
        return "redirect:view/saved";
    }
    
    @RequestMapping(value = "saveJSON", method = RequestMethod.POST)
    public String saveJSON(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            @RequestParam(value="schedules", required=true) String scheduleString,
            YukonUserContext yukonUserContext, 
            ModelMap map,
            FlashScope flashScope,
            HttpServletRequest request) throws NotAuthorizedException, ServletRequestBindingException {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        JSONObject scheduleJSON = JSONObject.fromObject(scheduleString);
        
        AccountThermostatSchedule ats = operatorThermostatHelper.JSONtoAccountThermostatSchedule(scheduleJSON);
        ats.setAccountId(account.getAccountId());

        //ensure this user can work with this schedule and thermostat
        accountCheckerService.checkThermostatSchedule(user, ats.getAccountThermostatScheduleId());
        accountCheckerService.checkInventory(user, thermostatIds);
        
        String oldScheduleName = "";
        if(ats.getAccountThermostatScheduleId() >= 0) {
            AccountThermostatSchedule oldAts = accountThermostatScheduleDao.getById(ats.getAccountThermostatScheduleId());
            oldScheduleName = oldAts.getScheduleName();
        }
        
        // Log thermostat schedule save attempt
        for (int thermostatId : thermostatIds) {
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            accountEventLogService.thermostatScheduleSavingAttemptedByConsumer(user, account.getAccountNumber(), thermostat.getSerialNumber(), ats.getScheduleName());
        }
        
        // Save the Schedule
        accountThermostatScheduleDao.save(ats);
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;

        // Log schedule name change
        if (!oldScheduleName.equalsIgnoreCase(ats.getScheduleName())) {
            accountEventLogService.thermostatScheduleNameChanged(user, oldScheduleName, ats.getScheduleName());
        }

        //flash messages
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.consumer.savedSchedules." + message, ats.getScheduleName()));
        map.addAttribute("thermostatIds", StringUtils.join(thermostatIds, ","));

        return "redirect:view/saved";
    }



    @RequestMapping(value = "view/saved", 
                    method = RequestMethod.GET)
    public String viewSaved(@ModelAttribute("customerAccount") CustomerAccount account,
                            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                            YukonUserContext yukonUserContext,
                            ModelMap map) throws NotAuthorizedException, ServletRequestBindingException {

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
        map.addAttribute("celsius_char", CtiUtilities.CELSIUS_CHARACTER);
        map.addAttribute("fahrenheit_char", CtiUtilities.FAHRENHEIT_CHARACTER);
        
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
                thermostatService.addMissingScheduleEntries(defaultAts);
                
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
    	
    	List<Integer> thermostatIdsList = getThermostatIds(request);
    	
    	AccountThermostatSchedule oldAts = accountThermostatScheduleDao.getById(scheduleId);
        String oldScheduleName = oldAts.getScheduleName();
    	
    	accountCheckerService.checkInventory(user, thermostatIdsList);
    	accountThermostatScheduleDao.deleteById(scheduleId);
    	
    	//flash message
    	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.deleteSuccess", oldScheduleName));
    	
    	map.addAttribute("thermostatIds", StringUtils.join(thermostatIds, ","));
    	return "redirect:view/saved";

    }
    
    @RequestMapping(value = "history", 
                    method = RequestMethod.GET)
    public String history(HttpServletRequest request,
                         @ModelAttribute("customerAccount") CustomerAccount account,
                         @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                         LiteYukonUser user,
                         FlashScope flashScope,
                         ModelMap map) throws NotAuthorizedException, ServletRequestBindingException {
        
        List<Integer> thermostatIdsList = getThermostatIds(request);
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getEventsByThermostatIds(thermostatIdsList);
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);

        SearchResult<ThermostatEvent> result = new SearchResult<ThermostatEvent>().pageBasedForWholeList(currentPage, itemsPerPage, eventHistoryList);
        map.addAttribute("searchResult", result);
        map.addAttribute("eventHistoryList", result.getResultList());
        
        List<Thermostat> thermostats = new ArrayList<Thermostat>();
        for(int statId : thermostatIds) {
            thermostats.add(inventoryDao.getThermostatById(statId));
        }
        map.addAttribute("thermostats", thermostats);
        
        return "consumer/history.jsp";
    }
    
    
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
        
        JSONObject returnJSON = new JSONObject();
        customerDao.setTemperatureUnit(account.getCustomerId(), temperatureUnit);
        
        returnJSON.put("temperatureUnit", customerDao.getCustomerForUser(user.getUserID()).getTemperatureUnit());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(returnJSON.toString());
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
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setThermostatEventHistoryDao(ThermostatEventHistoryDao thermostatEventHistoryDao) {
        this.thermostatEventHistoryDao = thermostatEventHistoryDao;
    }
}
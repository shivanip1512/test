package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
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

    
    @RequestMapping(value = "/consumer/thermostat/schedule/send", method = RequestMethod.POST)
    public String sendSchedule(HttpServletRequest request,
                               @ModelAttribute("customerAccount") CustomerAccount account,
                               @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                               @RequestParam("temperatureUnit") String temperatureUnit,
                               Integer scheduleId,
                               YukonUserContext yukonUserContext,
                               FlashScope flashScope,
                               ModelMap map) throws NotAuthorizedException, ServletRequestBindingException {
        // retrieve the schedule
        AccountThermostatSchedule ats = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId, account.getAccountId());
        ThermostatScheduleMode thermostatScheduleMode = ats.getThermostatScheduleMode();
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;
        
        //associate the thermostats with this schedule
        accountThermostatScheduleDao.mapThermostatsToSchedule(thermostatIds, ats.getAccountThermostatScheduleId());
        
        message = thermostatService.sendSchedule(account, ats, thermostatIds, thermostatScheduleMode, yukonUserContext.getYukonUser());
    
        if (message.isFailed()) {
            //Log schedule send to thermostat history
            if(thermostatIds.size() > 1) {
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
        
        map.addAttribute("thermostatIds", thermostatIds.toString());
        return "redirect:view/saved";
    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/saveJSON", method = RequestMethod.POST)
    public String saveJSON(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            @RequestParam("temperatureUnit") String temperatureUnit,
            @RequestParam(value="schedules", required=true) String scheduleString,
            YukonUserContext yukonUserContext, 
            ModelMap map,
            FlashScope flashScope,
            HttpServletRequest request) throws NotAuthorizedException, ServletRequestBindingException {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        JSONObject scheduleJSON = JSONObject.fromObject(scheduleString);
        
        AccountThermostatSchedule ats = operatorThermostatHelper.JSONtoAccountThermostatSchedule(scheduleJSON);
        ats.setAccountId(account.getAccountId());
        
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

        //ensure this user can work with this schedule and thermostat
        accountCheckerService.checkThermostatSchedule(user, ats.getAccountThermostatScheduleId());
        accountCheckerService.checkInventory(user, thermostatIds);
        
        //change the default temperature unit for the user based on what the interface was showing
        //when they submitted the form.  hmm... seems like an odd thing to do
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(account.getCustomerId(), escapedTempUnit);
        }
        
        // Save the Schedule
        accountThermostatScheduleDao.save(ats);
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;

        // Log schedule name change
        if (!oldScheduleName.equalsIgnoreCase(ats.getScheduleName())) {
            accountEventLogService.thermostatScheduleNameChanged(user, oldScheduleName, ats.getScheduleName());
        }

        //flash messages
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.dr.consumer.scheduleUpdate.result.CONSUMER_" + message, ats.getScheduleName()));
        map.addAttribute("thermostatIds", thermostatIds.toString());

        return "redirect:view/saved";
    }



    @RequestMapping(value = "/consumer/thermostat/schedule/view/saved", 
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
        List<SchedulableThermostatType> types = operatorThermostatHelper.getCompatibleSchedulableThermostatTypes(thermostat);
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
		List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllAllowedSchedulesAndEntriesForAccountByTypes(account.getAccountId(), types, yukonUserContext.getYukonUser());
		String temperatureUnit = customerDao.getCustomerForUser(yukonUserContext.getYukonUser().getUserID()).getTemperatureUnit();
		
		// current schedule is only relevant when operating in the context of 1 Thermostat
		if(thermostatIds.size() == 1){
    		AccountThermostatSchedule thermostatCurrentSchedule = accountThermostatScheduleDao.findByInventoryId(thermostat.getId());
            if (thermostatCurrentSchedule != null) {
                map.addAttribute("currentScheduleId", thermostatCurrentSchedule.getAccountThermostatScheduleId());
    
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
		}

		map.addAttribute("schedules", schedules);
        map.addAttribute("thermostat", thermostat);
        map.addAttribute("thermostats", thermostats);
        map.addAttribute("thermostatType", type);
        map.addAttribute("type", type);
        map.addAttribute("temperatureUnit", temperatureUnit);
        map.addAttribute("celcius_char", CtiUtilities.CELSIUS_CHARACTER);
        map.addAttribute("fahrenheit_char", CtiUtilities.FAHRENHEIT_CHARACTER);
        
        //default schedule(s)
        AccountThermostatSchedule defaultSchedule  = thermostatService.getAccountThermostatScheduleTemplate(account.getAccountId(), type);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, yukonUserContext);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        
        List<ThermostatScheduleMode> modes = operatorThermostatHelper.getAllowedModesForUserAndType(yukonUserContext.getYukonUser(), type);
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
    
    @RequestMapping(value = "/consumer/thermostat/schedule/delete", method = RequestMethod.POST)
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
    	
    	map.addAttribute("thermostatIds", thermostatIds.toString());
    	return "redirect:view/saved";

    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/history", 
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
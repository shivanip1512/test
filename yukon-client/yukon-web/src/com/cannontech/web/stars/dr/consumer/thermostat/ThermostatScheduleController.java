package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
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
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private ThermostatEventHistoryDao thermostatEventHistoryDao;

    
    @RequestMapping(value = "/consumer/thermostat/schedule/send", method = RequestMethod.POST)
    public String sendSchedule(HttpServletRequest request,
                               @ModelAttribute("customerAccount") CustomerAccount account,
                               @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                               Integer scheduleId,
                               YukonUserContext yukonUserContext,
                               FlashScope flashScope,
                               ModelMap map) throws Exception {
        // retrieve the schedule
        AccountThermostatSchedule ats = accountThermostatScheduleDao.findByIdAndAccountId(scheduleId, account.getAccountId());
        ThermostatScheduleMode thermostatScheduleMode = ats.getThermostatScheduleMode();
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;
        boolean failed = false;
        
        for (int thermostatId : thermostatIds) {
            message = thermostatService.sendSchedule(account, ats, thermostatIds, thermostatScheduleMode, yukonUserContext.getYukonUser());
        
            if (message.isFailed()) {
                //Log schedule send to thermostat history
                thermostatEventHistoryDao.logScheduleEvent(yukonUserContext.getYukonUser(), thermostatId, ats.getAccountThermostatScheduleId(), thermostatScheduleMode);
            }
        }
        
        if (failed){
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
        
        map.addAttribute("thermostatIds", thermostatIds);

        //navigate back to whence you came
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/saveJSON", method = RequestMethod.POST)
    public String saveJSON(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            @ModelAttribute("temperatureUnit") String temperatureUnit,
            @RequestParam(value="schedules", required=true) String scheduleString,
            YukonUserContext yukonUserContext, 
            ModelMap map,
            FlashScope flashScope,
            HttpServletRequest request) throws ServletRequestBindingException {

        LiteYukonUser user = yukonUserContext.getYukonUser();
        
        JSONObject scheduleJSON = JSONObject.fromObject(scheduleString);
        
        AccountThermostatSchedule ats = operatorThermostatHelper.JSONtoAccountThermostatSchedule(scheduleJSON);
        
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
        accountThermostatScheduleDao.saveAndMapToThermostats(ats, thermostatIds);
        ThermostatScheduleUpdateResult message = ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_SUCCESS;

        // Log schedule name change
        if (oldScheduleName.equalsIgnoreCase(ats.getScheduleName())) {
            accountEventLogService.thermostatScheduleNameChanged(user, oldScheduleName, ats.getScheduleName());
        }

        map.addAttribute("thermostatIds", thermostatIds.toString());
        
        //flash messages
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.dr.consumer.scheduleUpdate.result.CONSUMER_" + message));

        //navigate back
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }



    @RequestMapping(value = "/consumer/thermostat/schedule/view/saved", 
                    method = RequestMethod.GET)
    public String viewSaved(@ModelAttribute("customerAccount") CustomerAccount account,
                            @ModelAttribute("thermostatIds") List<Integer> thermostatIds, 
                            YukonUserContext yukonUserContext,
                            ModelMap map) throws NotAuthorizedException {

        accountCheckerService.checkInventory(yukonUserContext.getYukonUser(), thermostatIds);
        
        //ensure thermostatIds are of the same type
        

        //even if there are multiple thermostat ids, they are supposed to be of the same type
        int thermostatId = thermostatIds.get(0);
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        List<SchedulableThermostatType> types = operatorThermostatHelper.getCompatibleSchedulableThermostatTypes(thermostat);
        SchedulableThermostatType type = SchedulableThermostatType.getByHardwareType(thermostat.getType());
		List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllAllowedSchedulesAndEntriesForAccountByTypes(account.getAccountId(), types, yukonUserContext.getYukonUser());

		map.addAttribute("schedules", schedules);
		
        JSONObject json = new JSONObject();
        
        // temperatureUnitmanual
        String temperatureUnit = customerDao.getCustomerForUser(yukonUserContext.getYukonUser().getUserID()).getTemperatureUnit();
        map.addAttribute("temperatureUnit", temperatureUnit);
        
        json.put("SCHEDULES", new JSONArray());
        
        for(AccountThermostatSchedule schedule : schedules){
            json.accumulate("SCHEDULES", operatorThermostatHelper.AccountThermostatScheduleToJSON(schedule));
        }
        
        map.addAttribute("type", type);
        
        json.put("thermostat", operatorThermostatHelper.ThermostatToJSON(thermostat, yukonUserContext));

        //default schedule(s)
        AccountThermostatSchedule defaultSchedule  = thermostatService.getAccountThermostatScheduleTemplate(account.getAccountId(), type);
        String useDefaultScheduleName = operatorThermostatHelper.generateDefaultNameForUnnamedSchdule(defaultSchedule, null, yukonUserContext);
        defaultSchedule.setScheduleName(useDefaultScheduleName);
        json.put("DEFAULT", operatorThermostatHelper.AccountThermostatScheduleToJSON(defaultSchedule));
        
        YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostat.label",
                                                                                   thermostat.getLabel());
        map.addAttribute("thermostatLabel", resolvable);
        map.addAttribute("savedSchedules", json.toString());
        map.addAttribute("defaultSchedule", operatorThermostatHelper.AccountThermostatScheduleToJSON(defaultSchedule));
        map.addAttribute("allowedModes", operatorThermostatHelper.getAllowedModesForUserAndThermostat(yukonUserContext.getYukonUser(), thermostat));

        return "consumer/savedSchedules.jsp";
    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/delete", method = RequestMethod.POST)
    public String delete(HttpServletRequest request,
                         @ModelAttribute("customerAccount") CustomerAccount account,
                         String thermostatIds,
                         Integer scheduleId,
                         LiteYukonUser user,
                         FlashScope flashScope,
                         ModelMap map) throws Exception {
    	
    	List<Integer> thermostatIdsList = getThermostatIds(request);
    	
    	AccountThermostatSchedule oldAts = accountThermostatScheduleDao.getById(scheduleId);
        String oldScheduleName = oldAts.getScheduleName();
    	
    	accountCheckerService.checkInventory(user, thermostatIdsList);
    	accountThermostatScheduleDao.deleteById(scheduleId);
    	
    	//flash message
    	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.dr.consumer.thermostatSchedule.deleteSuccess", oldScheduleName));
    	map.addAttribute("thermostatIds", thermostatIds);
    	
    	String referer = request.getHeader("Referer");
        return "redirect:"+ referer;

    }
    
    @RequestMapping(value = "/consumer/thermostat/schedule/history", 
                    method = RequestMethod.GET)
    public String history(HttpServletRequest request,
                         @ModelAttribute("customerAccount") CustomerAccount account,
                         String thermostatIds,
                         LiteYukonUser user,
                         FlashScope flashScope,
                         ModelMap map) throws Exception {
        
        List<Integer> thermostatIdsList = getThermostatIds(request);
        
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getEventsByThermostatIds(thermostatIdsList);
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);

        SearchResult<ThermostatEvent> result = new SearchResult<ThermostatEvent>().pageBasedForWholeList(currentPage, itemsPerPage, eventHistoryList);
        map.addAttribute("searchResult", result);
        map.addAttribute("eventHistoryList", result.getResultList());
        
        return "consumer/history.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/schedule/saved", method = RequestMethod.POST, params = "createNew")
    public String createNewSchedule(String thermostatIds, Integer scheduleId, ModelMap map) {
        
        map.addAttribute("createAsNew", true);
        map.addAttribute("thermostatIds", thermostatIds);
        return "redirect:/spring/stars/consumer/thermostat/schedule/view/saved";
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
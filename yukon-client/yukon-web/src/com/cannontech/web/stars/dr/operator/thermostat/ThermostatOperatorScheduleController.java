package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
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
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;

/**
 * Controller for Operator-side Thermostat schedule operations
 */
@CheckRole(YukonRole.CONSUMER_INFO)
@Controller
public class ThermostatOperatorScheduleController 
	extends AbstractThermostatOperatorScheduleController {

    private InventoryDao inventoryDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private CustomerDao customerDao;
    private ThermostatService thermostatService;
    
    private CustomerAccountDao customerAccountDao;
	private StarsInventoryBaseDao starsInventoryBaseDao;
	private DateFormattingService dateFormattingService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
    
	@ModelAttribute("thermostatIds")
    public List<Integer> getThermostatIds(HttpServletRequest request)
            throws ServletRequestBindingException {

        String thermostatIds = ServletRequestUtils.getStringParameter(request,
                                                                      "thermostatIds");

        List<Integer> idList = new ArrayList<Integer>();

        // If thermostatIds exists, remove brackets, split and create Integer list
        thermostatIds = thermostatIds.replaceAll("[\\[|\\]]", "");
        if (!StringUtils.isBlank(thermostatIds)) {
			List<Integer> tempIdList = ServletUtil.getIntegerListFromString(thermostatIds);
        	idList.addAll(tempIdList);
        }

        return idList;
    }
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
    	
    	// Get the account info from the session
    	HttpSession session = request.getSession();
    	StarsCustAccountInformation accountInfo = 
            (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
    	
        int accountId = accountInfo.getStarsCustomerAccount().getAccountID();
		CustomerAccount account = customerAccountDao.getById(accountId);
        return account;
    }
    
    /**
     * Helper method to get the legacy 'inventory number' for use with leagcy stars operator
     * urls
     * @param request - Current request
     * @param thermostatId - Id to get number for
     * @return Inventory number
     */
    protected int getInventoryNumber(HttpServletRequest request, int thermostatId) {
    	
    	StarsCustAccountInformation accountInfo = 
            (StarsCustAccountInformation) request.getSession().getAttribute(
            		ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
        StarsInventories inventories = accountInfo.getStarsInventories();
        StarsInventory[] starsInventory = inventories.getStarsInventory();
        int count = 0;
        for(StarsInventory inventory : starsInventory) {
        	if(thermostatId == inventory.getInventoryID()) {
        		break;
        	}
        	count++;
        }
        
        return count;
    }
    
    /**
     * Helper method to make sure the inventory being used belongs to the current account 
     */
    public void checkInventoryAgainstAccount(List<Integer> inventoryIdList, CustomerAccount customerAccount){
    	
    	for(Integer inventoryId : inventoryIdList) {
    	
	    	LiteInventoryBase inventory = starsInventoryBaseDao.getById(inventoryId);
	    	int accountId = customerAccount.getAccountId();
			if(inventory.getAccountID() != accountId) {
	    		throw new NotAuthorizedException("The Inventory with id: " + inventoryId + 
	    				" does not belong to the current customer account with id: " + accountId);
	    	}
    	}
    }

    @RequestMapping(value = "/operator/thermostat/schedule/view", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount account, 
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            Integer scheduleId, YukonUserContext yukonUserContext, ModelMap map, 
            HttpServletRequest request) {

    	this.checkInventoryAgainstAccount(thermostatIds, account);
        
        ThermostatSchedule schedule = null;
        ThermostatSchedule defaultSchedule = null;
        int accountId = account.getAccountId();
        if (scheduleId != null) {
            // Get the schedule that the user selected
            schedule = thermostatScheduleDao.getThermostatScheduleById(scheduleId,
                                                                       accountId);
        }

        Integer thermostatId = null;
        HardwareType thermostatType = null;
        if (thermostatIds.size() == 1) {
            // single thermostat selected

            thermostatId = thermostatIds.get(0);

            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.operator.thermostatSchedule.label",
                                                                                       thermostat.getLabel());
            map.addAttribute("thermostatLabel", resolvable);

            if (schedule == null) {
                // Get the current (or default if no current) schedule
                schedule = thermostatService.getThermostatSchedule(thermostat,
                                                                   account);

                if (StringUtils.isBlank(schedule.getName())) {
                    schedule.setName(thermostat.getLabel());
                }
            }

            defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId,
                                                                                    thermostat.getType());
            thermostatType = thermostat.getType();
        } else {
            // multiple thermostats selected

            // Get type of the first thermostat in the list
			thermostatId = thermostatIds.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
			thermostatType = thermostat.getType();

            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.operator.thermostatSchedule.multipleLabel");
            map.addAttribute("thermostatLabel", resolvable);
            if (schedule == null) {
                schedule = thermostatService.getThermostatSchedule(thermostat, account);
            }

            defaultSchedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(accountId,
                                                                                    thermostatType);
        }

        // Add the temperature unit to model
        LiteCustomer customer = customerDao.getLiteCustomer(account.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        map.addAttribute("temperatureUnit", temperatureUnit);

        // Get json string for schedule and add schedule and string to model
        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equals(temperatureUnit);
        JSONObject scheduleJSON = this.getJSONForSchedule(schedule, isFahrenheit);
        map.addAttribute("scheduleJSONString", scheduleJSON.toString());
        map.addAttribute("schedule", schedule);

        // Get json string for the default schedule and add to model
        JSONObject defaultScheduleJSON = this.getJSONForSchedule(defaultSchedule, isFahrenheit);
        map.addAttribute("defaultScheduleJSONString",
                         defaultScheduleJSON.toString());


        Locale locale = yukonUserContext.getLocale();
        map.addAttribute("localeString", locale.toString());

        map.addAttribute("thermostatType", thermostatType.toString());
        
        int inventoryNumber = this.getInventoryNumber(request, thermostatId);
        map.addAttribute("inventoryNumber", inventoryNumber);
        
        return "operator/thermostat/thermostatSchedule.jsp";
    }
    
    @RequestMapping(value = "/operator/thermostat/schedule/showConfirm", method = RequestMethod.POST)
    public String showConfirm(@ModelAttribute("customerAccount") CustomerAccount account,
    		@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
    		String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
    		@RequestParam(value="schedules", required=true) String scheduleString,
    		HttpServletRequest request, ModelMap map) throws ServletRequestBindingException {
    	
    	this.checkInventoryAgainstAccount(thermostatIds, account);
    	
    	boolean sendAndSave = "saveApply".equals(saveAction);
    	if(!sendAndSave) {
    		return "forward:/spring/stars/operator/thermostat/schedule/save";
    	}
    	map.addAttribute("saveAction", saveAction);
    	map.addAttribute("scheduleId", scheduleId);
    	map.addAttribute("schedules", scheduleString);
    	map.addAttribute("timeOfWeek", timeOfWeek);
    	map.addAttribute("scheduleMode", scheduleMode);
    	map.addAttribute("temperatureUnit", temperatureUnit);
    	map.addAttribute("scheduleId", scheduleId);
    	map.addAttribute("scheduleName", scheduleName);
    	
    	// There is only 1 thermostat on this page
        int thermostatId = thermostatIds.get(0);
    	int inventoryNumber = this.getInventoryNumber(request, thermostatId);
    	map.addAttribute("InvNo", inventoryNumber);
    	
    	return "redirect:/operator/Consumer/ThermostatScheduleConfirm.jsp";
    	
    }
    
    @RequestMapping(value = "/operator/thermostat/schedule/confirm", method = RequestMethod.GET)
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
            String scheduleName, String saveAction, YukonUserContext yukonUserContext, 
            @RequestParam(value="schedules", required=true) String scheduleString,
            ModelMap map) throws ServletRequestBindingException {
    	
    	
    	// Create the confirm schedule text
    	boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
    	ThermostatSchedule schedule = this.getScheduleForJSON(scheduleString, isFahrenheit);
    	
    	MessageSource messageSource = messageSourceResolver.getMessageSource(yukonUserContext);
    	TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);
    	YukonMessageSourceResolvable timeOfWeekString = 
    		new YukonMessageSourceResolvable(scheduleTimeOfWeek.getDisplayKey());
    	
    	List<Object> argumentList = new ArrayList<Object>();
    	argumentList.add(timeOfWeekString);
    	
    	// Add the thermostat label to the model
    	Thermostat thermostat = null;
        int thermostatId = thermostatIds.get(0);

		thermostat = inventoryDao.getThermostatById(thermostatId);
        YukonMessageSourceResolvable resolvable = 
        	new YukonMessageSourceResolvable("yukon.dr.operator.thermostatSchedule.label",
        									 thermostat.getLabel());
        map.addAttribute("thermostatLabel", resolvable);
    	
        String scheduleConfirmKey = "yukon.dr.operator.thermostatScheduleConfirm.scheduleText";
    	if (HardwareType.COMMERCIAL_EXPRESSSTAT.equals(thermostat.getType())) {
    		this.setToTwoTimeTemps(schedule);
    		scheduleConfirmKey = 
    			"yukon.dr.operator.thermostatScheduleConfirm.scheduleTextTwoTimeTemp";
    	}

    	List<ThermostatSeasonEntry> seasonEntries = 
    		schedule.getSeason().getSeasonEntries(scheduleTimeOfWeek);
    	
    	for(ThermostatSeasonEntry entry : seasonEntries) {
    		Date startDate = entry.getStartDate();
    		Integer coolTemperature = entry.getCoolTemperature();
    		Integer heatTemperature = entry.getHeatTemperature();
    		
    		String startDateString = 
    			dateFormattingService.formatDate(startDate, DateFormatEnum.TIME, yukonUserContext);

    		// Temperatures are only -1 if this is a 2 time temp thermostat type - ignore if -1
    		if(coolTemperature != -1 && heatTemperature != -1) {
	    		argumentList.add(startDateString);
	    		argumentList.add(coolTemperature);
	    		argumentList.add(heatTemperature);
    		}
    	}
    	
    	String scheduleConfirm = messageSource.getMessage(
						    			scheduleConfirmKey, 
						    			argumentList.toArray(), 
						    			yukonUserContext.getLocale());
    	map.addAttribute("scheduleConfirm", scheduleConfirm);
    	
    	// Pass all of the parameters through to confirm page
    	map.addAttribute("schedules", scheduleString);
    	map.addAttribute("timeOfWeek", timeOfWeek);
    	map.addAttribute("scheduleMode", scheduleMode);
    	map.addAttribute("temperatureUnit", temperatureUnit);
    	map.addAttribute("scheduleId", scheduleId);
    	map.addAttribute("scheduleName", scheduleName);
    	map.addAttribute("saveAction", saveAction);
    	
    	return "operator/thermostat/thermostatScheduleConfirm.jsp";
    }    	

    @RequestMapping(value = "/operator/thermostat/schedule/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String timeOfWeek, String scheduleMode, String temperatureUnit, Integer scheduleId,
            String scheduleName, String saveAction, YukonUserContext yukonUserContext,
            @RequestParam(value="schedules", required=true) String scheduleString,
            HttpServletRequest request, ModelMap map) throws ServletRequestBindingException {

    	this.checkInventoryAgainstAccount(thermostatIds, account);
        
        boolean sendAndSave = "saveApply".equals(saveAction);

        boolean isFahrenheit = CtiUtilities.FAHRENHEIT_CHARACTER.equalsIgnoreCase(temperatureUnit);
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(account.getCustomerId(), escapedTempUnit);
        }

        // Create schedule from submitted JSON string
        ThermostatSchedule schedule = getScheduleForJSON(scheduleString,
                                                         isFahrenheit);
        schedule.setName(scheduleName);
        schedule.setAccountId(account.getAccountId());

        TimeOfWeek scheduleTimeOfWeek = TimeOfWeek.valueOf(timeOfWeek);
        ThermostatScheduleMode thermostatScheduleMode = ThermostatScheduleMode.valueOf(scheduleMode);
        boolean applyToAll = ThermostatScheduleMode.ALL.equals(thermostatScheduleMode);

        ThermostatScheduleUpdateResult message = null;

        boolean failed = false;
        for (Integer thermostatId : thermostatIds) {

        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

            // Default the schedule name if none provided
            if (StringUtils.isBlank(schedule.getName())) {
            	schedule.setName(thermostat.getLabel());
            }
            
            HardwareType type = thermostat.getType();
            schedule.setThermostatType(type);
            if (type.equals(HardwareType.COMMERCIAL_EXPRESSSTAT)) {
                this.setToTwoTimeTemps(schedule);
            }

			if (sendAndSave) {
                // Send the schedule to the thermsotat(s) and then update the
                // existing thermostat(s) schedule or create a new schedule for
                // the thermostat(s) if it has none

                schedule.setInventoryId(thermostatId);

                // Send schedule to thermsotat
                message = thermostatService.sendSchedule(account,
                                                         schedule,
                                                         scheduleTimeOfWeek,
                                                         thermostatScheduleMode,
                                                         applyToAll,
                                                         yukonUserContext);

                // Save changes to schedule
                thermostatService.updateSchedule(account,
                                                 schedule,
                                                 scheduleTimeOfWeek,
                                                 applyToAll ,
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
                thermostatService.updateSchedule(account,
                                                 schedule,
                                                 scheduleTimeOfWeek,
                                                 applyToAll,
                                                 yukonUserContext);

                message = ThermostatScheduleUpdateResult.CONSUMER_SAVE_SCHEDULE_SUCCESS;
            }
        }

        // If there was a failure and we are processing multiple
        // thermostats, set error to generic multiple error
        if (failed && thermostatIds.size() > 1) {
            message = ThermostatScheduleUpdateResult.CONSUMER_MULTIPLE_ERROR;
        }

        map.addAttribute("message", message.toString());

        // Manually put thermsotatIds into model for redirect
        map.addAttribute("thermostatIds", thermostatIds.toString());

        return "redirect:/operator/Consumer/ThermostatScheduleMessage.jsp";
    }

    @RequestMapping(value = "/operator/thermostat/schedule/complete", method = RequestMethod.GET)
    public String updateComplete(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            String message, LiteYukonUser user, ModelMap map, HttpServletRequest request) 
    	throws Exception {

    	this.checkInventoryAgainstAccount(thermostatIds, customerAccount);
    	
        ThermostatScheduleUpdateResult resultMessage = ThermostatScheduleUpdateResult.valueOf(message);
        String key = resultMessage.getDisplayKey();

        YukonMessageSourceResolvable resolvable;

        if (thermostatIds.size() == 1) {
            int thermostatId = thermostatIds.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

            resolvable = new YukonMessageSourceResolvable(key,
                                                          thermostat.getLabel());

        } else {
            resolvable = new YukonMessageSourceResolvable(key);
        }
        
        // Get the first thermostat id
        Integer thermostatId = thermostatIds.get(0);
        
        // Get the 'inventory number' for use with legacy stars operator links
        int inventoryNumber = this.getInventoryNumber(request, thermostatId);

        map.addAttribute("message", resolvable);

        map.addAttribute("viewUrl", "/operator/Consumer/ThermSchedule.jsp?InvNo=" + inventoryNumber);

        return "operator/thermostat/actionComplete.jsp";
    }
    
    @RequestMapping(value = "/operator/thermostat/schedule/hints", method = RequestMethod.GET)
    public String hints(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            ModelMap map, HttpServletRequest request) {
    	
    	this.checkInventoryAgainstAccount(thermostatIds, customerAccount);
    	
    	Integer thermostatId = null;
    	if(thermostatIds.size() > 0) {
    		thermostatId = thermostatIds.get(0);
    	}
    	
    	int inventoryNumber = this.getInventoryNumber(request, thermostatId);
    	map.addAttribute("inventoryNumber", inventoryNumber);
        return "operator/thermostat/scheduleHints.jsp";
    }

    @RequestMapping(value = "/operator/thermostat/schedule/view/saved", method = RequestMethod.GET)
    public String viewSaved(@ModelAttribute("customerAccount") CustomerAccount account,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            LiteYukonUser user, ModelMap map) throws Exception {

    	this.checkInventoryAgainstAccount(thermostatIds, account);

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIds.get(0));
        HardwareType type = thermostat.getType();
        
        int accountId = account.getAccountId();
        List<ScheduleDropDownItem> schedules = 
        	thermostatScheduleDao.getSavedThermostatSchedulesByAccountId(accountId, type);
        map.addAttribute("schedules", schedules);

        return "operator/thermostat/savedSchedules.jsp";
    }
    
    @RequestMapping(value = "/operator/thermostat/schedule/saved", method = RequestMethod.POST, params = "delete")
    public String delete(HttpServletRequest request, 
    		@ModelAttribute("customerAccount") CustomerAccount account,
    		@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		Integer scheduleId, LiteYukonUser user, ModelMap map) throws Exception {

    	this.checkInventoryAgainstAccount(thermostatIds, account);
    	
    	thermostatScheduleDao.delete(scheduleId);

    	// There is only 1 thermostat on this page
    	int thermostatId = thermostatIds.get(0);
    	int inventoryNumber = this.getInventoryNumber(request, thermostatId);
    	map.addAttribute("InvNo", inventoryNumber);
    	
    	map.addAttribute("scheduleId", scheduleId);
    	return "redirect:/operator/Consumer/SavedSchedules.jsp";
    }
    
    @RequestMapping(value = "/operator/thermostat/schedule/saved", method = RequestMethod.POST, params = "view")
    public String viewSchedule(@ModelAttribute("thermostatIds") List<Integer> thermostatIds, 
    		@ModelAttribute("customerAccount") CustomerAccount account,
    		Integer scheduleId, ModelMap map, HttpServletRequest request) throws Exception {
    	
    	this.checkInventoryAgainstAccount(thermostatIds, account);
    	
    	// There is only 1 thermostat on this page
    	int thermostatId = thermostatIds.get(0);
    	int inventoryNumber = this.getInventoryNumber(request, thermostatId);
    	map.addAttribute("InvNo", inventoryNumber);
    	
    	map.addAttribute("scheduleId", scheduleId);
    	return "redirect:/operator/Consumer/ThermSchedule.jsp";
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setThermostatScheduleDao(
            ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
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
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    @Autowired
    public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
    
    @Autowired
    public void setDateFormattingService(
			DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}

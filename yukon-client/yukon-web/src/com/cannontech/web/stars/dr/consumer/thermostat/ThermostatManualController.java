package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Controller for Consumer-side Manual thermostat operations
 */
@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatManualController extends AbstractThermostatController {
    private AccountEventLogService accountEventLogService;
    private InventoryDao inventoryDao;
    private CustomerEventDao customerEventDao;
    private ThermostatService thermostatService;
    private CustomerDao customerDao;
    private ThermostatEventHistoryDao thermostatEventHistoryDao;
    
    private final int NUMBER_OF_HISTORY_ROWS_TO_DISPLAY = 6;
    
    @RequestMapping(value = "/consumer/thermostat/view", method = RequestMethod.GET)
    public String view(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            LiteYukonUser user, ModelMap map) throws Exception {
        
        accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        // Get the first (or only) thermostat and add to model
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIds.get(0));
        map.addAttribute("thermostat", thermostat);

        ThermostatManualEvent event;
        if (thermostatIds.size() == 1) {
            // single thermostat selected
            int thermostatId = thermostatIds.get(0);

            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostat.label",
                                                                                       thermostat.getLabel());
            map.addAttribute("thermostatLabel", resolvable);

            event = customerEventDao.getLastManualEvent(thermostatId);
        } else {
            // multiple thermostats selected
            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.consumer.thermostat.multipleLabel");
            map.addAttribute("thermostatLabel", resolvable);

            event = new ThermostatManualEvent();
        }
        
        LiteCustomer customer = customerDao.getCustomerForUser(user.getUserID());
        String temperatureUnit = customer.getTemperatureUnit();
        event.setTemperatureUnit(temperatureUnit);

        map.addAttribute("event", event);
        
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getLastNEventsByThermostatIds(thermostatIds, NUMBER_OF_HISTORY_ROWS_TO_DISPLAY);
        map.addAttribute("eventHistoryList", eventHistoryList);
        
        return "consumer/thermostat.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/saveLabel", method = RequestMethod.POST)
    public String saveLabel(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String displayLabel, LiteYukonUser user, ModelMap map) throws Exception {

        if (thermostatIds.size() != 1) {
            throw new IllegalArgumentException("You can only change the label of 1 thermostat at a time.");
        }
        
        int thermostatId = thermostatIds.get(0);
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

        accountEventLogService.thermostatLabelChangeAttemptedByConsumer(user,
                                                                        thermostat.getSerialNumber(),
                                                                        thermostat.getDeviceLabel(),
                                                                        displayLabel);
        
        accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        


        thermostat.setDeviceLabel(displayLabel);

        inventoryDao.save(thermostat);

        map.addAttribute("thermostatIds", thermostatId);

        return "redirect:/spring/stars/consumer/thermostat/view";
    }

    @RequestMapping(value = "/consumer/thermostat/manual", method = RequestMethod.POST)
    public String manual(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String mode, String fan, String temperatureUnit, Integer temperature,
            YukonUserContext userContext, HttpServletRequest request, ModelMap map) 
            throws Exception {

        executeManualEvent(thermostatIds, mode, fan, temperatureUnit, temperature, userContext, request, map);

        return "redirect:/spring/stars/consumer/manualComplete";
    }
    
    @RequestMapping(value = "/consumer/thermostat/runProgram", method = RequestMethod.POST)
    public String runProgram(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String mode, String fan, String temperatureUnit, Integer temperature, YukonUserContext userContext,
            HttpServletRequest request, ModelMap map) throws Exception {

        executeManualEvent(thermostatIds, mode, fan, temperatureUnit, temperature, userContext, request, map);

        return "redirect:/spring/stars/consumer/runProgramComplete";
    }

	private void executeManualEvent(List<Integer> thermostatIds, 
	                                String mode,
	                                String fan, 
	                                String temperatureUnit,
	                                Integer temperature,
	                                YukonUserContext userContext,
	                                HttpServletRequest request, 
	                                ModelMap map) {
		
	    CustomerAccount account = getCustomerAccount(request);
		
        thermostatService.logConsumerThermostatManualSaveAttempt(thermostatIds, userContext, account);
        
        accountCheckerService.checkInventory(userContext.getYukonUser(), 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        thermostatService.updateTempUnitForCustomer(temperatureUnit, account.getCustomerId());
        
        ThermostatMode thermostatMode = thermostatService.getThermostatModeFromString(mode);
        
        // See if the run program button was clicked
        String runProgramButtonClicked = ServletRequestUtils.getStringParameter(request, "runProgram", null);
        boolean runProgram = runProgramButtonClicked != null;
        
        //temperature must be validated if mode is heat or cool and the run program
        //button was not pressed
        boolean needsTempValidation = thermostatMode.isHeatOrCool() && !runProgram;
        boolean isValid = true;
        ThermostatManualEventResult result = null;
        int temperatureInF = thermostatService.getTempOrDefaultInF(temperature, temperatureUnit);
        
        //Validate temperature for mode and thermostat type
        if(needsTempValidation) {
            ThermostatManualEventResult limitMessage = thermostatService.validateTempAgainstLimits(thermostatIds, temperatureInF, thermostatMode);
            
            if(limitMessage != null) {
                map.addAttribute("message", "yukon.dr.consumer.manualevent.result.CONSUMER_" + limitMessage.name());
                isValid = false;
            }
        }

        if(isValid) {
            boolean hold = ServletRequestUtils.getBooleanParameter(request, "hold", false);
            String key = "yukon.dr.consumer.manualevent.result.CONSUMER_";
            result = thermostatService.setupAndExecuteManualEvent(thermostatIds, hold, runProgram, temperatureInF, temperatureUnit, mode, fan, account, userContext);
            if (result.isFailed()) {
                if (thermostatIds.size() > 1) {
                    key += "MULTIPLE_ERROR";
                } else {
                    key += "ERROR";
                }
            } else {
                key += result.name();
            }
            map.addAttribute("message", key);
    	}
        
        // Manually put thermsotatIds into model for redirect
        map.addAttribute("thermostatIds", thermostatIds.toString());
	}

    @RequestMapping(value = "/consumer/manualComplete", method = RequestMethod.GET)
    public String manualComplete(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String message, LiteYukonUser user, ModelMap map) throws Exception {

        setupManualCompletePage(thermostatIds, message, user, map);

        map.addAttribute("viewUrl", "/spring/stars/consumer/thermostat/view");
        
        return "consumer/actionComplete.jsp";
    }
    
    @RequestMapping(value = "/consumer/runProgramComplete", method = RequestMethod.GET)
    public String runProgramComplete(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String message, LiteYukonUser user, ModelMap map) throws Exception {

        setupManualCompletePage(thermostatIds, message, user, map);

        map.addAttribute("viewUrl", "/spring/stars/consumer/thermostat/schedule/view/saved");
        
        return "consumer/actionComplete.jsp";
    }

	private void setupManualCompletePage(List<Integer> thermostatIds,
			String message, LiteYukonUser user, ModelMap map) {
		accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        List<String> thermostatLabels = new ArrayList<String>();
        for(Integer thermostatId : thermostatIds) {
        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        	thermostatLabels.add(thermostat.getLabel());
        }
        
        String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
        YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable(message, thermostatLabelString);

        map.addAttribute("message", resolvable);
        map.addAttribute("thermostatIds", thermostatIds);
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
    public void setCustomerEventDao(CustomerEventDao customerEventDao) {
        this.customerEventDao = customerEventDao;
    }

    @Autowired
    public void setThermostatService(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }
    
    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
    
    @Autowired
    public void setThermostatEventHistoryDao(ThermostatEventHistoryDao thermostatEventHistoryDao) {
        this.thermostatEventHistoryDao = thermostatEventHistoryDao;
    }
}

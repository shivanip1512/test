package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.hardware.validator.ThermostatValidator;

/**
 * Controller for Consumer-side Manual thermostat operations
 */
@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
public class ThermostatManualController extends AbstractThermostatController {
    private static int DEFAULT_DEADBAND = 3;
    
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private CustomerDao customerDao;
    @Autowired private CustomerEventDao customerEventDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LMHardwareBaseDao lmHardwareBaseDao;
    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private ThermostatEventHistoryDao thermostatEventHistoryDao;
    @Autowired private ThermostatService thermostatService;
    
    private final int NUMBER_OF_HISTORY_ROWS_TO_DISPLAY = 6;
    
    @RequestMapping(value = "/consumer/thermostat/view", method = RequestMethod.GET)
    public String view(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
                       LiteYukonUser user, 
                       ModelMap map,
                       HttpServletRequest request) throws Exception {
        
        if(isCommunicationDisabled(user)){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        
        accountCheckerService.checkInventory(user, 
                                             thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        // Get the first (or only) thermostat and add to model
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIds.get(0));
        map.addAttribute("thermostat", thermostat);

        if (thermostatIds.size() == 1) {
            // single thermostat selected
            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.web.modules.consumer.thermostat.label",
                                                                                       thermostat.getLabel());
            map.addAttribute("thermostatLabel", resolvable);
        } else {
            // multiple thermostats selected
            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.web.modules.consumer.thermostat.multipleLabel");
            map.addAttribute("thermostatLabel", resolvable);
        }
        
        ThermostatManualEvent event = null;
        for (Integer thermostatId : thermostatIds) {
            event = customerEventDao.getLastManualEvent(thermostatId);
            if (event.getEventId() != null) {
                break;
            }
        }
        if (event == null || event.getEventId() == null) {
            event = new ThermostatManualEvent();
        }
        map.addAttribute("event", event);
        
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getLastNEventsByThermostatIds(thermostatIds, NUMBER_OF_HISTORY_ROWS_TO_DISPLAY);
        map.addAttribute("eventHistoryList", eventHistoryList);

        String temperatureUnit = customerDao.getCustomerForUser(user.getUserID()).getTemperatureUnit();
        map.addAttribute("temperatureUnit", temperatureUnit);
        CustomerAccount customerAccount = customerAccountDao.getAccountByInventoryId(thermostatIds.get(0));
        
        // Check to see if auto mode is enabled for this device.
        boolean autoThermostatModeEnabled = thermostatService.isAutoModeAvailable(customerAccount.getAccountId(), thermostatIds.get(0));
        map.addAttribute("autoModeEnabled", autoThermostatModeEnabled);
        map.addAttribute("deadband", DEFAULT_DEADBAND);
        
        // Check if we're in the auto mode enabled view.
        if (map.get("autoModeEnabledCommandView") == null) {
            map.addAttribute("autoModeEnabledCommandView", false);
        }
        
        return "consumer/thermostat.jsp";
    }
    
    // VIEW
    @RequestMapping(value = "/consumer/thermostat/autoEnabledView", method = RequestMethod.GET)
    public String autoEnabledView(@ModelAttribute("thermostatIds") List<Integer> thermostatIds, LiteYukonUser user, 
                                   ModelMap map, HttpServletRequest request) throws Exception {
    
         map.addAttribute("autoModeEnabledCommandView", true);
         return view(thermostatIds, user, map, request);
     }


    @RequestMapping(value = "/consumer/thermostat/saveLabel", method = RequestMethod.POST)
    public String saveLabel(ModelMap map, @ModelAttribute Thermostat thermostat,
                            BindingResult bindingResult, FlashScope flashScope,
                            YukonUserContext userContext)  {
        LiteYukonUser user = userContext.getYukonUser();
        if(isCommunicationDisabled(user)){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        int inventoryId = thermostat.getId();
        // They are only allowed to change the label so we read the old thermostat from the
        // database and just change the one field they're allowed to change.
        String newLabel = thermostat.getDeviceLabel();
        thermostat = inventoryDao.getThermostatById(inventoryId);
        String oldLabel = thermostat.getDeviceLabel();
        thermostat.setDeviceLabel(newLabel);
        accountEventLogService.thermostatLabelChangeAttemptedByConsumer(user,
                                                                        thermostat.getSerialNumber(),
                                                                        oldLabel, newLabel);

        accountCheckerService.checkInventory(user, inventoryId);

        ThermostatValidator validator = new ThermostatValidator();
        validator.validate(thermostat, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        } else {
            inventoryDao.updateLabel(thermostat, user);
        }

        map.addAttribute("thermostatIds", inventoryId);

        return "redirect:/spring/stars/consumer/thermostat/view";
    }

    @RequestMapping(value = "/consumer/thermostat/manual", method = RequestMethod.POST)
    public String manual(@ModelAttribute("thermostatIds") List<Integer> thermostatIds, 
                         String mode, String fan, String temperatureUnit, Double heatTemperature, Double coolTemperature, 
                         FlashScope flashScope, YukonUserContext userContext, 
                         HttpServletRequest request, ModelMap map) 
    throws Exception {
        if(isCommunicationDisabled(userContext.getYukonUser())){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }

        boolean autoModeEnabledCommand = ServletRequestUtils.getBooleanParameter(request, "autoModeEnabled", false);
        
        Temperature heatTemp = null;
        if (heatTemperature != null) 
            heatTemp = thermostatService.getTempOrDefault(heatTemperature, temperatureUnit);
        
        Temperature coolTemp = null;
        if (coolTemperature != null) 
            coolTemp = thermostatService.getTempOrDefault(coolTemperature, temperatureUnit);
        
        executeManualEvent(thermostatIds, mode, fan, temperatureUnit, heatTemp, coolTemp, autoModeEnabledCommand, userContext, request, flashScope, map);
        
        if (autoModeEnabledCommand) {
            return "redirect:/spring/stars/consumer/thermostat/autoEnabledView";
        }
        
        return "redirect:/spring/stars/consumer/thermostat/view";
    }
    
    @RequestMapping(value = "/consumer/thermostat/runProgram", method = RequestMethod.POST)
    public String runProgram(@ModelAttribute("thermostatIds") List<Integer> thermostatIds, YukonUserContext userContext,
                             HttpServletRequest request, FlashScope flashScope, ModelMap map) throws Exception {
        
        if(isCommunicationDisabled(userContext.getYukonUser())){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        // Log run program attempt
        CustomerAccount account = getCustomerAccount(request);
        List<String> serialNumbers =  lmHardwareBaseDao.getSerialNumberForInventoryIds(thermostatIds);
        for (String serialNumber : serialNumbers) {
            accountEventLogService.thermostatRunProgramAttemptedByConsumer(userContext.getYukonUser(), account.getAccountNumber(), serialNumber);
        }

        // Send out run program commands
        ThermostatManualEventResult result = null;
        for (int thermostatId : thermostatIds) {
            result = thermostatService.runProgram(thermostatId, userContext.getYukonUser());
            
            if (result.isFailed() && thermostatIds.size() > 1) {
                result = ThermostatManualEventResult.MULTIPLE_ERROR;
                break;
            }
        }

        setConfirmationMessage(flashScope, thermostatIds, result);
        
        //redirect to the list of saved schedules as the restored program MAY be on this page
        return "redirect:/spring/stars/consumer/thermostat/schedule/view/saved";
    }

	private void executeManualEvent(List<Integer> thermostatIds, String mode, String fan, String temperatureUnit,
	                                Temperature heatTemperature, Temperature coolTemperature, boolean autoModeEnabledCommand,
	                                YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope, ModelMap map) {
		
	    CustomerAccount account = getCustomerAccount(request);
		
        thermostatService.logConsumerThermostatManualSaveAttempt(thermostatIds, userContext, account);
        
        accountCheckerService.checkInventory(userContext.getYukonUser(), thermostatIds.toArray(new Integer[thermostatIds.size()]));
        thermostatService.updateTempUnitForCustomer(temperatureUnit, account.getCustomerId());
        ThermostatMode thermostatMode = thermostatService.getThermostatModeFromString(mode);
        ThermostatFanState fanState = ThermostatFanState.valueOf(fan);
        
        boolean isValid = true;
        
        //Validate temperature for mode and thermostat type
        if(thermostatMode.isHeatOrCool()) {
            ThermostatManualEventResult limitMessage = thermostatService.validateTempAgainstLimits(thermostatIds, heatTemperature, coolTemperature, thermostatMode);
            
            if(limitMessage != null) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.dr.consumer.manualevent.result.CONSUMER_" + limitMessage.name()));
                isValid = false;
            }
        }

        if(isValid) {
            boolean hold = ServletRequestUtils.getBooleanParameter(request, "hold", false);

            // Send out manual thermostat commands
            ThermostatManualEventResult result = ThermostatManualEventResult.MANUAL_SUCCESS;
            for (int thermostatId : thermostatIds) {
                
                result = thermostatService.executeManualEvent(thermostatId, heatTemperature, coolTemperature, thermostatMode, fanState, hold,  
                                                              autoModeEnabledCommand, account, userContext.getYukonUser());

                if (result.isFailed() && thermostatIds.size() > 1) {
                    result = ThermostatManualEventResult.MULTIPLE_ERROR;
                    break;
                }
            }
            
            // Setup the confirmation message
            setConfirmationMessage(flashScope, thermostatIds, result);
    	}
        
        // Manually put thermsotatIds into model for redirect
        map.addAttribute("thermostatIds", thermostatIds.toString());
	}
	
	private boolean isCommunicationDisabled(LiteYukonUser user){
        return !optOutStatusService.getOptOutEnabled(user).isCommunicationEnabled();
    }

    /**
     * This method takes care of setting up the flash scope message that comes back from running a manual event command.
     */
    private void setConfirmationMessage(FlashScope flashScope, List<Integer> thermostatIds, ThermostatManualEventResult result) {
        // Add thermostat labels to message
        List<String> thermostatLabels =  inventoryDao.getThermostatLabels(thermostatIds);
        
        // Setup flash scope message
        String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
        String key = "yukon.dr.consumer.manualevent.result.CONSUMER_" + result.name();
        MessageSourceResolvable messageResolvable = new YukonMessageSourceResolvable(key, thermostatLabelString);
        
        flashScope.setMessage(messageResolvable, result.isFailed() ? FlashScopeMessageType.ERROR : FlashScopeMessageType.CONFIRM);
    }
}
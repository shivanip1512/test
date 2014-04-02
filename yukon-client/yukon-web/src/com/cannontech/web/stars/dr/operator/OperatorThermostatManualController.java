package com.cannontech.web.stars.dr.operator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
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
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
@RequestMapping(value = "/operator/thermostatManual/*")
public class OperatorThermostatManualController {
    
    private static int DEFAULT_DEADBAND = 3;
    
    @Autowired private AccountEventLogService accountEventLogService;
	@Autowired private CustomerEventDao customerEventDao;
	@Autowired private CustomerDao customerDao;
	@Autowired private CustomerAccountDao customerAccountDao;
	@Autowired private InventoryDao inventoryDao;
	@Autowired private LmHardwareBaseDao lmHardwareBaseDao;
	@Autowired private OperatorThermostatHelper operatorThermostatHelper;
	@Autowired private ThermostatEventHistoryDao thermostatEventHistoryDao;
	@Autowired private ThermostatService thermostatService;
	
	// VIEW
	@RequestMapping("view")
    public String view(String thermostatIds, ModelMap modelMap, FlashScope flashScope, 
                       AccountInfoFragment accountInfoFragment, HttpServletRequest request) {

		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIdsList.get(0));
        
        //The selected thermostats should all be of the same (or at least compatible) types.  The page
        //itself operates under the context of a single interface even when multiple thermostats are
        //selected so we need to have a single thermostat model passed to the UI.
        Integer inventoryId = thermostatIdsList.get(0);
        modelMap.addAttribute("thermostat", thermostat);
        modelMap.addAttribute("inventoryId", inventoryId);
        
        // single thermostat
        if (thermostatIdsList.size() == 1) {
            modelMap.addAttribute("pageNameSuffix", "single");
        // multiple thermostats
        } else {
            modelMap.addAttribute("pageNameSuffix", "multiple");
        }
        
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        modelMap.addAttribute("temperatureUnit", temperatureUnit);
        
        ThermostatManualEvent event = null;
        for (Integer thermostatId : thermostatIdsList) {
            event = customerEventDao.getLastManualEvent(thermostatId);
            if (event.getEventId() != null) {
                break;
            }
        }
        if (event == null || event.getEventId() == null) {
            event = new ThermostatManualEvent();
        }
        modelMap.addAttribute("event", event);
        
        List<ThermostatEvent> eventHistoryList = thermostatEventHistoryDao.getEventsByThermostatIds(thermostatIdsList);

        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);

        SearchResults<ThermostatEvent> result = SearchResults.pageBasedForWholeList(currentPage, itemsPerPage, eventHistoryList);
        modelMap.addAttribute("searchResult", result);
        modelMap.addAttribute("eventHistoryList", result.getResultList());
        
        if(thermostatIdsList.size() > 1){
            modelMap.addAttribute("displayName", new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatManual.multipleLabel"));
        }else{
            //the serial number is the same as the hardwareDto.getDisplayName() that appears on the parent page.
            modelMap.addAttribute("displayName", thermostat.getSerialNumber());
        }
        
        // Check to see if auto mode is enabled for this device.
        boolean autoThermostatModeEnabled = thermostatService.isAutoModeAvailable(accountInfoFragment.getAccountId(), inventoryId);
        modelMap.addAttribute("autoModeEnabled", autoThermostatModeEnabled);
        modelMap.addAttribute("deadband", DEFAULT_DEADBAND);
        
        // Check if we're in the auto mode enabled view.
        if (modelMap.get("autoModeEnabledCommandView") == null) {
            modelMap.addAttribute("autoModeEnabledCommandView", false);
        }
        
        return "operator/operatorThermostat/manual/view.jsp";
    }

	   // VIEW
    @RequestMapping("autoEnabledView")
    public String autoEnabledView(String thermostatIds, ModelMap modelMap, FlashScope flashScope, 
                       AccountInfoFragment accountInfoFragment, HttpServletRequest request) {

        modelMap.addAttribute("autoModeEnabledCommandView", true);
        return view(thermostatIds, modelMap, flashScope, accountInfoFragment, request);
    }
	
	// SAVE
	@RequestMapping("save")
    public String save(String thermostatIds, String mode, String fan, String temperatureUnit, Double heatTemperature, Double coolTemperature,
                       YukonUserContext userContext, HttpServletRequest request, ModelMap modelMap,
                       FlashScope flashScope, AccountInfoFragment accountInfoFragment) {

	    boolean autoModeEnabledCommand = ServletRequestUtils.getBooleanParameter(request, "autoModeEnabled", false);
	    
	    Temperature heatTemp = null;
	    if (heatTemperature != null) 
	        heatTemp = thermostatService.getTempOrDefault(heatTemperature, temperatureUnit);

	    Temperature coolTemp = null;
	    if (coolTemperature != null) 
	        coolTemp = thermostatService.getTempOrDefault(coolTemperature, temperatureUnit);
	    
	    executeManualEvent(thermostatIds, mode, fan, temperatureUnit, heatTemp, coolTemp, autoModeEnabledCommand, userContext, request, modelMap, flashScope, accountInfoFragment);
		
	    if (autoModeEnabledCommand) {
	        return "redirect:autoEnabledView";
	    }
	    
	    return "redirect:view";
    }
	
	@RequestMapping("runProgram")
    public String runProgram(String thermostatIds, YukonUserContext userContext, ModelMap modelMap,
                             FlashScope flashScope, AccountInfoFragment accountInfoFragment) {
	    List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);

        // Log run program attempt
	    CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        List<String> serialNumbers =  lmHardwareBaseDao.getSerialNumberForInventoryIds(thermostatIdsList);
        for (String serialNumber : serialNumbers) {
            accountEventLogService.thermostatRunProgramAttempted(userContext.getYukonUser(), customerAccount.getAccountNumber(), serialNumber, EventSource.OPERATOR);
        }
	    
        // Send out run program commands
        ThermostatManualEventResult result = null;
        for (int thermostatId : thermostatIdsList) {
            result = thermostatService.runProgram(thermostatId, userContext.getYukonUser());
            
            if (result.isFailed() && thermostatIdsList.size() > 1) {
                result = ThermostatManualEventResult.MULTIPLE_ERROR;
                break;
            }
        }

	    setConfirmationMessage(flashScope, thermostatIdsList, result);
	    return "redirect:/stars/operator/thermostatSchedule/savedSchedules";
    }
	
    private void executeManualEvent(String thermostatIds, String mode, String fan, String temperatureUnit, Temperature heatTemp, 
                                    Temperature coolTemp, boolean autoModeEnabledCommand, YukonUserContext userContext, 
                                    HttpServletRequest request, ModelMap modelMap, FlashScope flashScope, AccountInfoFragment accountInfoFragment) {

        List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());

        thermostatService.logThermostatManualSaveAttempt(thermostatIdsList, userContext, customerAccount,
                                                         (heatTemp != null) ? heatTemp.toFahrenheit().getValue() : null,
                                                         (coolTemp != null) ? coolTemp.toFahrenheit().getValue() : null,
                                                         mode, autoModeEnabledCommand, 
                                                         EventSource.OPERATOR);

        ThermostatMode thermostatMode = thermostatService.getThermostatModeFromString(mode);
        ThermostatFanState fanState = ThermostatFanState.valueOf(fan);
        thermostatService.updateTempUnitForCustomer(temperatureUnit, customerAccount.getCustomerId());
        
        boolean isValid = true;
        
        //Validate temperature for mode and thermostat type
        if(thermostatMode.isHeatOrCool()) {
            ThermostatManualEventResult limitMessage = thermostatService.validateTempAgainstLimits(thermostatIdsList, heatTemp, coolTemp, thermostatMode);
            
            if(limitMessage != null) {
                String key = "yukon.dr.consumer.manualevent.result.OPERATOR_" + limitMessage.name();
                MessageSourceResolvable messageResolvable = new YukonMessageSourceResolvable(key);
                flashScope.setError(messageResolvable);
                isValid = false;
            }
        }
        
        if(isValid) {
            boolean hold = ServletRequestUtils.getBooleanParameter(request, "hold", false);
            
            // Send out manual thermostat commands
            ThermostatManualEventResult result = ThermostatManualEventResult.MANUAL_SUCCESS;
            for (int thermostatId : thermostatIdsList) {
                
                result = thermostatService.executeManualEvent(thermostatId, heatTemp, coolTemp, thermostatMode, fanState, hold,  autoModeEnabledCommand, customerAccount, userContext.getYukonUser());
                if (result.isFailed() && thermostatIdsList.size() > 1) {
                    result = ThermostatManualEventResult.MULTIPLE_ERROR;
                    break;
                }
            }

            setConfirmationMessage(flashScope, thermostatIdsList,  result);
        }
    }

    /**
     * This method takes care of setting up the flash scope message that comes back from running a manual event command.
     */
    private void setConfirmationMessage(FlashScope flashScope, List<Integer> thermostatIds, ThermostatManualEventResult result) {
        // Add thermostat labels to message
        List<String> thermostatLabels =  inventoryDao.getThermostatLabels(thermostatIds);
        
        // Setup flash scope message
        String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
        String key = "yukon.dr.consumer.manualevent.result.OPERATOR_" + result.name();
        MessageSourceResolvable messageResolvable = new YukonMessageSourceResolvable(key, thermostatLabelString);
        
        flashScope.setMessage(messageResolvable, result.isFailed() ? FlashScopeMessageType.ERROR : FlashScopeMessageType.SUCCESS);
    }
}
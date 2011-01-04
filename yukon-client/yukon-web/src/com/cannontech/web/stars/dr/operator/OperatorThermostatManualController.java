package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
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
	
    private AccountEventLogService accountEventLogService;
    
	private InventoryDao inventoryDao;
	private CustomerEventDao customerEventDao;
	private CustomerDao customerDao;
	private CustomerAccountDao customerAccountDao;
	private ThermostatService thermostatService;
	private OperatorThermostatHelper operatorThermostatHelper;
	
	// VIEW
	@RequestMapping
    public String view(String thermostatIds,
				    		ModelMap modelMap,
					         FlashScope flashScope,
					         AccountInfoFragment accountInfoFragment) {

		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		
        ThermostatManualEvent event;
        
        // single thermostat
        if (thermostatIdsList.size() == 1) {
            
        	int thermostatId = thermostatIdsList.get(0);
        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        	modelMap.addAttribute("thermostat", thermostat);
        	
            event = customerEventDao.getLastManualEvent(thermostatId);
            
        // multiple thermostats
        } else {
            event = new ThermostatManualEvent();
        }
        
        
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        event.setTemperatureUnit(temperatureUnit);
        modelMap.addAttribute("event", event);

        return "operator/operatorThermostat/manual/view.jsp";
    }
	
	// SAVE
	@RequestMapping
    public String save(String thermostatIds,
				    		String mode, 
				    		String fan, 
				    		String temperatureUnit, 
				    		YukonUserContext userContext,
				            HttpServletRequest request, 
				            ModelMap modelMap,
				            FlashScope flashScope,
					        AccountInfoFragment accountInfoFragment) {

		executeManualEvent(thermostatIds, mode, fan, temperatureUnit, userContext, request, modelMap, flashScope, accountInfoFragment);
		
        return "redirect:view";
    }
	
	// RUN PROGRAM
	@RequestMapping
    public String runProgram(String thermostatIds,
				    		String mode, 
				    		String fan, 
				    		String temperatureUnit, 
				    		YukonUserContext userContext,
				            HttpServletRequest request, 
				            ModelMap modelMap,
				            FlashScope flashScope,
					        AccountInfoFragment accountInfoFragment) {

		executeManualEvent(thermostatIds, mode, fan, temperatureUnit, userContext, request, modelMap, flashScope, accountInfoFragment);
		
        return "redirect:/spring/stars/operator/thermostatSchedule/view";
    }
	
    private void executeManualEvent(String thermostatIds,
				    		String mode, 
				    		String fan, 
				    		String temperatureUnit, 
				    		YukonUserContext userContext,
				            HttpServletRequest request, 
				            ModelMap modelMap,
				            FlashScope flashScope,
					        AccountInfoFragment accountInfoFragment) {

		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        
        // Log thermostat manual event save attempt
		for (int thermostatId : thermostatIdsList) {
            Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
            
            accountEventLogService.thermostatManualSetAttemptedByOperator(userContext.getYukonUser(),
                                                                          accountInfoFragment.getAccountNumber(),
                                                                          thermostat.getSerialNumber());
        }

		
        ThermostatManualEventResult message = null;
        boolean failed = false;

        //Update the temperature unit for this customer
        if ("C".equalsIgnoreCase(temperatureUnit) || "F".equalsIgnoreCase(temperatureUnit) ) {
            customerDao.setTempForCustomer(customerAccount.getCustomerId(), temperatureUnit);
        } else {
        	throw new IllegalArgumentException("Invalid temperature unit set.");
        }

        
        for (int thermostatId : thermostatIdsList) {

            boolean hold = ServletRequestUtils.getBooleanParameter(request, "hold", false);
            int temperature = ServletRequestUtils.getIntParameter(request, "temperature", ThermostatManualEvent.DEFAULT_TEMPERATURE);

            // See if the run program button was clicked
            String runProgramButtonClicked = ServletRequestUtils.getStringParameter(request, "runProgram", null);
            boolean runProgram = runProgramButtonClicked != null;

            // Convert to fahrenheit temperature
            temperature = (int)CtiUtilities.convertTemperature(temperature, temperatureUnit, CtiUtilities.FAHRENHEIT_CHARACTER);

            // Build up manual event from submitted params
            ThermostatManualEvent event = new ThermostatManualEvent();
            event.setThermostatId(thermostatId);
            event.setHoldTemperature(hold);
            event.setPreviousTemperature(temperature);
            event.setTemperatureUnit(temperatureUnit);
            event.setRunProgram(runProgram);
            event.setEventType(CustomerEventType.THERMOSTAT_MANUAL);
            event.setAction(CustomerAction.MANUAL_OPTION);

            // Mode and fan can be blank
            if (runProgram) {
                event.setMode(ThermostatMode.DEFAULT);
            } else if (!StringUtils.isBlank(mode)) {
                ThermostatMode thermostatMode = ThermostatMode.valueOf(mode);
                event.setMode(thermostatMode);
            }
            if (!StringUtils.isBlank(fan)) {
                ThermostatFanState fanState = ThermostatFanState.valueOf(fan);
                event.setFanState(fanState);
            }

            // Execute manual event and get result
            message = thermostatService.executeManualEvent(customerAccount, event, userContext);

            if (message.isFailed()) {
                failed = true;
            }
        }

        // If there was a failure and we are processing multiple thermostats,
        // set error to generic multiple error
        if (failed && thermostatIdsList.size() > 1) {
            message = ThermostatManualEventResult.CONSUMER_MULTIPLE_ERROR;
        }

        // message
        List<String> thermostatLabels = new ArrayList<String>();
        for(Integer thermostatId : thermostatIdsList) {
        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        	thermostatLabels.add(thermostat.getLabel());
        }
        String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
        MessageSourceResolvable messageResolvable = new YukonMessageSourceResolvable(message.getDisplayKey(), thermostatLabelString);
        
    	flashScope.setMessage(messageResolvable, message.isFailed() ? FlashScopeMessageType.ERROR : FlashScopeMessageType.CONFIRM);
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
}

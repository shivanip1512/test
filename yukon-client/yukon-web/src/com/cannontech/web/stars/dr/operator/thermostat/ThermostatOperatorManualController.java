package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
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
import com.cannontech.web.security.annotation.CheckRole;

/**
 * Controller for Operator-side Manual thermostat operations
 */
@CheckRole(YukonRole.CONSUMER_INFO)
@Controller
public class ThermostatOperatorManualController extends AbstractThermostatOperatorController {

    private InventoryDao inventoryDao;
    private CustomerEventDao customerEventDao;
    private ThermostatService thermostatService;
    private CustomerDao customerDao;
    
    @RequestMapping(value = "/operator/thermostat/view", method = RequestMethod.GET)
    public String view(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            LiteYukonUser user, ModelMap map) throws Exception {

    	this.checkInventoryAgainstAccount(thermostatIds, customerAccount);
        
        // Get the first (or only) thermostat and add to model
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatIds.get(0));
        map.addAttribute("thermostat", thermostat);

        
        ThermostatManualEvent event;
        if (thermostatIds.size() == 1) {
            // single thermostat selected
            int thermostatId = thermostatIds.get(0);

            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.operator.thermostat.label",
                                                                                       thermostat.getLabel());
            map.addAttribute("thermostatLabel", resolvable);

            event = customerEventDao.getLastManualEvent(thermostatId);
        } else {
            // multiple thermostats selected
            YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.dr.operator.thermostat.multipleLabel");
            map.addAttribute("thermostatLabel", resolvable);

            event = new ThermostatManualEvent();
        }
        
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        String temperatureUnit = customer.getTemperatureUnit();
        event.setTemperatureUnit(temperatureUnit);

        map.addAttribute("event", event);

        return "operator/thermostat/thermostat.jsp";
    }

    @RequestMapping(value = "/operator/thermostat/manual", method = RequestMethod.POST)
    public String manual(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		String mode, String fan, String temperatureUnit, YukonUserContext userContext,
            HttpServletRequest request, ModelMap map) throws Exception {

    	this.checkInventoryAgainstAccount(thermostatIds, customerAccount);
        
        ThermostatManualEventResult message = null;
        boolean failed = false;

        //Update the temperature unit for this customer
        String escapedTempUnit = StringEscapeUtils.escapeHtml(temperatureUnit);
        if(StringUtils.isNotBlank(escapedTempUnit) && (escapedTempUnit.equalsIgnoreCase("C") || escapedTempUnit.equalsIgnoreCase("F")) ) {
            customerDao.setTempForCustomer(customerAccount.getCustomerId(), escapedTempUnit);
        }
        
        for (Integer thermostatId : thermostatIds) {

            boolean hold = ServletRequestUtils.getBooleanParameter(request,
                                                                   "hold",
                                                                   false);
            int temperature = ServletRequestUtils.getIntParameter(request,
                                                                  "temperature",
                                                                  ThermostatManualEvent.DEFAULT_TEMPERATURE);

            // See if the run program button was clicked
            String runProgramButtonClicked = ServletRequestUtils.getStringParameter(request,
                                                                                    "runProgram",
                                                                                    null);
            boolean runProgram = runProgramButtonClicked != null;

            // Convert to fahrenheit temperature
            temperature = (int) CtiUtilities.convertTemperature(temperature,
            		temperatureUnit,
            		CtiUtilities.FAHRENHEIT_CHARACTER);

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
            message = thermostatService.executeManualEvent(customerAccount,
                                                           event,
                                                           userContext);

            if (message.isFailed()) {
                failed = true;
            }
        }

        // If there was a failure and we are processing multiple thermostats,
        // set error to generic multiple error
        if (failed && thermostatIds.size() > 1) {
            message = ThermostatManualEventResult.CONSUMER_MULTIPLE_ERROR;
        }

        map.addAttribute("message", message.toString());

        // Manually put thermsotatIds into model for redirect
        map.addAttribute("thermostatIds", thermostatIds.toString());

        return "redirect:/operator/Consumer/ThermostatMessage.jsp";
    }

    @RequestMapping(value = "/operator/manualComplete", method = RequestMethod.GET)
    public String manualComplete(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            String message, LiteYukonUser user, ModelMap map, HttpServletRequest request) 
    	throws Exception {

    	this.checkInventoryAgainstAccount(thermostatIds, customerAccount);
        
        ThermostatManualEventResult resultMessage = ThermostatManualEventResult.valueOf(message);
        String key = resultMessage.getDisplayKey();

        List<String> thermostatLabels = new ArrayList<String>();
        for(Integer thermostatId : thermostatIds) {
        	Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        	thermostatLabels.add(thermostat.getLabel());
        }
        
        String thermostatLabelString = StringUtils.join(thermostatLabels, ", ");
        YukonMessageSourceResolvable resolvable = 
        	new YukonMessageSourceResolvable(key, thermostatLabelString);
        
        this.addThermostatModelAttribute(request, map, thermostatIds);
        
        map.addAttribute("message", resolvable);

        StringBuffer viewUrl = new StringBuffer("/operator/Consumer/Thermostat.jsp?");
        if(thermostatIds.size() > 1) {
        	viewUrl.append("AllTherm=true");
    	} else {
    		Integer thermostatId = thermostatIds.get(0);
    		int inventoryNumber = this.getInventoryNumber(request, thermostatId);
    		viewUrl.append("InvNo=" + inventoryNumber);
    	}
        map.addAttribute("viewUrl", viewUrl.toString());

        return "operator/thermostat/actionComplete.jsp";
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

}

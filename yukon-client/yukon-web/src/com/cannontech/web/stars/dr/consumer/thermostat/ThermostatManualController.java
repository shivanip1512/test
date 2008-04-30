package com.cannontech.web.stars.dr.consumer.thermostat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.servlet.YukonUserContextUtils;
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
import com.cannontech.web.stars.dr.consumer.AbstractConsumerController;

/**
 * Controller for Manual thermostat operations
 */
@Controller
public class ThermostatManualController extends AbstractConsumerController {

    private InventoryDao inventoryDao;
    private CustomerEventDao customerEventDao;
    private ThermostatService thermostatService;

    @RequestMapping(value = "/consumer/thermostat/view", method = RequestMethod.GET)
    public String view(ModelMap map, int thermostatId) {

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        map.addAttribute("thermostat", thermostat);

        ThermostatManualEvent event = customerEventDao.getLastManualEvent(thermostatId);
        map.addAttribute("event", event);

        return "consumer/thermostat.jsp";
    }

    @RequestMapping(value = "/consumer/thermostat/saveLabel", method = RequestMethod.POST)
    public String saveLabel(ModelMap map, int thermostatId, String displayLabel) {

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        thermostat.setDeviceLabel(displayLabel);

        inventoryDao.save(thermostat);

        map.addAttribute("thermostatId", thermostatId);

        return "redirect:/spring/stars/consumer/thermostat/view";
    }

    @RequestMapping(value = "/consumer/thermostat/manual", method = RequestMethod.POST)
    public String manual(HttpServletRequest request, ModelMap map,
            int thermostatId, String mode, String fan, String temperatureUnit) {

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
        if ("c".equalsIgnoreCase(temperatureUnit)) {
            temperature = (int) CtiUtilities.convertTemperature(temperature,
                                                                CtiUtilities.CELSIUS_CHARACTER,
                                                                CtiUtilities.FAHRENHEIT_CHARACTER);
        }

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
        if (!StringUtils.isBlank(mode)) {
            ThermostatMode thermostatMode = ThermostatMode.valueOf(mode);
            event.setMode(thermostatMode);
        }
        if (!StringUtils.isBlank(fan)) {
            ThermostatFanState fanState = ThermostatFanState.valueOf(fan);
            event.setFanState(fanState);
        }

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        CustomerAccount account = getCustomerAccount(request);

        // Execute manual event and get result
        ThermostatManualEventResult message = thermostatService.executeManualEvent(account,
                                                                                   event,
                                                                                   userContext);
        map.addAttribute("message", message.toString());

        map.addAttribute("thermostatId", thermostatId);

        return "redirect:/spring/stars/consumer/manualComplete";
    }

    @RequestMapping(value = "/consumer/manualComplete", method = RequestMethod.GET)
    public String manualComplete(ModelMap map, int thermostatId, String message) {

        ThermostatManualEventResult resultMessage = ThermostatManualEventResult.valueOf(message);
        map.addAttribute("message", resultMessage);

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        map.addAttribute("thermostat", thermostat);

        map.addAttribute("viewUrl", "/spring/stars/consumer/thermostat/view");

        return "consumer/actionComplete.jsp";
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

}

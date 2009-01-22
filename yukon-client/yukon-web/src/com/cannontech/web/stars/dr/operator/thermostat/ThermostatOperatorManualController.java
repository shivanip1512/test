package com.cannontech.web.stars.dr.operator.thermostat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.servlet.YukonUserContextUtils;
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
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;

/**
 * Controller for Manual thermostat operations
 */
@CheckRole(ConsumerInfoRole.ROLEID)
@Controller
public class ThermostatOperatorManualController {

    private InventoryDao inventoryDao;
    private CustomerEventDao customerEventDao;
    private CustomerAccountDao customerAccountDao;
    private ThermostatService thermostatService;
    
    @ModelAttribute("thermostatIds")
    public List<Integer> getThermostatIds(HttpServletRequest request)
            throws ServletRequestBindingException {

        String thermostatIds = ServletRequestUtils.getStringParameter(request,
                                                                      "thermostatIds");

        // Override the toString method to get a comma separated list with no
        // leading or trailing brackets
        List<Integer> idList = new ArrayList<Integer>() {
            @Override
            public String toString() {
                return super.toString().replaceAll("[\\[|\\]]", "");
            }

        };

        // If thermostatIds exists, split and create Integer list
        if (!StringUtils.isBlank(thermostatIds)) {
            String[] ids = thermostatIds.split(",");
            for (String id : ids) {
                try {
                    int idInt = Integer.parseInt(id.trim());
                    idList.add(idInt);
                } catch(NumberFormatException nfe) {
                    CTILogger.error(nfe);
                }
            }
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

    @RequestMapping(value = "/operator/thermostat/view", method = RequestMethod.GET)
    public String view(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            LiteYukonUser user, ModelMap map) throws Exception {

    	// TODO security check here!!!!!!
        
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

        map.addAttribute("event", event);

        return "operator/thermostat/thermostat.jsp";
    }

    @RequestMapping(value = "/operator/thermostat/saveLabel", method = RequestMethod.POST)
    public String saveLabel(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
            String displayLabel, LiteYukonUser user, ModelMap map) throws Exception {

    	// TODO security check here!!!!!!   
    
        if (thermostatIds.size() != 1) {
            throw new IllegalArgumentException("You can only change the label of 1 thermostat at a time.");
        }

        int thermostatId = thermostatIds.get(0);

        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        thermostat.setDeviceLabel(displayLabel);

        inventoryDao.save(thermostat);

        map.addAttribute("thermostatIds", thermostatId);

        return "redirect:/spring/stars/operator/thermostat/view";
    }

    @RequestMapping(value = "/operator/thermostat/manual", method = RequestMethod.POST)
    public String manual(@ModelAttribute("thermostatIds") List<Integer> thermostatIds,
    		@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		String mode, String fan, String temperatureUnit, LiteYukonUser user,
            HttpServletRequest request, ModelMap map) throws Exception {

    	// TODO security check here!!!!!!
        
        ThermostatManualEventResult message = null;
        boolean failed = false;

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
            String message, LiteYukonUser user, ModelMap map, HttpServletRequest request) 
    	throws Exception {

    	// TODO security check here!!!!!!
        
        ThermostatManualEventResult resultMessage = ThermostatManualEventResult.valueOf(message);
        String key = resultMessage.getDisplayKey();

        YukonMessageSourceResolvable resolvable;

        if (thermostatIds.size() == 1) {
            int id = thermostatIds.get(0);
            Thermostat thermostat = inventoryDao.getThermostatById(id);

            resolvable = new YukonMessageSourceResolvable(key,
                                                          thermostat.getLabel());

        } else {
            resolvable = new YukonMessageSourceResolvable(key);
        }
        
        // Get the first thermostat id
        Integer thermostatId = thermostatIds.get(0);
        
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

        map.addAttribute("message", resolvable);

        map.addAttribute("viewUrl", "/operator/Consumer/Thermostat.jsp?InvNo=" + count);

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
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}

    @Autowired
    public void setThermostatService(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }

}

package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Controller for Consumer-side Thermostat operations
 */
@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_THERMOSTATS_ALL)
@Controller
@RequestMapping("/consumer/thermostat/*")
public class ThermostatController extends AbstractThermostatController {

    @Autowired private InventoryDao inventoryDao;
    @Autowired private OptOutStatusService optOutStatusService;
    @Autowired private AccountCheckerService accountCheckerService;

    @RequestMapping(value = "view/all", method = RequestMethod.GET)
    public String viewAll(@ModelAttribute("customerAccount") CustomerAccount account,
                          LiteYukonUser user,
                          ModelMap map) {
        
        if(isCommunicationDisabled(user)){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        
        List<Thermostat> thermostats = inventoryDao.getThermostatsByAccount(account);
        map.addAttribute("thermostats", thermostats);

        return "consumer/allThermostats.jsp";
    }

    @RequestMapping(value = "view/allSelected", method = RequestMethod.POST)
    public String allSelected(@ModelAttribute("thermostatIds") List<Integer> thermostatIds, 
                              @ModelAttribute("customerAccount") CustomerAccount account,
                              LiteYukonUser user, 
                              HttpServletRequest request, 
                              ModelMap map) {

        if(isCommunicationDisabled(user)){
            return "consumer/thermostat/thermostatDisabled.jsp";
        }
        
        if(StringUtils.isBlank(thermostatIds.toString())) {
            return "redirect:/stars/consumer/thermostat/view/all";
        }
        
        accountCheckerService.checkInventory(user, thermostatIds.toArray(new Integer[thermostatIds.size()]));
        
        // Manually put thermsotatIds into model for redirect
        map.addAttribute("thermostatIds", thermostatIds.toString());

        boolean scheduleClicked = ServletRequestUtils.getStringParameter(request,
                                                                         "schedule",
                                                                         null) != null;

        if (scheduleClicked) {
            return "redirect:/stars/consumer/thermostat/schedule/view/saved";
        }
        
        return "redirect:/stars/consumer/thermostat/view";
    }
    
    private boolean isCommunicationDisabled(LiteYukonUser user){
        return !optOutStatusService.getOptOutEnabled(user).isCommunicationEnabled();
    }

}

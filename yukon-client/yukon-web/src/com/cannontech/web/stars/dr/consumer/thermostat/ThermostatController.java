package com.cannontech.web.stars.dr.consumer.thermostat;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Controller for Thermostat operations
 */
@Controller
public class ThermostatController extends AbstractThermostatController {

    private InventoryDao inventoryDao;

    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_THERMOSTATS_ALL)
    @RequestMapping(value = "/consumer/thermostat/view/all", method = RequestMethod.GET)
    public String viewAll(HttpServletRequest request, ModelMap map,
            @ModelAttribute("customerAccount") CustomerAccount account) {

        List<Thermostat> thermostats = inventoryDao.getThermostatsByAccount(account);
        map.addAttribute("thermostats", thermostats);

        return "consumer/allThermostats.jsp";
    }

    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_THERMOSTATS_ALL)
    @RequestMapping(value = "/consumer/thermostat/view/allSelected", method = RequestMethod.POST)
    public String allSelected(HttpServletRequest request, ModelMap map,
            @ModelAttribute("thermostatIds") List<Integer> thermostatIds, 
            @ModelAttribute("customerAccount") CustomerAccount account) {

        // Manually put thermsotatIds into model for redirect
        map.addAttribute("thermostatIds", thermostatIds.toString());

        boolean scheduleClicked = ServletRequestUtils.getStringParameter(request,
                                                                         "schedule",
                                                                         null) != null;

        if (scheduleClicked) {
            return "redirect:/spring/stars/consumer/thermostat/schedule/view";
        }
        
        return "redirect:/spring/stars/consumer/thermostat/view";
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

}

package com.cannontech.web.stars.dr.operator.thermostat;

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

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Controller for Consumer-side Thermostat operations
 */
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL)
@Controller
public class AllThermostatOperatorController extends AbstractThermostatOperatorController {

    private InventoryDao inventoryDao;

    @RequestMapping(value = "/operator/thermostat/view/all", method = RequestMethod.GET)
    public String viewAll(@ModelAttribute("customerAccount") CustomerAccount account, 
            ModelMap map) {

        List<Thermostat> thermostats = inventoryDao.getThermostatsByAccount(account);
        map.addAttribute("thermostats", thermostats);

        return "operator/thermostat/allThermostats.jsp";
    }

    @RequestMapping(value = "/operator/thermostat/view/allSelected", method = RequestMethod.POST)
    public String allSelected(@ModelAttribute("thermostatIds") List<Integer> thermostatIds, 
            @ModelAttribute("customerAccount") CustomerAccount account,
            LiteYukonUser user, HttpServletRequest request, ModelMap map) {

        if(StringUtils.isBlank(thermostatIds.toString())) {
            return "redirect:/operator/Consumer/AllTherm.jsp";
        }
        
        this.checkInventoryAgainstAccount(thermostatIds, account);
        
        this.addThermostatModelAttribute(request, map, thermostatIds);
        
        if(thermostatIds.size() > 1) {
        	request.getSession().setAttribute(
        			ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS, 
        			thermostatIds.toArray(new Integer[]{}));
        }

        boolean scheduleClicked = ServletRequestUtils.getStringParameter(request,
                                                                         "schedule",
                                                                         null) != null;

        if (scheduleClicked) {
            return "redirect:/operator/Consumer/ThermSchedule.jsp";
        }
        
        return "redirect:/operator/Consumer/Thermostat.jsp";
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

}

package com.cannontech.web.stars.dr.operator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatEventHistoryDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;

@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT)
@Controller
@RequestMapping(value = "/operator/thermostat/history/*")
public class OperatorThermostatHistoryController {

    @Autowired private InventoryDao inventoryDao;
    @Autowired private CustomerEventDao customerEventDao;
    @Autowired private OperatorThermostatHelper operatorThermostatHelper;
    @Autowired private ThermostatEventHistoryDao thermostatEventHistoryDao;
    
    // VIEW history for 1 or more thermostats
    @RequestMapping("view")
    public String view(String thermostatIds, ModelMap modelMap, FlashScope flashScope, AccountInfoFragment accountInfoFragment, HttpServletRequest request) {

        List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
        
        operatorThermostatHelper.setupModelMapForCommandHistory(modelMap, request, thermostatIdsList, accountInfoFragment.getAccountId());
        
        return "operator/operatorThermostat/history/view.jsp";
    }
}

package com.cannontech.web.stars.dr.operator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorThermostatHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@CheckRoleProperty({YukonRoleProperty.OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT})
@Controller
@RequestMapping(value = "/operator/thermostatSelect/*")
public class OperatorThermostatSelectController {
	
	private InventoryDao inventoryDao;
	private OperatorThermostatHelper operatorThermostatHelper;
	
	// SELECT THERMOSTATS
	@RequestMapping
    public String select(String inventoryIds,
    					ModelMap modelMap, 
    					YukonUserContext userContext,
    					FlashScope flashScope,
    					AccountInfoFragment accountInfoFragment) {
		
		
		List<Thermostat> thermostats = inventoryDao.getThermostatsByAccountId(accountInfoFragment.getAccountId());
		Iterable<Thermostat> schedualableThermostats = Iterables.filter(thermostats, new Predicate<Thermostat>() {
            @Override
            public boolean apply(Thermostat tstat) {
                return tstat.getType().isSupportsSchedules();
            }
		});
		modelMap.addAttribute("thermostats", Lists.newArrayList(schedualableThermostats));
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		return "operator/operatorThermostat/select.jsp";
	}
	
	// SELECT THERMOSTATS REDIRECT
	@RequestMapping
    public String selectRedirect(String thermostatIds, 
    							 String schedule,
						         ModelMap modelMap,
						         FlashScope flashScope,
						         AccountInfoFragment accountInfoFragment) {

		List<Integer> thermostatIdsList = operatorThermostatHelper.setupModelMapForThermostats(thermostatIds, accountInfoFragment, modelMap);
		
		// double check single type
		HardwareType selectedType = null;
		for (int thermostatId : thermostatIdsList) {
			
			Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
			HardwareType type = thermostat.getType();
			if (selectedType == null) {
				selectedType = type;
			} else {
				if (type != selectedType) {
					flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatSelect.twoTypes"));
		            return "redirect:select";
				}
			}
		}
		
        if(thermostatIdsList.size() <= 0) {
        	flashScope.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.operator.thermostatSelect.mustSelect"));
            return "redirect:select";
        }
        
        if (schedule != null) {
        	return "redirect:/stars/operator/thermostatSchedule/savedSchedules";
        } else {
        	return "redirect:/stars/operator/thermostatManual/view";
        }
    }
	
	@Autowired
	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
	
	@Autowired
	public void setOperatorThermostatHelper(OperatorThermostatHelper operatorThermostatHelper) {
		this.operatorThermostatHelper = operatorThermostatHelper;
	}
}

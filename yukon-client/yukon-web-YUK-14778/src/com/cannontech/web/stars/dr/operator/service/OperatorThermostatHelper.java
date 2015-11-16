package com.cannontech.web.stars.dr.operator.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;

public interface OperatorThermostatHelper {

	public List<Integer> setupModelMapForThermostats(String thermostatIds, 
	                                                 AccountInfoFragment accountInfoFragment, 
	                                                 ModelMap modelMap) throws IllegalArgumentException;
	
	public void setupModelMapForCommandHistory(ModelMap modelMap,
            HttpServletRequest request, List<Integer> thermostatIdsList, int accountId);
	
	public void setupModelMapForCommandHistory(ModelMap modelMap,
            HttpServletRequest request, List<Integer> thermostatIdsList,
            int accountId, int numPerPage);

    /**
     * Helper method to default the 2nd and 3rd time/temp values for a two
     * time/temp schedule
     * @param schedule - Schedule to default
     */
    public void setToTwoTimeTemps(AccountThermostatSchedule schedule);
    
    public String generateDefaultNameForUnnamedSchdule(AccountThermostatSchedule ats, String thermostatLabel, YukonUserContext yukonUserContext);
}

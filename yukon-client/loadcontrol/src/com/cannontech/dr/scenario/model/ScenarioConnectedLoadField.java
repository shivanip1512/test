package com.cannontech.dr.scenario.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;


public class ScenarioConnectedLoadField extends ScenarioBackingFieldBase {

    @Autowired private EstimatedLoadService estimatedLoadService;

    @Override
    public String getFieldName() {
        return "CONNECTED_LOAD";
    }

    @Override
    public Object getScenarioValue(Scenario scenario, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        try {
            estimatedLoadAmount = estimatedLoadService.retrieveEstimatedLoadValue(
                    scenario.getPaoIdentifier());
        } catch (EstimatedLoadCalculationException e) {
            return blankFieldResolvable;
        }
        MessageSourceResolvable amount = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.loadInKw", estimatedLoadAmount.getConnectedLoad());
        return amount;
    }
}

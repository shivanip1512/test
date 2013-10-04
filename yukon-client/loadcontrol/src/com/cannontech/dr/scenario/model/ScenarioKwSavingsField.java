package com.cannontech.dr.scenario.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;

public class ScenarioKwSavingsField extends ScenarioBackingFieldBase {

    @Autowired private EstimatedLoadService estimatedLoadService;

    @Override
    public String getFieldName() {
        return "KW_SAVINGS";
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
        YukonMessageSourceResolvable kwSavings = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.kwSavings", 
                estimatedLoadAmount.getMaxKwSavings(),
                estimatedLoadAmount.getNowKwSavings());
        return kwSavings;
    }
}

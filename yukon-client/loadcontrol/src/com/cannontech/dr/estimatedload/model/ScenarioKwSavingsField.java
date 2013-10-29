package com.cannontech.dr.estimatedload.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.program.service.impl.EstimatedLoadBackingFieldBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;

public class ScenarioKwSavingsField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;

    @Override
    public String getFieldName() {
        return "SCENARIO_KW_SAVINGS";
    }

    @Override
    public Object getValue(int scenarioId, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        PaoIdentifier scenario = new PaoIdentifier(scenarioId, PaoType.LM_SCENARIO);
        try {
            estimatedLoadAmount = backingServiceHelper.getScenarioValue(scenario);
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

package com.cannontech.dr.scenario.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ScenarioEstimatedLoadEerrorField extends ScenarioBackingFieldBase {

    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceAccessor;

    @Override
    public String getFieldName() {
        return "ESTIMATED_LOAD_ERROR";
    }

    @Override
    public Object getScenarioValue(Scenario scenario, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        try {
            estimatedLoadAmount = estimatedLoadService.retrieveEstimatedLoadValue(
                    scenario.getPaoIdentifier());
        } catch (EstimatedLoadCalculationException e) {
            return createErrorJson(scenario, userContext, e);
        }
        if (estimatedLoadAmount.isError()) {
            return createErrorJson(scenario, userContext, estimatedLoadAmount.getException());
        }
        return createSuccessJson(scenario);
    }

    private Object createErrorJson(Scenario scenario, YukonUserContext userContext,
            EstimatedLoadCalculationException e) {
        Map<String, String> errorTooltipJSON = new HashMap<>();
        
        YukonMessageSourceResolvable error = new YukonMessageSourceResolvable(
                e.getResolvableKey(),
                e.getResolvableArgs());
        MessageSourceAccessor accessor = messageSourceAccessor.getMessageSourceAccessor(userContext);
        String errorMessage = accessor.getMessage(error);
        
        errorTooltipJSON.put("paoId", String.valueOf(scenario.getPaoIdentifier().getPaoId()));
        errorTooltipJSON.put("errorMessage", errorMessage);
        return JSONObject.fromObject(errorTooltipJSON).toString();
    }

    private Object createSuccessJson(Scenario scenario) {
        return JSONObject.fromObject(Collections.singletonMap("paoId",
                String.valueOf(scenario.getPaoIdentifier().getPaoId()))).toString();
    }

}

package com.cannontech.dr.estimatedload.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.program.service.impl.EstimatedLoadBackingFieldBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ScenarioEstimatedLoadEerrorField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceAccessor;

    @Override
    public String getFieldName() {
        return "SCENARIO_ESTIMATED_LOAD_ERROR";
    }

    @Override
    public Object getValue(int paoId, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        PaoIdentifier scenario = new PaoIdentifier(paoId, PaoType.LM_SCENARIO);
        try {
            estimatedLoadAmount = backingServiceHelper.getScenarioValue(scenario);
        } catch (EstimatedLoadCalculationException e) {
            return createErrorJson(scenario, userContext, e);
        }
        if (estimatedLoadAmount.isError()) {
            return createErrorJson(scenario, userContext, estimatedLoadAmount.getException());
        }
        return createSuccessJson(scenario);
    }

    private Object createErrorJson(PaoIdentifier scenario, YukonUserContext userContext,
            EstimatedLoadCalculationException e) {
        Map<String, String> errorTooltipJSON = new HashMap<>();
        
        MessageSourceResolvable error = new YukonMessageSourceResolvable(
                e.getResolvableKey(),
                e.getResolvableArgs());
        MessageSourceAccessor accessor = messageSourceAccessor.getMessageSourceAccessor(userContext);
        String errorMessage = accessor.getMessage(error);
        
        errorTooltipJSON.put("paoId", String.valueOf(scenario.getPaoIdentifier().getPaoId()));
        errorTooltipJSON.put("errorMessage", errorMessage);
        return JSONObject.fromObject(errorTooltipJSON).toString();
    }

    private Object createSuccessJson(PaoIdentifier scenario) {
        return JSONObject.fromObject(Collections.singletonMap("paoId", String.valueOf(scenario.getPaoId()))).toString();
    }

}

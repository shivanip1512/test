package com.cannontech.dr.controlarea.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaEstimatedLoadErrorField extends ControlAreaBackingFieldBase {

    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String getFieldName() {
        return "ESTIMATED_LOAD_ERROR";
    }

    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        try {
            estimatedLoadAmount = estimatedLoadService.retrieveEstimatedLoadValue(
                    controlArea.getPaoIdentifier());
        } catch (EstimatedLoadCalculationException e) {
            return createErrorJson(controlArea, userContext, e);
        }
        if (estimatedLoadAmount.isError()) {
            return createErrorJson(controlArea, userContext, estimatedLoadAmount.getException());
        }
        return createSuccessJson(controlArea);
    }

    private Object createErrorJson(LMControlArea controlArea, YukonUserContext userContext,
            EstimatedLoadCalculationException e) {
        Map<String, String> errorTooltipJSON = new HashMap<>();
        
        MessageSourceResolvable error = new YukonMessageSourceResolvable(
                e.getResolvableKey(),
                e.getResolvableArgs());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String errorMessage = accessor.getMessage(error);
        
        errorTooltipJSON.put("paoId", String.valueOf(controlArea.getPaoIdentifier().getPaoId()));
        errorTooltipJSON.put("errorMessage", errorMessage);
        return JSONObject.fromObject(errorTooltipJSON).toString();
    }

    private Object createSuccessJson(LMControlArea controlArea) {
        return JSONObject.fromObject(Collections.singletonMap("paoId",
                String.valueOf(controlArea.getPaoIdentifier().getPaoId()))).toString();
    }
}

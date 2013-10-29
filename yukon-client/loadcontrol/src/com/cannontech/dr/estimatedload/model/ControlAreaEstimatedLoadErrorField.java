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
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaEstimatedLoadErrorField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String getFieldName() {
        return "CONTROL_AREA_ESTIMATED_LOAD_ERROR";
    }

    @Override
    public Object getValue(int paoId, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount = null;
        PaoIdentifier controlArea = null;
        try {
            controlArea = new PaoIdentifier(paoId, PaoType.LM_CONTROL_AREA);
            estimatedLoadAmount = backingServiceHelper.getControlAreaValue(controlArea);
        } catch (EstimatedLoadCalculationException e) {
            return createErrorJson(controlArea, userContext, e);
        }
        if (estimatedLoadAmount.isError()) {
            return createErrorJson(controlArea, userContext, estimatedLoadAmount.getException());
        }
        return createSuccessJson(controlArea);
    }

    private Object createErrorJson(PaoIdentifier controlArea, YukonUserContext userContext,
            EstimatedLoadCalculationException e) {
        Map<String, String> errorTooltipJSON = new HashMap<>();
        
        MessageSourceResolvable error = new YukonMessageSourceResolvable(
                e.getResolvableKey(),
                e.getResolvableArgs());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String errorMessage = accessor.getMessage(error);
        
        errorTooltipJSON.put("paoId", String.valueOf(controlArea.getPaoId()));
        errorTooltipJSON.put("errorMessage", errorMessage);
        return JSONObject.fromObject(errorTooltipJSON).toString();
    }

    private Object createSuccessJson(PaoIdentifier controlArea) {
        return JSONObject.fromObject(Collections.singletonMap("paoId",
                String.valueOf(controlArea.getPaoId()))).toString();
    }
}

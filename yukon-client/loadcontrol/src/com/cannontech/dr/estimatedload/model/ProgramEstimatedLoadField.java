package com.cannontech.dr.estimatedload.model;

import java.util.HashMap;
import java.util.Map;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.program.service.impl.EstimatedLoadBackingFieldBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramEstimatedLoadField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String getFieldName() {
        return "PROGRAM";
    }

    @Override
    public Object getValue(int programId, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount = null;
        LMProgramBase programBase = null;
        try {
            programBase = backingServiceHelper.getLmProgramBase(programId);
            estimatedLoadAmount = backingServiceHelper.getProgramValue(programBase.getPaoIdentifier());
        } catch (EstimatedLoadCalculationException e) {
            return createErrorJson(programBase, userContext, e);
        }
        if (null == estimatedLoadAmount) {
            return createCalculatingJson(programBase, userContext);
        }
        if (estimatedLoadAmount.isError()) {
            return createErrorJson(programBase, userContext, estimatedLoadAmount.getException());
        } else {
            return createSuccessJson(programBase, estimatedLoadAmount, userContext);
        }
    }

    private Object createCalculatingJson(LMProgramBase programBase, YukonUserContext userContext) {
        Map<String, String> calculatingJson = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        MessageSourceResolvable na = new YukonMessageSourceResolvable("yukon.web.defaults.na");
        MessageSourceResolvable calculating = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.calculating");
        String naMessage = accessor.getMessage(na);
        String calcMessage = accessor.getMessage(calculating);
        
        calculatingJson.put("paoId", String.valueOf(programBase.getPaoIdentifier().getPaoId()));
        calculatingJson.put("calculating", calcMessage);
        calculatingJson.put("na", naMessage);
        return JSONObject.fromObject(calculatingJson).toString();
    }

    private Object createErrorJson(LMProgramBase program, YukonUserContext userContext,
            EstimatedLoadCalculationException e) {
        Map<String, String> errorTooltipJson = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        MessageSourceResolvable na = new YukonMessageSourceResolvable("yukon.web.defaults.na");
        MessageSourceResolvable error = new YukonMessageSourceResolvable(
                e.getResolvableKey(),
                e.getResolvableArgs());
        String errorMessage = accessor.getMessage(error);
        String naMessage = accessor.getMessage(na);
        
        errorTooltipJson.put("paoId", String.valueOf(program.getPaoIdentifier().getPaoId()));
        errorTooltipJson.put("errorMessage", errorMessage);
        errorTooltipJson.put("na", naMessage);
        return JSONObject.fromObject(errorTooltipJson).toString();
    }
    
    private Object createSuccessJson(LMProgramBase program, EstimatedLoadReductionAmount amount,
            YukonUserContext userContext) {
        Map<String, String> successJson = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        MessageSourceResolvable connectedLoad = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.loadInKw");
        MessageSourceResolvable diversifiedLoad = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.loadInKw");
        MessageSourceResolvable kwSavings = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.kwSavings", 
                amount.getMaxKwSavings(),
                amount.getNowKwSavings());
        
        successJson.put("paoId", String.valueOf(program.getPaoIdentifier().getPaoId()));
        successJson.put("connected", accessor.getMessage(connectedLoad));
        successJson.put("diversified", accessor.getMessage(diversifiedLoad));
        successJson.put("kwSavings", accessor.getMessage(kwSavings));
        return JSONObject.fromObject(successJson).toString();
    }
}

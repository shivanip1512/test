package com.cannontech.web.updater.dr;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;

public class ProgramEstimatedLoadField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String getFieldName() {
        return "PROGRAM";
    }

    @Override
    public String getValue(int programId, YukonUserContext userContext) {
        EstimatedLoadResult estimatedLoadResult = null;
        
        estimatedLoadResult = backingServiceHelper.findProgramValue(programId, false);
        if (null == estimatedLoadResult) {
            return createCalculatingJson(programId, userContext);
        }
        if (estimatedLoadResult instanceof EstimatedLoadAmount) {
            return createSuccessJson(programId, (EstimatedLoadAmount) estimatedLoadResult, userContext);
        } else if (estimatedLoadResult instanceof EstimatedLoadException) {
            return createErrorJson(programId, (EstimatedLoadException) estimatedLoadResult, userContext);
        }
        return null;
    }

    private String createCalculatingJson(int programId, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        Map<String, String> calculatingNode = Maps.newHashMapWithExpectedSize(2);
        calculatingNode.put("paoId", String.valueOf(programId));
        calculatingNode.put("status", "calc");
        try {
            return JsonUtils.toJson(calculatingNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to write map to json", e);
        }
    }

    private String createErrorJson(int programId, EstimatedLoadException exception, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        MessageSourceResolvable na = new YukonMessageSourceResolvable("yukon.web.defaults.na");
        MessageSourceResolvable exceptionMessage = backingServiceHelper.resolveException(exception, userContext);

        Map<String, String> errorNode = Maps.newHashMapWithExpectedSize(5);
        errorNode.put("paoId", String.valueOf(programId));
        errorNode.put("status", "error");
        errorNode.put("na", accessor.getMessage(na));
        errorNode.put("icon", backingServiceHelper.findIconStringForException(exception));
        errorNode.put("buttonText", backingServiceHelper.findButtonTextForException(exception, userContext));
        try {
            return JsonUtils.toJson(errorNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to write map to json", e);
        }
    }

    private String createSuccessJson(int programId, EstimatedLoadAmount amount, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        MessageSourceResolvable connectedLoad = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.loadInKw", amount.getConnectedLoad());
        MessageSourceResolvable diversifiedLoad = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.loadInKw", amount.getDiversifiedLoad());
        MessageSourceResolvable kwSavings = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.kwSavings", 
                amount.getMaxKwSavings(),
                amount.getNowKwSavings());
        
        Map<String, String> successNode = Maps.newHashMapWithExpectedSize(5);
        successNode.put("paoId", String.valueOf(programId));
        successNode.put("status", "success");
        successNode.put("connected", accessor.getMessage(connectedLoad));
        successNode.put("diversified", accessor.getMessage(diversifiedLoad));
        successNode.put("kwSavings", accessor.getMessage(kwSavings));
        try {
            return JsonUtils.toJson(successNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to write map to json", e);
        }
    }
}

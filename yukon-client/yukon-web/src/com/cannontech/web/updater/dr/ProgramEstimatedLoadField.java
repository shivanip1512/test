package com.cannontech.web.updater.dr;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

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
        
        estimatedLoadResult = backingServiceHelper.getProgramValue(programId);
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
        
        MessageSourceResolvable calculating = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.calculating");
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode calculatingNode = mapper.createObjectNode();
        calculatingNode.put("paoId", String.valueOf(programId));
        calculatingNode.put("status", "calc");
        calculatingNode.put("tooltip", accessor.getMessage(calculating));
        return calculatingNode.toString();
    }

    private String createErrorJson(int programId, EstimatedLoadException e, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        MessageSourceResolvable na = new YukonMessageSourceResolvable("yukon.web.defaults.na");
        MessageSourceResolvable exceptionMessage = backingServiceHelper.resolveException(e, userContext);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("paoId", String.valueOf(programId));
        errorNode.put("status", "error");
        errorNode.put("tooltip", accessor.getMessage(exceptionMessage));
        errorNode.put("value", accessor.getMessage(na));
        return errorNode.toString();
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
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode successNode = mapper.createObjectNode();
        successNode.put("paoId", String.valueOf(programId));
        successNode.put("status", "success");
        successNode.put("connected", accessor.getMessage(connectedLoad));
        successNode.put("diversified", accessor.getMessage(diversifiedLoad));
        successNode.put("kwSavings", accessor.getMessage(kwSavings));
        return successNode.toString();
    }
}

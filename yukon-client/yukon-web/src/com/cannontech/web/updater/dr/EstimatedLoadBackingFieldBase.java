package com.cannontech.web.updater.dr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class EstimatedLoadBackingFieldBase implements EstimatedLoadBackingField {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    protected final static MessageSourceResolvable blankFieldResolvable = 
            new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");

    protected String createSummaryJson(PaoIdentifier pao, EstimatedLoadSummary summary, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        Map<String, String> summaryNode = new HashMap<>();
        summaryNode.put("paoId", String.valueOf(pao.getPaoId()));
        
        if (summary.getTotalPrograms() == 0) {
            // There are no programs in this summary. 
            summaryNode.put("status", "error");
            summaryNode.put("icon", summary.getButtonInfo().getIcon());
            summaryNode.put("buttonText", accessor.getMessage("yukon.web.modules.dr.estimatedLoad.button.noPrograms"));
            summaryNode.put("na", accessor.getMessage(blankFieldResolvable));
        } else {
            // There are programs in this summary. Are any of them in error?
            if (summary.getErrors() > 0) {
                // There is at least one program in error.
                summaryNode.put("status", "error");
                summaryNode.put("icon", summary.getButtonInfo().getIcon());
                summaryNode.put("buttonText", accessor.getMessage(summary.getButtonInfo().getButtonKey()));
                summaryNode.put("na", accessor.getMessage(blankFieldResolvable));
            } else {
                if (summary.getCalculating() > 0) {
                    // Some program values are still being calculated.
                    summaryNode.put("status", "calc");
                } else {
                    // There are no programs in error or still being calculated. Display correct summary value.
                    summaryNode.put("status", "success");
                    EstimatedLoadAmount summaryAmount = summary.getSummaryAmount();
                    MessageSourceResolvable connectedLoad = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.loadInKw", summaryAmount.getConnectedLoad());
                    MessageSourceResolvable diversifiedLoad = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.loadInKw", summaryAmount.getDiversifiedLoad());
                    MessageSourceResolvable kwSavings = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.kwSavings", 
                            summaryAmount.getMaxKwSavings(),
                            summaryAmount.getNowKwSavings());
                    summaryNode.put("connected", accessor.getMessage(connectedLoad));
                    summaryNode.put("diversified", accessor.getMessage(diversifiedLoad));
                    summaryNode.put("kwSavings", accessor.getMessage(kwSavings));
                }
            }
        }
        try {
            return JsonUtils.toJson(summaryNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to convert map to json", e);
        }
    }
}

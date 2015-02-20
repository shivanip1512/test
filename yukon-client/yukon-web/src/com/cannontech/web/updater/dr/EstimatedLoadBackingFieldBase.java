package com.cannontech.web.updater.dr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
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

        Map<String, Object> summaryNode = new HashMap<>();
        summaryNode.put("paoId", pao.getPaoId());
        if (summary.getContributing() == 0 && summary.getCalculating() == 0) {
            summaryNode.put("status", "error");
            MessageSourceResolvable noContributingPrograms = 
                    new YukonMessageSourceResolvable("yukon.web.modules.dr.estimatedLoad.calcErrors.noContributingPrograms");
            summaryNode.put("tooltip", accessor.getMessage(noContributingPrograms));
            summaryNode.put("connected", accessor.getMessage(blankFieldResolvable));
            summaryNode.put("diversified", accessor.getMessage(blankFieldResolvable));
            summaryNode.put("kwSavings", accessor.getMessage(blankFieldResolvable));
        } else {
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
            
            if (summary.getErrors() == 0 && summary.getCalculating() == 0) {
                summaryNode.put("status", "success");
            } else if (summary.getErrors() > 0 && summary.getCalculating() == 0) {
                summaryNode.put("status", "error");
                
                if (pao.getPaoType() == PaoType.LM_CONTROL_AREA) {
                    MessageSourceResolvable errorsExist = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.calcErrors.controlAreaHasErrors");
                    summaryNode.put("tooltip", accessor.getMessage(errorsExist));
                } else if (pao.getPaoType() == PaoType.LM_SCENARIO) {
                    MessageSourceResolvable errorsExist = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.calcErrors.scenarioHasErrors");
                    summaryNode.put("tooltip", accessor.getMessage(errorsExist));
                }
            } else if (summary.getErrors() == 0 && summary.getCalculating() > 0) {
                summaryNode.put("status", "calc");
                
                if (pao.getPaoType() == PaoType.LM_CONTROL_AREA) {
                    MessageSourceResolvable calculatingPrograms = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.calcErrors.controlAreaCalculating",
                            summary.getCalculating(), summary.getTotalPrograms());
                    summaryNode.put("tooltip", accessor.getMessage(calculatingPrograms));
                } else if (pao.getPaoType() == PaoType.LM_SCENARIO) {
                    MessageSourceResolvable calculatingPrograms = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.calcErrors.scenarioCalculating",
                            summary.getCalculating(), summary.getTotalPrograms());
                    summaryNode.put("tooltip", accessor.getMessage(calculatingPrograms));
                }
            } else if (summary.getErrors() > 0 && summary.getCalculating() > 0) {
                summaryNode.put("status", "errorAndCalc");
                
                if (pao.getPaoType() == PaoType.LM_CONTROL_AREA) {
                    MessageSourceResolvable calculatingAndErrors = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.calcErrors.controlAreaCalculatingAndErrors",
                            summary.getCalculating(), summary.getTotalPrograms());
                    summaryNode.put("tooltip", accessor.getMessage(calculatingAndErrors));
                } else if (pao.getPaoType() == PaoType.LM_SCENARIO) {
                    MessageSourceResolvable calculatingAndErrors = new YukonMessageSourceResolvable(
                            "yukon.web.modules.dr.estimatedLoad.calcErrors.scenarioCalculatingAndErrors",
                            summary.getCalculating(), summary.getTotalPrograms());
                    summaryNode.put("tooltip", accessor.getMessage(calculatingAndErrors));
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

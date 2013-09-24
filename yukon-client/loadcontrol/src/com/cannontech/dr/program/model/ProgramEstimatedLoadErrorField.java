package com.cannontech.dr.program.model;

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
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramEstimatedLoadErrorField extends ProgramBackingFieldBase {

    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceAccessor;

    @Override
    public String getFieldName() {
        return "ESTIMATED_LOAD_ERROR";
    }

    private final String EMTPY_STRING = "";

    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        try {
            estimatedLoadAmount = estimatedLoadService.retrieveEstimatedLoadValue(
                    program.getPaoIdentifier());
        } catch (EstimatedLoadCalculationException e) {
            return EMTPY_STRING;
        }
        if (estimatedLoadAmount.isError()) {
            Map<String, String> errorTooltipJSON = new HashMap<>();
            
            EstimatedLoadCalculationException exception = estimatedLoadAmount.getException();
            YukonMessageSourceResolvable error = new YukonMessageSourceResolvable(
                    exception.getResolvableKey(),
                    exception.getResolvableArgs());
            MessageSourceAccessor accessor = messageSourceAccessor.getMessageSourceAccessor(userContext);
            String errorMessage = accessor.getMessage(error);
            
            errorTooltipJSON.put("programId", new Integer(program.getPaoIdentifier().getPaoId()).toString());
            errorTooltipJSON.put("errorMessage", errorMessage);
            return JSONObject.fromObject(errorTooltipJSON).toString();
        } else {
            return EMTPY_STRING;
        }
    }
}

package com.cannontech.dr.program.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramKwSavingsField extends ProgramBackingFieldBase {

    @Autowired private EstimatedLoadService estimatedLoadService;

    @Override
    public String getFieldName() {
        return "KW_SAVINGS";
    }

    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        try {
            estimatedLoadAmount = estimatedLoadService.retrieveEstimatedLoadValue(
                    program.getPaoIdentifier());
        } catch (EstimatedLoadCalculationException e) {
            return blankFieldResolvable;
        }
        if (estimatedLoadAmount.isError()) {
            return blankFieldResolvable;
        } else {
            MessageSourceResolvable kwSavings = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.kwSavings", 
                    estimatedLoadAmount.getMaxKwSavings(),
                    estimatedLoadAmount.getNowKwSavings());
            return kwSavings;
        }
    }
}

package com.cannontech.dr.program.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramDiversifiedLoadField extends ProgramBackingFieldBase {

    @Autowired private EstimatedLoadService estimatedLoadService;

    @Override
    public String getFieldName() {
        return "DIVERSIFIED_LOAD";
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
            YukonMessageSourceResolvable amount = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.loadInKw", estimatedLoadAmount.getDiversifiedLoad());
            return amount;
        }
    }
}

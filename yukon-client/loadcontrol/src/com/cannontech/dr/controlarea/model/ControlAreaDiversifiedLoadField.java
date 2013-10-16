package com.cannontech.dr.controlarea.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaDiversifiedLoadField extends ControlAreaBackingFieldBase {

    @Autowired EstimatedLoadService estimatedLoadService;

    @Override
    public String getFieldName() {
        return "DIVERSIFIED_LOAD";
    }

    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        try {
            estimatedLoadAmount = estimatedLoadService.retrieveEstimatedLoadValue(
                    controlArea.getPaoIdentifier());
        } catch (EstimatedLoadCalculationException e) {
            return blankFieldResolvable;
        }
        MessageSourceResolvable amount = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.loadInKw", estimatedLoadAmount.getDiversifiedLoad());
        return amount;
    }


}

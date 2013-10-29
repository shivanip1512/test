package com.cannontech.dr.estimatedload.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadReductionAmount;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.program.service.impl.EstimatedLoadBackingFieldBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;

public class ScenarioDiversifiedLoadField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;

    @Override
    public String getFieldName() {
        return "SCENARIO_DIVERSIFIED_LOAD";
    }

    @Override
    public Object getValue(int paoId, YukonUserContext userContext) {
        EstimatedLoadReductionAmount estimatedLoadAmount;
        PaoIdentifier scenario = new PaoIdentifier(paoId, PaoType.LM_SCENARIO);
        try {
            estimatedLoadAmount = backingServiceHelper.getScenarioValue(scenario);
        } catch (EstimatedLoadCalculationException e) {
            return blankFieldResolvable;
        }
        MessageSourceResolvable amount = new YukonMessageSourceResolvable(
                "yukon.web.modules.dr.estimatedLoad.loadInKw", estimatedLoadAmount.getDiversifiedLoad());
        return amount;
    }
}

package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ScenarioEstimatedLoadField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceAccessor;

    @Override
    public String getFieldName() {
        return "SCENARIO";
    }

    @Override
    public String getValue(int paoId, YukonUserContext userContext) {
        EstimatedLoadSummary summary;
        PaoIdentifier scenario = new PaoIdentifier(paoId, PaoType.LM_SCENARIO);
        
        summary = backingServiceHelper.getScenarioValue(scenario, false);
        
        return createSummaryJson(scenario, summary, userContext);
    }

}

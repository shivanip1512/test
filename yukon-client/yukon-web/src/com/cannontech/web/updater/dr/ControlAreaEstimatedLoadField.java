package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.user.YukonUserContext;

public class ControlAreaEstimatedLoadField extends EstimatedLoadBackingFieldBase {

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    

    @Override
    public String getFieldName() {
        return "CONTROL_AREA";
    }

    @Override
    public String getValue(int paoId, YukonUserContext userContext) {
        EstimatedLoadSummary summary = null;
        PaoIdentifier controlArea = null;
        controlArea = new PaoIdentifier(paoId, PaoType.LM_CONTROL_AREA);
        
        summary = backingServiceHelper.getControlAreaValue(controlArea, false);
        
        return createSummaryJson(controlArea, summary, userContext);
    }

}

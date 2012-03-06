package com.cannontech.yukon.api.common.service;

import java.util.List;
import java.util.Map;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.pao.PaoIdentifier;

public interface TokenStatus {
    public boolean isFinished();
    public List<PaoIdentifier> getSuccesses();
    public Map<PaoIdentifier, SpecificDeviceErrorDescription> getErrors();
    public List<PaoIdentifier> getCanceledItems();
}

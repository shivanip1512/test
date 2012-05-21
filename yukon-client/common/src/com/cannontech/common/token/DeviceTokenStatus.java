package com.cannontech.common.token;

import java.util.List;
import java.util.Map;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.pao.PaoIdentifier;

public interface DeviceTokenStatus extends TokenStatus {
    List<PaoIdentifier> getSuccesses();
    Map<PaoIdentifier, SpecificDeviceErrorDescription> getErrors();
    List<PaoIdentifier> getCanceledItems();
}

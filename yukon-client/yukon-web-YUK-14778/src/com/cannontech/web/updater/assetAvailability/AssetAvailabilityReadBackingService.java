package com.cannontech.web.updater.assetAvailability;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.assetavailability.ping.AssetAvailabilityReadResult;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class AssetAvailabilityReadBackingService extends RecentResultUpdateBackingService {
    @Autowired private AssetAvailabilityPingService assetAvailabilityPingService;
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Override
    public Object getResultValue(String paoIdString, String resultTypeStr) {
        int paoId = Integer.parseInt(paoIdString);
        AssetAvailabilityReadResult result = assetAvailabilityPingService.getReadResult(paoId);
        if(result == null) {
            return "";
        }
        AssetAvailabilityReadType aaReadTypeEnum = AssetAvailabilityReadType.valueOf(resultTypeStr);
        return aaReadTypeEnum.getValue(result);
    }
}

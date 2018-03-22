package com.cannontech.web.capcontrol;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.capcontrol.model.ZoneVoltagePointsHolder;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;

public class IvvcHelper {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private CapControlCache ccCache;
    
    public void setIgnoreFlagForPoints(ZoneVoltagePointsHolder zoneVoltagePointsHolder) {
        for (VoltageLimitedDeviceInfo deviceInfo : zoneVoltagePointsHolder.getPoints()) {
            //check for other ignore scenarios (bad quality and cap bank disabled)
            PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(deviceInfo.getPointId());
            PointQuality quality = pointValue.getPointQuality();
            if (quality != PointQuality.Manual && quality != PointQuality.Normal) {
                deviceInfo.setIgnore(true);
            }
            if (deviceInfo.getParentPaoIdentifier().getPaoType() == PaoType.CAPBANK) {
                CapBankDevice capBank = ccCache.getCapBankDevice(deviceInfo.getParentPaoIdentifier().getPaoId());
                if (capBank.getCcDisableFlag()) {
                    deviceInfo.setIgnore(true);
                }
            }
        }
    }

}

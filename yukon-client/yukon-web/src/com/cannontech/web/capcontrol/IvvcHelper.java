package com.cannontech.web.capcontrol;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.capcontrol.model.ZoneVoltagePointsHolder;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;

public class IvvcHelper {
    
    private static int outdatedPointMinutes = 3;
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private CapControlCache ccCache;
    @Autowired private ConfigurationSource configurationSource;

    public void setIgnoreFlagForPoints(ZoneVoltagePointsHolder zoneVoltagePointsHolder) {
        for (VoltageLimitedDeviceInfo deviceInfo : zoneVoltagePointsHolder.getPoints()) {
            //check for other ignore scenarios (bad quality, outdated points and cap bank disabled)
            PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(deviceInfo.getPointId());
            
            boolean badQualityOrOutdated = isBadQualityOrOutdated(pointValue);
            if (badQualityOrOutdated) {
                deviceInfo.setIgnore(true);
            }

            //check for disabled cap bank
            if (deviceInfo.getParentPaoIdentifier().getPaoType() == PaoType.CAPBANK) {
                CapBankDevice capBank = ccCache.getCapBankDevice(deviceInfo.getParentPaoIdentifier().getPaoId());
                if (capBank.getCcDisableFlag()) {
                    deviceInfo.setIgnore(true);
                }
            }
        }
    }
    
    public boolean isBadQualityOrOutdated(PointValueQualityHolder pointValue) {
        PointQuality quality = pointValue.getPointQuality();
        //check for bad quality points
        if (quality != PointQuality.Manual && quality != PointQuality.Normal) {
            return true;
        }
        //check for outdated points
        outdatedPointMinutes = configurationSource.getInteger(MasterConfigInteger.CAP_CONTROL_POINT_AGE, outdatedPointMinutes);
        Date pointDate = pointValue.getPointDataTimeStamp();
        DateTime outdatedTime = DateTime.now().minusMinutes(outdatedPointMinutes + 1);
        if (pointDate != null && outdatedTime.isAfter(pointDate.getTime())) {
            return true;
        }
        
        return false;
    }

}

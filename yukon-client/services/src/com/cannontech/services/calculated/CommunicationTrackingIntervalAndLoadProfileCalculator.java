package com.cannontech.services.calculated;

import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.common.point.PointQuality;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.cache.Cache;

/**
 * This calculator is identical to the PerIntervalAndLoadProfileCalculator except that it adds tracking
 * of the communication times for asset availability purposes. Any attribute configured to use
 * this class will also update the device's last communication time and relay 1 non zero runtime 
 * in the DynamicLcrCommunications table.
 */
public class CommunicationTrackingIntervalAndLoadProfileCalculator extends PerIntervalAndLoadProfileCalculator {

    static final int METER_RELAY_NUMBER = 1;
    
    @Autowired DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    
    /**
     * This method calls the base calculate and the point data calculations are identical to the base class.
     * The difference between this and the base class are it tracks last communication time.
     */
    @Override
    public void calculate(Cache<CacheKey, CacheValue> recentReadings, CalculationData data, List<PointData> pointData) {
        // Do the standard calculation first, then we will process the data.
        super.calculate(recentReadings, data, pointData);
        
        // Using the standard readings, insert the necessary values into the DynamicLCRCommunications table
        final int paoId = data.getPaoPointValue().getPaoIdentifier().getPaoId();
        AssetAvailabilityPointDataTimes times = new AssetAvailabilityPointDataTimes(paoId);
        
        for (PointData point : pointData) {
            // Just in case we decide to calculate and send non normal values someday, be sure to not
            // count those as valid in our timestamp calculations.
            if (point.getPointQuality() == PointQuality.Normal) {
                Instant pointTimestamp = new Instant(point.getPointDataTimeStamp().toInstant().plusMillis(point.getMillis()).toEpochMilli());

                if (times.getLastCommunicationTime() == null || times.getLastCommunicationTime().isBefore(pointTimestamp)) {
                    times.setLastCommunicationTime(pointTimestamp);
                }
                
                if (point.getValue() > 0 
                        && (times.getRelayRuntime(METER_RELAY_NUMBER) == null || times.getRelayRuntime(METER_RELAY_NUMBER).isBefore(pointTimestamp))) {
                    times.setRelayRuntime(METER_RELAY_NUMBER, pointTimestamp);
                }
            }
        }
        
        dynamicLcrCommunicationsDao.insertData(times);
    }
}
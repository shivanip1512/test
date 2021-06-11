package com.cannontech.dr.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.message.dispatch.message.PointData;

public class RuntimeCalcServiceHelper {

    @Autowired private PointDao pointDao;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private RawPointHistoryDao rphDao;

    private static final int runtimePointOffset = 5;

    private static final Logger log = YukonLogManager.getLogger(RuntimeCalcServiceHelper.class);

    public Map<Integer, DateTime> getLastRuntimes(List<YukonPao> thermostats) {
        
        Map<PaoIdentifier, PointValueQualityHolder> runtimeValues = 
                rphDao.getSingleAttributeData(thermostats, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, false, null);
        
        return runtimeValues.entrySet()
                            .stream()
                            .collect(Collectors.toMap(entry -> entry.getKey().getPaoId(), 
                                                      entry -> new DateTime(entry.getValue().getPointDataTimeStamp())));
    }

    public boolean insertRuntimes(YukonPao pao, Map<DateTime, Integer> hourlyRuntimeSeconds, Predicate<Map.Entry<DateTime, Integer>> filter) {

        log.trace("Inserting runtimes for thermostat: " + pao);

        if (hourlyRuntimeSeconds.size() == 0) {
            log.info("Skipping runtime insertion for " + pao.getPaoIdentifier() + ". Not enough new data is available for calculation.");
            return false;
        }

        LitePoint runtimePoint;
        try {
            runtimePoint = pointDao.getLitePointIdByDeviceId_Offset_PointType(pao.getPaoIdentifier().getPaoId(), 
                                                                              runtimePointOffset, 
                                                                              PointType.Analog);
        } catch (NotFoundException e) {
            log.error("Unable to insert runtime for " + pao.getPaoIdentifier() + " - no runtime point at analog offset " + runtimePointOffset + ".");
            return false;
        }

        List<PointData> pointDatas = hourlyRuntimeSeconds.entrySet().stream().filter(nullSafeFilter(filter)).map(entry -> {
            Date runtimeDate = entry.getKey().toDate();
            int seconds = entry.getValue();
            Double runtimeMinutes = seconds / 60.0;

            PointData pointData = new PointData();
            pointData.setId(runtimePoint.getLiteID());
            pointData.setType(PointType.Analog.getPointTypeId());
            pointData.setMillis(0);
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setTime(runtimeDate);
            pointData.setValue(runtimeMinutes);
            pointData.setTagsPointMustArchive(true);
            return pointData;
        }).collect(Collectors.toList());
        try {
            asyncDynamicDataSource.putValues(pointDatas);
        } catch (DispatchNotConnectedException e) {
            log.error("Unable to insert runtime for " + pao.getPaoIdentifier() + " - no dispatch connection.");
            return false;
        }
        return true;
    }

    /**
     * If the specified predicate is null, replace it with a filter that accepts all input.
     */
    public Predicate<Map.Entry<DateTime, Integer>> nullSafeFilter(Predicate<Map.Entry<DateTime, Integer>> filter) {
        if (filter == null) {
            filter = input -> true;
        }
        return filter;
    }

    public void updateAssetAvailability(PaoIdentifier paoIdentifier, Instant lastRuntime) {
        AssetAvailabilityPointDataTimes newTimes = new AssetAvailabilityPointDataTimes(paoIdentifier.getPaoId());
        newTimes.setRelayRuntime(1, lastRuntime);
        dynamicLcrCommunicationsDao.insertData(newTimes);
    }
}

package com.cannontech.dr.ecobee.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeePointUpdateService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Iterables;

public class EcobeePointUpdateServiceImpl implements EcobeePointUpdateService {

    @Autowired private AttributeService attributeService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;

    @Override
    public void updatePointData(PaoIdentifier paoIdentifier, EcobeeDeviceReadings deviceReadings) {
        // indoor/outdoor data store first valid value in hour (archives on update)
        // set heat/cool data store every interval (archives on change)
        // runtime store all values added up set to on hour BEFORE hour (archives on update)

        // device readings are 5 minutes apart. partition these into hourly buckets to work with
        Instant lastReading = null;
        Instant lastRuntime = null;
        for (List<EcobeeDeviceReading> readings : Iterables.partition(deviceReadings.getReadings(), 12)) {
            // only update runtime if we have the full hours data
            boolean shouldUpdateRuntime = readings.size() == 12;

            Instant startDate = null;
            Float indoorTempToUpdate = null;
            Float outdoorTempToUpdate = null;
            Integer runtime = 0;
            for (EcobeeDeviceReading reading : readings) {
                if (lastReading == null || reading.getDate().isAfter(lastReading)) {
                    lastReading = reading.getDate();
                }
                if (startDate == null) {
                    startDate = reading.getDate();
                }
                if (indoorTempToUpdate == null && reading.getIndoorTempInF() != null) {
                    indoorTempToUpdate = reading.getIndoorTempInF();
                }
                if (outdoorTempToUpdate == null && reading.getOutdoorTempInF() != null) {
                    outdoorTempToUpdate = reading.getOutdoorTempInF();
                }
                if (reading.getSetHeatTempInF() != null) {
                    setPointValue(paoIdentifier, BuiltInAttribute.HEAT_SET_TEMPERATURE, 
                                  startDate, reading.getSetHeatTempInF());
                }
                if (reading.getSetCoolTempInF() != null) {
                    setPointValue(paoIdentifier, BuiltInAttribute.COOL_SET_TEMPERATURE, 
                                  startDate, reading.getSetCoolTempInF());
                }
                if (reading.getRuntimeSeconds() != 0 && lastRuntime == null || reading.getDate().isAfter(lastRuntime)) {
                    lastRuntime = reading.getDate();
                }
                runtime += reading.getRuntimeSeconds();
                TrueFalse controlStatus = 
                        StringUtils.isBlank(reading.getEventActivity()) ? TrueFalse.FALSE : TrueFalse.TRUE;
                setPointValue(paoIdentifier, BuiltInAttribute.CONTROL_STATUS, 
                              reading.getDate(), controlStatus.getRawState());
            }
            if (outdoorTempToUpdate != null) {
                setPointValue(paoIdentifier, BuiltInAttribute.OUTDOOR_TEMPERATURE, startDate, outdoorTempToUpdate);
            }
            if (indoorTempToUpdate != null) {
                setPointValue(paoIdentifier, BuiltInAttribute.INDOOR_TEMPERATURE, startDate, indoorTempToUpdate);
            }
            if (shouldUpdateRuntime) {
                setPointValue(paoIdentifier, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG,
                              startDate, TimeUnit.SECONDS.toMinutes(runtime));
            }
        }
        updateAssetAvailability(paoIdentifier, lastReading, lastRuntime);
    }

    private void updateAssetAvailability(PaoIdentifier paoIdentifier, Instant lastCommTime, Instant lastRuntime) {
        if (lastCommTime != null || lastRuntime != null) {
            AllRelayCommunicationTimes commTimes = 
                    dynamicLcrCommunicationsDao.findAllRelayCommunicationTimes(Collections.singleton(paoIdentifier.getPaoId())).get(paoIdentifier.getPaoId());
            Instant currentLastCommTime = commTimes == null ? null : commTimes.getLastCommunicationTime();
            Instant currentLastRuntime = commTimes == null ? null : commTimes.getLastNonZeroRuntime();

            boolean shouldUpdate = false;
            AssetAvailabilityPointDataTimes times = new AssetAvailabilityPointDataTimes(paoIdentifier.getPaoId());
            if (currentLastCommTime == null || (lastCommTime != null && lastCommTime.isAfter(currentLastCommTime))) {
                times.setLastCommunicationTime(lastCommTime);
                shouldUpdate = true;
            }

            if (currentLastRuntime == null || (lastRuntime != null && lastRuntime.isAfter(currentLastRuntime))) {
                times.setRelayRuntime(1, lastCommTime);
                shouldUpdate = true;
            }

            if (shouldUpdate) {
                dynamicLcrCommunicationsDao.insertData(times);
            }
        }
    }

    private void setPointValue(PaoIdentifier paoIdentifier, BuiltInAttribute attribute, Instant time, double value) {
        LitePoint point = attributeService.getPointForAttribute(paoIdentifier, attribute);

        PointData pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setValue(value);
        pointData.setType(point.getPointType());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTime(time.toDate());
        pointData.setTagsDataTimestampValid(true);

        dynamicDataSource.putValue(pointData);
    }
}

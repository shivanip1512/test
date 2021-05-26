package com.cannontech.dr.ecobee.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.ecobee.model.EcobeeZeusDeviceReading;
import com.cannontech.dr.ecobee.service.EcobeeZeusPointUpdateService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.yukon.IDatabaseCache;

public class EcobeeZeusPointUpdateServiceImpl implements EcobeeZeusPointUpdateService {

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private IDatabaseCache cache;
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusPointUpdateServiceImpl.class);

    @Override
    public void updatePointData(EcobeeZeusDeviceReading reading) {

        Set<PointData> pointValues = new HashSet<>(); 
        Instant messageTimestamp = reading.getDate();

        PaoIdentifier paoIdentifier = lmHardwareBaseDao.getDeviceIdBySerialNumber(reading.getSerialNumber());
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoIdentifier.getPaoId());

        if (reading.getSetHeatTempInF() != null) {
            inputPointValue(pointValues, pao, BuiltInAttribute.HEAT_SET_TEMPERATURE, messageTimestamp, reading.getSetHeatTempInF());
        }
        if (reading.getSetCoolTempInF() != null) {
            inputPointValue(pointValues, pao, BuiltInAttribute.COOL_SET_TEMPERATURE, messageTimestamp, reading.getSetCoolTempInF());
        }
        
        TrueFalse controlStatus = reading.getControlStatus();
        inputPointValue(pointValues, pao, BuiltInAttribute.CONTROL_STATUS, reading.getDate(), controlStatus.getRawState());

        if (reading.getOutdoorTempInF() != null) {
            inputPointValue(pointValues, pao, BuiltInAttribute.OUTDOOR_TEMPERATURE, messageTimestamp, reading.getOutdoorTempInF());
        }
        if (reading.getIndoorTempInF() != null) {
            inputPointValue(pointValues, pao, BuiltInAttribute.INDOOR_TEMPERATURE, messageTimestamp, reading.getIndoorTempInF());
        }
        if (reading.getCommStatus() != null) {
            inputPointValue(pointValues, pao, BuiltInAttribute.COMM_STATUS, messageTimestamp, reading.getCommStatus());
        }
        if (reading.getStateValue() != null) {
            inputPointValue(pointValues, pao, BuiltInAttribute.THERMOSTAT_RELAY_STATE, reading.getDate(), reading.getStateValue());
        }
        updateAssetAvailability(paoIdentifier, messageTimestamp);
        asyncDynamicDataSource.putValues(pointValues);
    }

    private void updateAssetAvailability(PaoIdentifier paoIdentifier, Instant time) {
        AssetAvailabilityPointDataTimes times = new AssetAvailabilityPointDataTimes(paoIdentifier.getPaoId());
        times.setLastCommunicationTime(time);
        dynamicLcrCommunicationsDao.insertData(times);
    }

    private void inputPointValue(Set<PointData> pointValues, LiteYukonPAObject pao, BuiltInAttribute attribute, Instant messageTimestamp, double value) {
        LitePoint litePoint = null;
        try {
            litePoint = attributeService.createAndFindPointForAttribute(pao, attribute);
        } catch (Exception e) {
            log.debug("Cannot get point for {} on {}. {}", attribute, pao, e.getMessage());
        }

        if (litePoint != null) {
            PointData pointData = new PointData();
            pointData.setId(litePoint.getLiteID());
            pointData.setValue(value);
            pointData.setType(litePoint.getPointType());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setTime(messageTimestamp.toDate());
            pointData.setTagsDataTimestampValid(true);

            pointValues.add(pointData);
            log.debug("Parsed point data for {} - {} ({} {}). Value: {} {}.",
                      pao.getPaoName(),
                      litePoint.getPointName(),
                      litePoint.getPointTypeEnum(),
                      litePoint.getPointOffset(),
                      pointData.getValue(),
                      pointData.getPointDataTimeStamp());
        }
    }

}

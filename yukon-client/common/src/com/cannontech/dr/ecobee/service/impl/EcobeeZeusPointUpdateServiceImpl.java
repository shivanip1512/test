package com.cannontech.dr.ecobee.service.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.ecobee.model.EcobeeZeusDeviceReading;
import com.cannontech.dr.ecobee.service.EcobeeZeusPointUpdateService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;

public class EcobeeZeusPointUpdateServiceImpl implements EcobeeZeusPointUpdateService {

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;

    @Override
    public void updatePointData(EcobeeZeusDeviceReading reading) {

        Instant messageTimestamp = reading.getDate();
        PaoIdentifier paoIdentifier = lmHardwareBaseDao.getDeviceIdBySerialNumber(reading.getSerialNumber());

        if (reading.getSetHeatTempInF() != null) {
            inputPointValue(paoIdentifier, BuiltInAttribute.HEAT_SET_TEMPERATURE, messageTimestamp, reading.getSetHeatTempInF());
        }
        if (reading.getSetCoolTempInF() != null) {
            inputPointValue(paoIdentifier, BuiltInAttribute.COOL_SET_TEMPERATURE, messageTimestamp, reading.getSetCoolTempInF());
        }
        if (reading.getDrRef() != null) {
            TrueFalse controlStatus = checkDREventReferenceId(reading.getDrRef());
            inputPointValue(paoIdentifier, BuiltInAttribute.CONTROL_STATUS, reading.getDate(), controlStatus.getRawState());
        }
        if (reading.getOutdoorTempInF() != null) {
            inputPointValue(paoIdentifier, BuiltInAttribute.OUTDOOR_TEMPERATURE, messageTimestamp, reading.getOutdoorTempInF());
        }
        if (reading.getIndoorTempInF() != null) {
            inputPointValue(paoIdentifier, BuiltInAttribute.INDOOR_TEMPERATURE, messageTimestamp, reading.getIndoorTempInF());
        }
        //TODO Comm Status Point
        // TODO Discuss Runtime Calculation 
        updateAssetAvailability(paoIdentifier, messageTimestamp);

    }

    private void updateAssetAvailability(PaoIdentifier paoIdentifier, Instant time) {
        AssetAvailabilityPointDataTimes times = new AssetAvailabilityPointDataTimes(paoIdentifier.getPaoId());
        times.setLastCommunicationTime(time);
        dynamicLcrCommunicationsDao.insertData(times);
    }

    private void inputPointValue(PaoIdentifier paoIdentifier, BuiltInAttribute attribute, Instant messageTimestamp, double value) {
        LitePoint point = attributeService.getPointForAttribute(paoIdentifier, attribute);

        PointData pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setValue(value);
        pointData.setType(point.getPointType());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTime(messageTimestamp.toDate());
        pointData.setTagsDataTimestampValid(true);

        asyncDynamicDataSource.putValue(pointData);
    }

    private TrueFalse checkDREventReferenceId(String drRef) {
        // TODO database call for drRef
        return TrueFalse.FALSE;
    }

}

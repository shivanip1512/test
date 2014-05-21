package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.dr.ecobee.EcobeeException;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Iterables;

public class DeviceAttributeReadEcobeeStrategy implements DeviceAttributeReadStrategy {
    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private AttributeService attributeService;

    @Override
    public StrategyType getType() {
        return StrategyType.ECOBEE;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        return paoType.isEcobee();
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> devices, LiteYukonUser user) {
        // TODO decide if anything more is required
        return true;
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> devices,
            final DeviceAttributeReadStrategyCallback delegateCallback,
            DeviceRequestType type, LiteYukonUser user) {

        Map<String, PaoIdentifier> ecobeeDevices = new HashMap<>();

        for (PaoMultiPointIdentifier paoMultiPointIdentifier : devices) {
            PaoIdentifier pao = paoMultiPointIdentifier.getPao();
            String ecobeeSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(pao.getPaoId());
            ecobeeDevices.put(ecobeeSerialNumber, pao);
        }

        Instant end = new Instant().minus(Duration.standardMinutes(15));
        MutableDateTime mutableDateTime = new MutableDateTime(end.minus(Duration.standardHours(24)));
        // Start request at top of hour requested. This makes some point archiving calculations easier
        mutableDateTime.setMinuteOfHour(0);
        Instant start = mutableDateTime.toInstant();
        Range<Instant> lastTwentyFourHours = Range.inclusive(start, end);
        try {
            for (List<String> serialNumbers : Iterables.partition(ecobeeDevices.keySet(), 25)) {
                List<EcobeeDeviceReadings> allDeviceReadings =
                        ecobeeCommunicationService.readDeviceData(serialNumbers, lastTwentyFourHours);
                for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
                    updatePointData(ecobeeDevices.get(deviceReadings.getSerialNumber()), deviceReadings);
                }
            }
        } catch (EcobeeException e) {
                MessageSourceResolvable summary =
                        YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.general.readError", e.getMessage());
                DeviceAttributeReadError exceptionError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, summary);
                delegateCallback.receivedException(exceptionError);
        }
        delegateCallback.complete();
    }

    // indoor/outdoor data store first valid value in hour (archives on update)
    // set heat/cool data store every interval (archives on change)
    // runtime store all values added up set to on hour BEFORE hour (archives on update)

    private void updatePointData(PaoIdentifier paoIdentifier, EcobeeDeviceReadings deviceReadings) {
        // device readings are 5 minutes apart. partition these into hourly buckets and work with these
        for (List<EcobeeDeviceReading> readings : Iterables.partition(deviceReadings.getReadings(), 12)) {
            // only update runtime if we have the full hours data
            boolean shouldUpdateRuntime = readings.size() == 12;

            Instant startDate = null;
            Float indoorTempToUpdate = null;
            Float outdoorTempToUpdate = null;
            Integer runtime = 0;
            for (EcobeeDeviceReading reading : readings) {
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
                    setPointValue(paoIdentifier, BuiltInAttribute.HEAT_SET_TEMPERATURE, startDate, reading.getSetHeatTempInF());
                }
                if (reading.getSetCoolTempInF() != null) {
                    setPointValue(paoIdentifier, BuiltInAttribute.COOL_SET_TEMPERATURE, startDate, reading.getSetCoolTempInF());
                }
                runtime += reading.getRuntimeSeconds();
                TrueFalse controlStatus = StringUtils.isBlank(reading.getEventActivity()) ? TrueFalse.FALSE : TrueFalse.TRUE;
                setPointValue(paoIdentifier, BuiltInAttribute.CONTROL_STATUS, startDate, controlStatus.getRawState());
            }
            if (outdoorTempToUpdate != null) {
                setPointValue(paoIdentifier, BuiltInAttribute.OUTDOOR_TEMPERATURE, startDate, outdoorTempToUpdate);
            }
            if (indoorTempToUpdate != null) {
                setPointValue(paoIdentifier, BuiltInAttribute.INDOOR_TEMPERATURE, startDate, indoorTempToUpdate);
            }
            if (shouldUpdateRuntime) {
                setPointValue(paoIdentifier, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, startDate, runtime);
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

    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        return devicesForThisStrategy.size();
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points,
                             final GroupCommandCompletionCallback groupCallback, DeviceRequestType type,
                             final YukonUserContext userContext) {
        initiateRead(points, groupCallback, type, userContext);
    }
}
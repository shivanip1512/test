package com.cannontech.deviceReadings.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.deviceReadings.model.DeviceReading;
import com.cannontech.deviceReadings.model.DeviceReadingRequest;
import com.cannontech.deviceReadings.model.DeviceReadingResponse;
import com.cannontech.deviceReadings.model.DeviceReadingsResponse;
import com.cannontech.deviceReadings.service.DeviceReadingsService;

public class DeviceReadingsServiceImpl implements DeviceReadingsService {

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private MeterDao meterDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;

    private static final Logger log = YukonLogManager.getLogger(DeviceReadingsServiceImpl.class);

    public List<DeviceReadingsResponse> getLatestReading(DeviceReadingRequest deviceReadingRequest) {

        List<DeviceReadingsResponse> listDeviceReadingResponse = new ArrayList<>();
        for (DeviceReading deviceReading : deviceReadingRequest.getDeviceReadings()) {
            YukonMeter yukonMeter = getYukonMeter(deviceReading);
            try {
                listDeviceReadingResponse.add(buildDeviceReadingResponse(deviceReading, yukonMeter));
            } catch (DynamicDataAccessException e) {
                String message = "Connection to the Dispatcher is invalid or Dispatcher Service is not running.";
                log.error(message);
                throw new DynamicDataAccessException(message);
            }
        }
        return listDeviceReadingResponse;
    }

    /**
     * Build device readings response object based on supplied attributes.
     */
    private DeviceReadingsResponse buildDeviceReadingResponse(DeviceReading deviceReading, YukonMeter yukonMeter) {
        DeviceReadingsResponse deviceReadingResponse = new DeviceReadingsResponse();
        List<DeviceReadingResponse> listOfDeviceReading = new ArrayList<>();

        Set<Attribute> supportedAttributes = attributeService.getAvailableAttributes(yukonMeter.getPaoType());
        if (CollectionUtils.isNotEmpty(deviceReading.getAttributes())) {
            for (BuiltInAttribute attribute : deviceReading.getAttributes()) {
                if (supportedAttributes.contains(attribute)) {
                    DeviceReadingResponse readingResponse = new DeviceReadingResponse();
                    LitePoint litePoint = attributeService.getPointForAttribute(yukonMeter, attribute);
                    PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getLiteID());

                    if (pointValueQualityHolder != null) {
                        readingResponse.setPointId(pointValueQualityHolder.getId());
                        readingResponse.setPointQuality(pointValueQualityHolder.getPointQuality());
                        readingResponse.setType(pointValueQualityHolder.getType());
                        // TODO considering only raw value.
                        readingResponse.setValue(pointValueQualityHolder.getValue());
                        readingResponse.setTime(pointValueQualityHolder.getPointDataTimeStamp());
                        readingResponse.setAttribute(attribute);
                        readingResponse.setMeterNumber(deviceReading.getMeterNumber());
                        listOfDeviceReading.add(readingResponse);
                    }
                }
            }
        }

        deviceReadingResponse.setDeviceReadingResponse(listOfDeviceReading);
        return deviceReadingResponse;
    }

    /**
     * Get Yukon Meter object based on serial number or Meter number.
     */
    private YukonMeter getYukonMeter(DeviceReading deviceReading) {

        YukonMeter yukonMeter = null;
        if (deviceReading.getSerialNumber() != null) {
            String serialNumber = deviceReading.getSerialNumber().trim();
            Integer paoId = rfnDeviceDao.findDeviceBySensorSerialNumber(serialNumber);
            if (paoId != null) {
                yukonMeter = meterDao.findForPaoName(serialNumber);
            }
        }

        if (yukonMeter == null) {
            if (deviceReading.getMeterNumber() != null) {
                try {
                    yukonMeter = meterDao.getForMeterNumber(deviceReading.getMeterNumber().trim());
                } catch (NotFoundException e) {
                    throw new NotFoundException("Device not found " + deviceReading.getMeterNumber());
                }
            }
        }
        return yukonMeter;

    }

}

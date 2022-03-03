package com.cannontech.deviceReadings.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.deviceReadings.model.DeviceReadingRequest;
import com.cannontech.deviceReadings.model.DeviceReadingResponse;
import com.cannontech.deviceReadings.model.DeviceReadingsResponse;
import com.cannontech.deviceReadings.model.DeviceReadingsSelector;
import com.cannontech.deviceReadings.model.Identifier;
import com.cannontech.deviceReadings.model.IdentifierType;
import com.cannontech.deviceReadings.service.DeviceReadingsService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class DeviceReadingsServiceImpl implements DeviceReadingsService {

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired protected DeviceDao deviceDao;
    @Autowired protected MeterDao meterDao;
    @Autowired protected RfnDeviceDao rfnDeviceDao;

    private ImmutableMap<IdentifierType, PaoSelector> paoSelectors;

    private static final Logger log = YukonLogManager.getLogger(DeviceReadingsServiceImpl.class);

    @PostConstruct
    public void init() throws Exception {
        Builder<IdentifierType, PaoSelector> builder = ImmutableMap.builder();
        builder.put(IdentifierType.METERNUMBER, new ByMeterNumbersSelector());
        builder.put(IdentifierType.PAONAME, new ByPaoNamesSelector());
        builder.put(IdentifierType.PAOID, new ByPaoIdSelector());
        builder.put(IdentifierType.SERIALNUMBER, new BySerialNumberSelector());
        builder.put(IdentifierType.DEVICEGROUP, new ByDeviceGroupNamesSelector());

        paoSelectors = builder.build();
    }

    private abstract class PaoSelector {

        /**
         * Device reading response based on selector.
         * @param deviceReadingsSelector
         * @return DeviceReadingsResponse
         */
        public abstract DeviceReadingsResponse selectPaos(DeviceReadingsSelector deviceReadingsSelector);
    }

    public List<DeviceReadingsResponse> getLatestReading(DeviceReadingRequest deviceReadingRequest) {

        List<DeviceReadingsResponse> listDeviceReadingResponse = new ArrayList<>();
        for (DeviceReadingsSelector deviceReadingsSelector : deviceReadingRequest.getDeviceReadingsSelectors()) {
            Identifier identifier = deviceReadingsSelector.getIdentifier();
            if (identifier != null && identifier.getIdentifierType() != null) {
                try {
                    listDeviceReadingResponse
                        .add(paoSelectors.get(identifier.getIdentifierType()).selectPaos(deviceReadingsSelector));
                } catch (NotFoundException ex) {
                    log.error("Lookup device by {} failed. {}", identifier.getIdentifierType(), ex.getMessage());
                    throw ex;
                }
            }
        }
        return listDeviceReadingResponse;
    }

    /**
     * Build device readings response object based on supplied DeviceReadingsSelector.
     **/
    private DeviceReadingsResponse buildDeviceReadingResponse(DeviceReadingsSelector deviceReadingSelector,
            YukonDevice yukonDevice) {
        DeviceReadingsResponse deviceReadingResponse = new DeviceReadingsResponse();
        List<DeviceReadingResponse> listOfDeviceReading = new ArrayList<>();
        Set<Attribute> supportedAttributes = attributeService.getAvailableAttributes(yukonDevice.getPaoIdentifier().getPaoType());
        if (CollectionUtils.isNotEmpty(deviceReadingSelector.getAttributes())) {
            for (BuiltInAttribute attribute : deviceReadingSelector.getAttributes()) {
                if (supportedAttributes.contains(attribute)) {
                    LitePoint litePoint = attributeService.getPointForAttribute(yukonDevice, attribute);
                    PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getLiteID());

                    if (pointValueQualityHolder != null) {
                        DeviceReadingResponse readingResponse = new DeviceReadingResponse(deviceReadingSelector.getIdentifier(),
                                attribute, pointValueQualityHolder);
                        listOfDeviceReading.add(readingResponse);
                    }
                }
            }
        }
        deviceReadingResponse.setDeviceReadingResponse(listOfDeviceReading);
        return deviceReadingResponse;
    }

    /**
     * Class for meter number as a selector in request,
     * @param deviceReadingsSelector
     * @return DeviceReadingsResponse -return DeviceReadingsResponse object for Meter Number selector.
     * MeterNumber is not enforced to be unique in Yukon so there is possibility of
     * IncorrectResultSizeDataAccessException,
     * In case of multiple meters found for meter number.
     */
    private class ByMeterNumbersSelector extends PaoSelector {
        @Override
        public DeviceReadingsResponse selectPaos(DeviceReadingsSelector deviceReadingsSelector) {
            DeviceReadingsResponse response = null;
            String meterNumber = deviceReadingsSelector.getIdentifier().getValue();
            if (meterNumber != null) {
                try {
                    YukonMeter yukonMeter = meterDao.getForMeterNumber(meterNumber);
                    response = buildDeviceReadingResponse(deviceReadingsSelector, yukonMeter);
                } catch (NotFoundException ex) {
                    throw ex;
                }
            }
            return response;

        }
    }

    /**
     * Class for Pao Name as a selector in request,
     * @param deviceReadingsSelector
     * @return DeviceReadingsResponse -return DeviceReadingsResponse object for Pao Name selector.
     * PaoName is only unique across category and class 
     * so we will get IncorrectResultSizeDataAccessException here if more than one Pao object found for pao name.
     */
    private class ByPaoNamesSelector extends PaoSelector {
        @Override
        public DeviceReadingsResponse selectPaos(DeviceReadingsSelector deviceReadingsSelector) {
            DeviceReadingsResponse response = null;
            String deviceName = deviceReadingsSelector.getIdentifier().getValue();
            if (deviceName != null) {
                try {
                    YukonDevice yukonDevice = deviceDao.getYukonDeviceObjectByName(deviceName);
                    response = buildDeviceReadingResponse(deviceReadingsSelector, yukonDevice);
                } catch (EmptyResultDataAccessException e) {
                    throw new NotFoundException("Unknown device name: " + deviceName);
                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Duplicate devices found for device name: " + deviceName);
                }
            }
            return response;
        }
    }

    /**
     * Class for Pao Id as a selector in request,
     * @param deviceReadingsSelector
     * @return DeviceReadingsResponse -Return DeviceReadingsResponse object for Pao Id selector.
     */
    private class ByPaoIdSelector extends PaoSelector {
        @Override
        public DeviceReadingsResponse selectPaos(DeviceReadingsSelector deviceReadingsSelector) {
            DeviceReadingsResponse response = null;
            String paoIdStr = deviceReadingsSelector.getIdentifier().getValue();
            if (paoIdStr != null) {
                try {
                    Integer paoId = Integer.parseInt(paoIdStr);
                    try {
                        YukonDevice yukonDevice = deviceDao.getYukonDevice(paoId);
                        response = buildDeviceReadingResponse(deviceReadingsSelector, yukonDevice);
                    } catch (NotFoundException e) {
                        throw e;
                    }
                } catch (NumberFormatException e) {
                    throw new NotFoundException("Unknown paoId: " + paoIdStr);
                }
            }
            return response;
        }
    }

    /**
     * Class for Serial Number as a selector in request,
     * @param deviceReadingsSelector
     * @return DeviceReadingsResponse - Return DeviceReadingsResponse object for Serial Number selector.
     * Here we are expecting we will get unique device for serial number
     *  IncorrectResultSizeDataAccessException is thrown here if more than one devices found for Serial Number.
     */
    private class BySerialNumberSelector extends PaoSelector {
        @Override
        public DeviceReadingsResponse selectPaos(DeviceReadingsSelector deviceReadingsSelector) {
            DeviceReadingsResponse deviceReadingResponse = null;
            String serialNumber = deviceReadingsSelector.getIdentifier().getValue();
            if (serialNumber != null) {
                try {
                    RfnDevice rfnDevice = rfnDeviceDao.findDeviceBySensorSerialNumber(serialNumber);
                    if (rfnDevice != null) {
                        deviceReadingResponse = buildDeviceReadingResponse(deviceReadingsSelector, rfnDevice);
                    } else {
                        throw new NotFoundException("Unknown serial number: " + serialNumber);
                    }
                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new NotFoundException("Duplicate devices found for serial number: " + serialNumber);
                }
            }
            return deviceReadingResponse;
        }
    }

    /**
     * Class for GroupName as a selector in request,
     * 
     * @param deviceReadingsSelector
     * @return DeviceReadingsResponse - Return DeviceReadingsResponse object for GroupName selector.
     */
    private class ByDeviceGroupNamesSelector extends PaoSelector {

        @Override
        public DeviceReadingsResponse selectPaos(DeviceReadingsSelector deviceReadingsSelector) {
            return null;
            // TODO Implementation for Group Name selector.

        }

    }

}

package com.cannontech.amr.crf.endpoint;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.crf.dao.CrfMeterDao;
import com.cannontech.amr.crf.message.ChannelData;
import com.cannontech.amr.crf.message.ChannelDataStatus;
import com.cannontech.amr.crf.message.MeterReadingDataMessage;
import com.cannontech.amr.crf.model.CrfMeter;
import com.cannontech.amr.crf.model.CrfMeterIdentifier;
import com.cannontech.amr.crf.service.AttributeConverter;
import com.cannontech.amr.crf.service.CrfAttributeLookupService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.TransactionTemplateHelper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;

public class MeterReadingDataMessageListener implements MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(MeterReadingDataMessageListener.class);
    private CrfMeterDao crfMeterDao;
    private DeviceDao deviceDao;
    private CrfAttributeLookupService crfAttributeLookupService;
    private AttributeService attributeService;
    private DynamicDataSource dynamicDataSource;
    private DeviceCreationService deviceCreationService;
    private ConfigurationSource configurationSource;
    private String meterTemplatePrefix;
    private TransactionTemplate transactionTemplate;
    
    @PostConstruct
    public void initialize() {
        meterTemplatePrefix = configurationSource.getString("CRF_METER_TEMPLATE_PREFIX", "*CrfTemplate_");
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof MeterReadingDataMessage) {
                    MeterReadingDataMessage meterReadingDataNotification = (MeterReadingDataMessage) object;
                    handleMeterReadingData(meterReadingDataNotification);
                    message.acknowledge();
                }
            } catch (JMSException e) {
                log.warn("Unable to extract object from message", e);
            }
        }
    }

    private void handleMeterReadingData(MeterReadingDataMessage meterReadingDataNotification) {
        LogHelper.debug(log, "Received EkaMeterReadingData: %s", meterReadingDataNotification);
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
        
        Instant readingInstant = new Instant(meterReadingDataNotification.getTimeStamp());
        
        CrfMeterIdentifier meterIdentifier = getMeterIdentifier(meterReadingDataNotification);
        CrfMeter crfMeter;
        try {
            crfMeter = crfMeterDao.getMeter(meterIdentifier);
        } catch (NotFoundException e) {
            crfMeter = createMeter(meterIdentifier);
        }
        
        for (ChannelData channelData : meterReadingDataNotification.getChannelDataList()) {
            log.debug("Processing channelData: " + channelData);
            ChannelDataStatus status = channelData.getStatus();
            if (!status.isOk()) {
                LogHelper.debug(log, "Received status of %s for channelData", status);
                continue;
            }
            
            // this call should be a simple hash lookup
            AttributeConverter attributeConverter = crfAttributeLookupService.findMatch(crfMeter.getPaoIdentifier().getPaoType(), meterReadingDataNotification.getMeterReadingType(), channelData.getUnitOfMeasure(), channelData.getUnitOfMeasureModifiers());
            if (attributeConverter == null) {
                log.debug("No AttributeConverter for this channelData");
                continue;
            }
            log.debug("Got AttributeConverter " + attributeConverter);
            
            LitePoint point;
            try {
                // this call is probably a little heavy considering how little of the LitePoint is actually needed
                point = attributeService.getPointForAttribute(crfMeter, attributeConverter.getAttribute());
            } catch (IllegalUseOfAttribute e) {
                LogHelper.debug(log, "Unable to find point for channelData: %s", channelData);
                continue;
            }
            
            PointData pointData = new PointData();
            pointData.setId(point.getLiteID());
            pointData.setPointQuality(PointQuality.Normal);
            double value = attributeConverter.convertValue(channelData.getValue());
            pointData.setValue(value);
            pointData.setTime(readingInstant.toDate());
            pointData.setType(point.getPointType());
            
            LogHelper.debug(log, "Generated PointData: %s", pointData);
            
            messagesToSend.add(pointData);
        }
        
        dynamicDataSource.putValues(messagesToSend);
        LogHelper.debug(log, "%d PointDatas generated for EkaMeterReadingData", messagesToSend.size());
    }

    private CrfMeter createMeter(final CrfMeterIdentifier meterIdentifier) {
        CrfMeter result = TransactionTemplateHelper.execute(transactionTemplate, new Callable<CrfMeter>() {

            @Override
            public CrfMeter call() {
                String templateName = meterTemplatePrefix + meterIdentifier.getSensorManufacturer() + "_" + meterIdentifier.getSensorModel();
                String deviceName = meterIdentifier.getSensorSerialNumber().trim();
                YukonDevice newDevice = deviceCreationService.createDeviceByTemplate(templateName, deviceName, true);
                CrfMeter meter = new CrfMeter(newDevice.getPaoIdentifier(), meterIdentifier);
                crfMeterDao.updateMeter(meter);
                deviceDao.changeMeterNumber(meter, deviceName);
                return meter;
            }
        });
        
        return result;
    }

    private CrfMeterIdentifier getMeterIdentifier(MeterReadingDataMessage meterReadingDataNotification) {
        return new CrfMeterIdentifier(meterReadingDataNotification.getSensorSerialNumber(), meterReadingDataNotification.getSensorManufacturer(), meterReadingDataNotification.getSensorModel());
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @Autowired
    public void setCrfAttributeLookupService(CrfAttributeLookupService crfAttributeLookupService) {
        this.crfAttributeLookupService = crfAttributeLookupService;
    }
    
    @Autowired
    public void setCrfMeterDao(CrfMeterDao crfMeterDao) {
        this.crfMeterDao = crfMeterDao;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setDeviceCreationService(DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }
    
    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}

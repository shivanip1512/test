package com.cannontech.amr.crf.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.crf.dao.CrfMeterDao;
import com.cannontech.amr.crf.message.ChannelData;
import com.cannontech.amr.crf.message.ChannelDataStatus;
import com.cannontech.amr.crf.message.CrfMeterReadDataReply;
import com.cannontech.amr.crf.message.CrfMeterReadReply;
import com.cannontech.amr.crf.message.CrfMeterReadRequest;
import com.cannontech.amr.crf.message.CrfMeterReadingData;
import com.cannontech.amr.crf.message.CrfMeterReadingReplyType;
import com.cannontech.amr.crf.model.CrfMeter;
import com.cannontech.amr.crf.model.CrfMeterIdentifier;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.TransactionTemplateHelper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;

public class CrfMeterReadService {
    
    private CrfMeterDao crfMeterDao;
    private DeviceDao deviceDao;
    private CrfAttributeLookupService crfAttributeLookupService;
    private AttributeService attributeService;
    private DeviceCreationService deviceCreationService;
    private ConfigurationSource configurationSource;
    private String meterTemplatePrefix;
    private TransactionTemplate transactionTemplate;
    private ConnectionFactory connectionFactory;
    private ExecutorService readRequestThreadPool;
    private int statusTimeout;
    private int dataTimeout;
    
    private static final Logger log = YukonLogManager.getLogger(CrfMeterReadService.class);
    
    /**
     * Attempts to send a read request for a CRF meter.  Will use a separate thread to make the request.
     * Will expect two responses. 
     * 
     * The first is a status message indicating this is a known meter and a read
     * will be tried, or a read is not possible for this meter.  This response should come back within seconds.
     * 
     *  The second response is the actual read data.  This response is only expected if the first response
     *  was 'OK'.  This response can take anywhere from seconds to minutes to tens of minutes depending
     *  on network performance.
     *  
     *  The master.cfg can contain two parameters to define the timeouts:
     *  
     *  CRF_METER_READ _STATUS_TIMEOUT_SECONDS
     *  CRF_METER_READ _DATA_TIMEOUT_MINUTES
     *  
     *  If not provided they default to 10 seconds and 15 minutes.
     *  
     * @param meter The meter to read.
     * @param callback The callback to use for updating status, errors and read data.
     */
    public void send(final CrfMeterIdentifier meter, final CrfMeterReadCompletionCallback callback) {
        
        readRequestThreadPool.execute(new Runnable() {
            
            @Override
            public void run() {
                JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
                
                jmsTemplate.execute(new SessionCallback() {
                    
                    @Override
                    public Object doInJms(Session session) throws JMSException {
                        try {
                            DynamicDestinationResolver resolver = new DynamicDestinationResolver();
                            MessageProducer producer = session.createProducer(resolver.resolveDestinationName(session, "yukon.rr.obj.amr.crf.MeterReadRequest", false));
                            
                            TemporaryQueue replyQueue = session.createTemporaryQueue();
                            MessageConsumer replyConsumer = session.createConsumer(replyQueue);
                            
                            ObjectMessage requestMessage = session.createObjectMessage(new CrfMeterReadRequest(meter));
                            
                            requestMessage.setJMSReplyTo(replyQueue);
                            producer.send(requestMessage);
                            
                            /* Blocks for status response or until timeout*/
                            Message replyMessage = replyConsumer.receive(Duration.standardSeconds(statusTimeout).getMillis());
                            
                            CrfMeterReadReply statusReply = (CrfMeterReadReply) replyMessage;
                            
                            if (statusReply == null || !statusReply.isSuccess()) {
                                /* Request failed */
                                if(statusReply == null) {
                                    callback.receivedStatus(CrfMeterReadingReplyType.TIMEOUT);
                                } else {
                                    callback.receivedStatusError(statusReply.getReplyType());
                                    callback.receivedStatus(statusReply.getReplyType());
                                }
                            } else {
                                /* Request successful */
                                callback.receivedStatus(statusReply.getReplyType());
                                List<PointValueHolder> pointDatas = Lists.newArrayList();
                                
                                /* Blocks for reading point data or until timeout*/
                                Message dataReply = replyConsumer.receive(Duration.standardMinutes(dataTimeout).getMillis());
                                
                                CrfMeterReadDataReply dataReplyMessage = (CrfMeterReadDataReply) dataReply;
                                
                                if (!dataReplyMessage.isSuccess()) {
                                    /* Data response failed */
                                    callback.receivedDataError(dataReplyMessage.getReplyType());
                                } else {
                                    /* Data response successful, process point data */
                                    processMeterReadingDataMessage(dataReplyMessage.getData(), pointDatas);
                                    
                                    for (PointValueHolder pointValueHolder : pointDatas) {
                                        callback.receivedData(dataReplyMessage.getReplyType(), pointValueHolder);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            callback.processingExceptionOccured(e.getMessage());
                        } finally {
                            callback.complete();
                        }
                        return null;
                    }
                });
            }
        });
    }
    
    public void processMeterReadingDataMessage(CrfMeterReadingData meterReadingDataNotification, List<? super PointData> pointDatas) {
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
            
            pointDatas.add(pointData);
        }
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

    private CrfMeterIdentifier getMeterIdentifier(CrfMeterReadingData meterReadingDataNotification) {
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
    
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @PostConstruct
    public void initialize() {
        meterTemplatePrefix = configurationSource.getString("CRF_METER_TEMPLATE_PREFIX", "*CrfTemplate_");
        statusTimeout = configurationSource.getInteger("CRF_METER_READ _STATUS_TIMEOUT_SECONDS", 10);
        dataTimeout = configurationSource.getInteger("CRF_METER_READ _DATA_TIMEOUT_MINUTES", 15);
        readRequestThreadPool = Executors.newFixedThreadPool(6);
    }
}
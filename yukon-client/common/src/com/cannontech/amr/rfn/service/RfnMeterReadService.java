package com.cannontech.amr.rfn.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.DatedChannelData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadDataReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadRequest;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterPlusReadingData;
import com.cannontech.amr.rfn.service.pointmapping.PointValueHandler;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;

public class RfnMeterReadService {
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private UnitOfMeasureToPointMapper unitOfMeasureToPointMapper;
    @Autowired private PointDao pointDao;
    
    private RequestReplyReplyTemplate<RfnMeterReadReply, RfnMeterReadDataReply> rrrTemplate;
    
    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadService.class);
    
    /**
     * Attempts to send a read request for a RFN meter. Will use a separate thread to make the request.
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
     *  RFN_METER_READ_REPLY1_TIMEOUT
     *  RFN_METER_READ_REPLY2_TIMEOUT
     *  
     *  If not provided they default to 1 minute and 10 minutes.
     *  
     * @param rfnMeter The meter to read.
     * @param callback The callback to use for updating status, errors and read data.
     */
    public void send(final RfnMeter rfnMeter, final RfnDeviceReadCompletionCallback<RfnMeterReadingReplyType, RfnMeterReadingDataReplyType> callback) {
        JmsReplyReplyHandler<RfnMeterReadReply, RfnMeterReadDataReply> handler = new JmsReplyReplyHandler<RfnMeterReadReply, RfnMeterReadDataReply>() {

            @Override
            public void complete() {
                callback.complete();
            }

            @Override
            public Class<RfnMeterReadReply> getExpectedType1() {
                return RfnMeterReadReply.class;
            }

            @Override
            public Class<RfnMeterReadDataReply> getExpectedType2() {
                return RfnMeterReadDataReply.class;
            }

            @Override
            public void handleException(Exception e) {
                callback.processingExceptionOccured(e.getMessage());
            }

            @Override
            public boolean handleReply1(RfnMeterReadReply statusReply) {
                if (!statusReply.isSuccess()) {
                    /* Request failed */
                    callback.receivedStatusError(statusReply.getReplyType());
                    return false;
                } else {
                    /* Request successful */
                    callback.receivedStatus(statusReply.getReplyType());
                    return true;
                }
            }

            @Override
            public void handleReply2(RfnMeterReadDataReply dataReplyMessage) {
                if (!dataReplyMessage.isSuccess()) {
                    /* Data response failed */
                    callback.receivedDataError(dataReplyMessage.getReplyType());
                } else {
                    /* Data response successful, process point data */
                    List<PointValueHolder> pointDatas = Lists.newArrayList();
                    processMeterReadingDataMessage(new RfnMeterPlusReadingData(rfnMeter, dataReplyMessage.getData()), pointDatas);

                    for (PointValueHolder pointValueHolder : pointDatas) {
                        callback.receivedData(pointValueHolder);
                    }
                }
           }

            @Override
            public void handleTimeout1() {
                callback.receivedStatusError(RfnMeterReadingReplyType.TIMEOUT);
            }

            @Override
            public void handleTimeout2() {
                callback.receivedDataError(RfnMeterReadingDataReplyType.TIMEOUT);
            }
        };
        
        rrrTemplate.send(new RfnMeterReadRequest(rfnMeter.getRfnIdentifier()), handler);
    }
    
    public void processMeterReadingDataMessage(RfnMeterPlusReadingData meterReadingData, List<? super PointData> pointDatas) {
        
        List<ChannelData> nonDatedChannelData = meterReadingData.getRfnMeterReadingData().getChannelDataList();
        List<? extends ChannelData> datedChannelData = meterReadingData.getRfnMeterReadingData().getDatedChannelDataList();
        
        List<ChannelData> allChannelData = Lists.newArrayList();
        
        if (nonDatedChannelData != null) {
            allChannelData.addAll(nonDatedChannelData);
        }
        
        if (datedChannelData != null) {
            allChannelData.addAll(datedChannelData);
        }
        
        Instant readingInstant = new Instant(meterReadingData.getRfnMeterReadingData().getTimeStamp());
        
        for (ChannelData channelData : allChannelData) {
            RfnMeter rfnMeter = meterReadingData.getRfnMeter();
            LogHelper.debug(log, "Processing %s for %s", channelData, rfnMeter);
            ChannelDataStatus status = channelData.getStatus();
            if (status == null) {
                LogHelper.debug(log, "Received null status for channelData, skipping");
                continue;
            }
            if (!status.isOk()) {
                LogHelper.debug(log, "Received status of %s for channelData, skipping", status);
                continue;
            }
            
            PointValueHandler pointValueHandler = unitOfMeasureToPointMapper.findMatch(rfnMeter, channelData);
            if (pointValueHandler == null) {
                log.debug("No PointValueHandler for this channelData");
                continue;
            }
            LogHelper.debug(log, "Got PointValueHandler %s", pointValueHandler);
            
            int pointId;
            try {
                pointId = pointDao.getPointId(pointValueHandler.getPaoPointIdentifier());
            } catch (NotFoundException e) {
                LogHelper.debug(log, "Unable to find point for channelData: %s", channelData);
                continue;
            }
            
            PointData pointData = new PointData();
            pointData.setId(pointId);
            pointData.setPointQuality(PointQuality.Normal);
            double value = pointValueHandler.convert(channelData.getValue());
            pointData.setValue(value);
            if (channelData instanceof DatedChannelData) {
                DatedChannelData dated = (DatedChannelData)channelData;
                pointData.setTime(new Instant(dated.getTimeStamp()).toDate());
            } else {
                pointData.setTime(readingInstant.toDate());
            }
            pointData.setType(pointValueHandler.getPaoPointIdentifier().getPointIdentifier().getPointType().getPointTypeId());
            pointData.setTagsPointMustArchive(true); // temporary solution
            
            LogHelper.debug(log, "Generated PointData: %s", pointData);
            
            pointDatas.add(pointData);
        }
    }
    
    @PostConstruct
    public void initialize() {
        rrrTemplate = new RequestReplyReplyTemplate<RfnMeterReadReply, RfnMeterReadDataReply>();
        rrrTemplate.setConfigurationName("RFN_METER_READ");
        rrrTemplate.setConfigurationSource(configurationSource);
        rrrTemplate.setConnectionFactory(connectionFactory);
        rrrTemplate.setRequestQueueName("yukon.qr.obj.amr.rfn.MeterReadRequest", false);
    }
    
}
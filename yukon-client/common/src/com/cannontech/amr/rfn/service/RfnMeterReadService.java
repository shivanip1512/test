package com.cannontech.amr.rfn.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.rfn.message.read.RfnMeterReadDataReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadRequest;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterPlusReadingData;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Lists;

public class RfnMeterReadService {
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private RfnChannelDataConverter rfnChannelDataConverter;

    private RequestReplyReplyTemplate<RfnMeterReadReply, RfnMeterReadDataReply> rrrTemplate;
    
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
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.exception", e.getMessage());
                callback.processingExceptionOccured(summary);
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
                    RfnDevice rfnDevice = new RfnDevice(rfnMeter.getName(), rfnMeter.getPaoIdentifier(), rfnMeter.getRfnIdentifier());
                    rfnChannelDataConverter.convert(new RfnMeterPlusReadingData(rfnDevice, dataReplyMessage.getData()), pointDatas);

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
    
    @PostConstruct
    public void initialize() {
        rrrTemplate = new RequestReplyReplyTemplate<RfnMeterReadReply, RfnMeterReadDataReply>(
                "RFN_METER_READ", configurationSource, connectionFactory,
                "yukon.qr.obj.amr.rfn.MeterReadRequest", false);
    }
    
}
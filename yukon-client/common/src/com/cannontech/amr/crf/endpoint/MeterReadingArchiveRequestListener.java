package com.cannontech.amr.crf.endpoint;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.crf.message.CrfMeterReadingArchiveRequest;
import com.cannontech.amr.crf.message.CrfMeterReadingData;
import com.cannontech.amr.crf.service.CrfMeterReadService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;

public class MeterReadingArchiveRequestListener implements MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(MeterReadingArchiveRequestListener.class);
    
    private DynamicDataSource dynamicDataSource;
    private CrfMeterReadService crfMeterReadService;
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof CrfMeterReadingArchiveRequest) {
                    CrfMeterReadingData meterReadingDataNotification = ((CrfMeterReadingArchiveRequest) object).getData();
                    handleMeterReadingData(meterReadingDataNotification);
                    message.acknowledge();
                }
            } catch (JMSException e) {
                log.warn("Unable to extract object from message", e);
            }
        }
    }

    private void handleMeterReadingData(CrfMeterReadingData meterReadingDataNotification) {
        LogHelper.debug(log, "Received EkaMeterReadingData: %s", meterReadingDataNotification);
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(5);
        
        crfMeterReadService.processMeterReadingDataMessage(meterReadingDataNotification, messagesToSend);
        
        dynamicDataSource.putValues(messagesToSend);
        LogHelper.debug(log, "%d PointDatas generated for EkaMeterReadingData", messagesToSend.size());
    }
    
    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Autowired
    public void setCrfMeterReadService(CrfMeterReadService crfMeterReadService) {
        this.crfMeterReadService = crfMeterReadService;
    }
}
package com.cannontech.web.support.development;

import java.util.List;

import javax.jms.ConnectionFactory;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Controller("/development/rfn/*")
@CheckDevelopmentMode
public class RfnMeterArchiveTestController {

    private ConnectionFactory connectionFactory;
    
    @RequestMapping("view")
    public String view() {
        
        return "development/rfn/view.jsp";
    }
    
    @RequestMapping("sendMeterArchiveRequest")
    public String send(String serial, String manufacturer, String model, double value) {
        JmsTemplate jmsTemplate;
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        
        RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
        
        RfnMeterReadingData data = new RfnMeterReadingData();
        data.setTimeStamp(new Instant().getMillis());
        data.setSensorSerialNumber(serial);
        data.setSensorManufacturer(manufacturer);
        data.setSensorModel(model);
        
        List<ChannelData> dataList = Lists.newArrayList();
        ChannelData channelData = new ChannelData();
        channelData.setChannelNumber(0);
        channelData.setStatus(ChannelDataStatus.OK);
        channelData.setUnitOfMeasure("Wh");
        channelData.setUnitOfMeasureModifiers(ImmutableSet.of("Quadrant 1", "Quadrant 2"));
        channelData.setValue(value);
        dataList.add(channelData);
        
        data.setChannelDataList(dataList);
        
        message.setData(data);
        message.setDataPointId(1);
        message.setReadingType(RfnMeterReadingType.CURRENT);
        
        jmsTemplate.convertAndSend("yukon.rr.obj.amr.rfn.MeterReadingArchiveRequest", message);
        
        return "redirect:view";
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
}
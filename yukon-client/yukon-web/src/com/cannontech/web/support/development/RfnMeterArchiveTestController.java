package com.cannontech.web.support.development;

import java.util.List;
import java.util.Set;

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
import com.google.common.collect.Sets;

@Controller("/development/rfn/*")
@CheckDevelopmentMode
public class RfnMeterArchiveTestController {

    private ConnectionFactory connectionFactory;
    
    @RequestMapping("view")
    public String view() {
        
        return "development/rfn/view.jsp";
    }
    
    @RequestMapping("sendMeterArchiveRequest")
    public String send(int serialFrom, int serialTo, String manufacturer, String model, Double value, boolean random, String uom, 
                       boolean quad1,
                       boolean quad2,
                       boolean quad3,
                       boolean quad4) {
        JmsTemplate jmsTemplate;
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        
        for (int i = serialFrom; i <= serialTo; i++) {
            RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
            
            RfnMeterReadingData data = new RfnMeterReadingData();
            data.setTimeStamp(new Instant().getMillis());
            data.setSensorSerialNumber(Integer.toString(i));
            data.setSensorManufacturer(manufacturer);
            data.setSensorModel(model);
            
            List<ChannelData> dataList = Lists.newArrayList();
            ChannelData channelData = new ChannelData();
            channelData.setChannelNumber(0);
            channelData.setStatus(ChannelDataStatus.OK);
            
            channelData.setUnitOfMeasure(uom);
            Set<String> modifiers = Sets.newHashSet();
            if (quad1) modifiers.add("Quadrant 1");
            if (quad2) modifiers.add("Quadrant 2");
            if (quad3) modifiers.add("Quadrant 3");
            if (quad4) modifiers.add("Quadrant 4");
            
            if (random) {
                value = Math.random() * 1000;
            }
            channelData.setValue(value);
            dataList.add(channelData);
            
            data.setChannelDataList(dataList);
            
            message.setData(data);
            message.setDataPointId(1);
            
            if (model.contains("water")) {
                message.setReadingType(RfnMeterReadingType.INTERVAL);
                modifiers.add("Kilo");
            } else {
                message.setReadingType(RfnMeterReadingType.CURRENT);
            }
            channelData.setUnitOfMeasureModifiers(modifiers);
            
            jmsTemplate.convertAndSend("yukon.rr.obj.amr.rfn.MeterReadingArchiveRequest", message);
        }
        
        return "redirect:view";
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
}
package com.cannontech.development.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.ConnectionFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.FileCopyUtils;

import com.cannontech.amr.rfn.message.alarm.RfnAlarm;
import com.cannontech.amr.rfn.message.alarm.RfnAlarmArchiveRequest;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.message.event.RfnEventArchiveRequest;
import com.cannontech.amr.rfn.message.event.RfnUomModifierSet;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.da.rfn.message.archive.RfDaArchiveRequest;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RfnEventTestingServiceImpl implements RfnEventTestingService {
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ResourceLoader loader;
        
    private static final String meterReadingArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";
    private static final String lcrReadingArchiveRequestQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";
    private static final String rfDaArchiveRequestQueueName = "yukon.qr.obj.da.rfn.RfDaArchiveRequest";
    private static final String eventArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.EventArchiveRequest";
    private static final String alarmArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.AlarmArchiveRequest";
    
    private static final Logger log = YukonLogManager.getLogger(RfnEventTestingServiceImpl.class);
    
    @Override
    public int sendEventsAndAlarms(RfnTestEvent event) {
        int numEventsSent = 0;
        for (int i = event.getSerialFrom(); i <= event.getSerialTo(); i++) {
            for (int j=0; j < event.getNumEventPerMeter(); j++) {
                buildAndSendEvent(event, i);
                numEventsSent++;
            }
            for (int j=0; j < event.getNumAlarmPerMeter(); j++) {
                buildAndSendAlarm(event, i);
                numEventsSent++;
            }
        }
        return numEventsSent;
    }
    
    @Override
    public void calculationStressTest() {
        
        List<RfnMeterReadingArchiveRequest> messages = new ArrayList<>();

        for (int i = 10000; i < 11000; i++) {
            
            DateTime now = new DateTime().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            
            for (int j = 0; j < 100; j++) {
                RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
                
                RfnMeterReadingData data = new RfnMeterReadingData();
                data.setTimeStamp(now.getMillis());
                
                RfnIdentifier meterIdentifier = new RfnIdentifier(Integer.toString(i), "EE", "A3R");
                data.setRfnIdentifier(meterIdentifier);
                
                List<ChannelData> dataList = Lists.newArrayList();
                ChannelData channelData = new ChannelData();
                channelData.setChannelNumber(1);
                channelData.setStatus(ChannelDataStatus.OK);
                
                channelData.setUnitOfMeasure("Wh");
                Set<String> modifiers = Sets.newHashSet();
                
                modifiers.add("Quadrant 1");
                modifiers.add("Quadrant 4");
    
                channelData.setValue(Math.random() * 1000);
                dataList.add(channelData);
                data.setChannelDataList(dataList);
                data.setRecordInterval(3600);
    
                message.setData(data);
                message.setDataPointId(1);
                message.setReadingType(RfnMeterReadingType.INTERVAL);
                
                channelData.setUnitOfMeasureModifiers(modifiers);
                messages.add(message);
                
                now = now.minus(Duration.standardHours(1));
            }
            
        }
        
        Collections.shuffle(messages);
        
        for (RfnMeterReadingArchiveRequest message : messages) {
            sendArchiveRequest(meterReadingArchiveRequestQueueName, message);
        }
    }
    
    @Override
    public int sendMeterArchiveRequests(int serialFrom, int serialTo, String manufacturer, String model, Double value, RfnMeterReadingType type, boolean random, String uom, 
                       boolean quad1, boolean quad2, boolean quad3, boolean quad4, boolean max, boolean min, boolean avg,
                       boolean phaseA, boolean phaseB, boolean phaseC, boolean touRateA, boolean touRateB, boolean touRateC,
                       boolean touRateD, boolean touRateE, boolean netFlow, boolean coincident, boolean harmonic, boolean cumulative) {
        
        type = type == null ? RfnMeterReadingType.INTERVAL : type;
        
        int numSent = 0;
        for (int i = serialFrom; i <= serialTo; i++) {
            
            RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
            
            RfnMeterReadingData data = new RfnMeterReadingData();
            data.setTimeStamp(new Instant().getMillis());
            RfnIdentifier meterIdentifier = new RfnIdentifier(Integer.toString(i), manufacturer, model);
            data.setRfnIdentifier(meterIdentifier);
            
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
            
            if (max) modifiers.add("Max");
            if (min) modifiers.add("Min");
            if (avg) modifiers.add("Avg");
            
            if (phaseA) modifiers.add("Phase A");
            if (phaseB) modifiers.add("Phase B");
            if (phaseC) modifiers.add("Phase C");
            
            if (touRateA) modifiers.add("TOU Rate A");
            if (touRateB) modifiers.add("TOU Rate B");
            if (touRateC) modifiers.add("TOU Rate C");
            if (touRateD) modifiers.add("TOU Rate D");
            if (touRateE) modifiers.add("TOU Rate E");
            
            if (netFlow)    modifiers.add("Net Flow");
            if (coincident) modifiers.add("Coincident");
            if (harmonic)   modifiers.add("Harmonic");
            if (cumulative) modifiers.add("Cumulative");
            
            if (random) {
                channelData.setValue(Math.random() * 1000);
            } else {
                channelData.setValue(value);
            }
            dataList.add(channelData);
            
            data.setChannelDataList(dataList);
            
            message.setData(data);
            message.setDataPointId(1);
            
            if (model.contains("water")) {
                message.setReadingType(RfnMeterReadingType.INTERVAL);
                modifiers.add("Kilo");
            }
            channelData.setUnitOfMeasureModifiers(modifiers);
            
            sendArchiveRequest(meterReadingArchiveRequestQueueName, message);
            numSent++;
        }
        return numSent;
    }
    
    @Override
    public int sendLcrReadArchive(int serialFrom, int serialTo, int days, DRReport drReport) throws IOException {
        if (serialTo < serialFrom) {
            serialTo = serialFrom;
        }
        int numRequests = 0;
        for (int serial = serialFrom; serial <= serialTo; serial++) {
            for (int i = 0; i < days; i++) {
                // Create archive request
                RfnLcrReadingArchiveRequest readArchiveRequest = new RfnLcrReadingArchiveRequest();
                RfnLcrReading data = new RfnLcrReading();
                
                // Read test encoded EXI file from classpath, assign it to payload.
                byte[] payload;
                Resource payloadResource = loader.getResource(drReport.getClasspath());
                if (payloadResource.exists()) {
                    payload = FileCopyUtils.copyToByteArray(payloadResource.getInputStream());
                } else {
                    payload = new byte[64];
                }
                long timeStamp = new Instant().minus(Duration.standardDays(i)).getMillis();
                RfnIdentifier rfnIdentifier = new RfnIdentifier(Integer.toString(serial), drReport.getManufacturer(), drReport.getModel());
                
                // Set all data
                data.setPayload(payload);
                data.setTimeStamp(timeStamp);
                readArchiveRequest.setData(data);
                readArchiveRequest.setRfnIdentifier(rfnIdentifier);
                readArchiveRequest.setType(RfnLcrReadingType.UNSOLICITED);
                
                // Put request on queue
                sendArchiveRequest(lcrReadingArchiveRequestQueueName, readArchiveRequest);
                numRequests++;
            }
        }
        return numRequests;
    }
    
    @Override
    public void sendLcrArchiveRequest(int serialFrom, int serialTo, String manufacturer, String model) {
        // Create archive request & fake identifier
        RfnLcrArchiveRequest archiveRequest = new RfnLcrArchiveRequest();
        RfnIdentifier rfnIdentifier = new RfnIdentifier(Integer.toString(serialFrom), manufacturer, model);
        
        // Set all data
        archiveRequest.setRfnIdentifier(rfnIdentifier);
        archiveRequest.setSensorId(1234);
        
        // Put request on queue
        sendArchiveRequest(lcrReadingArchiveRequestQueueName, archiveRequest);
    }
    
    @Override
    public void sendRfDaArchiveRequest(int serial, String manufacturer, String model) {
        // Create archive request & fake identifier
        RfDaArchiveRequest archiveRequest = new RfDaArchiveRequest();
        RfnIdentifier rfnIdentifier = new RfnIdentifier(Integer.toString(serial), manufacturer, model);
        
        // Set all data
        archiveRequest.setRfnIdentifier(rfnIdentifier);
        archiveRequest.setSensorId(1234);
        
        // Put request on queue
        sendArchiveRequest(rfDaArchiveRequestQueueName, archiveRequest);
    }
    
    private void buildAndSendEvent(RfnTestEvent event, int serialNum) {
        RfnEvent rfnEvent = buildEvent(event, new RfnEvent(), serialNum);
        
        RfnEventArchiveRequest archiveRequest = new RfnEventArchiveRequest();
        archiveRequest.setEvent(rfnEvent);
        archiveRequest.setDataPointId(1);
        sendArchiveRequest(eventArchiveRequestQueueName, archiveRequest);
    }
    
    private void buildAndSendAlarm(RfnTestEvent event, int serialNum) {
        RfnAlarm rfnAlarm = buildEvent(event, new RfnAlarm(), serialNum);
        
        RfnAlarmArchiveRequest archiveRequest = new RfnAlarmArchiveRequest();
        archiveRequest.setAlarm(rfnAlarm);
        archiveRequest.setDataPointId(1);
        sendArchiveRequest(alarmArchiveRequestQueueName, archiveRequest);
    }

    private <T extends RfnEvent> T buildEvent(RfnTestEvent testEvent, T rfnEvent, int serialNum) {
        rfnEvent.setType(testEvent.getRfnConditionType());
        RfnIdentifier meterIdentifier =
            new RfnIdentifier(Integer.toString(serialNum), testEvent.getManufacturer(), testEvent.getModel());
        rfnEvent.setRfnIdentifier(meterIdentifier);
        
        long timestamp;
        if (testEvent.getTimestamp() == null) {
            timestamp = new Instant().getMillis();
        } else {
            timestamp = testEvent.getTimestamp().getMillis();
        }
        rfnEvent.setTimeStamp(timestamp);
        
        if (testEvent.getOutageStartTime() == null) {
            testEvent.setOutageStartTime(timestamp - 60000); // 60 seconds ago
        } else if (testEvent.getOutageStartTime() == RfnInvalidValues.OUTAGE_DURATION.getValue()) {
            testEvent.setOutageStartTime(null);
        }

        Map<RfnConditionDataType, Object> testEventMap = Maps.newHashMap();
        copyDataTypesToMap(testEvent, testEventMap);
        
        // reset OutageStartTime value
        if (testEvent.getOutageStartTime() == null) {
            testEvent.setOutageStartTime(RfnInvalidValues.OUTAGE_DURATION.getValue());
        } else {
            testEvent.setOutageStartTime(null);
        }
        
        rfnEvent.setEventData(testEventMap);
        return rfnEvent;
    }
    
    private void copyDataTypesToMap(RfnTestEvent testEvent, Map<RfnConditionDataType, Object> rfnEventMap) {
        rfnEventMap.put(RfnConditionDataType.CLEARED, testEvent.getCleared());
        rfnEventMap.put(RfnConditionDataType.COUNT, testEvent.getCount());
        rfnEventMap.put(RfnConditionDataType.DIRECTION, testEvent.getDirection());
        rfnEventMap.put(RfnConditionDataType.MEASURED_VALUE, testEvent.getMeasuredValue());
        rfnEventMap.put(RfnConditionDataType.EVENT_START_TIME, testEvent.getOutageStartTime());
        rfnEventMap.put(RfnConditionDataType.THRESHOLD_VALUE, testEvent.getThresholdValue());
        rfnEventMap.put(RfnConditionDataType.UOM, testEvent.getUom());
        
        if (testEvent.getUomModifiers() != null) {
            Set<String> stringSet = Sets.newHashSet(StringUtils.split(testEvent.getUomModifiers(), ","));
            RfnUomModifierSet rfnUomModifierSet = new RfnUomModifierSet();
            rfnUomModifierSet.setUomModifiers(stringSet);
            rfnEventMap.put(RfnConditionDataType.UOM_MODIFIERS, rfnUomModifierSet);
        }
    }
    
    private <R extends RfnIdentifyingMessage> void sendArchiveRequest(String queueName, R archiveRequest) {
        log.debug("Sending archive request: " + archiveRequest.getRfnIdentifier().getCombinedIdentifier() + " on queue " + queueName);
        JmsTemplate jmsTemplate;
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.convertAndSend(queueName, archiveRequest);
    }
}

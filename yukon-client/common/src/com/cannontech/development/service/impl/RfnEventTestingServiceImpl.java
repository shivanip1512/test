package com.cannontech.development.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.cannontech.common.rfn.message.archive.RfRelayArchiveRequest;
import com.cannontech.common.rfn.message.location.LocationResponse;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.da.rfn.message.archive.RfDaArchiveRequest;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.model.RfnTestMeterReading;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RfnEventTestingServiceImpl implements RfnEventTestingService {
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ResourceLoader loader;
        
    private static final String meterReadingArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";
    private static final String lcrReadingArchiveRequestQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";
    private static final String relayReadingArchiveRequestQueueName = "yukon.qr.obj.da.rfn.RfRelayArchiveRequest";
    private static final String rfDaArchiveRequestQueueName = "yukon.qr.obj.da.rfn.RfDaArchiveRequest";
    private static final String eventArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.EventArchiveRequest";
    private static final String alarmArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.AlarmArchiveRequest";
    private static final String locationResponseQueueName = "yukon.qr.obj.amr.rfn.LocationResponse";
    
    private static final Logger log = YukonLogManager.getLogger(RfnEventTestingServiceImpl.class);

    private static final Map<String, String> modifierPaths;
    
    static {
        modifierPaths = ImmutableMap.<String, String>builder()
                .put("quad1", "Quadrant 1")
                .put("quad2", "Quadrant 2")
                .put("quad3", "Quadrant 3")
                .put("quad4", "Quadrant 4")
                //---
                .put("max", "Max")
                .put("min", "Min")
                .put("avg", "Avg")
                //---
                .put("phaseA", "Phase A")
                .put("phaseB", "Phase B")
                .put("phaseC", "Phase C")
                //---
                .put("touRateA", "TOU Rate A")
                .put("touRateB", "TOU Rate B")
                .put("touRateC", "TOU Rate C")
                .put("touRateD", "TOU Rate D")
                .put("touRateE", "TOU Rate E")
                //---
                .put("netFlow",    "Net Flow")
                .put("coincident", "Coincident")
                .put("harmonic",   "Harmonic")
                .put("cumulative", "Cumulative")
                .put("neutralToGround", "Phase Neutral->Ground")
                .build();
    }
    
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
    public int sendMeterArchiveRequests(RfnTestMeterReading reading) {
        
        RfnMeterReadingType type = reading.getType();
        if (type == null) {
            type = RfnMeterReadingType.INTERVAL;
        }
        
        int numSent = 0;
        for (int i = reading.getSerialFrom(); i <= reading.getSerialTo(); i++) {
            
            RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
            
            RfnMeterReadingData data = new RfnMeterReadingData();
            data.setTimeStamp(new Instant().getMillis());
            RfnIdentifier meterIdentifier = new RfnIdentifier(Integer.toString(i), reading.getManufacturer(), reading.getModel());
            data.setRfnIdentifier(meterIdentifier);
            data.setRecordInterval(300); // pick some default for testing, needs to be greater >= 300
            
            List<ChannelData> dataList = Lists.newArrayList();
            ChannelData channelData = new ChannelData();
            channelData.setChannelNumber(0);
            channelData.setStatus(ChannelDataStatus.OK);
            
            channelData.setUnitOfMeasure(reading.getUom());
            
            //  TODO JAVA 8
            
            Set<String> modifiers =
                    reading.getModifiers().entrySet().stream().filter(new Predicate<Map.Entry<String, Boolean>>() {
                        @Override
                        public boolean test(Entry<String, Boolean> t) {
                            return t.getValue();
                        }
                    }).map(new Function<Map.Entry<String, Boolean>, String>() {
                        @Override
                        public String apply(Entry<String, Boolean> t) {
                            return t.getKey();
                        }
                    }).map(new Function<String, String>() {
                        @Override
                        public String apply(String t) {
                            return modifierPaths.get(t);
                        }
                    }).collect(Collectors.toSet());
            
            if (reading.isRandom()) {
                channelData.setValue(Math.random() * 1000);
            } else {
                channelData.setValue(reading.getValue());
            }
            dataList.add(channelData);
            
            data.setChannelDataList(dataList);
            data.setTimeStamp(reading.getTimestampAsMillis());
            
            message.setData(data);
            message.setDataPointId(1);
            
            if (reading.getModel().contains("water")) {
                message.setReadingType(RfnMeterReadingType.INTERVAL);
                modifiers.add("Kilo");
            } else {
                message.setReadingType(type);
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
    public void sendRelayArchiveRequest(int serialFrom, int serialTo, String manufacturer, String model) {

        for (int i = serialFrom; i <= serialTo; i++) {
            // Create archive request & fake identifier
            RfRelayArchiveRequest archiveRequest = new RfRelayArchiveRequest();
            RfnIdentifier rfnIdentifier = new RfnIdentifier(Integer.toString(i),
                                                            manufacturer,
                                                            model);

            // Set all data
            archiveRequest.setRfnIdentifier(rfnIdentifier);
            archiveRequest.setNodeId(1234);

            // Put request on queue
            sendArchiveRequest(relayReadingArchiveRequestQueueName, archiveRequest);
        }
        
    }
    
    
    @Override
    public void sendLocationResponse(int serialFrom, int serialTo, String manufacturer, String model, double latitude, double longitude) {
     
        for (int i = serialFrom; i <= serialTo; i++) {
            LocationResponse locationResponse = new LocationResponse();
            RfnIdentifier rfnIdentifier = new RfnIdentifier(Integer.toString(i), manufacturer, model);
            locationResponse.setRfnIdentifier(rfnIdentifier);
            locationResponse.setLatitude(latitude);
            locationResponse.setLongitude(longitude);
            locationResponse.setLocationId(99 + i);
            locationResponse.setOrigin(Origin.RF_NODE);
            locationResponse.setLastChangedDate(new Instant().getMillis());
            sendArchiveRequest(locationResponseQueueName, locationResponse);
        }
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

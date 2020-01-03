package com.cannontech.development.service.impl;

import static com.cannontech.common.stream.StreamUtils.not;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
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
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.message.event.RfnEventArchiveRequest;
import com.cannontech.amr.rfn.message.event.RfnUomModifierSet;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.message.location.LocationResponse;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.util.ByteUtil;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.model.RfnTestMeterReading;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.RfnE2eDataIndication;
import com.cannontech.messaging.serialization.thrift.generated.RfnE2eMessagePriority;
import com.cannontech.messaging.serialization.thrift.generated.RfnE2eProtocol;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RfnEventTestingServiceImpl implements RfnEventTestingService {
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ResourceLoader loader;
        
    private static final String meterReadingArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";
    private static final String lcrReadingArchiveRequestQueueName = "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest";
    private static final String eventArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.EventArchiveRequest";
    private static final String alarmArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.AlarmArchiveRequest";
    private static final String locationResponseQueueName = "yukon.qr.obj.amr.rfn.LocationResponse";
    private static final String dataIndicationQueueName = "com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataIndication";
    
    private static final Logger log = YukonLogManager.getLogger(RfnEventTestingServiceImpl.class);

    private static final Map<String, String> modifierPaths;
    private static final Map<String, List<RfnManufacturerModel>> groupedMeterTypes;
    
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
                .put("netFlow", "Net Flow")
                .put("coincident", "Coincident")
                .put("harmonic", "Harmonic")
                .put("previous", "Previous")
                .put("cumulative", "Cumulative")
                .put("continuousCumulative", "Continuous cumulative")
                .put("neutralToGround", "Phase Neutral->Ground")
                .put("dailyMax", "Daily Max")
                .build();
        
        //  LinkedHashMap preserves insert order - this is how we want them to be displayed
        Map<String, List<RfnManufacturerModel>> groupedMeterTypesBuilder = new LinkedHashMap<>();
        
        groupedMeterTypesBuilder.put("Itron single phase", ImmutableList.of(
            RfnManufacturerModel.RFN_410CL,
            RfnManufacturerModel.RFN_420CL,
            RfnManufacturerModel.RFN_420CD));

        groupedMeterTypesBuilder.put("Landis & Gyr single phase", ImmutableList.of(
            RfnManufacturerModel.RFN_410FL,
            RfnManufacturerModel.RFN_410FX_D, 
            RfnManufacturerModel.RFN_410FX_R, 
            RfnManufacturerModel.RFN_410FD_D,
            RfnManufacturerModel.RFN_410FD_R,
            RfnManufacturerModel.RFN_420FL,
            RfnManufacturerModel.RFN_420FX,
            RfnManufacturerModel.RFN_420FD,
            RfnManufacturerModel.RFN_420FRX,
            RfnManufacturerModel.RFN_420FRD,
            RfnManufacturerModel.RFN_510FL,
            RfnManufacturerModel.RFN_520FAXD,
            RfnManufacturerModel.RFN_520FAXT,
            RfnManufacturerModel.RFN_520FAXR,
            RfnManufacturerModel.RFN_520FRXD,
            RfnManufacturerModel.RFN_520FRXT,
            RfnManufacturerModel.RFN_520FRXR,
            RfnManufacturerModel.RFN_520FAXD_SD,
            RfnManufacturerModel.RFN_520FAXT_SD,
            RfnManufacturerModel.RFN_520FAXR_SD,
            RfnManufacturerModel.RFN_520FRXD_SD,
            RfnManufacturerModel.RFN_520FRXT_SD,
            RfnManufacturerModel.RFN_520FRXR_SD));

        groupedMeterTypesBuilder.put("Landis & Gyr polyphase", ImmutableList.of(
            RfnManufacturerModel.RFN_530FAXD,
            RfnManufacturerModel.RFN_530FAXT,
            RfnManufacturerModel.RFN_530FAXR,
            RfnManufacturerModel.RFN_530FRXD,
            RfnManufacturerModel.RFN_530FRXT,
            RfnManufacturerModel.RFN_530FRXR,
            RfnManufacturerModel.RFN_530S4X,
            RfnManufacturerModel.RFN_530S4AD,
            RfnManufacturerModel.RFN_530S4AT,
            RfnManufacturerModel.RFN_530S4AR,
            RfnManufacturerModel.RFN_530S4RD,
            RfnManufacturerModel.RFN_530S4RT,
            RfnManufacturerModel.RFN_530S4RR));

        groupedMeterTypesBuilder.put("Elster A3", ImmutableList.of(
            RfnManufacturerModel.RFN_430A3D,
            RfnManufacturerModel.RFN_430A3T,
            RfnManufacturerModel.RFN_430A3K,
            RfnManufacturerModel.RFN_430A3R));

        groupedMeterTypesBuilder.put("General Electric polyphase", ImmutableList.of(
            RfnManufacturerModel.RFN_430KV));

        groupedMeterTypesBuilder.put("Schlumberger polyphase", ImmutableList.of(
            RfnManufacturerModel.RFN_430SL0,
            RfnManufacturerModel.RFN_430SL1,
            RfnManufacturerModel.RFN_430SL2,
            RfnManufacturerModel.RFN_430SL3,
            RfnManufacturerModel.RFN_430SL4));

        groupedMeterTypesBuilder.put("Eaton Water 2", ImmutableList.of(
            RfnManufacturerModel.RFW201_PULSE,
            RfnManufacturerModel.RFW201_ENCODER));

        groupedMeterTypesBuilder.put("Legacy Water", ImmutableList.of(
            RfnManufacturerModel.RFN_WATER_SENSOR));

        groupedMeterTypesBuilder.put("ELO", ImmutableList.of(
            RfnManufacturerModel.RFN_440_2131TD,
            RfnManufacturerModel.RFN_440_2132TD,
            RfnManufacturerModel.RFN_440_2133TD));
        
        groupedMeterTypesBuilder.put("Eaton Gas 2", ImmutableList.of(
            RfnManufacturerModel.RFG201_PULSE));
        
        groupedMeterTypesBuilder.put("Eaton Integrated Gas", ImmutableList.of(
            RfnManufacturerModel.RFG301_PULSE));
        
        groupedMeterTypesBuilder.put("Relays", ImmutableList.of(
            RfnManufacturerModel.RFN_RELAY));
        
        groupedMeterTypes = Collections.unmodifiableMap(groupedMeterTypesBuilder);
    }
    
    @Override
    public Map<String, List<RfnManufacturerModel>> getGroupedRfnTypes() {
        return groupedMeterTypes;
    }

    @Override
    public int sendEventsAndAlarms(RfnTestEvent event) {
        int[] serials = Optional.ofNullable(event.getSerialTo())
                                .map(to -> IntStream.rangeClosed(event.getSerialFrom(), to).toArray())
                                .orElse(new int[] {event.getSerialFrom()});
        for (int serial : serials) {
            IntStream.range(0, event.getNumEventPerMeter()).forEach(unused -> buildAndSendEvent(event, serial));
            IntStream.range(0, event.getNumAlarmPerMeter()).forEach(unused -> buildAndSendAlarm(event, serial));
        }
        return serials.length * (event.getNumEventPerMeter() + event.getNumAlarmPerMeter());
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
    public int sendMeterReadArchiveRequests(RfnTestMeterReading reading) {
        
        RfnMeterReadingType type = reading.getType();
        
        int[] serials = Optional.ofNullable(reading.getSerialTo())
                                .map(to -> IntStream.rangeClosed(reading.getSerialFrom(), to).toArray())
                                .orElse(new int[] {reading.getSerialFrom()});
        
        for (int serial : serials) {
            RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
            
            RfnMeterReadingData data = new RfnMeterReadingData();
            data.setTimeStamp(new Instant().getMillis());
            String manufacturer = 
                    Optional.ofNullable(reading.getManufacturerOverride())
                                .filter(not(String::isEmpty))
                                .orElse(reading.getManufacturerModel().getManufacturer());
            String model = 
                    Optional.ofNullable(reading.getModelOverride())
                                .filter(not(String::isEmpty))
                                .orElse(reading.getManufacturerModel().getModel());
            RfnIdentifier meterIdentifier = new RfnIdentifier(Integer.toString(serial), manufacturer, model);
            data.setRfnIdentifier(meterIdentifier);
            data.setRecordInterval(300); // pick some default for testing, needs to be greater >= 300
            
            List<ChannelData> dataList = Lists.newArrayList();
            ChannelData channelData = new ChannelData();
            channelData.setChannelNumber(0);
            channelData.setStatus(ChannelDataStatus.OK);
            
            channelData.setUnitOfMeasure(reading.getUom());
            
            Set<String> modifiers =
                    reading.getModifiers().stream()
                        .map(modifierPaths::get)
                        .collect(Collectors.toSet());
            
            if (reading.isRandom()) {
                channelData.setValue(ThreadLocalRandom.current().nextLong(1000));
            } else {
                channelData.setValue(reading.getValue());
            }
            dataList.add(channelData);
            
            data.setChannelDataList(dataList);
            if (reading.isNow()) {
                data.setTimeStamp(new Instant().getMillis());
            } else {
                data.setTimeStamp(reading.getTimestampAsMillis());
            }
            
            message.setData(data);
            //  On-demand meter reads don't set a dataPointId
            if (type != RfnMeterReadingType.CURRENT) {
                message.setDataPointId(ThreadLocalRandom.current().nextLong(1000000000));
            }
                
            if (reading.getManufacturerModel().getModel().contains("water")) {
                message.setReadingType(RfnMeterReadingType.INTERVAL);
                modifiers.add("Kilo");
            } else {
                message.setReadingType(type);
            }
            channelData.setUnitOfMeasureModifiers(modifiers);
            
            sendArchiveRequest(meterReadingArchiveRequestQueueName, message);
        }
        return serials.length;
    }
    
    @Override
    public int sendLcrReadArchive(int serialFrom, int serialTo, int days, DRReport drReport) throws IOException, DecoderException {
        if (serialTo < serialFrom) {
            serialTo = serialFrom;
        }
        int numRequests = 0;
        for (int serial = serialFrom; serial <= serialTo; serial++) {
            for (int i = 0; i < days; i++) {
                // Create archive request
                RfnLcrReadingArchiveRequest readArchiveRequest = new RfnLcrReadingArchiveRequest();
                RfnLcrReading data = new RfnLcrReading();
                
                // Read test encoded EXI/TLV file from classpath, assign it to payload.
                byte[] payload;
                Resource payloadResource = loader.getResource(drReport.getClasspath());
                if (payloadResource.exists() && drReport.getType() != PaoType.LCR6700_RFN) {
                    payload = FileCopyUtils.copyToByteArray(payloadResource.getInputStream());
                } else if (drReport.getType() == PaoType.LCR6700_RFN) {
                    payload = Hex.decodeHex(IOUtils.toCharArray(payloadResource.getInputStream()));
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
    public void sendLocationResponse(int serialFrom, int serialTo, String manufacturer, String model, double latitude, double longitude) {
     
        for (int i = serialFrom; i <= serialTo; i++) {
            LocationResponse locationResponse = new LocationResponse();
            RfnIdentifier rfnIdentifier = new RfnIdentifier(Integer.toString(i), manufacturer, model);
            locationResponse.setRfnIdentifier(rfnIdentifier);
            locationResponse.setLatitude(latitude);
            locationResponse.setLongitude(longitude);
            locationResponse.setLocationId(99L + i);
            locationResponse.setOrigin(Origin.RF_NODE);
            locationResponse.setLastChangedDate(new Instant().getMillis());
            sendArchiveRequest(locationResponseQueueName, locationResponse);
        }
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
            new RfnIdentifier(Integer.toString(serialNum), 
                              testEvent.getManufacturerModel().getManufacturer(), 
                              testEvent.getManufacturerModel().getModel());
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
        if (RfnConditionType.REMOTE_METER_CONFIGURATION_FAILURE.equals(testEvent.getRfnConditionType())) {
            rfnEventMap.put(RfnConditionDataType.METER_CONFIGURATION_ID, testEvent.getMeterConfigurationId());
            rfnEventMap.put(RfnConditionDataType.METER_CONFIGURATION_STATUS, testEvent.getMeterConfigurationStatus());
        }
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

    @Override
    public int sendConfigNotification(RfnTestMeterReading reading) {
        RfnE2eDataIndication dataIndication = new RfnE2eDataIndication();
        
        com.cannontech.messaging.serialization.thrift.generated.RfnIdentifier thriftRfnIdentifier = 
                new com.cannontech.messaging.serialization.thrift.generated.RfnIdentifier();
        thriftRfnIdentifier.setSensorManufacturer(reading.getManufacturerModel().getManufacturer());
        thriftRfnIdentifier.setSensorModel(reading.getManufacturerModel().getModel());
    
        int[] serials = Optional.ofNullable(reading.getSerialTo())
                                .map(to -> IntStream.rangeClosed(reading.getSerialFrom(), to).toArray())
                                .orElse(new int[] {reading.getSerialFrom()});

        dataIndication.setE2eProtocol(RfnE2eProtocol.findByValue(0));
        dataIndication.setApplicationServiceId((byte)0xFE);
        dataIndication.setPriority(RfnE2eMessagePriority.findByValue(1));
        
        //  Details in Device Configuration Aggregated Messaging Design.docx:
        //  http://teams.etn.com/es/EASEngineering/EAS%20Engineering%20Documents/End%20Point%20Automation/Network%20Communications%20Software/Releases/Release%209.0/Design/Device%20Configuration%20Aggregated%20Messaging%20Design.docx
        byte[] payload = ByteUtil.parseHexArray(
                  //  CoAP header
                  "42 01 c2 43  42 f1 ff"
                  //  RFN Config Notification  
                  + " 1e"
                  //  15 TLVs
                  + " 00 0f"
                  //  type 11, length 18 (OVUV)
                  + " 00 0b  00 12"
                  + " 04 07 e6 00  0f 3c 02 01  06 00 01 f4  00 10 80 00  01 c0"
                  //  type 11, length 18 (OVUV)
                  + " 00 0b  00 12"
                  + " 04 07 e7 00  0f 3c 02 01  06 00 01 b5  80 10 80 00  01 c0"
                  //  type 12, length 7 (Temperature)
                  + " 00 0c  00 07"
                  + " 01 00 52 00  48 0f 03"
                  //  type 1, length 1 (TOU enable)
                  + " 00 01  00 01"
                  + " 01"
                  //  type 2, length 56 (TOU schedule)
                  + " 00 02  00 38"
                  + " 00 00 00 02  1c 00 b4 00  b4 00 3c 01  e0 05 a0 00"
                  + " 00 00 00 00  00 00 00 05  a0 00 00 00  00 00 00 00"
                  + " 00 05 a0 00  00 00 00 00  00 00 00 08  00 00 01 00"
                  + " 00 02 00 00  03 00 00 01"
                  //  type 3, length 12 (TOU holiday)
                  + " 00 03  00 0c"
                  + " 55 55 55 55  66 66 66 66  77 77 77 77"
                  //  type 4, length 1 (Demand freeze day)
                  + " 00 04  00 01"
                  + " 0f"
                  //  type 8, length 2 (Voltage profile)
                  + " 00 08  00 02"
                  + " 08 05"
                  //  type 9, length 59 (C2SX display)
                  + " 00 09  00 3b"
                  + " 1d 00 02 01  02 02 02 03  08 04 08 05  08 06 08 07"
                  + " 09 08 09 09  09 0a 09 0b  00 0c 00 0d  00 0e 00 0f"
                  + " 00 10 00 11  00 12 00 13  00 14 00 15  00 16 00 17"
                  + " 00 18 00 19  00 fd 05 fe  01 ff 01"
                  //  type 6, length 3 (Channel selection)
                  + " 00 06  00 03"
                  + " 01 00 01"
                  //  type 14, length 1 (Demand interval)
                  + " 00 0e  00 01"
                  + " 0f"
                  //  type 7, length 2 (Disconnect)
                  + " 00 07  00 02"
                  + " 01 01"
                  //  type 5, length 11 (Interval recording/reporting)
                  + " 00 05  00 0b"
                  + " 00 00 03 84  00 00 1c 20  01 00 01"
                  //  type 13, length 26 (Data streaming)
                  + " 00 0d  00 1a"
                  + " 04 00 00 01  00 1e 00 00  02 00 1e 07  00 05 00 1e"
                  + " 07 00 73 00  1e 07 00 00  00 03"
                  //  type 15, length 1 (Voltage profile status)
                  + " 00 0f  00 01"
                  + " 00");

        dataIndication.setPayload(payload);

        SimpleThriftSerializer serializer = new SimpleThriftSerializer() {};
        int messagesSent = 0;
        
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Destination dest = session.createQueue(dataIndicationQueueName);
            try (MessageProducer producer = session.createProducer(dest)) {
                for (Integer serial : serials) {
                    BytesMessage outBytesMsg = session.createBytesMessage();
                    thriftRfnIdentifier.setSensorSerialNumber(Integer.toString(serial));
                    dataIndication.setRfnIdentifier(thriftRfnIdentifier);
                    outBytesMsg.writeBytes(serializer.serialize(dataIndication));
                    producer.send(outBytesMsg);
                    messagesSent++;
                }
            }
        } catch (Exception e) {
            log.error("Error sending config notification to porter: " + e);
        }

        return messagesSent;
    }

}

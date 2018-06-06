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
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Destination;

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
import com.cannontech.common.rfn.message.archive.RfRelayArchiveRequest;
import com.cannontech.common.rfn.message.location.LocationResponse;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.da.rfn.message.archive.RfDaArchiveRequest;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.model.RfnTestMeterReading;
import com.cannontech.development.service.RfnEventTestingService;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.cannontech.messaging.serialization.thrift.generated.RfnE2eDataIndication;
import com.cannontech.messaging.serialization.thrift.generated.RfnE2eMessagePriority;
import com.cannontech.messaging.serialization.thrift.generated.RfnE2eProtocol;
import com.cannontech.messaging.serialization.thrift.serializer.porter.DynamicPaoInfoRequestSerializer;
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
    private static final String relayReadingArchiveRequestQueueName = "yukon.qr.obj.rfn.RfRelayArchiveRequest";
    private static final String rfDaArchiveRequestQueueName = "yukon.qr.obj.da.rfn.RfDaArchiveRequest";
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
                .put("netFlow",    "Net Flow")
                .put("coincident", "Coincident")
                .put("harmonic",   "Harmonic")
                .put("cumulative", "Cumulative")
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
            RfnManufacturerModel.RFN_530FAX,
            RfnManufacturerModel.RFN_530FRX,
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
            RfnManufacturerModel.RFW201_ENCODER,
            RfnManufacturerModel.RFW205_PULSE,
            RfnManufacturerModel.RFW205_ENCODER));

        groupedMeterTypesBuilder.put("Legacy Water", ImmutableList.of(
            RfnManufacturerModel.RFN_WATER_SENSOR));

        groupedMeterTypesBuilder.put("ELO", ImmutableList.of(
            RfnManufacturerModel.RFN_440_2131TD,
            RfnManufacturerModel.RFN_440_2132TD,
            RfnManufacturerModel.RFN_440_2133TD));
        
        groupedMeterTypes = Collections.unmodifiableMap(groupedMeterTypesBuilder);
    }
    
    @Override
    public Map<String, List<RfnManufacturerModel>> getGroupedRfnTypes() {
        return groupedMeterTypes;
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
    public int sendMeterReadArchiveRequests(RfnTestMeterReading reading) {
        
        RfnMeterReadingType type = reading.getType();
        
        List<Integer> serials = 
                Optional.ofNullable(reading.getSerialTo())
                    .map(to -> IntStream.rangeClosed(reading.getSerialFrom(), to))
                    .orElseGet(() -> IntStream.of(reading.getSerialFrom()))
                    .boxed()
                    .collect(Collectors.toList());
        
        serials.forEach(serial -> {
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
        });
        return serials.size();
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

    @Override
    public int sendDataIndicationMessage(RfnTestMeterReading reading) {
        RfnE2eDataIndication dataIndication = new RfnE2eDataIndication();
        
        com.cannontech.messaging.serialization.thrift.generated.RfnIdentifier thriftRfnIdentifier = 
                new com.cannontech.messaging.serialization.thrift.generated.RfnIdentifier();
        thriftRfnIdentifier.setSensorManufacturer(reading.getManufacturerModel().getManufacturer());
        thriftRfnIdentifier.setSensorModel(reading.getManufacturerModel().getModel());
    
        List<Integer> serials = 
                Optional.ofNullable(reading.getSerialTo())
                    .map(to -> IntStream.rangeClosed(reading.getSerialFrom(), to))
                    .orElseGet(() -> IntStream.of(reading.getSerialFrom()))
                    .boxed()
                    .collect(Collectors.toList());

        dataIndication.setE2eProtocol(RfnE2eProtocol.findByValue(0));
        dataIndication.setApplicationServiceId((byte)0xFE);
        dataIndication.setPriority(RfnE2eMessagePriority.findByValue(1));
        byte[] payload = { (byte)0x42, (byte)0x01, (byte)0xdb, (byte)0x9f, (byte)0x00, (byte)0x7a, (byte)0xff,
                           (byte)0x1e, (byte)0x00, (byte)0x0e, (byte)0x00, (byte)0x0b, (byte)0x00, (byte)0x12,
                           (byte)0x04, (byte)0x07, (byte)0xe6, (byte)0x00, (byte)0x0f, (byte)0x3c, (byte)0x02,
                           (byte)0x01, (byte)0x06, (byte)0x00, (byte)0x01, (byte)0xf4, (byte)0x00, (byte)0x10,
                           (byte)0x80, (byte)0x00, (byte)0x01, (byte)0xc0, (byte)0x00, (byte)0x0b, (byte)0x00,
                           (byte)0x12, (byte)0x04, (byte)0x07, (byte)0xe7, (byte)0x00, (byte)0x0f, (byte)0x3c, 
                           (byte)0x02, (byte)0x01, (byte)0x06, (byte)0x00, (byte)0x01, (byte)0xb5, (byte)0x80, 
                           (byte)0x10, (byte)0x80, (byte)0x00, (byte)0x01, (byte)0xc0, (byte)0x00, (byte)0x0c, 
                           (byte)0x00, (byte)0x07, (byte)0x01, (byte)0x00, (byte)0xb4, (byte)0x00, (byte)0x3c, 
                           (byte)0x01, (byte)0xe0, (byte)0x05, (byte)0xa0, (byte)0x00, (byte)0x00, (byte)0x00, 
                           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x05, (byte)0xa0, 
                           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                           (byte)0x00, (byte)0x05, (byte)0xa0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x00, 
                           (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x03, 
                           (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x0c, 
                           (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x55, (byte)0x66, (byte)0x66, (byte)0x66, 
                           (byte)0x66, (byte)0x77, (byte)0x77, (byte)0x77, (byte)0x77, (byte)0x00, (byte)0x04, 
                           (byte)0x52, (byte)0x00, (byte)0x48, (byte)0x0f, (byte)0x03, (byte)0x00, (byte)0x01, 
                           (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x38, 
                           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x1c, (byte)0x00, (byte)0xb4, 
                           (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x0f, (byte)0x00, (byte)0x08, (byte)0x00, 
                           (byte)0x02, (byte)0x08, (byte)0x05, (byte)0x00, (byte)0x09, (byte)0x00, (byte)0x3b, 
                           (byte)0x1d, (byte)0x00, (byte)0x02, (byte)0x01, (byte)0x02, (byte)0x02, (byte)0x02, 
                           (byte)0x03, (byte)0x08, (byte)0x04, (byte)0x08, (byte)0x05, (byte)0x08, (byte)0x06, 
                           (byte)0x08, (byte)0x07, (byte)0x09, (byte)0x08, (byte)0x09, (byte)0x09, (byte)0x09, 
                           (byte)0x0a, (byte)0x09, (byte)0x0b, (byte)0x00, (byte)0x0c, (byte)0x00, (byte)0x0d, 
                           (byte)0x00, (byte)0x0e, (byte)0x00, (byte)0x0f, (byte)0x00, (byte)0x10, (byte)0x00, 
                           (byte)0x11, (byte)0x00, (byte)0x12, (byte)0x00, (byte)0x13, (byte)0x00, (byte)0x14, 
                           (byte)0x00, (byte)0x15, (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x17, (byte)0x00, 
                           (byte)0x18, (byte)0x00, (byte)0x19, (byte)0x00, (byte)0xfd, (byte)0x05, (byte)0xfe, 
                           (byte)0x01, (byte)0xff, (byte)0x01, (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x03, 
                           (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x0e, (byte)0x00, (byte)0x01, 
                           (byte)0x0f, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x02, (byte)0x01, (byte)0x01, 
                           (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x0b, (byte)0x00, (byte)0x00, (byte)0x03, 
                           (byte)0x84, (byte)0x00, (byte)0x00, (byte)0x1c, (byte)0x20, (byte)0x01, (byte)0x00, 
                           (byte)0x01, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x1a, (byte)0x04, (byte)0x00, 
                           (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x1e, (byte)0x00, (byte)0x00, (byte)0x02, 
                           (byte)0x00, (byte)0x1e, (byte)0x07, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x1e, 
                           (byte)0x07, (byte)0x00, (byte)0x73, (byte)0x00, (byte)0x1e, (byte)0x07, (byte)0x00, 
                           (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x0f, (byte)0x00, (byte)0x01, 
                           (byte)0x00 
        };
        dataIndication.setPayload(payload);

        //just need a simple thrift serializer, doesn't matter which implementation
        DynamicPaoInfoRequestSerializer serializer = new DynamicPaoInfoRequestSerializer();
        
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = session.createQueue(dataIndicationQueueName);

            for (Integer serial : serials) {
                BytesMessage outBytesMsg = session.createBytesMessage();
                thriftRfnIdentifier.setSensorSerialNumber(Integer.toString(serial));
                dataIndication.setRfnIdentifier(thriftRfnIdentifier);
                outBytesMsg.writeBytes(serializer.serialize(dataIndication));
                MessageProducer producer = session.createProducer(dest);
                producer.send(outBytesMsg);
            };
            
            session.close();
            connection.close();
        } catch (Exception e) {
            //do nothing
        }

        return serials.size();
    }
}

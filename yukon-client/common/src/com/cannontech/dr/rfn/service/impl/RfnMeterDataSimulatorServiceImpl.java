package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.DatedChannelData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper.ModifiersMatcher;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper.PointMapper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.dr.rfn.model.RfnMeterSimulatorStatus;
import com.cannontech.dr.rfn.service.RfnMeterDataSimulatorService;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class RfnMeterDataSimulatorServiceImpl implements RfnMeterDataSimulatorService {
    private final Logger log = YukonLogManager.getLogger(RfnMeterDataSimulatorServiceImpl.class);

    private static final String meterReadingArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    private final static Map<Integer,Long> perMinuteMsgCount = new ConcurrentHashMap<>();
    private static List<Future<?>> futures;
    private ScheduledFuture<?> msgSimulatorFt = null;
    private Map<RfnDevice, Map<Attribute, TimestampValue>> storedValue = new HashMap<>();
    private int hourlyCounter = 0;
    private int perDayCounter = 0;
    private int minsCounter = 0;
    private RfnMeterSimulatorStatus rfnMeterSimulatorStatus = new RfnMeterSimulatorStatus();
    
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
    }

    public RfnMeterSimulatorStatus getExistingRfnMeterSimulatorStatus() {
        return rfnMeterSimulatorStatus;        
    }
    
    public long getPerMinuteMsgCount() {
        int minuteOfHour = new Instant().get(DateTimeFieldType.minuteOfHour());
        if (perMinuteMsgCount.isEmpty() || !perMinuteMsgCount.containsKey(minuteOfHour)) {
            return 0;
        } else {
            return perMinuteMsgCount.get(minuteOfHour);
        }
    }

    @Override
    public synchronized void sendRfnMeterMessages(List<RfnDevice> rfnDeviceList,
            Multimap<PaoType, PointMapper> pointMapperMap) {

        futures = new ArrayList<Future<?>>();
        
        final AtomicBoolean isRunningRfnBillingType = rfnMeterSimulatorStatus.getIsRunningRfnBillingType();
        isRunningRfnBillingType.set(true);
        final AtomicBoolean isRunningRfnCurrentType = rfnMeterSimulatorStatus.getIsRunningRfnCurrentType();
        isRunningRfnCurrentType.set(true);
        final AtomicBoolean isRunningRfnIntervalType = rfnMeterSimulatorStatus.getIsRunningRfnIntervalType();
        isRunningRfnIntervalType.set(true);
        final AtomicBoolean isRunningRfnProfileType = rfnMeterSimulatorStatus.getIsRunningRfnProfileType();
        isRunningRfnProfileType.set(true);
        final AtomicBoolean isRunningRfnStatusType = rfnMeterSimulatorStatus.getIsRunningRfnStatusType();
        isRunningRfnStatusType.set(true);
        
        int minsCounter = 0;
        try {
            Map<RfnMeterReadingType, Integer> messagePerMin = getNumberOfMessagesPerMinute(rfnDeviceList);
            msgSimulatorFt = executor.scheduleAtFixedRate(new MessageSimulator(rfnDeviceList,
                                                                               pointMapperMap,
                                                                               messagePerMin),
                                                          minsCounter++,
                                                          1,
                                                          TimeUnit.MINUTES);
            futures.add(msgSimulatorFt);

        } catch (Exception e) {
            log.error("RF Meter Data Simulator has encountered an unexpected error.", e);
            rfnMeterSimulatorStatus.setErrorMessage(e.getMessage());
        }
    }
    
    /**
     * Identifies number of message to send per min for each reading type
     * (BILLING, INTERVAL,PROFILE).
     */

    private Map<RfnMeterReadingType, Integer> getNumberOfMessagesPerMinute(List<RfnDevice> rfnDeviceList) {

        Map<RfnMeterReadingType, Integer> readingTypeToMessagesPerMinute = new HashMap<>();
        int totalDevices = rfnDeviceList.size();
        // For billing the messages have to be distributed for 24 hrs.
        readingTypeToMessagesPerMinute.put(RfnMeterReadingType.BILLING,
                              (int) Math.ceil((double) totalDevices / 24 / 60));
        readingTypeToMessagesPerMinute.put(RfnMeterReadingType.INTERVAL,
                              (int) Math.ceil((double) totalDevices / 60));
        readingTypeToMessagesPerMinute.put(RfnMeterReadingType.PROFILE,
                              (int) Math.ceil((double) totalDevices / 60));
        return readingTypeToMessagesPerMinute;
    }
    
    /**
     * Loop through RFN meters and send the messages.
     */
    private class MessageSimulator implements Runnable {
        List<RfnDevice> rfnMeterList = null;
        Multimap<PaoType, PointMapper> pointMapperMap = null;
        Map<RfnMeterReadingType, Integer> messagePerMin = null;

        MessageSimulator(List<RfnDevice> rfnMeterList,
                Multimap<PaoType, PointMapper> pointMapperMap,
                Map<RfnMeterReadingType, Integer> messagePerMin) {
            this.rfnMeterList = rfnMeterList;
            this.pointMapperMap = pointMapperMap;
            this.messagePerMin = messagePerMin;
        }

        @Override
        public void run() {
            log.debug("RFN Meter message simulator sending message...");

            if (new Instant().get(DateTimeFieldType.minuteOfDay()) == 0) {
                // Reinitialize counter for the fresh day
                rfnMeterSimulatorStatus.resetCompletionState();
            }

            AtomicLong billingTypeNumComplete = rfnMeterSimulatorStatus.getNumCompleteRfnBillingType();
            AtomicLong currentTypeNumComplete = rfnMeterSimulatorStatus.getNumCompleteRfnCurrentType();
            AtomicLong intervalTypeNumComplete = rfnMeterSimulatorStatus.getNumCompleteRfnIntervalType();
            AtomicLong profileTypeNumComplete = rfnMeterSimulatorStatus.getNumCompleteRfnProfileType();
            AtomicLong statusTypeNumComplete = rfnMeterSimulatorStatus.getNumCompleteRfnStatusType();

            RfnMeterReadingArchiveRequest meterArchiveRequest;
            boolean sendBilling = false;
            // For reading type "Billing" messages should be send per hour
            if (minsCounter % 60 == 0) {
                hourlyCounter = 0;
                sendBilling = true;
            }
            // Reset the counter after 24 hrs.
            if (minsCounter % 1440 == 0) {
                perDayCounter = 0;
            }
            minsCounter++;
            Map<RfnMeterReadingType, List<RfnDevice>> meterList =
                generateMeterList(rfnMeterList, messagePerMin, sendBilling);
            for (RfnMeterReadingType type : meterList.keySet()) {
                for (RfnDevice meter : meterList.get(type)) {
                    RfnMeterReadingData meterReadingData = generateMeterReadingData(meter, pointMapperMap, type);
                    meterArchiveRequest = generateArchiveRequest(meterReadingData, type);
                    sendArchiveRequest(meterReadingArchiveRequestQueueName, meterArchiveRequest);

                    long msgCount = 0;
                    if (type.equals(RfnMeterReadingType.BILLING)) {
                        int minuteOfHour = new Instant().get(DateTimeFieldType.minuteOfHour());
                        if (perMinuteMsgCount.containsKey(minuteOfHour)) {
                            msgCount = perMinuteMsgCount.get(minuteOfHour);
                        }
                        msgCount++;
                        perMinuteMsgCount.clear();
                        // the record will have the number of messages sent for the current minute only
                        perMinuteMsgCount.put(minuteOfHour, msgCount);
                        billingTypeNumComplete.incrementAndGet();
                        rfnMeterSimulatorStatus.setLastFinishedInjectionRfnBillingType(Instant.now());
                    }
                    if (type.equals(RfnMeterReadingType.CURRENT)) {
                        currentTypeNumComplete.incrementAndGet();
                        rfnMeterSimulatorStatus.setLastFinishedInjectionRfnCurrentType(Instant.now());
                    }
                    if (type.equals(RfnMeterReadingType.INTERVAL)) {
                        intervalTypeNumComplete.incrementAndGet();
                        rfnMeterSimulatorStatus.setLastFinishedInjectionRfnIntervalType(Instant.now());
                    }
                    if (type.equals(RfnMeterReadingType.PROFILE)) {
                        profileTypeNumComplete.incrementAndGet();
                        rfnMeterSimulatorStatus.setLastFinishedInjectionRfnProfileType(Instant.now());
                    }
                    if (type.equals(RfnMeterReadingType.STATUS)) {
                        statusTypeNumComplete.incrementAndGet();
                        rfnMeterSimulatorStatus.setLastFinishedInjectionRfnStatusType(Instant.now());
                    }
                }
            }
        }

    }
    
    /**
     * Identifies the devices for which messages have to be send for a minute.
     */
    private Map<RfnMeterReadingType, List<RfnDevice>> generateMeterList(List<RfnDevice> rfnMeterList,
            Map<RfnMeterReadingType, Integer> messagePerMin, boolean sendHourly) {
        Map<RfnMeterReadingType, List<RfnDevice>> meterList = new HashMap<>();

        if (sendHourly) {
            if ((perDayCounter + messagePerMin.get(RfnMeterReadingType.BILLING)) <= rfnMeterList.size()) {
                List<RfnDevice> billingDevices =
                    rfnMeterList.subList(perDayCounter, perDayCounter + messagePerMin.get(RfnMeterReadingType.BILLING));
                meterList.put(RfnMeterReadingType.BILLING, billingDevices);
                perDayCounter = perDayCounter + messagePerMin.get(RfnMeterReadingType.BILLING);
            }
        }
        if ((hourlyCounter + messagePerMin.get(RfnMeterReadingType.INTERVAL)) <= rfnMeterList.size()) {
            List<RfnDevice> intervalDevices =
                rfnMeterList.subList(hourlyCounter, hourlyCounter + messagePerMin.get(RfnMeterReadingType.INTERVAL));
            meterList.put(RfnMeterReadingType.INTERVAL, intervalDevices);
        }

        if ((hourlyCounter + messagePerMin.get(RfnMeterReadingType.PROFILE)) <= rfnMeterList.size()) {
            List<RfnDevice> profileDevices =
                rfnMeterList.subList(hourlyCounter, hourlyCounter + messagePerMin.get(RfnMeterReadingType.PROFILE));
            meterList.put(RfnMeterReadingType.PROFILE, profileDevices);
        }

        if (((hourlyCounter + messagePerMin.get(RfnMeterReadingType.INTERVAL)) <= rfnMeterList.size())
            || (hourlyCounter + messagePerMin.get(RfnMeterReadingType.PROFILE) <= rfnMeterList.size())) {
            hourlyCounter = hourlyCounter + messagePerMin.get(RfnMeterReadingType.INTERVAL);
        }
        return meterList;
    }

    /**
     * Generate the actual RFN meter message to send
     */
    private RfnMeterReadingArchiveRequest generateArchiveRequest(
            RfnMeterReadingData meterReadingData, RfnMeterReadingType readingType) {
        RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
        message.setReadingType(readingType);
        message.setData(meterReadingData);
        message.setDataPointId(1);
        log.debug("Sending RFN Meter Message " + message);
        return message;
    }
    
    /**
     * Generate the data for each device that has to be send
     */
    private RfnMeterReadingData generateMeterReadingData(RfnDevice device,
            Multimap<PaoType, PointMapper> pointMapperMap, RfnMeterReadingType readingType) {

        List<BuiltInAttribute> attributeToGenerateMessage = RfnMeterSimulatorConfiguration.getValuesByMeterReadingType(readingType);
        RfnMeterReadingData data = new RfnMeterReadingData();
        data.setTimeStamp(new Instant().getMillis());
        data.setRfnIdentifier(device.getRfnIdentifier());
        data.setRecordInterval(300);

        List<ChannelData> channelDataList = Lists.newArrayList();
        List<DatedChannelData> datedChannelDataList = Lists.newArrayList();
        int channelNo = 1;
        for (PointMapper pointMapper : pointMapperMap.get(device.getPaoIdentifier().getPaoType())) {
            DatedChannelData datedChannelData = new DatedChannelData();
            String pointName = pointMapper.getName();
            Attribute attribute = getAttributeForPoint(device.getPaoIdentifier().getPaoType(),
                                                       pointName);
            if (attributeToGenerateMessage.contains(attribute)) {
                if (attribute != null && RfnMeterSimulatorConfiguration.attributeExists(attribute.toString())) {
                    TimestampValue timestampValue = getValueAndTimestampForPoint(device,
                                                                                 attribute);
                    datedChannelData.setChannelNumber(channelNo);
                    channelNo++;
                    datedChannelData.setStatus(ChannelDataStatus.OK);
                    datedChannelData.setUnitOfMeasure(pointMapper.getUom());
                    datedChannelData.setValue(timestampValue.getValue());

                    Set<String> modifiers = null;
                    for (ModifiersMatcher match : pointMapper.getModifiersMatchers()) {
                        modifiers = match.getModifiers();
                    }
                    datedChannelData.setUnitOfMeasureModifiers(modifiers);
                   
                    if (!RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).isDated()) { 
                        channelDataList.add(datedChannelData);
                    } else { 
                        datedChannelData.setTimeStamp(DateTime.now().getMillis());
                        datedChannelDataList.add(datedChannelData);
                    }
                }
            }
        }
        data.setChannelDataList(channelDataList);
        data.setDatedChannelDataList(datedChannelDataList);
        return data;
    }
    
    /**
     *  Find the attribute associated with a point
     */
    private Attribute getAttributeForPoint(PaoType paoType, String pointName) {
        Map<Attribute, AttributeDefinition> attrDefMap = paoDefinitionDao.getPaoAttributeAttrDefinitionMap()
                                                                         .get(paoType);
        for (AttributeDefinition attrDef : attrDefMap.values()) {
            String pointNameForAttribute = attrDef.getPointTemplate().getName();
            if (pointNameForAttribute.equals(pointName)) {
                return attrDef.getAttribute();
            }
        }
        return null;
    }

    
    
    /**
     *  Generate Value and Timestamp for each point. This will generate the same value for a point based on if its hourly
     * or daily.
     */
    private TimestampValue getValueAndTimestampForPoint(RfnDevice device, Attribute attribute) {

        Map<Attribute, TimestampValue> storedDevice = storedValue.get(device);
        TimestampValue storedTimestampValue = null;
        Double generateValue = 0.0;
        
        if (storedDevice != null) {
            storedTimestampValue = storedDevice.get(attribute);
            if (storedTimestampValue != null) {
                if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getGenerationType() == GenerationType.HOURLY) {
                    if (storedTimestampValue.getTimestamp().isAfter((DateTime.now().minusHours(1)))) {
                        return storedTimestampValue;
                    }
                } else if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getGenerationType() == GenerationType.DAILY) {
                    if (storedTimestampValue.getTimestamp().isAfter((DateTime.now().minusDays(1)))) {
                        return storedTimestampValue;
                    }
                    generateValue = generateValue(attribute, storedTimestampValue.getValue());
                } else {
                    generateValue = generateValue(attribute, storedTimestampValue.getValue());
                }
            } else {
                generateValue = generateValue(attribute, 0.0);
            }
            TimestampValue timestampValue = new TimestampValue(DateTime.now(), generateValue);
            Map<Attribute, TimestampValue> obj = storedValue.get(device);
            obj.put(attribute, timestampValue);
            return timestampValue;

        } else {
            // If device does not exists, add in map
            generateValue = generateValue(attribute, 0.0);
            TimestampValue timestampValue = new TimestampValue(DateTime.now(), generateValue);
            Map<Attribute, TimestampValue> timestampValueMap = new HashMap<>();
            timestampValueMap.put(attribute, timestampValue);
            storedValue.put(device, timestampValueMap);
            return timestampValue;
        }
    }

    /**
     * Generate value based on the last value and incrementor value
     */
    private Double generateValue(Attribute attribute, Double lastValue) {

        Double maxValue;
        Double minValue = lastValue;
        double result = 0.0;
        double random = 0.0;
        GeneratedValueType valueType = RfnMeterSimulatorConfiguration.getRfnMeterValueGenerator(attribute)
                                                             .getValueType();

        if (minValue == 0.0 || minValue > minValue + RfnMeterSimulatorConfiguration.valueOf(attribute.toString())
                                                                           .getChangeBy()) {
            minValue = RfnMeterSimulatorConfiguration.getRfnMeterValueGenerator(attribute).getMinValue();
        }

        if (valueType == GeneratedValueType.INCREASING) {
            maxValue = minValue + RfnMeterSimulatorConfiguration.valueOf(attribute.toString())
                                                        .getChangeBy();
            random = new Random().nextDouble();
            result = minValue + (random * (maxValue - minValue));
        } else if (valueType == GeneratedValueType.DECREASING) {
            if (minValue > minValue - RfnMeterSimulatorConfiguration.valueOf(attribute.toString())
                                                            .getChangeBy()) {
                minValue = RfnMeterSimulatorConfiguration.getRfnMeterValueGenerator(attribute)
                                                 .getMinValue();
            }
            maxValue = minValue - RfnMeterSimulatorConfiguration.valueOf(attribute.toString())
                                                        .getChangeBy();
            random = new Random().nextDouble();
            result = minValue + (random * (maxValue - minValue));
        } else if (valueType == GeneratedValueType.FLUCTUATING) {
            random = new Random().nextDouble();
            result = RfnMeterSimulatorConfiguration.getRfnMeterValueGenerator(attribute).getMinValue() 
                    + (random * (RfnMeterSimulatorConfiguration.getRfnMeterValueGenerator(attribute).getMaxValue() 
                    - RfnMeterSimulatorConfiguration.getRfnMeterValueGenerator(attribute).getMinValue()));
        }
        return Math.round(result * 100.0) / 100.0;
    }

    /**
     * Class to hold the timestamp and value
     */
    public class TimestampValue {
        DateTime timestamp;
        Double value;

        public TimestampValue(DateTime timestamp, Double value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public DateTime getTimestamp() {
            return timestamp;
        }

        public Double getValue() {
            return value;
        }
    }
    
    
    @Override
    public void stopSimulator() {
        log.debug("Stopping RFN Meter Simulator");
        hourlyCounter = 0;
        perDayCounter = 0;
        minsCounter = 0;
        msgSimulatorFt.cancel(true);
        RfnMeterSimulatorStatus.reInitializeStatus(rfnMeterSimulatorStatus);
        if (!rfnMeterSimulatorStatus.getIsRunningRfnBillingType().get()) {
            // Flush the counter if both simulators are off
            perMinuteMsgCount.clear();
        }
    }
    
    /**
     * Sends generated message on queue
     */
    private <R extends RfnIdentifyingMessage> void sendArchiveRequest(String queueName,
            R archiveRequest) {
        log.debug("Sending archive request: " + archiveRequest.getRfnIdentifier()
                                                              .getCombinedIdentifier() + " on queue " + queueName);
        jmsTemplate.convertAndSend(queueName, archiveRequest);
    }
}

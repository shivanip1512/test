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
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
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
    
    private final static int secondsPerYear = 31556926;
    private final static long epoch;
    private RfnMeterSimulatorStatus rfnMeterSimulatorStatus = new RfnMeterSimulatorStatus();
    
    private JmsTemplate jmsTemplate;
    
    static {
        epoch = (DateTimeFormat.forPattern("MM/dd/yyyy").withZoneUTC().parseMillis("1/1/2005"))/1000;
    }
    
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

            if (storedTimestampValue != null
                && RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).valueType != GeneratedValueType.INCREASING) {
                if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getIntervalSeconds() == DateTimeConstants.SECONDS_PER_HOUR) {
                    if (storedTimestampValue.getTimestamp().isAfter((DateTime.now().minusHours(1)))) {
                        return storedTimestampValue;
                    }
                } else if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getIntervalSeconds() == DateTimeConstants.SECONDS_PER_DAY) {
                    if (storedTimestampValue.getTimestamp().isAfter((DateTime.now().minusDays(1)))) {
                        return storedTimestampValue;
                    }
                    generateValue = generateValue(device, attribute, storedTimestampValue.getValue());
                } else {
                    generateValue = generateValue(device, attribute, storedTimestampValue.getValue());
                }
            } else {
                generateValue = generateValue(device, attribute, 0.0);
            }
            TimestampValue timestampValue = new TimestampValue(DateTime.now(), generateValue);
            Map<Attribute, TimestampValue> obj = storedValue.get(device);
            obj.put(attribute, timestampValue);
            return timestampValue;

        } else {
            // If device does not exists, add in map
            generateValue = generateValue(device, attribute, 0.0);
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
    private Double generateValue(RfnDevice device, Attribute attribute, Double lastValue) {

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

            long intervalSeconds = RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getIntervalSeconds();
            long nowSeconds = Instant.now().getMillis() / 1000;

            if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute() == BuiltInAttribute.USAGE
                || RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute() == BuiltInAttribute.DEMAND) {
                long beginningOfLastInterval = nowSeconds - (nowSeconds % intervalSeconds) - intervalSeconds;
                result = getHectoWattHours(device.getPaoIdentifier().getPaoId(), beginningOfLastInterval);
                return result;
            } else {
                maxValue = minValue + RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getChangeBy();
                random = new Random().nextDouble();
                result = minValue + (random * (maxValue - minValue));
            }

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

    private double getHectoWattHours(int address, long CurrentTimeInSeconds) {
        long duration = CurrentTimeInSeconds - epoch;
        double consumptionWs = makeValueConsumption(address, epoch, duration);
        double consumptionWh = consumptionWs / DateTimeConstants.SECONDS_PER_HOUR;

        /*
         * Introduced an offset value to be added to the value of the curve
         * based on the value of the address passed in to the function. This
         * offset allows for a range of 0-7350 to be added to the kWh reading
         * that is returned to the device, thus providing a better way of
         * distinguishing meter readings whose addresses are similar or near
         * each other in value. Meters whose addresses are within one of each
         * other will now have a 7.35 kWh difference between them for easier
         * distinguishing.
         */
        double offset = (address % 1000) * 7350;

        double reading = consumptionWh + (offset * getConsumptionMultiplier(address));

        // Mod the hWh by 10000000 to reduce the range from 0 to 9999999,
        // since the MCT Device reads hWh this corresponds to 999,999.9 kWh
        // which is the desired changeover point.
        return (reading / 100) % 10000000;
    }

    /**
     * The consumption value is constructed using the current time and meter address.
     */
    private double makeValueConsumption(int address, long consumptionTimeInSeconds, long duration) {
        if (duration == 0)
            return 0;

        double consumptionMultiplier = getConsumptionMultiplier(address);

        long beginSeconds = consumptionTimeInSeconds;
        long endSeconds = consumptionTimeInSeconds + duration;

        double yearPeriod = 2.0 * Math.PI / secondsPerYear;
        double dayPeriod = 2.0 * Math.PI / DateTimeConstants.SECONDS_PER_DAY;

        double yearPeriodReciprocal = 1.0 / yearPeriod;
        double dayPeriodReciprocal = 1.0 / dayPeriod;

        /*
         * These multipliers affect the amplification of the curve for the
         * consumption. amp year being set at 700.0 gives a normalized
         * curve for the meter which results in a 1x Consumption Multiplier
         * calculating to about 775-875 kWh per month, as desired.
         */

        double ampYear = 700.0;
        double ampDay = 500.0;

        return (ampYear
            * (duration + yearPeriodReciprocal
                * (Math.sin(beginSeconds * yearPeriod) - Math.sin(endSeconds * yearPeriod))) + ampDay
            * (duration + dayPeriodReciprocal * (Math.sin(beginSeconds * dayPeriod) - Math.sin(endSeconds * dayPeriod))))
            * consumptionMultiplier;
    }

    /**
     * This section of if-statements is used to determine the multiplier of the consumption used by a
     * household based on the address of the meter. The following scale shows how these multipliers are
     * determined:
     * Address % 1000:
     * Range 000-399: 1x Consumption Multiplier - 40% of Households
     * Range 400-599: 2x Consumption Multiplier - 20% of Households
     * Range 600-799: 3x Consumption Multiplier - 20% of Households
     * Range 800-949: 5x Consumption Multiplier - 15% of Households
     * Range 950-994: 10x Consumption Multiplier - 4.5% of Households
     * Range 995-999: 20x Consumption Multiplier - 0.5% of Households
     * This scale was made in order to represent more real-world readings and model the fact that some
     * households consume drastically more than other households do.
     */
    private double getConsumptionMultiplier(int address) {
        int addressRange = address % 1000;

        if (addressRange < 400) {
            return 1.0;
        } else if (addressRange < 600) {
            return 2.0;
        } else if (addressRange < 800) {
            return 3.0;
        } else if (addressRange < 950) {
            return 5.0;
        } else if (addressRange < 995) {
            return 10.0;
        } else {
            return 20.0;
        }
    }

}

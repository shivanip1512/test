package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.DatedChannelData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper;
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
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.service.RfnMeterDataSimulatorService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class RfnMeterDataSimulatorServiceImpl extends RfnDataSimulatorService implements RfnMeterDataSimulatorService {
    private final Logger log = YukonLogManager.getLogger(RfnMeterDataSimulatorServiceImpl.class);

    private static final String meterReadingArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";

    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private UnitOfMeasureToPointMapper unitOfMeasureToPointMapper;

    // minute of the day to send a request at/list of devices to send a read request to
    private SetMultimap<Integer, RfnDevice> meters = HashMultimap.create();
    private RfnDataSimulatorStatus status = new RfnDataSimulatorStatus();
    private Multimap<PaoType, PointMapper> pointMappers;

    private Map<RfnDevice, Map<Attribute, TimestampValue>> storedValue = new HashMap<>();
    private final static int secondsPerYear = 31556926;
    private final static long epoch =
        (DateTimeFormat.forPattern("MM/dd/yyyy").withZoneUTC().parseMillis("1/1/2005")) / 1000;
    
    @Override
    @PostConstruct
    public void initialize() {
        super.initialize();
        pointMappers = unitOfMeasureToPointMapper.getPointMapper();
    }
    
    @Override
    public synchronized void startSimulator(SimulatorSettings settings) {
        if (!status.isRunning().get()) {
            status = new RfnDataSimulatorStatus();
            status.setRunning(new AtomicBoolean(true));
            status.setStartTime(new Instant());
            if (this.settings == null) {
                this.settings = settings;
            }
            List<RfnDevice> devices = new ArrayList<RfnDevice>();
            try {
                PaoType paoType = PaoType.valueOf(settings.getPaoType());
                devices.addAll(rfnDeviceDao.getDevicesByPaoType(paoType));
            } catch (Exception e) {
                // user selected all rfn types;
                devices.addAll(rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfMeterTypes()));
            }

            for (RfnDevice device : devices) {
                try {
                    int minuteOfTheDay = getMinuteOfTheDay(device.getRfnIdentifier().getSensorSerialNumber());
                    meters.put(minuteOfTheDay, device);
                } catch (NumberFormatException e) {
                    // serial number is not an integer, skip
                }
            }

            // check if execution was canceled
            if (!meters.isEmpty() && status.isRunning().get()) {
                logDebugInjectionTime();
                schedule();
            } else {
                devices.clear();
            }
        }
    }
    
    @Override
    public void stopSimulator() {
        log.debug("Stopping RFN Meter Simulator");
        status.setStopTime(new Instant());
        status.setRunning(new AtomicBoolean(false));
        meters.clear();  
        settings = null;
    }

    @Override
    protected void execute(int minuteOfTheDay) {
        if (status.isRunning().get()) {
            Set<RfnDevice> devices = meters.get(minuteOfTheDay);
            if (!devices.isEmpty()) {
                log.debug("Sending read request to " + devices.size() + " meters.");
                status.setLastInjectionTime(new Instant());
            }
            for (RfnDevice meter : devices) {
                try {
                    List<RfnMeterReadingArchiveRequest> meterReadingData =
                        generateMeterReadingData(meter, DateTime.now(), 24);
                    for (RfnMeterReadingArchiveRequest meterArchiveRequest : meterReadingData) {
                        sendArchiveRequest(meterArchiveRequest);
                        if (needsDuplicate()) {
                            log.debug("Sending duplicate read request for " + meter.getRfnIdentifier());
                            sendArchiveRequest(meterArchiveRequest);
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                    status.getFailure().incrementAndGet();
                }
            }
        }
    }

    @Override
    public SimulatorSettings getCurrentSettings() {
        return settings;
    }

    @Override
    public RfnDataSimulatorStatus getStatus() {
        return status;
    }

    private List<RfnMeterReadingArchiveRequest> generateMeterReadingData(RfnDevice device, DateTime time,
            int reportingIntervalHours) {

        List<RfnMeterReadingArchiveRequest> resultList = Lists.newArrayList();

        // Do Billing reading type if we have not reported since midnight today
        
        // NOTE the real device does midnight but does not follow DST, so this is compensating by
        // getting midnight in local time, then removing DST compensation by finding the active offset
        // and the normal offset and doing some awesome math.
        DateTime todaysDeviceBillingGenerationTime = time.withTimeAtStartOfDay()
                                                         .plusMillis(time.getZone().getOffset(time.withTimeAtStartOfDay()))
                                                         .minusMillis (time.getZone().getStandardOffset(time.withTimeAtStartOfDay().getMillis()));
        
        if (time.minusHours(reportingIntervalHours).isBefore(todaysDeviceBillingGenerationTime)) {
            List<BuiltInAttribute> attributeToGenerateMessage =
                RfnMeterSimulatorConfiguration.getValuesByMeterReadingType(RfnMeterReadingType.BILLING);

            RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
            message.setReadingType(RfnMeterReadingType.BILLING);
            message.setData(createReadingForType(device, attributeToGenerateMessage, todaysDeviceBillingGenerationTime));
            message.setDataPointId(1);
            resultList.add(message);
        }

        List<BuiltInAttribute> attributeToGenerateMessage =
            RfnMeterSimulatorConfiguration.getValuesByMeterReadingType(RfnMeterReadingType.INTERVAL);

        // Example with 4 hour reporting interval: 6:45 (-:45) -> 6 (-4 +1 ) -> 3 ---- Generate time for 3, 4, 5, 6
        DateTime intervalTime = time.withTime(time.getHourOfDay(), 0, 0, 0).minusHours(reportingIntervalHours - 1);

        while (intervalTime.isBefore(time) || intervalTime.isEqual(time)) {

            RfnMeterReadingArchiveRequest message = new RfnMeterReadingArchiveRequest();
            message.setReadingType(RfnMeterReadingType.INTERVAL);
            message.setData(createReadingForType(device, attributeToGenerateMessage, intervalTime));
            message.setDataPointId(1);
            resultList.add(message);

            intervalTime = intervalTime.plusHours(1);
        }

        return resultList;
    }
    
    private RfnMeterReadingData createReadingForType(RfnDevice device,
            List<BuiltInAttribute> attributeToGenerateMessage, DateTime time) {
        RfnMeterReadingData data = new RfnMeterReadingData();
        data.setTimeStamp(time.getMillis());
        data.setRfnIdentifier(device.getRfnIdentifier());
        data.setRecordInterval(300);

        List<ChannelData> channelDataList = Lists.newArrayList();
        List<DatedChannelData> datedChannelDataList = Lists.newArrayList();
        int channelNo = 1;
        for (PointMapper pointMapper : pointMappers.get(device.getPaoIdentifier().getPaoType())) {
            ChannelData channelData = new ChannelData();
            String pointName = pointMapper.getName();
            Attribute attribute = getAttributeForPoint(device.getPaoIdentifier().getPaoType(), pointName);
            if (attributeToGenerateMessage.contains(attribute)) {
                if (attribute != null && RfnMeterSimulatorConfiguration.attributeExists(attribute.toString())) {
                    TimestampValue timestampValue = getValueAndTimestampForPoint(device, attribute, time);
                    channelData.setChannelNumber(channelNo);
                    channelNo++;
                    channelData.setStatus(ChannelDataStatus.OK);
                    channelData.setUnitOfMeasure(pointMapper.getUom());
                    channelData.setValue(timestampValue.getValue());

                    Set<String> modifiers = null;
                    for (ModifiersMatcher match : pointMapper.getModifiersMatchers()) {
                        modifiers = match.getModifiers();
                    }
                    channelData.setUnitOfMeasureModifiers(modifiers);

                    if (!RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).isDated()) {
                        channelDataList.add(channelData);
                    } else {
                        DatedChannelData datedChannelData = new DatedChannelData();
                        datedChannelData.setBaseChannelData(channelData);
                        datedChannelData.setTimeStamp(timestampValue.getTimestamp().getMillis());
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
     * Find the attribute associated with a point
     */
    private Attribute getAttributeForPoint(PaoType paoType, String pointName) {
        Map<Attribute, AttributeDefinition> attrDefMap =
            paoDefinitionDao.getPaoAttributeAttrDefinitionMap().get(paoType);
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
    private TimestampValue getValueAndTimestampForPoint(RfnDevice device, Attribute attribute, DateTime time) {

        Map<Attribute, TimestampValue> storedDevice = storedValue.get(device);
        TimestampValue storedTimestampValue = null;
        Double generateValue = 0.0;
        
        if (storedDevice != null) {
            storedTimestampValue = storedDevice.get(attribute);

            if (storedTimestampValue != null
                && RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).valueType != GeneratedValueType.INCREASING) {
                if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getIntervalSeconds() == DateTimeConstants.SECONDS_PER_HOUR) {
                    if (storedTimestampValue.getTimestamp().isAfter(time.minusHours(1))) {
                        return storedTimestampValue;
                    }
                } else if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getIntervalSeconds() == DateTimeConstants.SECONDS_PER_DAY) {
                    if (storedTimestampValue.getTimestamp().isAfter(time.minusDays(1))) {
                        return storedTimestampValue;
                    }
                    generateValue = generateValue(device, attribute, storedTimestampValue.getValue(), time);
                } else {
                    generateValue = generateValue(device, attribute, storedTimestampValue.getValue(), time);
                }
            } else {
                generateValue = generateValue(device, attribute, 0.0, time);
            }
            TimestampValue timestampValue = new TimestampValue(time, generateValue);
            Map<Attribute, TimestampValue> obj = storedValue.get(device);
            obj.put(attribute, timestampValue);
            return timestampValue;

        } else {
            // If device does not exists, add in map
            generateValue = generateValue(device, attribute, 0.0, time);
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
    private Double generateValue(RfnDevice device, Attribute attribute, Double lastValue, DateTime time) {

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
            long timeSeconds = time.getMillis() / 1000;

            if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute() == BuiltInAttribute.USAGE) {
                return getWattHours(device.getPaoIdentifier().getPaoId(), timeSeconds);
            } else if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute() == BuiltInAttribute.DEMAND) {
                // I think this may still be wrong since intervalSeconds may not end up being on the "clock" hour exactly due to leap seconds. 
                long endOfLastInterval = timeSeconds - (timeSeconds % intervalSeconds);
                long beginningOfLastInterval = endOfLastInterval - intervalSeconds;
                // If I used 1 kWh in 15 minutes, I would use 4kWh over an hour and my "demand" is 4 kW so we multiply Usage*<seconds in hour>/<Seconds in interval> thus  1kWh*3600/900=4kW.
                result = getWattHours(device.getPaoIdentifier().getPaoId(), endOfLastInterval) - getWattHours(device.getPaoIdentifier().getPaoId(), beginningOfLastInterval) * (3600/intervalSeconds);
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

    /**
     * Sends generated message on queue
     */
    private <R extends RfnIdentifyingMessage> void sendArchiveRequest(R archiveRequest) {
        log.debug("Sending archive request: " + archiveRequest.getRfnIdentifier().getCombinedIdentifier() + " on queue "
            + meterReadingArchiveRequestQueueName );
        jmsTemplate.convertAndSend(meterReadingArchiveRequestQueueName , archiveRequest);
        status.getSuccess().incrementAndGet();
    }

    private double getWattHours(int address, long CurrentTimeInSeconds) {
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
        return (reading/10) % 1_000_000_000;
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
            * (duration
                + yearPeriodReciprocal * (Math.sin(beginSeconds * yearPeriod) - Math.sin(endSeconds * yearPeriod)))
            + ampDay * (duration
                + dayPeriodReciprocal * (Math.sin(beginSeconds * dayPeriod) - Math.sin(endSeconds * dayPeriod))))
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

    private void logDebugInjectionTime() {
        if (log.isDebugEnabled()) {
            for (Integer runAt : meters.keySet()) {
                DateTime now = new DateTime();
                int minuteNow = now.getMinuteOfDay();
                DateTime injectionTime = new DateTime().withTimeAtStartOfDay();
                if (runAt < minuteNow) {
                    injectionTime = injectionTime.plusDays(1);
                }
                injectionTime = injectionTime.plusMinutes(runAt);
                log.debug("Values for " + meters.get(runAt).size() + " devices will be injected at " + runAt
                    + " minute of the day (" + injectionTime.toString("MM/dd/YYYY HH:mm") + ")");
            }
        }
    }
}

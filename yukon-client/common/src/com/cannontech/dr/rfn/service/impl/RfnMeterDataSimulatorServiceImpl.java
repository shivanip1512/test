package com.cannontech.dr.rfn.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
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
import com.cannontech.amr.rfn.service.pointmapping.ModifiersMatcher;
import com.cannontech.amr.rfn.service.pointmapping.PointMapper;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.model.SimulatorSettings.ReportingInterval;
import com.cannontech.dr.rfn.service.RfnMeterDataSimulatorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class RfnMeterDataSimulatorServiceImpl extends RfnDataSimulatorService implements RfnMeterDataSimulatorService {

    private final Logger log = YukonLogManager.getLogger(RfnMeterDataSimulatorServiceImpl.class);

    private static final String meterReadingArchiveRequestQueueName = "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";

    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private UnitOfMeasureToPointMapper unitOfMeasureToPointMapper;
    @Autowired private AttributeService attributeService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;

    // minute of the day to send a request at/list of devices to send a read request to
    private final SetMultimap<Integer, RfnDevice> meters = HashMultimap.create();
    private RfnDataSimulatorStatus status = new RfnDataSimulatorStatus();
    private Multimap<PaoType, PointMapper> pointMappers;

    private final Map<RfnDevice, Map<Attribute, TimestampValue>> billingStoredValue = new HashMap<>();
    private final Map<RfnDevice, Map<Attribute, TimestampValue>> intervalStoredValue = new HashMap<>();

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
            log.debug("Start simulator");
            status = new RfnDataSimulatorStatus();
            status.setRunning(new AtomicBoolean(true));
            status.setStartTime(new Instant());
            saveSettings(settings);
            if (this.settings == null) {
                this.settings = settings;
            }
            List<RfnDevice> devices = new ArrayList<>();
            try {
                PaoType paoType = PaoType.valueOf((settings).getPaoType());
                devices.addAll(rfnDeviceDao.getDevicesByPaoType(paoType));
            } catch (Exception e) {
                // user selected all rfn types;
                devices.addAll(rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfMeterTypes()));
            }

            for (RfnDevice device : devices) {
                try {
                    int minuteOffset = getMinuteOfTheDay(device.getRfnIdentifier().getSensorSerialNumber());
                    ReportingInterval reportingInterval = (settings).getReportingInterval();

                    minuteOffset = minuteOffset / reportingInterval.getDailyIntervals();

                    while (minuteOffset < Days.ONE.toStandardMinutes().getMinutes()) {
                        meters.put(minuteOffset, device);
                        minuteOffset = minuteOffset + reportingInterval.getDuration().toStandardMinutes().getMinutes();
                    }
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
    public void startSimulatorWithCurrentSettings() {
        startSimulator(getCurrentSettings());
    }

    @Override
    public void testSimulator(SimulatorSettings settings) {
        log.debug("Testing simulator");
        generateAndSendArchiveRequest(rfnDeviceDao.getDeviceForId(settings.getDeviceId()));
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
                    generateAndSendArchiveRequest(meter);
                } catch (Exception e) {
                    log.error(e);
                    status.getFailure().incrementAndGet();
                }
            }
        }
    }

    private void generateAndSendArchiveRequest(RfnDevice meter){
        List<RfnMeterReadingArchiveRequest> meterReadingData = generateMeterReadingData(meter);
        log.debug("Sending requests: " + meterReadingData.size() + " on queue "
            + meterReadingArchiveRequestQueueName);
        for (RfnMeterReadingArchiveRequest meterArchiveRequest : meterReadingData) {
            sendArchiveRequest(meterArchiveRequest);
            if (needsDuplicate()) {
                log.debug("Sending duplicate read request for " + meter.getRfnIdentifier());
                sendArchiveRequest(meterArchiveRequest);
            }
        }
    }

    public void saveSettings(SimulatorSettings settings) {
        log.debug("Saving RFN_METER settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_METER_TYPE, settings.getPaoType());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE, settings.getPercentOfDuplicates());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_REPORTING_INTERVAL, settings.getReportingInterval());
    }
    
    @Override
    public SimulatorSettings getCurrentSettings() {
        if (settings == null) {
            log.debug("Getting RFN_METER SimulatorSettings from db.");
            SimulatorSettings simulatorSettings = new SimulatorSettings();
            simulatorSettings.setPaoType(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_METER_TYPE));
            simulatorSettings.setPercentOfDuplicates(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE));
            simulatorSettings.setReportingInterval(ReportingInterval.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_REPORTING_INTERVAL)));
            settings = simulatorSettings;
        }
        return settings;
    }

    @Override
    public RfnDataSimulatorStatus getStatus() {
        return status;
    }

    private List<RfnMeterReadingArchiveRequest> generateMeterReadingData(RfnDevice device) {

        DateTime now = DateTime.now();
        DateTime intervalTime = null;
        log.debug("Generating meter reading data for "+ device);

        List<RfnMeterReadingArchiveRequest> requests = Lists.newArrayList();

        // Do Billing reading type if we have not reported since midnight today

        // NOTE the real device does midnight but does not follow DST, so this is compensating by
        // getting midnight in local time, then removing DST compensation by finding the active offset
        // and the normal offset and doing some awesome math.
        DateTime currentDateTime = now.withTimeAtStartOfDay()
                .plusMillis(now.getZone().getOffset(now.withTimeAtStartOfDay()))
                .minusMillis (now.getZone().getStandardOffset(now.withTimeAtStartOfDay().getMillis()));
        DateTime dateTimeNowMinusInterval = null;
        
        if (settings.getReportingInterval()!=null){
            dateTimeNowMinusInterval = now.minusHours(settings.getReportingInterval().getDuration().toStandardHours().getHours());
        }else {
            dateTimeNowMinusInterval = now.minusHours(24);
        }
        if(currentDateTime.isEqualNow() || currentDateTime.isAfter(dateTimeNowMinusInterval) && currentDateTime.isBeforeNow()){
            // Billing data point to be generated only once in a day, no matter whatever is the reporting interval
            //If device midnight falls in the time range (now to now-interval) , generate billing data for that device
            createAndAddArchiveRequest(device, currentDateTime, RfnMeterReadingType.BILLING, now, requests);
        }
        
        final ReportingInterval reportingIntervalEnum =
                (settings == null ? ReportingInterval.REPORTING_INTERVAL_24_HOURS : settings.getReportingInterval());
        if (reportingIntervalEnum == ReportingInterval.REPORTING_INTERVAL_24_HOURS) {
            // generate only 1 data point for 1 day
            intervalTime = now.withTime(now.getHourOfDay(), 0, 0, 0);
            createAndAddArchiveRequest(device, intervalTime, RfnMeterReadingType.INTERVAL, now, requests);
        } else {
            // Generate data point as per the interval mentioned 4 hr / 1 hr
            // Example with 4 hour reporting interval: 6:45 (-:45) -> 6 (-4 +1 ) -> 3 -- Generate time for 3, 4, 5, 6
            intervalTime = now.withTime(now.getHourOfDay(), 0, 0, 0).minusHours(
                settings.getReportingInterval().getDuration().toStandardHours().getHours() - 1);
            while (intervalTime.isBefore(now) || intervalTime.isEqual(now)) {
                createAndAddArchiveRequest(device, intervalTime, RfnMeterReadingType.INTERVAL, now, requests);
                intervalTime = intervalTime.plusHours(1);
            }
        }
        return requests;
    }

    private void createAndAddArchiveRequest(RfnDevice device, DateTime time, RfnMeterReadingType type,
            DateTime currentTime, List<RfnMeterReadingArchiveRequest> requests) {
        log.debug("\n--Creating archive request for " + type + " device=" + device);
        RfnMeterReadingArchiveRequest archiveRequest = new RfnMeterReadingArchiveRequest();
        archiveRequest.setReadingType(type);
        RfnMeterReadingData readingData = createReadingForType(device, time, type, currentTime);
        archiveRequest.setData(readingData);
        archiveRequest.setDataPointId(1);
        log.debug("--archiveRequest= " + archiveRequest + " device=" + device);
        requests.add(archiveRequest);
    }

    private PointMapper getMapperByName(String templateName, PaoType paoType) {
        for (PointMapper mapper : pointMappers.get(paoType)) {
            if (mapper.getName().equals(templateName)) {
                return mapper;
            }
        }
        return null;
    }

    @Override
    public RfnMeterReadingData createReadingForType(RfnDevice device, DateTime time, RfnMeterReadingType type,
            DateTime currentTime) {

        PaoType paoType = device.getPaoIdentifier().getPaoType();

        List<ChannelData> channelDataList = Lists.newArrayList();
        List<DatedChannelData> datedChannelDataList = Lists.newArrayList();

        Set<Attribute> simulatedAttributes = RfnMeterSimulatorConfiguration.getValuesByMeterReadingType(type);
        Set<Attribute> availableAttributes = attributeService.getAvailableAttributes(device);
        
        // contains all elements of 'availableAttributes' that also belong to 'simulatedAttributes' but no other attributes
        SetView<Attribute> intersection = Sets.intersection(simulatedAttributes, availableAttributes);
        log.debug("---creating reading for the following attributes= "+intersection);
        
        Map<Attribute, AttributeDefinition> attrDefMap =
            paoDefinitionDao.getPaoAttributeAttrDefinitionMap().get(paoType);

        int channelNo = 1;
        for (Attribute attribute : intersection) {
            AttributeDefinition definition = attrDefMap.get(attribute);
            String pointName = definition.getPointTemplate().getName();
            PointMapper mapper = getMapperByName(pointName, paoType);
            if (mapper != null) {
                ChannelData channelData = new ChannelData();
                TimestampValue timestampValue =
                    getValueAndTimestampForPoint(device, attribute, time, type, currentTime);
                channelData.setChannelNumber(channelNo);
                channelNo++;
                channelData.setStatus(ChannelDataStatus.OK);
                channelData.setUnitOfMeasure(mapper.getUom());
                channelData.setValue(timestampValue.getValue());
                
                Set<String> modifiers = null;
                for (ModifiersMatcher match : mapper.getModifiersMatchers()) {
                    modifiers = match.getModifiers();
                }
                channelData.setUnitOfMeasureModifiers(modifiers);

                if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).isDated()) {
                    DatedChannelData datedChannelData = new DatedChannelData();
                    datedChannelData.setBaseChannelData(channelData);
                    datedChannelData.setTimeStamp(timestampValue.getTimestamp().getMillis());
                    datedChannelDataList.add(datedChannelData);
                    log.debug("----pointName='"+pointName+"' datedChannelData= "+datedChannelData);
                } else {
                    channelDataList.add(channelData);
                    log.debug("----pointName='"+pointName+"' channelData= "+channelData);
                }
            }
        }
        RfnMeterReadingData data = new RfnMeterReadingData();
        data.setTimeStamp(time.getMillis());
        data.setRfnIdentifier(device.getRfnIdentifier());
        data.setRecordInterval(3600);
        data.setChannelDataList(channelDataList);
        data.setDatedChannelDataList(datedChannelDataList);
        log.debug("---readingData= "+data);
        return data;
    }

    /**
     *  Generate Value and Timestamp for each point. This will generate the same value for a point based on if its hourly
     * or daily.
     */
    private TimestampValue getValueAndTimestampForPoint(RfnDevice device, Attribute attribute, DateTime time,
            RfnMeterReadingType type, DateTime currentTime) {
        Map<Attribute, TimestampValue> storedDevice;
        Map<Attribute, TimestampValue> updateStoredDevice;
        // Create separate map for generating all 24 message for interval metering type.
        if (type == RfnMeterReadingType.BILLING) {
            storedDevice = billingStoredValue.get(device);
        } else {
            storedDevice = intervalStoredValue.get(device);
        }

        TimestampValue storedTimestampValue = null;
        Double generateValue = 0.0;
        if (storedDevice != null) {
            storedTimestampValue = storedDevice.get(attribute);

            if (storedTimestampValue != null) {
                if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).generationType == GenerationType.HOURLY) {
                    if (storedTimestampValue.getTimestamp().isAfter(time.minusHours(1))) {
                        return storedTimestampValue;
                    }
                } else if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).generationType == GenerationType.DAILY) {
                    if (storedTimestampValue.getTimestamp().isAfter(time.minusDays(1))) {
                        return storedTimestampValue;
                    }

                }
            }

            generateValue = generateValue(device, attribute, time, currentTime);
            TimestampValue timestampValue = new TimestampValue(time, generateValue);
            if (type == RfnMeterReadingType.BILLING) {
                updateStoredDevice = billingStoredValue.get(device);
            } else {
                updateStoredDevice = intervalStoredValue.get(device);
            }
            updateStoredDevice.put(attribute, timestampValue);
            return timestampValue;

        } else {
            // If device does not exists, add in map
            generateValue = generateValue(device, attribute, time, currentTime);
            TimestampValue timestampValue = new TimestampValue(time, generateValue);
            Map<Attribute, TimestampValue> timestampValueMap = new HashMap<>();
            timestampValueMap.put(attribute, timestampValue);

            if (type == RfnMeterReadingType.BILLING) {
                billingStoredValue.put(device, timestampValueMap);
            } else {
                intervalStoredValue.put(device, timestampValueMap);
            }
            return timestampValue;
        }
    }


    /**
     * Generate value based on time
     */
    private Double generateValue(RfnDevice device, Attribute attribute, DateTime time,
            DateTime currentTime) {

        double result = 0.0;

        long timeSeconds = time.getMillis() / 1000;

        if (RfnMeterSimulatorConfiguration.getUsageTypes().contains(
            RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute())) {
            // Increasing WH value hourly
            result = getWattHours(device.getPaoIdentifier().getPaoId(), timeSeconds);
        } else if (RfnMeterSimulatorConfiguration.getDemandTypes().contains(
            RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute())) {

            if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute() == BuiltInAttribute.DEMAND) {
                // Here getting hourly demand value based on the time stamp
                result =
                    getDemand(device.getPaoIdentifier().getPaoId(), DateTimeConstants.SECONDS_PER_HOUR, timeSeconds);
            } else if (RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute() == BuiltInAttribute.PEAK_DEMAND) {

                result =
                    getPeakDemand(device.getPaoIdentifier().getPaoId(), DateTimeConstants.SECONDS_PER_HOUR, currentTime);
            }
           
        } else if (RfnMeterSimulatorConfiguration.getRateDemandTypes().contains(
            RfnMeterSimulatorConfiguration.valueOf(attribute.toString()).getAttribute())) {
            // TODO for RATE DEMAND TYPES
        } else {
            result = getWattHours(device.getPaoIdentifier().getPaoId(), timeSeconds) / 4;
        }

        return roundToOneDecimalPlace(result);
    }

    /*
     * Data from the real device returns value rounded to one decimal place.
     * Examples:
     * 1.9991 -> 1.9
     * 1.1294 -> 1.1
     * 1.2097 -> 1.2
     * 1.2997 -> 1.2
     */
    private Double roundToOneDecimalPlace(Double value){
        return new BigDecimal(value).setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
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
        jmsTemplate.convertAndSend(meterReadingArchiveRequestQueueName , archiveRequest);
        status.getSuccess().incrementAndGet();
    }

    /**
     * Calculating demand (W) value based on address , demand interval and time
     */
    public double getDemand(int address, long demandIntervalSeconds, long nowSeconds) {

        long endOfLastInterval = nowSeconds - nowSeconds % demandIntervalSeconds;
        long beginningOfLastInterval = endOfLastInterval - demandIntervalSeconds;

        double demandHwhBegin = getWattHours(address, beginningOfLastInterval);
        double demandHwhEnd = getWattHours(address, endOfLastInterval);

        double diffWh = (demandHwhEnd - demandHwhBegin) * (DateTimeConstants.SECONDS_PER_HOUR / demandIntervalSeconds);
        return diffWh;

    }

    /**
     * Calculating peak demand (W) based on address, demand interval, current time
     * 
     * calculating peak demand if month day is more than 15 then considering freeze time
     * 15th date of current month. if month day is less than 15 considering freeze time 15th date of previous
     * month
     */

    public double getPeakDemand(int address, long demandInterval, DateTime currentTime) {

     
        long freezeTimestamp = 0;
        long currentTimeSeconds = currentTime.getMillis() / 1000;
        double maxIntervalConsumption = 0.0;
        long maxIntervalTimestamp = 0;

        if (currentTime.getDayOfMonth() <= 15) {
            freezeTimestamp = currentTime.withTime(1, 0, 0, 0).withDayOfMonth(15).minusMonths(1).getMillis() / 1000;
        } else {
            freezeTimestamp = currentTime.withTime(1, 0, 0, 0).withDayOfMonth(15).getMillis() / 1000;
        }

        // Align it to its following interval.
        long firstIntervalStart = freezeTimestamp - (freezeTimestamp % demandInterval) + demandInterval;

        long intervalBegin = firstIntervalStart;

        while ((intervalBegin + demandInterval) < currentTimeSeconds) {
            // Calculate the consumption from the start point of the interval.
            double intervalConsumption = makeValueConsumption(address, intervalBegin, demandInterval);

            if (intervalConsumption > maxIntervalConsumption) {
                maxIntervalConsumption = intervalConsumption;

                // Timestamp for the peak is the end of the interval.
                maxIntervalTimestamp = intervalBegin + demandInterval;
            }

            intervalBegin = intervalBegin + demandInterval;
        }

        double peakDemand = getDemand(address, demandInterval, maxIntervalTimestamp);

        return peakDemand;
    }

    /**
     * Calculating watt hour (WH) based on address and time
     */

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
        return (reading / 10) % 1_000_000_000;
    }

    /**
     * The consumption value is constructed using the current time and meter address.
     */
    private double makeValueConsumption(int address, long consumptionTimeInSeconds, long duration) {
        if (duration == 0) {
            return 0;
        }

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

package com.cannontech.dr.rfn.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.DatedChannelData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadDataReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadRequest;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingData;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper.ModifiersMatcher;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper.PointMapper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;
import com.cannontech.dr.rfn.service.RfnMeterReadAndControlSimulatorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class RfnMeterReadAndControlSimulatorServiceImpl implements RfnMeterReadAndControlSimulatorService {

    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadAndControlSimulatorServiceImpl.class);
    // private static final String meterReadRequestQueue = "yukon.qr.obj.amr.rfn.MeterReadRequest";
    private static final String meterReadRequestQueue = "yukon.qr.obj.amr.rfn.MeterReadRequest";
    private static final String meterDisconnectRequestQueue = "yukon.qr.obj.amr.rfn.MeterDisconnectRequest";
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private RfnDeviceDao dao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private AttributeService attributeService;
    @Autowired private UnitOfMeasureToPointMapper unitOfMeasureToPointMapper;
    private RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings;
    private RfnMeterReadAndControlReadSimulatorSettings readSettings;
    private Multimap<PaoType, PointMapper> pointMappers;
    private final Map<RfnDevice, Map<Attribute, TimestampValue>> billingStoredValue = new HashMap<>();
    private final Map<RfnDevice, Map<Attribute, TimestampValue>> intervalStoredValue = new HashMap<>();
    private final static int secondsPerYear = 31556926;
    private final static long epoch = (DateTimeFormat.forPattern("MM/dd/yyyy").withZoneUTC().parseMillis("1/1/2005")) / 1000;
    
    private volatile boolean meterReadReplyActive;
    private volatile boolean meterReadReplyStopping;
    
    private volatile boolean meterDisconnectReplyActive;
    private volatile boolean meterDisconnectReplyStopping;
    
    private static final int incomingMessageWaitMillis = 1000;
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
        pointMappers = unitOfMeasureToPointMapper.getPointMapper();
    }
    
    @Override
    public boolean startMeterReadReply(RfnMeterReadAndControlReadSimulatorSettings settings) {
        if (meterReadReplyActive) {
            return false;
        } else {
            
            saveReadSettings(settings);
            readSettings = settings;
            
            Thread meterReadThread = getMeterReadThread(settings);
            meterReadThread.start();
            
            meterReadReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopMeterReadReply() {
        if (meterReadReplyActive) {
            meterReadReplyStopping = true;
            readSettings = null;
        }
    }
    
    @Override
    public boolean startMeterDisconnectReply(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        
        if (meterDisconnectReplyActive) {
            return false;
        } else {
            saveDisconnectSettings(settings);
            disconnectSettings = settings;
            
            Thread meterDisconnectThread = getMeterDisconnectThread(settings);
            meterDisconnectThread.start();
            
            meterDisconnectReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopMeterDisconnectReply() {
        if (meterDisconnectReplyActive) {
            meterDisconnectReplyStopping = true;
            disconnectSettings = null;
        }
    }
    
    public void saveDisconnectSettings(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        log.debug("Saving RFN_METER_READ_CONTROL settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY1, settings.getDisconnectReply1());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY2, settings.getDisconnectReply2());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_1, settings.getDisconnectReply1FailPercent());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_2, settings.getDisconnectReply2FailPercent());
    }
    
    @Override
    public RfnMeterReadAndControlDisconnectSimulatorSettings getDisconnectSettings() {
        if (disconnectSettings == null) {
            log.debug("Getting RFN_METER_READ_CONTROL SimulatorSettings from db.");
            RfnMeterReadAndControlDisconnectSimulatorSettings simulatorSettings = new RfnMeterReadAndControlDisconnectSimulatorSettings();
            simulatorSettings.setDisconnectReply1(RfnMeterDisconnectInitialReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY1)));
            simulatorSettings.setDisconnectReply1FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_1));
            simulatorSettings.setDisconnectReply2(RfnMeterDisconnectConfirmationReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY2)));
            simulatorSettings.setDisconnectReply2FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_2));
            disconnectSettings = simulatorSettings;
        }
        return disconnectSettings;
    }
    
    public void saveReadSettings(RfnMeterReadAndControlReadSimulatorSettings settings) {
        log.debug("Saving RFN_METER_READ_CONTROL settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY1, settings.getReadReply1());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY2, settings.getReadReply2());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_1, settings.getReadReply1FailPercent());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_2, settings.getReadReply2FailPercent());
    }
    
    @Override
    public RfnMeterReadAndControlReadSimulatorSettings getReadSettings() {
        if (readSettings == null) {
            log.debug("Getting RFN_METER_READ_CONTROL SimulatorSettings from db.");
            RfnMeterReadAndControlReadSimulatorSettings simulatorSettings = new RfnMeterReadAndControlReadSimulatorSettings();
            simulatorSettings.setReadReply1(RfnMeterReadingReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY1)));
            simulatorSettings.setReadReply1FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_1));
            simulatorSettings.setReadReply2(RfnMeterReadingDataReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY2)));
            simulatorSettings.setReadReply2FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_2));
            readSettings = simulatorSettings;
        }
        return readSettings;
    }
    
    @Override
    public void startSimulatorWithCurrentSettings() {
        startMeterDisconnectReply(getDisconnectSettings());
        startMeterReadReply(getReadSettings());
    }

    /**
     * Generate a thread that monitors the meter read request queue, handles requests, and sends responses.
     */
    private Thread getMeterReadThread(RfnMeterReadAndControlReadSimulatorSettings settings) {
        Thread meterReadRunner = new Thread() {
            @Override
            public void run() {
                
                while (!meterReadReplyStopping) {
                    try {
                        
                        Object message = jmsTemplate.receive(meterReadRequestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnMeterReadRequest request = (RfnMeterReadRequest) requestMessage.getObject();
                            
                            RfnMeterReadReply response1 = setUpReadInitialResponse(settings);
                            
                            RfnMeterReadDataReply response2 = setUpReadDataResponse(request, settings);
                            
                            //System.out.println(response2.getData());
                            
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response1);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response2);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in meter read reply.", e);
                    }
                }
                
                log.info("Meter read thread shutting down.");
                meterReadReplyStopping = false;
                meterReadReplyActive = false;
            }
            
            
        };
        return meterReadRunner;
    }
    
    private RfnMeterReadReply setUpReadInitialResponse(RfnMeterReadAndControlReadSimulatorSettings settings) {
        RfnMeterReadReply response = new RfnMeterReadReply();
        
        // Calculates Fail Rates for read
        if(replyWithFailure(settings.getReadReply1FailPercent())) {
            response.setReplyType(settings.getReadReply1());
        } else {
            response.setReplyType(RfnMeterReadingReplyType.OK);
        }
        
        return response;
    }
    
    private RfnMeterReadDataReply setUpReadDataResponse(RfnMeterReadRequest request, RfnMeterReadAndControlReadSimulatorSettings settings) {
        RfnMeterReadDataReply response = new RfnMeterReadDataReply();
        
        // Calculates Fail Rates for disconnect and connect
        if(replyWithFailure(settings.getReadReply2FailPercent())) {
            response.setReplyType(settings.getReadReply2());
        } else {
            response.setReplyType(RfnMeterReadingDataReplyType.OK);
            //LiteYukonPAObject litePao = cache.getAllPaosMap().get();
            //RfnDevice device = new RfnDevice(null, cache.get  .getAllPaosMap().get(request.getRfnIdentifier()), request.getRfnIdentifier());
            RfnDevice device = dao.getDeviceForExactIdentifier(request.getRfnIdentifier());
            // Simulates meter data
            // RfnDevice temp = new RfnDevice();
            
            //RfnMeterDataSimulatorServiceImpl dataCreator = new RfnMeterDataSimulatorServiceImpl();
            response.setData(createReadingForType(device, DateTime.now(), RfnMeterReadingType.INTERVAL, DateTime.now()));
            
        }
        
        return response;
    }
    
   private RfnMeterReadingData createReadingForType(RfnDevice device, DateTime time, RfnMeterReadingType type,
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
        String pointName;
        //try {
            pointName = definition.getPointTemplate().getName();
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
        //} catch (Exception e) {
        //    continue;
        //}
    }
    RfnMeterReadingData data = new RfnMeterReadingData();
    data.setTimeStamp(time.getMillis());
    data.setRfnIdentifier(device.getRfnIdentifier());
    data.setRecordInterval(300);
    data.setChannelDataList(channelDataList);
    data.setDatedChannelDataList(datedChannelDataList);
    log.debug("---readingData= "+data);
    return data;
    }
   
    private PointMapper getMapperByName(String templateName, PaoType paoType) {
        for (PointMapper mapper : pointMappers.get(paoType)) {
            if (mapper.getName().equals(templateName)) {
                return mapper;
            }
        }
        return null;
    }
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
    
    /**
     * Generate a thread that monitors the meter disconnect request queue, handles requests, and sends responses.
     */
    private Thread getMeterDisconnectThread(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        Thread meterDisconnectRunner = new Thread() {
            @Override
            public void run() {
                while (!meterDisconnectReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(meterDisconnectRequestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnMeterDisconnectRequest request = (RfnMeterDisconnectRequest) requestMessage.getObject();
                            
                            RfnMeterDisconnectInitialReply response1 = setUpDisconnectInitialResponse(settings);
                            RfnMeterDisconnectConfirmationReply response2 = setUpDisconnectConfirmationResponse(request, settings);
                            
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response1);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response2);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in meter disconnect reply.", e);
                    }
                }
                
                log.info("Meter dissconnect thread shutting down.");
                meterDisconnectReplyStopping = false;
                meterDisconnectReplyActive = false;
            }
            
            
        };
        return meterDisconnectRunner;
    }
    
    private RfnMeterDisconnectInitialReply setUpDisconnectInitialResponse(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        RfnMeterDisconnectInitialReply response = new RfnMeterDisconnectInitialReply();
        
        // Calculates Fail Rates for disconnect and connect
        if(replyWithFailure(settings.getDisconnectReply1FailPercent())) {
            response.setReplyType(settings.getDisconnectReply1());
        } else {
            response.setReplyType(RfnMeterDisconnectInitialReplyType.OK);
        }
        
        return response;
    }
    
    private RfnMeterDisconnectConfirmationReply setUpDisconnectConfirmationResponse(RfnMeterDisconnectRequest request, RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        RfnMeterDisconnectConfirmationReply response = new RfnMeterDisconnectConfirmationReply();
        
        // Calculates Fail Rates for disconnect and connect
        if(replyWithFailure(settings.getDisconnectReply2FailPercent())) {
            response.setReplyType(settings.getDisconnectReply2());
        } else {
            response.setReplyType(RfnMeterDisconnectConfirmationReplyType.SUCCESS);
        }
        
        // Echo back of whatever user provides
        response.setState(RfnMeterDisconnectState.getForType(request.getAction()));
        
        return response;
    }
    
    /**
     * This method is used to calculate the chance of a request failing with the inputed failure message instead of succeeding.
     * @param failureRate Integer that is the inputed failure rate.
     * @return True if the reply should take the user inputed failure, and false otherwise.
     */
    private boolean replyWithFailure(int failureRate) {
        Random r = new Random();
        int n = r.nextInt(100) + 1; // Generates a random number between 1 and 100
        return n <= failureRate;    // If the number is over the fail rate, then the test succeeds
    }
    
    @Override
    public boolean isMeterReadReplyActive() {
        return meterReadReplyActive;
    }
    
    @Override
    public boolean isMeterDisconnectReplyActive() {
        return meterDisconnectReplyActive;
    }
    
    @Override
    public boolean isMeterReadReplyStopping() {
        return meterReadReplyStopping;
    }
    
    @Override
    public boolean isMeterDisconnectReplyStopping() {
        return meterDisconnectReplyStopping;
    }
}

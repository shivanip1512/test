package com.cannontech.dr.rfn.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.ByteUtil;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.model.RfnLcr6700RelayMap;
import com.cannontech.dr.rfn.model.RfnLcrTlvPointDataType;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RfnLcrTlvDataMappingServiceImpl extends RfnLcrDataMappingServiceImpl<ListMultimap<FieldType, byte[]>> {
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private ExpressComReportedAddressDao expressComReportedAddressDao;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;

    private static final Logger log = YukonLogManager.getLogger(RfnLcrTlvDataMappingServiceImpl.class);
    private static final DateTime year2001 = new DateTime(2001, 1, 1, 0, 0);
    private static final int RECORDING_INTERVAL = 60;

    @Override
    public List<PointData> mapPointData(RfnLcrReadingArchiveRequest request, ListMultimap<FieldType, byte[]>  data) {

        Long timeInSec = ByteUtil.getLong(data.get(FieldType.UTC).get(0));
        Instant timeOfReading = new Instant(timeInSec * 1000);

        RfnDevice device = rfnDeviceLookupService.getDevice(request.getRfnIdentifier());

        // Object to store communication and non-zero runtime times for insertion into
        // DynamicLcrCommunications.
        AssetAvailabilityPointDataTimes assetAvailabilityTimes = new AssetAvailabilityPointDataTimes(device.getPaoIdentifier().getPaoId());
        /*
         * This timestamp is created by the LCR when the report is generated.
         * The message is then held by the node until its reporting interval arrives.
         * This could be improved by getting the timestamp from the gateway if/when Yukon
         * and Network Manager share that information.
         */
        assetAvailabilityTimes.setLastCommunicationTime(timeOfReading);

        // Generate point data from raw TLV data. Create missing points if necessary.
        List<PointData> messagesToSend = generatePointData(device, data, timeOfReading);

        /**
         * This may be useful some day
         * boolean readNow = (data.evaluateAsInt("/DRReport/Info/Flags") & 0x01) == 1;
         */
        
        List<PointData> intervalData = mapIntervalData(data, device, assetAvailabilityTimes);
        if (intervalData != null) {
            messagesToSend.addAll(intervalData);
        }

        // Update asset availability times for this device
        dynamicLcrCommunicationsDao.insertData(assetAvailabilityTimes);

        return messagesToSend;
    }

    private List<PointData> generatePointData(RfnDevice device, ListMultimap<FieldType, byte[]> data, Instant timeOfReading) {
        Set<RfnLcrTlvPointDataType> rfnLcrPointDataMap = RfnLcrTlvPointDataType.getPointDataTypesByPaoType(device.getPaoIdentifier().getPaoType());
        
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
        for (RfnLcrTlvPointDataType entry : rfnLcrPointDataMap) {

            Double value = evaluateArchiveReadValue(data, entry);
            
            if (value != null) {
                
                // Check if the device supports this attribute
                if (!attributeService.isAttributeSupported(device, entry.getAttribute())) {
                    log.warn("The attribute: " + entry.getAttribute().toString() + " is not defined for the device: "
                            + device.getName() + " of type: " + device.getPaoIdentifier().getPaoType());
                    continue;
                }
                
                // If PQR is enabled, and it's PQR data, and the point is missing, create the point automatically
                boolean isPqrEnabled = configurationSource.getBoolean(MasterConfigBoolean.ENABLE_POWER_QUALITY_RESPONSE);
                if (isPqrEnabled && entry.isPowerQualityResponse()) {
                    boolean pointExists = attributeService.pointExistsForAttribute(device, entry.getAttribute());
                    if (!pointExists) {
                        log.debug("Creating point for PQR attribute (" + entry.getAttribute() + ") on device: " 
                                 + device.getName() + ".");
                        attributeService.createPointForAttribute(device, entry.getAttribute());
                    }
                }
                
                // Generate the point data
                try {
                    PaoPointIdentifier paoPointIdentifier = attributeService.getPaoPointIdentifierForAttribute(device, entry.getAttribute());
                    LitePoint point = pointDao.getLitePoint(paoPointIdentifier);
                    
                    Double multiplier = point.getMultiplier();
                    if (multiplier != null) {
                        double modifiedValue = value * multiplier;
                        if (log.isDebugEnabled() && multiplier != 1.0) {
                            log.debug("Applying multiplier to point value. Device=" + device.getName() 
                                      + ". Original value=" + value + ". Multiplier=" + multiplier + "."
                                      + ". Modified value=" + modifiedValue);
                        }
                        value = modifiedValue;
                    }
                    
                    PointData pointData = getPointData(entry.getAttribute(), value, paoPointIdentifier, point.getPointID(), timeOfReading.toDate());
                    messagesToSend.add(pointData);
                } catch (NotFoundException e) {
                    log.debug("Point for attribute (" + entry.getAttribute().toString() + ") does not exist for device: "
                            + device.getName());
                    continue;
                }
            }
        }
        return messagesToSend;
    }
    
    private Double evaluateArchiveReadValue(ListMultimap<FieldType, byte[]> data, RfnLcrTlvPointDataType entry) {

        Number value = null;
        if (entry.getFieldType() == FieldType.RELAY_N_REMAINING_CONTROLTIME) {
            // first byte indicates the relay number (0, 1, 2) but next 4 bytes represents Remaining Control Time
            if (CollectionUtils.isNotEmpty(data.get(entry.getFieldType()))) {
                value = data.get(entry.getFieldType()).stream()
                                                      .filter(relaynControlTime -> (ByteUtil.getInteger(relaynControlTime[0])) == entry.getRelayNum())
                                                      .map(relaynControlTime -> ByteUtil.getLong(Arrays.copyOfRange(relaynControlTime, 1, 5)))
                                                      .findFirst()
                                                      .orElse(null);
            } else {
             // Relays with 0 remaining control time are not included in the message, so Yukon must generate a 0 value
                value = 0;
            }
        } else {
            if (CollectionUtils.isNotEmpty(data.get(entry.getFieldType()))) {
                if (entry.getFieldType() == FieldType.CONTROL_STATE) {
                    // if control status value is 0xFF then it is in control otherwise its value is 0
                    value = ByteUtil.getInteger(data.get(entry.getFieldType()).get(0)) & 0x01;
                } else if (entry.getFieldType() == FieldType.POWER_QUALITY_RESPONSE_ENABLED) {
                    int rawValue = ByteUtil.getInteger(data.get(entry.getFieldType()).get(0));
                    value = (rawValue != 0 ? 1 : 0); //map any non-zero value to 1
                } else {
                    value = ByteUtil.getInteger(data.get(entry.getFieldType()).get(0));
                }
            }
        }
        if (value == null) {
            if (log.isDebugEnabled()) {
                log.debug("No value was found when evaluating TLV Type: " + entry.getFieldType());
            }
            return null;
        }
        return value.doubleValue();
    }

    @Override
    public PointData getPointData(BuiltInAttribute attribute, Double value, PaoPointIdentifier paoPointIdentifier,
            Integer pointId, Date timeOfReading) {
        if (attribute == BuiltInAttribute.SERVICE_STATUS) {
            /**
             * LCR 6700
             * Adjust value for state group 'LCR Service Status'
             * The service status is represented in two bits:
             * x00 (decimal value 0) - State name: 'In Service', RawState: 1
             * x04 (decimal value 4) - State name: 'Temporarily Out of Service', RawState: 3
             * x08 (decimal value 8) - State Name: 'Out of Service', RawState: 2
             */
            if (value == 0) {
                value = 1.0;
            } else if (value == 4) {
                value = 3.0;
            } else if (value == 8) {
                value = 2.0;
            }
        }
        Integer pointTypeId = paoPointIdentifier.getPointIdentifier().getPointType().getPointTypeId();
        return buildPointData(pointId, pointTypeId, timeOfReading, value);
    }

    private List<PointData> mapIntervalData(ListMultimap<FieldType, byte[]>  data, RfnDevice device,
            AssetAvailabilityPointDataTimes assetAvailabilityTimes) {

        List<PointData> intervalPointData = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcr6700RelayMap> rfnLcrRelayDataMap = Sets.newHashSet();

        try {
            rfnLcrRelayDataMap = RfnLcr6700RelayMap.getRelayMapByPaoType(device.getPaoIdentifier().getPaoType());
        } catch (ParseException e) {
            log.error("Cannot retrieve relay point data map for device type: " + device.getPaoIdentifier().getPaoType(),
                e);
            throw new RuntimeException();
        }
        Long timeInSec = ByteUtil.getLong(data.get(FieldType.UTC).get(0));
        Instant timeOfReading = new Instant(timeInSec * 1000);
        Long intervalStartTime = ByteUtil.getLong(data.get(FieldType.RELAY_INTERVAL_START_TIME).get(0));
        
        Instant firstIntervalTimestamp = new Instant(intervalStartTime * 1000);
        /**
         * Calculating last interval time using UTC (LCR report generation time) and first interval time.
         * Convert total minutes to integer (24*60) as LCR report is automatically generated after 24 Hours
         * 
         */
        int totalMinutes = new Duration(firstIntervalTimestamp, timeOfReading).toStandardMinutes().getMinutes();

        int minutesToAdd = (totalMinutes / RECORDING_INTERVAL) * RECORDING_INTERVAL;
        final Instant lastIntervalTimestamp =  new Instant(firstIntervalTimestamp).plus(Duration.standardMinutes(minutesToAdd));
        
        for (RfnLcr6700RelayMap relay : rfnLcrRelayDataMap) {
            Instant currentIntervalTimestamp = lastIntervalTimestamp;
            // first byte indicates the relay number (0, 1, 2) but next 24 bytes represents either runtime or shedtime
            List<Integer> intervalData = new ArrayList<>();
            data.get(relay.getFieldType()).stream()
                                          .filter(relayNIntervalData -> (ByteUtil.getInteger(relayNIntervalData[0])) == relay.getRelayNumber())
                                          .forEach(relayNIntervalData -> {
                                              for (byte interval : Arrays.copyOfRange(relayNIntervalData, 1, 25)) {
                                                  intervalData.add(ByteUtil.getInteger(interval));
                                              }
                                          });

            LitePoint relayPoint = attributeService.findPointForAttribute(device, relay.getAttribute());

            for (Integer value : intervalData) {
                // 0xFF represents invalid value
                if (value == 0xFF) {
                    continue;
                }
                // Store latest non-zero runtime for asset availability.
                // If runtime > 0 and no time stored for this relay, store currentIntervalTimestamp.
                if (relay.getIndex() != null) {
                    Instant relayTime = assetAvailabilityTimes.getRelayRuntime(relay.getIndex());
                    if (value > 0 && relayTime == null) {
                        assetAvailabilityTimes.setRelayRuntime(relay.getIndex(), currentIntervalTimestamp);
                    }
                }
                if (relayPoint != null) {
                    PointData pointData = buildPointData(relayPoint.getPointID(), relayPoint.getPointType(),
                        currentIntervalTimestamp.toDate(), Double.valueOf(value));
                    intervalPointData.add(pointData);
                }

                currentIntervalTimestamp = currentIntervalTimestamp.minus(Duration.standardMinutes(RECORDING_INTERVAL));
            }
        }

        return intervalPointData;
    }

    @Override
    public void storeAddressingData(JmsTemplate jmsTemplate, ListMultimap<FieldType, byte[]> data, RfnDevice device) {

        ExpressComReportedAddress currentAddress = expressComReportedAddressDao.findCurrentAddress(device.getPaoIdentifier().getPaoId());
        ExpressComReportedAddress address;
        if (currentAddress != null) {
            address = currentAddress.clone();
        } else {
            address = new ExpressComReportedAddress();
        }

        address.setDeviceId(device.getPaoIdentifier().getPaoId());

        address.setTimestamp(new Instant(ByteUtil.getLong(data.get(FieldType.UTC).get(0)) * 1000));
        // reject to save old addressing information in database
        if (currentAddress!= null && currentAddress.getTimestamp().isAfter(address.getTimestamp())) {
            log.info("Current addressing from" + currentAddress.getTimestamp().toDate() + " is newer than newly reported at "
                + address.getTimestamp().toDate() + " for device " + device.getName() + ", ignoring older addressing information");
            return;
        }

        updateIfPresent(data, FieldType.SPID, address::setSpid);
        updateIfPresent(data, FieldType.GEO_ADDRESS, address::setGeo);
        updateIfPresent(data, FieldType.SUBSTATION_ADDRESS, address::setSubstation);
        updateIfPresent(data, FieldType.FEEDER_ADDRESS, address::setFeeder);
        updateIfPresent(data, FieldType.ZIP_ADDRESS, address::setZip);
        updateIfPresent(data, FieldType.UDA_ADDRESS, address::setUda);
        updateIfPresent(data, FieldType.REQUIRED_ADDRESS, address::setRequired);

        List<byte[]> reportedRelaySplinterAddresses = data.get(FieldType.RELAY_N_SPLINTER_ADDRESS);
        List<byte[]> reportedRelayProgramAddresses = data.get(FieldType.RELAY_N_PROGRAM_ADDRESS);

        // If there are new relay addresses, update any existing relay records
        if (CollectionUtils.isNotEmpty(reportedRelaySplinterAddresses) || 
            CollectionUtils.isNotEmpty(reportedRelayProgramAddresses)) {
            //  Get the existing relays so we can update them
            Map<Integer, ExpressComReportedAddressRelay> relays = 
                    Maps.newHashMap(Maps.uniqueIndex(address.getRelays(), ExpressComReportedAddressRelay::getRelayNumber));

            reportedRelaySplinterAddresses.forEach(relayNode ->
                relays.computeIfAbsent(ByteUtil.getInteger(relayNode[0]), ExpressComReportedAddressRelay::new) 
                    .setSplinter(ByteUtil.getInteger(relayNode[1])));

            reportedRelayProgramAddresses.forEach(relayNode ->
                relays.computeIfAbsent(ByteUtil.getInteger(relayNode[0]), ExpressComReportedAddressRelay::new) 
                    .setProgram(ByteUtil.getInteger(relayNode[1])));

            address.setRelays(new HashSet<>(relays.values()));
        }

        log.debug(String.format("Received LM Address for %s - ", address, device.getName()));
        
        if (currentAddress != null) {
            expressComReportedAddressDao.save(address, currentAddress);
        } else {
            expressComReportedAddressDao.insertAddress(address);
        }

        jmsTemplate.convertAndSend(JmsApiDirectory.LM_ADDRESS_NOTIFICATION.getQueue().getName(), address);

    }

    private void updateIfPresent(ListMultimap<FieldType, byte[]> data, FieldType fieldType, Consumer<Integer> setter) {
        data.get(fieldType).stream()
            .findFirst()
            .map(ByteUtil::getInteger)
            .ifPresent(setter);
    }

    @Override
    public Map<Long, Instant> mapBroadcastVerificationMessages(ListMultimap<FieldType, byte[]> decodedPayload) {
        Map<Long, Instant> msgMap = new HashMap<>();
        byte[] messages =
            decodedPayload.get(FieldType.BROADCAST_VERIFICATION_MESSAGES).stream().findFirst().orElse(null);
        // The broadcast message entry can contain anywhere from 1 to 16 messages in the TLV payload.
        // Where each broadcast Message is defined to be 8 bytes long.(4 byte for Unique ID & Time Rx’d)
        log.debug("Parsed message ids:");
        if (messages != null) {
            ByteUtil.divideByteArray(messages, 8).forEach(message -> {
                long messageId = ByteUtil.getLong(Arrays.copyOfRange(message, 0, 4));
                long timeInSec = ByteUtil.getLong(Arrays.copyOfRange(message, 4, 8));
                Instant receivedTimestamp = new Instant(timeInSec * 1000);
                msgMap.put(messageId, receivedTimestamp);
                log.debug("   " + messageId + "     " + receivedTimestamp.toDate());
            });
        }
        return msgMap;
    }

    @Override
    public Range<Instant> mapBroadcastVerificationUnsuccessRange(ListMultimap<FieldType, byte[]> data,
            RfnDevice device) {
        Range<Instant> range = null;
        Long intervalStartTime = ByteUtil.getLong(data.get(FieldType.RELAY_INTERVAL_START_TIME).get(0));

        if (intervalStartTime != null) {
            Long timeInSec = ByteUtil.getLong(data.get(FieldType.UTC).get(0));
            // Remove minutes and seconds from time of reading.
            Instant timeOfReading = new DateTime(timeInSec * 1000).hourOfDay().roundFloorCopy().toInstant();
            Instant earliestStartTime = new Instant(intervalStartTime * 1000);
            range = new Range<>(earliestStartTime, true, timeOfReading, true);

            log.debug("Created range: Min: " + earliestStartTime.toDate() + "(earliest relay start time)       Max: "
                + timeOfReading.toDate() + "(utc truncated to an hour)");
        }

        return range;
    }

    @Override
    public boolean isValidTimeOfReading(ListMultimap<FieldType, byte[]> data) {

        Long timeInSec = ByteUtil.getLong(data.get(FieldType.UTC).get(0));
        DateTime timeOfReading = new DateTime(timeInSec * 1000);
        boolean isValid = timeOfReading.isAfter(year2001);
        log.debug("time of reading:" + timeOfReading.toDate() + " after 1/1/2001 =" + isValid);

        return isValid;

    }

}

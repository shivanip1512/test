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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.ByteUtil;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.model.RfnLcr6700PointDataMap;
import com.cannontech.dr.rfn.model.RfnLcr6700RelayMap;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RfnLcrTlvDataMappingServiceImpl extends RfnLcrDataMappingServiceImpl<ListMultimap<FieldType, byte[]>> {
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
        Instant instantOfReading = new Instant(timeInSec * 1000);
        Date timeOfReading = instantOfReading.toDate();

        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcr6700PointDataMap> rfnLcrPointDataMap = Sets.newHashSet();

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
        assetAvailabilityTimes.setLastCommunicationTime(instantOfReading);

        rfnLcrPointDataMap = RfnLcr6700PointDataMap.getRelayMapByPaoType(device.getPaoIdentifier().getPaoType());

        for (RfnLcr6700PointDataMap entry : rfnLcrPointDataMap) {

            PaoPointIdentifier paoPointIdentifier = null;
            Integer pointId = null;

            try {
                paoPointIdentifier = attributeService.getPaoPointIdentifierForAttribute(device, entry.getAttribute());
                pointId = pointDao.getPointId(paoPointIdentifier);
            } catch (IllegalUseOfAttribute e) {
                log.warn("The attribute: " + entry.getAttribute().toString() + " is not defined for the device: "
                    + device.getName() + " of type: " + device.getPaoIdentifier().getPaoType());
                continue;
            } catch (NotFoundException e) {
                log.debug("Point for attribute (" + entry.getAttribute().toString() + ") does not exist for device: "
                    + device.getName());
                continue;
            }

            Double value = evaluateArchiveReadValue(data, entry);

            if (value != null) {
                PointData pointData = getPointData(entry.getAttribute(), value, paoPointIdentifier, pointId, timeOfReading);
                messagesToSend.add(pointData);
            }
        }

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

    private Double evaluateArchiveReadValue(ListMultimap<FieldType, byte[]> data, RfnLcr6700PointDataMap entry) {

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
                // if this field is not present in message then its value is 0
                value = 0;
            }
        } else {
            if (CollectionUtils.isNotEmpty(data.get(entry.getFieldType()))) {
                if (entry.getFieldType() == FieldType.LUF_EVENTS || entry.getFieldType() == FieldType.LUV_EVENTS) {
                    value = ByteUtil.getInteger(data.get(entry.getFieldType()).get(0));
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
        
        int minutes = new Duration(firstIntervalTimestamp, timeOfReading).toStandardMinutes().getMinutes();
        int recordedIntervals = (minutes / RECORDING_INTERVAL);
        
        int minutesToAdd = recordedIntervals * RECORDING_INTERVAL;
        
        for (RfnLcr6700RelayMap relay : rfnLcrRelayDataMap) {
            Instant currentIntervalTimestamp = new Instant(firstIntervalTimestamp).plus(Duration.standardMinutes(minutesToAdd));
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
                        currentIntervalTimestamp.toDate(), new Double(value));
                    intervalPointData.add(pointData);
                }

                currentIntervalTimestamp = currentIntervalTimestamp.minus(Duration.standardMinutes(RECORDING_INTERVAL));
            }
        }

        return intervalPointData;
    }

    @Override
    public void storeAddressingData(JmsTemplate jmsTemplate, ListMultimap<FieldType, byte[]> data, RfnDevice device) {
        ExpressComReportedAddress address = new ExpressComReportedAddress();
        address.setDeviceId(device.getPaoIdentifier().getPaoId());

        address.setTimestamp(new Instant(ByteUtil.getLong(data.get(FieldType.RELAY_INTERVAL_START_TIME).get(0)) * 1000));

        if (CollectionUtils.isNotEmpty(data.get(FieldType.SPID))) {
            address.setSpid(ByteUtil.getInteger(data.get(FieldType.SPID).get(0)));
        }

        if (CollectionUtils.isNotEmpty(data.get(FieldType.GEO_ADDRESS))) {
            address.setGeo(ByteUtil.getInteger(data.get(FieldType.GEO_ADDRESS).get(0)));
        }

        Integer sub = -1;
        if (CollectionUtils.isNotEmpty(data.get(FieldType.SUBSTATION_ADDRESS))) {
            sub = ByteUtil.getInteger(data.get(FieldType.SUBSTATION_ADDRESS).get(0));
        }
        address.setSubstation(sub);

        if (CollectionUtils.isNotEmpty(data.get(FieldType.FEEDER_ADDRESS))) {
            address.setFeeder(ByteUtil.getInteger(data.get(FieldType.FEEDER_ADDRESS).get(0)));
        }
        if (CollectionUtils.isNotEmpty(data.get(FieldType.ZIP_ADDRESS))) {
            address.setZip(ByteUtil.getInteger(data.get(FieldType.ZIP_ADDRESS).get(0)));
        }
        if (CollectionUtils.isNotEmpty(data.get(FieldType.UDA_ADDRESS))) {
            address.setUda(ByteUtil.getInteger(data.get(FieldType.UDA_ADDRESS).get(0)));
        }
        if (CollectionUtils.isNotEmpty(data.get(FieldType.REQUIRED_ADDRESS))) {
            address.setRequired(ByteUtil.getInteger(data.get(FieldType.REQUIRED_ADDRESS).get(0)));
        }

        Map<Integer, ExpressComReportedAddressRelay> relays = new HashMap<>();

        if (CollectionUtils.isNotEmpty(data.get(FieldType.RELAY_N_SPLINTER_ADDRESS))) {
            data.get(FieldType.RELAY_N_SPLINTER_ADDRESS).forEach(relayNode -> {
                ExpressComReportedAddressRelay relay = new ExpressComReportedAddressRelay();
                relay.setRelayNumber(ByteUtil.getInteger(relayNode[0]));
                relay.setSplinter(ByteUtil.getInteger(relayNode[1]));
                relays.put(relay.getRelayNumber(), relay);
            });
        }

        if (CollectionUtils.isNotEmpty(data.get(FieldType.RELAY_N_PROGRAM_ADDRESS))) {
            data.get(FieldType.RELAY_N_PROGRAM_ADDRESS).forEach(relayNode -> {
                ExpressComReportedAddressRelay relay = relays.get(ByteUtil.getInteger(relayNode[0]));
                relay.setProgram(ByteUtil.getInteger(relayNode[1]));
            });
        }
        address.setRelays(new HashSet<ExpressComReportedAddressRelay>(relays.values()));

        log.debug(String.format("Received LM Address for %s - ", address, device.getName()));
        expressComReportedAddressDao.save(address);

        jmsTemplate.convertAndSend("yukon.notif.obj.dr.rfn.LmAddressNotification", address);

    }

    @Override
    public Map<Long, Instant> mapBroadcastVerificationMessages(ListMultimap<FieldType, byte[]> decodedPayload) {
        Map<Long, Instant> msgMap = new HashMap<>();
        byte[] messages =
            decodedPayload.get(FieldType.BROADCAST_VERIFICATION_MESSAGES).stream().findFirst().orElse(null);
        // The broadcast message entry can contain anywhere from 1 to 16 messages in the TLV payload.
        // Where each broadcast Message is defined to be 8 bytes long.(4 byte for Unique ID & Time Rx�d)
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
            range = new Range<Instant>(earliestStartTime, true, timeOfReading, true);

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
        log.debug("time of reading:" + timeOfReading.toDate() + "    after 1/1/2001 =" + isValid);

        return isValid;

    }

}

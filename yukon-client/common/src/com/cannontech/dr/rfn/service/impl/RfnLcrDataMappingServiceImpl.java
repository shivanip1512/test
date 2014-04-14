package com.cannontech.dr.rfn.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.model.RfnLcrPointDataMap;
import com.cannontech.dr.rfn.model.RfnLcrRelayDataMap;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RfnLcrDataMappingServiceImpl implements RfnLcrDataMappingService {
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private ExpressComReportedAddressDao expressComReportedAddressDao;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    
    private static final Logger log = YukonLogManager.getLogger(RfnLcrDataMappingServiceImpl.class);
    private static final LogHelper logHelper = YukonLogManager.getLogHelper(RfnLcrDataMappingServiceImpl.class);
    private static final DateTime year2001 = new DateTime(2001, 1, 1, 0, 0);
    private static final int MAX_INTERVALS = 24;

    @Override
    public List<PointData> mapPointData(RfnLcrReadingArchiveRequest request, SimpleXPathTemplate data) {
        Long timeInSec = data.evaluateAsLong("/DRReport/@utc");
        Instant instantOfReading = new Instant(timeInSec * 1000);
        Date timeOfReading = instantOfReading.toDate();
        
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcrPointDataMap> rfnLcrPointDataMap = Sets.newHashSet();

        RfnDevice device = rfnDeviceLookupService.getDevice(request.getRfnIdentifier());
        
        //Object to store communication and non-zero runtime times for insertion into DynamicLcrCommunications
        AssetAvailabilityPointDataTimes assetAvailabilityTimes = new AssetAvailabilityPointDataTimes(device.getPaoIdentifier().getPaoId());
        //This timestamp is created by the LCR when the report is generated. The message is then held by the node until
        //its reporting interval arrives. This could be improved by getting the timestamp from the gateway if/when Yukon
        //and Network Manager share that information.
        assetAvailabilityTimes.setLastCommunicationTime(instantOfReading);
        
        rfnLcrPointDataMap = RfnLcrPointDataMap.getRelayMapByPaoType(device.getPaoIdentifier().getPaoType());
        
        for (RfnLcrPointDataMap entry : rfnLcrPointDataMap) {
            PaoPointIdentifier paoPointIdentifier = null;
            Integer pointId = null;
            try {
                paoPointIdentifier = attributeService.getPaoPointIdentifierForAttribute(device, entry.getAttribute());
                pointId = pointDao.getPointId(paoPointIdentifier);
            }
            catch (IllegalUseOfAttribute e) {
                log.warn("The attribute: " + entry.getAttribute().toString() 
                         + " is not defined for the device: " + device.getName() 
                         + " of type: " + device.getPaoIdentifier().getPaoType());
                continue;
            } catch (NotFoundException e) {
                log.debug("Point for attribute (" + entry.getAttribute().toString() + ") does not exist for device: " + device.getName());
                continue;
            }
            Double value = evaluateArchiveReadValue(data, entry);
            if (value != null) {
                if (entry.getAttribute() == BuiltInAttribute.SERVICE_STATUS) {
                    /** Adjust value for state group 'LCR Service Status'
                     * The service status is represented in two bits:
                     * 00 (decimal value 0) - State name: 'In Service', RawState: 1 
                     * 01 (decimal value 1) - State name: 'Temporarily Out of Service', RawState: 3
                     * 10 (decimal value 2) - State Name: 'Out of Service', RawState: 2
                     */
                    if (value == 0) {
                        value = 1.0;
                    } else if (value == 1) {
                        value = 3.0;
                    }
                }
                Integer pointTypeId = paoPointIdentifier.getPointIdentifier().getPointType().getPointTypeId();
                PointData pointData = createPointData(pointId, pointTypeId, timeOfReading, value);
                messagesToSend.add(pointData);
            }
        }
        List<PointData> intervalData;
        
        /** This may be useful some day */
        boolean readNow = (data.evaluateAsInt("/DRReport/Info/Flags") & 0x01) == 1;
        
        intervalData = mapIntervalData(data, device, assetAvailabilityTimes);
        messagesToSend.addAll(intervalData);
        
        //Update asset availability times for this device
        dynamicLcrCommunicationsDao.insertData(assetAvailabilityTimes);
        
        return messagesToSend;
    }

    private List<PointData> mapIntervalData(SimpleXPathTemplate data, RfnDevice device, AssetAvailabilityPointDataTimes assetAvailabilityTimes) {
        
        int intervalLengthMinutes = evaluateArchiveReadValue(data, RfnLcrPointDataMap.RECORDING_INTERVAL).intValue();
        
        List<PointData> intervalPointData = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcrRelayDataMap> rfnLcrRelayDataMap = Sets.newHashSet();

        try {
            rfnLcrRelayDataMap = RfnLcrRelayDataMap.getRelayMapByPaoType(device.getPaoIdentifier().getPaoType());
        } catch (ParseException e) {
            log.error("Cannot retrieve relay point data map for device type: " + device.getPaoIdentifier().getPaoType(), e);
            throw new RuntimeException();
        }
        
        Long timeInSec = data.evaluateAsLong("/DRReport/@utc");
        Instant timeOfReading = new Instant(timeInSec * 1000);
        
        for (RfnLcrRelayDataMap relay : rfnLcrRelayDataMap) {
            
            List<Integer> intervalData = data.evaluateAsIntegerList("/DRReport/Relays/Relay" + 
                    relay.getRelayIdXPathString() + "/IntervalData/Interval");

            LitePoint runTimePoint = attributeService.getPointForAttribute(device, relay.getRunTimeAttribute());
            LitePoint shedTimePoint = attributeService.getPointForAttribute(device, relay.getShedTimeAttribute());

            Long intervalStartTime = data.evaluateAsLong("/DRReport/Relays/Relay" + relay.getRelayIdXPathString() + "/IntervalData/@startTime");
            if (intervalStartTime == null) continue;
            
            Instant firstIntervalTimestamp = new Instant(intervalStartTime * 1000);
            
            int minutes = new Duration(firstIntervalTimestamp, timeOfReading).toStandardMinutes().getMinutes();
            int recordedIntervals = (minutes / intervalLengthMinutes);
            if (recordedIntervals <= 0) continue;
            
            int minutesToAdd = recordedIntervals * intervalLengthMinutes;
            
            Instant currentIntervalTimestamp = new Instant(firstIntervalTimestamp).plus(Duration.standardMinutes(minutesToAdd));
            
            int intervalCount = 1;
            for (Integer interval : intervalData) {
                if (intervalCount++ > MAX_INTERVALS) {
                    // The RFN LCR returns 36 valid timestamped hourly values every 24 hours,
                    // the 12 hours are duplicated. Throw out the last 12 values.
                    break;
                }
                // Skip all intervals occuring in the future since the device sends us 36
                // intervals every time even if they have not happened yet. 
                // Also skip if interval value is 0xFFFF which indicates an invalid hour. 
                if (currentIntervalTimestamp.isBefore(firstIntervalTimestamp) || interval == 0xFFFF) {
                    continue;
                }
                
                Integer runTime = (interval & 0xFF00) >>> 8;
                Integer shedTime = interval & 0xFF;
                
                //Store latest non-zero runtime for asset availability
                //If runtime > 0 and no time stored for this relay, store currentIntervalTimestamp
                Instant relayTime = assetAvailabilityTimes.getRelayRuntime(relay.getIndex());
                if(runTime > 0 && relayTime == null) {
                    assetAvailabilityTimes.setRelayRuntime(relay.getIndex(), currentIntervalTimestamp);
                }

                PointData runTimePointData = createPointData(runTimePoint.getPointID(), 
                        runTimePoint.getPointType(), currentIntervalTimestamp.toDate(), new Double(runTime));
                PointData shedTimePointData = createPointData(shedTimePoint.getPointID(), 
                        shedTimePoint.getPointType(), currentIntervalTimestamp.toDate(), new Double(shedTime));

                intervalPointData.add(runTimePointData);
                intervalPointData.add(shedTimePointData);
                
                currentIntervalTimestamp = currentIntervalTimestamp.minus(Duration.standardMinutes(intervalLengthMinutes));
            }
        }
        
        return intervalPointData;
    }

    private PointData createPointData(Integer pointId, Integer pointType, Date timestamp, Double value) {
        
        PointData pointData = new PointData();
        pointData.setTagsPointMustArchive(true);    
        pointData.setPointQuality(PointQuality.Normal);

        pointData.setId(pointId);
        pointData.setType(pointType);
        pointData.setTime(timestamp);
        pointData.setValue(value);
        
        return pointData;
    }

    private Double evaluateArchiveReadValue(SimpleXPathTemplate data, RfnLcrPointDataMap entry) {
        
        Integer value = null;
        value = data.evaluateAsInt(entry.getxPathQuery());
        if (value == null) {
            log.error("No value was found when evaluating XPath query: " + entry.getxPathQuery());
            return null;
        }
        // If the value is found inside a Flags element, to pull it out it must be bit-masked and -shifted.
        if (entry.getMask() != null) {
            value = value & entry.getMask();
        }
        if (entry.getShift() != null) {
            value = value >>> entry.getShift();
        }
        
        Double retVal = value.doubleValue();
        if (entry.getMultiplier() != null) {
            retVal *= entry.getMultiplier();
        }
        
        return retVal;
    }

    @Override
    public void storeAddressingData(JmsTemplate jmsTemplate, SimpleXPathTemplate data, RfnDevice device) {
        ExpressComReportedAddress address = new ExpressComReportedAddress();
        address.setDeviceId(device.getPaoIdentifier().getPaoId());
        
        address.setTimestamp(new Instant(data.evaluateAsLong("/DRReport/@utc") * 1000));
        address.setSpid(data.evaluateAsInt("/DRReport/ExtendedAddresssing/SPID"));
        address.setGeo(data.evaluateAsInt("/DRReport/ExtendedAddresssing/Geo"));
        
        Integer sub = data.evaluateAsInt("/DRReport/ExtendedAddresssing/Substation");
        sub = sub == null ? -1 : sub;
        address.setSubstation(sub);
            
        address.setFeeder(data.evaluateAsInt("/DRReport/ExtendedAddresssing/Feeder"));
        address.setZip(data.evaluateAsInt("/DRReport/ExtendedAddresssing/Zip"));
        address.setUda(data.evaluateAsInt("/DRReport/ExtendedAddresssing/UDA"));
        address.setRequired(data.evaluateAsInt("/DRReport/ExtendedAddresssing/Required"));
        
        /** In the spec but not used yet
         * data.evaluateAsInt("/DRReport/ExtendedAddresssing/SEPDeviceClass");
         * data.evaluateAsInt("/DRReport/ExtendedAddresssing/SEPUtilityEnrollmentGroup"); 
         */
        
        List<Node> relaysNodes = data.evaluateAsNodeList("/DRReport/Relays/Relay");
        Set<ExpressComReportedAddressRelay> relays = Sets.newHashSet();
        for (Node relayNode : relaysNodes) {
            Element elem = (Element) relayNode;
            ExpressComReportedAddressRelay relay = new ExpressComReportedAddressRelay();
            
            relay.setRelayNumber(Integer.parseInt(elem.getAttribute("id")));
            relay.setProgram(Integer.parseInt(elem.getElementsByTagName("Program").item(0).getTextContent()));
            relay.setSplinter(Integer.parseInt(elem.getElementsByTagName("Splinter").item(0).getTextContent()));
            
            relays.add(relay);
        }
        address.setRelays(relays);
        
        logHelper.debug("Received LM Address for %s - " + address, device.getName());
        expressComReportedAddressDao.save(address);
        
        jmsTemplate.convertAndSend("yukon.notif.obj.dr.rfn.LmAddressNotification", address);
    }

    @Override
    public Map<Long, Instant> mapBroadcastVerificationMessages(SimpleXPathTemplate data) {
        Map<Long, Instant> msgMap = new HashMap<>();
        List<Node> events =
            data.evaluateAsNodeList("/DRReport/BroadcastVerificationMessages/Event");
        log.debug("Parsed message ids:");
        for (Node event : events) {
            Element elem = (Element) event;
            long messageId =
                Long.parseLong(elem.getElementsByTagName("UniqueIdentifier").item(0)
                    .getTextContent());
            long timeInSec =
                Long.parseLong(elem.getElementsByTagName("ReceivedTimestamp").item(0)
                    .getTextContent());
            Instant receivedTimestamp = new Instant(timeInSec * 1000);
            msgMap.put(messageId, receivedTimestamp);
            log.debug("   "+messageId +  "     " + receivedTimestamp.toDate());
        }
        return msgMap;
    }
    
    @Override
    public Range<Instant> mapBroadcastVerificationUnsuccessRange(SimpleXPathTemplate data,
                                                                 RfnDevice device) {
        Range<Instant> range = null;
        PaoType type = device.getPaoIdentifier().getPaoType();

        Set<RfnLcrRelayDataMap> rfnLcrRelayDataMap = Sets.newHashSet();
        try {
            rfnLcrRelayDataMap = RfnLcrRelayDataMap.getRelayMapByPaoType(type);
        } catch (ParseException e) {
            log.error("Cannot retrieve relay point data map for device type: " + type, e);
            throw new RuntimeException();
        }

        List<Long> relayStartTimes = new ArrayList<Long>();
        for (RfnLcrRelayDataMap relay : rfnLcrRelayDataMap) {
            try{
                long startTime = data.evaluateAsLong("/DRReport/Relays/Relay"
                                                     + relay.getRelayIdXPathString()
                                                     + "/IntervalData/@startTime");
                if (startTime > 0) {
                    relayStartTimes.add(startTime);
                }
            } catch (NullPointerException e) {
                // if there is no relay info it could be gap filled data before it was configured,
                // before it had a timesync or a combination
                // It is ok for this method to return null if the valid range can't be determined.
            }
        }

        if (!relayStartTimes.isEmpty()) {
            Collections.sort(relayStartTimes);
            Long timeInSec = data.evaluateAsLong("/DRReport/@utc");
            // remove minutes and seconds from time of reading
            Instant timeOfReading = new DateTime(timeInSec * 1000).hourOfDay()
                .roundFloorCopy().toInstant();
            Instant earliestStartTime = new Instant(relayStartTimes.get(0) * 1000);
            range = new Range<Instant>(earliestStartTime, true, timeOfReading, true);
            log.debug("Created range: Min: " + earliestStartTime.toDate()
                      + "(earliest relay start time)       Max: " + timeOfReading.toDate()
                      + "(/DRReport/@utc truncated to an hour)");
        }
        return range;
    }

    @Override
    public boolean isValidTimeOfReading(SimpleXPathTemplate data) {
        Long timeInSec = data.evaluateAsLong("/DRReport/@utc");
        DateTime timeOfReading = new DateTime(timeInSec * 1000);
        boolean isValid = timeOfReading.isAfter(year2001);
        log.debug("time of reading:"+timeOfReading.toDate()+"    after 1/1/2001 =" +isValid);
        return isValid;
    }
}
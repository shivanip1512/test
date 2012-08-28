package com.cannontech.dr.rfn.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.dao.LmDeviceReportedDataDao;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.dao.LmReportedAddressRelay;
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
    @Autowired private LmDeviceReportedDataDao lmDeviceReportedDataDao;

    private static final Logger log = YukonLogManager.getLogger(RfnLcrDataMappingServiceImpl.class);
    private static final LogHelper logHelper = YukonLogManager.getLogHelper(RfnLcrDataMappingServiceImpl.class);

    public List<PointData> mapPointData(RfnLcrReadingArchiveRequest request, SimpleXPathTemplate data) {
        
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcrPointDataMap> rfnLcrPointDataMap = Sets.newHashSet();

        RfnDevice device = rfnDeviceLookupService.getDevice(request.getRfnIdentifier());

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
                     * 01 (decimal value 1) - State name: 'Temporarily Out of Serivice', RawState: 3
                     * 10 (decimal value 2) - State Name: 'Out of Service', RawState: 2
                     */
                    if (value == 0) {
                        value = 1.0;
                    } else if (value == 1) {
                        value = 3.0;
                    }
                }
                Integer pointTypeId = paoPointIdentifier.getPointIdentifier().getPointType().getPointTypeId();
                Long timeInSec = data.evaluateAsLong("/DRReport/@utc");
                Date timeOfReading = new Instant(timeInSec * 1000).toDate();
                PointData pointData = createPointData(pointId, pointTypeId, timeOfReading, value);
                messagesToSend.add(pointData);
            }
        }
        
        List<PointData> intervalData = mapIntervalData(request, data, device);
        messagesToSend.addAll(intervalData);
        
        return messagesToSend;
    }

    private List<PointData> mapIntervalData(RfnLcrReadingArchiveRequest request, SimpleXPathTemplate data, RfnDevice device) {
        
        List<PointData> intervalPointData = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcrRelayDataMap> rfnLcrRelayDataMap = Sets.newHashSet();

        try {
            rfnLcrRelayDataMap = RfnLcrRelayDataMap.getRelayMapByPaoType(device.getPaoIdentifier().getPaoType());
        } catch (ParseException e) {
            log.error("Cannot retrieve relay point data map for device type: " + device.getPaoIdentifier().getPaoType(), e);
            throw new RuntimeException();
        }
        
        for (RfnLcrRelayDataMap relay : rfnLcrRelayDataMap) {
            List<Integer> intervalData = data.evaluateAsIntegerList("/DRReport/Relays/Relay" + 
                    relay.getRelayIdXPathString() + "/IntervalData/Interval");

            LitePoint runTimePoint = attributeService.getPointForAttribute(device, relay.getRunTimeAttribute());
            LitePoint shedTimePoint = attributeService.getPointForAttribute(device, relay.getShedTimeAttribute());

            Long firstIntervalTimestamp = data.evaluateAsLong("/DRReport/Relays/Relay" + relay.getRelayIdXPathString() + "/IntervalData/@startTime");
            Instant currentIntervalTimestamp = new Instant(firstIntervalTimestamp * 1000);
            for (Integer interval : intervalData) {
                Integer runTime = (interval & 0xFF00) >>> 8;
                Integer shedTime = interval & 0xFF;

                PointData runTimePointData = createPointData(runTimePoint.getPointID(), 
                        runTimePoint.getPointType(), currentIntervalTimestamp.toDate(), new Double(runTime));
                PointData shedTimePointData = createPointData(shedTimePoint.getPointID(), 
                        shedTimePoint.getPointType(), currentIntervalTimestamp.toDate(), new Double(shedTime));

                intervalPointData.add(runTimePointData);
                intervalPointData.add(shedTimePointData);
                currentIntervalTimestamp = currentIntervalTimestamp
                        .withDurationAdded(Duration.standardHours(1), 1);
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
        
        return new Double(value);
    }

    @Override
    public void storeAddressingData(JmsTemplate jmsTemplate, SimpleXPathTemplate data, RfnDevice device) {
        LmReportedAddress address = new LmReportedAddress();
        address.setDeviceId(device.getPaoIdentifier().getPaoId());
        
        address.setTimestamp(new Instant(data.evaluateAsLong("/DRReport/@utc") * 1000));
        address.setSpid(data.evaluateAsInt("/DRReport/ExtendedAddresssing/SPID"));
        address.setGeo(data.evaluateAsInt("/DRReport/ExtendedAddresssing/Geo"));
        
        /** hack until Karl gets us some new firmware */
        Integer sub = data.evaluateAsInt("/DRReport/ExtendedAddresssing/Substation");
        sub = sub == null ? 0 : sub;
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
        Set<LmReportedAddressRelay> relays = Sets.newHashSet();
        for (Node relayNode : relaysNodes) {
            Element elem = (Element) relayNode;
            LmReportedAddressRelay relay = new LmReportedAddressRelay();
            
            relay.setRelayNumber(Integer.parseInt(elem.getAttribute("id")));
            relay.setProgram(Integer.parseInt(elem.getElementsByTagName("Program").item(0).getTextContent()));
            relay.setSplinter(Integer.parseInt(elem.getElementsByTagName("Splinter").item(0).getTextContent()));
            
            relays.add(relay);
        }
        address.setRelays(relays);
        
        logHelper.debug("Received LM Address for %s - " + address, device.getName());
        lmDeviceReportedDataDao.save(address);
        
        jmsTemplate.convertAndSend("yukon.notif.obj.dr.rfn.LmAddressNotification", address);
    }
    
}
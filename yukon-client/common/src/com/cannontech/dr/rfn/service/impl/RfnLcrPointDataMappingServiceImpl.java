package com.cannontech.dr.rfn.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.model.RfnLcrPointDataMap;
import com.cannontech.dr.rfn.model.RfnLcrRelayDataMap;
import com.cannontech.dr.rfn.service.RfnLcrPointDataMappingService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RfnLcrPointDataMappingServiceImpl implements RfnLcrPointDataMappingService {
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;

    private static final Logger log = YukonLogManager.getLogger(RfnLcrPointDataMappingServiceImpl.class);    

    public List<PointData> mapPointData(RfnLcrReadingArchiveRequest archiveRequest, 
            SimpleXPathTemplate decodedMessage) {
        List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcrPointDataMap> rfnLcrPointDataMap = Sets.newHashSet();

        RfnDevice device = rfnDeviceLookupService.getDevice(archiveRequest.getRfnIdentifier());

        rfnLcrPointDataMap = RfnLcrPointDataMap.getRelayMapByPaoType(device.getPaoIdentifier().getPaoType());
        for (RfnLcrPointDataMap entry : rfnLcrPointDataMap) {
            PaoPointIdentifier paoPointIdentifier = null;
            Integer pointId = null;
            try {
                paoPointIdentifier = attributeService
                        .getPaoPointIdentifierForAttribute(device, entry.getAttribute());
                pointId = pointDao.getPointId(paoPointIdentifier);
            }
            catch (IllegalUseOfAttribute e) {
                log.warn("The attribute: " + entry.getAttribute().toString() + " is not defined for the device: " +
                        device.getName() + " of type: " + device.getPaoIdentifier().getPaoType());
                continue;
            } catch (NotFoundException e) {
                log.warn("Point for attribute (" + entry.getAttribute().toString() +
                        ") does not exist for device: " + device.getName());
                continue;
            }
            Double value = evaluateArchiveReadValue(decodedMessage, entry);
            if (value != null) {
                if(entry.getAttribute() == BuiltInAttribute.SERVICE_STATUS) { value += 1; } // temporary until we know what firmware is sending.
                Integer pointTypeId = paoPointIdentifier.getPointIdentifier().getPointType().getPointTypeId();
                Long timeInSec = decodedMessage.evaluateAsLong("/DRReport/@utc");
                Date timeOfReading = new Instant(timeInSec * 1000).toDate();
                PointData pointData = createPointData(pointId, pointTypeId, timeOfReading, value);
                messagesToSend.add(pointData);
            }
        }
        List<PointData> intervalData = mapIntervalData(archiveRequest, decodedMessage, device);
        messagesToSend.addAll(intervalData);
        return messagesToSend;
    }

    private List<PointData> mapIntervalData(RfnLcrReadingArchiveRequest archiveRequest, 
            SimpleXPathTemplate decodedXml, RfnDevice device) {
        List<PointData> intervalPointData = Lists.newArrayListWithExpectedSize(16);
        Set<RfnLcrRelayDataMap> rfnLcrRelayDataMap = Sets.newHashSet();

        try {
            rfnLcrRelayDataMap = RfnLcrRelayDataMap.getRelayMapByPaoType(device.getPaoIdentifier().getPaoType());
        } catch (ParseException e) {
            log.error("Cannot retrieve relay point data map for device type: " + device.getPaoIdentifier().getPaoType(), e);
            throw new RuntimeException();
        }
        for (RfnLcrRelayDataMap relay : rfnLcrRelayDataMap) {
            List<Integer> intervalData = decodedXml.evaluateAsIntegerList("/DRReport/Relays/Relay" + 
                    relay.getRelayIdXPathString() + "/IntervalData/Interval");

            LitePoint runTimePoint = attributeService.getPointForAttribute(device, relay.getRunTimeAttribute());
            LitePoint shedTimePoint = attributeService.getPointForAttribute(device, relay.getShedTimeAttribute());
            LitePoint controlTimePoint = attributeService.getPointForAttribute(device, relay.getControlTimeAttribute());

            Long firstIntervalTimestamp = decodedXml.evaluateAsLong("/DRReport/Relays/Relay" + relay.getRelayIdXPathString() + "/IntervalData/@startTime");
            Instant currentIntervalTimestamp = new Instant(firstIntervalTimestamp * 1000);
            for (Integer interval : intervalData) {
                Integer runTime = (interval & 0xFF00) >>> 8;
                Integer shedTime = interval & 0xFF;

                PointData runTimePointData = createPointData(runTimePoint.getPointID(), 
                        runTimePoint.getPointType(), currentIntervalTimestamp.toDate(), new Double(runTime));
                // Shed time is recorded in minutes, control time is recorded in seconds.
                PointData shedTimePointData = createPointData(shedTimePoint.getPointID(), 
                        shedTimePoint.getPointType(), currentIntervalTimestamp.toDate(), new Double(shedTime));
                PointData controlTimePointData = createPointData(controlTimePoint.getPointID(), 
                        controlTimePoint.getPointType(), currentIntervalTimestamp.toDate(), new Double(shedTime * 60));

                intervalPointData.add(runTimePointData);
                intervalPointData.add(shedTimePointData);
                intervalPointData.add(controlTimePointData);
                currentIntervalTimestamp = currentIntervalTimestamp
                        .withDurationAdded(Duration.standardHours(1), -1);
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

    private Double evaluateArchiveReadValue(SimpleXPathTemplate xPathTemplate, RfnLcrPointDataMap entry) {
        Integer value = null;
        value = xPathTemplate.evaluateAsInt(entry.getxPathQuery());
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
}


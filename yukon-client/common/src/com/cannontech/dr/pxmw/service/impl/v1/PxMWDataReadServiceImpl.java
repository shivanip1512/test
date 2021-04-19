package com.cannontech.dr.pxmw.service.impl.v1;

import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.buf.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.pxmw.model.MWChannel;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesValueV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;

public class PxMWDataReadServiceImpl implements PxMWDataReadService {

    private static final Logger log = YukonLogManager.getLogger(PxMWDataReadServiceImpl.class);

    @Autowired private AsyncDynamicDataSource dispatchData;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PxMWCommunicationServiceV1 pxMWCommunicationService;

    @Override
    public Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds) {
        Multimap<PaoType, LiteYukonPAObject> paoTypeToPao = HashMultimap.create();
        Multimap<PaoIdentifier, PointData> receivedPoints = HashMultimap.create();
        List<LiteYukonPAObject> paos = new ArrayList<>();
        Range<Instant> queryRange = getQueryRange();
        
        log.info("Initiating read for all attributes on device IDs: {}", deviceIds);

        for (LiteYukonPAObject pao : paoDao.getLiteYukonPaos(deviceIds)) {
            if (pao.getPaoType().isCloudLcr()) {
                paoTypeToPao.put(pao.getPaoType(), pao);
                paos.add(pao);
            } else {
                log.info("Non-RF-LCR device ID passed: {}. Will be skipped", pao);
            }
        }

        for (PaoType paoType : paoTypeToPao.keySet()) {
            try {
                receivedPoints.putAll(retrievePointData(paoTypeToPao.get(paoType), getAttributesForPaoType(paoType), queryRange));
            } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
                log.error("An error occurred when requesting data for attributes {} on PAOs {}", paoTypeToPao.get(paoType), getAttributesForPaoType(paoType), e);
            }
        }

        if (!receivedPoints.isEmpty()) {
            dispatchData.putValues(receivedPoints.values());
            updateAssetAvailability(receivedPoints);
        }

        return receivedPoints;
    }
    
    @Override
    public Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds, Set<BuiltInAttribute> attributes) {
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaos(deviceIds);
        Multimap<PaoIdentifier, PointData> receivedPoints = HashMultimap.create();
        
        log.info("Initiating read for deviceIDs: {} on attributes: {}", deviceIds, attributes);
        try {
            receivedPoints = retrievePointData(paos, attributes, getQueryRange());
        } catch (PxMWCommunicationExceptionV1 | PxMWException e) {
            log.error("An error occurred when requesting data for attributes {} on PAOs {}", deviceIds, attributes, e);
        }

        if (!receivedPoints.isEmpty()) {
            dispatchData.putValues(receivedPoints.values());
            updateAssetAvailability(receivedPoints);
        }

        return receivedPoints;
    }

    private Multimap<PaoIdentifier, PointData> retrievePointData(Iterable<LiteYukonPAObject> paos, Iterable<BuiltInAttribute> attribtues, Range<Instant> queryRange) {
        Multimap<PaoIdentifier, PointData> newPaoPointMap = HashMultimap.create();
        Map<Integer, LiteYukonPAObject> deviceIdToPao = StreamSupport.stream(paos.spliterator(), false).collect(Collectors.toMap(LiteYukonPAObject::getYukonID, liteYukonPao -> liteYukonPao));
        BidiMap<Integer, String> deviceIdGuid = new DualHashBidiMap<Integer, String>(deviceDao.getGuids(deviceIdToPao.keySet()));
        List<PxMWTimeSeriesDeviceResultV1> timeSeriesResults = new ArrayList<PxMWTimeSeriesDeviceResultV1>();

        List<String> tags = getTagsForAttributes(attribtues);
        List<PxMWTimeSeriesDeviceV1> request = buildRequests(deviceIdGuid.values(), tags);
        timeSeriesResults.addAll(pxMWCommunicationService.getTimeSeriesValues(request, queryRange));

        for (PxMWTimeSeriesDeviceResultV1 deviceResult : timeSeriesResults) {
            Integer deviceId = deviceIdGuid.getKey(deviceResult.getDeviceId());
            for (PxMWTimeSeriesResultV1 result : deviceResult.getResults()) {
                Integer tag = null;
                try {
                    tag = Integer.parseInt(result.getTag());
                } catch (NullPointerException | NumberFormatException e) {
                    log.error("Error parsing tag {} from API response", result.getTag(), e);
                }
                if (tag != null) {
                    MWChannel mwChannel = MWChannel.getChannelLookup().get(tag);
                    LitePoint holder = attributeService.findPointForAttribute(deviceIdToPao.get(deviceId), mwChannel.getBuiltInAttribute()); // This could be optimized
                    for (PxMWTimeSeriesValueV1 value : result.getValues()) {
                        PointData newPoint = parseValueDataToPoint(mwChannel, value);
                        if (newPoint != null) {
                            newPoint.setId(holder.getPointID());
                            newPoint.setPointQuality(PointQuality.Normal);
                            newPaoPointMap.put(deviceIdToPao.get(deviceId).getPaoIdentifier(), newPoint);
                        }
                    }
                }
            }
        }

        return newPaoPointMap;
    }

    /**
     * Parses the channel response into a point, using the channel to determine the correct parse method
     * Some channels are types that aren't stored in points (strings, specifically). Null is returned for those points
     */
    private PointData parseValueDataToPoint(MWChannel channel, PxMWTimeSeriesValueV1 value) {
        PointData pointData = new PointData();
        log.debug("Attempting to parse point data from message: {}", value);

        pointData.setTime(new Date(value.getTimestamp()));
        String pxReturnedValue = value.getValue();
        double pointValue;

        if (MWChannel.getBooleanChannels().contains(channel)) {
            if (!pxReturnedValue.toLowerCase().equals("true") && !pxReturnedValue.toLowerCase().equals("false")) {
                log.info("Unexpected Value {} for channel {}. Discarding value", pxReturnedValue, channel);
                pointData = null;
            } else {
                pointValue = Boolean.parseBoolean(pxReturnedValue) ? 1 : 0;
                pointData.setValue(pointValue);
            }
        } else if (MWChannel.getIntegerChannels().contains(channel)) {
            try {
                pointValue = Integer.parseInt(pxReturnedValue);
            } catch (NullPointerException | NumberFormatException e) {
                log.error("Error processing value {} for channel {}", pxReturnedValue, channel, e);
                pointData = null;
            }
        } else if (MWChannel.getFloatChannels().contains(channel)) {
            try {
                pointValue = Double.parseDouble(pxReturnedValue);
            } catch (NullPointerException | NumberFormatException e) {
                log.error("Error processing value {} for channel {}", pxReturnedValue, channel, e);
                pointData = null;
            }
        } else {
            log.info("Channel {} is a type that cannot be parsed into point data. Disarding received value: {}", pxReturnedValue);
            pointData = null;
        }

        log.debug("Parsed point data {} from response {}", pointData, value);
        return pointData;
    }

    /**
     * Helps optimize the requests that are built by taking a set of GUIDs and a set of 
     * tags that are being requested for those GUIDs and building the minimum number of requests
     */
    private List<PxMWTimeSeriesDeviceV1> buildRequests(Collection<String> guids, List<String> tags) {
        List<PxMWTimeSeriesDeviceV1> devices = new ArrayList<PxMWTimeSeriesDeviceV1>();
        List<List<String>> chunkedTags = Lists.partition(tags, 10);
        for (List<String> tagSubset : chunkedTags) {
            String tagCSV = buildTagString(tagSubset);
            for (String guid : guids) {
                devices.add(new PxMWTimeSeriesDeviceV1(guid, tagCSV));
            }
        }
        return devices;
    }

    /**
     * Currently just returns a range of the last week
     */
    private Range<Instant> getQueryRange() {
        DateTime queryEndTime = new DateTime();
        DateTime queryStartTime = queryEndTime.minusDays(7);
        return new Range<Instant>(queryStartTime.toInstant(), false, queryEndTime.toInstant(), true);
    }

    private void updateAssetAvailability(Multimap<PaoIdentifier, PointData> pointUpdates) {
        for (PaoIdentifier pao : pointUpdates.keySet()) {
            for (PointData pointData : pointUpdates.get(pao)) {
                AssetAvailabilityPointDataTimes time = new AssetAvailabilityPointDataTimes(pao.getPaoId());
                time.setLastCommunicationTime(new Instant(pointData.getTimeStamp()));
                log.debug("Publishing asset avaiability {} info for PAO {}", time, pao);
                dynamicLcrCommunicationsDao.insertData(time);
            }
        }
    }

    /**
     * Takes a paoTypes and gets all of the tags that have a builtInAttribute for that paoType
     */
    private List<BuiltInAttribute> getAttributesForPaoType(PaoType paoType) {
         return paoDefinitionDao.getDefinedAttributes(paoType).stream()
                .map(attributeDefinition -> attributeDefinition.getAttribute())
                .collect(Collectors.toList());
    }

    /**
     * Returns a List of tags for all the BuiltInAttributes. 
     * If a built in attribute has no associated tag it will be ignored
     */
    private List<String> getTagsForAttributes(Iterable<BuiltInAttribute> attributes) {
        return StreamSupport.stream(attributes.spliterator(), false)
                .map(attribute -> {
                    MWChannel channel = MWChannel.getMWChannel(attribute);
                    return channel != null ? channel.getChannelId().toString() : null;
                })
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    private String buildTagString(List<String> tagList) {
        return StringUtils.join(tagList, ',');
    }
}

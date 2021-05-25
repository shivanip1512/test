package com.cannontech.dr.pxmw.service.impl.v1;

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
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesValueV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

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
    public Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds, Range<Instant> queryRange) {

        Map<PaoType, Set<LiteYukonPAObject>> paos = paoDao.getLiteYukonPaos(deviceIds).stream()
                .filter(pao -> pao.getPaoType().isCloudLcr())
                .collect(Collectors.groupingBy(pao -> pao.getPaoType(), Collectors.toSet()));
          
        Multimap<PaoIdentifier, PointData> receivedPoints = HashMultimap.create();
        for (PaoType type : paos.keySet()) {
            Set<BuiltInAttribute> attr = getAttributesForPaoType(type);
            log.info("Initiating read for type:{} devices: {} on attributes:{}", type, paos.get(type).size(), attr.size());
            receivedPoints.putAll(retrievePointData(paos.get(type), attr, queryRange));
        }

        log.debug("Retrieved point data:{} for devices:{}", receivedPoints.values().size(), receivedPoints.keySet().size());
        if (!receivedPoints.isEmpty()) {
            dispatchData.putValues(receivedPoints.values());
            updateAssetAvailability(receivedPoints);
        }
        return receivedPoints;
    }

    private Multimap<PaoIdentifier, PointData> retrievePointData(Iterable<LiteYukonPAObject> paos,
            Set<BuiltInAttribute> attribtues, Range<Instant> queryRange) {
        Map<Integer, LiteYukonPAObject> deviceIdToPao = StreamSupport.stream(paos.spliterator(), false)
                .collect(Collectors.toMap(LiteYukonPAObject::getYukonID, liteYukonPao -> liteYukonPao));
        BidiMap<Integer, String> deviceIdGuid = new DualHashBidiMap<Integer, String>(deviceDao.getGuids(deviceIdToPao.keySet()));

        Multimap<PaoIdentifier, PointData> newPaoPointMap = HashMultimap.create();
        List<PxMWTimeSeriesDeviceResultV1> timeSeriesResults = new ArrayList<PxMWTimeSeriesDeviceResultV1>();
        Set<String> tags = MWChannel.getTagsForAttributes(attribtues);
        List<List<PxMWTimeSeriesDeviceV1>> chunkedRequests = buildRequests(deviceIdGuid.values(), tags);
        for (List<PxMWTimeSeriesDeviceV1> request : chunkedRequests) {
            List<PxMWTimeSeriesDeviceResultV1> result = pxMWCommunicationService.getTimeSeriesValues(request, queryRange);
            timeSeriesResults.addAll(result);
        }

        for (PxMWTimeSeriesDeviceResultV1 deviceResult : timeSeriesResults) {
            Integer deviceId = deviceIdGuid.getKey(deviceResult.getDeviceId());
            if (deviceId == null) {
                continue;
            }
            LiteYukonPAObject device = deviceIdToPao.get(deviceId);
            for (PxMWTimeSeriesResultV1 result : deviceResult.getResults()) {
                try {
                    Integer tag = Integer.parseInt(result.getTag());
                    MWChannel mwChannel = MWChannel.getChannelLookup().get(tag);
                    if(!attribtues.contains(mwChannel.getBuiltInAttribute())) {
                        //Received point data we didn't ask for
                        continue;
                    }
                    for (PxMWTimeSeriesValueV1 value : result.getValues()) {
                        Double pointValue = parsePointValue(mwChannel, value, device, deviceResult.getDeviceId());
                        if (pointValue != null) {
                            PointData pointData = generatePointData(device, mwChannel, pointValue,
                                    value.getTimestamp(), deviceResult.getDeviceId());
                            if (pointData != null) {
                                newPaoPointMap.put(device.getPaoIdentifier(), pointData);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error parsing tag {} from API response", result.getTag(), e);
                }
            }
        }
        return newPaoPointMap;
    }

    /**
     * Parses the channel response. Null is returned if unable to parse.
     */
    private Double parsePointValue(MWChannel channel, PxMWTimeSeriesValueV1 value, LiteYukonPAObject device, String guid) {

        String pxReturnedValue = value.getValue();
        try {
            if (MWChannel.getBooleanChannels().contains(channel)) {
                if (pxReturnedValue.toLowerCase().equals("true") || pxReturnedValue.toLowerCase().equals("false")) {
                    return Boolean.parseBoolean(pxReturnedValue) ? Double.valueOf("1") : Double.valueOf("0");
                }
            } else if (MWChannel.getIntegerChannels().contains(channel) || MWChannel.getFloatChannels().contains(channel)) {
                // Raw point value received from PxMW
                Double pointValue = Double.parseDouble(pxReturnedValue);
                // Multiply by channel multiplier to convert to default UOM
                pointValue = pointValue * channel.getPointMultiplier();
                return pointValue;
            }
        } catch (Exception e) {
            // can't parse data ignore exception
        }

        log.error("Device Id:{} Name:{} Guid:{} Channel:{} cannot be parsed into point data. Discarding received value: {}",
                device.getLiteID(), device.getPaoName(), guid, channel, pxReturnedValue);
        return null;
    }
    
   private PointData generatePointData(LiteYukonPAObject device, MWChannel channel, double value, long time, String guid) {

        PointData pointData = new PointData();
        BuiltInAttribute attribute = channel.getBuiltInAttribute();
        try {
            pointData.setTime(Date.from(java.time.Instant.ofEpochSecond(time)));
        } catch (Exception e) {
            log.error(
                    "Device Id:{} Name:{} Guid:{} Attribute:{} can't parse timestamp for point data. Discarding received value:{}",
                    device.getLiteID(), device.getPaoName(), guid, attribute, time);
            return null;
        }
        boolean pointCreated = attributeService.createPointForAttribute(device, attribute);
        LitePoint point = attributeService.getPointForAttribute(device, attribute);
        if (pointCreated) {
            log.info(
                    "Device Id:{} Name:{} Guid:{} Attribute:{} Point Id:{} Point {} created.", device.getLiteID(),
                    device.getPaoName(), guid, attribute, point.getLiteID(), point.getPointName());
        }
        // Multiply by point multiplier (usually 1), can be overridden in point setup by the user
        value = value * point.getMultiplier();

        pointData.setId(point.getLiteID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(value);
        pointData.setType(point.getPointType());
        pointData.setTagsPointMustArchive(true);
  
        log.debug(
                "Device Id:{} Name:{} Guid:{} Channel:{} ChannelMultiplier:{} Attribute:{} Point Id:{} Point Name:{} point data created with value:{} using point multiplier:{} data:{}.",
                device.getLiteID(),
                device.getPaoName(),
                guid,
                channel,
                channel.getPointMultiplier(),
                attribute,
                point.getLiteID(),
                point.getPointName(),
                pointData.getValue(),
                point.getMultiplier(),
                pointData.getTimeStamp());
        
        return pointData;
    }

    /**
     * Helps optimize the requests that are built by taking a set of GUIDs and a set of 
     * tags that are being requested for those GUIDs and building the minimum number of requests
     */
    private List<List<PxMWTimeSeriesDeviceV1>> buildRequests(Collection<String> guids, Set<String> tagSet) {
        List<List<PxMWTimeSeriesDeviceV1>> chunkedRequests = new ArrayList<>();
        List<List<String>> chunkedTags = Lists.partition(new ArrayList<>(tagSet), 10);
        for (List<String> tagSubset : chunkedTags) {
            String tagCSV = buildTagString(tagSubset);
            List<PxMWTimeSeriesDeviceV1> workingRequest = new ArrayList<PxMWTimeSeriesDeviceV1>();
            for (String guid : guids) {
                workingRequest.add(new PxMWTimeSeriesDeviceV1(guid, tagCSV));
            }
            chunkedRequests.add(workingRequest);
        }
        return chunkedRequests;
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
    private Set<BuiltInAttribute> getAttributesForPaoType(PaoType paoType) {
         return paoDefinitionDao.getDefinedAttributes(paoType).stream()
                .map(attributeDefinition -> attributeDefinition.getAttribute())
                .collect(Collectors.toSet());
    }

    private String buildTagString(List<String> tagList) {
        return StringUtils.join(tagList, ',');
    }
}
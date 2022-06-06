package com.cannontech.dr.eatonCloud.service.impl.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoDao.InfoKey;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.eatonCloud.model.EatonCloudChannel;
import com.cannontech.dr.eatonCloud.model.EatonCloudEventStatus;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceResultV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesResultV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesValueV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class EatonCloudDataReadServiceImpl implements EatonCloudDataReadService {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudDataReadServiceImpl.class);

    @Autowired private AsyncDynamicDataSource dispatchData;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private ConfigurationSource configurationSource;

    @Override
    public Multimap<PaoIdentifier, PointData> collectDataForRead(Integer deviceId, Range<Instant> range) {
        return collectDataForRead(Set.of(deviceId), range, true, "SINGLE DEVICE");
    }

    @Override
    public Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds, Range<Instant> range, String debugReadType) {
        return collectDataForRead(deviceIds, range, false, debugReadType);
    }

    private Multimap<PaoIdentifier, PointData> collectDataForRead(Set<Integer> deviceIds, Range<Instant> range,
            boolean throwErrorIfFailed, String debugReadType) {
        Map<PaoType, Set<LiteYukonPAObject>> paos = paoDao.getLiteYukonPaos(deviceIds).stream()
                .filter(pao -> pao.getPaoType().isCloudLcr())
                .collect(Collectors.groupingBy(pao -> pao.getPaoType(), Collectors.toSet()));

        Multimap<PaoIdentifier, PointData> receivedPoints = HashMultimap.create();
        for (PaoType type : paos.keySet()) {
            Set<BuiltInAttribute> attr = getAttributesForPaoType(type);
            log.info("Initiating read ({}) for type:{} devices: {} on attributes:{}", debugReadType, type, paos.get(type).size(),
                    attr.size());
            receivedPoints.putAll(retrievePointData(paos.get(type), attr, range, throwErrorIfFailed));
        }

        log.debug("({}) Retrieved point data:{} for devices:{}", debugReadType, receivedPoints.values().size(),
                receivedPoints.keySet().size());
        if (!receivedPoints.isEmpty()) {
            dispatchData.putValues(receivedPoints.values());
        }
        return receivedPoints;
    }

    private Multimap<PaoIdentifier, PointData> retrievePointData(Iterable<LiteYukonPAObject> paos,
            Set<BuiltInAttribute> attribtues, Range<Instant> queryRange, boolean throwErrorIfFailed) {

        Map<Integer, LiteYukonPAObject> deviceIdToPao = StreamSupport.stream(paos.spliterator(), false)
                .collect(Collectors.toMap(LiteYukonPAObject::getYukonID, liteYukonPao -> liteYukonPao));
        BidiMap<Integer, String> deviceIdGuid = new DualHashBidiMap<Integer, String>(deviceDao.getGuids(deviceIdToPao.keySet()));


        List<EatonCloudTimeSeriesDeviceResultV1> timeSeriesResults = new ArrayList<EatonCloudTimeSeriesDeviceResultV1>();
        Set<String> tags = EatonCloudChannel.getTagsForAttributes(attribtues);
        List<EatonCloudTimeSeriesDeviceV1> chunkedRequests = configurationSource.getBoolean(MasterConfigBoolean.EATON_CLOUD_JOBS_TREND, false) ? 
                buildRequests(deviceIdGuid.values(), tags) : buildRequestsLegacy(deviceIdGuid.values(), tags);
        for (EatonCloudTimeSeriesDeviceV1 request : chunkedRequests) {
            try {
                List<EatonCloudTimeSeriesDeviceResultV1> result = eatonCloudCommunicationService
                        .getTimeSeriesValues(List.of(request), queryRange);
                timeSeriesResults.addAll(result);
            } catch (Exception e) {
                log.error("Guid:" + request.getDeviceGuid(), e);
                if(throwErrorIfFailed) {
                    throw e;
                }
            }
        }

        Multimap<PaoIdentifier, PointData> pointMap = HashMultimap.create();

        for (EatonCloudTimeSeriesDeviceResultV1 deviceResult : timeSeriesResults) {
            Integer deviceId = deviceIdGuid.getKey(deviceResult.getDeviceId());
            if (deviceId == null) {
                continue;
            }
            LiteYukonPAObject device = deviceIdToPao.get(deviceId);
            for (EatonCloudTimeSeriesResultV1 result : deviceResult.getResults()) {
                if (CollectionUtils.isEmpty(result.getValues())) {
                    continue;
                }
                try {
                    Integer tag = Integer.parseInt(result.getTag());
                    EatonCloudChannel mwChannel = EatonCloudChannel.getChannelLookup().get(tag);
                    if (mwChannel == EatonCloudChannel.EVENT_STATE) {
                        updateEventParticipation(device, result.getValues());
                    } else if (InfoKey.hasKey(mwChannel)) {
                        updatePaoInfo(InfoKey.getKey(mwChannel), device, result.getValues());
                    } else if (attribtues.contains(mwChannel.getBuiltInAttribute())) {
                        createPointData(pointMap, deviceResult.getDeviceId(), device, result, mwChannel);
                    }
                } catch (Exception e) {
                    log.error("Error parsing tag {} from API response", result.getTag(), e);
                }
            }
        }
        return pointMap;
    }

    private void createPointData(Multimap<PaoIdentifier, PointData> pointMap, String guid,
            LiteYukonPAObject device, EatonCloudTimeSeriesResultV1 result, EatonCloudChannel mwChannel) {
        for (EatonCloudTimeSeriesValueV1 value : result.getValues()) {
            Double pointValue = parsePointValue(mwChannel, value, device, guid);
            if (pointValue != null) {
                PointData pointData = generatePointData(device, mwChannel, pointValue,
                        value.getTimestamp(), guid);
                if (pointData != null) {
                    pointMap.put(device.getPaoIdentifier(), pointData);
                    AssetAvailabilityPointDataTimes time = new AssetAvailabilityPointDataTimes(device.getLiteID());
                    time.setLastCommunicationTime(new Instant(pointData.getTimeStamp()));
                    if (mwChannel.getRelayNumberByRuntime() != null && pointData.getValue() > 0) {
                        time.setRelayRuntime(mwChannel.getRelayNumberByRuntime(), new Instant(pointData.getTimeStamp()));
                        log.debug("Publishing asset availability {} info for PAO {} relay:{} point data value:{}", time, device,
                                mwChannel.getRelayNumberByRuntime(), pointData.getValue());
                    } else {
                        log.debug("Publishing asset availability {} info for PAO {}", time, device);
                    }
                    dynamicLcrCommunicationsDao.insertData(time);
                }
            }
        }
    }

    private void updatePaoInfo(InfoKey key, LiteYukonPAObject device, List<EatonCloudTimeSeriesValueV1> values) {
        if (!values.isEmpty()) {
            EatonCloudTimeSeriesValueV1 value = Collections.max(values,
                    Comparator.comparing(v -> new Instant(v.getTimestamp() * 1000)));
            paoDao.savePaoInfo(device.getLiteID(), key, value.getValue(),
                    new Instant(value.getTimestamp() * 1000), YukonUserContext.system.getYukonUser());
        }
    }

    private void updateEventParticipation( LiteYukonPAObject device, List<EatonCloudTimeSeriesValueV1> values) {
        /*
         * 123,2 - first event starts
         * 123,2;124,2 - second event starts
         * 123,3;124,2 -first event complete
         * 124,3 - second event complete
         */
        values.forEach(value -> {
            splitString(value.getValue(), ";").forEach(v -> {
                try {
                    List<String> resultList = splitString(v, ",");
                    String externalEventId = resultList.get(0);
                    int statusId = Integer.valueOf(resultList.get(1));
                    EatonCloudEventStatus cloudStatus = EatonCloudEventStatus.getById(statusId);
                    ControlEventDeviceStatus controlEventStatus = ControlEventDeviceStatus.getDeviceStatus(cloudStatus);
                    if (controlEventStatus != null) {
                        recentEventParticipationDao.updateDeviceControlEvent(externalEventId, device.getLiteID(),
                                controlEventStatus,
                                new Instant(value.getTimestamp() * 1000), null,
                                null);
                    }
                } catch (Exception e) {
                    log.error("Unable to parse value:{} to update device participation status for device:{}({})", v,
                            device.getPaoName(), device.getLiteID());
                }
            });
        });
    }

    private List<String> splitString(String value, String on) {
        return Splitter.on(on)
                .trimResults()
                .omitEmptyStrings()
                .splitToList(value);
    }

    /**
     * Parses the channel response. Null is returned if unable to parse.
     */
    private Double parsePointValue(EatonCloudChannel channel, EatonCloudTimeSeriesValueV1 value, LiteYukonPAObject device, String guid) {

        String pxReturnedValue = value.getValue();
        try {
            if (EatonCloudChannel.getBooleanChannels().contains(channel)) {
                if (pxReturnedValue.toLowerCase().equals("true") || pxReturnedValue.toLowerCase().equals("false")) {
                    return Boolean.parseBoolean(pxReturnedValue) ? Double.valueOf("1") : Double.valueOf("0");
                }
            } else if (EatonCloudChannel.getIntegerChannels().contains(channel) || EatonCloudChannel.getFloatChannels().contains(channel)) {
                // Raw point value received from Eaton Cloud
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
    
   private PointData generatePointData(LiteYukonPAObject device, EatonCloudChannel channel, double value, long time, String guid) {

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
        value = point.getMultiplier() == null ? value : value * point.getMultiplier();

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
    private List<EatonCloudTimeSeriesDeviceV1> buildRequestsLegacy(Collection<String> guids, Set<String> tagSet) {
        List<EatonCloudTimeSeriesDeviceV1> chunkedRequests = new ArrayList<>();
        List<List<String>> chunkedTags = Lists.partition(new ArrayList<>(tagSet), 10);
        for (List<String> tagSubset : chunkedTags) {
            String tagCSV = buildTagString(tagSubset);
            for (String guid : guids) {
                chunkedRequests.add(new EatonCloudTimeSeriesDeviceV1(guid, tagCSV));
            }
        }
        return chunkedRequests;
    }
    
    /**
     * Helps optimize the requests that are built by taking a set of GUIDs and a set of 
     * tags that are being requested for those GUIDs and building the minimum number of requests
     */
    private List<EatonCloudTimeSeriesDeviceV1> buildRequests(Collection<String> guids, Set<String> tagSet) {
        List<EatonCloudTimeSeriesDeviceV1> chunkedRequests = new ArrayList<>();
        List<List<String>> chunkedTags = Lists.partition(new ArrayList<>(tagSet), 1000);
        for (List<String> tagSubset : chunkedTags) {
            String tagCSV = buildTagString(tagSubset);
            for (String guid : guids) {
                chunkedRequests.add(new EatonCloudTimeSeriesDeviceV1(guid, tagCSV));
            }
        }
        return chunkedRequests;
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

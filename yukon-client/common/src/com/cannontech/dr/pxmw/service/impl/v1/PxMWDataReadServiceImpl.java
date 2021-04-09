package com.cannontech.dr.pxmw.service.impl.v1;

import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.buf.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
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
        //5. Update recent participation
        //6. Update asset availability
        
        log.info("Data read called for device IDs: {}", deviceIds);
        
        Multimap<PaoIdentifier, PointData> recievedPoints = retrievePointData(deviceIds);
        dispatchData.putValues(recievedPoints.values());
        
        updateAssetAvaiability(recievedPoints);
        
        return recievedPoints;
    }

    private Multimap<PaoIdentifier, PointData> retrievePointData(Iterable<Integer> deviceIds) {
        Multimap<PaoIdentifier, PointData> newPaoPointMap = HashMultimap.create();
        BidiMap<Integer, String> deviceIdGuid = new DualHashBidiMap<Integer, String>(deviceDao.getGuids(deviceIds));
        Map<Integer, LiteYukonPAObject> deviceIdToPao = new HashMap<Integer, LiteYukonPAObject>();
        List<PxMWTimeSeriesDeviceV1> timeSeriesDevices = new ArrayList<PxMWTimeSeriesDeviceV1>();

        for (LiteYukonPAObject pao : paoDao.getLiteYukonPaos(deviceIds)) {
            deviceIdToPao.put(pao.getPaoIdentifier().getPaoId(), pao);

            List<String> tags = paoDefinitionDao.getDefinedAttributes(pao.getPaoType()).stream()
                    .map(attribute -> MWChannel.getAttributeChannelLookup().get(attribute.getAttribute()).getChannelId().toString())
                    .collect(Collectors.toList());
            log.debug("Updating tags: {}, for PAO: {}", tags, pao);

            String guid = deviceIdGuid.get(pao.getPaoIdentifier().getPaoId());
            PxMWTimeSeriesDeviceV1 pxmwTimeSeriesDevice = new PxMWTimeSeriesDeviceV1(guid, buildTagString(tags));
            timeSeriesDevices.add(pxmwTimeSeriesDevice);
        }

        List<PxMWTimeSeriesDeviceResultV1> devices = chunkAndProcessRequests(timeSeriesDevices);
        for (PxMWTimeSeriesDeviceResultV1 deviceResult : devices) {
            Integer deviceId = deviceIdGuid.getKey(deviceResult.getDeviceId());
            for (PxMWTimeSeriesResultV1 result : deviceResult.getResults()) {
                String tag = result.getTag();
                MWChannel mwChannel = MWChannel.getChannelLookup().get(Integer.parseInt(tag));
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

        return newPaoPointMap;
    }

    private String buildTagString(List<String> tagList) {
        return StringUtils.join(tagList, ',');
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
            }
            pointValue = Boolean.parseBoolean(pxReturnedValue) ? 1 : 0;
            pointData.setValue(pointValue);
        } else if (MWChannel.getIntegerChannels().contains(channel)) {
            pointValue = Integer.parseInt(pxReturnedValue);
        } else if (MWChannel.getFloatChannels().contains(channel)) {
            pointValue = Double.parseDouble(pxReturnedValue);
        } else {
            log.info("Channel {} is a type that cannot be parsed into point data. Disarding received value: {}", pxReturnedValue);
            return null;
        }

        pointData.setValue(pointValue);
        return pointData;
    }

    /**
     * Chunks requests into 10 devices at a time, sends individually, then assembles the responses.
     * Returns as many non-error responses as possible, logging the error responses
     */
    private List<PxMWTimeSeriesDeviceResultV1> chunkAndProcessRequests(List<PxMWTimeSeriesDeviceV1> requests) {
        List<PxMWTimeSeriesDeviceResultV1> responses = new ArrayList<>();

        List<List<PxMWTimeSeriesDeviceV1>> partitionedRequests = Lists.partition(requests, 10);
        for (List<PxMWTimeSeriesDeviceV1> request : partitionedRequests) {
            try {
                responses.addAll(pxMWCommunicationService.getTimeSeriesValues(request, getQueryRange()).getMsg());
            } catch (PxMWCommunicationExceptionV1 e) {
                log.error("An error occurred processing requests: {}", request);
            }
        }

        return responses;
    }

    /**
     * Currently just returns a range of the last week
     */
    private Range<Instant> getQueryRange() {
        Instant queryEndTime = new Instant();
        Instant queryStartTime = new Instant(System.currentTimeMillis() - (3600 * 24 * 7 * 1000)); // previous week
        return new Range<Instant>(queryStartTime, false, queryEndTime, true);
    }
    
    private void updateAssetAvaiability(Multimap<PaoIdentifier, PointData> pointUpdates) {
        for (PaoIdentifier pao : pointUpdates.keySet()) {
            for (PointData pointData : pointUpdates.get(pao)) {
                AssetAvailabilityPointDataTimes time = new AssetAvailabilityPointDataTimes(pao.getPaoId());
                time.setLastCommunicationTime(new Instant(pointData.getTimeStamp()));
                dynamicLcrCommunicationsDao.insertData(time);
            }
        }
    }

}

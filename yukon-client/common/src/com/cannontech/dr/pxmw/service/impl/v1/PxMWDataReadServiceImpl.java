package com.cannontech.dr.pxmw.service.impl.v1;

import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.tomcat.util.buf.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.pxmw.model.MWChannel;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesValueV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;

public class PxMWDataReadServiceImpl implements PxMWDataReadService {

    SimpleDateFormat dateFormat = new SimpleDateFormat();

    @Autowired private AsyncDynamicDataSource dispatchData;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PxMWCommunicationServiceV1 pxMWCommunicationService;

    @Override
    public void collectDataForRead(Set<Integer> deviceIds) {
        //1. Get GUID
        //2. Get all tags for device
        //3. Ask Johns thing for data
        //4. Update data in dispatch
        //5. Update recent participation
        //6. Update asset availability
        
        Multimap<PaoIdentifier, PointData> recievedPoints = retrievePointData(deviceIds);
        dispatchData.putValues(recievedPoints.values());
    }
    
    private Multimap<PaoIdentifier, PointData> retrievePointData(Iterable<Integer> deviceIds) {
        Multimap<PaoIdentifier, PointData> newPaoPointMap = HashMultimap.create();
        BidiMap<Integer, String> deviceIdGuid = new DualHashBidiMap<Integer, String>(deviceDao.getGuids(deviceIds));
        Map<Integer, LiteYukonPAObject> deviceIdToPao = new HashMap<Integer, LiteYukonPAObject>();
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaos(deviceIds);
        List<PxMWTimeSeriesDeviceV1> timeSeriesDevices = new ArrayList<PxMWTimeSeriesDeviceV1>();

        for (LiteYukonPAObject pao : paos) {
            deviceIdToPao.put(pao.getPaoIdentifier().getPaoId(), pao);
            
            List<String> tags = paoDefinitionDao.getDefinedAttributes(pao.getPaoType()).stream()
                    .map(attribute -> MWChannel.getAttributeChannelLookup().get(attribute.getAttribute()).getChannelId().toString())
                    .collect(Collectors.toList());
            
            String guid = deviceIdGuid.get(pao.getPaoIdentifier().getPaoId());
            PxMWTimeSeriesDeviceV1 pxmwTimeSeriesDevice = new PxMWTimeSeriesDeviceV1(guid, buildTagString(tags));
            timeSeriesDevices.add(pxmwTimeSeriesDevice);
        }

        Instant queryEndTime = new Instant();
        Instant queryStartTime = new Instant(System.currentTimeMillis() - (3600 * 24 * 7 * 1000)); // previous week
        Range<Instant> queryRange = new Range<Instant>(queryStartTime, false, queryEndTime, true);

        PxMWTimeSeriesDataResponseV1 response = pxMWCommunicationService.getTimeSeriesValues(timeSeriesDevices, queryRange);
        
        List<PxMWTimeSeriesDeviceResultV1> devices = response.getMsg();
        for (PxMWTimeSeriesDeviceResultV1 deviceResult : devices) {
            Integer deviceId = deviceIdGuid.getKey(deviceResult.getDeviceId());
            for (PxMWTimeSeriesResultV1 result : deviceResult.getResults()) {
                String tag = result.getTag();
                MWChannel mwChannel = MWChannel.getChannelLookup().get(Integer.parseInt(tag));
                LitePoint holder = attributeService.findPointForAttribute(deviceIdToPao.get(deviceId), mwChannel.getBuiltInAttribute());
                for (PxMWTimeSeriesValueV1 value : result.getValues()) {
                    PointData newPoint = parsePointData(mwChannel, value);
                    newPoint.setId(holder.getPointID());
                    newPoint.setPointQuality(PointQuality.Normal);
                    newPaoPointMap.put(deviceIdToPao.get(deviceId).getPaoIdentifier(), newPoint);
                }
            }
        }
        
        return newPaoPointMap;
    }

    private String buildTagString(List<String> tagList) {
        return StringUtils.join(tagList, ',');
    }
    
    private PointData parsePointData(MWChannel channel, PxMWTimeSeriesValueV1 value) {
        PointData pointData = new PointData();
        pointData.setTime(new Date(value.getTimestamp()));
        String pxReturnedValue = value.getValue();
        double pointValue;
        
        if (MWChannel.getBooleanChannels().contains(channel)) {
            pointValue = Boolean.parseBoolean(pxReturnedValue) ? 1 : 0;
            pointData.setValue(pointValue);
        } else if (MWChannel.getIntegerChannels().contains(channel)) {
            pointValue = Integer.parseInt(pxReturnedValue);
        } else if (MWChannel.getFloatChannels().contains(channel)) {
            pointValue = Double.parseDouble(pxReturnedValue);
        } else {
            throw new IllegalArgumentException();
        }
        
        pointData.setValue(pointValue);
        return pointData;
    }

}

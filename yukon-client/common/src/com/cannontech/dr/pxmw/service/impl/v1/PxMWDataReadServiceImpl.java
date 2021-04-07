package com.cannontech.dr.pxmw.service.impl.v1;

import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.message.dispatch.message.PointData;

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
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.pxmw.model.MWChannel;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;

public class PxMWDataReadServiceImpl implements PxMWDataReadService {

    SimpleDateFormat dateFormat = new SimpleDateFormat();

    @Autowired private AsyncDynamicDataSource dispatchData;
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
        BidiMap<Integer, String> deviceIdGuid = new DualHashBidiMap<Integer, String>(deviceDao.getGuids(deviceIds));
        Map<Integer, LiteYukonPAObject> deviceToPao = new HashMap<Integer, LiteYukonPAObject>();
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaos(deviceIds);
        Map<PaoType, List<BuiltInAttribute>> paoTypeToAttributes = new HashMap<PaoType, List<BuiltInAttribute>>();
        Map<Integer, Map<BuiltInAttribute, Integer>> deviceToAttributeToPointId = new HashMap<Integer, Map<BuiltInAttribute, Integer>>();
        Set<PaoType> paoTypes = new HashSet<PaoType>();
        Set<Integer> pointIds = new HashSet<Integer>();
        List<PxMWTimeSeriesDeviceV1> timeSeriesDevice = new ArrayList<PxMWTimeSeriesDeviceV1>();

        for (LiteYukonPAObject pao : paos) {
            int paoId = pao.getPaoIdentifier().getPaoId();
            deviceToPao.put(paoId, pao);
            paoTypes.add(pao.getPaoType());
            for (AttributeDefinition attributeDefinition : paoDefinitionDao.getDefinedAttributes(pao.getPaoType())) {
                BuiltInAttribute builtInAttribute = attributeDefinition.getAttribute();
                Integer pointId = attributeDefinition.getPointId(pao);
                PointType pointType = attributeDefinition.getPointTemplate().getPointIdentifier().getPointType();
                if (!deviceToAttributeToPointId.containsKey(paoId)) {
                    deviceToAttributeToPointId.put(paoId, new HashMap<BuiltInAttribute, Integer>());
                }
                deviceToAttributeToPointId.get(paoId).put(builtInAttribute, pointId);
                pointIds.add(pointId);
            }
        }

        Date startTime = new Date();
        Set<? extends PointValueQualityHolder> pointDatas = dispatchData.getPointValues(pointIds);
        if (pointDatas.isEmpty()) {
            startTime = new Date(System.currentTimeMillis() - (3600 * 24 * 1000));
        }
        for (PointValueQualityHolder pointData : pointDatas) {
            Date pointDate = pointData.getPointDataTimeStamp();
            if (pointDate.after(startTime)) {
                startTime = pointDate;
            }
        }
        Instant queryEndTime = new Instant();
        Instant queryStartTime = new Instant(startTime);

        for (Integer deviceId : deviceIds) {
            List<String> tags = deviceToAttributeToPointId.get(deviceId).keySet().stream()
                    .map(attribute -> MWChannel.getAttributeChannelLookup().get(attribute).getChannelId().toString())
                    .collect(Collectors.toList());
            String guid = deviceIdGuid.get(deviceId);
            PxMWTimeSeriesDeviceV1 pxmwTimeSeriesDevice = new PxMWTimeSeriesDeviceV1(guid, tags);
            timeSeriesDevice.add(pxmwTimeSeriesDevice);
        }

        
        Range<Instant> queryRange = new Range<Instant>(queryStartTime, false, queryEndTime, true);
        PxMWTimeSeriesDataResponseV1 response = pxMWCommunicationService.getTimeSeriesValues(timeSeriesDevice, queryRange);
        
        List<PxMWTimeSeriesDeviceResultV1> devices = response.getMsg();
        for (PxMWTimeSeriesDeviceResultV1 deviceResult : devices) {
            Integer deviceId = deviceIdGuid.getKey(deviceResult.getDeviceId());
            for (PxMWTimeSeriesResultV1 result : deviceResult.getResults()) {
                String tag = result.getTag();
                BuiltInAttribute attribute = MWChannel.getChannelLookup().get(Integer.parseInt(tag)).getBuiltInAttribute();
                Integer pointId = deviceToAttributeToPointId.get(deviceId).get(attribute);
                PointData pointData = new PointData();
                pointData.setId(pointId);
                pointData.setPointQuality(PointQuality.Normal);
                pointData.setType();
                pointData.setTagsPointMustArchive(true);
            }
        }
        
    }

}

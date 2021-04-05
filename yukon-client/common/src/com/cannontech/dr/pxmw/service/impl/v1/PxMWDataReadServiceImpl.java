package com.cannontech.dr.pxmw.service.impl.v1;

import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jfree.util.Log;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
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
    public void collectDataForRead(int deviceId) {
        //1. Get GUID
        //2. Get all tags for device
        //3. Ask Johns thing for data
        //4. Update data in dispatch
        //5. Update recent participation
        //6. Update asset availability
        String deviceGuid = deviceDao.getGuid(deviceId);
        YukonPao pao = paoDao.getYukonPao(deviceId);
        Map<BuiltInAttribute, Integer> attributeToPoint = new HashMap<BuiltInAttribute, Integer>();
        for (AttributeDefinition attributeDefinition : paoDefinitionDao.getDefinedAttributes(pao.getPaoIdentifier().getPaoType())) {
            BuiltInAttribute builtInAttribute = attributeDefinition.getAttribute();
            Integer pointId = attributeDefinition.getPointId(pao);
            attributeToPoint.put(builtInAttribute, pointId);
        }

        Date startTime = new Date();

        Set<? extends PointValueQualityHolder> pointDatas = dispatchData.getPointValues(new HashSet<Integer>(attributeToPoint.values()));
        if (pointDatas.isEmpty()) {
            try {
                startTime = dateFormat.parse("2010-01-01T00:00:00-06:00"); // Date is arbitrary in the past, intended to get all data if none exists
            } catch (ParseException e) {
                Log.error(e);
            }
        }
        for (PointValueQualityHolder pointData : pointDatas) {
            Date pointDate = pointData.getPointDataTimeStamp();
            if (pointDate.before(startTime)) {
                startTime = pointDate;
            }
        }

        Instant queryEndTime = new Instant();
        Instant queryStartTime = new Instant(startTime);

        List<String> tags = attributeToPoint.keySet().stream()
                .map(attribute -> MWChannel.getAttributeChannelLookup().get(attribute).getChannelId().toString())
                .collect(Collectors.toList());

        List<PxMWTimeSeriesDeviceV1> timeSeriesDevice = Arrays.asList(new PxMWTimeSeriesDeviceV1(deviceGuid, tags));
        Range<Instant> queryRange = new Range<Instant>(queryStartTime, false, queryEndTime, true);
        PxMWTimeSeriesDataResponseV1 response = pxMWCommunicationService.getTimeSeriesValues(timeSeriesDevice, queryRange);
        
        List<PxMWTimeSeriesDeviceResultV1> devices = response.getMsg();
        for (PxMWTimeSeriesDeviceResultV1 deviceResult : devices) {
            for (PxMWTimeSeriesResultV1 result : deviceResult.getResults()) {
                String tag = result.getTag();
                BuiltInAttribute attribute = MWChannel.getChannelLookup().get(Integer.parseInt(tag)).getBuiltInAttribute();
                
            }
        }
        
    }

}

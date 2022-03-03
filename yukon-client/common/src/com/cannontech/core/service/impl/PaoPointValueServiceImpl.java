package com.cannontech.core.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.paoPointValue.model.MeterPointValue;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PaoPointValueServiceImpl implements PaoPointValueService {

    @Autowired AttributeService attributeService;
    @Autowired MeterDao meterDao;
    @Autowired RfnDeviceDao rfnDeviceDao;
    @Autowired PointDao pointDao;
    @Autowired PointFormattingService pointFormattingService;
    @Autowired RawPointHistoryDao rawPointHistoryDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private static Comparator<MeterPointValue> getMeterPointValueComparator() {
        return MeterPointValue.getPaoIdOrdering().compound(MeterPointValue.getPointIdOrdering())
            .compound(MeterPointValue.getDateOrdering());
    }
    
    @Override
    public <P extends YukonPao> List<MeterPointValue> getMeterPointValues(Iterable<P> devices,
                                                                                Set<? extends Attribute> attributes,
                                                                                ReadableRange<Instant> range,
                                                                                Integer maxRows,
                                                                                boolean includeDisabledPaos,
                                                                                Set<Integer> discludedPointStateValues,
                                                                                YukonUserContext userContext) {
        
        Set<? extends Attribute> attributeSet = Sets.newHashSet(attributes);
        List<? extends PointValueHolder> pointData = null;
        Map<PaoPointIdentifier, PointInfo> paoPointInfoMap;
        Map<Integer, PointInfo> pointIdsToPointInfos;
        if (maxRows != null) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedAttributeDatas =
                rawPointHistoryDao.getLimitedAttributeData(devices,
                                                           attributes,
                                                           range,
                                                           maxRows,
                                                           !includeDisabledPaos,
                                                           Order.REVERSE,
                                                           null);
            pointData = Lists.newArrayList(limitedAttributeDatas.values());

            Set<Integer> pointIdsSet = Sets.newHashSet();
            for (PointValueQualityHolder pvqh : limitedAttributeDatas.values()) {
                pointIdsSet.add(pvqh.getId());
            }

            pointIdsToPointInfos = pointDao.getPointInfoByPointIds(pointIdsSet);
            Map<PointValueQualityHolder, PointInfo> pvqhPointInfoMap =
                Maps.newHashMapWithExpectedSize(limitedAttributeDatas.values().size());
            for (PointValueQualityHolder pvqh : limitedAttributeDatas.values()) {
                pvqhPointInfoMap.put(pvqh, pointIdsToPointInfos.get(pvqh.getId()));
            }

            paoPointInfoMap = Maps.newHashMap();
            for (Entry<PaoIdentifier, PointValueQualityHolder> pvqh : limitedAttributeDatas.entries()) {
                PointInfo pointInfo = pvqhPointInfoMap.get(pvqh.getValue());
                PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(pvqh.getKey(), pointInfo.getPointIdentifier());
                paoPointInfoMap.put(paoPointIdentifier, pointInfo);
            }
        } else {
            // get the PaoPointIdentifiers for the devices & attributes
            List<PaoMultiPointIdentifier> paoMultiPointIdentifiersForAttributes = attributeService
                    .findPaoMultiPointIdentifiersForAttributes(devices, attributeSet);
            Set<PaoPointIdentifier> paoPointIdentifiers = Sets.newHashSet();
            for (PaoMultiPointIdentifier paoMultiPointIdentifier : paoMultiPointIdentifiersForAttributes) {
                paoPointIdentifiers.addAll(paoMultiPointIdentifier.getPaoPointIdentifiers());
            }

            // get the pointIds for the PaoPointIdentifiers
            paoPointInfoMap = pointDao.getPointInfoById(paoPointIdentifiers);
            Set<Integer> pointIdsSet = Sets.newHashSet();
            pointIdsToPointInfos = Maps.newHashMapWithExpectedSize(paoPointInfoMap.size());
            for (PointInfo pointInfo : paoPointInfoMap.values()) {
                pointIdsSet.add(pointInfo.getPointId());
                pointIdsToPointInfos.put(pointInfo.getPointId(), pointInfo);
            }

            pointData = rawPointHistoryDao.getPointData(pointIdsSet,
                                                        range,
                                                        !includeDisabledPaos,
                                                        Order.FORWARD);
        }

        Map<Integer, PaoPointIdentifier> pointIdPaoPointIdMap = Maps.newHashMap();
        Set<PaoIdentifier> retrievedPaoIds = Sets.newHashSet();
        for (PointValueHolder pointValueHolder : pointData) {
            for (Entry<PaoPointIdentifier, PointInfo> entry : paoPointInfoMap.entrySet()) {
                if (entry.getValue().getPointId() == pointValueHolder.getId()) {
                    pointIdPaoPointIdMap.put(pointValueHolder.getId(), entry.getKey());
                    retrievedPaoIds.add(entry.getKey().getPaoIdentifier());
                    break;
                }
            }
        }

        List<YukonMeter> meters = meterDao.getMetersForYukonPaos(retrievedPaoIds);
        Map<PaoIdentifier, YukonMeter> paoPointIdMeterMap = Maps.newHashMap();
        for (YukonMeter meter : meters) {
            for (Entry<PaoPointIdentifier, PointInfo> entry : paoPointInfoMap.entrySet()) {
                if (meter.getDeviceId() == entry.getKey().getPaoIdentifier().getPaoId()) {
                    paoPointIdMeterMap.put(entry.getKey().getPaoIdentifier(), meter);
                    break;
                }
            }
        }
        
        Map<PaoIdentifier, RfnDevice> paoPointIdRfnMap = Maps.newHashMap();
        if (meters.size() != retrievedPaoIds.size()) {
            Set<Integer> rfnDeviceIds = Sets.newHashSet();
            rfnDeviceIds.addAll(retrievedPaoIds.stream().map(x -> x.getPaoId()).collect(Collectors.toSet()));
            Set<Integer> meterIds = meters.stream().map(x -> x.getPaoIdentifier().getPaoId()).collect(Collectors.toSet());
            rfnDeviceIds.removeAll(meterIds);

            List<RfnDevice> rfnDevices = rfnDeviceDao.getDevicesByPaoIds(rfnDeviceIds);
            for (RfnDevice rfnDevice : rfnDevices) {
                if (paoDefinitionDao.isTagSupported(rfnDevice.getPaoIdentifier().getPaoType(), PaoTag.RFN_EVENTS)) {
                    paoPointIdRfnMap.put(rfnDevice.getPaoIdentifier(), rfnDevice);
                }
            }
        }
        

        // put it all together into our return list
        List<MeterPointValue> pointValues = Lists.newArrayListWithExpectedSize(pointData.size());
        for (PointValueHolder pointValueHolder : pointData) {

            PaoPointIdentifier paoPointIdentifier = pointIdPaoPointIdMap.get(pointValueHolder.getId());
            PointInfo pointInfo = pointIdsToPointInfos.get(pointValueHolder.getId());
            YukonMeter meter = paoPointIdMeterMap.get(paoPointIdentifier.getPaoIdentifier());
            MeterPointValue meterPointValue = null;
            
            if (meter != null) {
                meterPointValue = new MeterPointValue(meter, paoPointIdentifier, pointValueHolder, pointInfo.getName());
            } else {
                RfnDevice relay = paoPointIdRfnMap.get(paoPointIdentifier.getPaoIdentifier());
                if (relay != null) {
                    meterPointValue = new MeterPointValue(relay, paoPointIdentifier, pointValueHolder, pointInfo.getName());
                }
            }
 
            if (meterPointValue != null) {
                if (!CollectionUtils.isEmpty(discludedPointStateValues)) {
                    String valueString = meterPointValue.getFormattedRawValue(pointFormattingService, userContext);
                    if (discludedPointStateValues.contains(Double.valueOf(valueString).intValue())) {
                        continue;
                    }
                }
                pointValues.add(meterPointValue);
            }
            
        }

        Collections.sort(pointValues, getMeterPointValueComparator());
        return pointValues;
    }
}

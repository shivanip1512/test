package com.cannontech.core.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.paoPointValue.model.PaoPointValue;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class PaoPointValueServiceImpl implements PaoPointValueService {

    @Autowired AttributeService attributeService;
    @Autowired MeterDao meterDao;
    @Autowired PointDao pointDao;
    @Autowired PointFormattingService pointFormattingService;
    @Autowired RawPointHistoryDao rawPointHistoryDao;
    
    private static Comparator<PaoPointValue> getPaoPointValueComparator() {
        Ordering<Integer> naturalIntOrdering = Ordering.natural().nullsLast();
        Ordering<PaoPointValue> paoIdOrdering = naturalIntOrdering
                .onResultOf(new Function<PaoPointValue, Integer>() {
                    @Override
                    public Integer apply(PaoPointValue from) {
                        return from.getPaoIdentifier().getPaoId();
                    }
                });
        Ordering<PaoPointValue> pointIdOrdering = naturalIntOrdering
                .onResultOf(new Function<PaoPointValue, Integer>() {
                    @Override
                    public Integer apply(PaoPointValue from) {
                        return from.getPointValueHolder().getId();
                    }
                });
        Ordering<PaoPointValue> dateOrdering = Ordering.natural().nullsLast()
            .onResultOf(new Function<PaoPointValue, Date>() {
                @Override
                public Date apply(PaoPointValue from) {
                    return from.getPointValueHolder().getPointDataTimeStamp();
                }
            });
        return paoIdOrdering.compound(pointIdOrdering).compound(dateOrdering);
    }
    
    @Override
    public <P extends YukonPao> List<PaoPointValue> getPaoPointValuesForMeters(Iterable<P> devices,
                                                                               Set<Attribute> attributes,
                                                                               Instant from,
                                                                               Instant to,
                                                                               Integer maxRows,
                                                                               boolean includeDisabledPaos,
                                                                               Set<String> discludedPointStateValues,
                                                                               YukonUserContext userContext) {

        Set<? extends Attribute> attributeSet = Sets.newHashSet(attributes);
        List<? extends PointValueHolder> pointData = null;
        Map<PaoPointIdentifier, PointInfo> paoPointInfoMap;
        Set<PointInfo> pointInfos;
        if (maxRows != null) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> limitedAttributeDatas =
                rawPointHistoryDao.getLimitedAttributeData(devices,
                                                           attributes,
                                                           from != null ? from.toDate() : null,
                                                           to != null ? to.toDate() : null,
                                                           maxRows,
                                                           !includeDisabledPaos,
                                                           Clusivity.INCLUSIVE_INCLUSIVE,
                                                           Order.REVERSE);
            pointData = Lists.newArrayList(limitedAttributeDatas.values());

            Set<Integer> pointIdsSet = Sets.newHashSet();
            for (PointValueQualityHolder pvqh : limitedAttributeDatas.values()) {
                pointIdsSet.add(pvqh.getId());
            }

            pointInfos = Sets.newHashSet(pointDao.getPointInfoByPointIds(pointIdsSet));
            Map<PointValueQualityHolder, PointInfo> pvqhPointInfoMap =
                Maps.newHashMapWithExpectedSize(limitedAttributeDatas.values().size());
            for (PointValueQualityHolder pvqh : limitedAttributeDatas.values()) {
                for (PointInfo pointInfo : pointInfos) {
                    if (pvqh.getId() == pointInfo.getPointId()) {
                        pvqhPointInfoMap.put(pvqh, pointInfo);
                        break;
                    }
                }
            }

            paoPointInfoMap = Maps.newHashMap();
            for (Entry<PaoIdentifier, PointValueQualityHolder> pvqh : limitedAttributeDatas.entries()) {
                PointInfo pointInfo = pvqhPointInfoMap.get(pvqh.getValue());
                PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(pvqh.getKey(), pointInfo.getPointIdentifier());
                paoPointInfoMap.put(paoPointIdentifier, pointInfo);
            }

        } else {
            // get the PaoPointIdentifiers for the devices & attributes
            List<PaoMultiPointIdentifier> paoMultiPointIdentifiersForAttributes =
                attributeService.findPaoMultiPointIdentifiersForAttributes(devices, attributeSet, false, false);
            Set<PaoPointIdentifier> paoPointIdentifiers = Sets.newHashSet();
            for (PaoMultiPointIdentifier paoMultiPointIdentifier : paoMultiPointIdentifiersForAttributes) {
                paoPointIdentifiers.addAll(paoMultiPointIdentifier.getPaoPointIdentifiers());
            }

            // get the pointIds for the PaoPointIdentifiers
            paoPointInfoMap = pointDao.getPointInfoById(paoPointIdentifiers);
            pointInfos = Sets.newHashSet(paoPointInfoMap.values());
            Set<Integer> pointIdsSet = Sets.newHashSet();
            for (PointInfo pointInfo : paoPointInfoMap.values()) {
                pointIdsSet.add(pointInfo.getPointId());
            }

            pointData = rawPointHistoryDao.getPointData(pointIdsSet,
                                                        from != null ? from.toDate() : null,
                                                        to != null ? to.toDate() : null,
                                                        !includeDisabledPaos,
                                                        Clusivity.INCLUSIVE_INCLUSIVE,
                                                        Order.FORWARD);
        }

        Map<Integer, PointInfo> pointIdPointInfoMap = Maps.newHashMap();
        Map<Integer, PaoPointIdentifier> pointIdPaoPointIdMap = Maps.newHashMap();
        Set<PaoIdentifier> retrievedPaoIds = Sets.newHashSet();
        for (PointValueHolder pointValueHolder : pointData) {
            for (PointInfo pointInfo : pointInfos) {
                if (pointInfo.getPointId() == pointValueHolder.getId()) {
                    pointIdPointInfoMap.put(pointValueHolder.getId(), pointInfo);
                    break;
                }
            }
            for (Entry<PaoPointIdentifier, PointInfo> entry : paoPointInfoMap.entrySet()) {
                if (entry.getValue().getPointId() == pointValueHolder.getId()) {
                    pointIdPaoPointIdMap.put(pointValueHolder.getId(), entry.getKey());
                    retrievedPaoIds.add(entry.getKey().getPaoIdentifier());
                    break;
                }
            }
        }

        List<Meter> meters = meterDao.getMetersForYukonPaos(retrievedPaoIds);
        Map<PaoIdentifier, Meter> paoPointIdMeterMap = Maps.newHashMap();
        for (Meter meter : meters) {
            for (Entry<PaoPointIdentifier, PointInfo> entry : paoPointInfoMap.entrySet()) {
                if (meter.getDeviceId() == entry.getKey().getPaoIdentifier().getPaoId()) {
                    paoPointIdMeterMap.put(entry.getKey().getPaoIdentifier(), meter);
                    break;
                }
            }
        }

        // put it all together into our return list
        List<PaoPointValue> paoPointValues = Lists.newArrayList();
        for (PointValueHolder pointValueHolder : pointData) {
            if (!CollectionUtils.isEmpty(discludedPointStateValues)) {
                String valueString = pointFormattingService.getValueString(pointValueHolder, Format.VALUE, userContext);
                if (discludedPointStateValues.contains(valueString.toLowerCase())) continue;
            }
            PointInfo pointInfo = pointIdPointInfoMap.get(pointValueHolder.getId());
            PaoPointIdentifier paoPointIdentifier = pointIdPaoPointIdMap.get(pointValueHolder.getId());
            Meter meter = paoPointIdMeterMap.get(paoPointIdentifier.getPaoIdentifier());
            BuiltInAttribute attribute = attributeService.findAttributeForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributeSet);

            PaoPointValue paoPointValue = new PaoPointValue();
            paoPointValue.setPaoPointIdentifier(paoPointIdentifier);
            paoPointValue.setAttribute(attribute);
            paoPointValue.setMeter(meter);
            paoPointValue.setPointValueHolder(pointValueHolder);
            paoPointValue.setPointName(pointInfo.getName());

            paoPointValues.add(paoPointValue);
        }

        Collections.sort(paoPointValues, getPaoPointValueComparator());
        return paoPointValues;
    }

}

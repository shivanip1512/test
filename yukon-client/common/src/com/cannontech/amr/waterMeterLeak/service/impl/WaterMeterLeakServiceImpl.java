package com.cannontech.amr.waterMeterLeak.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.Hours;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.waterMeterLeak.model.WaterMeterLeak;
import com.cannontech.amr.waterMeterLeak.service.WaterMeterLeakService;
import com.cannontech.common.chart.service.NormalizedUsageService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class WaterMeterLeakServiceImpl implements WaterMeterLeakService {

    @Autowired private MeterDao meterDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private NormalizedUsageService normalizedUsageService;

    private static class MeterPointValueHolder {
        private Meter meter;
        private PointValueHolder pointValueHolder;

        private MeterPointValueHolder(Meter meter, PointValueHolder pointValueHolder) {
            this.meter = meter;
            this.pointValueHolder = pointValueHolder;
        }
    }

    private static class PointIdTimestamp {
        private int pointId;
        private Date timestamp;

        private PointIdTimestamp(int pointId, Date timestamp) {
            this.pointId = pointId;
            this.timestamp = timestamp;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + pointId;
            result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PointIdTimestamp other = (PointIdTimestamp) obj;
            if (pointId != other.pointId)
                return false;
            if (timestamp == null) {
                if (other.timestamp != null)
                    return false;
            } else if (!timestamp.equals(other.timestamp))
                return false;
            return true;
        }
    }

    @Override
    public List<WaterMeterLeak> getWaterMeterLeakIntervalData(Set<SimpleDevice> devices,
                                                              Range<Instant> range,
                                                              boolean includeDisabledPaos,
                                                              double threshold,
                                                              YukonUserContext userContext) {
        return getLeaks(devices, range, includeDisabledPaos, threshold, userContext, true);
    }

    @Override
    public List<WaterMeterLeak> getWaterMeterLeaks(Set<SimpleDevice> devices,
                                                   Range<Instant> range,
                                                   boolean includeDisabledPaos,
                                                   double threshold,
                                                   YukonUserContext userContext) {
        return getLeaks(devices, range, includeDisabledPaos, threshold, userContext, false);
    }

    private List<WaterMeterLeak> getLeaks(Set<SimpleDevice> devices,
                                          Range<Instant> range,
                                          boolean includeDisabledPaos,
                                          double threshold,
                                          YukonUserContext userContext,
                                          boolean getIntervalData) {
        
        if (CollectionUtils.isEmpty(devices)) {
            return new ArrayList<WaterMeterLeak>();
        }

        List<MeterPointValueHolder> intervalReadings = Lists.newArrayList();
        Set<Meter> reportingMeters = Sets.newHashSet();
        List<Meter> meters = meterDao.getMetersForYukonPaos(devices);
        populateMeterSetAndIntervalReadings(meters,
                                            range,
                                            includeDisabledPaos,
                                            reportingMeters,
                                            intervalReadings);

        List<WaterMeterLeak> leakingMeterIntervalData = Lists.newArrayListWithCapacity(reportingMeters.size());
        int numHours = Hours.hoursBetween(range.getMin(), range.getMax()).getHours();
        for (Meter meter : reportingMeters) {
            Double leakRate = getLeakRate(meter, numHours, threshold, intervalReadings);
            if (getIntervalData) {
                List<PointValueHolder> pointValues = getPointValueFromMeterAndReadings(meter, intervalReadings);
                for (PointValueHolder pointValueHolder : pointValues) {
                    WaterMeterLeak leak = new WaterMeterLeak();
                    leak.setMeter(meter);
                    leak.setPointValueHolder(pointValueHolder);
                    leak.setLeakRate(leakRate);
                    leakingMeterIntervalData.add(leak);
                }
            } else {
                if (leakRate == null) continue;
                WaterMeterLeak leak = new WaterMeterLeak();
                leak.setMeter(meter);
                leak.setLeakRate(leakRate);
                leakingMeterIntervalData.add(leak);
            }
        }
        return leakingMeterIntervalData;
    }

    private Double getLeakRate(Meter meter, int numHours, double threshold,
                               List<MeterPointValueHolder> intervalReadings) {
        Map<Date, Double> dateValueMap = Maps.newHashMapWithExpectedSize(numHours);
        for (MeterPointValueHolder meterValue : intervalReadings) {
            if (!meterValue.meter.getPaoIdentifier().equals(meter.getPaoIdentifier()))
                continue;
            dateValueMap.put(meterValue.pointValueHolder.getPointDataTimeStamp(),
                             meterValue.pointValueHolder.getValue());
        }

        Collection<Double> values = dateValueMap.values();
        if (values == null || values.size() < numHours) {
            return null;
        }

        Double minValue = null;
        for (Double value : values) {
            if (value == null || value <= threshold)
                return null;
            if (minValue == null || value < minValue) {
                minValue = value;
            }
        }
        return minValue;
    }

    private List<PointValueHolder> getPointValueFromMeterAndReadings(Meter meter, List<MeterPointValueHolder> intervalReadings) {
        List<PointValueHolder> pointValueHolders = Lists.newArrayList();
        for (MeterPointValueHolder meterValue : intervalReadings) {
            if (meterValue.meter != meter)
                continue;
            pointValueHolders.add(meterValue.pointValueHolder);
        }
        return pointValueHolders;
    }

    private void populateMeterSetAndIntervalReadings(List<Meter> allMeters, Range<Instant> range,
                                                     boolean includeDisabledPaos,
                                                     Set<Meter> reportingMeters,
                                                     List<MeterPointValueHolder> intervalReadings) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
            rawPointHistoryDao.getAttributeData(allMeters,
                                                BuiltInAttribute.USAGE_WATER,
                                                range,
                                                null,
                                                !includeDisabledPaos,
                                                Order.FORWARD);

        Map<PointIdTimestamp, PaoIdentifier> idToPaoIdentifierMap = Maps.newHashMap();
        List<PointValueHolder> pvhs = Lists.newArrayList();
        for (Entry<PaoIdentifier, PointValueQualityHolder> entry : attributeData.entries()) {
            PaoIdentifier paoIdentifier = entry.getKey();
            PointValueQualityHolder pvqh = entry.getValue();

            PointIdTimestamp pointIdTimestamp = new PointIdTimestamp(pvqh.getId(), pvqh.getPointDataTimeStamp());
            idToPaoIdentifierMap.put(pointIdTimestamp, paoIdentifier);
            pvhs.add(pvqh);
        }

        Map<PaoIdentifier, Meter> metersByPaoId =
            Maps.uniqueIndex(allMeters, new Function<Meter, PaoIdentifier>() {
                @Override
                public PaoIdentifier apply(Meter input) {
                    return input.getPaoIdentifier();
                }
            });

        List<PointValueHolder> normalizedUsage = normalizedUsageService.getNormalizedUsage(pvhs, BuiltInAttribute.USAGE_WATER);
        for (PointValueHolder pvh : normalizedUsage) {
            PaoIdentifier paoIdentifier = idToPaoIdentifierMap.get(new PointIdTimestamp(pvh.getId(), pvh.getPointDataTimeStamp()));
            Meter meter = metersByPaoId.get(paoIdentifier);
            reportingMeters.add(meter);
            intervalReadings.add(new MeterPointValueHolder(meter, pvh));
        }
    }

}

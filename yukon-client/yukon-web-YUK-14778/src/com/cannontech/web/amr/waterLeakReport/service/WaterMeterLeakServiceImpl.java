package com.cannontech.web.amr.waterLeakReport.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.service.NormalizedUsageService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.waterLeakReport.model.WaterLeakReportFilter;
import com.cannontech.web.amr.waterLeakReport.model.WaterMeterLeak;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class WaterMeterLeakServiceImpl implements WaterMeterLeakService {

    @Autowired private MeterDao meterDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private NormalizedUsageService normalizedUsageService;
    private static final Logger log = YukonLogManager.getLogger(WaterMeterLeakServiceImpl.class);

    private static class MeterPointValueHolder {
        
        private YukonMeter meter;
        private PointValueHolder pointValueHolder;

        private MeterPointValueHolder(YukonMeter meter, PointValueHolder pointValueHolder) {
            this.meter = meter;
            this.pointValueHolder = pointValueHolder;
        }

        @Override
        public String toString() {
            return String.format("MeterPointValueHolder [meter=%s, pointValueHolder=%s]", meter, pointValueHolder);
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
    public List<WaterMeterLeak> getIntervalLeaks(WaterLeakReportFilter filter, YukonUserContext userContext) {
        return getLeaks(filter.getDeviceCollection().getDeviceList(),
                filter.getRange(),
                filter.isIncludeDisabledPaos(),
                filter.getThreshold(),
                userContext, 
                true);
    }

    @Override
    public List<WaterMeterLeak> getLeaks(WaterLeakReportFilter filter, YukonUserContext userContext) {
        return getLeaks(filter.getDeviceCollection().getDeviceList(),
                filter.getRange(),
                filter.isIncludeDisabledPaos(),
                filter.getThreshold(),
                userContext, 
                false);
    }
    
    @Override
    public List<WaterMeterLeak> getLeaks(Iterable<? extends YukonPao> devices,
            Range<Instant> range, 
            boolean includeDisabledPaos,
            double threshold, 
            YukonUserContext userContext) {
        return getLeaks(devices, range, includeDisabledPaos, threshold, userContext, false);
    }

    private List<WaterMeterLeak> getLeaks(Iterable<? extends YukonPao> devices,
                                          Range<Instant> range,
                                          boolean includeDisabledPaos,
                                          double threshold, 
                                          YukonUserContext userContext,
                                          boolean getIntervalData) {
        
        if (Iterables.isEmpty(devices)) {
            return new ArrayList<WaterMeterLeak>();
        }
        
        
        if (log.isDebugEnabled()) {
            DateTimeFormatter df = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
            log.debug("start=" + df.print(range.getMin()) + "---stop=" + df.print(range.getMax()));
            log.debug("range=" + range);
            log.debug("hours between=" + Hours.hoursBetween(range.getMin(), range.getMax()).getHours());
        }
        
        List<MeterPointValueHolder> intervalReadings = Lists.newArrayList();
        Set<YukonMeter> reportingMeters = Sets.newHashSet();
        List<YukonMeter> meters = meterDao.getMetersForYukonPaos(devices);
        populateMeterSetAndIntervalReadings(meters,
                                            range,
                                            includeDisabledPaos,
                                            reportingMeters,
                                            intervalReadings);

        List<WaterMeterLeak> leakingMeterIntervalData = Lists.newArrayListWithCapacity(reportingMeters.size());
        int numHours = Hours.hoursBetween(range.getMin(), range.getMax()).getHours();
        for (YukonMeter meter : reportingMeters) {
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

    private Double getLeakRate(YukonMeter meter, int numHours, double threshold,
                               List<MeterPointValueHolder> intervalReadings) {
        Map<Date, Double> dateValueMap = Maps.newHashMapWithExpectedSize(numHours);
        for (MeterPointValueHolder meterValue : intervalReadings) {
            if (!meterValue.meter.getPaoIdentifier().equals(meter.getPaoIdentifier()))
                continue;
            dateValueMap.put(meterValue.pointValueHolder.getPointDataTimeStamp(),
                             meterValue.pointValueHolder.getValue());
        }

        Collection<Double> values = dateValueMap.values();
        
        if(values != null && log.isDebugEnabled()){
            log.debug("values="+values.size()+"---"+ "numHours="+numHours);
        }
        if (values == null || values.size() < numHours) {
            log.debug("leak rate is null");
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

    private List<PointValueHolder> getPointValueFromMeterAndReadings(YukonMeter meter, List<MeterPointValueHolder> intervalReadings) {
        List<PointValueHolder> pointValueHolders = Lists.newArrayList();
        for (MeterPointValueHolder meterValue : intervalReadings) {
            if (meterValue.meter != meter)
                continue;
            pointValueHolders.add(meterValue.pointValueHolder);
        }
        return pointValueHolders;
    }

    private void populateMeterSetAndIntervalReadings(List<YukonMeter> allMeters, ReadableRange<Instant> range,
                                                     boolean includeDisabledPaos,
                                                     Set<YukonMeter> reportingMeters,
                                                     List<MeterPointValueHolder> intervalReadings) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
            rawPointHistoryDao.getAttributeData(allMeters,
                                                BuiltInAttribute.USAGE_WATER,
                                                range,
                                                null,
                                                !includeDisabledPaos,
                                                Order.FORWARD,
                                                null,
                                                null);

        Map<PointIdTimestamp, PaoIdentifier> idToPaoIdentifierMap = Maps.newHashMap();
        List<PointValueHolder> pvhs = Lists.newArrayList();
        for (Entry<PaoIdentifier, PointValueQualityHolder> entry : attributeData.entries()) {
            PaoIdentifier paoIdentifier = entry.getKey();
            PointValueQualityHolder pvqh = entry.getValue();

            PointIdTimestamp pointIdTimestamp = new PointIdTimestamp(pvqh.getId(), pvqh.getPointDataTimeStamp());
            idToPaoIdentifierMap.put(pointIdTimestamp, paoIdentifier);
            pvhs.add(pvqh);
        }

        Map<PaoIdentifier, YukonMeter> metersByPaoId =
            Maps.uniqueIndex(allMeters, new Function<YukonMeter, PaoIdentifier>() {
                @Override
                public PaoIdentifier apply(YukonMeter input) {
                    return input.getPaoIdentifier();
                }
            });

        List<PointValueHolder> normalizedUsage = normalizedUsageService.getNormalizedUsage(pvhs, BuiltInAttribute.USAGE_WATER);
        for (PointValueHolder pvh : normalizedUsage) {
            PaoIdentifier paoIdentifier = idToPaoIdentifierMap.get(new PointIdTimestamp(pvh.getId(), pvh.getPointDataTimeStamp()));
            YukonMeter meter = metersByPaoId.get(paoIdentifier);
            reportingMeters.add(meter);
            intervalReadings.add(new MeterPointValueHolder(meter, pvh));
        }
    }

}
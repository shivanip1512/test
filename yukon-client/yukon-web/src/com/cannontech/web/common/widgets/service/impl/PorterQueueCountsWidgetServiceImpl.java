package com.cannontech.web.common.widgets.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.widgets.service.PorterQueueCountsWidgetService;

public class PorterQueueCountsWidgetServiceImpl implements PorterQueueCountsWidgetService {

    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    private static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardMinutes(15);
    private static final Logger log = YukonLogManager.getLogger(PorterQueueCountsWidgetServiceImpl.class);

    @Override
    public Map<Integer, LiteYukonPAObject> getPointIdToPaoMap(List<Integer> portIds) {
        return paoDao.getLiteYukonPaos(portIds)
                .stream()
                .collect(StreamUtils.mapToSelf(pao -> pointDao.getPointIDByDeviceID_Offset_PointType(pao.getLiteID(), 1, PointTypes.ANALOG_POINT)));
    }
    
    @Override
    public Map<Integer, List<PointValueHolder>> rawPointHistoryDataProvider(Set<Integer> pointIds) {
        Map<Integer, List<PointValueHolder>> pointIdToPointValueHolder = new HashMap<>();
        
        pointIds.forEach(pointId -> {
            pointIdToPointValueHolder.put(pointId, null);
        });

        Range<Instant> range = Range.exclusiveInclusive(getEarliestStartDate().toInstant(), Instant.now());
        List<PointValueHolder> values = rawPointHistoryDao.getPointData(pointIds, range, false, Order.FORWARD);
        
        values.forEach(pvh -> {
            pointIdToPointValueHolder.get(pvh.getId()).add(pvh);
        });
        
        return pointIdToPointValueHolder;
    }
    
    @Override
    public List<Object[]> graphDataProvider(List<PointValueHolder> data) {
        log.debug("graphDataProvider called");
        List<Object[]> values = data.stream()
                .map(pvh -> new Object[] {pvh.getPointDataTimeStamp().getTime(), pvh.getValue()})
                .collect(Collectors.toList());
        log.debug("graphDataProvider returned " + values.size() + " {time, value} pairs");
        return values;
    }

    @Override
    public boolean isRefreshEligible(Instant lastGraphDataRefreshTime) {
        return lastGraphDataRefreshTime == null || 
                Instant.now().isAfter(lastGraphDataRefreshTime.plus(MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH));
    }
    
    /**
     * Returns the earliest starting date to collect porter queue count data for.
     * e.g. "I want 3 months of data."
     */
    private DateTime getEarliestStartDate() {
        Months months = Months.months(globalSettingDao.getInteger(GlobalSettingType.PORTER_QUEUE_COUNTS_HISTORICAL_MONTHS));
        return new DateTime().withTimeAtStartOfDay().minus(months);
    }

}

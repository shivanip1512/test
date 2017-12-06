package com.cannontech.web.common.widgets.service.impl;

import java.util.ArrayList;
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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.widgets.service.PorterQueueCountsWidgetService;

public class PorterQueueCountsWidgetServiceImpl implements PorterQueueCountsWidgetService {

    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private PaoDao paoDao;
    @Autowired private AttributeService attributeService;
    @Autowired private GlobalSettingDao globalSettingDao;
    private static final Logger log = YukonLogManager.getLogger(PorterQueueCountsWidgetServiceImpl.class);

    @Override
    public Map<Integer, LiteYukonPAObject> getPointIdToPaoMap(List<Integer> portIds) {
        return makeAndGetPointIdToPaoMap(paoDao.getLiteYukonPaos(portIds));
    }
    
    @Override 
    public Map<Integer, LiteYukonPAObject> makeAndGetPointIdToPaoMap(List<LiteYukonPAObject> portPaos) {
        return portPaos.stream()
                       .collect(StreamUtils.mapToSelf(pao -> attributeService.getPointForAttribute(pao, BuiltInAttribute.PORT_QUEUE_COUNT).getPointID()));
    }
    
    @Override
    public Map<Integer, List<PointValueHolder>> rawPointHistoryDataProvider(Set<Integer> pointIds) {
        Map<Integer, List<PointValueHolder>> pointIdToPointValueHolder = new HashMap<>();
        
        pointIds.forEach(pointId -> {
            pointIdToPointValueHolder.put(pointId, new ArrayList<PointValueHolder>());
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
        if (data == null) {
            log.debug("no data to provide for graph");
            return null;
        }
        List<Object[]> values = data.stream()
                .map(pvh -> new Object[] {pvh.getPointDataTimeStamp().getTime(), pvh.getValue()})
                .collect(Collectors.toList());
        log.debug("graphDataProvider returned " + values.size() + " {time, value} pairs");
        return values;
    }
    
    @Override
    public Instant getNextRefreshTime(Instant lastGraphDataRefreshTime) {
        return lastGraphDataRefreshTime.plus(Duration.standardMinutes(globalSettingDao.getInteger(GlobalSettingType.PORTER_QUEUE_COUNTS_MINUTES_TO_WAIT_BEFORE_REFRESH)));
    }
    
    @Override
    public long getRefreshMilliseconds() {
        return Duration.standardMinutes(globalSettingDao.getInteger(GlobalSettingType.PORTER_QUEUE_COUNTS_MINUTES_TO_WAIT_BEFORE_REFRESH)).getMillis();
    }
    
    /**
     * Returns the earliest starting date to collect porter queue count data for.
     * e.g. "I want 3 months of data."
     */
    private DateTime getEarliestStartDate() {
        Months months = Months.months(globalSettingDao.getInteger(GlobalSettingType.PORTER_QUEUE_COUNTS_HISTORICAL_MONTHS));
        return new DateTime().withTimeAtStartOfDay().minus(months);
    }
    
    @Override
    public List<LiteYukonPAObject> getAllPortPaos() {
        List<LiteYukonPAObject> paos = new ArrayList<LiteYukonPAObject>();
        PaoType.getPortTypes().stream().forEach(portType -> {
            paos.addAll(paoDao.getLiteYukonPAObjectByType(portType));
        });
        return paos;
    }
    
}

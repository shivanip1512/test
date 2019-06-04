package com.cannontech.web.amr.usageThresholdReport.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.InstantRangeLogHelper;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao.SortBy;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReport;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;
import com.cannontech.web.amr.usageThresholdReport.service.ThresholdReportService;
import com.google.common.collect.BiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

public class ThresholdReportServiceImpl implements ThresholdReportService{
    
    private static final Logger log = YukonLogManager.getLogger(ThresholdReportServiceImpl.class);
    
    @Autowired private ThresholdReportDao thresholdReportDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private AttributeService attributeService;
       
    //exclude all qualities other then normal
    private final static Set<PointQuality> excludedQualities = Sets.newHashSet(PointQuality.values());
    {
        excludedQualities.remove(PointQuality.Normal);
    }
    
    @Override
    public ThresholdReport getReportDetail(int reportId, ThresholdReportFilter filter, Integer[] selectedGatewayIds, PagingParameters paging,
            SortBy sortBy, Direction direction) {
        return thresholdReportDao.getReportDetail(reportId, filter, selectedGatewayIds, paging, sortBy, direction);
    }
    
    @Transactional
    @Override
    public int createThresholdReport(ThresholdReportCriteria criteria, List<SimpleDevice> devices) {

        log.debug("Creating threshold for " + devices.size() + " devices for criteria=" + criteria);

        Range<Instant> range = getReportRange(criteria);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> latest =
            getReading(criteria.getAttribute(), range, devices, Order.REVERSE);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> earliest =
            getReading(criteria.getAttribute(), range, devices, Order.FORWARD);

        BiMap<PaoIdentifier, LitePoint> points = attributeService.getPoints(devices, criteria.getAttribute());
        List<ThresholdReportDetail> details = new ArrayList<>();
        devices.forEach(device -> {
            ThresholdReportDetail detail = new ThresholdReportDetail();
            detail.setPointId(
                Optional.ofNullable(points.get(device.getPaoIdentifier()))
                        .map(LitePoint::getLiteID)
                        .orElse(null)
            );
            detail.setPaoIdentifier(device.getPaoIdentifier());
            List<PointValueQualityHolder> latestReading = latest.get(device.getPaoIdentifier());
            if (!CollectionUtils.isEmpty(latestReading)) {
                detail.setLatestReading(latestReading.get(0));
            }
            List<PointValueQualityHolder> earliestReading = earliest.get(device.getPaoIdentifier());
            if (!CollectionUtils.isEmpty(earliestReading)) {
                detail.setEarliestReading(earliestReading.get(0));
            }
            detail.calculateDelta();
            details.add(detail);
        });

        int reportId = thresholdReportDao.createReport(criteria);
        log.debug("Created report id=" + reportId);
        thresholdReportDao.createReportDetail(reportId, details);
        log.debug("Created inserted details=" + details.size());
        return reportId;
    }

    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getReading(BuiltInAttribute attribute,
            Range<Instant> range, List<SimpleDevice> devices, Order order) {
        return rphDao.getLimitedAttributeData(devices, attribute, range, null, 1, false, order, OrderBy.TIMESTAMP,
            excludedQualities);
    }
    
    /**
     * Report range: start date [inclusive] - end date + 1 [inclusive] 
     */
    private Range<Instant> getReportRange(ThresholdReportCriteria criteria){
        Instant startDate = criteria.getStartDate().toDateTime().withTimeAtStartOfDay().toInstant();
        Instant endDatePlus1Day = criteria.getEndDateAdjusted().toDateTime().withTimeAtStartOfDay().toInstant();
        Range<Instant> range = new Range<>(startDate, true, endDatePlus1Day, true);
        log.debug("Creating " + InstantRangeLogHelper.getLogString(range) + " for criteria=" + criteria);
        return new Range<>(startDate, true, endDatePlus1Day, true); 
    }
}

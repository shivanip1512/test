package com.cannontech.web.amr.usageThresholdReport.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao.SortBy;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReport;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportCriteria;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportDetail;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFilter;
import com.cannontech.web.amr.usageThresholdReport.service.ThresholdReportService;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

public class ThresholdReportServiceImpl implements ThresholdReportService{
    
    private static final Logger log = YukonLogManager.getLogger(ThresholdReportServiceImpl.class);
    
    @Autowired private ThresholdReportDao thresholdReportDao;
    @Autowired private RawPointHistoryDao rphDao;
       
    //exclude all qualities other then normal
    private final static Set<PointQuality> excludedQualities = Sets.newHashSet(PointQuality.values());
    {
        excludedQualities.remove(PointQuality.Normal);
    }
    
    @Override
    public ThresholdReport getReportDetail(int reportId, ThresholdReportFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction) {
        ThresholdReportCriteria criteria = thresholdReportDao.getReport(reportId);
        return thresholdReportDao.getReportDetail(reportId, criteria.getRange(), filter, paging, sortBy, direction);
    }
    
    @Transactional
    @Override
    public int createThresholdReport(ThresholdReportCriteria criteria, List<SimpleDevice> devices) {
               
        log.debug("Creating threshold for " + devices.size() + " for criteria=" + criteria);
       
        ListMultimap<PaoIdentifier, PointValueQualityHolder> latest = getReading(criteria, devices, Order.REVERSE);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> earliest = getReading(criteria, devices, Order.FORWARD);
        
        List<ThresholdReportDetail> details = new ArrayList<>();
        devices.forEach(device -> {
            ThresholdReportDetail detail = new ThresholdReportDetail();
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
        log.debug("Created report id="+reportId);
        thresholdReportDao.createReportDetail(reportId, details);
        log.debug("Created inserted details="+details.size());
        return reportId;
    }
    
    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getReading(ThresholdReportCriteria criteria,
            List<SimpleDevice> devices, Order order) {
        return rphDao.getLimitedAttributeData(devices, criteria.getAttribute(), criteria.getRange(), null, 1, false,
            order, OrderBy.TIMESTAMP, excludedQualities);
    }
}

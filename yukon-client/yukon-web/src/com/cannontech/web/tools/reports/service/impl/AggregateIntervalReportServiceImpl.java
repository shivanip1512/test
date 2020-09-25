package com.cannontech.web.tools.reports.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService;

@Service
public class AggregateIntervalReportServiceImpl implements AggregateIntervalReportService {

    @Override
    public List<List<String>> getIntervalDataReport(AggregateIntervalReportFilter filter, YukonUserContext context) {
        // TODO Auto-generated method stub
        return null;
    }
}

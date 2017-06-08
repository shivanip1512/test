package com.cannontech.web.amr.usageThresholdReport.model;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.search.result.SearchResults;

public class ThresholdReport {

    private SearchResults<ThresholdReportDetail> detail;
    private Map<DataAvailability, Integer> counts;
    private int totalCount;

    public ThresholdReport(SearchResults<ThresholdReportDetail> detail, int totalCount) {
        this.detail = detail;
        this.totalCount = totalCount;
        counts = detail.getResultList().stream().collect(
            Collectors.groupingBy(ThresholdReportDetail::getAvailability, Collectors.collectingAndThen(
                Collectors.mapping(ThresholdReportDetail::getAvailability, Collectors.toSet()), Set::size)));
    }

    public int getAvailabilityCount(DataAvailability availability) {
        return counts.get(availability) == null ? 0 : counts.get(availability);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public SearchResults<ThresholdReportDetail> getDetail() {
        return detail;
    }
}

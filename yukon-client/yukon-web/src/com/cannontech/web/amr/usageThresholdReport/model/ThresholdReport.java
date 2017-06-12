package com.cannontech.web.amr.usageThresholdReport.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.search.result.SearchResults;

public class ThresholdReport {

    private SearchResults<ThresholdReportDetail> detail;
    private Map<DataAvailability, Integer> counts = new HashMap<>();
    private List<SimpleDevice> devices;

    public ThresholdReport(SearchResults<ThresholdReportDetail> detail, List<ThresholdReportDetail> allDetails) {
        this.detail = detail;
        Arrays.asList(DataAvailability.values()).forEach(value -> {
            int count = (int) allDetails.stream().filter(r -> r.getAvailability() == value).count();
            counts.put(value, count);
        });
        devices = allDetails.stream().map(result -> new SimpleDevice(result.getPaoIdentifier())).collect(Collectors.toList());
    }

    public int getAvailabilityCount(DataAvailability availability) {
        return counts.get(availability) == null ? 0 : counts.get(availability);
    }

    public  List<SimpleDevice> getAllDevices() {
        return devices;
    }

    public SearchResults<ThresholdReportDetail> getDetail() {
        return detail;
    }
}

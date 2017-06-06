package com.cannontech.web.amr.usageThresholdReport.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Range;

public class ThresholdReportCriteria {

    final private static DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
    
    private BuiltInAttribute attribute;
    private Instant startDate;
    private Instant endDate;
    private Instant runTime;
    //description - if individual devices were selected “X devices” otherwise a group name
    private String description;
    private int reportId;

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Instant getRunTime() {
        return runTime;
    }

    public void setRunTime(Instant runTime) {
        this.runTime = runTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
    
    public Range<Instant> getRange(){
        return new Range<>(startDate, false, endDate, true);
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("description", description);
        tsb.append("attribute", attribute);
        if (runTime != null) {
            tsb.append("Run time", runTime.toString(df.withZone(DateTimeZone.getDefault())));
        }
        Range<Instant> range = getRange();
        if (range != null) {
            String min = range.getMin() == null ? "" : range.getMin().toString(df.withZone(DateTimeZone.getDefault()));
            String max = range.getMax() == null ? "" : range.getMax().toString(df.withZone(DateTimeZone.getDefault()));
            String includesMin = " [exclusive] ";
            String includesMax = " [exclusive] ";
            if (range.isIncludesMinValue()) {
                includesMin = " [inclusive] ";
            }
            if (range.isIncludesMaxValue()) {
                includesMax = " [inclusive] ";
            }
            
            tsb.append("Range", includesMin + min + " - " + includesMax + max + " ");
        }
        return tsb.toString();
    }
}

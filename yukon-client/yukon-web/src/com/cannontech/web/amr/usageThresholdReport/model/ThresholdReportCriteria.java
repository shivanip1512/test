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
    private Range<Instant> range;
    private Instant runTime;
    //description - if individual devices were selected “X devices” otherwise a group name
    private String description;

    public ThresholdReportCriteria(String description, BuiltInAttribute attribute, Instant start, Instant end) {
        range = new Range<>(start, false, end, true);
        this.attribute = attribute;
        this.description = description;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public Range<Instant> getRange() {
        return range;
    }

    public String getDescription() {
        return description;
    }

    public Instant getRunTime() {
        return runTime;
    }

    public void setRunTime(Instant runTime) {
        this.runTime = runTime;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("description", description);
        tsb.append("attribute", attribute);
        if (runTime != null) {
            tsb.append("Run time", runTime.toString(df.withZone(DateTimeZone.getDefault())));
        }
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

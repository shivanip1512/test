package com.cannontech.web.amr.usageThresholdReport.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.InstantRangeLogHelper;

public class ThresholdReportCriteria {
    
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
    
    /**
     * Returns end date +1 day
     */
    public Instant getEndDateAdjusted(){
        return getEndDate().toDateTime().plusDays(1).toInstant();
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("description", description);
        tsb.append("attribute", attribute);
        if (runTime != null) {
            tsb.append("Run time", runTime.toString(InstantRangeLogHelper.df));
        }
        tsb.append(InstantRangeLogHelper.getLogString(startDate)+"-"+InstantRangeLogHelper.getLogString(endDate));
        return tsb.toString();
    }
}

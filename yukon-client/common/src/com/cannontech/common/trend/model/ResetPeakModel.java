package com.cannontech.common.trend.model;

import org.joda.time.DateTime;

import com.cannontech.common.util.DateDeserializer;
import com.cannontech.common.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ResetPeakModel {
    private DateTime startDate;

    @JsonSerialize(using = DateSerializer.class)
    @JsonDeserialize(using = DateDeserializer.class)
    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

}

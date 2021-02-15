package com.cannontech.common.trend.model;

import org.joda.time.DateTime;

import com.cannontech.common.util.JsonSerializers.TO_MMDDYYYY_FORMAT;
import com.cannontech.common.util.JsonSerializers.FROM_MMDDYYYY_FORMAT;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ResetPeakModel {
    private DateTime startDate;

    @JsonSerialize(using = TO_MMDDYYYY_FORMAT.class)
    @JsonDeserialize(using = FROM_MMDDYYYY_FORMAT.class)
    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

}

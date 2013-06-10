package com.cannontech.amr.archivedValueExporter.model.dataRange;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.user.YukonUserContext;

public class LocalDateRange {
    private LocalDate startDate = LocalDate.now().minus(Period.days(2));
    private LocalDate endDate = LocalDate.now();
    
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public ReadableRange<Instant> getInstantDateRange(YukonUserContext userContext) {
        Instant startDate = this.startDate.toDateMidnight(userContext.getJodaTimeZone()).toInstant();
        Instant stopDate = this.endDate.toDateMidnight(userContext.getJodaTimeZone()).toInstant();
        
        return Range.exclusiveInclusive(startDate, stopDate);
    }
}
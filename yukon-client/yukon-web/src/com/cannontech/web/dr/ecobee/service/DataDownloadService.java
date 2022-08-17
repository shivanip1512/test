package com.cannontech.web.dr.ecobee.service;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import com.cannontech.common.util.Range;
import com.cannontech.user.YukonUserContext;

public interface DataDownloadService {

    /**
     * Starts a task to read data for devices and save them to a csv file.
     * @param timeZone The time zone to print the dates in. This may be set to <code>null</code> to print dates in UTC.
     * @return the recent result cache key to look up the task by.
     */
    String start(List<String> serialNumbers, Range<LocalDate> dateRange, YukonUserContext userContext) throws IOException;
}

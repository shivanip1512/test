package com.cannontech.web.dr.ecobee.service;

import java.io.IOException;
import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;

public interface DataDownloadService {

    /**
     * Starts a task to read data for devices and save them to a csv file.
     * @return the recent result cache key to look up the task by.
     */
    String start(List<String> serialNumbers, Range<Instant> dateRange) throws IOException;
}

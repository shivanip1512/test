package com.cannontech.common.device.peakReport.service;

import java.util.Date;

import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PeakReportService {

    /**
     * Run a Peak Report command.
     * The result of the command is returned as a PeakReportResult object
     * 
     * @param deviceId
     * @param peakType
     * @param runType
     * @param channel
     * @param startDate
     * @param stopDate
     * @param persist
     * @param user
     * @return
     */
    public PeakReportResult requestPeakReport(int deviceId, PeakReportPeakType peakType, PeakReportRunType runType, int channel, Date startDate, Date stopDate, boolean persist, LiteYukonUser user);

    /**
     * Find last saved peak report result for a given device and run type. Process result into a PeakReportResult and return it if found.
     * 
     * @param deviceId
     * @param runType
     * @param user
     * @return
     */
    public PeakReportResult retrieveArchivedPeakReport(int deviceId, PeakReportRunType runType, LiteYukonUser user);
    
    /**
     * Helper method that parses the result string of a peak report request and sets the appropriate members of a given PeakReportResult object.
     * 
     * @param peakResult
     * @param resultString
     * @param interval
     * @param user
     */
    public void parseResultString(PeakReportResult peakResult, String resultString, int interval, LiteYukonUser user);
    
    /**
     * Helper method to determine the interval of a device for a given channel.
     * 
     * @param deviceId
     * @param channel
     * @return
     */
    public int getChannelIntervalForDevice(int deviceId, int channel);
}
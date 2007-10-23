package com.cannontech.common.device.peakReport.service;

import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PeakReportService {

    public PeakReportResult parseResultString(PeakReportResult peakResult, String resultString, int interval, LiteYukonUser user);
    
    public int getChannelIntervalForDevice(int deviceId, int channel);

}
package com.cannontech.common.device.peakReport.dao;

import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;

public interface PeakReportDao {

    public PeakReportResult getResult(int deviceId, PeakReportRunType runType);

    public void saveResult(PeakReportResult peakResult);
}

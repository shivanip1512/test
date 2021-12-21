package com.cannontech.web.support.systemPerformanceMetrics.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.database.data.lite.LitePoint;

public interface SystemPerformanceMetricsService {

    /*
     * Get list of all System Points
     */
    List<LitePoint> getAllSystemPoints();

    /*
     * Get point data for the passed pointId and date range and convert it to ChartValue
     */
    List<ChartValue<Double>> getPointData(int pointId, Date startDate, Date stopDate);

}

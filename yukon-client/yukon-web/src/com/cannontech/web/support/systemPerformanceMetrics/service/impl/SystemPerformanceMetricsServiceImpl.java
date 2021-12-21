package com.cannontech.web.support.systemPerformanceMetrics.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.device.Device;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.support.systemPerformanceMetrics.service.SystemPerformanceMetricsService;

public class SystemPerformanceMetricsServiceImpl implements SystemPerformanceMetricsService {
    private static final Logger log = YukonLogManager.getLogger(SystemPerformanceMetricsServiceImpl.class);
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public List<LitePoint> getAllSystemPoints() {
        List<LitePoint> systemPoints = new ArrayList<LitePoint>();
        try {
            // For System Points, Device Id is 0 and Point Type is System.
            systemPoints = pointDao.getLitePointIdByDeviceId_PointType(Device.SYSTEM_DEVICE_ID, PointType.System);
            if (log.isDebugEnabled()) {
                systemPoints.stream().forEach(litePoint -> log.debug("Point Name {}, Point type {}, PAObject ID {} ",
                        litePoint.getPointName(), PointType.getForId(litePoint.getLiteType()), litePoint.getPaobjectID()));
            }
        } catch (NotFoundException e) {
            log.error("No System points found in the database.");
        }
        return systemPoints;
    }

    @Override
    public List<ChartValue<Double>> getPointData(int pointId, Date startDate, Date stopDate, YukonUserContext userContext,
            ChartInterval interval, ConverterType converterType) {
        
        List<PointValueHolder> pointData = rawPointHistoryDao.getPointData(pointId, startDate, stopDate);

        log.debug("Point ID {}, Point Data Count {} ",
                pointId, pointData.size());

        String pointName = pointDao.getPointName(pointId);
        
        LitePointUnit pointUnit = pointDao.getPointUnit(pointId);
        NumberFormat pointValueFormat = new DecimalFormat();
        pointValueFormat.setMaximumFractionDigits(pointUnit.getDecimalPlaces());
        pointValueFormat.setMinimumFractionDigits(pointUnit.getDecimalPlaces());
        pointValueFormat.setGroupingUsed(false);

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        UnitOfMeasure unitMeasure = UnitOfMeasure.getForId(pointUnit.getUomID());
        String chartIntervalString = messageSourceAccessor.getMessage(interval.getIntervalString());
        String units = messageSourceAccessor
                .getMessage(converterType.getFormattedUnits(unitMeasure, chartIntervalString));

        List<ChartValue<Double>> chartData = new ArrayList<>();
        for (PointValueHolder point : pointData) {
            ChartValue<Double> chartValue = new ChartValue<>();
            long timeStamp = point.getPointDataTimeStamp().getTime();
            chartValue.setId(point.getId());
            chartValue.setTime(timeStamp);
            chartValue.setValue(point.getValue());
            chartValue.setPointName(pointName);
            chartValue.setUnits(units);
            chartValue.setFormattedValue(pointValueFormat.format(point.getValue()));
            chartData.add(chartValue);
        }
        return chartData;
    }
}

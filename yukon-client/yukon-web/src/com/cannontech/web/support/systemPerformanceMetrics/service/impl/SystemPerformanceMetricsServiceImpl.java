package com.cannontech.web.support.systemPerformanceMetrics.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.support.systemPerformanceMetrics.service.SystemPerformanceMetricsService;

public class SystemPerformanceMetricsServiceImpl implements SystemPerformanceMetricsService {

    @Autowired RawPointHistoryDao rawPointHistoryDao;
    @Autowired PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public List<LitePoint> getAllSystemPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ChartValue<Double>> getPointData(int pointId, Date startDate, Date stopDate, YukonUserContext userContext,
            ChartInterval interval, ConverterType converterType) {
        
        List<PointValueHolder> pointData = rawPointHistoryDao.getPointData(pointId, startDate, stopDate);
        
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

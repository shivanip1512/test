package com.cannontech.common.chart.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.NormalizedUsageService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.Lists;


public class NormalizedUsageServiceImpl implements NormalizedUsageService {

    @Autowired PointDao pointDao;
    
    @Override
    public List<PointValueHolder> getNormalizedUsage(List<PointValueHolder> unNormalizedPoints,
                                                     Attribute usageAttribute) {
        List<ChartValue<Double>> chartValueList = new ArrayList<ChartValue<Double>>();
        int tempPointId = -1;
        PaoType paoType = null;
        for (PointValueHolder pvh : unNormalizedPoints) {
            ChartValue<Double> chartValue = new ChartValue<Double>();
            chartValue.setId(pvh.getId());
            chartValue.setTime(pvh.getPointDataTimeStamp().getTime());
            chartValue.setValue(pvh.getValue());

            if (tempPointId != pvh.getId()) {
                // trying to minimize loading of paoPointIdentifier by saving pointId through iterations since it most likely won't change.
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(unNormalizedPoints.get(0).getId());
                paoType = paoPointIdentifier.getPaoIdentifier().getPaoType();
            }

            if (paoType != null && paoType.isRfElectric()) {
                // only add the midnight reads for RF electric meters. CAUTION: This call is also used by Water Leak Report where all intervals are needed.
                DateTime dateTime = new DateTime(chartValue.getTime());
                if (dateTime.getHourOfDay() == 0 && dateTime.getMinuteOfHour() == 0) {
                    chartValueList.add(chartValue);
                }
            } else {
                chartValueList.add(chartValue);
            }
            tempPointId = pvh.getId();
        }

        // normalize ChartValue list
        ChartDataConverter normalizer = getChartDataConverter(usageAttribute);
        List<ChartValue<Double>> normalizedCharValueList = normalizer.convertValues(chartValueList, null);

        // convert normalized ChartValue back to PointValueHolders
        List<PointValueHolder> pvhs = Lists.newArrayListWithExpectedSize(normalizedCharValueList.size());
        for (ChartValue<Double> cv : normalizedCharValueList) {
            int cvId = ((Long) cv.getId()).intValue();
            Date cvDate = new Date(cv.getTime());
            double cvValue = cv.getValue();

            PointValueHolder pvh = new SimplePointValue(cvId, cvDate, PointType.PulseAccumulator.getPointTypeId(), cvValue);
            pvhs.add(pvh);
        }
        return pvhs;
    }

    private ChartDataConverter getChartDataConverter(Attribute attribute) {
        if (attribute == BuiltInAttribute.USAGE_WATER 
                || attribute == BuiltInAttribute.USAGE_GAS) {
            return new ChartDeltaWaterConverter();
        }

        // Since we only have attribute, and we're processing for multiple points, we will simply use the ChartNormalizedDeltaConverter for everything else.
        // ChartDailyUsageDeltaConverter _should_ be used for RF meters, however we can't guarantee that is the only type of meter we have points for.
        // Instead, we're limiting the RF point data to only midnight reads (lines 48-54). That will accomplish essentially same thing as the ChartDailyUsageDeltaConverter 
        //   even though we're using the ChartNormalizedDeltaConverter
        return new ChartNormalizedDeltaConverter();
    }
}

package com.cannontech.common.chart.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.NormalizedUsageService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.point.PointTypes;
import com.google.common.collect.Lists;

public class NormalizedUsageServiceImpl implements NormalizedUsageService {

    @Override
    public List<PointValueHolder> getNormalizedUsage(List<PointValueHolder> unNormalizedPoints,
                                                     Attribute usageAttribute) {
        List<ChartValue<Double>> chartValueList = new ArrayList<ChartValue<Double>>();
        for (PointValueHolder pvh : unNormalizedPoints) {
            ChartValue<Double> chartValue = new ChartValue<Double>();
            chartValue.setId(pvh.getId());
            chartValue.setTime(pvh.getPointDataTimeStamp().getTime());
            chartValue.setValue(pvh.getValue());

            chartValueList.add(chartValue);
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

            PointValueHolder pvh = new SimplePointValue(cvId, cvDate, PointTypes.PULSE_ACCUMULATOR_POINT, cvValue);
            pvhs.add(pvh);
        }
        return pvhs;
    }

    private ChartDataConverter getChartDataConverter(Attribute attribute) {
        if (attribute == BuiltInAttribute.USAGE_WATER) {
            return new ChartDeltaWaterConverter();
        }
        if (attribute == BuiltInAttribute.USAGE) {
            return new ChartNormalizedDeltaConverter();
        } else { // default to regular normalized converter
            return new ChartNormalizedDeltaConverter();
        }
    }

}

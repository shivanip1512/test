package com.cannontech.common.chart.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.impl.ChartDailyUsageDeltaConverter;
import com.cannontech.common.chart.service.impl.ChartDefaultConverter;
import com.cannontech.common.chart.service.impl.ChartDeltaWaterConverter;
import com.cannontech.common.chart.service.impl.ChartNormalizedDeltaConverter;
import com.cannontech.common.chart.service.impl.ChartPowerFactorDataConverter;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public enum ConverterType implements DisplayableEnum {

    RAW {
        public ChartDataConverter getDataConverter() {
            return new ChartDefaultConverter();
        }
    },
    NORMALIZED_DELTA {
        public ChartDataConverter getDataConverter() {
            return new ChartNormalizedDeltaConverter();
        }
    },
    DELTA_WATER {
        public ChartDataConverter getDataConverter() {
            return new ChartDeltaWaterConverter();
        }
    },
    POWERFACTOR {
        public ChartDataConverter getDataConverter() {
        	return new ChartPowerFactorDataConverter();
        }
    },
    DAILY_USAGE {
        public ChartDataConverter getDataConverter() {
            return new ChartDailyUsageDeltaConverter();
        }
    };

    private static String baseKey = "yukon.common.chart.model.converterType.";

    public abstract ChartDataConverter getDataConverter();

    //wrapper function for jsps right now
    public String getLabel() {
        return getFormatKey();
    }

    @Override
    public String getFormatKey() {
    	return baseKey + name();
    }

    /**
     * Method to get the y-units for this graph given a unit of measure and chartInterval
     * 	- NORMALIZED_USAGE (ChartNormalizedDeltaConverter) always converts to a daily value.
     *  - DELTA_WATER (ChartDeltaWaterConverter) only uses delta between readings, so base off interval
     *  - All else, ignores chartInterval and returns unitMeasure only.
     *  These are all defined in chart.xml
     * @param unitMeasure - Unit of measure of point being graphed
     * @param chartIntervalString - (i18n'd) chartInterval string
     * @return {@link MessageSourceResolvable}
     */
    public MessageSourceResolvable getFormattedUnits(LiteUnitMeasure unitMeasure, String chartIntervalString) {
        return YukonMessageSourceResolvable.createSingleCodeWithArguments(baseKey + name(), 
        		unitMeasure.getUnitMeasureName(), chartIntervalString);
    }
}

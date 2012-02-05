package com.cannontech.common.chart.model;

import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.impl.ChartDefaultConverter;
import com.cannontech.common.chart.service.impl.ChartNormalizedDeltaConverter;
import com.cannontech.common.chart.service.impl.ChartDeltaWaterConverter;
import com.cannontech.common.chart.service.impl.ChartPowerFactorDataConverter;
import com.cannontech.database.data.lite.LiteUnitMeasure;

public enum ConverterType {

    RAW("Raw") {
        public ChartDataConverter getDataConverter() {
            return new ChartDefaultConverter();
        }

        public String getUnits(LiteUnitMeasure unitMeasure, ChartInterval interval) {
            return unitMeasure.getUnitMeasureName();
        }

    },
    NORMALIZED_DELTA("Normalized") {
        public ChartDataConverter getDataConverter() {
            return new ChartNormalizedDeltaConverter();
        }

        public String getUnits(LiteUnitMeasure unitMeasure, ChartInterval interval) {
        	// ChartNormalizedDeltaConverter always converts to a daily value.
            return unitMeasure.getUnitMeasureName() + " / day";
        }
    },
    DELTA_WATER("Delta") {
        public ChartDataConverter getDataConverter() {
            return new ChartDeltaWaterConverter();
        }

        public String getUnits(LiteUnitMeasure unitMeasure, ChartInterval interval) {
        	// ChartDeltaWaterConverter only uses delta between readings, so base off interval
            return unitMeasure.getUnitMeasureName() + " / " + interval.getIntervalString().getDefaultMessage();
        }
    },
    POWERFACTOR("PowerFactor") {
        public ChartDataConverter getDataConverter() {
        	return new ChartPowerFactorDataConverter();
        }

        public String getUnits(LiteUnitMeasure unitMeasure, ChartInterval interval) {
            return unitMeasure.getUnitMeasureName();
        }
    };

    private String label = null;

    private ConverterType(String label) {
        this.label = label;
    }

    public abstract ChartDataConverter getDataConverter();

    /**
     * Method to get the y-units for this graph given a unit of measure and (opt) chartInterval
     * @param unitMeasure - Unit of measure of point being graphed
     * @return Graph type specific units.
     */
    public abstract String getUnits(LiteUnitMeasure unitMeasure, ChartInterval interval);

    public String getLabel() {
        return label;
    }
}

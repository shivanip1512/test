package com.cannontech.common.chart.model;

import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.impl.ChartDefaultConverter;
import com.cannontech.common.chart.service.impl.ChartNormalizedDeltaConverter;
import com.cannontech.database.data.lite.LiteUnitMeasure;

public enum ConverterType {

    RAW("Raw") {
        public ChartDataConverter getDataConverter() {
            return new ChartDefaultConverter();
        }

        public String getUnits(LiteUnitMeasure unitMeasure) {
            return unitMeasure.getUnitMeasureName();
        }

    },
    NORMALIZED_DELTA("Normalized") {
        public ChartDataConverter getDataConverter() {
            return new ChartNormalizedDeltaConverter();
        }

        public String getUnits(LiteUnitMeasure unitMeasure) {
            return unitMeasure.getUnitMeasureName() + " / day";
        }
    };

    private String label = null;

    private ConverterType(String label) {
        this.label = label;
    }

    public abstract ChartDataConverter getDataConverter();

    /**
     * Method to get the y-units for this graph given a unit of measure
     * @param unitMeasure - Unit of measure of point being graphed
     * @return Graph type specific units.
     */
    public abstract String getUnits(LiteUnitMeasure unitMeasure);

    public String getLabel() {
        return label;
    }
}

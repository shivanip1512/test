package com.cannontech.jfreechart.chart;

/**
 * @author snebben
 *
 *         To change this generated comment edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class Dataset_MinMaxValues {
    private double minimumValue = 0;
    private double maximumValue = 0;

    public Dataset_MinMaxValues() {
    }

    public Dataset_MinMaxValues(double min, double max) {
        this.minimumValue = min;
        this.maximumValue = max;
    }

    public double getMinimumValue() {
        return minimumValue;
    }

    public double getMaximumValue() {
        return maximumValue;
    }
}

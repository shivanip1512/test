package com.cannontech.jfreechart.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * @author snebben
 *
 *         To change this generated comment edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class YukonChartPanel extends ChartPanel {

    /**
     * Constructor for YukonChartPanel.
     * 
     * @param chart
     */
    public YukonChartPanel(JFreeChart chart) {
        super(chart);
        this.setInitialDelay(0);
        // Set the the dismiss delay to 60000 so that tool tips will be
        // displayed for 60 seconds
        this.setDismissDelay(60000);
    }

    /**
     * Constructor for YukonChartPanel.
     * 
     * @param chart
     * @param useBuffer
     */
    public YukonChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
    }

    /**
     * Constructor for YukonChartPanel.
     * 
     * @param chart
     * @param properties
     * @param save
     * @param print
     * @param zoom
     * @param tooltips
     */
    public YukonChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom,
            boolean tooltips) {
        super(chart, properties, save, print, zoom, tooltips);
    }

    /**
     * Constructor for YukonChartPanel.
     * 
     * @param chart
     * @param width
     * @param height
     * @param minimumDrawWidth
     * @param minimumDrawHeight
     * @param maximumDrawWidth
     * @param maximumDrawHeight
     * @param useBuffer
     * @param properties
     * @param save
     * @param print
     * @param zoom
     * @param tooltips
     */
    public YukonChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight,
            int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save,
            boolean print, boolean zoom, boolean tooltips) {
        super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight,
            useBuffer, properties, save, print, zoom, tooltips);
    }

}

/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Extends XYStepRenderer.
 * Adds a shape representation to an XYStepRenderer of the Min and Max values
 *  for all series in the dataset.
 */
package com.cannontech.jfreechart.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.renderer.XYStepRenderer;
import com.jrefinery.data.XYDataset;

/**
 * Shapes rendered for MIN/MAX valuse on a Line/Step item renderer for an XYPlot.
 * This class draws lines between data points, 
 * only allowing horizontal or vertical lines (steps).
 */

public class XYStepRenderer_MinMax extends XYStepRenderer
{
	public Dataset_MinMaxValues [][] minMaxValues = null;
	private boolean plotMinMaxValues = false;

    /**
     * Constructs a new renderer.
     */
    public XYStepRenderer_MinMax() {

        super();
    }

    /**
     * Constructs a new renderer.
     */
    public XYStepRenderer_MinMax(boolean plotMinMax)
    {
        super();
        this.plotMinMaxValues = plotMinMax;
    }

    /**
     * Draws a the visual representation of a single data item using the super class.
     * Adds a shape representation of the MIN/MAX values for each series in the dataset.
     *
     * @param g2 The graphics device.
     * @param dataArea The area within which the data is being drawn.
     * @param info Collects information about the drawing.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info,
                          XYPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                          XYDataset data, int datasetIndex, int series, int item,
                          CrosshairInfo crosshairInfo) {
		super.drawItem(g2, dataArea, info, plot, horizontalAxis, verticalAxis, data, datasetIndex, series, item, crosshairInfo);

        // get the data point...
        Number x1 = data.getXValue(series, item);
        Number y1 = data.getYValue(series, item);
        if (y1==null)
			return;

		double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea);
		double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea);

        if( this.plotMinMaxValues)
		{                        
            if (minMaxValues[datasetIndex][series] != null && (y1.doubleValue() == minMaxValues[datasetIndex][series].getMaximumValue() || 
            	y1.doubleValue() == minMaxValues[datasetIndex][series].getMinimumValue()))
            {
                double scale = 6.0;
                java.awt.Shape shape = plot.getShape(series, item, transX1, transY1, scale);
            	g2.fill(shape);
            }
		}
	}
}
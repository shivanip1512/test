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

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYStepRenderer;
import org.jfree.data.XYDataset;

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
                         XYDataset dataset, int datasetIndex, int series, int item,
                         CrosshairInfo crosshairInfo, int pass) {
		super.drawItem(g2, dataArea, info, plot, horizontalAxis, verticalAxis, dataset, datasetIndex, series, item, crosshairInfo, pass);

        // get the data point...
        Number x1 = dataset.getXValue(series, item);
        Number y1 = dataset.getYValue(series, item);
        if (y1==null)
			return;

		AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
		AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();

		double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea, domainAxisLocation);
		double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea, rangeAxisLocation);

        if( this.plotMinMaxValues)
		{                        
            if (minMaxValues != null && (y1.doubleValue() == minMaxValues[datasetIndex][series].getMaximumValue() || 
            	y1.doubleValue() == minMaxValues[datasetIndex][series].getMinimumValue()))
            {
                java.awt.Shape shape = getItemShape(datasetIndex, series, item);
                shape = createTransformedShape(shape, transX1, transY1);
            	g2.fill(shape);
            }
		}
	}
}
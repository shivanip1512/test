/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Extends StandardXYItemRenderer.
 * Adds a shape representation to an StandardXYItemRenderer of the Min and Max values
 *  for all series in the dataset.
 */

package com.cannontech.jfreechart.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Shapes rendered for MIN/MAX valuse on a Standard item renderer for an XYPlot.
 * This class can draw (a) shapes at each point, or (b) lines between points, 
 * or (c) both shapes and lines.
 */
public class StandardXYItemRenderer_MinMax extends StandardXYItemRenderer
{
	public Dataset_MinMaxValues[] minMaxValues = null;
	private boolean plotMinMaxValues = true;

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES
     * or SHAPES_AND_LINES.
     *
     * @param The type.
     */
    public StandardXYItemRenderer_MinMax(int type) {
        super(type, new StandardXYToolTipGenerator());
    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES
     * or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     * @param toolTipGenerator The tooltip generator.
     */
    public StandardXYItemRenderer_MinMax(int type, XYToolTipGenerator toolTipGenerator) {

        super(type, toolTipGenerator, null);

    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     * @param toolTipGenerator The tooltip generator.
     * @param urlGenerator The URL generator.
     */
    public StandardXYItemRenderer_MinMax(int type,
                                  XYToolTipGenerator toolTipGenerator,
                                  XYURLGenerator urlGenerator) {

        super(type, toolTipGenerator, urlGenerator);
	}

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  The graphics device.
     * @param dataArea  The area within which the data is being drawn.
     * @param info  Collects information about the drawing.
     * @param plot  The plot (can be used to obtain standard color information etc).
     * @param domainAxis  The domain (horizontal) axis.
     * @param rangeAxis  The range (vertical) axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     * @param crosshairInfo Information about crosshairs on a plot.
     */
	public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo,
                         int pass)
	{
		super.drawItem(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairInfo, pass);
		// get the data point...
		Number x1n = dataset.getXValue(series, item);
		Number y1n = dataset.getYValue(series, item);
		if (y1n != null)
		{
//			AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
//			AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
			final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
			final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

		    double x1 = x1n.doubleValue();
		    double y1 = y1n.doubleValue();
		    double transX1 = domainAxis.translateValueToJava2D(x1, dataArea, xAxisLocation);
		    double transY1 = rangeAxis.translateValueToJava2D(y1, dataArea, yAxisLocation);
	
			if( this.plotMinMaxValues)
			{  
				if (minMaxValues != null && (y1 == minMaxValues[series].getMaximumValue() || y1 == minMaxValues[series].getMinimumValue()))
				{
					Shape shape = getItemShape(pass, series);
					shape = createTransformedShape(shape, transX1, transY1);
					g2.fill(shape);
				}
			}
		}
    }
}
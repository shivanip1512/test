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
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

/**
 * Shapes rendered for MIN/MAX valuse on a Line/Step item renderer for an XYPlot.
 * This class draws lines between data points, 
 * only allowing horizontal or vertical lines (steps).
 */

public class XYStepRenderer_MinMax extends XYStepRenderer
{
	public Dataset_MinMaxValues[] minMaxValues = null;
	private boolean plotMinMaxValues = false;
	private boolean plotAllValues = false;
	public static final int NONE = 0;
	public static final int SHAPES = 1;
	public static final int MIN_MAX = 2;
	public static final int MIN_MAX_WITH_SHAPES = 3;
    /**
     * Constructs a new renderer.
     */
    public XYStepRenderer_MinMax() {

        this(NONE);
    }

    /**
     * Constructs a new renderer.
     */
    public XYStepRenderer_MinMax(int type)
    {
        super();
        plotAllValues = (type == SHAPES || type == MIN_MAX_WITH_SHAPES);
        plotMinMaxValues = (type == MIN_MAX || type == MIN_MAX_WITH_SHAPES);
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
     * @param item The pass index.
     */
    public void drawItem(Graphics2D g2,
    					 XYItemRendererState state, 
    					 Rectangle2D dataArea, 
    					 PlotRenderingInfo info,
    					 XYPlot plot, 
    					 ValueAxis domainAxis, 
    					 ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series, 
                         int item,
                         CrosshairState crosshairState,
                         int pass) {
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
		if (!getItemVisible(series, item)) {
			return;   
		}
		
        // get the data point...
        Number x1 = dataset.getX(series, item);
        Number y1 = dataset.getY(series, item);
        if (y1 == null) {
            return;
        }

        double transX1 = domainAxis.valueToJava2D(x1.doubleValue(), dataArea, plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1.doubleValue(), dataArea, plot.getRangeAxisEdge());
		
		PlotOrientation orientation = plot.getOrientation();
		
		boolean drawShape = false;
		if( this.plotAllValues || isPlotMinOrMaxValue(y1.doubleValue(), series))
		{
			Shape shape = getItemShape(series, item);
			if (orientation == PlotOrientation.HORIZONTAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
			}
			else if (orientation == PlotOrientation.VERTICAL) {
				shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
			}
			if (shape.intersects(dataArea)) {
				//draw with opposite fill, so if all shapes is selected, we can see a difference
				if (plotAllValues && isPlotMinOrMaxValue(y1.doubleValue(), series)){
					g2.fill(shape);
				}
				else {
					g2.draw(shape);
				}
			}
		}
	}
    
    private boolean isPlotMinOrMaxValue(double yVal, int series)
    {
        if( minMaxValues != null && plotMinMaxValues)
        {
            return (yVal == minMaxValues[series].getMaximumValue() || yVal == minMaxValues[series].getMinimumValue());
        }
        return false;
    }
}
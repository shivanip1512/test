/*
 * Created on Jan 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.jfreechart.chart;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

/**
 * Area item renderer for an {@link XYPlot}.  This class can draw (a) shapes at each
 * point, or (b) lines between points, or (c) both shapes and lines, or (d)
 * filled areas, or (e) filled areas and shapes.
 */
public class XYStepAreaRenderer_MinMax extends XYStepAreaRenderer
{
	public Dataset_MinMaxValues[] minMaxValues = null;
	private boolean plotMinMaxValues = true;
	
    /**
     * Constructs a new renderer.
     */
    public XYStepAreaRenderer_MinMax() {
        this(AREA);
    }

    /**
     * Constructs a new renderer.
     *
     * @param type  the type of the renderer.
     */
    public XYStepAreaRenderer_MinMax(int type) {
        this(type, null, null);
    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES,
     * SHAPES_AND_LINES, AREA or AREA_AND_SHAPES.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the tool tip generator to use.  <code>null</code> is none.
     * @param urlGenerator  the URL generator (null permitted).
     */
    public XYStepAreaRenderer_MinMax(int type,
                          XYToolTipGenerator toolTipGenerator, XYURLGenerator urlGenerator) {
        super(type, toolTipGenerator, urlGenerator);
     }
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot (<code>null</code> permitted).
     * @param pass  the pass index.
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
        
        PlotOrientation orientation = plot.getOrientation();
        // get the data point...
        // get the data point...
        Number x1 = dataset.getX(series, item);
        Number y1 = dataset.getY(series, item);
        double x = x1.doubleValue();
        double y = y1 == null ? getRangeBase() : y1.doubleValue();
        double transX1 = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
        
        // avoid possible sun.dc.pr.PRException: endPath: bad path
        transY1 = restrictValueToDataArea(transY1, plot, dataArea);
        
		if( this.plotMinMaxValues)
		{  
			if (minMaxValues != null && (y == minMaxValues[series].getMaximumValue() || y == minMaxValues[series].getMinimumValue()))
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
					if (getPlotShapes() && isShapesFilled()) {
						g2.draw(shape);
					}
					else {
						g2.fill(shape);
					}
				}
			}
        }
    }
}


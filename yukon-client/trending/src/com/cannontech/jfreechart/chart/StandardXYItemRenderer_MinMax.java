/*
 * =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 * Extends StandardXYItemRenderer.
 * Adds a shape representation to an StandardXYItemRenderer of the Min and Max values
 * for all series in the dataset.
 */

package com.cannontech.jfreechart.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

/**
 * Shapes rendered for MIN/MAX valuse on a Standard item renderer for an XYPlot.
 * This class can draw (a) shapes at each point, or (b) lines between points,
 * or (c) both shapes and lines.
 */
public class StandardXYItemRenderer_MinMax extends StandardXYItemRenderer {
    public Dataset_MinMaxValues[] minMaxValues = null;
    private boolean plotMinMaxValues = true;

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param The type.
     */
    public StandardXYItemRenderer_MinMax(int type) {
        this(type, null);
    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     * @param toolTipGenerator The tooltip generator.
     */
    public StandardXYItemRenderer_MinMax(int type, XYToolTipGenerator toolTipGenerator) {
        this(type, toolTipGenerator, null);
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
    public StandardXYItemRenderer_MinMax(int type, XYToolTipGenerator toolTipGenerator, XYURLGenerator urlGenerator) {

        super(type, toolTipGenerator, urlGenerator);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2 The graphics device.
     * @param dataArea The area within which the data is being drawn.
     * @param info Collects information about the drawing.
     * @param plot The plot (can be used to obtain standard color information etc).
     * @param domainAxis The domain (horizontal) axis.
     * @param rangeAxis The range (vertical) axis.
     * @param data The dataset.
     * @param series The series index.
     * @param item The item index.
     * @param crosshairInfo Information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info,
            XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item,
            CrosshairState crosshairState, int pass) {
        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState,
            pass);
        if (!getItemVisible(series, item)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(x1) || Double.isNaN(y1)) {
            return;
        }

        double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());

        if (this.plotMinMaxValues) {
            if (minMaxValues != null && minMaxValues[series] != null
                && (y1 == minMaxValues[series].getMaximumValue() || y1 == minMaxValues[series].getMinimumValue())) {
                Shape shape = getItemShape(series, item);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transY1, transX1);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transX1, transY1);
                }
                if (shape.intersects(dataArea)) {
                    // don't fill shape if other shapes are being plotted, so we can see a difference
                    if (getPlotShapes() && getItemShapeFilled(series, item)) {
                        g2.draw(shape);
                    } else {
                        g2.fill(shape);
                    }
                }
            }
        }
    }
}
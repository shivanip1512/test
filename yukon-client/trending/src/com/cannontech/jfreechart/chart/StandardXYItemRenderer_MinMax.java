/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------------------------
 * StandardXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Andreas Schneider;
 *                   Norbert Kiesel (for TBD Networks);
 *
 * $Id: StandardXYItemRenderer.java,v 1.4 2002/08/21 13:16:34 mungady Exp $
 *
 * Changes:
 * --------
 * 19-Oct-2001 : Version 1, based on code by Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 21-Dec-2001 : Added working line instance to improve performance (DG);
 * 22-Jan-2002 : Added code to lock crosshairs to data points.  Based on code by Jonathan Nash (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that the renderer no longer needs to
 *               be immutable (DG);
 * 02-Apr-2002 : Modified to handle null values (DG);
 * 09-Apr-2002 : Modified draw method to return void.  Removed the translated zero from the
 *               drawItem method.  Override the initialise() method to calculate it (DG);
 * 13-May-2002 : Added code from Andreas Schneider to allow changing shapes/colors per item (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 05-Aug-2002 : Incorporated URLs for HTML image maps into chart entities (RA);
 * 08-Aug-2002 : Added discontinuous lines option contributed by Norbert Kiesel (DG);
 * 20-Aug-2002 : Added user definable default values to be returned by protected methods unless
 *               overridden by a subclass (DG);
 *
 */

package com.cannontech.jfreechart.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.cannontech.jfreechart.chart.*;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.StandardXYItemRenderer;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;
import com.jrefinery.chart.urls.StandardXYURLGenerator;

/**
 * Standard item renderer for an XYPlot.  This class can draw (a) shapes at
 * each point, or (b) lines between points, or (c) both shapes and lines.
 */
public class StandardXYItemRenderer_MinMax extends StandardXYItemRenderer
{
	public Dataset_MinMaxValues [] minMaxValues = null;
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
                         XYDataset data,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info!=null) {
            entities = info.getEntityCollection();
        }

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1n = data.getXValue(series, item);
        Number y1n = data.getYValue(series, item);
        if (y1n!=null) {
            double x1 = x1n.doubleValue();
            double y1 = y1n.doubleValue();
            double transX1 = domainAxis.translateValueToJava2D(x1, dataArea);
            double transY1 = rangeAxis.translateValueToJava2D(y1, dataArea);

            Paint paint = getPaint(plot, series, item, transX1, transY1);
            if (paint!=null) {
                g2.setPaint(paint);
            }

            if (getPlotLines()) {

                if (item>0) {
                    // get the previous data point...
                    Number x0n = data.getXValue(series, item-1);
                    Number y0n = data.getYValue(series, item-1);
                    if (y0n!=null) {
                        double x0 = x0n.doubleValue();
                        double y0 = y0n.doubleValue();
                        boolean drawLine = true;
                        if (plotDiscontinuous) {
                            // only draw a line if the gap between the current and previous data
                            // point is within the threshold
                            int numX = data.getItemCount(series);
                            double minX = data.getXValue(series, 0).doubleValue();
                            double maxX = data.getXValue(series, numX-1).doubleValue();
                            drawLine = (x1-x0) <= ((maxX-minX)/numX*getGapThreshold());
                        }
                        if (drawLine) {
                            double transX0 = domainAxis.translateValueToJava2D(x0, dataArea);
                            double transY0 = rangeAxis.translateValueToJava2D(y0, dataArea);

                            line.setLine(transX0, transY0, transX1, transY1);
                            if (line.intersects(dataArea)) {
                                g2.draw(line);
                            }
                        }
                        if( this.plotMinMaxValues)
						{                       
	                        if (minMaxValues[series] != null && (y1 == minMaxValues[series].getMaximumValue() || y1 == minMaxValues[series].getMinimumValue()))
	                        {
				                double scale = getShapeScale(plot, series, item, transX1, transY1);
				                Shape shape = getShape(plot, series, item, transX1, transY1, scale);
	                        	g2.fill(shape);
	                        }
						}
                    }
                }
            }

            if (getPlotShapes()) {

                double scale = getShapeScale(plot, series, item, transX1, transY1);
                Shape shape = getShape(plot, series, item, transX1, transY1, scale);
                if (shape.intersects(dataArea)) {
                    if (isShapeFilled(plot, series, item, transX1, transY1)) {
                        g2.fill(shape);
                    }
                    else {
                        g2.draw(shape);
                    }
                }
                entityArea = shape;

            }

            if (getPlotImages()) {
                // use shape scale with transform??
                double scale = getShapeScale(plot, series, item, transX1, transY1);
                Image image = getImage(plot, series, item, transX1, transY1);
                if (image != null) {
                    Point hotspot = getImageHotspot(plot, series, item,	transX1, transY1, image);
                    g2.drawImage(image,
                                 (int)(transX1-hotspot.getX()),
                                 (int)(transY1-hotspot.getY()), (ImageObserver)null);
                }
                // tooltipArea = image; not sure how to handle this yet
            }

            // add an entity for the item...
            if (entities!=null) {
                if (entityArea==null) {
                    entityArea = new Rectangle2D.Double(transX1-2, transY1-2, 4, 4);
                }
                String tip = "";
                if (getToolTipGenerator()!=null) {
                    tip = getToolTipGenerator().generateToolTip(data, series, item);
                }
                String url = null;
                if (getURLGenerator()!=null) {
                    url = getURLGenerator().generateURL(data, series, item);
                }
                XYItemEntity entity = new XYItemEntity(entityArea, tip, url, series, item);
                entities.addEntity(entity);
            }

            // do we need to update the crosshair values?
            if (domainAxis.isCrosshairLockedOnData()) {
                if (rangeAxis.isCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(x1, y1);
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(x1);
                }
            }
            else {
                if (rangeAxis.isCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(y1);
                }
            }
        }

    }

}
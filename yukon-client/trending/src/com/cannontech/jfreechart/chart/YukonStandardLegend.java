/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * -------------------
 * StandardLegend.java
 * -------------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *
 * $Id: StandardLegend.java,v 1.1 2003/04/23 09:02:00 mungady Exp $
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved some methods [getSeriesPaint(...) etc.] from JFreeChart to Plot (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 06-Feb-2002 : Bug fix for legends in small areas (DG);
 * 23-Apr-2002 : Legend item labels are now obtained from the plot, not the chart (DG);
 * 20-Jun-2002 : Added outline paint and stroke attributes for the key boxes (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Sep-2002 : Changed the name of LegendItem --> DrawableLegendItem (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Adjusted vertical text position in legend item (DG);
 * 17-Oct-2002 : Fixed bug where legend items are not using the font that has been set (DG);
 * 11-Feb-2003 : Added title code by Donald Mitchell, removed unnecessary constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 
 */

package com.cannontech.jfreechart.chart;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.DrawableLegendItem;
import org.jfree.chart.Legend;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.LegendItemEntity;

/**
 * A chart legend shows the names and visual representations of the series
 * that are plotted in a chart.
 *
 * @author David Gilbert
 */
public class YukonStandardLegend extends StandardLegend implements Serializable {

    /**
     * Constructs a new legend with default settings.
     *
     * @param chart  the chart that the legend belongs to.
     */
    public YukonStandardLegend() {

        super();
    	setAnchor(Legend.SOUTH);
    	setItemFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 10));
    }

    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param available  the area within which the legend, and afterwards the plot, should be
     *                   drawn.
     *
     * @return the area used by the legend.
     */
//    public Rectangle2D draw(Graphics2D g2, Rectangle2D available) {
//
//        return draw(g2, available, (getAnchor() & HORIZONTAL) != 0, (getAnchor() & INVERTED) != 0);
//
//    }

    /**
     * Draws the legend.
     *
     * @param g2  the graphics device.
     * @param available  the area available for drawing the chart.
     * @param horizontal  a flag indicating whether the legend items are laid out horizontally.
     * @param inverted ???
     *
     * @return the remaining available drawing area.
     */
	protected Rectangle2D draw(Graphics2D g2, Rectangle2D available,
								  boolean horizontal, boolean inverted,
								  ChartRenderingInfo info) {
								  	
        LegendItemCollection legendItems = getChart().getPlot().getLegendItems();

        if ((legendItems != null) && (legendItems.getItemCount() > 0)) {

            DrawableLegendItem legendTitle = null;

            Rectangle2D legendArea = new Rectangle2D.Double();
            Rectangle2D legendArea_2 = new Rectangle2D.Double();
            double availableWidth = available.getWidth();
            double availableHeight = available.getHeight();
            
            // the translation point for the origin of the drawing system
            Point2D translation = new Point2D.Double();

            // the translation point for the origin of the drawing system
            Point2D translation_2 = new Point2D.Double();

            // Create buffer for individual rectangles within the legend
            DrawableLegendItem[] items = new DrawableLegendItem[legendItems.getItemCount()];

            // Compute individual rectangles in the legend, translation point as well
            // as the bounding box for the legend.
            if (horizontal) {
                double xstart = available.getX() + getOuterGap().getLeftSpace(availableWidth);
                double xlimit = available.getMaxX() 
                                + getOuterGap().getRightSpace(availableWidth) - 1;
            
                xlimit = 0;		//FORCE A NEW LINE ON EACH LEGEND ITEM DRAW
                                                
                                
                double maxRowWidth = 0;
                double xoffset = 0;
                double rowHeight = 0;
                double totalHeight = 0;
                boolean startingNewRow = true;


                if (getTitle() != null && !getTitle().equals("")) {

                    g2.setFont(getTitleFont());

                    LegendItem titleItem = new LegendItem(getTitle(),
                                                          getTitle(),
                                                          null,
                                                          Color.black,
                                                          DEFAULT_OUTLINE_STROKE,
                                                          DEFAULT_OUTLINE_PAINT);

                    legendTitle = createDrawableLegendItem(g2, titleItem,
                                                           xoffset,
                                                           totalHeight);

                    rowHeight = Math.max(rowHeight, legendTitle.getHeight());
                    xoffset += legendTitle.getWidth();
                }

                g2.setFont(getItemFont());
                for (int i = 0; i < legendItems.getItemCount(); i++) {
                    items[i] = createDrawableLegendItem(g2, legendItems.get(i),
                                                        xoffset, totalHeight);
                    if ((!startingNewRow)
                        && (items[i].getX() + items[i].getWidth() + xstart > xlimit)) {

                        maxRowWidth = Math.max(maxRowWidth, xoffset);
                        xoffset = 0;
                        totalHeight += rowHeight;
                        i--;
                        startingNewRow = true;

                    }
                    else {
                        rowHeight = Math.max(rowHeight, items[i].getHeight());
                        xoffset += items[i].getWidth();
                        startingNewRow = false;
                    }
                }

                maxRowWidth = Math.max(maxRowWidth, xoffset);
                totalHeight += rowHeight;

                // Create the bounding box
                legendArea = new Rectangle2D.Double(0, 0, maxRowWidth, totalHeight);
                legendArea_2 = new Rectangle2D.Double(5, 0, maxRowWidth, totalHeight);

                // The yloc point is the variable part of the translation point
                // for horizontal legends. xloc is constant.
                double yloc = (inverted)
                    ? available.getMaxY() - totalHeight 
                                          - getOuterGap().getBottomSpace(availableHeight)
                    : available.getY() + getOuterGap().getTopSpace(availableHeight);
                double xloc = available.getX() + (available.getWidth() / 2) - maxRowWidth / 2;

//				if( xloc  < available.getWidth() / 2)
//					System.out.println(" WE ARE TOO BIG!");
                // Create the translation point
                translation = new Point2D.Double(xloc, yloc);
            }
            else {  // vertical...
                double totalHeight = 0;
                double maxWidth = 0;

                if (getTitle() != null && !getTitle().equals("")) {

                    g2.setFont(getTitleFont());

                    LegendItem titleItem = new LegendItem(getTitle(),
                                                          getTitle(),
                                                          null,
                                                          Color.black,
                                                          DEFAULT_OUTLINE_STROKE,
                                                          DEFAULT_OUTLINE_PAINT);

                    legendTitle = createDrawableLegendItem(g2, titleItem, 0,
                                                           totalHeight);

                    totalHeight += legendTitle.getHeight();
                    maxWidth = Math.max(maxWidth, legendTitle.getWidth());
                }

                g2.setFont(getItemFont());
                for (int i = 0; i < items.length; i++) {
                    items[i] = createDrawableLegendItem(g2, legendItems.get(i),
                                                        0, totalHeight);
                    totalHeight += items[i].getHeight();
                    maxWidth = Math.max(maxWidth, items[i].getWidth());
                }

                // Create the bounding box
                legendArea = new Rectangle2D.Float(0, 0, (float) maxWidth, (float) totalHeight);

                // The xloc point is the variable part of the translation point
                // for vertical legends. yloc is constant.
                double xloc = (inverted)
                    ? available.getMaxX() - maxWidth - getOuterGap().getRightSpace(availableWidth)
                    : available.getX() + getOuterGap().getLeftSpace(availableWidth);
                double yloc = available.getY() + (available.getHeight() / 2) - (totalHeight / 2);

                // Create the translation point
                translation = new Point2D.Double(xloc, yloc);
            }

            // Move the origin of the drawing to the appropriate location
            g2.translate(translation.getX(), translation.getY());

            // Draw the legend's bounding box
            g2.setPaint(getBackgroundPaint());
            g2.fill(legendArea);
            g2.setPaint(getOutlinePaint());
            g2.setStroke(getOutlineStroke());
            g2.draw(legendArea);

/*
            g2.setPaint(getBackgroundPaint());
            g2.fill(legendArea_2);
            g2.setPaint(getOutlinePaint());
            g2.setStroke(getOutlineStroke());
            g2.draw(legendArea_2);
*/
            // draw legend title
            if (legendTitle != null) {
                // XXX dsm - make title bold?
                g2.setPaint(legendTitle.getItem().getFillPaint());
                g2.setPaint(getItemPaint());
                g2.setFont(getTitleFont());
                g2.drawString(legendTitle.getItem().getLabel(),
                              (float) legendTitle.getLabelPosition().getX(),
                              (float) legendTitle.getLabelPosition().getY());
            }

			EntityCollection entities = null;
			if( info != null){
				entities = info.getEntityCollection();
			}
            // Draw individual series elements
            for (int i = 0; i < items.length; i++) {
                g2.setPaint(items[i].getItem().getFillPaint());
                Shape keyBox = items[i].getMarker();
                g2.fill(keyBox);
                if (getOutlineShapes()) {
                    g2.setPaint(getShapeOutlinePaint());
                    g2.setStroke(getShapeOutlineStroke());
                    g2.draw(keyBox);
                }
                g2.setPaint(getItemPaint());
                g2.setFont(getItemFont());
                g2.drawString(items[i].getItem().getLabel(),
                              (float) items[i].getLabelPosition().getX(),
                              (float) items[i].getLabelPosition().getY());
                              
                if (entities != null) {
                	Rectangle2D area = new Rectangle2D.Double(translation.getX() + items[i].getX(),
                	                                          translation.getY() + items[i].getY(),
                	                                          items[i].getWidth(),
                	                                          items[i].getHeight());
                	LegendItemEntity entity = new LegendItemEntity(area);
                	entity.setSeriesIndex(i);
                	entities.add(entity);
                }
            }

            // translate the origin back to what it was prior to drawing the legend
            g2.translate(-translation.getX(), -translation.getY());

            if (horizontal) {
                // The remaining drawing area bounding box will have the same
                // x origin, width and height independent of the anchor's
                // location. The variable is the y coordinate. If the anchor is
                // SOUTH, the y coordinate is simply the original y coordinate
                // of the available area. If it is NORTH, we adjust original y
                // by the total height of the legend and the initial gap.
                double yy = available.getY();
                double yloc = (inverted) ? yy
                                         : yy + legendArea.getHeight() 
                                              + getOuterGap().getBottomSpace(availableHeight);

                // return the remaining available drawing area
                return new Rectangle2D.Double(available.getX(), yloc, availableWidth,
                    availableHeight - legendArea.getHeight() 
                                    - getOuterGap().getTopSpace(availableHeight)
                                    - getOuterGap().getBottomSpace(availableHeight));
            }
            else {
                // The remaining drawing area bounding box will have the same
                // y  origin, width and height independent of the anchor's
                // location. The variable is the x coordinate. If the anchor is
                // EAST, the x coordinate is simply the original x coordinate
                // of the available area. If it is WEST, we adjust original x
                // by the total width of the legend and the initial gap.
                double xloc = (inverted) ? available.getX()
                                         : available.getX()
                                           + legendArea.getWidth()
                                           + getOuterGap().getLeftSpace(availableWidth)
                                           + getOuterGap().getRightSpace(availableWidth);


                // return the remaining available drawing area
                return new Rectangle2D.Double(xloc, available.getY(),
                    availableWidth - legendArea.getWidth() 
                                   - getOuterGap().getLeftSpace(availableWidth)
                                   - getOuterGap().getRightSpace(availableWidth),
                    availableHeight);
            }
        }
        else {
            return available;
        }
    }

    /**
     * Returns a rectangle surrounding a individual entry in the legend.
     * <P>
     * The marker box for each entry will be positioned next to the name of the
     * specified series within the legend area.  The marker box will be square
     * and 70% of the height of current font.
     *
     * @param graphics  the graphics context (supplies font metrics etc.).
     * @param legendItem  the legend item.
     * @param x  the upper left x coordinate for the bounding box.
     * @param y  the upper left y coordinate for the bounding box.
     *
     * @return a DrawableLegendItem encapsulating all necessary info for drawing.
     */
    private DrawableLegendItem createDrawableLegendItem(Graphics2D graphics,
                                                        LegendItem legendItem,
                                                        double x, double y) {

        int innerGap = 2;
        FontMetrics fm = graphics.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(legendItem.getLabel(), graphics);
        float textAscent = lm.getAscent();
        float lineHeight = textAscent + lm.getDescent() + lm.getLeading();

        DrawableLegendItem item = new DrawableLegendItem(legendItem);

        float xloc = (float) (x + innerGap + 1.15f * lineHeight);
        float yloc = (float) (y + innerGap + 0.15f * lineHeight + textAscent);

        item.setLabelPosition(new Point2D.Float(xloc, yloc));

        float boxDim = lineHeight * 0.70f;
        xloc = (float) (x + innerGap + 0.15f * lineHeight);
        yloc = (float) (y + innerGap + 0.15f * lineHeight);

		if (false)
		{	
		//FALSE FOR NOW AS WE DON't DO THIS YET...  
		//Must change but wanted to be able to compile with a realeased version jfreechart0.9.8.jar
//        if (getDisplaySeriesShapes()) {
            Shape marker = legendItem.getShape();
            AffineTransform transformer = AffineTransform.getTranslateInstance(xloc + boxDim / 2, 
                                                                               yloc + boxDim / 2);
            marker = transformer.createTransformedShape(marker);
            item.setMarker(marker);
        }
        else {
            item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));
        }

        float width = (float) (item.getLabelPosition().getX() - x
                                + fm.getStringBounds(legendItem.getLabel(), graphics).getWidth()
                                + 0.5 * textAscent);

        float height = (float) (2 * innerGap + lineHeight);
        item.setBounds(x, y, width, height);
        return item;

    }
    
}

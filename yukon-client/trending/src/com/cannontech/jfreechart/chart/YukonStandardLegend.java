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
 * 05-MAY-2005 : LINE 167 is the only line altered by CANNON.  This alteration requires reimplementing all this 
 * 					other junk since none of the members are protected for extension.  The change allows each legend
 * 					item to be printed on its own line.  The constructor also has Yukon's defaults.  SN - cti 
 */

package com.cannontech.jfreechart.chart;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.DrawableLegendItem;
import org.jfree.chart.Legend;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendRenderingOrder;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;

import com.cannontech.clientutils.CTILogger;

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

		if (legendItems == null || legendItems.getItemCount() == 0) {
			return available; 
		}
		// else...

		DrawableLegendItem legendTitle = null;
		LegendItem titleItem = null; 

		if (getTitle() != null && !getTitle().equals("")) {
			titleItem = new LegendItem(
				getTitle(), getTitle(), (Shape) null, Color.black
			);
		}

		RectangularShape legendArea;
		double availableWidth = available.getWidth();
		// the translation point for the origin of the drawing system
		Point2D translation;

		// Create buffer for individual items within the legend
		List items = new ArrayList();

		// Compute individual rectangles in the legend, translation point as well
		// as the bounding box for the legend.
		if (horizontal) {
			double xstart = available.getX() 
							+ getMargin().calculateLeftOutset(availableWidth);
			double xlimit = available.getMaxX() 
							- getMargin().calculateRightOutset(availableWidth);
            
			xlimit = 0;		//FORCE A NEW LINE ON EACH LEGEND ITEM DRAW
							//CTI THIS IS THE ONLY LINE ALTERED!!  SN20050505
            
			double maxRowWidth = 0;
			double xoffset = 0;
			double rowHeight = 0;
			double totalHeight = 0;
			boolean wrappingAllowed = true;

			if (titleItem != null) {
				g2.setFont(getTitleFont());

				legendTitle = createDrawableLegendItem(
					g2, titleItem, xoffset, totalHeight
				);

				rowHeight = Math.max(0, legendTitle.getHeight());
				xoffset += legendTitle.getWidth();
			}

			g2.setFont(getItemFont());
			for (int i = 0; i < legendItems.getItemCount(); i++) {
				DrawableLegendItem item;
                
				if (getRenderingOrder() == LegendRenderingOrder.STANDARD) {
					item = createDrawableLegendItem(
						g2, legendItems.get(i), xoffset, totalHeight
					);
				}
				else if (getRenderingOrder() == LegendRenderingOrder.REVERSE) {
					item = createDrawableLegendItem(
						g2, legendItems.get(legendItems.getItemCount() - i - 1), xoffset, 
						totalHeight
					);                        
				}
				else {
					// we're not supposed to get here, will cause NullPointerException
					item = null;
				}

				if (item.getMaxX() + xstart > xlimit && wrappingAllowed) {
					// start a new row
					maxRowWidth = Math.max(maxRowWidth, xoffset);
					xoffset = 0;
					totalHeight += rowHeight;
					i--; // redo this item in the next row
					// if item to big to fit, we dont want to attempt wrapping endlessly. 
					// we therefore disable wrapping for at least one item.
					wrappingAllowed = false;  
				}
				else {
					// continue current row
					rowHeight = Math.max(rowHeight, item.getHeight());
					xoffset += item.getWidth();
					// we placed an item in this row, re-allow wrapping for next item.
					wrappingAllowed = true; 
					items.add(item);
				}
			}

			maxRowWidth = Math.max(maxRowWidth, xoffset);
			totalHeight += rowHeight;

			// Create the bounding box
			legendArea = new RoundRectangle2D.Double(
				0, 0, maxRowWidth, totalHeight, getBoundingBoxArcWidth(), getBoundingBoxArcHeight()
			);

			translation = createTranslationPointForHorizontalDraw(
				available, inverted, maxRowWidth, totalHeight 
			);
		}
		else {  // vertical...
			double totalHeight = 0;
			double maxWidth = (getPreferredWidth() == NO_PREFERRED_WIDTH) ? 0 : getPreferredWidth();
            
			if (titleItem != null) {
				g2.setFont(getTitleFont());

				legendTitle = createDrawableLegendItem(g2, titleItem, 0, totalHeight);

				totalHeight += legendTitle.getHeight();
				maxWidth = Math.max(maxWidth, legendTitle.getWidth());
			}

			g2.setFont(getItemFont());
            
			int legendItemsLength = legendItems.getItemCount();
			for (int i = 0; i < legendItemsLength; i++) {
				List drawableParts;
                
				if (getRenderingOrder() == LegendRenderingOrder.STANDARD) {
					drawableParts = createAllDrawableLinesForItem(g2, 
							legendItems.get(i), 0, totalHeight, maxWidth);
				}
				else if (getRenderingOrder() == LegendRenderingOrder.REVERSE) {
					drawableParts = createAllDrawableLinesForItem(
						g2, legendItems.get(legendItemsLength - i - 1), 0, totalHeight, maxWidth
					);
				}
				else {
					// we're not supposed to get here, will cause NullPointerException
					drawableParts = null;
				}
                
				for (Iterator j = drawableParts.iterator(); j.hasNext();) {
					DrawableLegendItem item = (DrawableLegendItem) j.next();
                    
					totalHeight += item.getHeight();
					maxWidth = Math.max(maxWidth, item.getWidth());
                    
					items.add(item);
				}
			}

			// Create the bounding box
			legendArea = new RoundRectangle2D.Float(
				0, 0, (float) maxWidth, (float) totalHeight, 
				getBoundingBoxArcWidth(), getBoundingBoxArcHeight()
			);

			translation = createTranslationPointForVerticalDraw(
				available, inverted, totalHeight, maxWidth 
			);
		}

		// Move the origin of the drawing to the appropriate location
		g2.translate(translation.getX(), translation.getY());

		CTILogger.debug("legendArea = " + legendArea.getWidth() + ", " + legendArea.getHeight());
		drawLegendBox(g2, legendArea);
		drawLegendTitle(g2, legendTitle);
		drawSeriesElements(g2, items, translation, info);

		// translate the origin back to what it was prior to drawing the legend
		g2.translate(-translation.getX(), -translation.getY());

		return calcRemainingDrawingArea(available, horizontal, inverted, legendArea);
        
	}

	/**
	 * Creates a drawable legend item.
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
	 * @return A legend item encapsulating all necessary info for drawing.
	 */
	private DrawableLegendItem createDrawableLegendItem(Graphics2D graphics,
														LegendItem legendItem,
														double x, double y) {

		CTILogger.debug("In createDrawableLegendItem(x = " + x + ", y = " + y);
		int insideGap = 2;
		FontMetrics fm = graphics.getFontMetrics();
		LineMetrics lm = fm.getLineMetrics(legendItem.getLabel(), graphics);
		float textAscent = lm.getAscent();
		float lineHeight = textAscent + lm.getDescent() + lm.getLeading();

		DrawableLegendItem item = new DrawableLegendItem(legendItem);

		float xLabelLoc = (float) (x + insideGap + 1.15f * lineHeight);
		float yLabelLoc = (float) (y + insideGap + 0.5f * lineHeight);

		item.setLabelPosition(new Point2D.Float(xLabelLoc, yLabelLoc));

		float width = (float) (item.getLabelPosition().getX() - x
			+ fm.stringWidth(legendItem.getLabel()) + 0.5 * textAscent);

		float height = (2 * insideGap + lineHeight);
		item.setBounds(x, y, width, height);
		float boxDim = lineHeight * 0.70f;
		float xloc = (float) (x + insideGap + 0.15f * lineHeight);
		float yloc = (float) (y + insideGap + 0.15f * lineHeight);
		if (legendItem.isLineVisible()) {
			Line2D line = new Line2D.Float(
				xloc, yloc + boxDim / 2, xloc + boxDim * 3, yloc + boxDim / 2
			);
			item.setLine(line);
			// lengthen the bounds to accomodate the longer item
			item.setBounds(
				item.getX(), item.getY(), item.getWidth() + boxDim * 2, item.getHeight()
			);
			item.setLabelPosition(new Point2D.Float(xLabelLoc + boxDim * 2, yLabelLoc));
			if (item.getItem().isShapeVisible()) {
				Shape marker = legendItem.getShape();
				AffineTransform t1 = AffineTransform.getScaleInstance(
					getShapeScaleX(), getShapeScaleY()
				);
				Shape s1 = t1.createTransformedShape(marker);
				AffineTransform transformer = AffineTransform.getTranslateInstance(
					xloc + (boxDim * 1.5), yloc + boxDim / 2);
				Shape s2 = transformer.createTransformedShape(s1);
				item.setMarker(s2);
		   }

		} 
		else {
			if (item.getItem().isShapeVisible()) {
				Shape marker = legendItem.getShape();
				AffineTransform t1 = AffineTransform.getScaleInstance(
						getShapeScaleX(), getShapeScaleY()
				);
				Shape s1 = t1.createTransformedShape(marker);
				AffineTransform transformer = AffineTransform.getTranslateInstance(
					xloc + boxDim / 2, yloc + boxDim / 2);
				Shape s2 = transformer.createTransformedShape(s1);
				item.setMarker(s2);
			}
			else {
				item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));
			}
		}
		return item;

	}

	
	/**
	 * ???
	 * 
	 * @param available  the available area.
	 * @param inverted  inverted?
	 * @param maxRowWidth  the maximum row width.
	 * @param totalHeight  the total height.
	 * 
	 * @return The translation point.
	 */
	private Point2D createTranslationPointForHorizontalDraw(Rectangle2D available, 
			boolean inverted, double maxRowWidth, double totalHeight) {
		// The yloc point is the variable part of the translation point
		// for horizontal legends xloc can be: left, center or right.
		double yloc = (inverted)
			? available.getMaxY() - totalHeight
			  - getMargin().calculateBottomOutset(available.getHeight())
			: available.getY() 
			  + getMargin().calculateTopOutset(available.getHeight());
		double xloc;
		if (isAnchoredToLeft()) {
			xloc = available.getX() 
				   + getMargin().calculateLeftOutset(available.getWidth());
		}
		else if (isAnchoredToCenter()) {
			xloc = available.getX() + available.getWidth() / 2 - maxRowWidth / 2;
		}
		else if (isAnchoredToRight()) {
			xloc = available.getX() + available.getWidth()
				   - maxRowWidth - getChart().getPlot().getInsets().left;
		}
		else {
			throw new IllegalStateException("UNEXPECTED_LEGEND_ANCHOR");
		}
        
		// Create the translation point
		return new Point2D.Double(xloc, yloc);
	}
    
	/**
	 * Returns a list of drawable legend items for the specified legend item.
	 * Word-wrapping is applied to the specified legend item and it is broken
	 * into a few lines in order to fit into the specified 
	 * <code>wordWrapWidth</code>.
	 *
	 * @param g2 the graphics context.
	 * @param legendItem  the legend item.
	 * @param x  the upper left x coordinate for the bounding box.
	 * @param y  the upper left y coordinate for the bounding box.
	 * @param wordWrapWidth  the word wrap width.
	 *
	 * @return A list of drawable legend items for the specified legend item.
	 * 
	 * @see #setPreferredWidth(double)
	 */
	private List createAllDrawableLinesForItem(Graphics2D g2,
			LegendItem legendItem, double x, double y, double wordWrapWidth) {
        
		List drawableParts = new ArrayList();

		DrawableLegendItem line = createDrawableLegendItem(g2, legendItem, x, y);

		if (line.getWidth() < wordWrapWidth) {
			// we don't need word-wrapping, return just a single line.
			drawableParts.add(line);
			return drawableParts;
		}
        
		// we need word-wrapping. start laying out the lines. add words to 
		// every line until it's full.
        
		boolean firstLine = true;
		double totalHeight = y;
		String prefix = "";
		String suffix = legendItem.getLabel().trim();
        
		LegendItem tmpItem = new LegendItem(
			prefix.trim(), 
			legendItem.getLabel(), 
			legendItem.isShapeVisible(),
			legendItem.getShape(),
			legendItem.isShapeFilled(), 
			legendItem.getFillPaint(),
			legendItem.isShapeOutlineVisible(),
			legendItem.getOutlinePaint(), 
			legendItem.getOutlineStroke(),
			legendItem.isLineVisible(),
			legendItem.getLine(),
			legendItem.getLineStroke(),
			legendItem.getLinePaint()
		);

		line = createDrawableLegendItem(g2, tmpItem, x, totalHeight);
        
		DrawableLegendItem goodLine = null; // no good known line yet.

		do {
			// save the suffix, we might need to restore it.
			String prevSuffix = suffix; 

			// try to extend the prefix.
			int spacePos = suffix.indexOf(" ");
			if (spacePos < 0) {
				// no space found, append all the suffix to the prefix.
				prefix += suffix;
				suffix = "";
			}
			else {
				// move a word from suffix to prefix.
				prefix += suffix.substring(0, spacePos + 1);
				suffix = suffix.substring(spacePos + 1);
			}
            
			// Create a temporary legend item for the extended prefix.
			// If first line, make its marker visible.
			tmpItem = new LegendItem(
				prefix.trim(),
				legendItem.getLabel(), 
				firstLine && legendItem.isShapeVisible(),
				legendItem.getShape(),
				firstLine && legendItem.isShapeFilled(), 
				legendItem.getFillPaint(),
				firstLine && legendItem.isShapeOutlineVisible(), 
				legendItem.getOutlinePaint(),
				legendItem.getOutlineStroke(),
				firstLine && legendItem.isLineVisible(), 
				legendItem.getLine(),
				legendItem.getLineStroke(),
				legendItem.getLinePaint()
			);
            
			// and create a line for it as well.
			line = createDrawableLegendItem(g2, tmpItem, x, totalHeight);

			// now check if line fits in width.
			if (line.getWidth() < wordWrapWidth) {
				// fits! save it as the last good known line.
				goodLine = line;
			}
			else {
				// doesn't fit. do we have a saved good line? 
				if (goodLine == null) {
					// nope. this means we will have to add it anyway and exceed 
					// the desired wordWrapWidth. life is tough sometimes...
					drawableParts.add(line);
					totalHeight += line.getHeight();
				}
				else {
					// yep, we have a saved good line, and we intend to use it...
					drawableParts.add(goodLine);
					totalHeight += goodLine.getHeight();
					// restore previous suffix.
					suffix = prevSuffix;
				}
				// prepare to start a new line.
				firstLine = false;
				prefix = "";
				suffix = suffix.trim();
				line = null; // mark as used to avoid using twice.
				goodLine = null; // mark as used to avoid using twice.
			}
		} 
		while (!suffix.equals(""));
        
		// make sure not to forget last line.
		if (line != null) {
			drawableParts.add(line);
		}
        
		return drawableParts;
	}
    
	/**
	 * ???
	 * 
	 * @param available  the available area.
	 * @param inverted  inverted?
	 * @param totalHeight  the total height.
	 * @param maxWidth  the maximum width.
	 * 
	 * @return The translation point.
	 */
	private Point2D createTranslationPointForVerticalDraw(Rectangle2D available, 
			boolean inverted, double totalHeight, double maxWidth) {
		// The xloc point is the variable part of the translation point
		// for vertical legends yloc can be: top, middle or bottom.
		double xloc = (inverted)
			? available.getMaxX() - maxWidth 
			  - getMargin().calculateRightOutset(available.getWidth())
			: available.getX() 
			  + getMargin().calculateLeftOutset(available.getWidth());
		double yloc;
		if (isAnchoredToTop()) {
			yloc = available.getY() + getChart().getPlot().getInsets().top;
		}
		else if (isAnchoredToMiddle()) {
			yloc = available.getY() + (available.getHeight() / 2) - (totalHeight / 2);
		}
		else if (isAnchoredToBottom()) {
			yloc = available.getY() + available.getHeight() 
				   - getChart().getPlot().getInsets().bottom - totalHeight;
		}
		else {
			throw new IllegalStateException("UNEXPECTED_LEGEND_ANCHOR");
		}
		// Create the translation point
		return new Point2D.Double(xloc, yloc);
	}

	/**
	 * Draws the legend title.
	 * 
	 * @param g2  the graphics device (<code>null</code> not permitted).
	 * @param legendTitle  the title (<code>null</code> permitted, in which case the method 
	 *                     does nothing).
	 */
	private void drawLegendTitle(Graphics2D g2, DrawableLegendItem legendTitle) {
		if (legendTitle != null) {
			// XXX dsm - make title bold?
			g2.setPaint(legendTitle.getItem().getFillPaint());
			g2.setPaint(getItemPaint());
			g2.setFont(getTitleFont());
			TextUtilities.drawAlignedString(
				legendTitle.getItem().getLabel(), g2,
				(float) legendTitle.getLabelPosition().getX(),
				(float) legendTitle.getLabelPosition().getY(), TextAnchor.CENTER_LEFT
			);
			CTILogger.debug("Title x = " + legendTitle.getLabelPosition().getX());
			CTILogger.debug("Title y = " + legendTitle.getLabelPosition().getY());
		}
	}

	/**
	 * Draws the bounding box for the legend.
	 * 
	 * @param g2  the graphics device.
	 * @param legendArea  the legend area.
	 */
	private void drawLegendBox(Graphics2D g2, RectangularShape legendArea) {
		// Draw the legend's bounding box
		g2.setPaint(getBackgroundPaint());
		g2.fill(legendArea);
		g2.setPaint(getOutlinePaint());
		g2.setStroke(getOutlineStroke());
		g2.draw(legendArea);
	}
    
	/**
	 * Draws the series elements.
	 * 
	 * @param g2  the graphics device.
	 * @param items  the items.
	 * @param translation  the translation point.
	 * @param info  optional carrier for rendering info.
	 */
	private void drawSeriesElements(Graphics2D g2, List items, Point2D translation, 
									ChartRenderingInfo info) {
		EntityCollection entities = null;
		if (info != null) {
			entities = info.getEntityCollection();
		}
		// Draw individual series elements
		for (int i = 0; i < items.size(); i++) {
			DrawableLegendItem item = (DrawableLegendItem) items.get(i);
			g2.setPaint(item.getItem().getFillPaint());
			Shape keyBox = item.getMarker();
			if (item.getItem().isLineVisible()) {
				g2.setStroke(item.getItem().getLineStroke());
				g2.draw(item.getLine());

				if (item.getItem().isShapeVisible()) {
					if (item.getItem().isShapeFilled()) {
						g2.fill(keyBox);
					}
					else {
						g2.draw(keyBox);
					}
				}
			} 
			else {
				if (item.getItem().isShapeFilled()) {
					g2.fill(keyBox);
				}
				if (item.getItem().isShapeOutlineVisible()) {
					g2.setPaint(item.getItem().getOutlinePaint());
					g2.setStroke(item.getItem().getOutlineStroke());
					g2.draw(keyBox);
				}
			}
			g2.setPaint(getItemPaint());
			g2.setFont(getItemFont());
			TextUtilities.drawAlignedString(
				item.getItem().getLabel(), g2,
				(float) item.getLabelPosition().getX(), (float) item.getLabelPosition().getY(),
				TextAnchor.CENTER_LEFT
			);
			CTILogger.debug("Item x = " + item.getLabelPosition().getX());
			CTILogger.debug("Item y = " + item.getLabelPosition().getY());

			if (entities != null) {
				Rectangle2D area = new Rectangle2D.Double(
					translation.getX() + item.getX(),
					translation.getY() + item.getY(),
					item.getWidth(), item.getHeight()
				);
				LegendItemEntity entity = new LegendItemEntity(area);
				entity.setSeriesIndex(i);
				entities.add(entity);
			}
		}
	}

	/**
	 * Calculates the remaining drawing area.
	 * 
	 * @param available  the available area.
	 * @param horizontal  horizontal?
	 * @param inverted  inverted?
	 * @param legendArea  the legend area.
	 * 
	 * @return The remaining drawing area.
	 */
	private Rectangle2D calcRemainingDrawingArea(Rectangle2D available, 
			boolean horizontal, boolean inverted, RectangularShape legendArea) {
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
				+ getMargin().calculateBottomOutset(available.getHeight());

			// return the remaining available drawing area
			return new Rectangle2D.Double(
				available.getX(), yloc, available.getWidth(),
				available.getHeight() - legendArea.getHeight()
				- getMargin().calculateTopOutset(available.getHeight())
				- getMargin().calculateBottomOutset(available.getHeight())
			);
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
				+ getMargin().calculateLeftOutset(available.getWidth())
				+ getMargin().calculateRightOutset(available.getWidth());


			// return the remaining available drawing area
			return new Rectangle2D.Double(
				xloc, available.getY(),
				available.getWidth() - legendArea.getWidth()
				- getMargin().calculateLeftOutset(available.getWidth())
				- getMargin().calculateRightOutset(available.getWidth()),
				available.getHeight()
			);
		}
	}
}
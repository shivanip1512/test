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
 * -------------------
 * StandardLegend.java
 * -------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *
 * $Id: StandardLegend.java,v 1.1.1.1 2002/08/16 20:30:44 stew Exp $
 * 09-Sep-2002 : Extended the Standard Legend to allow for Vertical Legend Items to be drawn. (SN-CTI)
 */

package com.cannontech.jfreechart.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.jrefinery.chart.DrawableLegendItem;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.LegendItem;
import com.jrefinery.chart.LegendItemCollection;
import com.jrefinery.chart.Spacer;
import com.jrefinery.chart.StandardLegend;

/**
 * A chart legend shows the names and visual representations of the series
 * that are plotted in a chart.
 */
public class StandardLegend_VerticalItems extends StandardLegend
{
	String[] statsString = null;
	/**
	 * Constructs a new legend with default settings.
	 *
	 * @param chart The chart that the legend belongs to.
	 */
	public StandardLegend_VerticalItems(JFreeChart chart)
	{
		//this(chart,
		super(chart,
			 3,
			 new Spacer(Spacer.ABSOLUTE, 2, 2, 2, 2),
			 Color.white, new BasicStroke(), Color.gray,
			 DEFAULT_FONT, Color.black);
	}

	/**
	 * Constructs a new legend.
	 *
	 * @param chart     The chart that the legend belongs to.
	 * @param outerGap      The gap around the outside of the legend.
	 * @param innerGap      The gap inside the legend.
	 * @param backgroundPaint   The background color.
	 * @param outlineStroke     The pen/brush used to draw the outline.
	 * @param outlinePaint      The color used to draw the outline.
	 * @param itemFont      The font used to draw the legend items.
	 * @param itemPaint     The color used to draw the legend items.
	 */
	public StandardLegend_VerticalItems(JFreeChart chart,
		int outerGap, Spacer innerGap,
		Paint backgroundPaint,
		Stroke outlineStroke, Paint outlinePaint,
		Font itemFont, Paint itemPaint)
	{
		super(chart, outerGap, innerGap,backgroundPaint, outlineStroke, outlinePaint, itemFont, itemPaint);
	}

	/**
	 * Draws the legend on a Java 2D graphics device (such as the screen or a
	 * printer).
	 *
	 * @param g2    The graphics device.
	 * @param available     The area within which the legend, and afterwards
	 *      the plot, should be drawn.
	 *
	 * @return The area used by the legend.
	 */
	public Rectangle2D draw(Graphics2D g2, Rectangle2D available)
	{
		return draw(g2, available, (getAnchor() & HORIZONTAL)!=0, (getAnchor() & INVERTED)!=0);
	}

	/**
	 * Draws the legend.
	 *
	 * @param g2      The graphics device.
	 * @param available     The area available for drawing the chart.
	 * @param horizontal    A flag indicating whether the legend items are laid
	 *      out horizontally.
	 * @param inverted ???
	 *
	 * @return the remaining available drawing area
	 */
	protected Rectangle2D draw(Graphics2D g2, Rectangle2D available,
							   boolean horizontal, boolean inverted) {
		LegendItemCollection legendItems = getChart().getPlot().getLegendItems();
		
		if ((legendItems!=null) && (legendItems.getItemCount()>0)) {

			Rectangle2D legendArea = new Rectangle2D.Double();

			// the translation point for the origin of the drawing system
			Point2D translation = new Point2D.Double();

			// Create buffer for individual rectangles within the legend
            DrawableLegendItem[] items = new DrawableLegendItem[legendItems.getItemCount()];
            DrawableLegendItem[] stats = new DrawableLegendItem[legendItems.getItemCount()];
			g2.setFont(getItemFont());

			// Compute individual rectangles in the legend, translation point as well
			// as the bounding box for the legend.
			double totalWidth = 0;
			double totalHeight = 0;			
			double  maxWidth = 0;
			double  maxStatsWidth = 0;

			double divideBy = 4;
			double legendStart = 0;
			if (horizontal)
			{
				double xoffset = 0;

				for (int i=0; i<legendItems.getItemCount(); i++) 
				{
					items[i] = createDrawableLegendItem(g2, legendItems.get(i), 0, totalHeight);
					maxWidth = Math.max(maxWidth, items[i].getWidth());
					totalHeight +=items[i].getHeight();
				}
				if( statsString != null)
				{
					totalHeight = 0;	
					divideBy = 4;				
					for (int i=0; i<statsString.length; i++) 
					{
						stats[i] = createDrawableLegendItem_stat(g2, legendItems.get(i), maxWidth, totalHeight, statsString[i]);
						maxStatsWidth = Math.max(maxStatsWidth, stats[i].getWidth());
						totalHeight +=items[i].getHeight();
					}
					totalWidth = maxWidth + maxStatsWidth;
					legendStart = 0 - totalWidth / 4d;
				}
				else
				{
					totalWidth = maxWidth;
				}

				// Create the bounding box
				legendArea = new Rectangle2D.Double(legendStart, 0, totalWidth, totalHeight);
				// The yloc point is the variable part of the translation point
				// for horizontal legends. xloc is constant.
				double yloc = (inverted) ?
					available.getY() + available.getHeight() - totalHeight - getOuterGap() :
					available.getY() + getOuterGap();
				double xloc = available.getX() + available.getWidth()/2 - maxWidth/2;

				// Create the translation point
				translation = new Point2D.Double(xloc,yloc);
			}
			else {  // vertical...
				totalHeight = 0;
				maxWidth = 0;
				g2.setFont(getItemFont());
				for (int i = 0; i < items.length; i++) {
					items[i] = createDrawableLegendItem(g2, legendItems.get(i), 0, totalHeight);
					totalHeight +=items[i].getHeight();
					maxWidth = Math.max(maxWidth, items[i].getWidth());
				}

				// Create the bounding box
				legendArea = new Rectangle2D.Float(0, 0, (float)maxWidth, (float)totalHeight);

				// The xloc point is the variable part of the translation point
				// for vertical legends. yloc is constant.
				double xloc = (inverted) ?
								  available.getMaxX()-maxWidth-getOuterGap():
								  available.getX()+getOuterGap();
				double yloc = available.getY()+(available.getHeight()/2)-(totalHeight/2);

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

//			float startOffset = (float) (0- totalWidth/4d + 2);
			// Draw individual series elements
			for (int i=0; i<items.length; i++)
			{
				float itemHeight = (float)items[i].getHeight();
				float markerSize = itemHeight/2;
				
				g2.setPaint(getChart().getPlot().getSeriesPaint(i));
				items[i].setMarker(new Rectangle2D.Float((float)legendStart + 2,(float)items[i].getLabelPosition().getY() - markerSize, 
						markerSize, markerSize ));
				Shape keyBox = items[i].getMarker();
				g2.fill(keyBox);
				if (getOutlineKeyBoxes()) {
					g2.setPaint(getKeyBoxOutlinePaint());
					g2.setStroke(getKeyBoxOutlineStroke());
					g2.draw(keyBox);
				}
				g2.setPaint(getItemPaint());
				g2.drawString(items[i].getItem().getLabel(),(float)(legendStart +14),(float)items[i].getLabelPosition().getY());
//									(float)items[i].labelPosition.getX(),
//									(float)items[i].labelPosition.getY());
//				if( statsString != null && stats[i] != null)
//					g2.drawString(statsString[i],(float)(legendStart + maxWidth + 14), (float)stats[i].getLabelPosition().getY());

				if( statsString != null)
					g2.drawString(statsString[i],(float)(legendStart + maxWidth + 14), (float)items[i].getLabelPosition().getY());


			}

			// translate the origin back to what it was prior to drawing the
			// legend
			g2.translate(-translation.getX(),-translation.getY());

			if (horizontal) {
				// The remaining drawing area bounding box will have the same
				// x origin, width and height independent of the anchor's
				// location. The variable is the y coordinate. If the anchor is
				// SOUTH, the y coordinate is simply the original y coordinate
				// of the available area. If it is NORTH, we adjust original y
				// by the total height of the legend and the initial gap.
				double yloc = (inverted) ? available.getY() :
							  available.getY()+legendArea.getHeight()+getOuterGap();

				// return the remaining available drawing area
				return new Rectangle2D.Double(available.getX(), yloc,
					available.getWidth(),
					available.getHeight()-legendArea.getHeight()-2*getOuterGap());
			}
			else {
				// The remaining drawing area bounding box will have the same
				// y  origin, width and height independent of the anchor's
				// location. The variable is the x coordinate. If the anchor is
				// EAST, the x coordinate is simply the original x coordinate
				// of the available area. If it is WEST, we adjust original x
				// by the total width of the legend and the initial gap.
				double xloc = (inverted) ? available.getX() :
					available.getX()+legendArea.getWidth()+2*getOuterGap();

				// return the remaining available drawing area
				return new Rectangle2D.Double(xloc, available.getY(),
					available.getWidth()-legendArea.getWidth()-2 * getOuterGap(),
					available.getHeight());
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
	 * @param graphics      The graphics context (supplies font metrics etc.).
	 * @param label     The series name.
	 * @param x     The upper left x coordinate for the bounding box.
	 * @param y     The upper left y coordinate for the bounding box.
	 * @return  A LegendItem encapsulating all necessary info for drawing.
	 */
	/**
	 * Returns a rectangle surrounding a individual entry in the legend.
     * <P>
	 * The marker box for each entry will be positioned next to the name of the
	 * specified series within the legend area.  The marker box will be square
	 * and 70% of the height of current font.
	 *
	 * @param graphics      The graphics context (supplies font metrics etc.).
	 * @param label     The series name.
	 * @param x     The upper left x coordinate for the bounding box.
	 * @param y     The upper left y coordinate for the bounding box.
	 * @return  A LegendItem encapsulating all necessary info for drawing.
	 */
	private DrawableLegendItem createDrawableLegendItem(Graphics2D graphics, LegendItem legendItem,
		double x, double y)
	{

		int innerGap = 2;
		java.awt.FontMetrics fm = graphics.getFontMetrics();
		java.awt.font.LineMetrics lm = fm.getLineMetrics(legendItem.getLabel(), graphics);
		float textHeight = lm.getHeight();
	
		DrawableLegendItem item = new DrawableLegendItem(legendItem);

		float xloc = (float)(x+innerGap+1.15f*textHeight);
		float yloc = (float)(y+innerGap+(textHeight-lm.getLeading()
			- lm.getDescent()));

		item.setLabelPosition(new Point2D.Float(xloc, yloc));

		float boxDim = textHeight*0.70f;
		xloc = (float)(x+innerGap+0.15f*textHeight);
		yloc = (float)(y+innerGap+0.15f*textHeight);

//		item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));
		float width = (float)(item.getLabelPosition().getX()-x
			+ fm.getStringBounds(legendItem.getLabel(),graphics).getWidth()+0.5*textHeight);

		float height = (float)(2*innerGap+textHeight);
		item.setBounds(x, y, width, height);
		return item;

	}

	/**
	 * Returns a rectangle surrounding a individual entry in the legend.
     * <P>
	 * The marker box for each entry will be positioned next to the name of the
	 * specified series within the legend area.  The marker box will be square
	 * and 70% of the height of current font.
	 *
	 * @param graphics      The graphics context (supplies font metrics etc.).
	 * @param label     The series name.
	 * @param x     The upper left x coordinate for the bounding box.
	 * @param y     The upper left y coordinate for the bounding box.
	 * @return  A LegendItem encapsulating all necessary info for drawing.
	 */
	/**
	 * Returns a rectangle surrounding a individual entry in the legend.
     * <P>
	 * The marker box for each entry will be positioned next to the name of the
	 * specified series within the legend area.  The marker box will be square
	 * and 70% of the height of current font.
	 *
	 * @param graphics      The graphics context (supplies font metrics etc.).
	 * @param label     The series name.
	 * @param x     The upper left x coordinate for the bounding box.
	 * @param y     The upper left y coordinate for the bounding box.
	 * @return  A LegendItem encapsulating all necessary info for drawing.
	 */
//	private DrawableLegendItem createDrawableLegendItem_stat(Graphics2D graphics, double x, double y, String statLabel)

	private DrawableLegendItem createDrawableLegendItem_stat(Graphics2D graphics, LegendItem legendItem,
		double x, double y, String statLabel)
	{
		int innerGap = 2;
		java.awt.FontMetrics fm = graphics.getFontMetrics();
		java.awt.font.LineMetrics lm = fm.getLineMetrics(statLabel, graphics);
		float textHeight = lm.getHeight();
	
		DrawableLegendItem item = new DrawableLegendItem(legendItem);

		float xloc = (float)(x+innerGap+1.15f*textHeight);
		float yloc = (float)(y+innerGap+(textHeight-lm.getLeading()
			- lm.getDescent()));

		item.setLabelPosition(new Point2D.Float(xloc, yloc));

		float boxDim = textHeight*0.70f;
		xloc = (float)(x+innerGap+0.15f*textHeight);
		yloc = (float)(y+innerGap+0.15f*textHeight);

//		item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));
		float width = (float)(item.getLabelPosition().getX()-x
			+ fm.getStringBounds(statLabel,graphics).getWidth()+0.5*textHeight);

		float height = (float)(2*innerGap+textHeight);
		item.setBounds(x, y, width, height);
		return item;

	}





	public void setStatsString(String[] newStats)
	{
		statsString = newStats;
	}

	public String[] getStatsString()
	{
		return statsString;
	}
		
}
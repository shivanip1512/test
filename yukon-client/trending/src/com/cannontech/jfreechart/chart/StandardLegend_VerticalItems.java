/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Extends StandardLegend.
 * Forces the legend items to be drawn each on one line.
 * Also allows for the addition of 'otherInfo', or rather more info to be add
 *  to the item string in the legend box.
 *
 * createDrawableLegendItem_info function was added that is similar to createDrawableLegendItem
 * but also allows for an additional string to be appended to the item name.
 */

package com.cannontech.jfreechart.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.jrefinery.chart.DrawableLegendItem;
import com.jrefinery.chart.LegendItem;

/**
 * A chart legend shows the names and visual representations of the series
 * that are plotted in a chart.
 *
 * @author DG
 */
public class StandardLegend_VerticalItems extends com.jrefinery.chart.StandardLegend
{
	String[] otherInfo = null;
    /**
     * Constructs a new legend with default settings.
     *
     * @param chart  the chart that the legend belongs to.
     */
    public StandardLegend_VerticalItems(com.jrefinery.chart.JFreeChart chart) {

        this(chart,
             3,
             new com.jrefinery.chart.Spacer(com.jrefinery.chart.Spacer.ABSOLUTE, 2, 2, 2, 2),
             Color.white, new BasicStroke(), Color.gray,
             DEFAULT_FONT, Color.black);

    }

    /**
     * Constructs a new legend.
     *
     * @param chart  the chart that the legend belongs to.
     * @param outerGap  the gap around the outside of the legend.
     * @param innerGap  the gap inside the legend.
     * @param backgroundPaint  the background color.
     * @param outlineStroke  the pen/brush used to draw the outline.
     * @param outlinePaint  the color used to draw the outline.
     * @param itemFont  the font used to draw the legend items.
     * @param itemPaint  the color used to draw the legend items.
     */
    public StandardLegend_VerticalItems(com.jrefinery.chart.JFreeChart chart,
                          int outerGap, com.jrefinery.chart.Spacer innerGap,
                          Paint backgroundPaint,
                          Stroke outlineStroke, Paint outlinePaint,
                          Font itemFont, Paint itemPaint) {

        super(chart, outerGap, innerGap, backgroundPaint, outlineStroke, outlinePaint, itemFont, itemPaint);
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
                               boolean horizontal, boolean inverted) {

        com.jrefinery.chart.LegendItemCollection legendItems = getChart().getPlot().getLegendItems();

        if ((legendItems != null) && (legendItems.getItemCount() > 0)) {

            Rectangle2D legendArea = new Rectangle2D.Double();

            // the translation point for the origin of the drawing system
            Point2D translation = new Point2D.Double();

            // Create buffer for individual rectangles within the legend
            DrawableLegendItem[] items = new DrawableLegendItem[legendItems.getItemCount()];
			DrawableLegendItem[] info = new DrawableLegendItem[legendItems.getItemCount()];
            g2.setFont(getItemFont());
			
			double totalWidth = 0;
			double totalHeight = 0;			
			double  maxWidth = 0;
			double  maxInfoWidth = 0;

			double divideBy = 4;
			double legendStart = 0;
            // Compute individual rectangles in the legend, translation point as well
            // as the bounding box for the legend.
            if (horizontal) {
//                double xstart = available.getX() + getOuterGap();
//                double xlimit = available.getX() + available.getWidth() - 2 * getOuterGap() - 1;
//                double maxRowWidth = 0;
//                double xoffset = 0;
//                double rowHeight = 0;
//                boolean startingNewRow = true;

				for (int i=0; i<legendItems.getItemCount(); i++) 
				{
					items[i] = createDrawableLegendItem(g2, legendItems.get(i), 0, totalHeight);
					maxWidth = Math.max(maxWidth, items[i].getWidth());
					totalHeight +=items[i].getHeight();
				}
				if( otherInfo != null)
				{
					totalHeight = 0;	
					divideBy = 4;				
					for (int i=0; i<otherInfo.length; i++) 
					{
						info[i] = createDrawableLegendItem_info(g2, legendItems.get(i), maxWidth, totalHeight, otherInfo[i]);
						maxInfoWidth = Math.max(maxInfoWidth, info[i].getWidth());
						totalHeight +=items[i].getHeight();
					}
					totalWidth = maxWidth + maxInfoWidth;
					legendStart = 0 - totalWidth / 4d;
				}
				else
				{
					totalWidth = maxWidth;
				}

//                maxRowWidth = Math.max(maxRowWidth, xoffset);
//                totalHeight += rowHeight;

                // Create the bounding box
                legendArea = new Rectangle2D.Double(legendStart, 0, totalWidth, totalHeight);

                // The yloc point is the variable part of the translation point
                // for horizontal legends. xloc is constant.
                double yloc = (inverted)
                    ? available.getY() + available.getHeight() - totalHeight - getOuterGap()
                    : available.getY() + getOuterGap();
                double xloc = available.getX() + available.getWidth() / 2 - maxWidth / 2;

                // Create the translation point
                translation = new Point2D.Double(xloc, yloc);
            }
            else {  // vertical...
//                double totalHeight = 0;
//                double maxWidth = 0;
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
                    ? available.getMaxX() - maxWidth - getOuterGap()
                    : available.getX() + getOuterGap();
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

            // Draw individual series elements
            for (int i = 0; i < items.length; i++) {

				float itemHeight = (float)items[i].getHeight();
				float markerSize = itemHeight/2;
				items[i].setMarker(new Rectangle2D.Float((float)legendStart + 2,(float)items[i].getLabelPosition().getY() - markerSize, 
						markerSize, markerSize ));

                g2.setPaint(items[i].getItem().getPaint());
                Shape keyBox = items[i].getMarker();
                g2.fill(keyBox);
                if (getOutlineKeyBoxes()) {
                    g2.setPaint(getKeyBoxOutlinePaint());
                    g2.setStroke(getKeyBoxOutlineStroke());
                    g2.draw(keyBox);
                }
                g2.setPaint(getItemPaint());
                g2.setFont(getItemFont());
				g2.drawString(items[i].getItem().getLabel(),(float)(legendStart +14),(float)items[i].getLabelPosition().getY());
//                              (float) items[i].getLabelPosition().getX(),
//                              (float) items[i].getLabelPosition().getY());
				if( otherInfo != null)
					g2.drawString(otherInfo[i],(float)(legendStart + maxWidth + 14), (float)items[i].getLabelPosition().getY());
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
                                         : yy + legendArea.getHeight() + getOuterGap();

                // return the remaining available drawing area
                return new Rectangle2D.Double(available.getX(), yloc, available.getWidth(),
                    available.getHeight() - legendArea.getHeight() - 2 * getOuterGap());
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
                                           + legendArea.getWidth() + 2 * getOuterGap();

                // return the remaining available drawing area
                return new Rectangle2D.Double(xloc, available.getY(),
                    available.getWidth() - legendArea.getWidth() - 2 * getOuterGap(),
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
     * @param graphics  the graphics context (supplies font metrics etc.).
     * @param legendItem  the legend item.
     * @param x  the upper left x coordinate for the bounding box.
     * @param y  the upper left y coordinate for the bounding box.
     *
     * @return a DrawableLegendItem encapsulating all necessary info for drawing.
     */
    private com.jrefinery.chart.DrawableLegendItem createDrawableLegendItem(Graphics2D graphics,
                                                        com.jrefinery.chart.LegendItem legendItem,
                                                        double x, double y) {

        int innerGap = 2;
        FontMetrics fm = graphics.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(legendItem.getLabel(), graphics);
        float textAscent = lm.getAscent();
        float lineHeight = textAscent + lm.getDescent() + lm.getLeading();

        com.jrefinery.chart.DrawableLegendItem item = new com.jrefinery.chart.DrawableLegendItem(legendItem);

        float xloc = (float) (x + innerGap + 1.15f * lineHeight);
        float yloc = (float) (y + innerGap + 0.15f * lineHeight + textAscent);

        item.setLabelPosition(new Point2D.Float(xloc, yloc));

        float boxDim = lineHeight * 0.70f;
        xloc = (float) (x + innerGap + 0.15f * lineHeight);
        yloc = (float) (y + innerGap + 0.15f * lineHeight);

        item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));

        float width = (float) (item.getLabelPosition().getX() - x
                               + fm.getStringBounds(legendItem.getLabel(), graphics).getWidth()
                               + 0.5 * textAscent);

        float height = (float) (2 * innerGap + lineHeight);
        item.setBounds(x, y, width, height);
        return item;

    }
private DrawableLegendItem createDrawableLegendItem_info(Graphics2D graphics, LegendItem legendItem,
		double x, double y, String infoLabel)
	{
		int innerGap = 2;
		java.awt.FontMetrics fm = graphics.getFontMetrics();
		java.awt.font.LineMetrics lm = fm.getLineMetrics(infoLabel, graphics);
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
			+ fm.getStringBounds(infoLabel,graphics).getWidth()+0.5*textHeight);

		float height = (float)(2*innerGap+textHeight);
		item.setBounds(x, y, width, height);
		return item;

	}

	public void setOtherInfo(String[] newInfo)
	{
		otherInfo= newInfo;
	}

	public String[] getOtherInfo()
	{
		return otherInfo;
	}
}
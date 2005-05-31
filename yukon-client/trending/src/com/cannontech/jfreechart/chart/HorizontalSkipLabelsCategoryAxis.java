/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.jfreechart.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryTick;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.text.TextBlock;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

/**
 * @author snebben
 */
public class HorizontalSkipLabelsCategoryAxis extends CategoryAxis
{
	public HorizontalSkipLabelsCategoryAxis(String label)
	{
		super(label);
	}
	
	/**
	 * Overrides to draw only 5 (or 6) labels, using mod functionality.
	 * 
	 * Draws the category labels when the axis is 'horizontal'. 
	 * @param g2 the graphics device.
	 * @param plotArea the plot area.
	 * @param dataArea the area inside the axes.
	 * @param edge the axis location.
	 * @param state the axis state.
	 * @return The revised axis state.
	*/
	protected AxisState drawCategoryLabels( Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state, PlotRenderingInfo plotState)
	{        
		if (logger.isDebugEnabled()) {
		logger.debug(
				"Entering drawCategoryLabels() method, cursor = " 
				+ state.getCursor()
			);
		}
		if (state == null) {
			throw new IllegalArgumentException("Null 'state' argument.");
		}

		if (isTickLabelsVisible()) {
			g2.setFont(getTickLabelFont());
			g2.setPaint(getTickLabelPaint());
			List ticks = refreshTicks(g2, state, plotArea, dataArea, edge);
			state.setTicks(ticks);        
          
			int categoryIndex = 0;
			Iterator iterator = ticks.iterator();

			int i = 0;			//CTI
			int numLabels = 5;	//CTI
			int mod = ticks.size();
			if (mod > 5 )
				mod = ticks.size()/ 5;	//CTI

			
			while (iterator.hasNext()) {
				CategoryTick tick = (CategoryTick) iterator.next();
				if (i++%mod==0)
				{                
				g2.setPaint(getTickLabelPaint());

				CategoryLabelPosition position 
					= super.getCategoryLabelPositions().getLabelPosition(edge);
				double x0 = 0.0;
				double x1 = 0.0;
				double y0 = 0.0;
				double y1 = 0.0;
				if (edge == RectangleEdge.TOP) {
					x0 = getCategoryStart(
						categoryIndex, ticks.size(), dataArea, edge
					);
					x1 = getCategoryEnd(
						categoryIndex, ticks.size(), dataArea, edge
					);
					y1 = state.getCursor() - super.getCategoryLabelPositionOffset();
					y0 = y1 - state.getMax();
				}
				else if (edge == RectangleEdge.BOTTOM) {
					x0 = getCategoryStart(
						categoryIndex, ticks.size(), dataArea, edge
					);
					x1 = getCategoryEnd(
						categoryIndex, ticks.size(), dataArea, edge
					); 
					y0 = state.getCursor() + super.getCategoryLabelPositionOffset();
					y1 = y0 + state.getMax();
				}
				else if (edge == RectangleEdge.LEFT) {
					y0 = getCategoryStart(
						categoryIndex, ticks.size(), dataArea, edge
					);
					y1 = getCategoryEnd(
						categoryIndex, ticks.size(), dataArea, edge
					);
					x1 = state.getCursor() - super.getCategoryLabelPositionOffset();
					x0 = x1 - state.getMax();
				}
				else if (edge == RectangleEdge.RIGHT) {
					y0 = getCategoryStart(
						categoryIndex, ticks.size(), dataArea, edge
					);
					y1 = getCategoryEnd(
						categoryIndex, ticks.size(), dataArea, edge
					);
					x0 = state.getCursor() + super.getCategoryLabelPositionOffset();
					x1 = x0 - state.getMax();
				}
				Rectangle2D area = new Rectangle2D.Double(
					x0, y0, (x1 - x0), (y1 - y0)
				);
				Point2D anchorPoint = RectangleAnchor.coordinates(
					area, position.getCategoryAnchor()
				);
				TextBlock block = tick.getLabel();
				block.draw(
					g2, 
					(float) anchorPoint.getX(), (float) anchorPoint.getY(), 
					position.getLabelAnchor(), 
					(float) anchorPoint.getX(), (float) anchorPoint.getY(), 
					position.getAngle()
				);
				Shape bounds = block.calculateBounds(
					g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), 
					position.getLabelAnchor(), 
					(float) anchorPoint.getX(), (float) anchorPoint.getY(), 
					position.getAngle()
				);
                
				//CTI We don't have access to the cateogryLabelTooTips map, therefore we're ignoring this step for now.
/*                if (plotState != null) {
					EntityCollection entities 
						= plotState.getOwner().getEntityCollection();
					if (entities != null) {
						String tooltip 
							= (String) this.categoryLabelToolTips.get(
								tick.getCategory()
							);
						entities.add(
							new TickLabelEntity(bounds, tooltip, null)
						);
					}
				}
				*/
				}
				categoryIndex++;
			}

			if (edge.equals(RectangleEdge.TOP)) {
				double h = state.getMax();
				state.cursorUp(h);
			}
			else if (edge.equals(RectangleEdge.BOTTOM)) {
				double h = state.getMax();
				state.cursorDown(h);
			}
			else if (edge == RectangleEdge.LEFT) {
				double w = state.getMax();
				state.cursorLeft(w);
			}
			else if (edge == RectangleEdge.RIGHT) {
				double w = state.getMax();
				state.cursorRight(w);
			}
		}
		return state;
	}
}
/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.jfreechart.chart;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.CategoryTick;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.text.TextBlock;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Size2D;
import org.jfree.util.ShapeUtilities;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HorizontalSkipLabelsCategoryAxis extends CategoryAxis
{
	/**
	 * This renderer (based on JFreeChart 0.9.16) only draws every other category label. 
	 * I use it to create category labels like this:
	 * 1 3 5 7 9 11 13 15 17 ..
	 */
	public HorizontalSkipLabelsCategoryAxis(String label)
	{
		super(label);
	}
	
	/**
	 * Overrides to draw to draw every other item
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
			logger.debug("Entering drawCategoryLabels() method, cursor = " + state.getCursor());
		}

		int i = 0; //?
		if (state == null)
		{
			throw new IllegalArgumentException("null state not permitted.");
		}
		if (isTickLabelsVisible())
		{
			g2.setFont(getTickLabelFont());
			g2.setPaint(getTickLabelPaint());
			List ticks = refreshTicks(g2, state, plotArea, dataArea, edge);
			state.setTicks(ticks);
			int categoryIndex = 0;
			Iterator iterator = ticks.iterator();
			while (iterator.hasNext())
			{
				CategoryTick tick = (CategoryTick) iterator.next();
				if (i++%12==0 || !iterator.hasNext())
				{
					g2.setPaint(getTickLabelPaint());
					CategoryLabelPosition position = getCategoryLabelPositions().getLabelPosition(edge);
					double x0 = 0.0;
					double x1 = 0.0;
					double y0 = 0.0;
					double y1 = 0.0;
					if (edge == RectangleEdge.TOP)
					{
						x0 = getCategoryStart( categoryIndex, ticks.size(), dataArea, edge);
						x1 = getCategoryEnd( categoryIndex, ticks.size(), dataArea, edge);
						y1 = state.getCursor() - getCategoryLabelPositionOffset();
						y0 = y1 - state.getMax();
					}
					else if (edge == RectangleEdge.BOTTOM)
					{
						x0 = getCategoryStart( categoryIndex, ticks.size(), dataArea, edge);
						x1 = getCategoryEnd( categoryIndex, ticks.size(), dataArea, edge);
						y0 = state.getCursor() + getCategoryLabelPositionOffset();
						y1 = y0 + state.getMax();
					}
					else if (edge == RectangleEdge.LEFT)
					{
						y0 = getCategoryStart( categoryIndex, ticks.size(), dataArea, edge);
						y1 = getCategoryEnd( categoryIndex, ticks.size(), dataArea, edge);
						x1 = state.getCursor() - getCategoryLabelPositionOffset();
						x0 = x1 - state.getMax();
					}
					else if (edge == RectangleEdge.RIGHT)
					{
						y0 = getCategoryStart( categoryIndex, ticks.size(), dataArea, edge);
						y1 = getCategoryEnd( categoryIndex, ticks.size(), dataArea, edge);
						x0 = state.getCursor() + getCategoryLabelPositionOffset();
						x1 = x0 - state.getMax();
					}
					Rectangle2D area = new Rectangle2D.Double(x0, y0, (x1 - x0), (y1 - y0));
					double[] anchorPoint = RectangleAnchor.coordinates( area, position.getCategoryAnchor());
					TextBlock block = tick.getLabel();
					//the following is the only change from 0.9.16 source code
//					if (i++%13==0)
						block.draw(g2, (float) anchorPoint[0], (float) anchorPoint[1], position.getLabelAnchor(),
								(float) anchorPoint[0], (float) anchorPoint[1], position.getAngle());
								
					Shape bounds = block.calculateBounds( g2, (float) anchorPoint[0], (float) anchorPoint[1], position.getLabelAnchor(), 
									  (float) anchorPoint[0], (float) anchorPoint[1], position.getAngle() );
									  
					//TODO Implement this code for future, it's private in the super.
//					if (plotState != null)
//					{
//						EntityCollection entities = plotState.getOwner().getEntityCollection();
//						if (entities != null)
//						{
//							String tooltip = (String) super.getcategoryLabelToolTips.get( tick.getCategory());
//							entities.addEntity(new TickLabelEntity(bounds, tooltip, null));
//						}
//					}
	
				}							
				categoryIndex++;
			}
			if (edge.equals(RectangleEdge.TOP))
			{
				double h = state.getMax();
				state.cursorUp(h);
			}
			else if (edge.equals(RectangleEdge.BOTTOM))
			{
				double h = state.getMax();
				state.cursorDown(h);
			}
			else if (edge == RectangleEdge.LEFT)
			{
				double w = state.getMax();
				state.cursorLeft(w);
			}
			else if (edge == RectangleEdge.RIGHT)
			{
				double w = state.getMax();
				state.cursorRight(w);
			}
		} return state;
	}
	/**
	   * Estimates the space required for the axis, given a specific drawing area.
	   *
	   * @param g2  the graphics device (used to obtain font information).
	   * @param plot  the plot that the axis belongs to.
	   * @param plotArea  the area within which the axis should be drawn.
	   * @param edge  the axis location (top or bottom).
	   * @param space  the space already reserved.
	   *
	   * @return The space required to draw the axis.
	   */
	  public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, 
									RectangleEdge edge, AxisSpace space) {

		  // create a new space object if one wasn't supplied...
		  if (space == null) {
			  space = new AxisSpace();
		  }
        
		  // if the axis is not visible, no additional space is required...
		  if (!isVisible()) {
			  return space;
		  }

		  // calculate the max size of the tick labels (if visible)...
		  double tickLabelHeight = 0.0;
		  double tickLabelWidth = 0.0;
		  if (isTickLabelsVisible()) {
			  g2.setFont(getTickLabelFont());
			  AxisState state = new AxisState();
			  refreshTicks(g2, state, plotArea, plotArea, edge);
			  if (edge == RectangleEdge.TOP) {
				  tickLabelHeight = state.getMax();
			  }
			  else if (edge == RectangleEdge.BOTTOM) {
				  tickLabelHeight = state.getMax();
			  }
			  else if (edge == RectangleEdge.LEFT) {
				  tickLabelWidth = state.getMax(); 
			  }
			  else if (edge == RectangleEdge.RIGHT) {
				  tickLabelWidth = state.getMax(); 
			  }
		  }
        
		  // get the axis label size and update the space object...
		  Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
		  double labelHeight = 0.0;
		  double labelWidth = 0.0;
		  if (RectangleEdge.isTopOrBottom(edge)) {
			  labelHeight = labelEnclosure.getHeight();
			  space.add(labelHeight + tickLabelHeight + getCategoryLabelPositionOffset(), edge);
		  }
		  else if (RectangleEdge.isLeftOrRight(edge)) {
			  labelWidth = labelEnclosure.getWidth();
			  space.add(labelWidth + tickLabelWidth + getCategoryLabelPositionOffset(), edge);
		  }

		  return space;

	  }
	/**
	 * A utility method for determining the height of a text block.
	 *
	 * @param block  the text block.
	 * @param position  the label position.
	 * @param g2  the graphics device.
	 *
	 * @return the height.
	 */
	protected double calculateTextBlockHeight(TextBlock block, 
											  CategoryLabelPosition position, 
											  Graphics2D g2) {
                                                    
		Insets insets = getTickLabelInsets();
		Size2D size = block.calculateDimensions(g2);
		Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, size.getWidth(), size.getHeight());
		Shape rotatedBox = ShapeUtilities.rotateShape(box, position.getAngle(), 0.0f, 0.0f);
		double h = rotatedBox.getBounds2D().getHeight() + insets.top + insets.bottom;
		return h;
        
	}
	/**
	  * Creates a temporary list of ticks that can be used when drawing the axis.
	  *
	  * @param g2  the graphics device (used to get font measurements).
	  * @param state  the axis state.
	  * @param plotArea  the area where the plot and axes will be drawn.
	  * @param dataArea  the area inside the axes.
	  * @param edge  the location of the axis.
	  * 
	  * @return A list of ticks.
	  */
	 public List refreshTicks(Graphics2D g2, 
							  AxisState state,
							  Rectangle2D plotArea, 
							  Rectangle2D dataArea,
							  RectangleEdge edge) {

		 List ticks = new java.util.ArrayList();
        
		 // sanity check for data area...
		 if (dataArea.getHeight() <= 0.0 || dataArea.getWidth() < 0.0) {
			 return ticks;
		 }

		 CategoryPlot plot = (CategoryPlot) getPlot();
		 List categories = plot.getCategories();
		 double max = 0.0;
                
		 if (categories != null) {
			 CategoryLabelPosition position = getCategoryLabelPositions().getLabelPosition(edge);
			 float r = getMaxCategoryLabelWidthRatio();
			 if (r <= 0.0) {
				 r = position.getWidthRatio();   
			 }
            
            
			 float l = 0.0f;
			 if (position.getWidthType() == CategoryLabelWidthType.CATEGORY) {
				 l = (float) calculateCategorySize(categories.size(), dataArea, edge);  
			 }
			 else {
				 if (RectangleEdge.isLeftOrRight(edge)) {
					 l = (float) dataArea.getWidth();   
				 }
				 else {
					 l = (float) dataArea.getHeight();   
				 }
			 }
			 int categoryIndex = 0;
			 Iterator iterator = categories.iterator();
			 while (iterator.hasNext()) {
				 Comparable category = (Comparable) iterator.next();
				 TextBlock label = createLabel(category, l * r, edge, g2);
				 if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
					 max = Math.max(max, calculateTextBlockHeight(label, position, g2));
				 }
				 else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
					 max = Math.max(max, calculateTextBlockWidth(label, position, g2));
				 }
				 Tick tick = new CategoryTick(
					 category, label, 
					 position.getLabelAnchor(), position.getRotationAnchor(), position.getAngle()
				 );
				 ticks.add(tick);
				 categoryIndex = categoryIndex + 1;
			 }
		 }
		 state.setMax(max);
		 return ticks;
        
	 }
	
}

package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (8/24/00 3:40:29 PM)
 * @author: 
 */
import javax.swing.JSplitPane;

public class SplitPaneDividerListener extends java.awt.event.ComponentAdapter implements java.beans.PropertyChangeListener, java.awt.event.MouseMotionListener
{
	private JSplitPane sPane = null;
	private boolean ignore = false;
	private double percent = -1.0;

	private boolean collapsedTop = false;
	private boolean collapsedBottom = false;
	
	private int posFromTheTop = 0;
/**
 * SplitPaneDividerListener constructor comment.
 */
public SplitPaneDividerListener( JSplitPane splitPane )
{	
	super();
	sPane = splitPane;
}
/**
 * componentResize method comment.
 */
public void componentResized(java.awt.event.ComponentEvent e ) 
{
	if( collapsedBottom == true )
		sPane.setDividerLocation( 1.0 );
	else if( collapsedTop == true )
		sPane.setDividerLocation( 0.0 );	
	else if( percent > 0 ) 
	{
		ignore = true;
		sPane.setDividerLocation( posFromTheTop );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 4:33:00 PM)
 * @param e java.awt.event.MouseEvent
 */
public void mouseDragged(java.awt.event.MouseEvent e) 
{
	if( e.isConsumed() )
	{
		//only drag events that have been consumed by another listener
		// are allowed here, listeners such as the divider drag event!!
		sPane.setDividerLocation( e.getY() );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 4:33:00 PM)
 * @param e java.awt.event.MouseEvent
 */
public void mouseMoved(java.awt.event.MouseEvent e) {}
/**
 * propertyChange method comment.
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) 
{
	if( evt.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY) )
	{
		if( ignore )
			ignore = false;
		else
		{
			int leftValue = 0, rightValue = 0, paneValue = 0;
			
			if( sPane.getOrientation() == JSplitPane.VERTICAL_SPLIT )
			{
				leftValue = sPane.getLeftComponent().getHeight();
				rightValue = sPane.getRightComponent().getHeight();
				paneValue = sPane.getHeight();
			}
			else
			{
				leftValue = sPane.getLeftComponent().getWidth();
				rightValue = sPane.getRightComponent().getWidth();
				paneValue = sPane.getWidth();
			}
				
			if( leftValue == 0 && rightValue == 0 )
			{
				collapsedBottom = true;
				posFromTheTop = sPane.getMaximumDividerLocation(); // bumping power goes to the right/top component
			}
			else if( leftValue == 0 )
			{
				collapsedTop = true;
				posFromTheTop = sPane.getMinimumDividerLocation();
			}
			else if( rightValue == 0 )
			{
				collapsedBottom = true;
				posFromTheTop = sPane.getMaximumDividerLocation();
			}
			else
			{
				collapsedBottom = false;
				collapsedTop = false;
				posFromTheTop = sPane.getDividerLocation();
			}

/*			// Code chunk for setting the divider location to its releative last position	
			if( leftValue == 0 && rightValue == 0 )
				percent = 1.0; // bumping power goes to the right/top component
			else if( leftValue == 0 )
				percent = 0.0;
			else if( rightValue == 0 )
				percent = 1.0;
			else
				percent = ((double)sPane.getDividerLocation()) / paneValue;
*/
		}

	}
		
}
}

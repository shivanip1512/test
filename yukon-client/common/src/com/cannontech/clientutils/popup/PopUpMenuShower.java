package com.cannontech.clientutils.popup;

/**
 * Insert the type's description here.
 * Creation date: (4/4/00 2:43:57 PM)
 * @author: 
 * @Version: <version>
 */
import javax.swing.JPopupMenu;

public class PopUpMenuShower extends java.awt.event.MouseAdapter 
{
	private JPopupMenu popup = null;
/**
 * PopUpMenuShower constructor comment.
 */
public PopUpMenuShower() {
	super();
}
/**
 * PopUpMenuShower constructor comment.
 */
public PopUpMenuShower( javax.swing.JPopupMenu popupMenu ) 
{
	super();

	this.popup = popupMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 2:47:23 PM)
 * Version: <version>
 * @param e java.awt.event.MouseEvent
 */
public void mousePressed(java.awt.event.MouseEvent e) 
{
	//showIfPopupTrigger( e );
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 2:47:23 PM)
 * Version: <version>
 * @param e java.awt.event.MouseEvent
 */
public void mouseReleased(java.awt.event.MouseEvent e) 
{
	showIfPopupTrigger( e );
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 2:46:05 PM)
 * Version: <version>
 * @param e java.awt.event.MouseEvent
 */
protected void showIfPopupTrigger(java.awt.event.MouseEvent e) 
{
	if( e.isPopupTrigger() )
	{		
		popup.show( e.getComponent(), e.getX(), e.getY() );
	}
}
}

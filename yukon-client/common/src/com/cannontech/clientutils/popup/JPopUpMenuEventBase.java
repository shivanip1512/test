package com.cannontech.clientutils.popup;

/**
 * Insert the type's description here.
 * Creation date: (2/22/2001 2:05:17 PM)
 * @author: 
 */
// Use this class to register for and receive PopUpEvent's
public abstract class JPopUpMenuEventBase extends javax.swing.JPopupMenu 
{
	private boolean firingPopUpEvent = false;
	private String popUpCommand = "menuItemClickedCommand";
/**
 * JPopUpMenuEventBase constructor comment.
 */
public JPopUpMenuEventBase() {
	super();
}
/**
 * JPopUpMenuEventBase constructor comment.
 * @param label java.lang.String
 */
public JPopUpMenuEventBase(String label) {
	super(label);
}
/** 
 * Adds an ActionListener. The listener will receive an action event
 * the user finishes making a selection.
 *
 * @param l  the com.cannontech.clientutils.popup.PopUpEventListener that is to be notified
 */
public void addPopUpEventListener(com.cannontech.clientutils.popup.PopUpEventListener l)
{
   listenerList.add(com.cannontech.clientutils.popup.PopUpEventListener.class, l);
}
/**
 * Notify all listeners that have registered interest for
 * notification on this event type.
 *  
 * @see EventListenerList
 */
protected void firePopUpEvent( com.cannontech.clientutils.commonutils.GenericEvent e )
{
   if (!firingPopUpEvent)
   {
	  firingPopUpEvent = true;
	  
	  // Guaranteed to return a non-null array
	  Object[] listeners = listenerList.getListenerList();
	  // Process the listeners last to first, notifying
	  // those that are interested in this event
	  for (int i = listeners.length - 2; i >= 0; i -= 2)
	  {
		 if (listeners[i] == PopUpEventListener.class)
		 {
			if (e == null)
			   e = new com.cannontech.clientutils.commonutils.GenericEvent(this, 
				   			getPopUpCommand(), 
				   			com.cannontech.clientutils.commonutils.GenericEvent.DEFAULT_GENERIC_EVENT );
			   
			((PopUpEventListener) listeners[i + 1]).handlePopUpEvent(e);
		 }
	  }
	  
	  firingPopUpEvent = false; 
	}
   
}
/** 
 * Returns the action commnand that is included in the event sent to
 *  action listeners.
 *
 * @return  the string containing the "command" that is sent
 *          to popupevent listeners.
 */
public String getPopUpCommand()
{
   return popUpCommand;
}
}
